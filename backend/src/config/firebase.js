import admin from 'firebase-admin';
import { Firestore } from '@google-cloud/firestore';
import { logger } from '../utils/logger.js';

let db = null;
let storage = null;
let auth = null;

export const initializeFirebase = () => {
  try {
    // Initialize Firebase Admin SDK
    const serviceAccount = {
      type: 'service_account',
      project_id: process.env.FIREBASE_PROJECT_ID,
      client_email: process.env.FIREBASE_CLIENT_EMAIL,
      private_key: process.env.FIREBASE_PRIVATE_KEY?.replace(/\\n/g, '\n')
    };

    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      databaseURL: process.env.FIREBASE_DATABASE_URL,
      storageBucket: process.env.FIREBASE_STORAGE_BUCKET
    });

    // Initialize Firestore
    db = new Firestore({
      projectId: process.env.FIREBASE_PROJECT_ID,
      timestampsInSnapshots: true
    });

    storage = admin.storage();
    auth = admin.auth();

    logger.info('✅ Firebase initialized successfully');
  } catch (error) {
    logger.error('❌ Failed to initialize Firebase:', error);
    throw error;
  }
};

export const getFirestore = () => {
  if (!db) {
    throw new Error('Firestore not initialized');
  }
  return db;
};

export const getStorage = () => {
  if (!storage) {
    throw new Error('Storage not initialized');
  }
  return storage;
};

export const getAuth = () => {
  if (!auth) {
    throw new Error('Auth not initialized');
  }
  return auth;
};

// Collection references
export const collections = {
  users: () => db.collection('users'),
  rooms: () => db.collection('rooms'),
  games: () => db.collection('games'),
  rounds: () => db.collection('rounds'),
  submissions: () => db.collection('submissions'),
  wordPacks: () => db.collection('word_packs'),
  words: () => db.collection('words'),
  bots: () => db.collection('bots'),
  purchases: () => db.collection('purchases'),
  gallery: () => db.collection('gallery_items'),
  analytics: () => db.collection('analytics_events'),
  leaderboards: () => db.collection('leaderboards')
};
