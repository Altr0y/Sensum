package si.sensum.sws.client

import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.StationChannelPairDto
import si.sensum.sws.model.SwsChannelQuery
import si.sensum.sws.model.SwsDateTimeRange
import si.sensum.sws.model.SwsSession
import si.sensum.sws.model.SwsSoapOperation
import si.sensum.sws.model.SwsStationMeasurementQuery
import si.sensum.sws.operation.SwsOperations
import si.sensum.sws.operation.measurements.GetMeasurementsByStationChannelPairsOperation
import si.sensum.sws.parser.SwsMeasurementsResponseParser

internal class SwsMeasurementClient(
    private val operationExecutor: SwsOperationExecutor
) {
    suspend fun getAllMeasurements(
        session: SwsSession,
        range: SwsDateTimeRange
    ): List<MeasurementDto> {
        return executeMeasurementOperation(
            session = session,
            operation = SwsOperations.simple(
                methodName = "GetAllMeasurements",
                parameters = range.asParameters()
            )
        )
    }

    suspend fun getAllMeasurementsDetailed(
        session: SwsSession,
        range: SwsDateTimeRange
    ): List<MeasurementDto> {
        return executeMeasurementOperation(
            session = session,
            operation = SwsOperations.simple(
                methodName = "GetAllMeasurements_Detailed",
                parameters = range.asParameters()
            )
        )
    }

    suspend fun getMeasurementsByStationChannelPairs(
        session: SwsSession,
        stationChannelPairs: List<StationChannelPairDto>,
        range: SwsDateTimeRange
    ): List<MeasurementDto> {
        return executeMeasurementOperation(
            session = session,
            operation = GetMeasurementsByStationChannelPairsOperation.create(
                stationChannelPairs = stationChannelPairs,
                range = range
            )
        )
    }

    suspend fun getStationMeasurements(
        session: SwsSession,
        query: SwsStationMeasurementQuery
    ): List<MeasurementDto> {
        return executeMeasurementOperation(
            session = session,
            operation = SwsOperations.simple(
                methodName = "GetStationMeasurements",
                parameters = query.asParameters()
            )
        )
    }

    suspend fun getStationMeasurementsDetailed(
        session: SwsSession,
        query: SwsStationMeasurementQuery
    ): List<MeasurementDto> {
        return executeMeasurementOperation(
            session = session,
            operation = SwsOperations.simple(
                methodName = "GetStationMeasurements_Detailed",
                parameters = query.asParameters()
            )
        )
    }

    suspend fun getChannelMeasurements(
        session: SwsSession,
        query: SwsChannelQuery
    ): List<MeasurementDto> {
        return executeMeasurementOperation(
            session = session,
            operation = SwsOperations.simple(
                methodName = "GetChannelMeasurements",
                parameters = query.asParameters()
            )
        )
    }

    suspend fun getChannelMeasurementsDetailed(
        session: SwsSession,
        query: SwsChannelQuery
    ): List<MeasurementDto> {
        return executeMeasurementOperation(
            session = session,
            operation = SwsOperations.simple(
                methodName = "GetChannelMeasurements_Detailed",
                parameters = query.asParameters()
            )
        )
    }

    private suspend fun executeMeasurementOperation(
        session: SwsSession,
        operation: SwsSoapOperation
    ): List<MeasurementDto> {
        return operationExecutor.executeAndParse(
            session = session,
            operation = operation,
            parser = SwsMeasurementsResponseParser::xmlToMeasurements
        )
    }
}