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

class ChatViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()
    private val userCollection = Firebase.firestore.collection(USERS_COLLECTION)
    var userDataListener: ListenerRegistration?= null
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
}