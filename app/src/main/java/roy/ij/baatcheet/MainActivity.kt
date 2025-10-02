package roy.ij.baatcheet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import roy.ij.baatcheet.features.auth.AuthScreen
import roy.ij.baatcheet.features.auth.AuthViewModel
import roy.ij.baatcheet.navigation.NavRoutes
import roy.ij.baatcheet.ui.theme.BaatCheetTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import roy.ij.baatcheet.features.chat.RoomScreen
import roy.ij.baatcheet.features.chat.RoomViewModel
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import roy.ij.baatcheet.ui.theme.BaatCheetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BaatCheetTheme {
                val navController = rememberNavController()

                // Single shared VM for auth flow
                val authViewModel: AuthViewModel = viewModel()
                val authState by authViewModel.state.collectAsState()

                // When we have a token, go to ChatList and remove Auth from back stack
                LaunchedEffect(authState.token) {
                    if (authState.token != null) {
                        navController.navigate(NavRoutes.ChatList.route) {
                            popUpTo(NavRoutes.Auth.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.Auth.route,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable(NavRoutes.Auth.route) {
                            // Pass the same VM to keep state across recompositions
                            AuthScreen(viewModel = authViewModel)
                        }
                        composable(NavRoutes.ChatList.route) {
                            // Placeholder — replace with your real chat list screen
                            ChatListScreen(
                                onGoToRooms = {
                                    navController.navigate(NavRoutes.Room.route)
                                }
                            )
                        }
                        composable(NavRoutes.Room.route) {
                            // Provide token to RoomViewModel
                            val roomVm: RoomViewModel = viewModel()
                            LaunchedEffect(authState.token) {
                                authState.token?.let { roomVm.setToken(it) }
                            }
                            RoomScreen(viewModel = roomVm)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListScreen(onGoToRooms: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Welcome to ChatList 👋")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onGoToRooms) {
            Text("Go to Rooms")
        }
    }
}


//=/** Minimal placeholder screen so the app runs */
//@androidx.compose.runtime.Composable
//private fun ChatListScreen() {
//    Text("Welcome to ChatList 👋")
//}

//class MainActivity : ComponentActivity() {
//    private val viewModel: ChatViewModel by viewModels()
//
//    private val googleAuthUiClient by lazy {
//        GoogleAuthUiClient(
//            context = applicationContext,
//            viewModel = viewModel,
//            oneTapClient = Identity.getSignInClient(applicationContext)
//        )
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            BaatCheetTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Box(
//                        modifier = Modifier
//                            .padding(innerPadding)
//                            .fillMaxSize()
//                    ) {
//                        val state by viewModel.state.collectAsState()
//                        val navController = rememberNavController()
//                        NavHost(navController = navController, startDestination = StartScreen){
//                            composable<StartScreen> {
//
//                                LaunchedEffect(key1 = Unit){
//                                    val userData = googleAuthUiClient.getSignedInUser()
//                                    if (userData != null){
//                                        navController.navigate(ChatsScreen)
//                                    }else{
//                                        navController.navigate(SignInScreen)
//                                    }
//                                }
//                            }
//
//                            composable<SignInScreen> {
//                                val launcher =
//                                    rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
//                                        onResult = { result ->
//                                            if (result.resultCode == RESULT_OK) {
//                                                lifecycleScope.launch {
//                                                    val signInResult = googleAuthUiClient.signInWithClient(
//                                                        intent = result.data ?: return@launch
//                                                    )
//                                                    viewModel.onSignInResult(signInResult)
//                                                }
//                                            }
//                                        }
//                                    )
//                                LaunchedEffect(key1 = state.isSignedIn){
//                                    val userData = googleAuthUiClient.getSignedInUser()
//                                    userData?.run {
//                                        viewModel.addUserToFirestore(userData)
//                                        viewModel.getUserData(userData.userId)
//                                        navController.navigate(ChatsScreen)
//
//                                        viewModel.connectToSocket()
//                                    }
//
//                                }
//                                SignInScreenUI(
//                                    onSignInClick = {
//                                        lifecycleScope.launch {
//                                            val signInIntentSender = googleAuthUiClient.signIn()
//                                            launcher.launch(
//                                                IntentSenderRequest.Builder(
//                                                    signInIntentSender ?: return@launch
//                                                ).build()
//                                            )
//                                        }
//                                    }
//                                )
//                            }
//                            composable<ChatsScreen> {
//                                ChatsScreenUI(navController = navController,chatViewModel = viewModel)
//
//                            }
//                            // Add the new destination for our feature
//                            composable<PromptWriterScreen> {
//                                PromptWriterScreen()
//                            }
//
//                            composable<ConversationScreen> { backStackEntry ->
//                                // Extract the route object which contains the chatId
//                                val conversationRoute = backStackEntry.toRoute<ConversationScreen>()
//                                // Pass the chatId to your screen
//                                ConversationScreen(chatId = conversationRoute.chatId,chatViewModel = viewModel)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
