package roy.ij.baatcheet.screens

// ChatsScreenUI.kt

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AutoAwesome // New Icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import roy.ij.baatcheet.PromptWriterScreen

@OptIn(ExperimentalMaterial3Api::class) // Required for TopAppBar
@Composable
// We now pass the NavController to handle clicks
fun ChatsScreenUI(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BaatCheet") },
                actions = {
                    // This is the button to open the Prompt Architect
                    IconButton(onClick = {
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
                onClick = {},
                shape = RoundedCornerShape(50.dp),
                contentColor = MaterialTheme.colorScheme.inversePrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.AddComment,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ){ paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // Your list of chats will go here later
            Text(text = "Chats Screen")
        }
    }
}