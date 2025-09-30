package roy.ij.baatcheet.screens

// screens/ConversationScreen.kt

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import roy.ij.baatcheet.ChatViewModel

@Composable
fun ConversationScreen(
    chatId: String,
    chatViewModel: ChatViewModel
) {

    Log.d("ConversationScreen", "Displaying chat for ID: $chatId")

    // Tell the ViewModel which chat we are in
    LaunchedEffect(key1 = chatId) {
        chatViewModel.loadMessagesFor(chatId)
    }

    var messageText by remember { mutableStateOf("") }
    val messages by chatViewModel.messages.collectAsState()

    Column(Modifier.fillMaxSize()) {
        // Message List
        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp),
            reverseLayout = true // To show newest messages at the bottom
        ) {
            items(messages.reversed()) { message ->
                Text(message, modifier = Modifier.padding(vertical = 4.dp))
            }
        }

        // Message Input
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter message...") }
            )
            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        // In a real app, you'd get the recipient's ID
                        chatViewModel.sendMessage(chatId, messageText)
                        messageText = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Send")
            }
        }
    }
}