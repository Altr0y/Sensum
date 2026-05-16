package si.sensum.demo.components.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object SensumColors {
    // === Dark ===
    val Dark = Palette(
        background     = Color(0xFF1E1E1E),
        surface        = Color(0xFF2B2B2B),
        surfaceVariant = Color(0xFF3C3F41),
        border         = Color(0xFF4A4D50),
        onBackground   = Color(0xFFBBBBBB),
        onSurface      = Color(0xFFD4D4D4),
        muted          = Color(0xFF7A7E82),
        accent         = Color(0xFFE8A838),
        accentMuted    = Color(0xFF7A4F10),
        onAccent       = Color(0xFFFFFFFF),
        success        = Color(0xFF4CAF7D),
        warning        = Color(0xFFE8A838),
        error          = Color(0xFFCF6679),
        info           = Color(0xFF4E9FD1),
    )

    // === Light ===
    val Light = Palette(
        background     = Color(0xFFF5F5F5),
        surface        = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE8E8E8),
        border         = Color(0xFFCCCCCC),
        onBackground   = Color(0xFF2B2B2B),
        onSurface      = Color(0xFF1E1E1E),
        muted          = Color(0xFF7A7E82),
        accent         = Color(0xFF4A7C3F),  // zelena v stilu logotipa
        accentMuted    = Color(0xFFD6E8D3),
        onAccent       = Color(0xFFFFFFFF),
        success        = Color(0xFF4CAF7D),
        warning        = Color(0xFFE8A838),
        error          = Color(0xFFCF6679),
        info           = Color(0xFF4E9FD1),
    )

    data class Palette(
        val background: Color,
        val surface: Color,
        val surfaceVariant: Color,
        val border: Color,
        val onBackground: Color,
        val onSurface: Color,
        val muted: Color,
        val accent: Color,
        val accentMuted: Color,
        val onAccent: Color,
        val success: Color,
        val warning: Color,
        val error: Color,
        val info: Color,
    )
}

internal fun darkColorScheme() = darkColorScheme(
    primary          = SensumColors.Dark.accent,
    onPrimary        = SensumColors.Dark.onAccent,
    primaryContainer = SensumColors.Dark.accentMuted,
    background       = SensumColors.Dark.background,
    onBackground     = SensumColors.Dark.onBackground,
    surface          = SensumColors.Dark.surface,
    onSurface        = SensumColors.Dark.onSurface,
    surfaceVariant   = SensumColors.Dark.surfaceVariant,
    error            = SensumColors.Dark.error,
    outline          = SensumColors.Dark.border,
)

internal fun lightColorScheme() = lightColorScheme(
    primary          = SensumColors.Light.accent,
    onPrimary        = SensumColors.Light.onAccent,
    primaryContainer = SensumColors.Light.accentMuted,
    background       = SensumColors.Light.background,
    onBackground     = SensumColors.Light.onBackground,
    surface          = SensumColors.Light.surface,
    onSurface        = SensumColors.Light.onSurface,
    surfaceVariant   = SensumColors.Light.surfaceVariant,
    error            = SensumColors.Light.error,
    outline          = SensumColors.Light.border,
)