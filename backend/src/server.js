import express from 'express';
import { createServer } from 'http';
import { WebSocketServer } from 'ws';
import cors from 'cors';
import helmet from 'helmet';
import compression from 'compression';
import dotenv from 'dotenv';
import { initializeFirebase } from './config/firebase.js';
import { logger } from './utils/logger.js';
import { errorHandler } from './middleware/errorHandler.js';
import { rateLimiter } from './middleware/rateLimiter.js';
import { setupWebSocket } from './websocket/wsHandler.js';

// Routes
import authRoutes from './routes/auth.js';
import roomRoutes from './routes/rooms.js';
import gameRoutes from './routes/game.js';
import userRoutes from './routes/users.js';
import storeRoutes from './routes/store.js';
import wordPackRoutes from './routes/wordPacks.js';
import botRoutes from './routes/bots.js';
import analyticsRoutes from './routes/analytics.js';

// Load environment variables
dotenv.config();

const PORT = process.env.PORT || 8080;
const app = express();
const server = createServer(app);

// Initialize Firebase
initializeFirebase();

// Middleware
app.use(helmet({
  contentSecurityPolicy: false, // Allow WebSocket connections
  crossOriginEmbedderPolicy: false
}));
app.use(cors({
  origin: process.env.CORS_ORIGIN || '*',
  credentials: true
}));
app.use(compression());
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Apply rate limiting
if (process.env.ENABLE_RATE_LIMITING === 'true') {
  app.use('/api/', rateLimiter);
}

// Request logging
app.use((req, res, next) => {
  logger.info(`${req.method} ${req.path}`, {
    ip: req.ip,
    userAgent: req.get('user-agent')
  });
  next();
});

// Health check (for Cloud Run)
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'healthy',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
    environment: process.env.NODE_ENV
  });
});

// API Routes
app.use('/api/auth', authRoutes);
app.use('/api/rooms', roomRoutes);
app.use('/api/game', gameRoutes);
app.use('/api/users', userRoutes);
app.use('/api/store', storeRoutes);
app.use('/api/word-packs', wordPackRoutes);
app.use('/api/bots', botRoutes);
app.use('/api/analytics', analyticsRoutes);

// Root endpoint
app.get('/', (req, res) => {
  res.json({
    service: 'Hornswoggled Game Backend',
    version: '1.0.0',
    status: 'running',
    endpoints: {
      health: '/health',
      api: '/api',
      websocket: '/ws'
    }
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    error: 'Not Found',
    message: `Cannot ${req.method} ${req.path}`,
    timestamp: new Date().toISOString()
  });
});

// Error handler (must be last)
app.use(errorHandler);

// Setup WebSocket server
const wss = new WebSocketServer({
  server,
  path: '/ws'
});

setupWebSocket(wss);

// Start server
server.listen(PORT, () => {
  logger.info(`🎮 Hornswoggled Backend started on port ${PORT}`);
  logger.info(`📊 Environment: ${process.env.NODE_ENV}`);
  logger.info(`🔌 WebSocket server ready at ws://localhost:${PORT}/ws`);
});

// Graceful shutdown
const shutdown = async () => {
  logger.info('Shutting down gracefully...');

  server.close(() => {
    logger.info('HTTP server closed');
  });

  wss.close(() => {
    logger.info('WebSocket server closed');
  });

  // Close Firebase connections
  setTimeout(() => {
    process.exit(0);
  }, 10000);
};

process.on('SIGTERM', shutdown);
process.on('SIGINT', shutdown);

export default app;
