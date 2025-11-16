import { collections } from '../config/firebase.js';
import { AppError } from '../middleware/errorHandler.js';
import { logger } from '../utils/logger.js';

export class UserService {
  async createOrUpdateUser(userId, userData) {
    try {
      const userRef = collections().users().doc(userId);
      const userDoc = await userRef.get();

      const timestamp = new Date();

      if (!userDoc.exists) {
        // Create new user
        const newUser = {
          userId,
          email: userData.email,
          displayName: userData.displayName,
          avatarUrl: userData.avatarUrl || `https://api.dicebear.com/7.x/avataaars/svg?seed=${userId}`,
          level: 1,
          xp: 0,
          coins: 100, // Starting coins
          stats: {
            gamesPlayed: 0,
            gamesWon: 0,
            roundsWon: 0,
            submissionsCreated: 0,
            votesReceived: 0
          },
          cosmetics: {
            equipped: {
              avatar: null,
              background: null,
              frame: null,
              badge: null
            },
            owned: []
          },
          unlockedWordPacks: ['default', 'starter'],
          powerups: {
            extraTime: 0,
            peek: 0,
            swap: 0,
            boost: 0,
            shield: 0
          },
          createdAt: timestamp,
          lastLogin: timestamp
        };

        await userRef.set(newUser);
        logger.info(`Created new user: ${userId}`);
        return newUser;
      } else {
        // Update existing user
        await userRef.update({
          lastLogin: timestamp,
          ...(userData.displayName && { displayName: userData.displayName }),
          ...(userData.avatarUrl && { avatarUrl: userData.avatarUrl })
        });

        const updatedDoc = await userRef.get();
        return updatedDoc.data();
      }
    } catch (error) {
      logger.error('Error in createOrUpdateUser:', error);
      throw new AppError('Failed to create or update user', 500);
    }
  }

  async getUserById(userId) {
    try {
      const userDoc = await collections().users().doc(userId).get();

      if (!userDoc.exists) {
        throw new AppError('User not found', 404);
      }

      return userDoc.data();
    } catch (error) {
      logger.error('Error in getUserById:', error);
      throw error;
    }
  }

  async updateUser(userId, updates) {
    try {
      const userRef = collections().users().doc(userId);

      // Don't allow updating sensitive fields
      const allowedUpdates = {};
      const allowedFields = ['displayName', 'avatarUrl', 'bio', 'preferences'];

      for (const field of allowedFields) {
        if (updates[field] !== undefined) {
          allowedUpdates[field] = updates[field];
        }
      }

      await userRef.update(allowedUpdates);

      const updatedDoc = await userRef.get();
      return updatedDoc.data();
    } catch (error) {
      logger.error('Error in updateUser:', error);
      throw new AppError('Failed to update user', 500);
    }
  }

  async addXP(userId, amount) {
    try {
      const userRef = collections().users().doc(userId);
      const userDoc = await userRef.get();

      if (!userDoc.exists) {
        throw new AppError('User not found', 404);
      }

      const userData = userDoc.data();
      const newXP = (userData.xp || 0) + amount;
      const newLevel = Math.floor(newXP / 1000) + 1; // 1000 XP per level

      const leveledUp = newLevel > (userData.level || 1);

      await userRef.update({
        xp: newXP,
        level: newLevel
      });

      return { xp: newXP, level: newLevel, leveledUp };
    } catch (error) {
      logger.error('Error in addXP:', error);
      throw error;
    }
  }

  async addCoins(userId, amount) {
    try {
      const userRef = collections().users().doc(userId);
      await userRef.update({
        coins: admin.firestore.FieldValue.increment(amount)
      });
    } catch (error) {
      logger.error('Error in addCoins:', error);
      throw error;
    }
  }

  async getUserStats(userId) {
    try {
      const userDoc = await collections().users().doc(userId).get();

      if (!userDoc.exists) {
        throw new AppError('User not found', 404);
      }

      const userData = userDoc.data();
      return {
        userId,
        level: userData.level,
        xp: userData.xp,
        stats: userData.stats,
        rank: await this.getUserRank(userId)
      };
    } catch (error) {
      logger.error('Error in getUserStats:', error);
      throw error;
    }
  }

  async getUserRank(userId) {
    try {
      // Get all users ordered by XP
      const usersSnapshot = await collections().users()
        .orderBy('xp', 'desc')
        .get();

      let rank = 1;
      for (const doc of usersSnapshot.docs) {
        if (doc.id === userId) {
          return rank;
        }
        rank++;
      }

      return null;
    } catch (error) {
      logger.error('Error in getUserRank:', error);
      return null;
    }
  }

  async getLeaderboard(period = 'all-time', limit = 100) {
    try {
      let query = collections().users()
        .orderBy('xp', 'desc')
        .limit(limit);

      const snapshot = await query.get();

      return snapshot.docs.map((doc, index) => {
        const data = doc.data();
        return {
          rank: index + 1,
          userId: doc.id,
          displayName: data.displayName,
          avatarUrl: data.avatarUrl,
          level: data.level,
          xp: data.xp,
          stats: data.stats
        };
      });
    } catch (error) {
      logger.error('Error in getLeaderboard:', error);
      throw new AppError('Failed to fetch leaderboard', 500);
    }
  }

  async getUserGallery(userId, limit = 20, offset = 0) {
    try {
      const snapshot = await collections().gallery()
        .where('userId', '==', userId)
        .orderBy('createdAt', 'desc')
        .limit(limit)
        .offset(offset)
        .get();

      return snapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      }));
    } catch (error) {
      logger.error('Error in getUserGallery:', error);
      throw new AppError('Failed to fetch gallery', 500);
    }
  }

  async saveToGallery(userId, submissionId, title) {
    try {
      const galleryRef = collections().gallery().doc();

      const galleryItem = {
        userId,
        submissionId,
        title,
        createdAt: new Date()
      };

      await galleryRef.set(galleryItem);

      return { id: galleryRef.id, ...galleryItem };
    } catch (error) {
      logger.error('Error in saveToGallery:', error);
      throw new AppError('Failed to save to gallery', 500);
    }
  }

  async deleteUser(userId) {
    try {
      await collections().users().doc(userId).delete();
      logger.info(`Deleted user: ${userId}`);
    } catch (error) {
      logger.error('Error in deleteUser:', error);
      throw new AppError('Failed to delete user', 500);
    }
  }
}
