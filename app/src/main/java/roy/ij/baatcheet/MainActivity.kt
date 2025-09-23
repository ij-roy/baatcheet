package roy.ij.baatcheet

import android.content.IntentSender
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import roy.ij.baatcheet.googleSignIn.GoogleAuthUiClient
import roy.ij.baatcheet.screens.ChatsScreenUI
import roy.ij.baatcheet.screens.SignInScreenUI
import roy.ij.baatcheet.ui.theme.BaatCheetTheme

class MainActivity : ComponentActivity() {
    // Video 05 done start from video 6 15 minutesll
    private val viewModel: ChatViewModel by viewModels()

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            viewModel = viewModel,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BaatCheetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        val state by viewModel.state.collectAsState()
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = StartScreen){
                            composable<StartScreen> {

                                LaunchedEffect(key1 = Unit){
                                    val userData = googleAuthUiClient.getSignedInUser()
                                    if (userData != null){
                                        navController.navigate(ChatsScreen)
                                    }else{
                                        navController.navigate(SignInScreen)
                                    }
                                }
                            }

                            composable<SignInScreen> {
                                val launcher =
                                    rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
                                        onResult = { result ->
                                            if (result.resultCode == RESULT_OK) {
                                                lifecycleScope.launch {
                                                    val signInResult = googleAuthUiClient.signInWithClient(
                                                        intent = result.data ?: return@launch
                                                    )
                                                    viewModel.onSignInResult(signInResult)
                                                }
                                            }
                                        }
                                    )
                                LaunchedEffect(key1 = state.isSignedIn){
                                    val userData = googleAuthUiClient.getSignedInUser()
                                    userData?.run {
                                        viewModel.addUserToFirestore(userData)
                                        viewModel.getUserData(userData.userId)
                                        navController.navigate(ChatsScreen)
                                    }

                                }
                                SignInScreenUI(
                                    onSignInClick = {
                                        lifecycleScope.launch {
                                            val signInIntentSender = googleAuthUiClient.signIn()
                                            launcher.launch(
                                                IntentSenderRequest.Builder(
                                                    signInIntentSender ?: return@launch
                                                ).build()
                                            )
                                        }
                                    }
                                )
                            }
                            composable<ChatsScreen> {
                                ChatsScreenUI()
                            }
                        }
                    }
                }
            }
        }
    }
}

