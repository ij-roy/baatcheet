package roy.ij.baatcheet.features.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.interaction.MutableInteractionSource
import roy.ij.baatcheet.navigation.NavRoutes

@Composable
fun ChatListScreen(
    navController: NavController,
    token: String
) {
    val vm: ChatListViewModel = viewModel()
    val rooms by vm.rooms.collectAsState()

    LaunchedEffect(token) { vm.load(token) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Rooms", style = MaterialTheme.typography.titleLarge)

        LazyColumn {
            items(rooms) { room ->
                val roomId = room["roomId"] as String
                val members = room["members"] as List<Map<String, Any>>
                val me = members.find { (it["status"] == "approved") }
                val amApproved = me != null && me["status"] == "approved"

                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            enabled = amApproved,
                            indication = null, // 👈 removes ripple, avoids crash
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            navController.navigate(NavRoutes.Conversation.create(roomId))
                        }
                        .padding(12.dp)
                ) {
                    Text("Room $roomId")
                    Spacer(Modifier.weight(1f))
                    if (amApproved) Text("✅") else Text("⏳ waiting")
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(onClick = { navController.navigate(NavRoutes.Room.route) }) {
            Text("Create/Join Room")
        }
    }
}