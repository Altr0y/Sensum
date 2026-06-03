package si.sensum.demo.components.ui

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.pin

@Composable
fun SensumPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    SensumTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = PasswordVisualTransformation(),
        leadingIcon = {
            Icon(
                painter = painterResource(Res.drawable.pin),
                contentDescription = null,
                tint = SensumThemeColors.muted,
                modifier = Modifier.size(SensumSizes.fieldIconSize)
            )
        }
    )
}