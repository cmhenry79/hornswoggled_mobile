package com.hornswoggled.data.remote

import com.hornswoggled.BuildConfig
import com.hornswoggled.domain.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor() {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }

        install(WebSockets) {
            pingInterval = 15_000
        }
    }

    private val baseUrl = BuildConfig.API_BASE_URL

    // Auth token management
    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        authToken = token
    }

    private fun HttpRequestBuilder.authenticate() {
        authToken?.let {
            headers {
                append(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    // Auth endpoints
    suspend fun login(displayName: String, avatarUrl: String?): Result<User> = runCatching {
        client.post("$baseUrl/api/auth/login") {
            authenticate()
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "displayName" to displayName,
                "avatarUrl" to avatarUrl
            ))
        }.body<ApiResponse<Map<String, User>>>().data?.get("user")!!
    }

    suspend fun getCurrentUser(): Result<User> = runCatching {
        client.get("$baseUrl/api/auth/me") {
            authenticate()
        }.body<ApiResponse<Map<String, User>>>().data?.get("user")!!
    }

    // Room endpoints
    suspend fun createRoom(settings: RoomSettings): Result<Room> = runCatching {
        client.post("$baseUrl/api/rooms/create") {
            authenticate()
            contentType(ContentType.Application.Json)
            setBody(settings)
        }.body<ApiResponse<Map<String, Room>>>().data?.get("room")!!
    }

    suspend fun joinRoom(code: String): Result<Room> = runCatching {
        client.post("$baseUrl/api/rooms/join/$code") {
            authenticate()
        }.body<ApiResponse<Map<String, Room>>>().data?.get("room")!!
    }

    suspend fun getRoom(roomId: String): Result<Room> = runCatching {
        client.get("$baseUrl/api/rooms/$roomId") {
            authenticate()
        }.body<ApiResponse<Map<String, Room>>>().data?.get("room")!!
    }

    suspend fun leaveRoom(roomId: String): Result<Unit> = runCatching {
        client.post("$baseUrl/api/rooms/$roomId/leave") {
            authenticate()
        }
    }

    suspend fun addBot(roomId: String, botId: String, difficulty: String): Result<Room> = runCatching {
        client.post("$baseUrl/api/rooms/$roomId/bots/add") {
            authenticate()
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "botId" to botId,
                "difficulty" to difficulty
            ))
        }.body<ApiResponse<Map<String, Room>>>().data?.get("room")!!
    }

    suspend fun startGame(roomId: String): Result<Game> = runCatching {
        client.post("$baseUrl/api/rooms/$roomId/start") {
            authenticate()
        }.body<ApiResponse<Map<String, Game>>>().data?.get("game")!!
    }

    suspend fun getPublicRooms(limit: Int = 20, offset: Int = 0): Result<List<Room>> = runCatching {
        client.get("$baseUrl/api/rooms") {
            parameter("limit", limit)
            parameter("offset", offset)
        }.body<ApiResponse<Map<String, List<Room>>>>().data?.get("rooms") ?: emptyList()
    }

    // Game endpoints
    suspend fun getGame(gameId: String): Result<Game> = runCatching {
        client.get("$baseUrl/api/game/$gameId") {
            authenticate()
        }.body<ApiResponse<Map<String, Game>>>().data?.get("game")!!
    }

    suspend fun startRound(gameId: String): Result<Round> = runCatching {
        client.post("$baseUrl/api/game/$gameId/round/start") {
            authenticate()
        }.body<ApiResponse<Map<String, Round>>>().data?.get("round")!!
    }

    suspend fun submitAnswer(
        gameId: String,
        roundId: String,
        content: String,
        type: String = "text"
    ): Result<Submission> = runCatching {
        client.post("$baseUrl/api/game/$gameId/submit") {
            authenticate()
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "roundId" to roundId,
                "content" to content,
                "type" to type
            ))
        }.body<ApiResponse<Map<String, Submission>>>().data?.get("submission")!!
    }

    suspend fun voteForSubmission(
        gameId: String,
        roundId: String,
        submissionId: String
    ): Result<Unit> = runCatching {
        client.post("$baseUrl/api/game/$gameId/vote") {
            authenticate()
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "roundId" to roundId,
                "submissionId" to submissionId
            ))
        }
    }

    // Bot endpoints
    suspend fun getAvailableBots(): Result<List<Bot>> = runCatching {
        client.get("$baseUrl/api/bots").body<ApiResponse<Map<String, List<Bot>>>>().data?.get("bots") ?: emptyList()
    }

    // Word pack endpoints
    suspend fun getWordPacks(): Result<List<WordPack>> = runCatching {
        client.get("$baseUrl/api/word-packs").body<ApiResponse<Map<String, List<WordPack>>>>().data?.get("packs") ?: emptyList()
    }

    // Store endpoints
    suspend fun getStoreItems(category: String? = null): Result<List<StoreItem>> = runCatching {
        client.get("$baseUrl/api/store/items") {
            category?.let { parameter("category", it) }
        }.body<ApiResponse<Map<String, List<StoreItem>>>>().data?.get("items") ?: emptyList()
    }

    suspend fun purchaseItem(itemId: String, paymentMethod: String = "coins"): Result<Unit> = runCatching {
        client.post("$baseUrl/api/store/purchase") {
            authenticate()
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "itemId" to itemId,
                "paymentMethod" to paymentMethod
            ))
        }
    }

    // Analytics
    suspend fun trackEvent(eventName: String, properties: Map<String, Any> = emptyMap()): Result<Unit> = runCatching {
        client.post("$baseUrl/api/analytics/event") {
            authenticate()
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "eventName" to eventName,
                "properties" to properties
            ))
        }
    }
}
