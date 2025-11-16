import { collections, getStorage } from '../config/firebase.js';
import { AppError } from '../middleware/errorHandler.js';
import { logger } from '../utils/logger.js';
import { WordPackService } from './wordPackService.js';
import { BotService } from './botService.js';
import { UserService } from './userService.js';
import { nanoid } from 'nanoid';

const wordPackService = new WordPackService();
const botService = new BotService();
const userService = new UserService();

export class GameService {
  async createGame(roomId, roomData) {
    try {
      const gameRef = collections().games().doc();

      const game = {
        gameId: gameRef.id,
        roomId,
        status: 'active',
        currentRound: 0,
        maxRounds: roomData.settings.maxRounds,
        roundDuration: roomData.settings.roundDuration,
        wordPackIds: roomData.settings.wordPackIds,
        mutators: roomData.settings.mutators || [],
        players: roomData.participants.map(p => ({
          userId: p.userId,
          isBot: p.isBot,
          score: 0,
          wins: 0,
          powerups: {}
        })),
        bots: roomData.bots,
        rounds: [],
        createdAt: new Date(),
        updatedAt: new Date()
      };

      await gameRef.set(game);
      logger.info(`Game created: ${gameRef.id}`);

      return game;
    } catch (error) {
      logger.error('Error in createGame:', error);
      throw new AppError('Failed to create game', 500);
    }
  }

  async getGameById(gameId) {
    try {
      const gameDoc = await collections().games().doc(gameId).get();

      if (!gameDoc.exists) {
        throw new AppError('Game not found', 404);
      }

      return gameDoc.data();
    } catch (error) {
      logger.error('Error in getGameById:', error);
      throw error;
    }
  }

  async startRound(gameId, userId) {
    try {
      const gameRef = collections().games().doc(gameId);
      const gameDoc = await gameRef.get();

      if (!gameDoc.exists) {
        throw new AppError('Game not found', 404);
      }

      const game = gameDoc.data();

      if (game.currentRound >= game.maxRounds) {
        throw new AppError('Game has ended', 400);
      }

      const roundNumber = game.currentRound + 1;

      // Select random judge (rotate through players)
      const players = game.players.filter(p => !p.isBot);
      const judgeIndex = (roundNumber - 1) % players.length;
      const judge = players[judgeIndex];

      // Get random word from word packs
      const word = await this.getRandomWordFromPacks(game.wordPackIds);

      // Create round
      const roundRef = collections().rounds().doc();
      const deadline = new Date(Date.now() + game.roundDuration * 1000);

      const round = {
        roundId: roundRef.id,
        gameId,
        roundNumber,
        word: word.word,
        wordCategory: word.category,
        judgeUserId: judge.userId,
        status: 'submitting', // submitting, judging, completed
        submissions: [],
        deadline,
        createdAt: new Date()
      };

      await roundRef.set(round);

      // Update game
      await gameRef.update({
        currentRound: roundNumber,
        rounds: [...game.rounds, roundRef.id],
        updatedAt: new Date()
      });

      // Trigger bot submissions
      this.triggerBotSubmissions(gameId, round);

      return round;
    } catch (error) {
      logger.error('Error in startRound:', error);
      throw error;
    }
  }

  async getRandomWordFromPacks(packIds) {
    try {
      const randomPackId = packIds[Math.floor(Math.random() * packIds.length)];
      return await wordPackService.getRandomWord(randomPackId);
    } catch (error) {
      logger.error('Error getting random word:', error);
      // Fallback
      return {
        word: 'Happiness',
        category: 'default'
      };
    }
  }

  async triggerBotSubmissions(gameId, round) {
    try {
      const game = await this.getGameById(gameId);
      const botPlayers = game.players.filter(p => p.isBot);

      // Schedule bot submissions with random delays
      for (const botPlayer of botPlayers) {
        const botData = game.bots.find(b => b.userId === botPlayer.userId);
        if (!botData) continue;

        // Random delay between 5-30 seconds
        const delay = Math.random() * 25000 + 5000;

        setTimeout(async () => {
          try {
            const submission = await botService.generateSubmission(
              round.word,
              botData.persona,
              botData.difficulty,
              round.roundId
            );

            await this.submitAnswer(
              gameId,
              round.roundId,
              botPlayer.userId,
              submission
            );
          } catch (error) {
            logger.error('Error in bot submission:', error);
          }
        }, delay);
      }
    } catch (error) {
      logger.error('Error triggering bot submissions:', error);
    }
  }

  async submitAnswer(gameId, roundId, userId, submissionData) {
    try {
      const roundRef = collections().rounds().doc(roundId);
      const roundDoc = await roundRef.get();

      if (!roundDoc.exists) {
        throw new AppError('Round not found', 404);
      }

      const round = roundDoc.data();

      // Check if round is still accepting submissions
      if (round.status !== 'submitting') {
        throw new AppError('Round is not accepting submissions', 400);
      }

      // Check if user is the judge
      if (round.judgeUserId === userId) {
        throw new AppError('Judge cannot submit', 400);
      }

      // Check if user already submitted
      const alreadySubmitted = round.submissions.some(s => s.userId === userId);
      if (alreadySubmitted) {
        throw new AppError('Already submitted for this round', 400);
      }

      const submission = {
        submissionId: nanoid(),
        userId,
        type: submissionData.type || 'text',
        content: submissionData.content,
        mediaUrl: submissionData.mediaUrl || null,
        votes: 0,
        submittedAt: new Date()
      };

      await roundRef.update({
        submissions: [...round.submissions, submission]
      });

      // Check if all players submitted
      const game = await this.getGameById(gameId);
      const nonJudgePlayers = game.players.filter(p => p.userId !== round.judgeUserId);

      if (round.submissions.length + 1 >= nonJudgePlayers.length) {
        // All submitted, move to judging
        await roundRef.update({
          status: 'judging'
        });
      }

      return submission;
    } catch (error) {
      logger.error('Error in submitAnswer:', error);
      throw error;
    }
  }

  async submitImage(gameId, roundId, userId, file, type = 'image') {
    try {
      // Upload to Firebase Storage
      const bucket = getStorage().bucket();
      const fileName = `submissions/${gameId}/${roundId}/${userId}_${Date.now()}.${file.mimetype.split('/')[1]}`;
      const fileUpload = bucket.file(fileName);

      await fileUpload.save(file.buffer, {
        metadata: {
          contentType: file.mimetype
        }
      });

      // Make file publicly accessible
      await fileUpload.makePublic();

      const publicUrl = `https://storage.googleapis.com/${bucket.name}/${fileName}`;

      // Submit with media URL
      return await this.submitAnswer(gameId, roundId, userId, {
        type,
        content: type === 'doodle' ? 'Drawing' : 'Image',
        mediaUrl: publicUrl
      });
    } catch (error) {
      logger.error('Error in submitImage:', error);
      throw new AppError('Failed to upload image', 500);
    }
  }

  async voteForSubmission(gameId, roundId, userId, submissionId) {
    try {
      const roundRef = collections().rounds().doc(roundId);
      const roundDoc = await roundRef.get();

      if (!roundDoc.exists) {
        throw new AppError('Round not found', 404);
      }

      const round = roundDoc.data();

      // Verify user is the judge
      if (round.judgeUserId !== userId) {
        throw new AppError('Only judge can select winner', 403);
      }

      if (round.status !== 'judging') {
        throw new AppError('Round is not in judging phase', 400);
      }

      // Find winning submission
      const winningSubmission = round.submissions.find(s => s.submissionId === submissionId);
      if (!winningSubmission) {
        throw new AppError('Submission not found', 404);
      }

      // Update round
      await roundRef.update({
        status: 'completed',
        winnerId: winningSubmission.userId,
        winningSubmissionId: submissionId,
        completedAt: new Date()
      });

      // Update player scores
      const gameRef = collections().games().doc(gameId);
      const gameDoc = await gameRef.get();
      const game = gameDoc.data();

      const updatedPlayers = game.players.map(p => {
        if (p.userId === winningSubmission.userId) {
          return {
            ...p,
            score: p.score + 100,
            wins: p.wins + 1
          };
        }
        // Participation points
        if (round.submissions.some(s => s.userId === p.userId)) {
          return {
            ...p,
            score: p.score + 10
          };
        }
        return p;
      });

      await gameRef.update({
        players: updatedPlayers,
        updatedAt: new Date()
      });

      // Award XP to winner
      if (!winningSubmission.userId.startsWith('bot_')) {
        await userService.addXP(winningSubmission.userId, 100);
      }

      return {
        winnerId: winningSubmission.userId,
        round
      };
    } catch (error) {
      logger.error('Error in voteForSubmission:', error);
      throw error;
    }
  }

  async usePowerup(gameId, userId, powerupType, targetUserId = null) {
    try {
      // Powerup logic implementation
      logger.info(`Powerup ${powerupType} used by ${userId}`);
      return { success: true, powerupType };
    } catch (error) {
      logger.error('Error in usePowerup:', error);
      throw error;
    }
  }

  async getRoundResults(gameId, roundId) {
    try {
      const roundDoc = await collections().rounds().doc(roundId).get();

      if (!roundDoc.exists) {
        throw new AppError('Round not found', 404);
      }

      return roundDoc.data();
    } catch (error) {
      logger.error('Error in getRoundResults:', error);
      throw error;
    }
  }

  async getFinalResults(gameId) {
    try {
      const game = await this.getGameById(gameId);

      // Sort players by score
      const rankings = [...game.players].sort((a, b) => b.score - a.score);

      return {
        gameId,
        rankings,
        totalRounds: game.currentRound,
        winner: rankings[0]
      };
    } catch (error) {
      logger.error('Error in getFinalResults:', error);
      throw error;
    }
  }

  async endGame(gameId, userId) {
    try {
      const gameRef = collections().games().doc(gameId);
      const gameDoc = await gameRef.get();

      if (!gameDoc.exists) {
        throw new AppError('Game not found', 404);
      }

      const game = gameDoc.data();

      // Get room to verify host
      const roomDoc = await collections().rooms().doc(game.roomId).get();
      if (roomDoc.exists && roomDoc.data().hostId !== userId) {
        throw new AppError('Only host can end game early', 403);
      }

      await gameRef.update({
        status: 'completed',
        endedAt: new Date()
      });

      // Update room status
      if (roomDoc.exists) {
        await collections().rooms().doc(game.roomId).update({
          status: 'finished',
          updatedAt: new Date()
        });
      }

      // Update user stats
      for (const player of game.players) {
        if (!player.userId.startsWith('bot_')) {
          await this.updatePlayerStats(player.userId, game, player);
        }
      }

      logger.info(`Game ${gameId} ended`);
    } catch (error) {
      logger.error('Error in endGame:', error);
      throw error;
    }
  }

  async updatePlayerStats(userId, game, playerData) {
    try {
      const userRef = collections().users().doc(userId);
      const userDoc = await userRef.get();

      if (!userDoc.exists) return;

      const user = userDoc.data();
      const stats = user.stats || {};

      const winner = game.players.reduce((prev, current) =>
        current.score > prev.score ? current : prev
      );

      await userRef.update({
        'stats.gamesPlayed': (stats.gamesPlayed || 0) + 1,
        'stats.gamesWon': (stats.gamesWon || 0) + (winner.userId === userId ? 1 : 0),
        'stats.roundsWon': (stats.roundsWon || 0) + playerData.wins,
        'stats.totalScore': (stats.totalScore || 0) + playerData.score
      });
    } catch (error) {
      logger.error('Error updating player stats:', error);
    }
  }
}
