import express from 'express';
import { authenticate } from '../middleware/auth.js';
import { asyncHandler } from '../middleware/errorHandler.js';
import { validatePagination, sanitizeAllInputs } from '../middleware/validation.js';
import { UserService } from '../services/userService.js';

const router = express.Router();
const userService = new UserService();

// Get user profile by ID
router.get('/:userId', asyncHandler(async (req, res) => {
  const { userId } = req.params;
  const user = await userService.getUserById(userId);

  res.status(200).json({
    success: true,
    data: { user }
  });
}));

// Get user stats
router.get('/:userId/stats', asyncHandler(async (req, res) => {
  const { userId } = req.params;
  const stats = await userService.getUserStats(userId);

  res.status(200).json({
    success: true,
    data: { stats }
  });
}));

// Get user gallery
router.get('/:userId/gallery', authenticate, validatePagination, asyncHandler(async (req, res) => {
  const { userId } = req.params;
  const { limit = 20, offset = 0 } = req.query;

  const gallery = await userService.getUserGallery(userId, parseInt(limit), parseInt(offset));

  res.status(200).json({
    success: true,
    data: { gallery }
  });
}));

// Save submission to gallery
router.post('/gallery/save', authenticate, sanitizeAllInputs, asyncHandler(async (req, res) => {
  const { submissionId, title } = req.body;

  const galleryItem = await userService.saveToGallery(req.user.uid, submissionId, title);

  res.status(201).json({
    success: true,
    data: { galleryItem }
  });
}));

// Get leaderboard
router.get('/leaderboard/global', asyncHandler(async (req, res) => {
  const { limit = 100, period = 'all-time' } = req.query;

  const leaderboard = await userService.getLeaderboard(period, parseInt(limit));

  res.status(200).json({
    success: true,
    data: { leaderboard }
  });
}));

export default router;
