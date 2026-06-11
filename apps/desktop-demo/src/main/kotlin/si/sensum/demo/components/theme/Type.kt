package si.sensum.demo.components.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
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
internal fun jetBrainsMonoFamily(): FontFamily {
    return FontFamily(
        Font(resource = Res.font.jetbrains_mono_thin,      weight = FontWeight.Thin),
        Font(resource = Res.font.jetbrains_mono_light,     weight = FontWeight.Light),
        Font(resource = Res.font.jetbrains_mono_regular,   weight = FontWeight.Normal),
        Font(resource = Res.font.jetbrains_mono_medium,    weight = FontWeight.Medium),
        Font(resource = Res.font.jetbrains_mono_semibold,  weight = FontWeight.SemiBold),
        Font(resource = Res.font.jetbrains_mono_bold,      weight = FontWeight.Bold),
        Font(resource = Res.font.jetbrains_mono_extrabold, weight = FontWeight.ExtraBold),
    )
}

@Composable
internal fun sensumTypography(): Typography {
    val font = jetBrainsMonoFamily()
    return Typography(
        displayLarge   = TextStyle(fontFamily = font, fontWeight = FontWeight.ExtraBold, fontSize = 57.sp),
        displayMedium  = TextStyle(fontFamily = font, fontWeight = FontWeight.ExtraBold, fontSize = 45.sp),
        displaySmall   = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold,      fontSize = 36.sp),
        headlineLarge  = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold,      fontSize = 32.sp),
        headlineMedium = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold,      fontSize = 24.sp),
        headlineSmall  = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold,      fontSize = 20.sp),
        titleLarge     = TextStyle(fontFamily = font, fontWeight = FontWeight.Bold,      fontSize = 20.sp),
        titleMedium    = TextStyle(fontFamily = font, fontWeight = FontWeight.SemiBold,  fontSize = 16.sp),
        titleSmall     = TextStyle(fontFamily = font, fontWeight = FontWeight.SemiBold,  fontSize = 14.sp),
        bodyLarge      = TextStyle(fontFamily = font, fontWeight = FontWeight.Normal,    fontSize = 16.sp),
        bodyMedium     = TextStyle(fontFamily = font, fontWeight = FontWeight.Normal,    fontSize = 14.sp),
        bodySmall      = TextStyle(fontFamily = font, fontWeight = FontWeight.Normal,    fontSize = 12.sp),
        labelLarge     = TextStyle(fontFamily = font, fontWeight = FontWeight.Medium,    fontSize = 14.sp),
        labelMedium    = TextStyle(fontFamily = font, fontWeight = FontWeight.Medium,    fontSize = 12.sp),
        labelSmall     = TextStyle(fontFamily = font, fontWeight = FontWeight.Medium,    fontSize = 11.sp),
    )
}
