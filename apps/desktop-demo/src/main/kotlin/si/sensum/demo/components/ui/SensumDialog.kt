package si.sensum.demo.components.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensumDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String = "Cancel",
    confirmVariant: SensumButtonVariant = SensumButtonVariant.Primary,
    contentPadding: PaddingValues = PaddingValues(SensumSpacing.xl),
    content: (@Composable () -> Unit)? = null
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.widthIn(min = 380.dp, max = 640.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, SensumThemeColors.border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(SensumSpacing.lg)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = SensumThemeColors.onSurface
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SensumThemeColors.onBackground
                )

                content?.invoke()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    SensumButton(
                        text = dismissText,
                        onClick = onDismiss,
                        variant = SensumButtonVariant.Secondary
                    )
                    Spacer(Modifier.width(SensumSpacing.sm))
                    SensumButton(
                        text = confirmText,
                        onClick = onConfirm,
                        variant = confirmVariant
                    )
                }
            }
        }
    }
}
