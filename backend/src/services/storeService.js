import { collections } from '../config/firebase.js';
import { AppError } from '../middleware/errorHandler.js';
import { logger } from '../utils/logger.js';

export class StoreService {
  async getStoreItems(filters = {}) {
    try {
      let query = collections().collection('store_items');

      if (filters.category) {
        query = query.where('category', '==', filters.category);
      }

      query = query.where('available', '==', true);

      const snapshot = await query
        .limit(filters.limit || 50)
        .offset(filters.offset || 0)
        .get();

      return snapshot.docs.map(doc => ({
        itemId: doc.id,
        ...doc.data()
      }));
    } catch (error) {
      logger.error('Error in getStoreItems:', error);
      throw new AppError('Failed to fetch store items', 500);
    }
  }

  async getUserInventory(userId) {
    try {
      const userDoc = await collections().users().doc(userId).get();

      if (!userDoc.exists) {
        throw new AppError('User not found', 404);
      }

      const userData = userDoc.data();

      return {
        coins: userData.coins || 0,
        cosmetics: userData.cosmetics || { owned: [], equipped: {} },
        unlockedWordPacks: userData.unlockedWordPacks || ['default'],
        powerups: userData.powerups || {}
      };
    } catch (error) {
      logger.error('Error in getUserInventory:', error);
      throw error;
    }
  }

  async purchaseItem(userId, itemId, paymentMethod = 'coins') {
    try {
      const userRef = collections().users().doc(userId);
      const userDoc = await userRef.get();

      if (!userDoc.exists) {
        throw new AppError('User not found', 404);
      }

      const userData = userDoc.data();

      // Get item details
      const itemDoc = await collections().collection('store_items').doc(itemId).get();

      if (!itemDoc.exists) {
        throw new AppError('Item not found', 404);
      }

      const item = itemDoc.data();

      // Check if already owned
      if (userData.cosmetics?.owned?.includes(itemId)) {
        throw new AppError('Item already owned', 400);
      }

      // Check if user has enough coins
      if (paymentMethod === 'coins') {
        if (userData.coins < item.price) {
          throw new AppError('Insufficient coins', 400);
        }

        // Deduct coins and add item
        await userRef.update({
          coins: userData.coins - item.price,
          'cosmetics.owned': [...(userData.cosmetics?.owned || []), itemId]
        });
      }

      // Record purchase
      await collections().purchases().add({
        userId,
        itemId,
        price: item.price,
        paymentMethod,
        purchasedAt: new Date()
      });

      logger.info(`User ${userId} purchased item ${itemId}`);

      return {
        success: true,
        item,
        newBalance: paymentMethod === 'coins' ? userData.coins - item.price : userData.coins
      };
    } catch (error) {
      logger.error('Error in purchaseItem:', error);
      throw error;
    }
  }

  async equipItem(userId, itemId, slot) {
    try {
      const userRef = collections().users().doc(userId);
      const userDoc = await userRef.get();

      if (!userDoc.exists) {
        throw new AppError('User not found', 404);
      }

      const userData = userDoc.data();

      // Check if user owns the item
      if (!userData.cosmetics?.owned?.includes(itemId)) {
        throw new AppError('Item not owned', 400);
      }

      // Equip item
      await userRef.update({
        [`cosmetics.equipped.${slot}`]: itemId
      });

      return { success: true, equippedItem: itemId, slot };
    } catch (error) {
      logger.error('Error in equipItem:', error);
      throw error;
    }
  }

  async unlockViaAd(userId, itemId, adToken) {
    try {
      // In production, verify ad token with ad network
      // For now, we'll just grant the item

      const userRef = collections().users().doc(userId);
      const userDoc = await userRef.get();

      if (!userDoc.exists) {
        throw new AppError('User not found', 404);
      }

      const userData = userDoc.data();

      // Check if already owned
      if (userData.cosmetics?.owned?.includes(itemId)) {
        throw new AppError('Item already owned', 400);
      }

      // Grant item
      await userRef.update({
        'cosmetics.owned': [...(userData.cosmetics?.owned || []), itemId]
      });

      logger.info(`User ${userId} unlocked item ${itemId} via ad`);

      return { success: true, itemId };
    } catch (error) {
      logger.error('Error in unlockViaAd:', error);
      throw error;
    }
  }
}
