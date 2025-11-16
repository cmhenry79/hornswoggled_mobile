package com.hornswoggled.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String,
    val email: String? = null,
    val displayName: String,
    val avatarUrl: String,
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 0,
    val stats: UserStats = UserStats()
)

@Serializable
data class UserStats(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val roundsWon: Int = 0,
    val totalScore: Int = 0
)

@Serializable
data class Room(
    val roomId: String,
    val code: String,
    val hostId: String,
    val visibility: String, // public or private
    val status: String, // waiting, playing, finished
    val settings: RoomSettings,
    val participants: List<Participant> = emptyList(),
    val bots: List<BotParticipant> = emptyList(),
    val currentGameId: String? = null
)

@Serializable
data class RoomSettings(
    val maxPlayers: Int = 8,
    val maxRounds: Int = 5,
    val roundDuration: Int = 90,
    val wordPackIds: List<String> = listOf("default"),
    val mutators: List<String> = emptyList(),
    val enableBots: Boolean = true
)

@Serializable
data class Participant(
    val userId: String,
    val role: String, // host or player
    val isBot: Boolean = false
)

@Serializable
data class BotParticipant(
    val botId: String,
    val userId: String,
    val name: String,
    val persona: String,
    val difficulty: String,
    val avatarUrl: String
)

@Serializable
data class Game(
    val gameId: String,
    val roomId: String,
    val status: String, // active, completed
    val currentRound: Int,
    val maxRounds: Int,
    val roundDuration: Int,
    val players: List<GamePlayer> = emptyList(),
    val rounds: List<String> = emptyList()
)

@Serializable
data class GamePlayer(
    val userId: String,
    val isBot: Boolean = false,
    val score: Int = 0,
    val wins: Int = 0
)

@Serializable
data class Round(
    val roundId: String,
    val gameId: String,
    val roundNumber: Int,
    val word: String,
    val wordCategory: String,
    val judgeUserId: String,
    val status: String, // submitting, judging, completed
    val submissions: List<Submission> = emptyList(),
    val deadline: String,
    val winnerId: String? = null,
    val winningSubmissionId: String? = null
)

@Serializable
data class Submission(
    val submissionId: String,
    val userId: String,
    val type: String, // text, gif, image, doodle, emoji
    val content: String,
    val mediaUrl: String? = null,
    val votes: Int = 0
)

@Serializable
data class Bot(
    val botId: String,
    val name: String,
    val persona: String,
    val description: String,
    val humorStyle: String,
    val avatarUrl: String,
    val difficulty: String = "medium"
)

@Serializable
data class WordPack(
    val packId: String,
    val title: String,
    val description: String,
    val category: String,
    val premium: Boolean = false,
    val price: Int = 0,
    val wordCount: Int = 0
)

@Serializable
data class StoreItem(
    val itemId: String,
    val name: String,
    val category: String, // avatar, background, frame, badge
    val price: Int,
    val available: Boolean = true
)

// API Response wrappers
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null
)

@Serializable
data class ApiError(
    val message: String,
    val statusCode: Int
)

// WebSocket messages
@Serializable
data class WsMessage(
    val type: String,
    val data: String? = null
)

sealed class WsEvent {
    data class Connected(val message: String) : WsEvent()
    data class PlayerJoined(val userId: String) : WsEvent()
    data class PlayerLeft(val userId: String) : WsEvent()
    data class RoundStarted(val round: Round) : WsEvent()
    data class SubmissionReceived(val submission: Submission) : WsEvent()
    data class RoundCompleted(val winnerId: String) : WsEvent()
    data class GameEnded(val gameId: String) : WsEvent()
    data class ChatMessage(val userId: String, val message: String) : WsEvent()
    data object Disconnected : WsEvent()
    data class Error(val message: String) : WsEvent()
}
