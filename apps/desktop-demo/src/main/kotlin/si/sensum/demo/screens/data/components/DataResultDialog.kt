package si.sensum.demo.screens.data.components

import androidx.compose.runtime.Composable
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumDialog
import si.sensum.demo.screens.data.model.DataActionDialog

@Composable
fun DataResultDialog(
    dialog: DataActionDialog?,
    onDismiss: () -> Unit
) {
    if (dialog == null) return

    SensumDialog(
        title = dialog.title,
        message = dialog.message,
        confirmText = "OK",
        dismissText = "Close",
        confirmVariant = if (dialog.isError) SensumButtonVariant.Danger else SensumButtonVariant.Primary,
        onConfirm = onDismiss,
        onDismiss = onDismiss
    )
}
