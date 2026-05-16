package si.sensum.gm.services

import si.sensum.shared.models.api.channels.ChannelDto
import si.sensum.sws.client.SmartWebSoapClient
import si.sensum.sws.model.SwsSession

internal class ChannelService(
    private val soapClient: SmartWebSoapClient
) : GmService("GM ChannelService") {

    suspend fun getChannels(
        session: SwsSession,
        stationId: Long
    ): List<ChannelDto> {
        return logged(
            operation = "getChannels",
            details = "stationId=$stationId"
        ) {
            soapClient.getChannels(
                session = session,
                stationId = stationId
            )
        }
    }
}