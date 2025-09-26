package roy.ij.baatcheet.screens

// PromptWriterViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import roy.ij.baatcheet.WriterRequest
import roy.ij.baatcheet.WriterResponse

// --- 1. Define the API Service Interface ---
interface ApiService {
    @POST("/api/proxy/writer-prompt")
    suspend fun getArchitectedPrompt(@Body request: WriterRequest): WriterResponse
}

// --- 2. Create a Singleton Retrofit Client ---
object RetrofitClient {
    // For Android emulator, localhost is 10.0.2.2
    private const val BASE_URL = "http://ij.dophera.xyz/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

// --- 3. The ViewModel ---
class PromptWriterViewModel  : ViewModel() {

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
                val response = RetrofitClient.instance.getArchitectedPrompt(request)
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

// --- 4. A helper sealed interface to manage UI state ---
sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Success(val response: WriterResponse ) : UiState
    data class Error(val message: String) : UiState
}