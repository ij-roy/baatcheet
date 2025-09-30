package roy.ij.baatcheet.screens

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import roy.ij.baatcheet.navigation.ConversationScreen
import roy.ij.baatcheet.navigation.PromptWriterScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import roy.ij.baatcheet.ChatViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreenUI(navController: NavController,chatViewModel: ChatViewModel = viewModel()) {
    val chats = listOf("Chat with John Doe", "Chat with Jane Smith", "Group Chat")
    val showDialog by chatViewModel.showNewChatDialog.collectAsState()
    val searchEmail by chatViewModel.searchEmail.collectAsState()

    if (showDialog) {
        NewChatDialog(
            email = searchEmail,
            onEmailChange = { chatViewModel.onSearchEmailChange(it) },
            onDismiss = { chatViewModel.onDismissNewChatDialog() },
            onAddChat = {
                chatViewModel.onAddChatClicked { userId ->
                    navController.navigate(ConversationScreen(chatId = userId))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Baatcheet") },
                actions = {
                    IconButton(onClick = {
                        // Use the imported route OBJECT
                        navController.navigate(PromptWriterScreen)
                    }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Prompt Writer"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { chatViewModel.onShowNewChatDialog() },
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Chat"
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (chats.isEmpty()) {
                EmptyChatPlaceholder()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(chats) { chatTitle ->
                        ChatItem(
                            title = chatTitle.toString(),
                            onClick = {
                                // Use the imported route DATA CLASS
                                navController.navigate(ConversationScreen(chatId = chatTitle))
                            }
                        )
                    }
                }
            }
        }
    }
}

// ... (Your ChatItem and EmptyChatPlaceholder composables remain the same)

@Composable
fun ChatItem(title: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true), // This provides the ripple effect
                onClick = onClick
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun EmptyChatPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Chats Yet",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the '+' button to start a new conversation.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NewChatDialog(
    email: String,
    onEmailChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddChat: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start New Chat") },
        text = {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Enter friend's email") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = onAddChat) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}