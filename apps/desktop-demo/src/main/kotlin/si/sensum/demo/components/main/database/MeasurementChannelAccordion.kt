package si.sensum.demo.components.main.database

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.chevron_down
import si.sensum.demo.resources.chevron_right
@Composable
fun MeasurementChannelAccordion(
    channelId: Int,
    entries: List<MeasurementUi>,
    onUpdate: (MeasurementUi) -> Unit,
    onDelete: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val channelName = entries.firstOrNull()?.channelName ?: "Channel $channelId"

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (expanded) Res.drawable.chevron_down else Res.drawable.chevron_right
                        ),
                        contentDescription = null,
                        tint = SensumThemeColors.accent,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        "$channelId — $channelName",
                        style = MaterialTheme.typography.titleSmall,
                        color = SensumThemeColors.onSurface
                    )
                }

                Text(
                    "${entries.size} rows",
                    style = MaterialTheme.typography.labelMedium,
                    color = SensumThemeColors.muted
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    MeasurementTableHeader()

                    HorizontalDivider(color = SensumThemeColors.border)

                    entries.forEachIndexed { index, measurement ->
                        EditableMeasurementRow(
                            measurement = measurement,
                            isEven = index % 2 == 0,
                            onUpdate = onUpdate,
                            onDelete = onDelete
                        )
                    }
                }
            }
        }
    }
}