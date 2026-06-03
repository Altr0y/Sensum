package si.sensum.demo.screens.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.model.DemoChannels
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.model.UiStatus
import si.sensum.demo.screens.data.model.DataActionDialog
import si.sensum.demo.screens.data.model.DataEntityType
import si.sensum.demo.screens.data.model.DataSourceType
import si.sensum.shared.models.common.DataSourceDto
import si.sensum.shared.models.measurements.StationChannelPairDto
import java.time.LocalDateTime
import kotlin.math.roundToInt
import kotlin.random.Random

enum class ChannelSortField {
    ID,
    NAME
}

enum class ChannelSortDirection {
    ASC,
    DESC
}

class DataScreenState(
    private val apiClient: SensumApiClient,
    private val scope: CoroutineScope,
    private val onOpenRecords: (List<MeasurementUi>) -> Unit,
    private val onStatusChange: (UiStatus) -> Unit
) {
    var sourceType by mutableStateOf(DataSourceType.SWS)
    var entityType by mutableStateOf(DataEntityType.MEASUREMENT)
    var singleTimestamp by mutableStateOf(false)
    var previewVisible by mutableStateOf(true)

    var datetimeFrom by mutableStateOf(LocalDateTime.of(2026, 1, 1, 0, 0))
    var datetimeTo by mutableStateOf(LocalDateTime.of(2026, 1, 1, 6, 0))

    var specificChannelsEnabled by mutableStateOf(false)
    var channelSortField by mutableStateOf(ChannelSortField.ID)
    var channelSortDirection by mutableStateOf(ChannelSortDirection.ASC)

    var status by mutableStateOf<UiStatus>(UiStatus.Idle)
        private set

    var dialog by mutableStateOf<DataActionDialog?>(null)
        private set

    val selectedChannels = mutableStateMapOf<Int, Boolean>().apply {
        DemoChannels.names.keys.forEach { channelId ->
            put(channelId, channelId == 127 || channelId == 128)
        }
    }

    var stationIdText by mutableStateOf(DemoChannels.DEFAULT_STATION_ID.toString())
    var stationNameText by mutableStateOf(DemoChannels.DEFAULT_STATION_NAME)
    var stationLatitudeText by mutableStateOf("46.662512")
    var stationLongitudeText by mutableStateOf("16.160122")
    var stationDescriptionText by mutableStateOf("Manual demo station")

    var channelIdText by mutableStateOf("127")
    var channelNameText by mutableStateOf(DemoChannels.nameOf(127))
    var channelUnitText by mutableStateOf("m")
    var channelDescriptionText by mutableStateOf("Manual demo channel")

    var measurementValueText by mutableStateOf("1.25")
    var measurementStatusText by mutableStateOf("0")

    var dslSourceText by mutableStateOf(
        """
        country "Slovenia" {
            station 2241 "Radar test" at (16.160122, 46.662512) {
                channel 127 "Water level" kind water_level unit m {
                    measurement 2026-01-01T00:00:00 value 1.25 status ok;
                }
            }
        }
        """.trimIndent()
    )

    var geoJsonText by mutableStateOf("")
    var sqlText by mutableStateOf("")
    private var lastSqlText by mutableStateOf("")

    val generatedPreview = mutableStateListOf<MeasurementUi>()

    fun dismissDialog() {
        dialog = null
    }

    fun selectAllChannels() {
        DemoChannels.names.keys.forEach { selectedChannels[it] = true }
        updateSqlPreview()
    }

    fun clearChannels() {
        DemoChannels.names.keys.forEach { selectedChannels[it] = false }
        updateSqlPreview()
    }

    fun clearFilters() {
        sourceType = DataSourceType.SWS
        entityType = DataEntityType.STATION
        singleTimestamp = false
        datetimeFrom = LocalDateTime.of(2026, 1, 1, 0, 0)
        datetimeTo = LocalDateTime.of(2026, 1, 1, 6, 0)
        specificChannelsEnabled = false
        channelSortField = ChannelSortField.ID
        channelSortDirection = ChannelSortDirection.ASC
        DemoChannels.names.keys.forEach { selectedChannels[it] = false }
        updateSqlPreview()
    }

    fun openCurrentRecords() {
        onOpenRecords(generatedPreview.toList())
    }

    fun restoreLastSql() {
        sqlText = lastSqlText
    }

    fun updateStatus(nextStatus: UiStatus) {
        status = nextStatus
        onStatusChange(nextStatus)
    }

    fun updateSqlPreview() {
        sqlText = buildSqlPreview()
        lastSqlText = sqlText
    }

    fun runPrimaryAction() {
        updateSqlPreview()

        when (sourceType) {
            DataSourceType.ALL -> showError("Select source", "Choose SWS, DSL, SIM or Manual.")
            DataSourceType.SWS -> refreshFromSws()
            DataSourceType.DSL -> runDslAction()
            DataSourceType.SIM -> runSimulation()
            DataSourceType.MANUAL -> runManualInsert()
        }
    }

    private fun selectedOrAllChannelIdsForSws(): List<Int> {
        return if (specificChannelsEnabled) {
            selectedChannels
                .filterValues { it }
                .keys
                .toList()
        } else {
            DemoChannels.names.keys.toList()
        }
    }

    private fun selectedChannelIdsForLocalActions(): List<Int> {
        return selectedChannels
            .filterValues { it }
            .keys
            .toList()
    }

    private fun refreshFromSws() {
        val stationId = stationIdText.toLongOrNull()

        if (stationId == null) {
            showError("Invalid station", "Station ID must be a number.")
            return
        }

        val channelIds = selectedOrAllChannelIdsForSws()

        if (channelIds.isEmpty()) {
            showError("Missing channels", "Select at least one channel or turn off specific channel filtering.")
            return
        }

        scope.launch {
            updateStatus(UiStatus.Loading)

            runCatching {
                val result = apiClient.refreshMeasurements(
                    stationChannelPairs = channelIds.map { channelId ->
                        StationChannelPairDto(
                            stationId = stationId,
                            channelId = channelId
                        )
                    },
                    datetimeFrom = datetimeFrom.toApiString(),
                    datetimeTo = effectiveTo().toApiString()
                )

                result to apiClient.getMeasurements()
            }.fold(
                onSuccess = { (result, allMeasurements) ->
                    updateStatus(
                        UiStatus.Success(
                            "Inserted ${result.insertedCount}, deleted ${result.deletedCount}"
                        )
                    )

                    dialog = DataActionDialog(
                        title = "SWS refresh",
                        message = "Inserted: ${result.insertedCount}\nDeleted/replaced: ${result.deletedCount}"
                    )

                    generatedPreview.clear()
                    generatedPreview.addAll(allMeasurements.take(100))
                },
                onFailure = { error ->
                    showError("SWS refresh failed", error.message ?: "Unknown API error.")
                }
            )
        }
    }

    private fun runDslAction() {
        val source = dslSourceText.trim()

        if (source.isBlank()) {
            showError("DSL error", "DSL input is empty.")
            return
        }

        scope.launch {
            updateStatus(UiStatus.Loading)

            runCatching {
                val escaped = source
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .lineSequence()
                    .take(12)
                    .joinToString("\\n")

                """
                {
                  "type": "FeatureCollection",
                  "features": [],
                  "properties": {
                    "source": "DSL",
                    "sourcePreview": "$escaped"
                  }
                }
                """.trimIndent()
            }.fold(
                onSuccess = { geoJson ->
                    geoJsonText = geoJson
                    updateStatus(UiStatus.Success("DSL preview prepared"))
                    dialog = DataActionDialog(
                        title = "DSL preview",
                        message = geoJson.take(1200) + if (geoJson.length > 1200) "\n…" else ""
                    )
                },
                onFailure = { error ->
                    showError("DSL parser error", error.message ?: "Invalid DSL input.")
                }
            )
        }
    }

    private fun runSimulation() {
        val stationId = stationIdText.toIntOrNull() ?: DemoChannels.DEFAULT_STATION_ID
        val channelIds = selectedChannelIdsForLocalActions()

        if (
            entityType == DataEntityType.STATION ||
            entityType == DataEntityType.CHANNEL ||
            entityType == DataEntityType.ALL
        ) {
            updateStatus(UiStatus.Success("${entityType.label} simulator prepared"))

            dialog = DataActionDialog(
                title = "Simulator",
                message = "Preview is prepared. Backend create routes for station/channel still need to be connected."
            )

            return
        }

        if (channelIds.isEmpty()) {
            showError("Missing channels", "Select at least one channel for simulated measurements.")
            return
        }

        val measurements = buildSimulatedMeasurements(
            stationId = stationId,
            channelIds = channelIds
        )

        generatedPreview.clear()
        generatedPreview.addAll(measurements.take(100))

        scope.launch {
            updateStatus(UiStatus.Loading)

            runCatching {
                apiClient.createMeasurements(measurements)
                apiClient.getMeasurements()
            }.fold(
                onSuccess = { allMeasurements ->
                    updateStatus(UiStatus.Success("Saved ${measurements.size} simulated measurements"))

                    dialog = DataActionDialog(
                        title = "Simulation completed",
                        message = "Generated and saved ${measurements.size} rows."
                    )

                    onOpenRecords(allMeasurements)
                },
                onFailure = { error ->
                    showError("Simulation failed", error.message ?: "Unknown API error.")
                }
            )
        }
    }

    private fun runManualInsert() {
        when (entityType) {
            DataEntityType.ALL,
            DataEntityType.STATION,
            DataEntityType.CHANNEL -> {
                updateStatus(UiStatus.Success("${entityType.label} form prepared"))

                dialog = DataActionDialog(
                    title = "${entityType.label} form",
                    message = "Fields are ready. Connect backend route before insert."
                )
            }

            DataEntityType.MEASUREMENT -> insertManualMeasurement()
        }
    }

    private fun insertManualMeasurement() {
        val stationId = stationIdText.toIntOrNull()
        val channelId = channelIdText.toIntOrNull()
        val value = measurementValueText.toDoubleOrNull()
        val statusValue = measurementStatusText.toIntOrNull()

        if (stationId == null || channelId == null || value == null || statusValue == null) {
            showError(
                title = "Manual measurement error",
                message = "Station ID, channel ID, value and status must be numeric."
            )
            return
        }

        val measurement = MeasurementUi(
            stationId = stationId,
            stationName = stationNameText.ifBlank { "Station $stationId" },
            channelId = channelId,
            channelName = channelNameText.ifBlank { DemoChannels.nameOf(channelId) },
            dateTime = datetimeFrom,
            value = value,
            status = statusValue,
            source = DataSourceDto.MANUAL
        )

        generatedPreview.clear()
        generatedPreview.add(measurement)

        scope.launch {
            updateStatus(UiStatus.Loading)

            runCatching {
                apiClient.createMeasurement(measurement)
                apiClient.getMeasurements()
            }.fold(
                onSuccess = { allMeasurements ->
                    updateStatus(UiStatus.Success("Manual measurement inserted"))

                    dialog = DataActionDialog(
                        title = "Measurement inserted",
                        message = "station=$stationId\nchannel=$channelId\ntime=$datetimeFrom\nvalue=$value"
                    )

                    onOpenRecords(allMeasurements)
                },
                onFailure = { error ->
                    showError("Manual insert failed", error.message ?: "Unknown API error.")
                }
            )
        }
    }

    private fun buildSimulatedMeasurements(
        stationId: Int,
        channelIds: List<Int>
    ): List<MeasurementUi> {
        val result = mutableListOf<MeasurementUi>()
        var current = datetimeFrom
        val end = effectiveTo()

        while (!current.isAfter(end)) {
            channelIds.forEach { channelId ->
                val base = when (channelId) {
                    131 -> 18.0
                    127,
                    128,
                    130,
                    132,
                    133,
                    134 -> 1.2

                    else -> 5.0
                }

                val noise = Random.nextDouble(-0.18, 0.18)
                val value = ((base + noise) * 1000.0).roundToInt() / 1000.0

                result += MeasurementUi(
                    stationId = stationId,
                    stationName = stationNameText.ifBlank { DemoChannels.DEFAULT_STATION_NAME },
                    channelId = channelId,
                    channelName = DemoChannels.nameOf(channelId),
                    dateTime = current,
                    value = value,
                    status = if (value > base + 0.12) 1 else 0,
                    source = DataSourceDto.SIM
                )
            }

            if (singleTimestamp) {
                break
            }

            current = current.plusHours(1)
        }

        return result
    }

    private fun effectiveTo(): LocalDateTime {
        return if (singleTimestamp) {
            datetimeFrom
        } else {
            datetimeTo
        }
    }

    private fun buildSqlPreview(): String {
        val source = sourceType.label.uppercase()
        val channelComment = if (sourceType == DataSourceType.SWS && !specificChannelsEnabled) {
            "-- channels: all station channels"
        } else {
            "-- channels: ${selectedChannels.filterValues { it }.keys.joinToString(", ").ifBlank { "none" }}"
        }

        return when (entityType) {
            DataEntityType.ALL -> """
                -- target: all
                -- source: $source
                $channelComment
                -- run will apply current form context to stations, channels and measurements where supported
            """.trimIndent()

            DataEntityType.STATION -> """
                INSERT INTO stations (id, alias, longitude, latitude, location_description, source)
                VALUES (${stationIdText.sqlOrNull()}, ${stationNameText.sqlString()}, ${stationLongitudeText.sqlOrNull()}, ${stationLatitudeText.sqlOrNull()}, ${stationDescriptionText.sqlString()}, '$source');
                $channelComment
            """.trimIndent()

            DataEntityType.CHANNEL -> """
                INSERT INTO channels (id, station_id, name, unit, description, source)
                VALUES (${channelIdText.sqlOrNull()}, ${stationIdText.sqlOrNull()}, ${channelNameText.sqlString()}, ${channelUnitText.sqlString()}, ${channelDescriptionText.sqlString()}, '$source');
            """.trimIndent()

            DataEntityType.MEASUREMENT -> """
                INSERT INTO measurements (station_id, channel_id, date_time, value, status, source)
                VALUES (${stationIdText.sqlOrNull()}, ${channelIdText.sqlOrNull()}, '${datetimeFrom.toApiString()}', ${measurementValueText.sqlOrNull()}, ${measurementStatusText.sqlOrNull()}, '$source');
                $channelComment
            """.trimIndent()
        }
    }

    private fun showError(
        title: String,
        message: String
    ) {
        dialog = DataActionDialog(
            title = title,
            message = message,
            isError = true
        )

        updateStatus(UiStatus.Error(message))
    }
}

private fun String.sqlString(): String {
    return "'" + replace("'", "''") + "'"
}

private fun String.sqlOrNull(): String {
    return takeIf { it.isNotBlank() } ?: "NULL"
}