package roy.ij.baatcheet.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import roy.ij.baatcheet.BuildConfig

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}