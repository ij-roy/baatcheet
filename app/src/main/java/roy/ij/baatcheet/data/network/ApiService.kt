package roy.ij.baatcheet.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import roy.ij.baatcheet.WriterRequest
import roy.ij.baatcheet.WriterResponse
import roy.ij.baatcheet.data.model.User

interface ApiService {
    @POST("/api/proxy/writer-prompt")
    suspend fun getRewrittenPrompt(@Body request: WriterRequest): WriterResponse

    @POST("/api/users/sync")
    suspend fun syncUser(@Body request: roy.ij.baatcheet.data.model.SyncRequest): User

    @GET("/api/users")
    suspend fun findUserByEmail(@Query("email") email: String): User
}