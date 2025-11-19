import { body, param, query, validationResult } from 'express-validator';
import sanitizeHtml from 'sanitize-html';
import { AppError } from './errorHandler.js';

// Middleware to handle validation errors
export const handleValidationErrors = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    const errorMessages = errors.array().map(err => ({
      field: err.path || err.param,
      message: err.msg
    }));
    throw new AppError('Validation failed', 400, errorMessages);
  }
  next();
};

// Sanitize HTML from string fields
export const sanitizeInput = (field) => {
  return (req, res, next) => {
    const value = req.body[field];
    if (value && typeof value === 'string') {
      req.body[field] = sanitizeHtml(value, {
        allowedTags: [],
        allowedAttributes: {}
      }).trim();
    }
    next();
  };
};

// Comprehensive sanitization middleware for all string inputs
export const sanitizeAllInputs = (req, res, next) => {
  const sanitizeObject = (obj) => {
    if (!obj || typeof obj !== 'object') return obj;

    for (const key in obj) {
      if (typeof obj[key] === 'string') {
        obj[key] = sanitizeHtml(obj[key], {
          allowedTags: [],
          allowedAttributes: {}
        });
      } else if (typeof obj[key] === 'object' && obj[key] !== null) {
        sanitizeObject(obj[key]);
      }
    }
    return obj;
  };

  if (req.body) {
    req.body = sanitizeObject({ ...req.body });
  }

  next();
};

// Auth validation
export const validateLogin = [
  body('displayName')
    .optional()
    .isString()
    .trim()
    .isLength({ min: 3, max: 30 })
    .withMessage('Display name must be between 3 and 30 characters')
    .matches(/^[a-zA-Z0-9_\s-]+$/)
    .withMessage('Display name can only contain letters, numbers, spaces, hyphens, and underscores'),
  body('avatarUrl')
    .optional()
    .isURL()
    .withMessage('Avatar URL must be a valid URL'),
  handleValidationErrors
];

export const validateUpdateProfile = [
  body('displayName')
    .optional()
    .isString()
    .trim()
    .isLength({ min: 3, max: 30 })
    .withMessage('Display name must be between 3 and 30 characters')
    .matches(/^[a-zA-Z0-9_\s-]+$/)
    .withMessage('Display name can only contain letters, numbers, spaces, hyphens, and underscores'),
  body('avatarUrl')
    .optional()
    .isURL()
    .withMessage('Avatar URL must be a valid URL'),
  body('bio')
    .optional()
    .isString()
    .trim()
    .isLength({ max: 200 })
    .withMessage('Bio must be 200 characters or less'),
  handleValidationErrors
];

// Room validation
export const validateCreateRoom = [
  body('visibility')
    .optional()
    .isIn(['public', 'private'])
    .withMessage('Visibility must be either public or private'),
  body('maxPlayers')
    .optional()
    .isInt({ min: 2, max: 12 })
    .withMessage('Max players must be between 2 and 12'),
  body('maxRounds')
    .optional()
    .isInt({ min: 1, max: 10 })
    .withMessage('Max rounds must be between 1 and 10'),
  body('roundDuration')
    .optional()
    .isInt({ min: 30, max: 180 })
    .withMessage('Round duration must be between 30 and 180 seconds'),
  body('wordPackIds')
    .optional()
    .isArray()
    .withMessage('Word pack IDs must be an array'),
  body('wordPackIds.*')
    .optional()
    .isString()
    .withMessage('Each word pack ID must be a string'),
  body('mutators')
    .optional()
    .isArray()
    .withMessage('Mutators must be an array'),
  body('enableBots')
    .optional()
    .isBoolean()
    .withMessage('Enable bots must be a boolean'),
  handleValidationErrors
];

export const validateRoomCode = [
  param('code')
    .isString()
    .trim()
    .isLength({ min: 4, max: 6 })
    .matches(/^[A-Z0-9]+$/)
    .withMessage('Invalid room code format'),
  handleValidationErrors
];

export const validateRoomId = [
  param('roomId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Room ID is required'),
  handleValidationErrors
];

export const validateAddBot = [
  param('roomId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Room ID is required'),
  body('botId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Bot ID is required'),
  body('difficulty')
    .optional()
    .isIn(['easy', 'medium', 'hard'])
    .withMessage('Difficulty must be easy, medium, or hard'),
  handleValidationErrors
];

// Game validation
export const validateGameId = [
  param('gameId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Game ID is required'),
  handleValidationErrors
];

export const validateSubmitAnswer = [
  param('gameId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Game ID is required'),
  body('roundId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Round ID is required'),
  body('content')
    .isString()
    .trim()
    .isLength({ min: 1, max: 500 })
    .withMessage('Content must be between 1 and 500 characters'),
  body('type')
    .optional()
    .isIn(['text', 'gif', 'image', 'doodle', 'emoji'])
    .withMessage('Type must be text, gif, image, doodle, or emoji'),
  handleValidationErrors
];

export const validateSubmitImage = [
  param('gameId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Game ID is required'),
  body('roundId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Round ID is required'),
  body('type')
    .optional()
    .isIn(['image', 'doodle'])
    .withMessage('Type must be image or doodle'),
  handleValidationErrors
];

export const validateVote = [
  param('gameId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Game ID is required'),
  body('roundId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Round ID is required'),
  body('submissionId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Submission ID is required'),
  handleValidationErrors
];

export const validateUsePowerup = [
  param('gameId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Game ID is required'),
  body('powerupType')
    .isString()
    .trim()
    .isIn(['double_points', 'time_freeze', 'steal_vote', 'reveal_judge'])
    .withMessage('Invalid powerup type'),
  body('targetUserId')
    .optional()
    .isString()
    .trim()
    .withMessage('Target user ID must be a string'),
  handleValidationErrors
];

// Store validation
export const validatePurchase = [
  body('itemId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Item ID is required'),
  body('paymentMethod')
    .optional()
    .isIn(['coins', 'real_money', 'ad'])
    .withMessage('Payment method must be coins, real_money, or ad'),
  handleValidationErrors
];

export const validateEquip = [
  body('itemId')
    .isString()
    .trim()
    .notEmpty()
    .withMessage('Item ID is required'),
  body('slot')
    .optional()
    .isString()
    .trim()
    .isIn(['avatar', 'frame', 'badge', 'emote'])
    .withMessage('Slot must be avatar, frame, badge, or emote'),
  handleValidationErrors
];

// Query validation
export const validatePagination = [
  query('limit')
    .optional()
    .isInt({ min: 1, max: 100 })
    .withMessage('Limit must be between 1 and 100'),
  query('offset')
    .optional()
    .isInt({ min: 0 })
    .withMessage('Offset must be 0 or greater'),
  handleValidationErrors
];

// Analytics validation
export const validateTrackEvent = [
  body('eventName')
    .isString()
    .trim()
    .notEmpty()
    .isLength({ max: 100 })
    .withMessage('Event name is required and must be 100 characters or less'),
  body('properties')
    .optional()
    .isObject()
    .withMessage('Properties must be an object'),
  handleValidationErrors
];

// File upload validation middleware
export const validateImageUpload = (req, res, next) => {
  if (!req.file) {
    throw new AppError('No image file provided', 400);
  }

  const allowedMimeTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!allowedMimeTypes.includes(req.file.mimetype)) {
    throw new AppError('Invalid file type. Only JPEG, PNG, GIF, and WebP are allowed', 400);
  }

  const maxSize = 5 * 1024 * 1024; // 5MB
  if (req.file.size > maxSize) {
    throw new AppError('File too large. Maximum size is 5MB', 400);
  }

  next();
};
