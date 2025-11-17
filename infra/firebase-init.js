import admin from 'firebase-admin';
import { Firestore } from '@google-cloud/firestore';
import dotenv from 'dotenv';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

// Get current directory
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// Load .env from backend directory
dotenv.config({ path: join(__dirname, '../backend/.env') });

// Initialize Firebase Admin
const serviceAccount = {
  type: 'service_account',
  project_id: process.env.FIREBASE_PROJECT_ID,
  client_email: process.env.FIREBASE_CLIENT_EMAIL,
  private_key: process.env.FIREBASE_PRIVATE_KEY?.replace(/\\n/g, '\n')
};

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  storageBucket: process.env.FIREBASE_STORAGE_BUCKET
});

const db = new Firestore({
  projectId: process.env.FIREBASE_PROJECT_ID
});

console.log('🔥 Initializing Firestore with seed data...\n');

// Seed default bots
async function seedBots() {
  console.log('📦 Seeding bots...');

  const bots = [
    {
      botId: 'sarcastic-sam',
      name: 'Sarcastic Sam',
      persona: 'sarcastic',
      description: 'Master of dry wit and deadpan humor',
      humorStyle: 'sarcastic and dry',
      avatarUrl: 'https://api.dicebear.com/7.x/bottts/svg?seed=sam',
      traits: ['witty', 'clever', 'deadpan'],
      preferGifs: false,
      difficulty: 'medium'
    },
    {
      botId: 'punny-paula',
      name: 'Punny Paula',
      persona: 'punny',
      description: 'Queen of puns and wordplay',
      humorStyle: 'pun-heavy and playful',
      avatarUrl: 'https://api.dicebear.com/7.x/bottts/svg?seed=paula',
      traits: ['punny', 'playful', 'wordsmith'],
      preferGifs: false,
      difficulty: 'easy'
    },
    {
      botId: 'random-randy',
      name: 'Random Randy',
      persona: 'absurdist',
      description: 'Completely unpredictable and absurd',
      humorStyle: 'random and absurdist',
      avatarUrl: 'https://api.dicebear.com/7.x/bottts/svg?seed=randy',
      traits: ['random', 'absurd', 'chaotic'],
      preferGifs: true,
      difficulty: 'hard'
    },
    {
      botId: 'wholesome-wendy',
      name: 'Wholesome Wendy',
      persona: 'wholesome',
      description: 'Positive and silly humor',
      humorStyle: 'wholesome and silly',
      avatarUrl: 'https://api.dicebear.com/7.x/bottts/svg?seed=wendy',
      traits: ['positive', 'silly', 'kind'],
      preferGifs: false,
      difficulty: 'easy'
    },
    {
      botId: 'edgy-eddie',
      name: 'Edgy Eddie',
      persona: 'edgy',
      description: 'Dark humor master (within limits)',
      humorStyle: 'edgy but appropriate',
      avatarUrl: 'https://api.dicebear.com/7.x/bottts/svg?seed=eddie',
      traits: ['edgy', 'dark', 'bold'],
      preferGifs: false,
      difficulty: 'hard'
    },
    {
      botId: 'nerdy-ned',
      name: 'Nerdy Ned',
      persona: 'nerdy',
      description: 'Pop culture and reference master',
      humorStyle: 'nerdy and reference-heavy',
      avatarUrl: 'https://api.dicebear.com/7.x/bottts/svg?seed=ned',
      traits: ['nerdy', 'references', 'clever'],
      preferGifs: true,
      difficulty: 'medium'
    },
    {
      botId: 'chaotic-carla',
      name: 'Chaotic Carla',
      persona: 'chaotic',
      description: 'Completely unpredictable chaos agent',
      humorStyle: 'chaotic and unexpected',
      avatarUrl: 'https://api.dicebear.com/7.x/bottts/svg?seed=carla',
      traits: ['chaotic', 'unpredictable', 'wild'],
      preferGifs: true,
      difficulty: 'hard'
    }
  ];

  for (const bot of bots) {
    await db.collection('bots').doc(bot.botId).set(bot);
    console.log(`  ✓ Created bot: ${bot.name}`);
  }

  console.log(`✅ Seeded ${bots.length} bots\n`);
}

// Seed default word packs
async function seedWordPacks() {
  console.log('📦 Seeding word packs...');

  const packs = [
    {
      packId: 'default',
      title: 'Default Pack',
      description: 'Standard words to get started',
      category: 'general',
      premium: false,
      price: 0,
      wordCount: 100,
      coverUrl: null
    },
    {
      packId: 'starter',
      title: 'Starter Pack',
      description: 'Easy words for beginners',
      category: 'general',
      premium: false,
      price: 0,
      wordCount: 50,
      coverUrl: null
    },
    {
      packId: 'internet-culture',
      title: 'Internet Culture',
      description: 'Memes, viral trends, and online slang',
      category: 'culture',
      premium: true,
      price: 500,
      wordCount: 75,
      coverUrl: null
    },
    {
      packId: 'tech-nerds',
      title: 'Tech Nerds',
      description: 'Programming, gadgets, and tech terms',
      category: 'tech',
      premium: true,
      price: 500,
      wordCount: 80,
      coverUrl: null
    },
    {
      packId: 'pop-culture',
      title: 'Pop Culture',
      description: 'Movies, TV shows, music, and celebrities',
      category: 'culture',
      premium: true,
      price: 750,
      wordCount: 100,
      coverUrl: null
    },
    {
      packId: 'adulting',
      title: 'Adulting',
      description: 'Relatable adult situations and responsibilities',
      category: 'lifestyle',
      premium: true,
      price: 600,
      wordCount: 60,
      coverUrl: null
    }
  ];

  for (const pack of packs) {
    await db.collection('word_packs').doc(pack.packId).set(pack);
    console.log(`  ✓ Created word pack: ${pack.title}`);
  }

  console.log(`✅ Seeded ${packs.length} word packs\n`);
}

// Seed default words
async function seedWords() {
  console.log('📦 Seeding words...');

  const defaultWords = [
    'Happiness', 'Love', 'Success', 'Failure', 'Friendship',
    'Work', 'Weekend', 'Coffee', 'Sleep', 'Exercise',
    'Money', 'Technology', 'Social Media', 'Dating', 'Family',
    'Pizza', 'Vacation', 'Stress', 'Motivation', 'Procrastination',
    'Dreams', 'Goals', 'Adventure', 'Creativity', 'Courage',
    'Patience', 'Freedom', 'Justice', 'Truth', 'Beauty',
    'Knowledge', 'Wisdom', 'Power', 'Responsibility', 'Legacy',
    'Ambition', 'Passion', 'Empathy', 'Gratitude', 'Resilience',
    'Innovation', 'Tradition', 'Progress', 'Change', 'Stability',
    'Balance', 'Harmony', 'Chaos', 'Order', 'Simplicity'
  ];

  let count = 0;
  for (const word of defaultWords) {
    await db.collection('words').add({
      packId: 'default',
      word,
      category: 'general',
      difficulty: 'medium',
      addedAt: new Date()
    });
    count++;
  }

  console.log(`  ✓ Created ${count} default words`);
  console.log(`✅ Seeded words\n`);
}

// Seed store items
async function seedStoreItems() {
  console.log('📦 Seeding store items...');

  const items = [
    // Avatars
    { category: 'avatar', name: 'Cool Shades', price: 100, available: true },
    { category: 'avatar', name: 'Party Hat', price: 150, available: true },
    { category: 'avatar', name: 'Crown', price: 500, available: true },
    { category: 'avatar', name: 'Halo', price: 750, available: true },

    // Backgrounds
    { category: 'background', name: 'Sunset', price: 200, available: true },
    { category: 'background', name: 'Starry Night', price: 300, available: true },
    { category: 'background', name: 'Neon City', price: 400, available: true },

    // Frames
    { category: 'frame', name: 'Gold Frame', price: 250, available: true },
    { category: 'frame', name: 'Diamond Frame', price: 1000, available: true },

    // Badges
    { category: 'badge', name: 'First Win', price: 0, available: true },
    { category: 'badge', name: 'Comedy King', price: 500, available: true }
  ];

  for (const item of items) {
    await db.collection('store_items').add(item);
  }

  console.log(`  ✓ Created ${items.length} store items`);
  console.log(`✅ Seeded store items\n`);
}

// Run all seeds
async function runSeeds() {
  try {
    await seedBots();
    await seedWordPacks();
    await seedWords();
    await seedStoreItems();

    console.log('🎉 All seed data created successfully!');
    process.exit(0);
  } catch (error) {
    console.error('❌ Error seeding data:', error);
    process.exit(1);
  }
}

runSeeds();
