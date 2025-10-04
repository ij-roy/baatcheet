package roy.ij.baatcheet.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import roy.ij.baatcheet.data.crypto.CryptoHelper
import roy.ij.baatcheet.data.network.SocketManager
import roy.ij.baatcheet.data.network.MemberDto
import roy.ij.baatcheet.data.network.RetrofitClient
import roy.ij.baatcheet.data.network.ApiService
import javax.crypto.SecretKey
import java.time.Instant


data class ChatMessage(val id: String?, val alias: String, val text: String, val mine: Boolean, val at: Long)

data class ChatUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val myUserId: String? = null,
    val roomId: String,
    val members: List<MemberDto> = emptyList(),
    val messages: List<ChatMessage> = emptyList()
)



class ChatViewModel(
    private val token: String,
    private val roomId: String,
    private val api: ApiService = RetrofitClient.api,
    private val repo: ChatRepository = ChatRepository(RetrofitClient.api)
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState(roomId = roomId))
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    private val socket = SocketManager(token)

    init {
        viewModelScope.launch {
            try {
                // 1) who am I
                val meResp = repo.me(token) // { user: { id, username } }
                val meUser = (meResp["user"] as? Map<*, *>) ?: throw IllegalStateException("me failed")
                val myUserId = meUser["id"] as String

                // 2) load members & keys
                val membersResp = repo.members(token, roomId)
                val members = membersResp.members

                // 3) connect socket + join
                socket.connect()
                socket.joinRoom(roomId) { /* ack ignored here */ }

                // 4) subscribe to incoming messages
                socket.onNewMessage { msg ->
                    // msg: { _id, roomId, alias, type, ciphertext, iv, encKey, createdAt }
                    try {
//                        println("inside try 1 📩 msg:new encKey.len=${msg.optString("encKey").length} iv.len=${msg.optString("iv").length} ct.len=${msg.optString("ciphertext").length}")
                        val senderId = msg.optString("senderId")
                        val myId = state.value.myUserId
                        if (senderId == myId) {
                            // 👇 ignore messages I just sent myself
                            return@onNewMessage
                        }

                        val encKey = msg.getString("encKey")
                        val secret: SecretKey = CryptoHelper.unwrapAesKey(encKey)
                        val text = CryptoHelper.decryptAes(msg.getString("ciphertext"), msg.getString("iv"), secret)
                        val alias = msg.getString("alias")
                        val id = msg.optString("_id", null)
                        val at = Instant.parse(msg.getString("createdAt")).toEpochMilli()
                        appendMessage(ChatMessage(id, alias, text, mine = false, at = at))
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        println("inside catch 1📩 msg:new encKey.len=${msg.optString("encKey").length} iv.len=${msg.optString("iv").length} ct.len=${msg.optString("ciphertext").length}")
                        appendMessage(ChatMessage(null, "System", "⚠️ failed to decrypt a message", false, System.currentTimeMillis()))
//                        println("inside catch 2📩 msg:new encKey.len=${msg.optString("encKey").length} iv.len=${msg.optString("iv").length} ct.len=${msg.optString("ciphertext").length}")
                    }
                }

                // 5) (optional) load history once
                // 5) Load and decrypt message history before listening to new messages
                loadHistory(token, roomId, myUserId)


                _state.update { it.copy(loading = false, myUserId = myUserId, members = members) }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message ?: "init failed") }
            }
        }
    }

    fun send(text: String) {
        val s = state.value
        val myUserId = s.myUserId ?: return
        val members = s.members.filter { it.publicKey != null } // only those with keys

        viewModelScope.launch(Dispatchers.Default) {
            try {
                // 1) encrypt content once
                val secret = CryptoHelper.generateAesKey()
                val (ciphertext, iv) = CryptoHelper.encryptAes(text, secret)

                // 2) wrap AES key for EVERY approved member (including self so history works offline)
                val envelopes = members.map { m ->
                    val encKey = CryptoHelper.wrapAesKey(secret, m.publicKey!!)
                    mapOf("userId" to m.userId, "encKey" to encKey)
                }

                // 🔹 Debug log here
                println("📤 Sending message with envelopes:")
                envelopes.forEach {
                    println(" -> userId=${it["userId"]}, encKey.len=${(it["encKey"] as String).length}")
                }

                // 3) payload
                val payload = JSONObject(
                    mapOf(
                        "roomId" to s.roomId,
                        "ciphertext" to ciphertext,
                        "iv" to iv,
                        "keyEnvelope" to envelopes
                    )
                )

                // 4) emit
                socket.sendMessage(payload) { /* ack ignored for now */ }

                // 5) optimistic UI
                appendMessage(ChatMessage(id = null, alias = "Me", text = text, mine = true, at = System.currentTimeMillis()))
            } catch (e: Exception) {
                e.printStackTrace()
                appendMessage(ChatMessage(null, "System", "❌ send failed: ${e.message}", true, System.currentTimeMillis()))
            }
        }
    }

    private fun appendMessage(m: ChatMessage) {
        _state.update { it.copy(messages = it.messages + m) }
    }

    private suspend fun loadHistory(token: String, roomId: String, myId: String) {
        try {
            val hist = repo.history(token, roomId)
            val arr = (hist["messages"] as? List<*>) ?: return
            val msgs = arr.mapNotNull { any ->
                try {
                    val m = any as Map<*, *>
                    val envs = m["keyEnvelope"] as? List<*> ?: return@mapNotNull null
                    val myEnv = envs.firstOrNull { (it as Map<*, *>)["userId"] == myId } as? Map<*, *> ?: return@mapNotNull null
                    val secret = CryptoHelper.unwrapAesKey(myEnv["encKey"] as String)
                    val text = CryptoHelper.decryptAes(
                        m["ciphertext"] as String,
                        (m["iv"] as? String) ?: "",
                        secret
                    )
                    ChatMessage(
                        id = m["_id"]?.toString(),
                        alias = m["alias"] as String,
                        text = text,
                        mine = (m["senderId"] as? String) == myId,
                        at = Instant.parse(m["createdAt"] as String).toEpochMilli()
                    )
                } catch (_: Exception) { null }
            }
            _state.update { it.copy(messages = msgs) }
        } catch (e: Exception) {
            e.printStackTrace()
            appendMessage(ChatMessage(null, "System", "⚠️ failed to load history", false, System.currentTimeMillis()))
        }
    }


    override fun onCleared() {
        super.onCleared()
        socket.disconnect()
    }
}