# 🛠 Hornswoggled Complete Setup Guide

This guide will walk you through setting up the entire Hornswoggled platform from scratch.

## Prerequisites Checklist

- [ ] Google Cloud account with billing enabled
- [ ] Firebase project created
- [ ] Node.js 20+ installed
- [ ] Android Studio installed
- [ ] Docker installed
- [ ] Git installed
- [ ] Gemini API key obtained
- [ ] (Optional) GIPHY API key

---

## Step 1: Firebase Setup

### 1.1 Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add Project"
3. Enter project name: `hornswoggled`
4. Enable Google Analytics (recommended)
5. Click "Create Project"

### 1.2 Enable Required Services

In Firebase Console:

1. **Authentication**
   - Go to Authentication > Sign-in method
   - Enable "Anonymous" authentication
   - (Optional) Enable Google, Email/Password

2. **Firestore Database**
   - Go to Firestore Database
   - Click "Create database"
   - Start in production mode
   - Choose location (e.g., us-central1)

3. **Storage**
   - Go to Storage
   - Click "Get started"
   - Start in production mode

4. **Analytics**
   - Already enabled if selected during project creation

### 1.3 Get Service Account Credentials

1. Go to Project Settings (gear icon)
2. Service Accounts tab
3. Click "Generate new private key"
4. Save the JSON file securely
5. You'll need this for backend configuration

### 1.4 Register Android App

1. In Project Settings > General
2. Click "Add app" > Android
3. Package name: `com.hornswoggled`
4. App nickname: `Hornswoggled Android`
5. Download `google-services.json`
6. Place in `android/app/google-services.json`

---

## Step 2: Google Cloud Setup

### 2.1 Enable APIs

```bash
gcloud services enable \
  run.googleapis.com \
  cloudbuild.googleapis.com \
  artifactregistry.googleapis.com \
  firestore.googleapis.com \
  storage.googleapis.com
```

### 2.2 Set Default Project

```bash
gcloud config set project YOUR_PROJECT_ID
gcloud config set run/region us-central1
```

---

## Step 3: Gemini API Setup

### 3.1 Get API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Click "Create API Key"
3. Select your Google Cloud project
4. Copy the API key
5. Save securely - you'll need it for backend

### 3.2 Enable Gemini API

```bash
gcloud services enable generativelanguage.googleapis.com
```

---

## Step 4: Backend Configuration

### 4.1 Install Dependencies

```bash
cd backend
npm install
```

### 4.2 Configure Environment Variables

```bash
cp .env.example .env
```

Edit `.env`:

```bash
# Server
PORT=8080
NODE_ENV=development  # Change to 'production' when deploying
LOG_LEVEL=info

# Firebase
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
FIREBASE_DATABASE_URL=https://your-project.firebaseio.com
FIREBASE_STORAGE_BUCKET=your-project.appspot.com

# Google Cloud
GCP_PROJECT_ID=your-project-id
GCP_REGION=us-central1

# Gemini API
GEMINI_API_KEY=your-gemini-api-key-here
GEMINI_MODEL=gemini-pro

# GIPHY API (Optional)
GIPHY_API_KEY=your-giphy-api-key

# Rate Limiting
RATE_LIMIT_WINDOW_MS=60000
RATE_LIMIT_MAX_REQUESTS=100

# Game Configuration
MAX_PLAYERS_PER_ROOM=12
MAX_BOTS_PER_ROOM=6
ROUND_DURATION_SECONDS=90
MAX_ROUNDS_PER_GAME=10

# Security
CORS_ORIGIN=*
API_SECRET_KEY=generate-a-random-secret-key

# Features
ENABLE_ANALYTICS=true
ENABLE_PROFANITY_FILTER=true
ENABLE_RATE_LIMITING=true
```

### 4.3 Start Development Server

```bash
npm run dev
```

Backend should be running on `http://localhost:8080`

Test: `curl http://localhost:8080/health`

---

## Step 5: Deploy Firestore Rules and Indexes

### 5.1 Install Firebase CLI

```bash
npm install -g firebase-tools
firebase login
```

### 5.2 Initialize Firebase in Project

```bash
cd infra
firebase init

# Select:
# - Firestore
# - Storage
#
# Use existing files:
# - firestore.rules
# - firestore.indexes.json
# - storage.rules
```

### 5.3 Deploy Rules

```bash
firebase deploy --only firestore:rules,firestore:indexes,storage
```

---

## Step 6: Seed Initial Data

### 6.1 Prepare Service Account

Export Firebase service account JSON:

```bash
export FIREBASE_SERVICE_ACCOUNT='{"type":"service_account","project_id":"..."}'
export FIREBASE_STORAGE_BUCKET="your-project.appspot.com"
```

### 6.2 Run Seed Script

```bash
cd infra
node firebase-init.js
```

This creates:
- ✅ 7 bot personalities
- ✅ 6 word packs (2 free, 4 premium)
- ✅ 50+ default words
- ✅ Store items

---

## Step 7: Deploy Backend to Cloud Run

### 7.1 Build and Deploy

```bash
cd infra

# Set environment variables
export GCP_PROJECT_ID=your-project-id
export GCP_REGION=us-central1

# Deploy
./deploy_cloud_run.sh
```

### 7.2 Set Environment Variables

After deployment, update Cloud Run service with environment variables:

```bash
gcloud run services update hornswoggled-backend \
  --update-env-vars FIREBASE_PROJECT_ID=your-project-id \
  --update-env-vars GEMINI_API_KEY=your-gemini-key \
  --update-env-vars NODE_ENV=production \
  --region us-central1
```

Or use Firebase service account:

```bash
gcloud run services update hornswoggled-backend \
  --update-secrets FIREBASE_SERVICE_ACCOUNT=firebase-credentials:latest \
  --region us-central1
```

### 7.3 Verify Deployment

Get service URL:

```bash
gcloud run services describe hornswoggled-backend \
  --region us-central1 \
  --format 'value(status.url)'
```

Test:

```bash
curl https://your-service-url.run.app/health
```

---

## Step 8: Android App Configuration

### 8.1 Add google-services.json

1. Download from Firebase Console
2. Place in `android/app/google-services.json`

### 8.2 Update Backend URL

Edit `android/app/build.gradle.kts`:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://your-cloud-run-url.run.app\"")
buildConfigField("String", "WS_BASE_URL", "\"wss://your-cloud-run-url.run.app/ws\"")
```

### 8.3 Build in Android Studio

1. Open `android` folder in Android Studio
2. Wait for Gradle sync
3. Select device/emulator
4. Click Run (Shift+F10)

### 8.4 Generate Release Build

1. Build > Generate Signed Bundle/APK
2. Select "Android App Bundle"
3. Create or select keystore
4. Build release

---

## Step 9: Marketing Website Deployment

### 9.1 Local Development

```bash
cd marketing
npm install
npm run dev
```

Visit `http://localhost:3000`

### 9.2 Deploy to Vercel

```bash
# Install Vercel CLI
npm install -g vercel

# Deploy
vercel

# Follow prompts
```

Or connect GitHub repo to Vercel for automatic deployments.

---

## Step 10: Testing the Complete System

### 10.1 Backend Health Check

```bash
curl https://your-backend-url/health
```

Expected response:
```json
{
  "status": "healthy",
  "timestamp": "2025-11-16T...",
  "uptime": 123.45,
  "environment": "production"
}
```

### 10.2 Test Room Creation

```bash
# Get Firebase auth token first (from Android app logs)
curl -X POST https://your-backend-url/api/rooms/create \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "visibility": "public",
    "maxPlayers": 8,
    "maxRounds": 5
  }'
```

### 10.3 Test Bot Endpoints

```bash
curl https://your-backend-url/api/bots
```

Expected: List of 7 bots

### 10.4 Android App Flow

1. Launch app
2. Login (anonymous)
3. Create room
4. Add bot
5. Start game
6. Submit definition
7. Verify real-time updates

---

## Troubleshooting

### Backend Issues

**Error: Firebase credentials invalid**
- Solution: Check `.env` file, ensure private key has proper `\n` escaping

**Error: Port 8080 already in use**
- Solution: `lsof -ti:8080 | xargs kill -9`

**Error: Cannot connect to Firestore**
- Solution: Verify Firebase project ID and credentials

### Android Issues

**Error: google-services.json not found**
- Solution: Download from Firebase Console, place in `android/app/`

**Build error: SDK version mismatch**
- Solution: Update to Android Studio Hedgehog, sync Gradle

**Error: Cannot connect to backend**
- Solution: For emulator, use `10.0.2.2:8080` instead of `localhost:8080`

### Cloud Run Issues

**Error: Permission denied**
- Solution: `gcloud auth login && gcloud config set project PROJECT_ID`

**Error: Service timeout**
- Solution: Increase timeout in Cloud Run settings (max 60 minutes)

**WebSocket fails on Cloud Run**
- Solution: Cloud Run supports WebSocket, but ensure connection upgrade headers are set

---

## Security Hardening (Production)

### 1. Environment Variables

Move secrets to Google Secret Manager:

```bash
# Create secret
echo -n "your-api-key" | gcloud secrets create gemini-api-key --data-file=-

# Grant access to Cloud Run
gcloud secrets add-iam-policy-binding gemini-api-key \
  --member="serviceAccount:PROJECT_NUMBER-compute@developer.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"

# Update Cloud Run to use secret
gcloud run services update hornswoggled-backend \
  --update-secrets GEMINI_API_KEY=gemini-api-key:latest \
  --region us-central1
```

### 2. Enable Cloud Armor (DDoS Protection)

```bash
gcloud compute security-policies create hornswoggled-policy \
  --description "Hornswoggled security policy"

gcloud compute security-policies rules create 1000 \
  --security-policy hornswoggled-policy \
  --expression "origin.region_code == 'CN'" \
  --action "deny-403"
```

### 3. Set up Firebase App Check

1. Go to Firebase Console > App Check
2. Enable for Android app
3. Register with Play Integrity API
4. Enforce in Firestore rules

---

## Cost Estimation (Monthly)

### Free Tier Usage
- **Cloud Run**: 2M requests, always free
- **Firestore**: 50K reads, 20K writes, 20K deletes per day
- **Storage**: 5GB free
- **Gemini API**: 15 requests/minute, 1500/day free

### Expected Costs (1000 active users)
- Cloud Run: ~$5-10/month
- Firestore: ~$10-20/month
- Storage: ~$1-5/month
- Gemini API: ~$20-50/month (depends on bot usage)
- **Total**: ~$40-85/month

### Scaling (10,000 users)
- Cloud Run: ~$50-100/month
- Firestore: ~$100-200/month
- Storage: ~$10-20/month
- Gemini API: ~$200-500/month
- **Total**: ~$360-820/month

---

## Next Steps

✅ Backend deployed and running
✅ Firebase configured and seeded
✅ Android app connected
✅ Marketing site live

Now you can:
1. 🧪 Test the complete game flow
2. 📱 Submit to Google Play
3. 🌐 Share marketing site
4. 📊 Monitor analytics
5. 🚀 Start acquiring users!

---

## Additional Resources

- [Hornswoggled Documentation](../docs/)
- [API Reference](../docs/API.md)
- [Game Mechanics](../docs/GAME_MECHANICS.md)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Cloud Run Documentation](https://cloud.google.com/run/docs)
- [Gemini API Documentation](https://ai.google.dev/docs)

---

**Need help? Join our [Discord](https://discord.gg/hornswoggled) or email support@hornswoggled.com**
