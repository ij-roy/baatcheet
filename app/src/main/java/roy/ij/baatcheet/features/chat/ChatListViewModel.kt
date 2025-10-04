package roy.ij.baatcheet.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import roy.ij.baatcheet.data.network.ApiService
import roy.ij.baatcheet.data.network.RetrofitClient

class ChatListViewModel(private val api: ApiService = RetrofitClient.api) : ViewModel() {
    private val _rooms = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val rooms: StateFlow<List<Map<String, Any>>> = _rooms

    fun load(token: String) {
        viewModelScope.launch {
            try {
                val resp = api.listMyRooms("Bearer $token")
                val arr = resp["rooms"] as? List<Map<String, Any>> ?: emptyList()
                _rooms.value = arr
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}