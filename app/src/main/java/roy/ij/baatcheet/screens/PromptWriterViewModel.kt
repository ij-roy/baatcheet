package roy.ij.baatcheet.screens // Or your features.prompt_writer package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import roy.ij.baatcheet.WriterRequest
import roy.ij.baatcheet.WriterResponse
import roy.ij.baatcheet.data.network.RetrofitClient // Import the new central client

// --- The ViewModel ---
class PromptWriterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun fetchRewrittenPrompt(prompt: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val request = WriterRequest(
                    userId = "android-user-123", // Replace with actual user ID
                    initialPrompt = prompt,
                    targetModel = "gemini-1.5-pro", // This can be dynamic later
                    taskType = "code_generation"  // This can be dynamic later
                )
                // Use the new, central RetrofitClient
                val response = RetrofitClient.instance.getRewrittenPrompt(request)
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

// --- A helper sealed interface to manage UI state ---
sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Success(val response: WriterResponse) : UiState
    data class Error(val message: String) : UiState
}