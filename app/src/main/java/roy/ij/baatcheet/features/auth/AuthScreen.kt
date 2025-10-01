package roy.ij.baatcheet.features.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        Spacer(Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

        Row {
            Button(onClick = { viewModel.login(username, password) }) { Text("Login") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { viewModel.register(username, password) }) { Text("Register") }
        }

        if (state.isLoading) Text("Loading…")
        state.error?.let { Text("Error: $it", color = Color.Red) }
        state.token?.let { Text("Logged in! Token: ${it.take(20)}...") }
    }
}
