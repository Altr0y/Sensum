package si.sensum.demo.screens.records

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.model.UiStatus
import si.sensum.demo.util.DateTimeFormat.toApiString
import si.sensum.shared.models.records.ChannelRecordDto
import si.sensum.shared.models.records.MeasurementRecordDto
import si.sensum.shared.models.records.StationRecordDto
import java.time.LocalDateTime

class DataRecordsState(
    private val apiClient: SensumApiClient,
    private val scope: CoroutineScope
) {
    var entityType by mutableStateOf(RecordsEntityType.MEASUREMENTS)
        private set

    var status by mutableStateOf<UiStatus>(UiStatus.Idle)
        private set

    var page by mutableStateOf(0)
        private set

    var pageSize by mutableStateOf(10)
        private set

    var totalItems by mutableStateOf(0)
        private set

    var totalPages by mutableStateOf(1)
        private set

    var sortBy by mutableStateOf("dateTime")
        private set

    var sortDirection by mutableStateOf(RecordsSortDirection.ASC)
        private set

    val filters = mutableStateListOf<RecordsFilter>()

    /** Date range applied as a pre-filter on the measurements entity. */
    var datetimeFrom by mutableStateOf<LocalDateTime?>(null)
    var datetimeTo by mutableStateOf<LocalDateTime?>(null)

    var stations by mutableStateOf<List<StationRecordDto>>(emptyList())
        private set

    var channels by mutableStateOf<List<ChannelRecordDto>>(emptyList())
        private set

    var measurements by mutableStateOf<List<MeasurementRecordDto>>(emptyList())
        private set

    val isLoading: Boolean
        get() = status is UiStatus.Loading

    fun changeEntityType(type: RecordsEntityType) {
        entityType = type
        page = 0
        sortBy = when (type) {
            RecordsEntityType.STATIONS -> "stationId"
            RecordsEntityType.CHANNELS -> "channelId"
            RecordsEntityType.MEASUREMENTS -> "dateTime"
        }
        sortDirection = RecordsSortDirection.ASC
        filters.clear()
        load()
    }

    fun changeDateRange(from: LocalDateTime?, to: LocalDateTime?) {
        datetimeFrom = from
        datetimeTo = to
        page = 0
        load()
    }

    fun changePageSize(size: Int) {
        pageSize = size
        page = 0
        load()
    }

    fun changePage(newPage: Int) {
        page = newPage.coerceIn(0, (totalPages - 1).coerceAtLeast(0))
        load()
    }

    fun sort(column: String) {
        if (sortBy == column) {
            sortDirection = sortDirection.toggled()
        } else {
            sortBy = column
            sortDirection = RecordsSortDirection.ASC
        }

        page = 0
        load()
    }

    fun addFilter(filter: RecordsFilter) {
        filters.add(filter)
        page = 0
        load()
    }

    fun removeFilter(index: Int) {
        if (index in filters.indices) {
            filters.removeAt(index)
            page = 0
            load()
        }
    }

    fun resetFilters() {
        filters.clear()
        datetimeFrom = null
        datetimeTo = null
        page = 0
        pageSize = 10
        sortBy = when (entityType) {
            RecordsEntityType.STATIONS -> "stationId"
            RecordsEntityType.CHANNELS -> "channelId"
            RecordsEntityType.MEASUREMENTS -> "dateTime"
        }
        sortDirection = RecordsSortDirection.ASC
        load()
    }

    fun load() {
        scope.launch {
            status = UiStatus.Loading

            runCatching {
                when (entityType) {
                    RecordsEntityType.STATIONS -> {
                        val result = apiClient.getStationRecords(
                            page = page,
                            pageSize = pageSize,
                            sortBy = sortBy,
                            sortDirection = sortDirection,
                            filters = filters
                        )

                        stations = result.items
                        applyPageInfo(
                            page = result.page,
                            pageSize = result.pageSize,
                            totalItems = result.totalItems,
                            totalPages = result.totalPages
                        )
                    }

                    RecordsEntityType.CHANNELS -> {
                        val result = apiClient.getChannelRecords(
                            page = page,
                            pageSize = pageSize,
                            sortBy = sortBy,
                            sortDirection = sortDirection,
                            filters = filters
                        )

                        channels = result.items
                        applyPageInfo(
                            page = result.page,
                            pageSize = result.pageSize,
                            totalItems = result.totalItems,
                            totalPages = result.totalPages
                        )
                    }

                    RecordsEntityType.MEASUREMENTS -> {
                        val dateFilters = buildList {
                            datetimeFrom?.let { add(RecordsFilter("dateTime", RecordsFilterOperator.GTE, it.toApiString())) }
                            datetimeTo?.let { add(RecordsFilter("dateTime", RecordsFilterOperator.LTE, it.toApiString())) }
                        }
                        val result = apiClient.getMeasurementRecords(
                            page = page,
                            pageSize = pageSize,
                            sortBy = sortBy,
                            sortDirection = sortDirection,
                            filters = dateFilters + filters
                        )

                        measurements = result.items
                        applyPageInfo(
                            page = result.page,
                            pageSize = result.pageSize,
                            totalItems = result.totalItems,
                            totalPages = result.totalPages
                        )
                    }
                }
            }.fold(
                onSuccess = {
                    status = UiStatus.Success("Loaded $totalItems records.")
                },
                onFailure = { error ->
                    status = UiStatus.Error(error.message ?: "Records load failed.")
                }
            )
        }
    }

    fun updateMeasurement(
        measurement: MeasurementRecordDto,
        valueText: String,
        statusText: String
    ) {
        val id = measurement.id ?: return
        val newValue = valueText.toDoubleOrNull() ?: return
        val newStatus = statusText.toIntOrNull() ?: return

        scope.launch {
            status = UiStatus.Loading

            runCatching {
                val current = apiClient.getMeasurements()
                    .first { it.id == id.toInt() }

                apiClient.updateMeasurement(
                    current.copy(
                        value = newValue,
                        status = newStatus
                    )
                )
            }.fold(
                onSuccess = {
                    status = UiStatus.Success("Measurement updated.")
                    load()
                },
                onFailure = { error ->
                    status = UiStatus.Error(error.message ?: "Update failed.")
                }
            )
        }
    }

    fun deleteMeasurement(id: Long?) {
        if (id == null) {
            return
        }

        scope.launch {
            status = UiStatus.Loading

            runCatching {
                apiClient.deleteMeasurement(id.toInt())
            }.fold(
                onSuccess = {
                    status = UiStatus.Success("Measurement deleted.")
                    load()
                },
                onFailure = { error ->
                    status = UiStatus.Error(error.message ?: "Delete failed.")
                }
            )
        }
    }

    private fun applyPageInfo(
        page: Int,
        pageSize: Int,
        totalItems: Int,
        totalPages: Int
    ) {
        this.page = page
        this.pageSize = pageSize
        this.totalItems = totalItems
        this.totalPages = totalPages
    }
}