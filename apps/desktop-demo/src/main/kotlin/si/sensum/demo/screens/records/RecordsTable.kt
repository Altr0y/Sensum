package si.sensum.demo.screens.records

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumCard
import si.sensum.demo.components.ui.SensumSourceLabel
import si.sensum.demo.components.ui.SensumTextField
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.arrow_drop_down
import si.sensum.demo.resources.arrow_drop_up
import si.sensum.shared.models.records.ChannelRecordDto
import si.sensum.shared.models.records.MeasurementRecordDto
import si.sensum.shared.models.records.StationRecordDto

@Composable
fun RecordsTable(
    state: DataRecordsState
) {
    SensumCard {
        Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
            when (state.entityType) {
                RecordsEntityType.STATIONS -> StationsTable(state)
                RecordsEntityType.CHANNELS -> ChannelsTable(state)
                RecordsEntityType.MEASUREMENTS -> MeasurementsTable(state)
            }
        }
    }
}

@Composable
private fun StationsTable(
    state: DataRecordsState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SortableHeaderCell("ID", 0.7f, state, "stationId")
        SortableHeaderCell("Name", 1.4f, state, "name")
        SortableHeaderCell("Latitude", 0.9f, state, "latitude")
        SortableHeaderCell("Longitude", 0.9f, state, "longitude")
        SortableHeaderCell("Serial", 1.1f, state, "serialNumber")
        SortableHeaderCell("Source", 0.8f, state, "source")
        HeaderCell("Actions", 1.0f)
    }

    state.stations.forEach { station ->
        StationRow(station)
    }
}

@Composable
private fun StationRow(
    station: StationRecordDto
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(SensumSizes.fieldBorderWidth, SensumThemeColors.border)
            .padding(SensumSizes.tableRowPadding),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyCell(station.stationId.toString(), 0.7f)
        BodyCell(station.name ?: "-", 1.4f)
        BodyCell(station.latitude?.toString() ?: "-", 0.9f)
        BodyCell(station.longitude?.toString() ?: "-", 0.9f)
        BodyCell(station.serialNumber ?: "-", 1.1f)
        SourceCell(station.source.name, 0.8f)
        DisabledActionsCell(1.0f)
    }
}

@Composable
private fun ChannelsTable(
    state: DataRecordsState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SortableHeaderCell("ID", 0.7f, state, "channelId")
        SortableHeaderCell("Station", 0.8f, state, "stationId")
        SortableHeaderCell("Name", 1.8f, state, "name")
        SortableHeaderCell("Unit", 0.7f, state, "unit")
        HeaderCell("Description", 1.4f)
        SortableHeaderCell("Source", 0.8f, state, "source")
        HeaderCell("Actions", 1.0f)
    }

    state.channels.forEach { channel ->
        ChannelRow(channel)
    }
}

@Composable
private fun ChannelRow(
    channel: ChannelRecordDto
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(SensumSizes.fieldBorderWidth, SensumThemeColors.border)
            .padding(SensumSizes.tableRowPadding),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyCell(channel.channelId.toString(), 0.7f)
        BodyCell(channel.stationId.toString(), 0.8f)
        BodyCell(channel.name ?: "-", 1.8f)
        BodyCell(channel.unit ?: "-", 0.7f)
        BodyCell(channel.description ?: "-", 1.4f)
        SourceCell(channel.source.name, 0.8f)
        DisabledActionsCell(1.0f)
    }
}

@Composable
private fun MeasurementsTable(
    state: DataRecordsState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SortableHeaderCell("ID", 0.6f, state, "id")
        SortableHeaderCell("Station", 1.3f, state, "stationId")
        SortableHeaderCell("Channel", 1.5f, state, "channelId")
        SortableHeaderCell("Date/time", 1.3f, state, "dateTime")
        SortableHeaderCell("Value", 0.8f, state, "value")
        SortableHeaderCell("Status", 0.7f, state, "status")
        SortableHeaderCell("Source", 0.8f, state, "source")
        HeaderCell("Actions", 1.0f)
    }

    state.measurements.forEach { measurement ->
        MeasurementRow(
            state = state,
            measurement = measurement
        )
    }
}

@Composable
private fun MeasurementRow(
    state: DataRecordsState,
    measurement: MeasurementRecordDto
) {
    val valueText = remember(measurement.id, measurement.value) {
        mutableStateOf(measurement.value.toString())
    }

    val statusText = remember(measurement.id, measurement.status) {
        mutableStateOf(measurement.status.toString())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(SensumSizes.fieldBorderWidth, SensumThemeColors.border)
            .padding(SensumSizes.tableRowPadding),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyCell(measurement.id?.toString() ?: "-", 0.6f)
        BodyCell("${measurement.stationId} — ${measurement.stationName ?: "-"}", 1.3f)
        BodyCell("${measurement.channelId} — ${measurement.channelName ?: "-"}", 1.5f)
        BodyCell(measurement.dateTime, 1.3f)

        SensumTextField(
            value = valueText.value,
            onValueChange = { valueText.value = it },
            label = "",
            modifier = Modifier.weight(0.8f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        SensumTextField(
            value = statusText.value,
            onValueChange = { statusText.value = it },
            label = "",
            modifier = Modifier.weight(0.7f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        SourceCell(measurement.source.name, 0.8f)

        MeasurementActionsCell(
            state = state,
            measurement = measurement,
            valueText = valueText.value,
            statusText = statusText.value,
            weight = 1.0f
        )
    }
}

@Composable
private fun RowScope.HeaderCell(
    text: String,
    weight: Float
) {
    Row(
        modifier = Modifier.weight(weight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = SensumThemeColors.muted,
            maxLines = 1
        )
    }
}

@Composable
private fun RowScope.SortableHeaderCell(
    text: String,
    weight: Float,
    state: DataRecordsState,
    sortColumn: String
) {
    val sorted = state.sortBy == sortColumn

    Row(
        modifier = Modifier
            .weight(weight)
            .clickable { state.sort(sortColumn) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = SensumThemeColors.accent,
            maxLines = 1
        )

        if (sorted) {
            Icon(
                painter = painterResource(sortIconFor(state.sortDirection)),
                contentDescription = state.sortDirection.apiValue,
                tint = SensumThemeColors.accent,
                modifier = Modifier.size(SensumSizes.recordsSortIconSize)
            )
        }
    }
}

@Composable
private fun RowScope.BodyCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = MaterialTheme.typography.bodySmall,
        color = SensumThemeColors.onSurface,
        maxLines = 1
    )
}

@Composable
private fun RowScope.SourceCell(
    sourceName: String,
    weight: Float
) {
    SensumSourceLabel(
        sourceName = sourceName,
        modifier = Modifier.weight(weight)
    )
}

@Composable
private fun RowScope.DisabledActionsCell(
    weight: Float
) {
    Row(
        modifier = Modifier.weight(weight),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = { },
            enabled = false
        ) {
            Text("Update")
        }

        OutlinedButton(
            onClick = { },
            enabled = false
        ) {
            Text("Delete")
        }
    }
}

@Composable
private fun RowScope.MeasurementActionsCell(
    state: DataRecordsState,
    measurement: MeasurementRecordDto,
    valueText: String,
    statusText: String,
    weight: Float
) {
    Row(
        modifier = Modifier.weight(weight),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {
                state.updateMeasurement(
                    measurement = measurement,
                    valueText = valueText,
                    statusText = statusText
                )
            },
            enabled = measurement.id != null && !state.isLoading
        ) {
            Text("Update")
        }

        OutlinedButton(
            onClick = {
                state.deleteMeasurement(measurement.id)
            },
            enabled = measurement.id != null && !state.isLoading
        ) {
            Text(
                text = "Delete",
                color = SensumThemeColors.error
            )
        }
    }
}

private fun sortIconFor(
    direction: RecordsSortDirection
): DrawableResource {
    return when (direction) {
        RecordsSortDirection.ASC -> Res.drawable.arrow_drop_up
        RecordsSortDirection.DESC -> Res.drawable.arrow_drop_down
    }
}