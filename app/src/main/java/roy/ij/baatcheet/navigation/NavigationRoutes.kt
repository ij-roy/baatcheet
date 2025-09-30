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