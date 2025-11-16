import { collections } from '../config/firebase.js';
import { AppError } from '../middleware/errorHandler.js';
import { logger } from '../utils/logger.js';

export class WordPackService {
  async getWordPacks(filters = {}) {
    try {
      let query = collections().wordPacks();

      if (filters.category) {
        query = query.where('category', '==', filters.category);
      }

      const snapshot = await query.get();

      let packs = snapshot.docs.map(doc => ({
        packId: doc.id,
        ...doc.data()
      }));

      // Filter based on user ownership if userId provided
      if (filters.userId && !filters.includeOwned) {
        const userDoc = await collections().users().doc(filters.userId).get();
        if (userDoc.exists) {
          const unlockedPacks = userDoc.data().unlockedWordPacks || [];
          packs = packs.filter(pack => !unlockedPacks.includes(pack.packId));
        }
      }

      return packs;
    } catch (error) {
      logger.error('Error in getWordPacks:', error);
      throw new AppError('Failed to fetch word packs', 500);
    }
  }

  async getWordPackById(packId) {
    try {
      const packDoc = await collections().wordPacks().doc(packId).get();

      if (!packDoc.exists) {
        throw new AppError('Word pack not found', 404);
      }

      const pack = {
        packId: packDoc.id,
        ...packDoc.data()
      };

      // Get word count
      const wordsSnapshot = await collections().words()
        .where('packId', '==', packId)
        .get();

      pack.wordCount = wordsSnapshot.size;

      return pack;
    } catch (error) {
      logger.error('Error in getWordPackById:', error);
      throw error;
    }
  }

  async unlockWordPack(userId, packId, paymentMethod = 'coins') {
    try {
      const userRef = collections().users().doc(userId);
      const userDoc = await userRef.get();

      if (!userDoc.exists) {
        throw new AppError('User not found', 404);
      }

      const userData = userDoc.data();

      // Check if already unlocked
      if (userData.unlockedWordPacks?.includes(packId)) {
        throw new AppError('Word pack already unlocked', 400);
      }

      // Get pack details
      const packDoc = await collections().wordPacks().doc(packId).get();

      if (!packDoc.exists) {
        throw new AppError('Word pack not found', 404);
      }

      const pack = packDoc.data();

      // Check payment
      if (paymentMethod === 'coins' && pack.premium) {
        if (userData.coins < pack.price) {
          throw new AppError('Insufficient coins', 400);
        }

        await userRef.update({
          coins: userData.coins - pack.price,
          unlockedWordPacks: [...(userData.unlockedWordPacks || []), packId]
        });
      } else {
        // Free pack
        await userRef.update({
          unlockedWordPacks: [...(userData.unlockedWordPacks || []), packId]
        });
      }

      logger.info(`User ${userId} unlocked word pack ${packId}`);

      return { success: true, packId };
    } catch (error) {
      logger.error('Error in unlockWordPack:', error);
      throw error;
    }
  }

  async getRandomWord(packId) {
    try {
      const wordsSnapshot = await collections().words()
        .where('packId', '==', packId)
        .get();

      if (wordsSnapshot.empty) {
        throw new AppError('No words found in pack', 404);
      }

      const words = wordsSnapshot.docs.map(doc => doc.data());
      const randomWord = words[Math.floor(Math.random() * words.length)];

      return randomWord;
    } catch (error) {
      logger.error('Error in getRandomWord:', error);
      throw error;
    }
  }
}
