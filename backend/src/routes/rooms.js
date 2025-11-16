import express from 'express';
import { authenticate } from '../middleware/auth.js';
import { asyncHandler, AppError } from '../middleware/errorHandler.js';
import { createRoomLimiter } from '../middleware/rateLimiter.js';
import { RoomService } from '../services/roomService.js';

const router = express.Router();
const roomService = new RoomService();

// Create a new game room
router.post('/create', authenticate, createRoomLimiter, asyncHandler(async (req, res) => {
  const {
    visibility = 'public',
    maxPlayers = 8,
    maxRounds = 5,
    roundDuration = 90,
    wordPackIds = [],
    mutators = [],
    enableBots = true
  } = req.body;

  const room = await roomService.createRoom(req.user.uid, {
    visibility,
    maxPlayers,
    maxRounds,
    roundDuration,
    wordPackIds,
    mutators,
    enableBots
  });

  res.status(201).json({
    success: true,
    data: { room }
  });
}));

// Join a room by code
router.post('/join/:code', authenticate, asyncHandler(async (req, res) => {
  const { code } = req.params;
  const room = await roomService.joinRoom(code, req.user.uid);

  res.status(200).json({
    success: true,
    data: { room }
  });
}));

// Leave a room
router.post('/:roomId/leave', authenticate, asyncHandler(async (req, res) => {
  const { roomId } = req.params;
  await roomService.leaveRoom(roomId, req.user.uid);

  res.status(200).json({
    success: true,
    message: 'Left room successfully'
  });
}));

// Get room details
router.get('/:roomId', authenticate, asyncHandler(async (req, res) => {
  const { roomId } = req.params;
  const room = await roomService.getRoomById(roomId);

  if (!room) {
    throw new AppError('Room not found', 404);
  }

  res.status(200).json({
    success: true,
    data: { room }
  });
}));

// Update room settings (host only)
router.patch('/:roomId', authenticate, asyncHandler(async (req, res) => {
  const { roomId } = req.params;
  const updates = req.body;

  const room = await roomService.updateRoom(roomId, req.user.uid, updates);

  res.status(200).json({
    success: true,
    data: { room }
  });
}));

// Add bot to room
router.post('/:roomId/bots/add', authenticate, asyncHandler(async (req, res) => {
  const { roomId } = req.params;
  const { botId, difficulty = 'medium' } = req.body;

  const room = await roomService.addBot(roomId, req.user.uid, botId, difficulty);

  res.status(200).json({
    success: true,
    data: { room }
  });
}));

// Remove bot from room
router.delete('/:roomId/bots/:botId', authenticate, asyncHandler(async (req, res) => {
  const { roomId, botId } = req.params;

  await roomService.removeBot(roomId, req.user.uid, botId);

  res.status(200).json({
    success: true,
    message: 'Bot removed successfully'
  });
}));

// Get active public rooms
router.get('/', asyncHandler(async (req, res) => {
  const { limit = 20, offset = 0 } = req.query;

  const rooms = await roomService.getPublicRooms(parseInt(limit), parseInt(offset));

  res.status(200).json({
    success: true,
    data: { rooms }
  });
}));

// Start game in room
router.post('/:roomId/start', authenticate, asyncHandler(async (req, res) => {
  const { roomId } = req.params;

  const game = await roomService.startGame(roomId, req.user.uid);

  res.status(200).json({
    success: true,
    data: { game }
  });
}));

export default router;
