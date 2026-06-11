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

@Composable
fun ResourceBackground(
    isDark: Boolean,
    darkResource: DrawableResource,
    lightResource: DrawableResource,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    overlay: Boolean = true,
    darkOverlayAlpha: Float = 0.20f,
    lightOverlayAlpha: Float = 0.36f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(
                if (isDark) darkResource else lightResource
            ),
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