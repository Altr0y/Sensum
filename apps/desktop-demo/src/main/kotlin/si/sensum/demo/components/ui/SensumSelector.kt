package si.sensum.demo.components.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.chevron_down

@Composable
fun <T> SensumSelector(
    label: String,
    selected: T,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = optionLabel(selected),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            trailingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.chevron_down),
                    contentDescription = null,
                    tint = SensumThemeColors.muted
                )
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .heightIn(min = SensumSizes.fieldMinHeight)
                .clickable(enabled) { expanded = true },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SensumThemeColors.accent,
                unfocusedBorderColor = SensumThemeColors.border,
                focusedLabelColor = SensumThemeColors.accent,
                unfocusedLabelColor = SensumThemeColors.muted,
                focusedTextColor = SensumThemeColors.onSurface,
                unfocusedTextColor = SensumThemeColors.onSurface,
                focusedContainerColor = SensumThemeColors.surface,
                unfocusedContainerColor = SensumThemeColors.surface
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
