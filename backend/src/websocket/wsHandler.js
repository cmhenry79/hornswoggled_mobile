import { logger } from '../utils/logger.js';
import { getAuth } from '../config/firebase.js';
import { collections } from '../config/firebase.js';
import { filterProfanity } from '../utils/profanityFilter.js';
import sanitizeHtml from 'sanitize-html';

const connections = new Map(); // userId -> ws connection
const roomSubscriptions = new Map(); // roomId -> Set of userIds

const MAX_MESSAGE_LENGTH = 500;
const MAX_MESSAGES_PER_MINUTE = 20;
const messageRateLimits = new Map(); // userId -> { count, resetTime }

export const setupWebSocket = (wss) => {
  wss.on('connection', async (ws, req) => {
    let userId = null;
    let currentRoomId = null;

    logger.info('WebSocket connection established');

    ws.on('message', async (message) => {
      try {
        const data = JSON.parse(message.toString());

        switch (data.type) {
          case 'auth':
            userId = await authenticateUser(data.token);
            if (userId) {
              connections.set(userId, ws);
              ws.send(JSON.stringify({
                type: 'auth_success',
                userId
              }));
              logger.info(`User ${userId} authenticated via WebSocket`);
            } else {
              ws.send(JSON.stringify({
                type: 'auth_failed',
                message: 'Invalid token'
              }));
            }
            break;

          case 'join_room':
            if (!userId) {
              ws.send(JSON.stringify({
                type: 'error',
                message: 'Not authenticated'
              }));
              return;
            }

            currentRoomId = data.roomId;
            subscribeToRoom(userId, currentRoomId);

            ws.send(JSON.stringify({
              type: 'room_joined',
              roomId: currentRoomId
            }));

            // Notify other players
            broadcastToRoom(currentRoomId, {
              type: 'player_joined',
              userId,
              timestamp: new Date().toISOString()
            }, userId);

            break;

          case 'leave_room':
            if (currentRoomId) {
              unsubscribeFromRoom(userId, currentRoomId);

              broadcastToRoom(currentRoomId, {
                type: 'player_left',
                userId,
                timestamp: new Date().toISOString()
              }, userId);

              currentRoomId = null;
            }
            break;

          case 'ping':
            ws.send(JSON.stringify({ type: 'pong', timestamp: Date.now() }));
            break;

          case 'chat':
            if (!currentRoomId || !userId) {
              ws.send(JSON.stringify({
                type: 'error',
                message: 'Not in a room or not authenticated'
              }));
              return;
            }

            // Rate limiting
            if (!checkRateLimit(userId)) {
              ws.send(JSON.stringify({
                type: 'error',
                message: 'Too many messages. Please slow down.'
              }));
              return;
            }

            // Validate message
            if (!data.message || typeof data.message !== 'string') {
              ws.send(JSON.stringify({
                type: 'error',
                message: 'Invalid message format'
              }));
              return;
            }

            // Sanitize and filter message
            let cleanMessage = sanitizeHtml(data.message, {
              allowedTags: [],
              allowedAttributes: {}
            }).trim();

            if (cleanMessage.length === 0) {
              ws.send(JSON.stringify({
                type: 'error',
                message: 'Message cannot be empty'
              }));
              return;
            }

            if (cleanMessage.length > MAX_MESSAGE_LENGTH) {
              ws.send(JSON.stringify({
                type: 'error',
                message: `Message too long (max ${MAX_MESSAGE_LENGTH} characters)`
              }));
              return;
            }

            // Filter profanity
            cleanMessage = filterProfanity(cleanMessage);

            broadcastToRoom(currentRoomId, {
              type: 'chat_message',
              userId,
              message: cleanMessage,
              timestamp: new Date().toISOString()
            });
            break;

          default:
            ws.send(JSON.stringify({
              type: 'error',
              message: 'Unknown message type'
            }));
        }
      } catch (error) {
        logger.error('WebSocket message error:', error);
        ws.send(JSON.stringify({
          type: 'error',
          message: 'Internal server error'
        }));
      }
    });

    ws.on('close', () => {
      if (userId) {
        connections.delete(userId);
        if (currentRoomId) {
          unsubscribeFromRoom(userId, currentRoomId);
          broadcastToRoom(currentRoomId, {
            type: 'player_disconnected',
            userId,
            timestamp: new Date().toISOString()
          });
        }
        logger.info(`User ${userId} disconnected`);
      }
    });

    ws.on('error', (error) => {
      logger.error('WebSocket error:', error);
    });

    // Send welcome message
    ws.send(JSON.stringify({
      type: 'connected',
      message: 'Connected to Hornswoggled WebSocket server'
    }));
  });

  logger.info('WebSocket server initialized');
};

async function authenticateUser(token) {
  try {
    if (!token || typeof token !== 'string') {
      return null;
    }
    const decodedToken = await getAuth().verifyIdToken(token);
    return decodedToken.uid;
  } catch (error) {
    logger.error('WebSocket auth failed:', error);
    return null;
  }
}

function checkRateLimit(userId) {
  if (!userId) return false;

  const now = Date.now();
  const limit = messageRateLimits.get(userId);

  if (!limit || now > limit.resetTime) {
    // Reset or create new limit
    messageRateLimits.set(userId, {
      count: 1,
      resetTime: now + 60000 // 1 minute
    });
    return true;
  }

  if (limit.count >= MAX_MESSAGES_PER_MINUTE) {
    return false;
  }

  limit.count++;
  return true;
}

function subscribeToRoom(userId, roomId) {
  if (!roomSubscriptions.has(roomId)) {
    roomSubscriptions.set(roomId, new Set());
  }
  roomSubscriptions.get(roomId).add(userId);
}

function unsubscribeFromRoom(userId, roomId) {
  const subscribers = roomSubscriptions.get(roomId);
  if (subscribers) {
    subscribers.delete(userId);
    if (subscribers.size === 0) {
      roomSubscriptions.delete(roomId);
    }
  }
}

function broadcastToRoom(roomId, message, excludeUserId = null) {
  const subscribers = roomSubscriptions.get(roomId);
  if (!subscribers) return;

  const payload = JSON.stringify(message);

  subscribers.forEach(userId => {
    if (userId !== excludeUserId) {
      const ws = connections.get(userId);
      if (ws && ws.readyState === 1) { // OPEN
        ws.send(payload);
      }
    }
  });
}

// Utility functions for other services to broadcast events
export function broadcastGameEvent(roomId, event) {
  broadcastToRoom(roomId, {
    type: 'game_event',
    event,
    timestamp: new Date().toISOString()
  });
}

export function notifyPlayer(userId, notification) {
  const ws = connections.get(userId);
  if (ws && ws.readyState === 1) {
    ws.send(JSON.stringify({
      type: 'notification',
      ...notification,
      timestamp: new Date().toISOString()
    }));
  }
}

export function getOnlineUsers() {
  return Array.from(connections.keys());
}

export function getRoomParticipants(roomId) {
  return Array.from(roomSubscriptions.get(roomId) || []);
}
