package si.sensum.demo.screens.records

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumIconButton
import si.sensum.demo.components.ui.SensumIconButtonVariant
import si.sensum.demo.components.ui.SensumTextField
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.check
import si.sensum.demo.resources.filter_alt
import si.sensum.demo.resources.filter_alt_off
import si.sensum.demo.resources.x

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsFilterDropdown(
    state: DataRecordsState
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedField by remember(state.entityType) {
        mutableStateOf(fieldsFor(state.entityType).first())
    }
    var selectedOperator by remember { mutableStateOf(RecordsFilterOperator.CONTAINS) }
    var value by remember { mutableStateOf("") }

    LaunchedEffect(state.entityType) {
        selectedField = fieldsFor(state.entityType).first()
        selectedOperator = RecordsFilterOperator.CONTAINS
        value = ""
        expanded = false
    }

    LaunchedEffect(selectedField) {
        val operators = operatorsFor(selectedField)

        if (selectedOperator !in operators) {
            selectedOperator = operators.first()
        }
    }

    fun closeDropdown() {
        expanded = false
    }

    fun confirmFilter() {
        val trimmedValue = value.trim()

        if (trimmedValue.isBlank()) {
            return
        }

        state.addFilter(
            RecordsFilter(
                field = selectedField,
                operator = selectedOperator,
                value = trimmedValue
            )
        )

        value = ""
        expanded = false
    }

    Column(
        modifier = Modifier.onPreviewKeyEvent { event ->
            when {
                expanded &&
                        event.key == Key.Escape &&
                        event.type == KeyEventType.KeyDown -> {
                    expanded = false
                    true
                }

                expanded &&
                        (event.key == Key.Enter || event.key == Key.NumPadEnter) &&
                        event.type == KeyEventType.KeyDown -> {
                    confirmFilter()
                    true
                }

                else -> false
            }
        }
    ) {
        Text(
            text = "Filter",
            style = MaterialTheme.typography.labelMedium,
            color = SensumThemeColors.muted
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = if (state.filters.isEmpty()) {
                    "Add filter"
                } else {
                    "Edit filters (${state.filters.size})"
                },
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            if (state.filters.isEmpty()) {
                                Res.drawable.filter_alt
                            } else {
                                Res.drawable.filter_alt_off
                            }
                        ),
                        contentDescription = null,
                        tint = SensumThemeColors.accent,
                        modifier = Modifier.size(SensumSizes.fieldIconSize)
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .width(SensumSizes.recordsFilterMenuWidth),
                shape = MaterialTheme.shapes.small,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SensumThemeColors.accent,
                    unfocusedBorderColor = SensumThemeColors.accent.copy(alpha = 0.65f),
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
                    unfocusedLeadingIconColor = SensumThemeColors.accent,
                    disabledLeadingIconColor = SensumThemeColors.muted.copy(alpha = 0.55f),
                    errorLeadingIconColor = SensumThemeColors.error,

                    focusedTrailingIconColor = SensumThemeColors.muted,
                    unfocusedTrailingIconColor = SensumThemeColors.muted,
                    disabledTrailingIconColor = SensumThemeColors.muted.copy(alpha = 0.55f),
                    errorTrailingIconColor = SensumThemeColors.error
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(SensumSizes.recordsFilterMenuWidth)
                    .background(SensumThemeColors.surface)
                    .border(
                        width = SensumSizes.fieldBorderWidth,
                        color = SensumThemeColors.border,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SensumThemeColors.surface)
                        .padding(SensumSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)
                ) {
                    FilterSectionTitle("Field")

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
                    ) {
                        fieldsFor(state.entityType).forEach { field ->
                            RecordsFilterChip(
                                text = field,
                                selected = selectedField == field,
                                onClick = { selectedField = field }
                            )
                        }
                    }

                    FilterSectionTitle("Operator")

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
                    ) {
                        operatorsFor(selectedField).forEach { operator ->
                            RecordsFilterChip(
                                text = operator.label,
                                selected = selectedOperator == operator,
                                onClick = { selectedOperator = operator }
                            )
                        }
                    }

                    SensumTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = "Value",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SensumIconButton(
                                icon = Res.drawable.x,
                                contentDescription = "Cancel filter",
                                onClick = { closeDropdown() },
                                variant = SensumIconButtonVariant.Danger
                            )

                            SensumIconButton(
                                icon = Res.drawable.check,
                                contentDescription = "Apply filter",
                                onClick = { confirmFilter() },
                                enabled = value.isNotBlank(),
                                variant = SensumIconButtonVariant.Success
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSectionTitle(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = SensumThemeColors.muted
    )
}

@Composable
private fun RecordsFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = SensumThemeColors.surfaceVariant.copy(alpha = 0.35f),
            selectedContainerColor = SensumThemeColors.accentMuted,
            labelColor = SensumThemeColors.onSurface,
            selectedLabelColor = SensumThemeColors.accent,
            disabledContainerColor = SensumThemeColors.surface,
            disabledLabelColor = SensumThemeColors.muted
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = SensumThemeColors.border,
            selectedBorderColor = SensumThemeColors.accent,
            disabledBorderColor = SensumThemeColors.border.copy(alpha = 0.45f)
        )
    )
}

private fun fieldsFor(type: RecordsEntityType): List<String> {
    return when (type) {
        RecordsEntityType.STATIONS -> listOf(
            "stationId",
            "name",
            "latitude",
            "longitude",
            "serialNumber",
            "source"
        )

        RecordsEntityType.CHANNELS -> listOf(
            "channelId",
            "stationId",
            "name",
            "unit",
            "source"
        )

        RecordsEntityType.MEASUREMENTS -> listOf(
            "id",
            "stationId",
            "stationName",
            "channelId",
            "channelName",
            "dateTime",
            "value",
            "status",
            "source"
        )
    }
}

private fun operatorsFor(field: String): List<RecordsFilterOperator> {
    val numericFields = setOf(
        "id",
        "stationId",
        "channelId",
        "latitude",
        "longitude",
        "value",
        "status"
    )

    return if (field in numericFields) {
        listOf(
            RecordsFilterOperator.EQ,
            RecordsFilterOperator.GT,
            RecordsFilterOperator.LT,
            RecordsFilterOperator.GTE,
            RecordsFilterOperator.LTE
        )
    } else {
        listOf(
            RecordsFilterOperator.CONTAINS,
            RecordsFilterOperator.EQ
        )
    }
}