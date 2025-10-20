package roy.ij.baatcheet.features.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import roy.ij.baatcheet.data.AuthSession
import roy.ij.baatcheet.navigation.NavRoutes
import roy.ij.baatcheet.security.*

@Composable
fun LockScreen(navController: NavHostController,viewModel: AuthViewModel) {
    val ctx = LocalContext.current
    val username = remember { SecureStore.getUsername(ctx) ?: "" }
    val blob = remember { SecureStore.getTokenBlob(ctx) }
    val activity = ctx as FragmentActivity

    // Automatically prompt biometric unlock
    LaunchedEffect(Unit) {
        promptToDecryptAndUnlock(activity, blob) { tokenOrNull ->
            if (tokenOrNull != null) {
//                println("🔓 Biometric decrypt success: ${tokenOrNull.take(10)}...")

                // ✅ Update shared ViewModel state so rest of app knows we're authenticated
                viewModel.onAuthSuccess(ctx, username, tokenOrNull)

                // ✅ Navigate safely to ChatList
                navController.navigate(NavRoutes.ChatList.route) {
                    popUpTo(NavRoutes.Lock.route) { inclusive = true }
                    launchSingleTop = true
                }
            } else {
                println("❌ Biometric unlock failed or canceled")
            }
        }
    }
    // Optional: fallback button or UI message
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Unlocking securely…")
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome back, $username")
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            promptToDecryptAndUnlock(activity, blob) { tokenOrNull ->
                if (tokenOrNull != null) {
                    viewModel.onAuthSuccess(ctx, username, tokenOrNull)
                    navController.navigate(NavRoutes.ChatList.route) {
                        popUpTo(NavRoutes.Lock.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }) {
            Text("Unlock with biometric")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = {
            navController.navigate(NavRoutes.Auth.route) {
                popUpTo(NavRoutes.Auth.route) { inclusive = true }
            }
        }) {
            Text("Use password")
        }
    }
}
