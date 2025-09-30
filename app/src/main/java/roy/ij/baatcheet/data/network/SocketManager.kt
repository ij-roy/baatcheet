package roy.ij.baatcheet.data.network

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import roy.ij.baatcheet.BuildConfig

object SocketManager {
    private var mSocket: Socket? = null
    fun getSocket(): Socket? {
        return mSocket
    }
    // Call this function after a user logs in
    fun establishConnection(token: String) {
        try {
            val options = IO.Options().apply {
                // Send the Firebase ID token for authentication
                auth = mapOf("token" to token)
            }
            // Use the base URL from your BuildConfig
            mSocket = IO.socket(BuildConfig.BASE_URL, options)
            mSocket?.connect()
            mSocket?.on(Socket.EVENT_CONNECT) {
                Log.d("SocketManager", "Successfully connected to the server.")
            }
            mSocket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e("SocketManager", "Connection error: ${args.firstOrNull()}")
            }
        } catch (e: Exception) {
            Log.e("SocketManager", "Failed to establish connection", e)
        }
    }
    fun sendMessage(toUserId: String, message: String) {
        val messageData = mapOf("toUserId" to toUserId, "message" to message)
        mSocket?.emit("private_message", JSONObject(messageData))
        Log.d("SocketManager", "Sent message to $toUserId: $message")
    }
    fun closeConnection() {
        mSocket?.disconnect()
        mSocket?.off() // Remove all listeners
        Log.d("SocketManager", "Connection closed.")
    }
}