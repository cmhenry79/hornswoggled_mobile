import express from 'express';
import { authenticate, optionalAuth } from '../middleware/auth.js';
import { asyncHandler } from '../middleware/errorHandler.js';
import { WordPackService } from '../services/wordPackService.js';

const router = express.Router();
const wordPackService = new WordPackService();

// Get all word packs
router.get('/', optionalAuth, asyncHandler(async (req, res) => {
  const { category, includeOwned = false } = req.query;

  const packs = await wordPackService.getWordPacks({
    userId: req.user?.uid,
    category,
    includeOwned: includeOwned === 'true'
  });

  res.status(200).json({
    success: true,
    data: { packs }
  });
}));

// Get word pack details
router.get('/:packId', asyncHandler(async (req, res) => {
  const { packId } = req.params;

  const pack = await wordPackService.getWordPackById(packId);

  res.status(200).json({
    success: true,
    data: { pack }
  });
}));

// Unlock word pack
router.post('/:packId/unlock', authenticate, asyncHandler(async (req, res) => {
  const { packId } = req.params;
  const { paymentMethod = 'coins' } = req.body;

  const result = await wordPackService.unlockWordPack(req.user.uid, packId, paymentMethod);

  res.status(200).json({
    success: true,
    data: result
  });
}));

// Get random word from pack
router.get('/:packId/random', asyncHandler(async (req, res) => {
  const { packId } = req.params;

  const word = await wordPackService.getRandomWord(packId);

  res.status(200).json({
    success: true,
    data: { word }
  });
}));

export default router;
