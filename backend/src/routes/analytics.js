import express from 'express';
import { authenticate, optionalAuth } from '../middleware/auth.js';
import { asyncHandler } from '../middleware/errorHandler.js';
import { AnalyticsService } from '../services/analyticsService.js';

const router = express.Router();
const analyticsService = new AnalyticsService();

// Track event
router.post('/event', optionalAuth, asyncHandler(async (req, res) => {
  const { eventName, properties = {} } = req.body;

  await analyticsService.trackEvent({
    eventName,
    userId: req.user?.uid,
    properties,
    timestamp: new Date()
  });

  res.status(201).json({
    success: true,
    message: 'Event tracked'
  });
}));

// Track multiple events (batch)
router.post('/events/batch', optionalAuth, asyncHandler(async (req, res) => {
  const { events } = req.body;

  await analyticsService.trackEventsBatch(events, req.user?.uid);

  res.status(201).json({
    success: true,
    message: `${events.length} events tracked`
  });
}));

export default router;
