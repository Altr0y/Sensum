package si.sensum.gm.services

import si.sensum.shared.models.api.stations.StationDto
import si.sensum.sws.client.SmartWebSoapClient
import si.sensum.sws.model.SwsSession

internal class StationService(
    private val soapClient: SmartWebSoapClient
) : GmService("GM StationService") {

    suspend fun getStations(session: SwsSession): List<StationDto> {
        return logged(operation = "getStations") {
            soapClient.getStations(session)
        }
    }
}