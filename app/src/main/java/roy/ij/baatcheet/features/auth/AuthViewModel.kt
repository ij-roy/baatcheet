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
                _state.value = AuthState(isLoading = false)
                println("inside login function")
                afterAuthSuccess(resp.token)

                roy.ij.baatcheet.data.AuthSession.token = resp.token
                println("inside login function")
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
                _state.value = AuthState(isLoading = false)
                afterAuthSuccess(resp.token)

                roy.ij.baatcheet.data.AuthSession.token = resp.token
            } catch (e: Exception) {
                _state.value = AuthState(error = e.message)
            }
        }
    }

    private fun afterAuthSuccess(token: String) {
        _state.value = _state.value.copy(token = token)

        viewModelScope.launch {
            try {
                // Ensure keypair exists
                roy.ij.baatcheet.data.crypto.KeyManager.ensureKeyPair()
                println("inside afterAuthSuccess function")

                // 🔑 Debug print
                val fingerprint = roy.ij.baatcheet.data.crypto.KeyManager.debugKeyFingerprint()
                println("🔑 Current key fingerprint: $fingerprint")

                // Export public key
                val pubB64 = roy.ij.baatcheet.data.crypto.KeyManager.exportPublicKeyBase64()

                // Upload to backend
                println("inside upload to backend")
                repo.uploadPublicKey(token, pubB64)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = _state.value.copy(error = "Key upload failed (will retry)")
            }
        }
    }
}
