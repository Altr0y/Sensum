package si.sensum.demo.screens.data.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.datetime.DateTimePicker
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import java.time.LocalDateTime

@Composable
fun DateRangeSection(
    singleTimestamp: Boolean,
    from: LocalDateTime,
    to: LocalDateTime,
    onSingleTimestampChange: (Boolean) -> Unit,
    onCurrentYearSelected: () -> Unit,
    onCurrentMonthSelected: () -> Unit,
    onFromChange: (LocalDateTime) -> Unit,
    onToChange: (LocalDateTime) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md),
            verticalArrangement = Arrangement.spacedBy(SensumSpacing.xs)
        ) {
            DateTimePicker(
                label = if (singleTimestamp) {
                    "Timestamp"
                } else {
                    "From"
                },
                value = from,
                onValueChange = {
                    onFromChange(it)

                    if (singleTimestamp) {
                        onToChange(it)
                    }
                }
            )

            if (!singleTimestamp) {
                DateTimePicker(
                    label = "To",
                    value = to,
                    onValueChange = onToChange
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md),
            verticalArrangement = Arrangement.spacedBy(SensumSpacing.xs)
        ) {
            CompactCheckbox(
                checked = singleTimestamp,
                text = "Single timestamp",
                onCheckedChange = onSingleTimestampChange
            )

            CompactCheckbox(
                checked = isCurrentYearRange(from, to, singleTimestamp),
                text = "Current year",
                onCheckedChange = { checked ->
                    if (checked) {
                        onCurrentYearSelected()
                    }
                }
            )

            CompactCheckbox(
                checked = isCurrentMonthRange(from, to, singleTimestamp),
                text = "Current month",
                onCheckedChange = { checked ->
                    if (checked) {
                        onCurrentMonthSelected()
                    }
                }
            )
        }
    }
}

@Composable
private fun CompactCheckbox(
    checked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(20.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = SensumThemeColors.accent,
                uncheckedColor = SensumThemeColors.muted,
                checkmarkColor = SensumThemeColors.onAccent
            )
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = SensumThemeColors.onSurface
        )
    }
}

private fun isCurrentYearRange(
    from: LocalDateTime,
    to: LocalDateTime,
    singleTimestamp: Boolean
): Boolean {
    if (singleTimestamp) {
        return false
    }

    val now = LocalDateTime.now()
    val start = LocalDateTime.of(now.year, 1, 1, 0, 0)
    val end = LocalDateTime.of(now.year, 12, 31, 23, 59)

    return from == start && to == end
}

private fun isCurrentMonthRange(
    from: LocalDateTime,
    to: LocalDateTime,
    singleTimestamp: Boolean
): Boolean {
    if (singleTimestamp) {
        return false
    }

    val now = LocalDateTime.now()
    val start = LocalDateTime.of(now.year, now.month, 1, 0, 0)
    val end = start.plusMonths(1).minusMinutes(1)

    return from == start && to == end
}