package roy.ij.baatcheet.features.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ConversationScreen(viewModel: ChatViewModel) {

    val ui by viewModel.state.collectAsState()
    var input by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        if (ui.loading) Text("Loading…")
        ui.error?.let { Text("Error: $it", color = Color.Red) }

        LazyColumn(Modifier.weight(1f)) {
            items(ui.messages) { m ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = if (m.mine) Arrangement.End else Arrangement.Start
                ) {
                    Text("${m.alias}: ${m.text}",
                        Modifier.padding(8.dp).background(Color.LightGray))
                }
            }
        }
        Row(Modifier.padding(8.dp)) {
            OutlinedTextField(value = input, onValueChange = { input = it },
                modifier = Modifier.weight(1f))
            Button(onClick = {
                if (input.isNotBlank()) {
                    viewModel.send(input)
                    input = ""
                }
            }) { Text("Send") }
        }
    }
}