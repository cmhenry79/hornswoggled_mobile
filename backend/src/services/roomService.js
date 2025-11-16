import { collections } from '../config/firebase.js';
import { AppError } from '../middleware/errorHandler.js';
import { logger } from '../utils/logger.js';
import { nanoid } from 'nanoid';
import { GameService } from './gameService.js';

const gameService = new GameService();

export class RoomService {
  generateRoomCode() {
    // Generate 6-character alphanumeric code
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'; // Remove ambiguous chars
    let code = '';
    for (let i = 0; i < 6; i++) {
      code += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return code;
  }

  async createRoom(hostId, settings) {
    try {
      const roomCode = this.generateRoomCode();
      const roomRef = collections().rooms().doc();

      const room = {
        roomId: roomRef.id,
        code: roomCode,
        hostId,
        visibility: settings.visibility || 'public',
        status: 'waiting', // waiting, playing, finished
        settings: {
          maxPlayers: Math.min(settings.maxPlayers || 8, 12),
          maxRounds: Math.min(settings.maxRounds || 5, 10),
          roundDuration: settings.roundDuration || 90,
          wordPackIds: settings.wordPackIds || ['default'],
          mutators: settings.mutators || [],
          enableBots: settings.enableBots !== false
        },
        participants: [
          {
            userId: hostId,
            role: 'host',
            isBot: false,
            joinedAt: new Date()
          }
        ],
        bots: [],
        currentGameId: null,
        createdAt: new Date(),
        updatedAt: new Date()
      };

      await roomRef.set(room);
      logger.info(`Room created: ${roomRef.id} with code ${roomCode}`);

      return room;
    } catch (error) {
      logger.error('Error in createRoom:', error);
      throw new AppError('Failed to create room', 500);
    }
  }

  async joinRoom(code, userId) {
    try {
      // Find room by code
      const snapshot = await collections().rooms()
        .where('code', '==', code.toUpperCase())
        .where('status', '==', 'waiting')
        .limit(1)
        .get();

      if (snapshot.empty) {
        throw new AppError('Room not found or game already started', 404);
      }

      const roomDoc = snapshot.docs[0];
      const room = roomDoc.data();

      // Check if user already in room
      const alreadyJoined = room.participants.some(p => p.userId === userId);
      if (alreadyJoined) {
        return room;
      }

      // Check if room is full
      const totalPlayers = room.participants.length + room.bots.length;
      if (totalPlayers >= room.settings.maxPlayers) {
        throw new AppError('Room is full', 400);
      }

      // Add participant
      await roomDoc.ref.update({
        participants: [
          ...room.participants,
          {
            userId,
            role: 'player',
            isBot: false,
            joinedAt: new Date()
          }
        ],
        updatedAt: new Date()
      });

      const updatedDoc = await roomDoc.ref.get();
      return updatedDoc.data();
    } catch (error) {
      logger.error('Error in joinRoom:', error);
      throw error;
    }
  }

  async leaveRoom(roomId, userId) {
    try {
      const roomRef = collections().rooms().doc(roomId);
      const roomDoc = await roomRef.get();

      if (!roomDoc.exists) {
        throw new AppError('Room not found', 404);
      }

      const room = roomDoc.data();
      const participants = room.participants.filter(p => p.userId !== userId);

      // If host leaves and there are other players, transfer host
      if (room.hostId === userId && participants.length > 0) {
        const newHost = participants[0];
        newHost.role = 'host';

        await roomRef.update({
          hostId: newHost.userId,
          participants,
          updatedAt: new Date()
        });
      } else if (participants.length === 0) {
        // Delete room if empty
        await roomRef.delete();
      } else {
        await roomRef.update({
          participants,
          updatedAt: new Date()
        });
      }

      logger.info(`User ${userId} left room ${roomId}`);
    } catch (error) {
      logger.error('Error in leaveRoom:', error);
      throw error;
    }
  }

  async getRoomById(roomId) {
    try {
      const roomDoc = await collections().rooms().doc(roomId).get();

      if (!roomDoc.exists) {
        throw new AppError('Room not found', 404);
      }

      return roomDoc.data();
    } catch (error) {
      logger.error('Error in getRoomById:', error);
      throw error;
    }
  }

  async updateRoom(roomId, hostId, updates) {
    try {
      const roomRef = collections().rooms().doc(roomId);
      const roomDoc = await roomRef.get();

      if (!roomDoc.exists) {
        throw new AppError('Room not found', 404);
      }

      const room = roomDoc.data();

      // Verify user is host
      if (room.hostId !== hostId) {
        throw new AppError('Only host can update room settings', 403);
      }

      // Only allow updating settings
      const allowedUpdates = {
        ...(updates.settings && { settings: { ...room.settings, ...updates.settings } }),
        updatedAt: new Date()
      };

      await roomRef.update(allowedUpdates);

      const updatedDoc = await roomRef.get();
      return updatedDoc.data();
    } catch (error) {
      logger.error('Error in updateRoom:', error);
      throw error;
    }
  }

  async addBot(roomId, hostId, botId, difficulty) {
    try {
      const roomRef = collections().rooms().doc(roomId);
      const roomDoc = await roomRef.get();

      if (!roomDoc.exists) {
        throw new AppError('Room not found', 404);
      }

      const room = roomDoc.data();

      // Verify user is host
      if (room.hostId !== hostId) {
        throw new AppError('Only host can add bots', 403);
      }

      // Check room capacity
      const totalPlayers = room.participants.length + room.bots.length;
      if (totalPlayers >= room.settings.maxPlayers) {
        throw new AppError('Room is full', 400);
      }

      // Check bot limit
      if (room.bots.length >= 6) {
        throw new AppError('Maximum bots per room reached', 400);
      }

      // Get bot details
      const botDoc = await collections().bots().doc(botId).get();
      if (!botDoc.exists) {
        throw new AppError('Bot not found', 404);
      }

      const bot = botDoc.data();

      const botParticipant = {
        botId,
        userId: `bot_${nanoid(10)}`,
        name: bot.name,
        persona: bot.persona,
        difficulty: difficulty || 'medium',
        avatarUrl: bot.avatarUrl,
        addedAt: new Date()
      };

      await roomRef.update({
        bots: [...room.bots, botParticipant],
        participants: [
          ...room.participants,
          {
            userId: botParticipant.userId,
            role: 'player',
            isBot: true,
            joinedAt: new Date()
          }
        ],
        updatedAt: new Date()
      });

      const updatedDoc = await roomRef.get();
      return updatedDoc.data();
    } catch (error) {
      logger.error('Error in addBot:', error);
      throw error;
    }
  }

  async removeBot(roomId, hostId, botUserId) {
    try {
      const roomRef = collections().rooms().doc(roomId);
      const roomDoc = await roomRef.get();

      if (!roomDoc.exists) {
        throw new AppError('Room not found', 404);
      }

      const room = roomDoc.data();

      if (room.hostId !== hostId) {
        throw new AppError('Only host can remove bots', 403);
      }

      await roomRef.update({
        bots: room.bots.filter(b => b.userId !== botUserId),
        participants: room.participants.filter(p => p.userId !== botUserId),
        updatedAt: new Date()
      });
    } catch (error) {
      logger.error('Error in removeBot:', error);
      throw error;
    }
  }

  async getPublicRooms(limit = 20, offset = 0) {
    try {
      const snapshot = await collections().rooms()
        .where('visibility', '==', 'public')
        .where('status', '==', 'waiting')
        .orderBy('createdAt', 'desc')
        .limit(limit)
        .offset(offset)
        .get();

      return snapshot.docs.map(doc => ({
        ...doc.data(),
        // Hide some details for public listing
        participants: undefined,
        bots: undefined,
        playerCount: doc.data().participants.length
      }));
    } catch (error) {
      logger.error('Error in getPublicRooms:', error);
      throw new AppError('Failed to fetch public rooms', 500);
    }
  }

  async startGame(roomId, hostId) {
    try {
      const roomRef = collections().rooms().doc(roomId);
      const roomDoc = await roomRef.get();

      if (!roomDoc.exists) {
        throw new AppError('Room not found', 404);
      }

      const room = roomDoc.data();

      if (room.hostId !== hostId) {
        throw new AppError('Only host can start the game', 403);
      }

      if (room.participants.length < 2) {
        throw new AppError('Need at least 2 players to start', 400);
      }

      // Create game
      const game = await gameService.createGame(roomId, room);

      await roomRef.update({
        status: 'playing',
        currentGameId: game.gameId,
        updatedAt: new Date()
      });

      return game;
    } catch (error) {
      logger.error('Error in startGame:', error);
      throw error;
    }
  }
}
