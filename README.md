# 🎮 Hornswoggled - The Ultimate Party Game Platform

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/hornswoggled/hornswoggled)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

**Hornswoggled** is a complete multiplayer party game platform where players compete to create the funniest definitions, memes, and responses. Featuring AI-powered bots using Google Gemini, real-time multiplayer gameplay, and a full monetization system.

---

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Quick Start](#quick-start)
- [Deployment](#deployment)
- [Development](#development)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Contributing](#contributing)
- [License](#license)

---

## ✨ Features

### 🎯 Core Gameplay
- **Real-time Multiplayer**: WebSocket-based gameplay with up to 12 players per room
- **AI Bots**: 7 unique bot personalities powered by Google Gemini
- **Multiple Submission Types**: Text, GIFs, images, doodles, and emoji mashups
- **Dynamic Rounds**: Customizable round duration, word packs, and game modes
- **Mutators & Powerups**: Chaos mode, speed rounds, reverse scoring, and more

### 🤖 AI System
- **Gemini-Powered Bots**: Context-aware, hilarious AI opponents
- **7 Unique Personas**: Sarcastic Sam, Punny Paula, Random Randy, Wholesome Wendy, Edgy Eddie, Nerdy Ned, Chaotic Carla
- **Difficulty Levels**: Easy, Medium, Hard - bots get smarter and funnier
- **Dynamic Humor**: Never repeating jokes, always contextual

### 🎨 Customization
- **Cosmetic Store**: Avatars, backgrounds, frames, badges
- **Word Packs**: Default, Internet Culture, Tech Nerds, Pop Culture, and more
- **User Profiles**: Levels, XP, achievements, stats
- **Gallery System**: Save and share your best moments

### 📱 Mobile Experience
- **Native Android**: Built with Kotlin and Jetpack Compose
- **Smooth Animations**: Confetti, transitions, celebrations
- **Offline Support**: Practice with bots offline
- **Push Notifications**: Game invites and updates

### 💰 Monetization
- **Virtual Currency**: Earn and spend coins
- **Premium Word Packs**: Unlock new content
- **Cosmetics Store**: Personalize your profile
- **Ad Integration**: Optional ad-to-unlock system

---

## 🏗 Architecture

```
┌─────────────────┐
│  Android App    │
│ Kotlin+Compose  │
└────────┬────────┘
         │
         │ REST + WebSocket
         ▼
┌─────────────────┐
│  Cloud Run      │
│  Node.js API    │
└────────┬────────┘
         │
    ┌────┴──────────┬──────────────┬────────────┐
    │               │              │            │
    ▼               ▼              ▼            ▼
┌─────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│Firestore│  │  Storage │  │  Gemini  │  │ Analytics│
│   DB    │  │  Bucket  │  │   API    │  │  Events  │
└─────────┘  └──────────┘  └──────────┘  └──────────┘
```

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed architecture diagrams and system design.

---

## 🚀 Quick Start

### Prerequisites

- **Node.js** 20+
- **Android Studio** Hedgehog or later
- **Docker** (for backend deployment)
- **Google Cloud SDK** (for Cloud Run)
- **Firebase** account
- **Gemini API** key

### 1. Clone Repository

```bash
git clone https://github.com/your-org/hornswoggled.git
cd hornswoggled
```

### 2. Backend Setup

```bash
cd backend

# Install dependencies
npm install

# Copy environment variables
cp .env.example .env

# Edit .env with your credentials
# - Firebase credentials
# - Gemini API key
# - GIPHY API key (optional)

# Run development server
npm run dev
```

Backend runs on `http://localhost:8080`

### 3. Android Setup

```bash
cd android

# Open in Android Studio
# File > Open > Select android folder

# Add google-services.json
# Download from Firebase Console
# Place in: android/app/google-services.json

# Update API_BASE_URL in build.gradle.kts
# Point to your backend URL

# Build and Run
# Click Run button or Shift+F10
```

### 4. Marketing Website (Optional)

```bash
cd marketing

# Install dependencies
npm install

# Run development server
npm run dev
```

Website runs on `http://localhost:3000`

---

## 🌐 Deployment

### Backend to Google Cloud Run

```bash
cd infra

# Make script executable
chmod +x deploy_cloud_run.sh

# Set environment variables
export GCP_PROJECT_ID=your-project-id
export GCP_REGION=us-central1

# Deploy
./deploy_cloud_run.sh
```

The script will:
1. ✅ Build Docker image
2. ✅ Push to Google Container Registry
3. ✅ Deploy to Cloud Run
4. ✅ Configure auto-scaling
5. ✅ Display service URL

### Firebase Configuration

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firebase
firebase init

# Deploy Firestore rules and indexes
firebase deploy --only firestore:rules,firestore:indexes,storage

# Seed initial data
node infra/firebase-init.js
```

### Android App Deployment

```bash
cd android

# Generate signed APK/AAB
# Build > Generate Signed Bundle/APK

# Follow Android Studio wizard
# Upload to Google Play Console
```

See [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) for detailed deployment instructions.

---

## 🛠 Development

### Backend Development

```bash
cd backend

# Run with hot reload
npm run dev

# Run tests
npm test

# Lint code
npm run lint

# Build Docker image locally
npm run docker:build
```

### Android Development

```bash
# Run on emulator
./gradlew installDebug

# Run tests
./gradlew test

# Generate APK
./gradlew assembleDebug
```

### Database Schema

See `infra/firebase-init.js` for complete Firestore schema including:
- Users
- Rooms
- Games
- Rounds
- Submissions
- Word Packs
- Bots
- Store Items
- Analytics

---

## 📁 Project Structure

```
hornswoggled/
├── android/                 # Android app (Kotlin + Compose)
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/hornswoggled/
│   │   │   │   ├── ui/              # Compose UI screens
│   │   │   │   ├── data/            # Repositories & API
│   │   │   │   ├── domain/          # Models & Use Cases
│   │   │   │   └── di/              # Hilt DI modules
│   │   │   └── res/                 # Android resources
│   │   └── build.gradle.kts
│   └── settings.gradle.kts
│
├── backend/                 # Node.js backend
│   ├── src/
│   │   ├── routes/         # API endpoints
│   │   ├── services/       # Business logic
│   │   ├── models/         # Data models
│   │   ├── middleware/     # Auth, errors, rate limiting
│   │   ├── websocket/      # WebSocket handlers
│   │   ├── bots/           # Bot AI logic
│   │   ├── utils/          # Utilities
│   │   └── config/         # Firebase & configs
│   ├── Dockerfile
│   └── package.json
│
├── infra/                   # Infrastructure & deployment
│   ├── deploy_cloud_run.sh # Cloud Run deployment script
│   ├── firestore.rules     # Firestore security rules
│   ├── firestore.indexes.json
│   ├── storage.rules       # Storage security rules
│   └── firebase-init.js    # Seed data script
│
├── prompts/                 # Gemini AI prompts
│   ├── humor_engine/
│   │   └── gemini_humor_engine.md
│   └── bots/
│       └── personas/       # Bot personality configs
│           ├── sarcastic-sam.json
│           ├── punny-paula.json
│           └── ...
│
├── marketing/               # Next.js marketing website
│   ├── pages/
│   ├── components/
│   ├── styles/
│   └── public/
│
├── docs/                    # Documentation
│   ├── API.md
│   ├── DEPLOYMENT.md
│   └── GAME_MECHANICS.md
│
├── ARCHITECTURE.md          # System architecture docs
└── README.md               # This file
```

---

## 🔧 Tech Stack

### Backend
- **Runtime**: Node.js 20 LTS
- **Framework**: Express.js
- **WebSocket**: ws library
- **Database**: Google Firestore
- **Storage**: Firebase Storage
- **AI**: Google Gemini API
- **Hosting**: Cloud Run (serverless)

### Android
- **Language**: Kotlin 1.9+
- **UI**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Networking**: Ktor Client
- **Local DB**: Room
- **Image Loading**: Coil
- **Auth**: Firebase Auth

### Marketing Website
- **Framework**: Next.js 14
- **Styling**: Tailwind CSS
- **Hosting**: Vercel (recommended)

### DevOps
- **Containers**: Docker
- **CI/CD**: GitHub Actions (recommended)
- **Monitoring**: Cloud Logging
- **Analytics**: Firebase Analytics

---

## 🎮 Game Mechanics

### Room Flow
1. **Create/Join Room** → Enter room code or create new
2. **Configure Settings** → Word packs, mutators, round count
3. **Add Bots** (optional) → Choose bot personalities
4. **Start Game** → Host starts when ready

### Round Flow
1. **Word Revealed** → Players see the word to define
2. **Submit Phase** (30-90s) → Players create submissions
3. **Judging Phase** → Rotating judge selects winner
4. **Results** → Winner announced, scores updated
5. **Next Round** → Repeat until max rounds

### Scoring
- **Round Win**: 100 points
- **Participation**: 10 points
- **Speed Bonus**: +25 for first submission
- **Streak Bonus**: +10 per consecutive win

---

## 🧪 Testing

### Backend Tests
```bash
cd backend
npm test
```

### Android Tests
```bash
cd android
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
```

---

## 📊 Analytics

The platform tracks:
- User login/signup
- Room creation/joins
- Game starts/completions
- Round performance
- Submission types
- Bot interactions
- Store purchases
- Retention metrics

Configure analytics in Firebase Console.

---

## 🔐 Security

### Implemented
- ✅ Firestore security rules
- ✅ Storage security rules
- ✅ Firebase Auth token verification
- ✅ Rate limiting (100 req/min per user)
- ✅ Input validation (Joi schemas)
- ✅ Profanity filtering (basic)
- ✅ HTTPS only in production

### TODO for Production
- [ ] Enhanced profanity filter
- [ ] Image moderation
- [ ] DDoS protection (Cloud Armor)
- [ ] Secrets management (Secret Manager)
- [ ] Advanced rate limiting per endpoint

---

## 💡 Environment Variables

### Backend (.env)

```bash
# Server
PORT=8080
NODE_ENV=production

# Firebase
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CLIENT_EMAIL=your-service-account@project.iam.gserviceaccount.com
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----..."
FIREBASE_STORAGE_BUCKET=your-project.appspot.com

# APIs
GEMINI_API_KEY=your-gemini-api-key
GIPHY_API_KEY=your-giphy-api-key

# Features
ENABLE_ANALYTICS=true
ENABLE_RATE_LIMITING=true
ENABLE_PROFANITY_FILTER=true
```

### Android (build.gradle.kts)

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://your-backend-url\"")
buildConfigField("String", "WS_BASE_URL", "\"wss://your-backend-url/ws\"")
```

---

## 🐛 Troubleshooting

### Backend won't start
- ✅ Check `.env` file exists and is populated
- ✅ Verify Firebase credentials are correct
- ✅ Check port 8080 is available
- ✅ Run `npm install` to ensure dependencies

### Android build fails
- ✅ Ensure `google-services.json` is in `android/app/`
- ✅ Update Android Studio to latest version
- ✅ Sync Gradle files
- ✅ Check JDK version is 17+

### WebSocket connection fails
- ✅ Verify backend URL in Android build config
- ✅ Check firewall/network settings
- ✅ Ensure Firebase Auth token is valid
- ✅ Check Cloud Run allows WebSocket connections

---

## 🚦 Roadmap

### v1.1 (Next Release)
- [ ] iOS app
- [ ] Voice chat integration
- [ ] Tournament mode
- [ ] Seasonal events
- [ ] More bot personalities

### v1.2
- [ ] Team battles
- [ ] Custom word packs (user-created)
- [ ] Replay system
- [ ] Spectator mode

### v2.0
- [ ] Web client
- [ ] Cross-platform play
- [ ] Advanced analytics dashboard
- [ ] Moderation tools
- [ ] Creator economy

---

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Development Workflow
1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

---

## 📄 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

---

## 👥 Team

Built with ❤️ by the Hornswoggled team

---

## 📞 Support

- **Email**: support@hornswoggled.com
- **Discord**: [Join our server](https://discord.gg/hornswoggled)
- **Issues**: [GitHub Issues](https://github.com/your-org/hornswoggled/issues)

---

## 🙏 Acknowledgments

- Google Gemini team for AI capabilities
- Firebase team for amazing backend services
- Jetpack Compose team for modern Android UI
- Our amazing community of players and testers

---

**Ready to play? Let's get Hornswoggled! 🎮**
