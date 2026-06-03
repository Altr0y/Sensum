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
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.sourceIconResource
import si.sensum.demo.screens.data.model.DataSourceType

@Composable
fun DataSourceTabs(
    selected: DataSourceType,
    onSelected: (DataSourceType) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
        DataSourceType.entries.forEach { type ->
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
                            painter = painterResource(sourceIconResource(type.name)),
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
                    labelColor = SensumThemeColors.onSurface
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