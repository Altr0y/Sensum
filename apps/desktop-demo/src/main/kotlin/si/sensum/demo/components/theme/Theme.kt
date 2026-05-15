package si.sensum.demo.components.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*

val LocalIsDarkTheme = staticCompositionLocalOf { true }
val LocalThemeColors = staticCompositionLocalOf { SensumColors.Dark }

val SensumThemeColors: SensumColors.Palette
    @Composable
    get() = LocalThemeColors.current
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
            content = content
        )
    }
}
