import express from 'express';
import { authenticate } from '../middleware/auth.js';
import { asyncHandler } from '../middleware/errorHandler.js';
import {
  validatePurchase,
  validateEquip,
  validatePagination,
  sanitizeAllInputs
} from '../middleware/validation.js';
import { StoreService } from '../services/storeService.js';

const router = express.Router();
const storeService = new StoreService();

// Get all store items
router.get('/items', validatePagination, asyncHandler(async (req, res) => {
  const { category, limit = 50, offset = 0 } = req.query;

  const items = await storeService.getStoreItems({
    category,
    limit: parseInt(limit),
    offset: parseInt(offset)
  });

  res.status(200).json({
    success: true,
    data: { items }
  });
}));

// Get user's inventory
router.get('/inventory', authenticate, asyncHandler(async (req, res) => {
  const inventory = await storeService.getUserInventory(req.user.uid);

  res.status(200).json({
    success: true,
    data: { inventory }
  });
}));

// Purchase item
router.post('/purchase', authenticate, sanitizeAllInputs, validatePurchase, asyncHandler(async (req, res) => {
  const { itemId, paymentMethod = 'coins' } = req.body;

  const purchase = await storeService.purchaseItem(req.user.uid, itemId, paymentMethod);

  res.status(201).json({
    success: true,
    data: { purchase }
  });
}));

// Equip cosmetic
router.post('/equip', authenticate, sanitizeAllInputs, validateEquip, asyncHandler(async (req, res) => {
  const { itemId, slot } = req.body;

  const result = await storeService.equipItem(req.user.uid, itemId, slot);

  res.status(200).json({
    success: true,
    data: result
  });
}));

// Unlock via ad watch
router.post('/unlock/ad', authenticate, sanitizeAllInputs, validatePurchase, asyncHandler(async (req, res) => {
  const { itemId, adToken } = req.body;

  const result = await storeService.unlockViaAd(req.user.uid, itemId, adToken);

  res.status(200).json({
    success: true,
    data: result
  });
}));

export default router;
