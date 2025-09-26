package roy.ij.baatcheet.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.noties.markwon.Markwon
import roy.ij.baatcheet.WriterResponse

@Composable
fun PromptArchitectScreen(
    // Get an instance of our ViewModel
    promptViewModel: PromptWriterViewModel  = viewModel()
) {
    // ... the rest of the code is the same
    // 1. Collect the UI state from the ViewModel
    val uiState by promptViewModel.uiState.collectAsStateWithLifecycle()

    // 2. State for the user's text input
    var inputText by remember { mutableStateOf("") }

    // 3. Main UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make the column scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Prompt Writer", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Text field for user input
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter your prompt idea...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Button to submit the prompt
        Button(
            onClick = {
                if (inputText.isNotBlank()) {
                    promptViewModel.fetchRewrittenPrompt(inputText)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get the Prompt")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 4. Display the result based on the UI state
        when (val state = uiState) {
            is UiState.Idle -> {
                // Do nothing or show a placeholder
            }
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Success -> {
                ResultCard(response = state.response)
            }
            is UiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ResultCard(response: WriterResponse) {
    val context = LocalContext.current
    // Get the clipboard manager to handle copying text
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Rewritten Prompt:", style = MaterialTheme.typography.titleMedium)
                // Add the Copy button
                IconButton(onClick = {
                    // Create a clip with the raw, unformatted text
                    val clip = ClipData.newPlainText("prompt", response.rewrittenPrompt)
                    clipboardManager.setPrimaryClip(clip)
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Prompt")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Replace the simple Text composable with our new MarkdownText composable
            MarkdownText(markdown = response.rewrittenPrompt)
        }
    }
}

// New composable to render Markdown
@Composable
fun MarkdownText(markdown: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val markwon = remember { Markwon.create(context) }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            TextView(ctx)
        },
        update = { textView ->
            // Use the Markwon library to set the styled text
            markwon.setMarkdown(textView, markdown)
        }
    )
}