package si.sensum.sws.measurements

import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.sws.SwsSoapExecutor
import si.sensum.sws.model.SwsSession
import si.sensum.sws.model.SwsSoapOperation
import si.sensum.sws.operation.GetAllMeasurementsDetailedOperation
import si.sensum.sws.operation.GetAllMeasurementsOperation
import si.sensum.sws.parser.SwsMeasurementsResponseParser
import si.sensum.sws.operation.GetMeasurementsByStationChannelPairsOperation
import si.sensum.shared.models.api.measurements.StationChannelPairDto

internal class SwsMeasurementsClient(
    private val soapExecutor: SwsSoapExecutor
) {

    suspend fun getAllMeasurements(
        session: SwsSession,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return executeMeasurementsOperation(
            session = session,
            operation = GetAllMeasurementsOperation.create(
                datetimeFrom = datetimeFrom,
                datetimeTo = datetimeTo
            )
        )
    }

    suspend fun getMeasurementsByStationChannelPairs(
        session: SwsSession,
        stationChannelPairs: List<StationChannelPairDto>,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return executeMeasurementsOperation(
            session = session,
            operation = GetMeasurementsByStationChannelPairsOperation.create(
                stationChannelPairs = stationChannelPairs,
                datetimeFrom = datetimeFrom,
                datetimeTo = datetimeTo
            )
        )
    }

    suspend fun getAllMeasurementsDetailed(
        session: SwsSession,
        datetimeFrom: String,
        datetimeTo: String
    ): List<MeasurementDto> {
        return executeMeasurementsOperation(
            session = session,
            operation = GetAllMeasurementsDetailedOperation.create(
                datetimeFrom = datetimeFrom,
                datetimeTo = datetimeTo
            )
        )
    }

    private suspend fun executeMeasurementsOperation(
        session: SwsSession,
        operation: SwsSoapOperation
    ): List<MeasurementDto> {
        val xml = soapExecutor.execute(
            operation = operation,
            session = session
        )

        return SwsMeasurementsResponseParser.xmlToMeasurements(xml)
    }
}