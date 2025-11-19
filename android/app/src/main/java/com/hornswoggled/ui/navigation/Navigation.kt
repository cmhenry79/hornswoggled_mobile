package com.hornswoggled.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hornswoggled.ui.splash.SplashScreen
import com.hornswoggled.ui.auth.LoginScreen
import com.hornswoggled.ui.lobby.LobbyScreen
import com.hornswoggled.ui.room.RoomScreen
import com.hornswoggled.ui.game.GameScreen
import com.hornswoggled.ui.profile.ProfileScreen
import com.hornswoggled.ui.store.StoreScreen
import com.hornswoggled.ui.gallery.GalleryScreen
import com.hornswoggled.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Lobby : Screen("lobby")
    object Room : Screen("room/{roomId}") {
        fun createRoute(roomId: String) = "room/$roomId"
    }
    object Game : Screen("game/{gameId}") {
        fun createRoute(gameId: String) = "game/$gameId"
    }
    object Profile : Screen("profile")
    object Store : Screen("store")
    object WordPacks : Screen("word-packs")
    object Gallery : Screen("gallery")
    object Settings : Screen("settings")
}

@Composable
fun HornswoggledNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToLobby = { navController.navigate(Screen.Lobby.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Lobby.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Lobby.route) {
            LobbyScreen(
                onNavigateToRoom = { roomId ->
                    navController.navigate(Screen.Room.createRoute(roomId))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToStore = {
                    navController.navigate(Screen.Store.route)
                }
            )
        }

        composable(
            route = Screen.Room.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            RoomScreen(
                roomId = roomId,
                onNavigateToGame = { gameId ->
                    navController.navigate(Screen.Game.createRoute(gameId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: return@composable
            GameScreen(
                gameId = gameId,
                onNavigateBack = {
                    navController.popBackStack(Screen.Lobby.route, inclusive = false)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGallery = { navController.navigate(Screen.Gallery.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Store.route) {
            StoreScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Gallery.route) {
            GalleryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
