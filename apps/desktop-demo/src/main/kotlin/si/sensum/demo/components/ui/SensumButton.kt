package si.sensum.demo.components.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import si.sensum.demo.components.theme.SensumRadius
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumThemeColors

enum class SensumButtonVariant {
    Primary,
    Secondary,
    Outline,
    Ghost,
    Danger,
    Success,
    Info
}

@Composable
fun SensumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: SensumButtonVariant = SensumButtonVariant.Primary,
    compact: Boolean = false
) {
    val shape = RoundedCornerShape(SensumRadius.lg)

    val buttonModifier = modifier.heightIn(
        min = if (compact) {
            SensumSizes.buttonCompactHeight
        } else {
            SensumSizes.buttonHeight
        }
    )

    val padding = if (compact) {
        PaddingValues(
            horizontal = SensumSizes.buttonCompactHorizontalPadding,
            vertical = SensumSizes.buttonCompactVerticalPadding
        )
    } else {
        PaddingValues(
            horizontal = SensumSizes.buttonHorizontalPadding,
            vertical = SensumSizes.buttonVerticalPadding
        )
    }

    when (variant) {
        SensumButtonVariant.Primary -> {
            SensumFilledButton(
                text = text,
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                isLoading = isLoading,
                containerColor = SensumThemeColors.accent,
                contentColor = SensumThemeColors.onAccent,
                padding = padding,
                shape = shape
            )
        }

        SensumButtonVariant.Secondary -> {
            SensumFilledButton(
                text = text,
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                isLoading = isLoading,
                containerColor = SensumThemeColors.surfaceVariant,
                contentColor = SensumThemeColors.onSurface,
                padding = padding,
                shape = shape
            )
        }

        SensumButtonVariant.Outline -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled && !isLoading,
                modifier = buttonModifier,
                shape = shape,
                contentPadding = padding,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SensumThemeColors.accent,
                    disabledContentColor = SensumThemeColors.muted
                )
            ) {
                SensumButtonContent(
                    text = text,
                    isLoading = isLoading,
                    contentColor = SensumThemeColors.accent
                )
            }
        }

        SensumButtonVariant.Ghost -> {
            TextButton(
                onClick = onClick,
                enabled = enabled && !isLoading,
                modifier = buttonModifier,
                shape = shape,
                contentPadding = padding,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SensumThemeColors.onSurface,
                    disabledContentColor = SensumThemeColors.muted
                )
            ) {
                SensumButtonContent(
                    text = text,
                    isLoading = isLoading,
                    contentColor = SensumThemeColors.onSurface
                )
            }
        }

        SensumButtonVariant.Danger -> {
            SensumFilledButton(
                text = text,
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                isLoading = isLoading,
                containerColor = SensumThemeColors.error,
                contentColor = SensumThemeColors.onAccent,
                padding = padding,
                shape = shape
            )
        }

        SensumButtonVariant.Success -> {
            SensumFilledButton(
                text = text,
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                isLoading = isLoading,
                containerColor = SensumThemeColors.success,
                contentColor = SensumThemeColors.onAccent,
                padding = padding,
                shape = shape
            )
        }

        SensumButtonVariant.Info -> {
            SensumFilledButton(
                text = text,
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                isLoading = isLoading,
                containerColor = SensumThemeColors.info,
                contentColor = SensumThemeColors.onAccent,
                padding = padding,
                shape = shape
            )
        }
    }
}

@Composable
private fun SensumFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    isLoading: Boolean,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    padding: PaddingValues,
    shape: RoundedCornerShape
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier,
        shape = shape,
        contentPadding = padding,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = SensumThemeColors.accentMuted,
            disabledContentColor = SensumThemeColors.muted
        )
    ) {
        SensumButtonContent(
            text = text,
            isLoading = isLoading,
            contentColor = contentColor
        )
    }
}

@Composable
private fun SensumButtonContent(
    text: String,
    isLoading: Boolean,
    contentColor: androidx.compose.ui.graphics.Color
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(SensumSizes.buttonLoaderSize),
            color = contentColor,
            strokeWidth = SensumSizes.fieldBorderWidth
        )
    } else {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}