package roy.ij.baatcheet.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import roy.ij.baatcheet.WriterRequest
import roy.ij.baatcheet.WriterResponse
import roy.ij.baatcheet.data.model.User

data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String, val user: UserDto?)
data class UserDto(val id: String, val username: String)
data class PublicKeyRequest(val publicKey: String)
data class OkResponse(val ok: Boolean)

interface ApiService {
    @POST("/api/proxy/writer-prompt")
    suspend fun getRewrittenPrompt(@Body request: WriterRequest): WriterResponse

    @POST("users/register")
    suspend fun register(@Body req: AuthRequest): AuthResponse

    @POST("users/login")
    suspend fun login(@Body req: AuthRequest): AuthResponse

    @GET("users/me")
    suspend fun me(@Header("Authorization") bearer: String): Map<String, Any>

    @POST("users/public-key")
    suspend fun setPublicKey(
        @Header("Authorization") bearer: String,
        @Body req: PublicKeyRequest
    ): OkResponse


}