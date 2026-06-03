package si.sensum.demo.components.ui

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumThemeColors

@Composable
fun SensumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            if (label.isNotBlank()) {
                Text(label)
            }
        },
        singleLine = singleLine,
        enabled = enabled,
        isError = isError,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.heightIn(min = SensumSizes.fieldMinHeight),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SensumThemeColors.accent,
            unfocusedBorderColor = SensumThemeColors.accent.copy(alpha = 0.55f),
            disabledBorderColor = SensumThemeColors.border.copy(alpha = 0.45f),
            errorBorderColor = SensumThemeColors.error,

            focusedLabelColor = SensumThemeColors.accent,
            unfocusedLabelColor = SensumThemeColors.muted,
            disabledLabelColor = SensumThemeColors.muted.copy(alpha = 0.55f),
            errorLabelColor = SensumThemeColors.error,

            cursorColor = SensumThemeColors.accent,
            errorCursorColor = SensumThemeColors.error,

            focusedTextColor = SensumThemeColors.onSurface,
            unfocusedTextColor = SensumThemeColors.onSurface,
            disabledTextColor = SensumThemeColors.muted.copy(alpha = 0.65f),
            errorTextColor = SensumThemeColors.onSurface,

            focusedContainerColor = SensumThemeColors.surface,
            unfocusedContainerColor = SensumThemeColors.surface,
            disabledContainerColor = SensumThemeColors.surface.copy(alpha = 0.65f),
            errorContainerColor = SensumThemeColors.surface,

            focusedLeadingIconColor = SensumThemeColors.accent,
            unfocusedLeadingIconColor = SensumThemeColors.muted,
            disabledLeadingIconColor = SensumThemeColors.muted.copy(alpha = 0.55f),
            errorLeadingIconColor = SensumThemeColors.error,

            focusedTrailingIconColor = SensumThemeColors.accent,
            unfocusedTrailingIconColor = SensumThemeColors.muted,
            disabledTrailingIconColor = SensumThemeColors.muted.copy(alpha = 0.55f),
            errorTrailingIconColor = SensumThemeColors.error
        )
    )
}