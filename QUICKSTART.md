# ⚡ Hornswoggled Quick Start (5 Minutes)

Get Hornswoggled running locally in 5 minutes!

## Prerequisites

- Node.js 20+
- Docker
- Android Studio
- Firebase account
- Gemini API key

---

## 1. Clone & Setup (1 min)

```bash
git clone https://github.com/your-org/hornswoggled.git
cd hornswoggled
```

---

## 2. Backend Setup (2 min)

```bash
cd backend

# Install dependencies
npm install

# Create .env file
cp .env.example .env

# Edit .env with your credentials:
# - FIREBASE_PROJECT_ID
# - FIREBASE_CLIENT_EMAIL
# - FIREBASE_PRIVATE_KEY
# - GEMINI_API_KEY

# Start server
npm run dev
```

Backend running at `http://localhost:8080` ✅

Test: `curl http://localhost:8080/health`

---

## 3. Android Setup (2 min)

```bash
# Open Android project
cd android
# Open this folder in Android Studio

# Add google-services.json
# Download from Firebase Console
# Place in: android/app/google-services.json

# Click Run in Android Studio
```

App launches on emulator/device ✅

---

## 4. Quick Test

1. **Login**: App opens → Enter display name → Login
2. **Create Room**: Click "+" → Create Room
3. **Add Bot**: Add "Sarcastic Sam"
4. **Start Game**: Click "Start Game"
5. **Play Round**: Define word → Submit → Judge votes
6. **Win**: See results and celebration! 🎉

---

## That's It!

You're now running the complete Hornswoggled platform locally!

### Next Steps

- 📖 Read [README.md](README.md) for full documentation
- 🚀 Deploy backend: `cd infra && ./deploy_cloud_run.sh`
- 📱 Build release APK for testing
- 🌐 Set up marketing site: `cd marketing && npm run dev`

### Need Help?

- [Setup Guide](docs/SETUP_GUIDE.md) - Detailed setup instructions
- [Architecture](ARCHITECTURE.md) - System design docs
- [Troubleshooting](README.md#troubleshooting) - Common issues

---

**Happy Hornswoggling! 🎮**
