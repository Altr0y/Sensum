package si.sensum.demo.screens.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.api.toUi
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.model.UiStatus
import si.sensum.demo.screens.data.model.DataActionDialog
import si.sensum.demo.screens.data.model.DataEntityType
import si.sensum.demo.screens.data.model.DataSourceType
import si.sensum.demo.util.DateTimeFormat.toApiString
import si.sensum.shared.models.channels.ChannelDto
import si.sensum.shared.models.common.DataSourceDto
import si.sensum.shared.models.data.DataImportCommand
import si.sensum.shared.models.data.DataImportResult
import si.sensum.shared.models.datetime.ApiDateTime
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.simulator.SimulateRequestDto
import si.sensum.shared.models.simulator.SimulatedStationDto
import si.sensum.shared.models.stations.StationDto
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.sin
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter

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

    var datetimeFrom: LocalDateTime by mutableStateOf(
        LocalDateTime.of(2026, 1, 1, 0, 0)
    )

    var datetimeTo: LocalDateTime by mutableStateOf(
        LocalDateTime.of(2026, 1, 1, 6, 0)
    )

    var intervalMinutesText by mutableStateOf("60")

    var specificChannelsEnabled by mutableStateOf(false)
    var channelSortField by mutableStateOf(ChannelSortField.ID)
    var channelSortDirection by mutableStateOf(ChannelSortDirection.ASC)

    var status by mutableStateOf<UiStatus>(UiStatus.Idle)
        private set

    var dialog by mutableStateOf<DataActionDialog?>(null)
        private set

    val userStations = mutableStateListOf<StationDto>()
    val userChannels = mutableStateListOf<ChannelDto>()

    val selectedChannels = mutableStateMapOf<Int, Boolean>()

    var stationIdText by mutableStateOf("")
    var stationNameText by mutableStateOf("")
    var stationLatitudeText by mutableStateOf("")
    var stationLongitudeText by mutableStateOf("")
    var stationDescriptionText by mutableStateOf("Manual station")

    var channelIdText by mutableStateOf("")
    var channelNameText by mutableStateOf("")
    var channelUnitText by mutableStateOf("m")
    var channelDescriptionText by mutableStateOf("Manual channel")

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

    init {
        updateSqlPreview()
        loadUserStationsAndChannels()
    }

    fun dismissDialog() {
        dialog = null
    }

    fun loadUserStationsAndChannels() {
        scope.launch {
            runCatching {
                val stations = apiClient.getStations()
                stations
            }.onSuccess { stations ->
                userStations.clear()
                userStations.addAll(stations)

                if (stations.isNotEmpty() && stationIdText.isBlank()) {
                    applyStationToInputs(stations.first())
                }

                val stationId = stationIdText.toLongOrNull()
                if (stationId != null) {
                    loadUserChannels(stationId)
                }

                updateSqlPreview()
            }.onFailure { error ->
                showError(
                    title = "Could not load user stations",
                    message = error.message ?: "Stations could not be loaded from database."
                )
            }
        }
    }

    fun loadUserChannelsForCurrentStation() {
        val stationId = stationIdText.toLongOrNull()

        if (stationId == null) {
            showError("Invalid station", "Station ID must be a number.")
            return
        }

        loadUserChannels(stationId)
    }

    private fun loadUserChannels(stationId: Long) {
        scope.launch {
            runCatching {
                apiClient.getChannelsByStation(stationId)
            }.onSuccess { channels ->
                setUserChannels(channels)

                if (channels.isNotEmpty() && channelIdText.isBlank()) {
                    applyChannelToInputs(channels.first())
                }

                updateSqlPreview()
            }.onFailure { error ->
                showError(
                    title = "Could not load user channels",
                    message = error.message ?: "Channels could not be loaded from database."
                )
            }
        }
    }

    private fun setUserChannels(channels: List<ChannelDto>) {
        val sorted = sortChannels(channels)

        userChannels.clear()
        userChannels.addAll(sorted)

        selectedChannels.clear()

        sorted.forEachIndexed { index, channel ->
            selectedChannels[channel.channelId] = index == 0
        }
    }

    private fun sortChannels(channels: List<ChannelDto>): List<ChannelDto> {
        val sorted = when (channelSortField) {
            ChannelSortField.ID -> channels.sortedBy { it.channelId }
            ChannelSortField.NAME -> channels.sortedBy { it.name.orEmpty().lowercase() }
        }

        return when (channelSortDirection) {
            ChannelSortDirection.ASC -> sorted
            ChannelSortDirection.DESC -> sorted.reversed()
        }
    }

    fun selectAllChannels() {
        if (userChannels.isEmpty()) {
            loadUserChannelsForCurrentStation()
            return
        }

        userChannels.forEach { channel ->
            selectedChannels[channel.channelId] = true
        }

        updateSqlPreview()
    }

    fun clearChannels() {
        selectedChannels.keys.toList().forEach { channelId ->
            selectedChannels[channelId] = false
        }

        updateSqlPreview()
    }


    fun selectChannel(
        channel: ChannelDto,
        selected: Boolean
    ) {
        val id = channel.channelId

        selectedChannels[id] = selected
        channelIdText = id.toString()
        channelNameText = channel.name ?: "Channel $id"
        channelUnitText = channel.unit ?: channelUnitText
        channelDescriptionText = channel.description.orEmpty()

        updateSqlPreview()
    }

    fun clearFilters() {
        sourceType = DataSourceType.SWS
        entityType = DataEntityType.STATION
        singleTimestamp = false

        datetimeFrom = LocalDateTime.of(2026, 1, 1, 0, 0)
        datetimeTo = LocalDateTime.of(2026, 1, 1, 6, 0)

        intervalMinutesText = "60"
        specificChannelsEnabled = false
        channelSortField = ChannelSortField.ID
        channelSortDirection = ChannelSortDirection.ASC

        selectedChannels.keys.toList().forEach { channelId ->
            selectedChannels[channelId] = false
        }

        userChannels.firstOrNull()?.let { channel ->
            selectedChannels[channel.channelId] = true
            applyChannelToInputs(channel)
        }

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

    fun importDslFile() {
        chooseDslFile()?.let { path ->
            dslSourceText = java.io.File(path).readText()
            updateSqlPreview()

            dialog = DataActionDialog(
                title = "DSL file loaded",
                message = path,
                isError = false
            )
        }
    }

    private fun chooseDslFile(): String? {
        runCatching {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        }

        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            dialogTitle = "Select Sensum DSL file"
            approveButtonText = "Select"

            fileFilter = FileNameExtensionFilter(
                "Sensum DSL files (*.sensum, *.txt)",
                "sensum",
                "txt"
            )
        }

        val result = chooser.showOpenDialog(null)

        return if (result == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile.absolutePath
        } else {
            null
        }
    }

    fun runPrimaryAction() {
        updateSqlPreview()

        when (sourceType) {
            DataSourceType.SWS -> runSwsAction()
            DataSourceType.DSL -> runDslAction()
            DataSourceType.SIM -> runSimulation()
            DataSourceType.MANUAL -> runManualInsert()
        }
    }

    private fun runSwsAction() {
        when (entityType) {
            DataEntityType.STATION -> importSwsStations()
            DataEntityType.CHANNEL -> importSwsChannels()
            DataEntityType.MEASUREMENT -> importSwsMeasurements()
        }
    }

    private fun importSwsStations() {
        scope.launch {
            updateStatus(UiStatus.Loading)

            runCatching {
                apiClient.importSwsStations()
            }.fold(
                onSuccess = { result ->
                    userStations.clear()
                    userStations.addAll(result.stations)

                    result.stations.firstOrNull()?.let { station ->
                        applyStationToInputs(station)
                        loadUserChannels(station.stationId)
                    }

                    showImportResult(
                        title = "SWS stations imported",
                        result = result
                    )
                },
                onFailure = { error ->
                    showError("SWS station import failed", error.message ?: "Unknown API error.")
                }
            )
        }
    }

    private fun importSwsChannels() {
        val stationId = stationIdText.toLongOrNull()

        if (stationId == null) {
            showError("Invalid station", "Station ID must be a number.")
            return
        }

        scope.launch {
            updateStatus(UiStatus.Loading)

            runCatching {
                apiClient.importSwsChannels(stationId)
            }.fold(
                onSuccess = { result ->
                    setUserChannels(result.channels)

                    result.channels.firstOrNull()?.let { channel ->
                        applyChannelToInputs(channel)
                    }

                    showImportResult(
                        title = "SWS channels imported",
                        result = result
                    )
                },
                onFailure = { error ->
                    showError("SWS channel import failed", error.message ?: "Unknown API error.")
                }
            )
        }
    }

    private fun importSwsMeasurements() {
        val stationId = stationIdText.toLongOrNull()

        if (stationId == null) {
            showError("Invalid station", "Station ID must be a number.")
            return
        }

        scope.launch {
            updateStatus(UiStatus.Loading)

            runCatching {
                val channelIds = resolveSelectedChannelIdsForMeasurements(stationId)

                if (channelIds.isEmpty()) {
                    error("No channels found for station $stationId. Import or load channels first.")
                }

                val results = channelIds.map { channelId ->
                    apiClient.importSwsMeasurements(
                        stationId = stationId,
                        channelId = channelId,
                        datetimeFrom = datetimeFrom.toApiString(),
                        datetimeTo = effectiveTo().toApiString()
                    )
                }

                combineSwsMeasurementResults(results)
            }.fold(
                onSuccess = { result ->
                    showImportResult(
                        title = "SWS measurements imported",
                        result = result
                    )
                },
                onFailure = { error ->
                    showError("SWS measurement import failed", error.message ?: "Unknown API error.")
                }
            )
        }
    }

    private suspend fun resolveSelectedChannelIdsForMeasurements(
        stationId: Long
    ): List<Int> {
        if (userChannels.isEmpty()) {
            val channels = apiClient.getChannelsByStation(stationId)
            setUserChannels(channels)
        }

        return if (specificChannelsEnabled) {
            selectedChannels
                .filterValues { it }
                .keys
                .toList()
        } else {
            userChannels.map { channel ->
                channel.channelId
            }
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
                apiClient.importDsl(source)
            }.fold(
                onSuccess = { result ->
                    geoJsonText = result.geoJson.orEmpty()

                    result.stations.firstOrNull()?.let { station ->
                        applyStationToInputs(station)
                    }

                    if (result.channels.isNotEmpty()) {
                        setUserChannels(result.channels)
                        applyChannelToInputs(result.channels.first())
                    }

                    showImportResult(
                        title = "DSL imported",
                        result = result
                    )
                },
                onFailure = { error ->
                    showError(
                        title = "DSL import failed",
                        message = error.message ?: "Invalid DSL input."
                    )
                }
            )
        }
    }

    private fun runSimulation() {
        val stationId = stationIdText.toIntOrNull()
        val selectedChannelIds = selectedChannelIdsForLocalActions()

        val mode = when (entityType) {
            DataEntityType.STATION -> "stations_only"
            DataEntityType.CHANNEL -> "channels_only"
            DataEntityType.MEASUREMENT -> "full"
        }

        val channelKinds = listOf(
            "water_level",
            "temperature",
            "rainfall",
            "flow_rate"
        )

        val intervalMinutes = intervalMinutesText.toLongOrNull() ?: 60L

        val request = SimulateRequestDto(
            mode = mode,
            count = 3,
            prefix = "SIM Station",
            channelKinds = channelKinds,
            stationId = stationId,
            channelIds = selectedChannelIds,
            from = datetimeFrom.toApiString(),
            to = effectiveTo().toApiString(),
            intervalMinutes = intervalMinutes
        )

        scope.launch {
            updateStatus(UiStatus.Loading)

            runCatching {
                val simulatedStations = apiClient.simulate(request)

                val stations = simulatedStations.toStationDtos()
                val channels = simulatedStations.toChannelDtos()
                val measurements = if (entityType == DataEntityType.MEASUREMENT) {
                    simulatedStations.toGeneratedMeasurementDtos(
                        from = datetimeFrom,
                        to = effectiveTo(),
                        intervalMinutes = intervalMinutes
                    )
                } else {
                    emptyList()
                }

                val command = when (entityType) {
                    DataEntityType.STATION -> DataImportCommand(
                        source = DataSourceDto.SIM,
                        stations = stations
                    )

                    DataEntityType.CHANNEL -> DataImportCommand(
                        source = DataSourceDto.SIM,
                        stations = stations,
                        channels = channels
                    )

                    DataEntityType.MEASUREMENT -> DataImportCommand(
                        source = DataSourceDto.SIM,
                        stations = stations,
                        channels = channels,
                        measurements = measurements
                    )
                }

                apiClient.importSimulation(command)
            }.fold(
                onSuccess = { result ->
                    if (result.stations.isNotEmpty()) {
                        userStations.clear()
                        userStations.addAll(result.stations)
                        applyStationToInputs(result.stations.first())
                    }

                    if (result.channels.isNotEmpty()) {
                        setUserChannels(result.channels)
                        applyChannelToInputs(result.channels.first())
                    }

                    showImportResult(
                        title = "Simulation imported",
                        result = result
                    )
                },
                onFailure = { error ->
                    showError(
                        title = "Simulation failed",
                        message = error.message ?: "Unknown API error."
                    )
                }
            )
        }
    }

    private fun runManualInsert() {
        val command = when (entityType) {
            DataEntityType.STATION -> {
                val station = buildManualStation() ?: return
                DataImportCommand(
                    source = DataSourceDto.MANUAL,
                    stations = listOf(station)
                )
            }

            DataEntityType.CHANNEL -> {
                val station = buildManualStation() ?: return
                val channel = buildManualChannel(station.stationId) ?: return

                DataImportCommand(
                    source = DataSourceDto.MANUAL,
                    stations = listOf(station),
                    channels = listOf(channel)
                )
            }

            DataEntityType.MEASUREMENT -> {
                val station = buildManualStation() ?: return
                val channel = buildManualChannel(station.stationId) ?: return
                val measurement = buildManualMeasurement(station.stationId, channel.channelId) ?: return

                DataImportCommand(
                    source = DataSourceDto.MANUAL,
                    stations = listOf(station),
                    channels = listOf(channel),
                    measurements = listOf(measurement)
                )
            }
        }

        scope.launch {
            updateStatus(UiStatus.Loading)

            runCatching {
                apiClient.importManual(command)
            }.fold(
                onSuccess = { result ->
                    if (result.stations.isNotEmpty()) {
                        userStations.removeAll { station ->
                            result.stations.any { it.stationId == station.stationId }
                        }
                        userStations.addAll(result.stations)
                    }

                    if (result.channels.isNotEmpty()) {
                        setUserChannels(result.channels)
                    }

                    showImportResult(
                        title = "Manual data imported",
                        result = result
                    )
                },
                onFailure = { error ->
                    showError("Manual import failed", error.message ?: "Unknown API error.")
                }
            )
        }
    }

    private fun buildManualStation(): StationDto? {
        val stationId = stationIdText.toLongOrNull()
        val latitude = stationLatitudeText.toDoubleOrNull()
        val longitude = stationLongitudeText.toDoubleOrNull()

        if (stationId == null || latitude == null || longitude == null) {
            showError(
                title = "Manual station error",
                message = "Station ID, latitude and longitude must be numeric."
            )
            return null
        }

        return StationDto(
            stationId = stationId,
            name = stationNameText.ifBlank { "Station $stationId" },
            serialNumber = stationId.toString(),
            description = stationDescriptionText.ifBlank { "Manual station" },
            latitude = latitude,
            longitude = longitude,
            source = DataSourceDto.MANUAL
        )
    }

    private fun buildManualChannel(stationId: Long): ChannelDto? {
        val channelId = channelIdText.toIntOrNull()

        if (channelId == null) {
            showError(
                title = "Manual channel error",
                message = "Channel ID must be numeric."
            )
            return null
        }

        return ChannelDto(
            stationId = stationId,
            channelId = channelId,
            name = channelNameText.ifBlank { "Channel $channelId" },
            unit = channelUnitText.ifBlank { "unknown" },
            description = channelDescriptionText.ifBlank { "Manual channel" },
            source = DataSourceDto.MANUAL
        )
    }

    private fun buildManualMeasurement(
        stationId: Long,
        channelId: Int
    ): MeasurementDto? {
        val value = measurementValueText.toDoubleOrNull()
        val statusValue = measurementStatusText.toIntOrNull()

        if (value == null || statusValue == null) {
            showError(
                title = "Manual measurement error",
                message = "Measurement value and status must be numeric."
            )
            return null
        }

        return MeasurementDto(
            id = null,
            stationId = stationId,
            channelId = channelId,
            dateTime = ApiDateTime.fromAppLocal(datetimeFrom),
            value = value,
            status = statusValue,
            source = DataSourceDto.MANUAL
        )
    }

    private fun selectedChannelIdsForLocalActions(): List<Int> {
        return selectedChannels
            .filterValues { it }
            .keys
            .toList()
    }

    private fun effectiveTo(): LocalDateTime {
        return if (singleTimestamp) {
            datetimeFrom
        } else {
            datetimeTo
        }
    }

    private fun showImportResult(
        title: String,
        result: DataImportResult
    ) {
        updateStatus(
            UiStatus.Success(
                "Stations: ${result.stationCount}, channels: ${result.channelCount}, measurements: ${result.measurementCount}"
            )
        )

        generatedPreview.clear()

        val previewMeasurements = result.measurements.map { measurement ->
            measurement.toUi(
                stations = result.stations,
                channels = result.channels
            )
        }

        generatedPreview.addAll(previewMeasurements)

        dialog = DataActionDialog(
            title = title,
            message = buildString {
                append("Source: ")
                append(result.source)
                append("\nStations: ")
                append(result.stationCount)
                append("\nChannels: ")
                append(result.channelCount)
                append("\nMeasurements: ")
                append(result.measurementCount)
                append("\nUser stations: ")
                append(result.userStationCount)

                if (result.message.isNotBlank()) {
                    append("\n\n")
                    append(result.message)
                }

                if (result.stations.isNotEmpty()) {
                    append("\n\nStations:")
                    result.stations.take(10).forEach { station ->
                        append("\n- ")
                        append(station.stationId)
                        append(" ")
                        append(station.name ?: "Station ${station.stationId}")
                    }
                }

                if (result.channels.isNotEmpty()) {
                    append("\n\nChannels:")
                    result.channels.take(10).forEach { channel ->
                        append("\n- ")
                        append(channel.channelId)
                        append(" ")
                        append(channel.name ?: "Channel ${channel.channelId}")
                    }
                }

                if (result.measurements.isNotEmpty()) {
                    append("\n\nMeasurements preview:")
                    result.measurements.take(10).forEach { measurement ->
                        append("\n- ch=")
                        append(measurement.channelId)
                        append(" time=")
                        append(measurement.dateTime)
                        append(" value=")
                        append(measurement.value)
                    }
                }
            }
        )

        updateSqlPreview()
    }

    private fun combineSwsMeasurementResults(
        results: List<DataImportResult>
    ): DataImportResult {
        return DataImportResult(
            source = DataSourceDto.SWS,
            stationCount = results.sumOf { it.stationCount },
            channelCount = results.sumOf { it.channelCount },
            measurementCount = results.sumOf { it.measurementCount },
            userStationCount = results.sumOf { it.userStationCount },
            stations = results.flatMap { it.stations }.distinctBy { it.stationId },
            channels = results.flatMap { it.channels }.distinctBy { it.channelId },
            measurements = results.flatMap { it.measurements }.take(100),
            geoJson = results.firstNotNullOfOrNull { it.geoJson },
            message = "SWS measurements imported."
        )
    }

    private fun applyStationToInputs(station: StationDto) {
        stationIdText = station.stationId.toString()
        stationNameText = station.name ?: "Station ${station.stationId}"
        stationLatitudeText = station.latitude?.toString() ?: ""
        stationLongitudeText = station.longitude?.toString() ?: ""
        stationDescriptionText = station.description ?: ""
    }

    private fun applyChannelToInputs(channel: ChannelDto) {
        channelIdText = channel.channelId.toString()
        channelNameText = channel.name ?: "Channel ${channel.channelId}"
        channelUnitText = channel.unit ?: "unknown"
        channelDescriptionText = channel.description ?: ""
    }

    private fun buildSqlPreview(): String {
        val source = sourceType.label.uppercase()

        if (sourceType == DataSourceType.DSL) {
            return """
                -- source: DSL
                -- DSL source will be parsed by backend-core
                -- inserted records will use source='DSL'
                POST /api/v1/data/import/dsl
            """.trimIndent()
        }

        val channelComment = if (specificChannelsEnabled) {
            "-- channels from database: ${selectedChannels.filterValues { it }.keys.joinToString(", ").ifBlank { "none" }}"
        } else {
            "-- channels from database: all user/station channels"
        }

        return when (entityType) {
            DataEntityType.STATION -> """
                INSERT INTO stations (id, alias, longitude, latitude, location_description, source)
                VALUES (${stationIdText.sqlOrNull()}, ${stationNameText.sqlString()}, ${stationLongitudeText.sqlOrNull()}, ${stationLatitudeText.sqlOrNull()}, ${stationDescriptionText.sqlString()}, '$source');
            """.trimIndent()

            DataEntityType.CHANNEL -> """
                INSERT INTO channels (id, station_id, name, unit, description, source)
                VALUES (${channelIdText.sqlOrNull()}, ${stationIdText.sqlOrNull()}, ${channelNameText.sqlString()}, ${channelUnitText.sqlString()}, ${channelDescriptionText.sqlString()}, '$source');
                $channelComment
            """.trimIndent()

            DataEntityType.MEASUREMENT -> """
                INSERT INTO measurements (station_id, channel_id, date_time, value, status, source)
                VALUES (${stationIdText.sqlOrNull()}, ${channelIdText.sqlOrNull()}, '${datetimeFrom.toApiString()}', ${measurementValueText.sqlOrNull()}, ${measurementStatusText.sqlOrNull()}, '$source');
                $channelComment
            """.trimIndent()
        }
    }

    fun openGeneratedMeasurementsOnline() {
        if (generatedPreview.isEmpty()) {
            showError("Nothing to open", "Generate, import or refresh measurements first, then open them online.")
            return
        }

        val from: String = URLEncoder.encode(datetimeFrom.toApiString(), Charsets.UTF_8)
        val to: String = URLEncoder.encode(effectiveTo().toApiString(), Charsets.UTF_8)
        val url = "${apiClient.baseUrl.trimEnd('/')}/monitoring?dashboard=measurements&from=$from&to=$to"

        runCatching {
            val desktop: Desktop = Desktop.getDesktop()
            desktop.browse(URI(url))
        }.onFailure { error ->
            showError(
                title = "Could not open browser",
                message = error.message ?: "Unable to launch the system browser for $url"
            )
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

private fun List<SimulatedStationDto>.toStationDtos(): List<StationDto> {
    return map { station ->
        StationDto(
            stationId = station.stationId.toLong(),
            name = station.name,
            description = "Simulated station",
            latitude = station.latitude,
            longitude = station.longitude,
            serialNumber = station.stationId.toString(),
            source = DataSourceDto.SIM
        )
    }
}

private fun List<SimulatedStationDto>.toChannelDtos(): List<ChannelDto> {
    return flatMap { station ->
        station.channels.map { channel ->
            ChannelDto(
                stationId = station.stationId.toLong(),
                channelId = channel.channelId,
                name = channel.name,
                unit = channel.unit,
                description = "Simulated ${channel.kind} channel",
                source = DataSourceDto.SIM
            )
        }
    }
}

private fun List<SimulatedStationDto>.toGeneratedMeasurementDtos(
    from: LocalDateTime,
    to: LocalDateTime,
    intervalMinutes: Long
): List<MeasurementDto> {
    val safeInterval = max(1L, intervalMinutes)
    val result = mutableListOf<MeasurementDto>()

    forEach { station ->
        station.channels.forEach { channel ->
            var current = from
            var index = 0

            while (!current.isAfter(to)) {
                val baseValue = when (channel.kind) {
                    "water_level" -> 1.0
                    "temperature" -> 12.0
                    "rainfall" -> 0.4
                    "flow_rate" -> 15.0
                    else -> 1.0
                }

                val value = baseValue + sin(index.toDouble() / 3.0)

                result.add(
                    MeasurementDto(
                        id = null,
                        stationId = station.stationId.toLong(),
                        channelId = channel.channelId,
                        dateTime = ApiDateTime.fromAppLocal(current),
                        value = value,
                        status = 0,
                        source = DataSourceDto.SIM
                    )
                )

                current = current.plusMinutes(safeInterval)
                index++
            }
        }
    }

    return result
}

private fun String.sqlString(): String {
    return "'" + replace("'", "''") + "'"
}

private fun String.sqlOrNull(): String {
    return takeIf { it.isNotBlank() } ?: "NULL"
}