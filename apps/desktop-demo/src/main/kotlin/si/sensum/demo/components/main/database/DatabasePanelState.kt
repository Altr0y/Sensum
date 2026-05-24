package si.sensum.demo.components.main.database

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.model.UiStatus

class DatabasePanelState(
    private val apiClient: SensumApiClient,
    private val scope: CoroutineScope
) {
    var measurements by mutableStateOf<List<MeasurementUi>>(emptyList())
        private set

    var status by mutableStateOf<UiStatus>(UiStatus.Idle)
        private set

    val isLoading: Boolean
        get() = status is UiStatus.Loading

    fun load() {
        scope.launch {
            status = UiStatus.Loading

            runCatching {
                apiClient.getMeasurements()
            }.fold(
                onSuccess = { loaded ->
                    measurements = loaded
                    status = UiStatus.Success("Loaded from API Gateway.")
                },
                onFailure = { error ->
                    status = UiStatus.Error("API Error: ${error.message}")
                }
            )
        }
    }

    fun saveLoadedData(loadedMeasurements: List<MeasurementUi>) {
        scope.launch {
            status = UiStatus.Loading

            runCatching {
                apiClient.createMeasurements(loadedMeasurements)
            }.fold(
                onSuccess = { count ->
                    status = UiStatus.Success("Saved $count measurements through API Gateway.")
                    load()
                },
                onFailure = { error ->
                    status = UiStatus.Error("API Error: ${error.message}")
                }
            )
        }
    }

    fun update(measurement: MeasurementUi) {
        scope.launch {
            status = UiStatus.Loading

            runCatching {
                apiClient.updateMeasurement(measurement)
            }.fold(
                onSuccess = { saved ->
                    measurements = measurements.map {
                        if (it.id == saved.id) saved else it
                    }
                    status = UiStatus.Success("Updated through API Gateway.")
                },
                onFailure = { error ->
                    status = UiStatus.Error("API Error: ${error.message}")
                }
            )
        }
    }

    fun delete(id: Int) {
        scope.launch {
            status = UiStatus.Loading

            runCatching {
                apiClient.deleteMeasurement(id)
            }.fold(
                onSuccess = {
                    measurements = measurements.filter { it.id != id }
                    status = UiStatus.Success("Deleted through API Gateway.")
                },
                onFailure = { error ->
                    status = UiStatus.Error("API Error: ${error.message}")
                }
            )
        }
    }

    fun deleteAll() {
        scope.launch {
            status = UiStatus.Loading

            runCatching {
                apiClient.deleteAllMeasurements()
            }.fold(
                onSuccess = {
                    measurements = emptyList()
                    status = UiStatus.Success("All measurements deleted through API Gateway.")
                },
                onFailure = { error ->
                    status = UiStatus.Error("API Error: ${error.message}")
                }
            )
        }
    }
}