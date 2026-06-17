package roy.ij.baatcheet.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import roy.ij.baatcheet.R

@Immutable
data class BaatCheetColors(
    val privacy: Color,
    val notification: Color,
    val success: Color,
    val encryptionBadge: Color,
    val chatMineBubble: Color,
    val chatMineText: Color,
    val chatOtherBubble: Color,
    val chatOtherText: Color,
    val textSecondary: Color
)

internal val LocalBaatCheetColors = staticCompositionLocalOf {
    BaatCheetColors(
        privacy = Color.Unspecified,
        notification = Color.Unspecified,
        success = Color.Unspecified,
        encryptionBadge = Color.Unspecified,
        chatMineBubble = Color.Unspecified,
        chatMineText = Color.Unspecified,
        chatOtherBubble = Color.Unspecified,
        chatOtherText = Color.Unspecified,
        textSecondary = Color.Unspecified
    )
}

val MaterialTheme.baatCheetColors: BaatCheetColors
    @Composable
    @ReadOnlyComposable
    get() = LocalBaatCheetColors.current

@Composable
internal fun securePopColorScheme(darkTheme: Boolean): ColorScheme {
    val primary = colorResource(R.color.baat_primary)
    val secondary = colorResource(R.color.baat_secondary)
    val privacy = colorResource(R.color.baat_privacy)
    val notification = colorResource(R.color.baat_notification)
    val background = colorResource(R.color.baat_background)
    val surface = colorResource(R.color.baat_surface)
    val text = colorResource(R.color.baat_text)
    val textSecondary = colorResource(R.color.baat_text_secondary)
    val outline = colorResource(R.color.baat_outline)
    val error = colorResource(R.color.baat_error)
    val chatOtherBubble = colorResource(R.color.baat_chat_other_bubble)
    val chatOtherText = colorResource(R.color.baat_chat_other_text)
    val onPrimary = colorResource(R.color.baat_on_primary)
    val onAccent = colorResource(R.color.baat_on_accent)

    return if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = chatOtherBubble,
            onPrimaryContainer = chatOtherText,
            secondary = secondary,
            onSecondary = onAccent,
            secondaryContainer = secondary,
            onSecondaryContainer = onAccent,
            tertiary = privacy,
            onTertiary = onPrimary,
            tertiaryContainer = notification,
            onTertiaryContainer = onAccent,
            background = background,
            onBackground = text,
            surface = surface,
            onSurface = text,
            surfaceVariant = chatOtherBubble,
            onSurfaceVariant = textSecondary,
            outline = outline,
            outlineVariant = outline,
            error = error,
            onError = onPrimary
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = chatOtherBubble,
            onPrimaryContainer = chatOtherText,
            secondary = secondary,
            onSecondary = onAccent,
            secondaryContainer = secondary,
            onSecondaryContainer = onAccent,
            tertiary = privacy,
            onTertiary = onPrimary,
            tertiaryContainer = notification,
            onTertiaryContainer = onAccent,
            background = background,
            onBackground = text,
            surface = surface,
            onSurface = text,
            surfaceVariant = chatOtherBubble,
            onSurfaceVariant = textSecondary,
            outline = outline,
            outlineVariant = outline,
            error = error,
            onError = onPrimary
        )
    }
}

@Composable
internal fun securePopExtraColors(): BaatCheetColors =
    BaatCheetColors(
        privacy = colorResource(R.color.baat_privacy),
        notification = colorResource(R.color.baat_notification),
        success = colorResource(R.color.baat_success),
        encryptionBadge = colorResource(R.color.baat_encryption_badge),
        chatMineBubble = colorResource(R.color.baat_chat_mine_bubble),
        chatMineText = colorResource(R.color.baat_chat_mine_text),
        chatOtherBubble = colorResource(R.color.baat_chat_other_bubble),
        chatOtherText = colorResource(R.color.baat_chat_other_text),
        textSecondary = colorResource(R.color.baat_text_secondary)
    )
