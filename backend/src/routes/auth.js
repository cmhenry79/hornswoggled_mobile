import express from 'express';
import { authenticate } from '../middleware/auth.js';
import { asyncHandler } from '../middleware/errorHandler.js';
import { UserService } from '../services/userService.js';

const router = express.Router();
const userService = new UserService();

// Register/Login - Create or update user profile
router.post('/login', authenticate, asyncHandler(async (req, res) => {
  const { displayName, avatarUrl } = req.body;

  const user = await userService.createOrUpdateUser(
    req.user.uid,
    {
      email: req.user.email,
      displayName: displayName || req.user.email?.split('@')[0],
      avatarUrl: avatarUrl || null
    }
  );

  res.status(200).json({
    success: true,
    data: { user }
  });
}));

// Get current user profile
router.get('/me', authenticate, asyncHandler(async (req, res) => {
  const user = await userService.getUserById(req.user.uid);

  res.status(200).json({
    success: true,
    data: { user }
  });
}));

// Update user profile
router.patch('/me', authenticate, asyncHandler(async (req, res) => {
  const updates = req.body;
  const user = await userService.updateUser(req.user.uid, updates);

  res.status(200).json({
    success: true,
    data: { user }
  });
}));

// Delete account
router.delete('/me', authenticate, asyncHandler(async (req, res) => {
  await userService.deleteUser(req.user.uid);

  res.status(200).json({
    success: true,
    message: 'Account deleted successfully'
  });
}));

export default router;
