package si.sensum.demo.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.jetbrains_mono_bold
import si.sensum.demo.resources.jetbrains_mono_extrabold
import si.sensum.demo.resources.jetbrains_mono_light
import si.sensum.demo.resources.jetbrains_mono_medium
import si.sensum.demo.resources.jetbrains_mono_regular
import si.sensum.demo.resources.jetbrains_mono_semibold
import si.sensum.demo.resources.jetbrains_mono_thin

@Composable
private fun jetBrainsMonoFamily(): FontFamily {
    return FontFamily(
        Font(resource = Res.font.jetbrains_mono_thin, weight = FontWeight.Thin),
        Font(resource = Res.font.jetbrains_mono_light, weight = FontWeight.Light),
        Font(resource = Res.font.jetbrains_mono_regular, weight = FontWeight.Normal),
        Font(resource = Res.font.jetbrains_mono_medium, weight = FontWeight.Medium),
        Font(resource = Res.font.jetbrains_mono_semibold, weight = FontWeight.SemiBold),
        Font(resource = Res.font.jetbrains_mono_bold, weight = FontWeight.Bold),
        Font(resource = Res.font.jetbrains_mono_extrabold, weight = FontWeight.ExtraBold),
    )
}

@Composable
private fun sensumTypography(): Typography {
    val jetBrainsMonoFamily = jetBrainsMonoFamily()

    return Typography(
        bodySmall = TextStyle(
            fontFamily = jetBrainsMonoFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = jetBrainsMonoFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = jetBrainsMonoFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        labelSmall = TextStyle(
            fontFamily = jetBrainsMonoFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
        ),
        labelMedium = TextStyle(
            fontFamily = jetBrainsMonoFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        ),
        titleSmall = TextStyle(
            fontFamily = jetBrainsMonoFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        ),
        titleMedium = TextStyle(
            fontFamily = jetBrainsMonoFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        ),
        titleLarge = TextStyle(
            fontFamily = jetBrainsMonoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = jetBrainsMonoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
    )
}

object SensumColors {
    val Background = Color(0xFF1E1E1E)
    val Surface = Color(0xFF2B2B2B)
    val SurfaceVariant = Color(0xFF3C3F41)
    val Border = Color(0xFF4A4D50)

    val OnBackground = Color(0xFFBBBBBB)
    val OnSurface = Color(0xFFD4D4D4)
    val Muted = Color(0xFF7A7E82)

    val Accent = Color(0xFFE8A838)
    val AccentMuted = Color(0xFF7A4F10)
    val OnAccent = Color(0xFFFFFFFF)

    val Success = Color(0xFF4CAF7D)
    val Warning = Color(0xFFE8A838)
    val Error = Color(0xFFCF6679)
    val Info = Color(0xFF4E9FD1)
}

private val SensumColorScheme = darkColorScheme(
    primary = SensumColors.Accent,
    onPrimary = SensumColors.OnAccent,
    primaryContainer = SensumColors.AccentMuted,

    background = SensumColors.Background,
    onBackground = SensumColors.OnBackground,

    surface = SensumColors.Surface,
    onSurface = SensumColors.OnSurface,
    surfaceVariant = SensumColors.SurfaceVariant,

    error = SensumColors.Error,
    outline = SensumColors.Border,
)

@Composable
fun SensumTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SensumColorScheme,
        typography = sensumTypography()
    ) {
        CompositionLocalProvider(
            LocalContentColor provides SensumColors.OnBackground,
            content = content
        )
    }
}