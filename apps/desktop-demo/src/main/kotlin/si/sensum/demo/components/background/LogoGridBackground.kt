package si.sensum.demo.components.background

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun LogoGridBackground(
    isDark: Boolean,
    logoResource: DrawableResource,
    modifier: Modifier = Modifier,
    logoSize: Dp = 50.dp,
    cellSize: Dp = 150.dp,
    logoAlphaDark: Float = 0.05f,
    logoAlphaLight: Float = 0.07f,
    overlay: Boolean = true,
    darkOverlayAlpha: Float = 0.05f,
    lightOverlayAlpha: Float = 0.28f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val columns = (maxWidth / cellSize).toInt() + 2
            val rows = (maxHeight / cellSize).toInt() + 2

            for (row in 0 until rows) {
                for (column in 0 until columns) {
                    Image(
                        painter = painterResource(logoResource),
                        contentDescription = null,
                        modifier = Modifier
                            .offset(
                                x = column * cellSize,
                                y = row * cellSize
                            )
                            .size(logoSize),
                        alpha = if (isDark) logoAlphaDark else logoAlphaLight,
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        if (overlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isDark) {
                            Color.Black.copy(alpha = darkOverlayAlpha)
                        } else {
                            Color.White.copy(alpha = lightOverlayAlpha)
                        }
                    )
            )
        }

        content()
    }
}