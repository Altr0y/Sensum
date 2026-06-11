package si.sensum.demo.components.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import org.jetbrains.letsPlot.themes.flavorDarcula
import org.jetbrains.letsPlot.themes.flavorStandard

val LocalIsDarkTheme = staticCompositionLocalOf { true }
val LocalThemeColors = staticCompositionLocalOf { SensumColors.Dark }

val SensumThemeColors: SensumColors.Palette
    @Composable
    get() = LocalThemeColors.current

private val SensumShapes = Shapes(
    extraSmall = RoundedCornerShape(SensumRadius.xs),
    small = RoundedCornerShape(SensumRadius.sm),
    medium = RoundedCornerShape(SensumRadius.md),
    large = RoundedCornerShape(SensumRadius.lg),
    extraLarge = RoundedCornerShape(SensumRadius.xl)
)

@Composable
fun SensumTheme(
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (isDark) SensumColors.Dark else SensumColors.Light
    val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()

    CompositionLocalProvider(
        LocalIsDarkTheme provides isDark,
        LocalThemeColors provides colors,
        LocalContentColor provides colors.onBackground
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = sensumTypography(),
            shapes = SensumShapes,
            content = content
        )
    }
}

@Composable
fun letsPlotTheme() =
    if (LocalIsDarkTheme.current) flavorDarcula() else flavorStandard()
