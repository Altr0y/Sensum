package si.sensum.demo.components.background

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.corners_dark_bg_vector
import si.sensum.demo.resources.corners_light_bg_vector
import si.sensum.demo.resources.logo_dark_bg_vector
import si.sensum.demo.resources.logo_light_bg_vector
import si.sensum.demo.resources.logo_transparent
import si.sensum.demo.resources.network_dark_bg_vector
import si.sensum.demo.resources.network_light_bg_vector
import si.sensum.demo.resources.waves_dark_bg_vector
import si.sensum.demo.resources.waves_light_bg_vector

@Composable
fun AppVectorBackground(
    isDark: Boolean,
    style: AppBackgroundStyle,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    overlay: Boolean = true,
    overlayAlphaDark: Float = 0.18f,
    overlayAlphaLight: Float = 0.30f,
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    if (style == AppBackgroundStyle.LogoGrid) {
        LogoGridBackground(
            isDark = isDark,
            logoResource = Res.drawable.logo_transparent,
            modifier = modifier,
            overlay = overlay,
            darkOverlayAlpha = overlayAlphaDark,
            lightOverlayAlpha = overlayAlphaLight
        ) {
            content?.invoke(this)
        }
        return
    }

    val resource = backgroundResource(
        isDark = isDark,
        style = style
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(resource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )

        if (overlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isDark) {
                            Color.Black.copy(alpha = overlayAlphaDark)
                        } else {
                            Color.White.copy(alpha = overlayAlphaLight)
                        }
                    )
            )
        }

        content?.invoke(this)
    }
}

private fun backgroundResource(
    isDark: Boolean,
    style: AppBackgroundStyle
): DrawableResource {
    return when (style) {
        AppBackgroundStyle.Network -> {
            if (isDark) {
                Res.drawable.network_dark_bg_vector
            } else {
                Res.drawable.network_light_bg_vector
            }
        }

        AppBackgroundStyle.Corners -> {
            if (isDark) {
                Res.drawable.corners_dark_bg_vector
            } else {
                Res.drawable.corners_light_bg_vector
            }
        }

        AppBackgroundStyle.Waves -> {
            if (isDark) {
                Res.drawable.waves_dark_bg_vector
            } else {
                Res.drawable.waves_light_bg_vector
            }
        }

        AppBackgroundStyle.Logo -> {
            if (isDark) {
                Res.drawable.logo_dark_bg_vector
            } else {
                Res.drawable.logo_light_bg_vector
            }
        }

        AppBackgroundStyle.LogoGrid -> {
            error("LogoGrid is drawn with LogoGridBackground, not with a single background resource.")
        }
    }
}