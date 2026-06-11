package si.sensum.demo.screens.data.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumCard
import si.sensum.demo.components.ui.SensumSelector
import si.sensum.demo.components.ui.SensumTextField
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.arrow_drop_down
import si.sensum.demo.resources.arrow_drop_up
import si.sensum.demo.resources.dew_point
import si.sensum.demo.resources.distance
import si.sensum.demo.resources.sensors
import si.sensum.demo.screens.data.ChannelSortDirection
import si.sensum.demo.screens.data.ChannelSortField
import si.sensum.demo.screens.data.DataScreenState

private val UnitOptions = listOf("m", "°C", "mm", "l/s", "-")
private val StatusOptions = listOf("0", "1", "2", "3")

@Composable
fun StationFields(
    state: DataScreenState
) {
    SensumCard {
        Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
            FormTitle(
                icon = Res.drawable.distance,
                text = "Station"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
                SensumTextField(
                    value = state.stationIdText,
                    onValueChange = {
                        state.stationIdText = it
                        state.updateSqlPreview()
                    },
                    label = "Station ID",
                    modifier = Modifier.weight(1f)
                )

                SensumTextField(
                    value = state.stationNameText,
                    onValueChange = {
                        state.stationNameText = it
                        state.updateSqlPreview()
                    },
                    label = "Name",
                    modifier = Modifier.weight(2f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
                SensumTextField(
                    value = state.stationLongitudeText,
                    onValueChange = {
                        state.stationLongitudeText = it
                        state.updateSqlPreview()
                    },
                    label = "Longitude",
                    modifier = Modifier.weight(1f)
                )

                SensumTextField(
                    value = state.stationLatitudeText,
                    onValueChange = {
                        state.stationLatitudeText = it
                        state.updateSqlPreview()
                    },
                    label = "Latitude",
                    modifier = Modifier.weight(1f)
                )
            }

            SensumTextField(
                value = state.stationDescriptionText,
                onValueChange = {
                    state.stationDescriptionText = it
                    state.updateSqlPreview()
                },
                label = "Description",
                singleLine = false
            )
        }
    }
}

@Composable
fun ChannelFields(
    state: DataScreenState
) {
    SensumCard {
        Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
            FormTitle(
                icon = Res.drawable.sensors,
                text = "Channel"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
                SensumTextField(
                    value = state.channelIdText,
                    onValueChange = {
                        state.channelIdText = it
                        state.updateSqlPreview()
                    },
                    label = "Channel ID",
                    modifier = Modifier.weight(1f)
                )

                SensumTextField(
                    value = state.channelNameText,
                    onValueChange = {
                        state.channelNameText = it
                        state.updateSqlPreview()
                    },
                    label = "Name",
                    modifier = Modifier.weight(2f)
                )

                SensumSelector(
                    label = "Unit",
                    selected = state.channelUnitText,
                    options = UnitOptions,
                    optionLabel = { it },
                    onSelected = {
                        state.channelUnitText = it
                        state.updateSqlPreview()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            SensumTextField(
                value = state.channelDescriptionText,
                onValueChange = {
                    state.channelDescriptionText = it
                    state.updateSqlPreview()
                },
                label = "Description",
                singleLine = false
            )
        }
    }
}

@Composable
fun MeasurementFields(
    state: DataScreenState
) {
    SensumCard {
        Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
            FormTitle(
                icon = Res.drawable.dew_point,
                text = "Measurement"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
                SensumTextField(
                    value = state.measurementValueText,
                    onValueChange = {
                        state.measurementValueText = it
                        state.updateSqlPreview()
                    },
                    label = "Value",
                    modifier = Modifier.weight(1f)
                )

                SensumSelector(
                    label = "Status",
                    selected = state.measurementStatusText,
                    options = StatusOptions,
                    optionLabel = {
                        when (it) {
                            "0" -> "0 · OK"
                            "1" -> "1 · Warning"
                            "2" -> "2 · Critical"
                            "3" -> "3 · Error"
                            else -> it
                        }
                    },
                    onSelected = {
                        state.measurementStatusText = it
                        state.updateSqlPreview()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ChannelSelector(
    state: DataScreenState,
    optional: Boolean = false
) {
    SensumCard {
        Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormTitle(
                    icon = Res.drawable.sensors,
                    text = "Channels from database",
                    modifier = Modifier.weight(1f)
                )

                if (optional) {
                    SpecificChannelsCheckbox(
                        checked = state.specificChannelsEnabled,
                        onCheckedChange = {
                            state.specificChannelsEnabled = it
                            state.updateSqlPreview()
                        }
                    )
                }
            }

            if (!optional || state.specificChannelsEnabled) {
                ChannelSelectorToolbar(state)
                ChannelCheckboxList(state)
            } else {
                Text(
                    text = "Specific channels are disabled. The action will use all channels loaded from the database for the selected station.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SensumThemeColors.muted
                )
            }
        }
    }
}

@Composable
private fun ChannelSelectorToolbar(
    state: DataScreenState
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
    ) {
        SensumButton(
            text = "Load",
            onClick = state::loadUserChannelsForCurrentStation,
            variant = SensumButtonVariant.Secondary,
            compact = true
        )

        SensumButton(
            text = "All",
            onClick = state::selectAllChannels,
            variant = SensumButtonVariant.Secondary,
            compact = true
        )

        SensumButton(
            text = "Clear",
            onClick = state::clearChannels,
            variant = SensumButtonVariant.Outline,
            compact = true
        )

        SortChip(
            text = "ID",
            selected = state.channelSortField == ChannelSortField.ID,
            onClick = {
                state.channelSortField = ChannelSortField.ID
                state.updateSqlPreview()
            }
        )

        SortChip(
            text = "Name",
            selected = state.channelSortField == ChannelSortField.NAME,
            onClick = {
                state.channelSortField = ChannelSortField.NAME
                state.updateSqlPreview()
            }
        )

        SortDirectionChip(
            direction = state.channelSortDirection,
            onClick = {
                state.channelSortDirection = when (state.channelSortDirection) {
                    ChannelSortDirection.ASC -> ChannelSortDirection.DESC
                    ChannelSortDirection.DESC -> ChannelSortDirection.ASC
                }

                state.updateSqlPreview()
            }
        )
    }
}

@Composable
private fun ChannelCheckboxList(
    state: DataScreenState
) {
    val channels = when (state.channelSortField) {
        ChannelSortField.ID -> state.userChannels.sortedBy { channel ->
            channel.channelId
        }

        ChannelSortField.NAME -> state.userChannels.sortedBy { channel ->
            channel.name.orEmpty().lowercase()
        }
    }.let { sorted ->
        when (state.channelSortDirection) {
            ChannelSortDirection.ASC -> sorted
            ChannelSortDirection.DESC -> sorted.reversed()
        }
    }

    if (channels.isEmpty()) {
        Text(
            text = "No channels loaded. Click Load, or first import channels from SWS/DSL/SIM/MANUAL.",
            style = MaterialTheme.typography.bodySmall,
            color = SensumThemeColors.muted
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.xs)) {
        channels.forEach { channel ->
            val id = channel.channelId
            val name = channel.name ?: "Channel $id"
            val unit = channel.unit.orEmpty()
            val selected = state.selectedChannels[id] == true

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
            ) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = { checked ->
                        state.selectChannel(
                            channel = channel,
                            selected = checked
                        )
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SensumThemeColors.accent,
                        uncheckedColor = SensumThemeColors.muted,
                        checkmarkColor = SensumThemeColors.onAccent
                    )
                )

                Text(
                    text = buildString {
                        append(id)
                        append(" — ")
                        append(name)

                        if (unit.isNotBlank()) {
                            append(" [")
                            append(unit)
                            append("]")
                        }
                    },
                    modifier = Modifier.clickable {
                        state.selectChannel(
                            channel = channel,
                            selected = !selected
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = SensumThemeColors.onSurface
                )
            }
        }
    }
}

@Composable
private fun FormTitle(
    icon: DrawableResource,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            tint = SensumThemeColors.onSurface,
            modifier = Modifier.size(SensumSizes.fieldIconSize)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = SensumThemeColors.onSurface
        )
    }
}

@Composable
private fun SpecificChannelsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.xs)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(SensumSizes.fieldIconSize),
            colors = CheckboxDefaults.colors(
                checkedColor = SensumThemeColors.accent,
                uncheckedColor = SensumThemeColors.muted,
                checkmarkColor = SensumThemeColors.onAccent
            )
        )

        Text(
            text = "Specific channels",
            style = MaterialTheme.typography.labelSmall,
            color = SensumThemeColors.onSurface
        )
    }
}

@Composable
private fun SortChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = "Sort: $text",
        modifier = Modifier.clickable {
            onClick()
        },
        style = MaterialTheme.typography.labelSmall,
        color = if (selected) {
            SensumThemeColors.accent
        } else {
            SensumThemeColors.muted
        }
    )
}

@Composable
private fun SortDirectionChip(
    direction: ChannelSortDirection,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable {
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.xs)
    ) {
        Icon(
            painter = painterResource(
                when (direction) {
                    ChannelSortDirection.ASC -> Res.drawable.arrow_drop_up
                    ChannelSortDirection.DESC -> Res.drawable.arrow_drop_down
                }
            ),
            contentDescription = direction.name,
            tint = SensumThemeColors.accent,
            modifier = Modifier.size(SensumSizes.recordsSortIconSize)
        )

        Text(
            text = direction.name,
            style = MaterialTheme.typography.labelSmall,
            color = SensumThemeColors.accent
        )
    }
}