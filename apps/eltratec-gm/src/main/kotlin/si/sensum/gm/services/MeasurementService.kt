package si.sensum.gm.services

import si.sensum.gm.model.MeasurementQuery
import si.sensum.shared.models.api.measurements.MeasurementDto
import si.sensum.sws.SmartWebSoapClient
import si.sensum.sws.model.SwsSession

class MeasurementService(
    private val soapClient: SmartWebSoapClient
) : GmService("GM MeasurementService") {

    suspend fun getMeasurements(
        session: SwsSession,
        query: MeasurementQuery
    ): List<MeasurementDto> {
        return logged(
            operation = "getMeasurements",
            details = "from=${query.datetimeFrom}, to=${query.datetimeTo}"
        ) {
            soapClient.getAllMeasurements(
                session = session,
                datetimeFrom = query.datetimeFrom,
                datetimeTo = query.datetimeTo
            )
        }
    }
}