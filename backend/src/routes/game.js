import express from 'express';
import { authenticate } from '../middleware/auth.js';
import { asyncHandler, AppError } from '../middleware/errorHandler.js';
import {
  validateGameId,
  validateSubmitAnswer,
  validateSubmitImage,
  validateVote,
  validateUsePowerup,
  validateImageUpload,
  sanitizeAllInputs
} from '../middleware/validation.js';
import { GameService } from '../services/gameService.js';
import multer from 'multer';

const router = express.Router();
const gameService = new GameService();

// Configure multer for memory storage
const upload = multer({
  storage: multer.memoryStorage(),
  limits: {
    fileSize: 5 * 1024 * 1024 // 5MB
  },
  fileFilter: (req, file, cb) => {
    const allowedMimeTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    if (allowedMimeTypes.includes(file.mimetype)) {
      cb(null, true);
    } else {
      cb(new AppError('Invalid file type. Only JPEG, PNG, GIF, and WebP are allowed', 400), false);
    }
  }
});

// Get game state
router.get('/:gameId', authenticate, validateGameId, asyncHandler(async (req, res) => {
  const { gameId } = req.params;
  const game = await gameService.getGameById(gameId);

  if (!game) {
    throw new AppError('Game not found', 404);
  }

  res.status(200).json({
    success: true,
    data: { game }
  });
}));

// Start a new round
router.post('/:gameId/round/start', authenticate, validateGameId, asyncHandler(async (req, res) => {
  const { gameId } = req.params;
  const round = await gameService.startRound(gameId, req.user.uid);

  res.status(200).json({
    success: true,
    data: { round }
  });
}));

// Submit answer (text)
router.post('/:gameId/submit', authenticate, sanitizeAllInputs, validateSubmitAnswer, asyncHandler(async (req, res) => {
  const { gameId } = req.params;
  const { roundId, content, type = 'text' } = req.body;

  const submission = await gameService.submitAnswer(
    gameId,
    roundId,
    req.user.uid,
    { type, content }
  );

  res.status(201).json({
    success: true,
    data: { submission }
  });
}));

// Submit image/doodle
router.post('/:gameId/submit/image',
  authenticate,
  upload.single('image'),
  validateSubmitImage,
  validateImageUpload,
  asyncHandler(async (req, res) => {
    const { gameId } = req.params;
    const { roundId, type = 'image' } = req.body;

    const submission = await gameService.submitImage(
      gameId,
      roundId,
      req.user.uid,
      req.file,
      type
    );

    res.status(201).json({
      success: true,
      data: { submission }
    });
  })
);

// Vote for submission
router.post('/:gameId/vote', authenticate, sanitizeAllInputs, validateVote, asyncHandler(async (req, res) => {
  const { gameId } = req.params;
  const { roundId, submissionId } = req.body;

  const result = await gameService.voteForSubmission(
    gameId,
    roundId,
    req.user.uid,
    submissionId
  );

  res.status(200).json({
    success: true,
    data: result
  });
}));

// Use powerup
router.post('/:gameId/powerup/use', authenticate, sanitizeAllInputs, validateUsePowerup, asyncHandler(async (req, res) => {
  const { gameId } = req.params;
  const { powerupType, targetUserId } = req.body;

  const result = await gameService.usePowerup(
    gameId,
    req.user.uid,
    powerupType,
    targetUserId
  );

  res.status(200).json({
    success: true,
    data: result
  });
}));

// Get round results
router.get('/:gameId/round/:roundId/results', authenticate, validateGameId, asyncHandler(async (req, res) => {
  const { gameId, roundId } = req.params;

  const results = await gameService.getRoundResults(gameId, roundId);

  res.status(200).json({
    success: true,
    data: { results }
  });
}));

// Get final game results
router.get('/:gameId/results', authenticate, validateGameId, asyncHandler(async (req, res) => {
  const { gameId } = req.params;

  const results = await gameService.getFinalResults(gameId);

  res.status(200).json({
    success: true,
    data: { results }
  });
}));

// End game early
router.post('/:gameId/end', authenticate, validateGameId, asyncHandler(async (req, res) => {
  const { gameId } = req.params;

  await gameService.endGame(gameId, req.user.uid);

  res.status(200).json({
    success: true,
    message: 'Game ended successfully'
  });
}));

export default router;
