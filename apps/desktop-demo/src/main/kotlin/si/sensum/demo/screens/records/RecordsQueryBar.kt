package si.sensum.demo.screens.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import si.sensum.demo.components.datetime.DateTimePicker
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumIconButton
import si.sensum.demo.components.ui.SensumIconButtonVariant
import si.sensum.demo.components.ui.SensumTextField
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.arrow_drop_left
import si.sensum.demo.resources.arrow_drop_right
import si.sensum.demo.resources.filter_alt_off
import java.time.LocalDateTime

@Composable
fun RecordsQueryBar(
    state: DataRecordsState
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Page ${state.page + 1} / ${state.totalPages} · ${state.totalItems} records",
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.onSurface
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(SensumSpacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SensumIconButton(
                    icon = Res.drawable.arrow_drop_left,
                    contentDescription = "Previous page",
                    onClick = { state.changePage(state.page - 1) },
                    enabled = state.page > 0 && !state.isLoading
                )

                SensumIconButton(
                    icon = Res.drawable.arrow_drop_right,
                    contentDescription = "Next page",
                    onClick = { state.changePage(state.page + 1) },
                    enabled = state.page < state.totalPages - 1 && !state.isLoading
                )
            }

            listOf(10, 50, 100).forEach { size ->
                FilterChip(
                    selected = state.pageSize == size,
                    onClick = { state.changePageSize(size) },
                    label = { Text(size.toString()) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SensumThemeColors.accentMuted,
                        selectedLabelColor = SensumThemeColors.accent,
                        labelColor = SensumThemeColors.onSurface
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = state.pageSize == size,
                        selectedBorderColor = SensumThemeColors.accent,
                        borderColor = SensumThemeColors.border
                    )
                )
            }

            PageJumpField(state)

            RecordsFilterDropdown(state)

            SensumIconButton(
                icon = Res.drawable.filter_alt_off,
                contentDescription = "Reset filters",
                onClick = state::resetFilters,
                enabled = !state.isLoading,
                variant = SensumIconButtonVariant.Primary
            )
        }

        if (state.entityType == RecordsEntityType.MEASUREMENTS) {
            DateRangeRow(state)
        }

        if (state.filters.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
            ) {
                state.filters.forEachIndexed { index, filter ->
                    FilterChip(
                        selected = true,
                        onClick = { state.removeFilter(index) },
                        label = { Text("${filter.field} ${filter.operator.label} ${filter.value}  ×") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SensumThemeColors.surfaceVariant,
                            selectedLabelColor = SensumThemeColors.onSurface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = true,
                            selectedBorderColor = SensumThemeColors.border,
                            borderColor = SensumThemeColors.border
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DateRangeRow(
    state: DataRecordsState
) {
    var from by remember { mutableStateOf(state.datetimeFrom ?: LocalDateTime.now().minusDays(7)) }
    var to by remember { mutableStateOf(state.datetimeTo ?: LocalDateTime.now()) }
    var active by remember { mutableStateOf(state.datetimeFrom != null) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md),
        verticalAlignment = Alignment.Bottom
    ) {
        DateTimePicker(
            label = "From",
            value = from,
            onValueChange = { from = it },
        )

        DateTimePicker(
            label = "To",
            value = to,
            onValueChange = { to = it },
            //compactPopup = true
        )

        SensumButton(
            text = if (active) "Applied" else "Apply range",
            onClick = {
                active = true
                state.changeDateRange(from, to)
            },
            //variant = if (active) SensumButtonVariant.Tinted else SensumButtonVariant.Outline,
            compact = true,
            isLoading = state.isLoading
        )

        if (active) {
            SensumButton(
                text = "Clear range",
                onClick = {
                    active = false
                    state.changeDateRange(null, null)
                },
                variant = SensumButtonVariant.Ghost,
                compact = true
            )
        }
    }
}

@Composable
private fun PageJumpField(
    state: DataRecordsState
) {
    var text by remember { mutableStateOf((state.page + 1).toString()) }

    LaunchedEffect(state.page) {
        text = (state.page + 1).toString()
    }

    SensumTextField(
        value = text,
        onValueChange = { value ->
            if (value.all { it.isDigit() }) {
                text = value
                val pageNumber = value.toIntOrNull()
                if (pageNumber != null) {
                    state.changePage(pageNumber - 1)
                }
            }
        },
        label = "Page",
        modifier = Modifier.width(SensumSizes.recordsPageFieldWidth)
    )
}
