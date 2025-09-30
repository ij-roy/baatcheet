package roy.ij.baatcheet.googleSignIn


import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import roy.ij.baatcheet.ChatViewModel
import roy.ij.baatcheet.UserData
import roy.ij.baatcheet.SignInResult
import roy.ij.baatcheet.data.model.SyncRequest
import roy.ij.baatcheet.data.network.RetrofitClient

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient,
    val viewModel: ChatViewModel
) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("365846685425-ima92qet9j3i42lt25kto5o0qs77b6m1.apps.googleusercontent.com")
                .build()
        ).setAutoSelectEnabled(true).build()
    }

    suspend fun signInWithClient(intent: Intent): SignInResult {
        viewModel.resetState()
        val cred = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = cred.googleIdToken
        val googleCred = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            // This part signs the user into Firebase
            val authResult = auth.signInWithCredential(googleCred).await()
            val user = authResult.user

            // --- NEW AND CRITICAL CODE ---
            // After signing in, get a fresh ID token FROM FIREBASE.
            // This token will have the correct audience for your backend.
            val firebaseIdToken = user?.getIdToken(true)?.await()?.token

            if (user != null && firebaseIdToken != null) {
                try {
                    // Send the CORRECT token to your backend
                    RetrofitClient.instance.syncUser(SyncRequest(idToken = firebaseIdToken))
                    Log.d("UserSync", "User successfully synced with backend.")
                } catch (e: Exception) {
                    Log.e("UserSync", "Failed to sync user with backend.", e)
                }
            }
            // --- END OF NEW CODE ---

            SignInResult(
                errorMessage = null,
                data = user?.run {
                    UserData(
                        email = email.toString(),
                        userId = uid,
                        userName = displayName.toString(),
                        ppurl = photoUrl.toString().substring(0, photoUrl.toString().length - 6)
                    )
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            email = email.toString(),
            userId = uid,
            userName = displayName,
            ppurl = photoUrl.toString()
        )
    }
}