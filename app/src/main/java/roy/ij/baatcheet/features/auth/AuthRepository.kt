package roy.ij.baatcheet.features.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import roy.ij.baatcheet.data.network.ApiService
import roy.ij.baatcheet.data.network.AuthRequest

class AuthRepository(private val api: ApiService) {
    suspend fun register(username: String, password: String) =
        withContext(Dispatchers.IO) { api.register(AuthRequest(username, password)) }

    suspend fun login(username: String, password: String) =
        withContext(Dispatchers.IO) { api.login(AuthRequest(username, password)) }
}
