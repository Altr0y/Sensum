package si.sensum.sws.client

import si.sensum.shared.models.api.channels.ChannelDto
import si.sensum.sws.model.SwsSession
import si.sensum.sws.model.SwsStationQuery
import si.sensum.sws.operation.SwsOperations
import si.sensum.sws.parser.SwsChannelsResponseParser

internal class SwsChannelClient(
    private val operationExecutor: SwsOperationExecutor
) {
    suspend fun getChannels(
        session: SwsSession,
        stationId: Long
    ): List<ChannelDto> {
        val query = SwsStationQuery(stationId = stationId)

        return operationExecutor.executeAndParse(
            session = session,
            operation = SwsOperations.simple(
                methodName = "GetChannels",
                parameters = query.asParameters()
            ),
            parser = { xml ->
                SwsChannelsResponseParser.xmlToChannels(
                    xml = xml,
                    stationId = stationId
                )
            }
        )
    }
}