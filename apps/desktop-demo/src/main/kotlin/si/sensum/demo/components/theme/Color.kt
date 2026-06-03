package si.sensum.demo.components.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object SensumColors {
    val Dark = Palette(
        background = Color(0xFF1E1E1E),
        surface = Color(0xFF262626),
        surfaceVariant = Color(0xFF303322),
        surfaceOverlay = Color(0xCC262626),
        border = Color(0xFF4C5630),

        onBackground = Color(0xFFF2F2F2),
        onSurface = Color(0xFFF5F5F5),
        muted = Color(0xFFC7C7C7),

        accent = Color(0xFFE8A838),
        accentStrong = Color(0xFFF2B84B),
        accentMuted = Color(0xFF4A3412),
        onAccent = Color(0xFF1E1E1E),

        success = Color(0xFF9DC447),
        warning = Color(0xFFE8A838),
        error = Color(0xFFCB0505),
        info = Color(0xFF88DBC1),
    )

    val Light = Palette(
        background = Color(0xFFF5F5F5),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF1F6E5),
        surfaceOverlay = Color(0xEAFFFFFF),
        border = Color(0xFFD7E8B6),

        onBackground = Color(0xFF1E1E1E),
        onSurface = Color(0xFF232323),
        muted = Color(0xFF666B5A),

        accent = Color(0xFF8CB546),
        accentStrong = Color(0xFF97BE46),
        accentMuted = Color(0xFFDDEDC0),
        onAccent = Color(0xFF1E1E1E),

        success = Color(0xFF8CB546),
        warning = Color(0xFFB57918),
        error = Color(0xFFCB0505),
        info = Color(0xFF88DBC1),
    )

    data class Palette(
        val background: Color,
        val surface: Color,
        val surfaceVariant: Color,
        val surfaceOverlay: Color,
        val border: Color,
        val onBackground: Color,
        val onSurface: Color,
        val muted: Color,
        val accent: Color,
        val accentStrong: Color,
        val accentMuted: Color,
        val onAccent: Color,
        val success: Color,
        val warning: Color,
        val error: Color,
        val info: Color,
    )
}

object SensumRadius {
    val xs = 2.dp
    val sm = 4.dp
    val md = 6.dp
    val lg = 8.dp
    val xl = 10.dp
}

object SensumSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
}

internal fun darkColorScheme() = darkColorScheme(
    primary = SensumColors.Dark.accent,
    onPrimary = SensumColors.Dark.onAccent,
    primaryContainer = SensumColors.Dark.accentMuted,
    onPrimaryContainer = SensumColors.Dark.accentStrong,

    secondary = SensumColors.Dark.success,
    onSecondary = SensumColors.Dark.onAccent,
    secondaryContainer = Color(0xFF303322),
    onSecondaryContainer = SensumColors.Dark.onSurface,

    background = SensumColors.Dark.background,
    onBackground = SensumColors.Dark.onBackground,

    surface = SensumColors.Dark.surface,
    onSurface = SensumColors.Dark.onSurface,
    surfaceVariant = SensumColors.Dark.surfaceVariant,
    onSurfaceVariant = SensumColors.Dark.muted,

    error = SensumColors.Dark.error,
    outline = SensumColors.Dark.border,
)

internal fun lightColorScheme() = lightColorScheme(
    primary = SensumColors.Light.accent,
    onPrimary = SensumColors.Light.onAccent,
    primaryContainer = SensumColors.Light.accentMuted,
    onPrimaryContainer = SensumColors.Light.accentStrong,

    secondary = Color(0xFF9DC447),
    onSecondary = SensumColors.Light.onAccent,
    secondaryContainer = Color(0xFFE9F4CF),
    onSecondaryContainer = SensumColors.Light.onSurface,

    background = SensumColors.Light.background,
    onBackground = SensumColors.Light.onBackground,

    surface = SensumColors.Light.surface,
    onSurface = SensumColors.Light.onSurface,
    surfaceVariant = SensumColors.Light.surfaceVariant,
    onSurfaceVariant = SensumColors.Light.muted,

    error = SensumColors.Light.error,
    outline = SensumColors.Light.border,
)