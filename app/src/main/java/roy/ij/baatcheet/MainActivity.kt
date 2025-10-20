package roy.ij.baatcheet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import roy.ij.baatcheet.features.auth.AuthViewModel
import roy.ij.baatcheet.features.auth.LockScreen
import roy.ij.baatcheet.features.chat.ChatListScreen
import roy.ij.baatcheet.features.chat.ChatViewModel
import roy.ij.baatcheet.features.chat.ConversationScreen
import roy.ij.baatcheet.features.chat.RoomScreen
import roy.ij.baatcheet.features.chat.RoomViewModel
import roy.ij.baatcheet.features.dm.MyProfileQrScreen
import roy.ij.baatcheet.features.dm.MyProfileQrViewModel
import roy.ij.baatcheet.features.dm.ScanOrTypeScreen
import roy.ij.baatcheet.navigation.NavRoutes
import roy.ij.baatcheet.security.SecureStore
import roy.ij.baatcheet.ui.screens.auth.AuthScreen
import roy.ij.baatcheet.ui.theme.BaatCheetTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BaatCheetTheme {
                val navController = rememberNavController()

                val ctx = this
                val storedUsername = remember { SecureStore.getUsername(ctx) }
                val hasBlob = remember { !SecureStore.getTokenBlob(ctx).isNullOrBlank() }
                val bioEnabled = remember { SecureStore.isBiometricEnabled(ctx) }

                val startDestination = remember(storedUsername, hasBlob, bioEnabled) {
                    when {
                        storedUsername.isNullOrBlank() -> NavRoutes.Auth.route
                        bioEnabled && hasBlob -> NavRoutes.Lock.route
                        else -> NavRoutes.Auth.route
                    }
                }

                // Single shared VM for auth flow
                val authViewModel: AuthViewModel = viewModel()
                val authState by authViewModel.state.collectAsState()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    NavHost(navController, startDestination) {
                        composable(NavRoutes.Auth.route) {
                            // Pass the same VM to keep state across recompositions
                            AuthScreen(viewModel = authViewModel, navController = navController)
                        }
                        composable(NavRoutes.ChatList.route) {
                            val token = authState.token ?: return@composable
                            ChatListScreen(navController = navController, token = token)
                        }
                        composable(NavRoutes.Room.route) {
                            // Provide token to RoomViewModel
                            val roomVm: RoomViewModel = viewModel()
                            LaunchedEffect(authState.token) {
                                authState.token?.let { roomVm.setToken(it) }
                            }
                            RoomScreen(navController = navController, viewModel = roomVm)
                        }
                        composable(
                            NavRoutes.Conversation.route,
                            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable

                            // build ChatViewModel with token + roomId
                            val token = authState.token ?: return@composable
                            val chatVm = remember(roomId, token) {
                                ChatViewModel(token = token, roomId = roomId)
                            }
                            ConversationScreen(viewModel = chatVm)
                        }
                        composable(NavRoutes.MyQr.route) {
                            val token = authState.token ?: return@composable
                            val vm: MyProfileQrViewModel = viewModel()
                            val user by vm.user.collectAsState()

                            LaunchedEffect(Unit) { vm.load(token) }

                            user?.let {
                                MyProfileQrScreen(username = it.username, userId = it.id)
                            } ?: run {
                                LinearProgressIndicator(Modifier.fillMaxWidth().padding(16.dp))
                            }
                        }

                        composable(NavRoutes.ScanOrType.route) {
                            val token = authState.token ?: return@composable
                            ScanOrTypeScreen(token = token) { roomId ->
                                // on success -> go to DM conversation
                                navController.navigate(NavRoutes.Conversation.create(roomId)) {
                                    launchSingleTop = true
                                }
                            }
                        }
                        composable(NavRoutes.Lock.route) {
                            LockScreen(navController = navController, viewModel = authViewModel)
                        }
                    }
                }
            }
        }
    }
}

