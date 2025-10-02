package roy.ij.baatcheet.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import roy.ij.baatcheet.data.network.*

class RoomViewModel(
    private val api: ApiService = RetrofitClient.api
) : ViewModel() {

    private val _state = MutableStateFlow(RoomState())
    val state: StateFlow<RoomState> = _state

    private var token: String? = null
    fun setToken(t: String) {
        token = t
    }

    // you’ll want to persist token somewhere (EncryptedSharedPreferences or DataStore)
//    private var token: String? = null
//    fun setToken(t: String) { token = t }

    fun createRoom(code: String?, duration: Int?) {
        val bearer = "Bearer ${roy.ij.baatcheet.data.AuthSession.token ?: return}"
        _state.value = RoomState(isLoading = true)
        viewModelScope.launch {
            try {
                val resp = api.createRoom(bearer, CreateRoomReq(code, duration))
                _state.value = RoomState(
                    isLoading = false,
                    roomId = resp.roomId,
                    alias = resp.alias,
                    message = "Room created: ${resp.roomId}"
                )
            } catch (e: Exception) {
                _state.value = RoomState(isLoading = false, error = e.message)
            }
        }
    }

    fun joinRoom(roomId: String, code: String?) {
        val bearer = "Bearer ${roy.ij.baatcheet.data.AuthSession.token ?: return}"
        _state.value = RoomState(isLoading = true)
        viewModelScope.launch {
            try {
                val resp = api.joinRoom(bearer, JoinRoomReq(roomId, code))
                _state.value = RoomState(
                    isLoading = false,
                    message = resp["message"] as? String ?: "Join requested"
                )
            } catch (e: Exception) {
                _state.value = RoomState(isLoading = false, error = e.message)
            }
        }
    }

    fun approveMember(roomId: String, memberId: String) {
        val bearer = "Bearer ${roy.ij.baatcheet.data.AuthSession.token ?: return}"
        _state.value = RoomState(isLoading = true)
        viewModelScope.launch {
            try {
                val resp = api.approve(bearer, ApproveReq(roomId, memberId))
                _state.value = RoomState(
                    isLoading = false,
                    message = resp["message"] as? String ?: "Approved"
                )
            } catch (e: Exception) {
                _state.value = RoomState(isLoading = false, error = e.message)
            }
        }
    }

    fun listMyRooms() {
        val bearer = "Bearer ${roy.ij.baatcheet.data.AuthSession.token ?: return}"
        viewModelScope.launch {
            try {
                val resp = api.listMyRooms(bearer)
                println("Rooms: $resp") // you’ll build UI for this next
            } catch (e: Exception) {
                println("list rooms error: ${e.message}")
            }
        }
    }
}