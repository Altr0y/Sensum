package si.sensum.demo.components.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumThemeColors

enum class SensumIconButtonVariant {
    Default,
    Primary,
    Danger,
    Success,
    Info
}

@Composable
fun SensumIconButton(
    icon: DrawableResource,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: SensumIconButtonVariant = SensumIconButtonVariant.Default
) {
    val iconColor = when (variant) {
        SensumIconButtonVariant.Default -> SensumThemeColors.muted
        SensumIconButtonVariant.Primary -> SensumThemeColors.accent
        SensumIconButtonVariant.Danger -> SensumThemeColors.error
        SensumIconButtonVariant.Success -> SensumThemeColors.success
        SensumIconButtonVariant.Info -> SensumThemeColors.info
    }

    Surface(
        modifier = modifier.size(SensumSizes.iconButtonSize),
        shape = RoundedCornerShape(SensumSizes.iconButtonRadius),
        color = SensumThemeColors.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(SensumSizes.fieldBorderWidth, SensumThemeColors.border)
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = iconColor,
                disabledContentColor = SensumThemeColors.border
            )
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(SensumSizes.iconButtonIconSize)
            )
        }
    }
}