package si.sensum.demo.screens.data.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.all_inclusive
import si.sensum.demo.resources.dew_point
import si.sensum.demo.resources.distance
import si.sensum.demo.resources.sensors
import si.sensum.demo.screens.data.model.DataEntityType

@Composable
fun DataEntitySelector(
    selected: DataEntityType,
    onSelected: (DataEntityType) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
        DataEntityType.entries.forEach { type ->
            val isSelected = selected == type

            FilterChip(
                selected = isSelected,
                onClick = { onSelected(type) },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.xs)
                    ) {
                        Icon(
                            painter = painterResource(entityIconResource(type)),
                            contentDescription = type.label,
                            tint = if (isSelected) {
                                SensumThemeColors.accent
                            } else {
                                SensumThemeColors.onSurface
                            },
                            modifier = androidx.compose.ui.Modifier.size(SensumSizes.fieldIconSize)
                        )

                        Text(text = type.label)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SensumThemeColors.accentMuted,
                    selectedLabelColor = SensumThemeColors.accent,
                    labelColor = SensumThemeColors.onSurface,
                    containerColor = SensumThemeColors.surface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    selectedBorderColor = SensumThemeColors.accent,
                    borderColor = SensumThemeColors.border
                )
            )
        }
    }
}

fun entityIconResource(
    type: DataEntityType
): DrawableResource {
    return when (type) {
        DataEntityType.ALL -> Res.drawable.all_inclusive
        DataEntityType.STATION -> Res.drawable.distance
        DataEntityType.CHANNEL -> Res.drawable.sensors
        DataEntityType.MEASUREMENT -> Res.drawable.dew_point
    }
}