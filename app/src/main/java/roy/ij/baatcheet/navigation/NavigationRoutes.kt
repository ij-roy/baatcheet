package roy.ij.baatcheet.navigation

import kotlinx.serialization.Serializable

@Serializable
object StartScreen

@Serializable
object SignInScreen

@Serializable
object ChatsScreen

@Serializable
object PromptWriterScreen

@Serializable
data class ConversationScreen(val chatId: String)

sealed class NavRoutes(val route: String) {
    object Auth : NavRoutes("auth")
    object ChatList : NavRoutes("chatlist")
    object Room : NavRoutes("room")
}
