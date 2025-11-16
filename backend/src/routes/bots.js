import express from 'express';
import { asyncHandler } from '../middleware/errorHandler.js';
import { BotService } from '../services/botService.js';

const router = express.Router();
const botService = new BotService();

// Get all available bots
router.get('/', asyncHandler(async (req, res) => {
  const bots = await botService.getAvailableBots();

  res.status(200).json({
    success: true,
    data: { bots }
  });
}));

// Get bot details
router.get('/:botId', asyncHandler(async (req, res) => {
  const { botId } = req.params;

  const bot = await botService.getBotById(botId);

  res.status(200).json({
    success: true,
    data: { bot }
  });
}));

export default router;
