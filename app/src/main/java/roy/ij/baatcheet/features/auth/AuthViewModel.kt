package roy.ij.baatcheet.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import roy.ij.baatcheet.data.network.RetrofitClient

data class AuthState(
    val isLoading: Boolean = false,
    val token: String? = null,
    val error: String? = null
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository(RetrofitClient.api)
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun login(username: String, password: String) {
        _state.value = AuthState(isLoading = true)
        viewModelScope.launch {
            try {
                val resp = repo.login(username, password)
                _state.value = AuthState(token = resp.token)
                // TODO save token securely (EncryptedSharedPreferences / Keystore)
            } catch (e: Exception) {
                _state.value = AuthState(error = e.message)
            }
        }
    }

    fun register(username: String, password: String) {
        _state.value = AuthState(isLoading = true)
        viewModelScope.launch {
            try {
                val resp = repo.register(username, password)
                _state.value = AuthState(token = resp.token)
            } catch (e: Exception) {
                _state.value = AuthState(error = e.message)
            }
        }
    }
}
