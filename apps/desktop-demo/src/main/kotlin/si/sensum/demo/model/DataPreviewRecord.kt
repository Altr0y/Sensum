package si.sensum.demo.model

import si.sensum.demo.api.toMeasurementUi
import si.sensum.shared.models.channels.ChannelDto
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.stations.StationDto

sealed interface DataPreviewRecord {
    data class Station(
        val value: StationDto
    ) : DataPreviewRecord

    data class Channel(
        val value: ChannelDto
    ) : DataPreviewRecord

    data class Measurement(
        val value: MeasurementUi
    ) : DataPreviewRecord

    companion object {
        fun fromStation(value: StationDto): DataPreviewRecord {
            return Station(value)
        }

        fun fromChannel(value: ChannelDto): DataPreviewRecord {
            return Channel(value)
        }

        fun fromMeasurement(value: MeasurementDto): DataPreviewRecord {
            return Measurement(value.toMeasurementUi())
        }
    }
}