# Hornswoggled - Complete System Architecture

## 🎯 System Overview

Hornswoggled is a real-time multiplayer party game platform where players compete to create the funniest definitions, memes, and responses to prompts. AI-powered bots join games to enhance gameplay and provide hilarious competition.

## 📊 Architecture Diagrams

### 1. High-Level System Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        A[Android App<br/>Kotlin + Compose]
        M[Marketing Website<br/>Next.js]
    end

    subgraph "Google Cloud Platform"
        B[Cloud Run<br/>Node.js Backend]
        C[Firebase Auth]
        D[Firestore Database]
        E[Firebase Storage]
        F[Cloud Functions]
        G[Gemini API<br/>Bot Intelligence]
    end

    subgraph "External Services"
        H[GIPHY API]
        I[Analytics]
        J[Payment Gateway]
    end

    A -->|REST + WebSocket| B
    A -->|Authentication| C
    A -->|Real-time Data| D
    A -->|Media Upload| E
    B -->|User Management| C
    B -->|Game State| D
    B -->|Media Storage| E
    B -->|Bot Responses| G
    B -->|GIF Search| H
    B -->|Events| I
    A -->|Purchases| J
    F -->|Triggers| D
    F -->|Moderation| E
```

### 2. Data Flow Architecture

```mermaid
sequenceDiagram
    participant Player
    participant Android
    participant CloudRun
    participant Firestore
    participant Gemini
    participant Bot

    Player->>Android: Create Game Room
    Android->>CloudRun: POST /rooms/create
    CloudRun->>Firestore: Create room document
    CloudRun->>Android: Room ID + WebSocket URL
    Android->>CloudRun: WebSocket Connect

    Player->>Android: Add Bot Player
    Android->>CloudRun: POST /rooms/{id}/bots/add
    CloudRun->>Gemini: Initialize bot persona
    Gemini->>CloudRun: Bot personality config
    CloudRun->>Firestore: Add bot to room

    Player->>Android: Start Round
    Android->>CloudRun: POST /game/round/start
    CloudRun->>Firestore: Update game state
    CloudRun-->>Android: WS: round_started event
    CloudRun->>Gemini: Generate bot submission
    Gemini->>CloudRun: Funny response
    CloudRun->>Firestore: Store bot submission
    CloudRun-->>Android: WS: submission_received

    Player->>Android: Submit Answer
    Android->>CloudRun: POST /game/submit
    CloudRun->>Firestore: Store submission
    CloudRun-->>Android: WS: all_submissions_ready

    Player->>Android: Vote for Winner
    Android->>CloudRun: POST /game/vote
    CloudRun->>Firestore: Calculate scores
    CloudRun-->>Android: WS: round_complete
```

### 3. Real-time Game Room System

```mermaid
stateDiagram-v2
    [*] --> Lobby: Create Room
    Lobby --> Configuring: Host Configure
    Configuring --> Lobby: Cancel
    Configuring --> WaitingForPlayers: Settings Done
    WaitingForPlayers --> InRound: Start Game
    InRound --> Submitting: Round Start
    Submitting --> Judging: All Submitted
    Judging --> Results: Judge Selects
    Results --> InRound: Next Round
    Results --> GameOver: Final Round
    GameOver --> [*]: Room Closed

    note right of Submitting
        Players & Bots submit
        Text, GIF, Image, Doodle
        30-90 second timer
    end note

    note right of Judging
        Random judge each round
        Reviews submissions
        Picks winner
    end note
```

### 4. Android Application Architecture

```mermaid
graph TB
    subgraph "Presentation Layer"
        UI[Jetpack Compose UI]
        VM[ViewModels]
    end

    subgraph "Domain Layer"
        UC[Use Cases]
        REPO[Repositories]
    end

    subgraph "Data Layer"
        API[API Service<br/>Retrofit/Ktor]
        WS[WebSocket Manager]
        LOCAL[Local Storage<br/>Room DB]
        CACHE[Image Cache<br/>Coil]
        AUTH[Firebase Auth SDK]
    end

    UI --> VM
    VM --> UC
    UC --> REPO
    REPO --> API
    REPO --> WS
    REPO --> LOCAL
    REPO --> CACHE
    REPO --> AUTH
```

### 5. Backend Service Architecture

```mermaid
graph TB
    subgraph "API Layer"
        REST[REST Controllers]
        WS[WebSocket Handlers]
    end

    subgraph "Business Logic"
        GAME[Game Engine]
        ROOM[Room Manager]
        BOT[Bot Controller]
        SCORE[Scoring System]
        MUTATION[Mutator Engine]
    end

    subgraph "External Integration"
        GEMINI[Gemini Client]
        GIPHY[GIPHY Client]
        STORAGE[Firebase Storage]
        PAYMENT[Payment Processor]
    end

    subgraph "Data Access"
        FIRESTORE[Firestore Client]
        CACHE[Redis Cache]
    end

    REST --> ROOM
    REST --> GAME
    WS --> GAME
    GAME --> SCORE
    GAME --> MUTATION
    GAME --> BOT
    BOT --> GEMINI
    ROOM --> FIRESTORE
    GAME --> FIRESTORE
    SCORE --> FIRESTORE
    GAME --> GIPHY
    GAME --> STORAGE
```

### 6. Monetization Flow

```mermaid
graph LR
    A[Player] --> B{Action}
    B -->|Free| C[Basic Word Packs]
    B -->|Free| D[Limited Cosmetics]
    B -->|Purchase| E[Premium Word Packs]
    B -->|Purchase| F[Cosmetic Store]
    B -->|Purchase| G[Bot Personalities]
    B -->|Ad Watch| H[Unlock Temporary Item]

    E --> I[Payment Gateway]
    F --> I
    G --> I
    H --> J[Ad Network]

    I --> K[Firestore: purchases]
    J --> L[Analytics]
    K --> M[Grant Items]
    M --> N[Update User Profile]
```

### 7. Database Schema Overview

```mermaid
erDiagram
    USERS ||--o{ ROOMS : creates
    USERS ||--o{ SUBMISSIONS : makes
    USERS ||--o{ PURCHASES : makes
    USERS ||--o{ GALLERY_ITEMS : saves

    ROOMS ||--|{ GAMES : contains
    ROOMS ||--o{ PARTICIPANTS : has

    GAMES ||--|{ ROUNDS : contains
    ROUNDS ||--|{ SUBMISSIONS : receives

    SUBMISSIONS ||--o{ VOTES : receives

    WORD_PACKS ||--o{ WORDS : contains
    GAMES ||--o{ WORD_PACKS : uses

    BOTS ||--o{ PARTICIPANTS : joins_as
    BOTS ||--o{ SUBMISSIONS : creates

    USERS {
        string userId PK
        string displayName
        string avatarUrl
        int level
        int xp
        object cosmetics
        timestamp createdAt
    }

    ROOMS {
        string roomId PK
        string hostId FK
        string code
        string visibility
        object settings
        string status
        timestamp createdAt
    }

    GAMES {
        string gameId PK
        string roomId FK
        int currentRound
        int maxRounds
        array wordPackIds
        array mutators
        timestamp startedAt
    }

    ROUNDS {
        string roundId PK
        string gameId FK
        string word
        string judgeUserId FK
        int roundNumber
        string status
        timestamp deadline
    }

    SUBMISSIONS {
        string submissionId PK
        string roundId FK
        string userId FK
        string type
        string content
        string mediaUrl
        int votes
        timestamp createdAt
    }

    WORD_PACKS {
        string packId PK
        string title
        string category
        boolean premium
        int price
    }

    BOTS {
        string botId PK
        string name
        string persona
        string humorStyle
        string difficulty
    }
```

### 8. Deployment Pipeline

```mermaid
graph LR
    A[Code Push] --> B[GitHub Actions]
    B --> C{Tests Pass?}
    C -->|No| D[Fail Build]
    C -->|Yes| E[Build Docker Image]
    E --> F[Push to Artifact Registry]
    F --> G[Deploy to Cloud Run]
    G --> H[Health Check]
    H -->|Pass| I[Update DNS]
    H -->|Fail| J[Rollback]
    I --> K[Deployment Complete]

    L[Android Build] --> M[Gradle Build]
    M --> N[Run Tests]
    N --> O[Generate AAB]
    O --> P[Sign Bundle]
    P --> Q[Upload to Play Console]
```

## 🔧 Technology Stack Details

### Android Client
- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Networking**: Ktor Client
- **WebSocket**: OkHttp WebSocket
- **Image Loading**: Coil
- **Local Storage**: Room Database
- **Auth**: Firebase Auth SDK
- **Analytics**: Firebase Analytics

### Backend Service
- **Runtime**: Node.js 20 LTS
- **Framework**: Express.js
- **WebSocket**: ws library
- **Database**: Firestore
- **Storage**: Firebase Storage
- **AI**: Google Gemini API (free tier)
- **Container**: Docker
- **Hosting**: Cloud Run (free tier)

### Marketing Website
- **Framework**: Next.js 14
- **Styling**: Tailwind CSS
- **Animations**: Framer Motion
- **Deployment**: Vercel/Cloud Run
- **Analytics**: Google Analytics

### Infrastructure
- **Cloud Provider**: Google Cloud Platform
- **CDN**: Firebase Hosting
- **Functions**: Cloud Functions
- **Monitoring**: Cloud Logging
- **CI/CD**: GitHub Actions

## 🎮 Core Game Mechanics

### Submission Types
1. **Text**: Classic text-based answers
2. **GIF**: GIPHY integration for animated responses
3. **Image**: Photo uploads from device or camera
4. **Doodle**: Canvas drawing tool
5. **Emoji Mashup**: Combine emojis to create meaning

### Mutators (Game Modifiers)
- **Chaos Mode**: Random constraints each round
- **Speed Round**: 15-second submission timer
- **Bot Invasion**: Extra bots join mid-game
- **Reverse**: Worst answer wins
- **Combo**: Require multiple submission types

### Powerups
- **Extra Time**: +30 seconds
- **Peek**: See one other submission
- **Swap**: Change your submission
- **Boost**: Double points this round
- **Shield**: Immunity from negative effects

### Scoring System
- **Win Round**: 100 points
- **Second Place**: 50 points (voted submissions)
- **Participation**: 10 points
- **Streak Bonus**: +10 per consecutive win
- **Speed Bonus**: +25 for first submission
- **Comeback**: 2x points if in last place

## 🤖 Bot Intelligence System

### Persona Types
1. **Sarcastic Sam**: Dry, witty, deadpan
2. **Punny Paula**: Loves wordplay
3. **Random Randy**: Absurdist humor
4. **Wholesome Wendy**: Positive, silly
5. **Edgy Eddie**: Dark humor (filtered)
6. **Nerdy Ned**: References pop culture
7. **Chaotic Carla**: Unpredictable

### Bot Difficulty Levels
- **Easy**: Obvious jokes, slower responses
- **Medium**: Good timing, varied humor
- **Hard**: Clever, context-aware, competitive

### Gemini Prompt Engineering
```
System: You are {persona_name}, a bot player in Hornswoggled.
Context: Word to define: "{word}"
Other players submitted: {previous_submissions_count} responses
Game mode: {mutators}
Your humor style: {persona_style}

Generate a {submission_type} that is:
- Hilarious and {humor_adjective}
- Max {max_length} characters
- Appropriate for {rating}
- Different from previous submissions
- Matches {persona_name}'s personality

Previous submissions to avoid copying:
{recent_submissions}

Respond with ONLY the submission content, no explanation.
```

## 🔒 Security & Anti-Cheat

### Rate Limiting
- 10 requests/second per user
- 100 room creates per day per user
- 1000 submissions per day per user

### Anti-Cheat Measures
- Submission timestamp validation
- Duplicate content detection
- Rate limit on voting
- Room code complexity
- Report system with auto-moderation

### Data Privacy
- GDPR compliant
- Data encryption at rest and in transit
- User data deletion on request
- Anonymous gameplay option

## 📈 Analytics & Telemetry

### Key Metrics
- Daily/Monthly Active Users
- Average session duration
- Games created/completed
- Submission type distribution
- Bot vs human win rates
- Revenue per user
- Retention curves
- Crash rates

### Events Tracked
- `user_login`
- `room_created`
- `game_started`
- `round_completed`
- `submission_made`
- `vote_cast`
- `cosmetic_purchased`
- `word_pack_unlocked`
- `bot_added`
- `share_created`

## 🚀 Scalability Considerations

### Current Architecture (Free Tier)
- Cloud Run: 2M requests/month
- Firestore: 50K reads, 20K writes per day
- Storage: 5GB
- Gemini API: 15 requests/minute

### Growth Strategy
1. **Phase 1** (0-1K users): Free tier
2. **Phase 2** (1K-10K users): Add caching, optimize queries
3. **Phase 3** (10K-100K users): Horizontal scaling, CDN, Redis
4. **Phase 4** (100K+ users): Multi-region, load balancing, premium tier

## 📱 Offline Support

- Cache last played game state
- Queue submissions when offline
- Sync when reconnected
- Offline bot practice mode
- Local gallery access

## 🎨 Asset Requirements

### Images
- App icon (adaptive, 512x512)
- Splash screen
- Avatar options (50+)
- Cosmetic items (100+)
- Word pack covers (20+)
- Celebration animations
- Loading states

### Sounds
- Button clicks
- Submission sent
- Timer warning
- Vote received
- Round win
- Level up
- Purchase confirmation
- Notification

### Animations
- Confetti on win
- Slide in/out transitions
- Card flips for reveals
- Particle effects
- Loading spinners
- Progress bars

---

**Generated**: 2025-11-16
**Version**: 1.0.0
**Platform**: Hornswoggled Complete Production System
