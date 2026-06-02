package si.sensum.sws.client

import si.sensum.shared.models.stations.StationDto
import si.sensum.sws.model.SwsSession
import si.sensum.sws.operation.SwsOperations
import si.sensum.sws.parser.SwsStationsResponseParser

internal class SwsStationClient(
    private val operationExecutor: SwsOperationExecutor
) {
    suspend fun getStations(session: SwsSession): List<StationDto> {
        return operationExecutor.executeAndParse(
            session = session,
            operation = SwsOperations.simple("GetStations"),
            parser = SwsStationsResponseParser::xmlToStations
        )
    }
}