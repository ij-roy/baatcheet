package roy.ij.baatcheet

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import roy.ij.baatcheet.data.network.RetrofitClient
import roy.ij.baatcheet.data.network.SocketManager

class ChatViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()
    private val userCollection = Firebase.firestore.collection(USERS_COLLECTION)
    var userDataListener: ListenerRegistration?= null

    private val _showNewChatDialog = MutableStateFlow(false)
    val showNewChatDialog: StateFlow<Boolean> = _showNewChatDialog.asStateFlow()

    private val _searchEmail = MutableStateFlow("")
    val searchEmail: StateFlow<String> = _searchEmail.asStateFlow()

    fun onShowNewChatDialog() {
        _showNewChatDialog.value = true
    }

    fun onDismissNewChatDialog() {
        _showNewChatDialog.value = false
        _searchEmail.value = ""
    }

    fun onSearchEmailChange(email: String) {
        _searchEmail.value = email
    }

    fun onAddChatClicked(onChatCreated: (String) -> Unit) {
        Log.d("Navigation_Debug", "onAddChatClicked function was called with email: ${_searchEmail.value}")

        viewModelScope.launch {
            try {
                val user = RetrofitClient.instance.findUserByEmail(_searchEmail.value)
                // TODO: Save the new chat to local DB
                onDismissNewChatDialog()
                Log.d("Navigation_Debug", "User found! Navigating with userId1: ${user.firebaseUid}")
                onChatCreated(user.firebaseUid) // Pass the user's ID to navigate
                Log.d("Navigation_Debug", "User found! Navigating with userId2: ${user.firebaseUid}")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error finding user", e)
                Log.e("Navigation_Debug", "Error finding user", e)
                // TODO: Show an error message to the user
            }
        }
    }

    fun resetState(){
    }

    fun onSignInResult(signInResult: SignInResult) {
        _state.update {
            it.copy(
                isSignedIn = signInResult.data != null,
                signInError = signInResult.errorMessage
            )
        }
    }

    fun addUserToFirestore(userData: UserData) {
        val userDataMap = mapOf(
            "userId" to userData?.userId,
            "username" to userData?.userName,
            "ppurl" to userData?.ppurl,
            "email" to userData?.email
        )
//        userCollection.document(userDataMap.get(userId/))
        val userDocument = userCollection.document(userData.userId)
        userDocument.get().addOnSuccessListener {
            if (it.exists()){
                userDocument.update(userDataMap).addOnSuccessListener {
                    Log.d(ContentValues.TAG,"User Data added to Firebase Successfully ")
                }.addOnFailureListener {
                    Log.d(ContentValues.TAG, "User Data added to Firebase Failed")
                }
            }else{
                userDocument.set(userDataMap).addOnSuccessListener {
                    Log.d(ContentValues.TAG,"User Data added to Firebase Successfully ")
                }.addOnFailureListener{
                    Log.d(ContentValues.TAG,"User Data added to Firebase Failed")
                }
            }
        }
    }

    fun getUserData(userId: String) {
        userDataListener = userCollection.document(userId).addSnapshotListener { value , error ->
            if (value != null){
                _state.update {
                    it.copy(userData = value.toObject(UserData::class.java))
                }
            }
        }
    }

    fun connectToSocket() {
        viewModelScope.launch {
            try {
                // Get the current user's Firebase ID token
                val token = Firebase.auth.currentUser?.getIdToken(true)?.await()?.token
                if (token != null) {
                    // Use the token to establish an authenticated connection
                    SocketManager.establishConnection(token)
                    Log.d("ChatViewModel", "Socket connection established.")
                } else {
                    Log.e("ChatViewModel", "Could not get Firebase ID token.")
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error getting ID token for socket", e)
            }
        }
    }

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages.asStateFlow()

    init {
        listenForMessages()
    }

    private fun listenForMessages() {
        SocketManager.getSocket()?.on("private_message") { args ->
            val data = args[0] as JSONObject
            val message = data.getString("message")
            _messages.value = _messages.value + message // Add new message to the list
        }
    }

    fun sendMessage(toUserId: String, message: String) {
        SocketManager.sendMessage(toUserId, message)
        _messages.value = _messages.value + "Me: $message" // Add your own message to the list
    }

    fun loadMessagesFor(chatId: String) {
        // TODO: In the future, you will load messages from your Room database here
        // For now, we can clear the list for the new chat.
        _messages.value = emptyList()
        Log.d("ChatViewModel", "Loading messages for chat: $chatId")
    }

}