import rateLimit from 'express-rate-limit';
import { logger } from '../utils/logger.js';

export const rateLimiter = rateLimit({
  windowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS) || 60000,
  max: parseInt(process.env.RATE_LIMIT_MAX_REQUESTS) || 100,
  message: {
    error: 'Too many requests from this IP, please try again later.',
    retryAfter: 60
  },
  standardHeaders: true,
  legacyHeaders: false,
  handler: (req, res) => {
    logger.warn('Rate limit exceeded', {
      ip: req.ip,
      path: req.path
    });
    res.status(429).json({
      error: 'Too many requests',
      message: 'You have exceeded the rate limit. Please try again later.',
      retryAfter: 60
    });
  }
});

export const strictRateLimiter = rateLimit({
  windowMs: 60000,
  max: 10,
  message: {
    error: 'Too many attempts, please slow down.',
    retryAfter: 60
  }
});

export const createRoomLimiter = rateLimit({
  windowMs: 24 * 60 * 60 * 1000, // 24 hours
  max: 100,
  message: {
    error: 'Daily room creation limit reached',
    retryAfter: 24 * 60 * 60
  }
});
