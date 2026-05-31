package si.sensum.gm.services

import si.sensum.shared.models.measurements.MeasurementRangeQuery
import si.sensum.shared.models.measurements.MeasurementDto
import si.sensum.shared.models.measurements.RefreshMeasurementsCommand
import si.sensum.sws.client.SmartWebSoapClient
import si.sensum.sws.model.SwsSession

internal class MeasurementService(
    private val soapClient: SmartWebSoapClient
) : GmService("GM MeasurementService") {

    suspend fun getAllMeasurements(
        session: SwsSession,
        query: MeasurementRangeQuery
    ): List<MeasurementDto> {
        return logged(
            operation = "getAllMeasurements",
            details = query.details()
        ) {
            soapClient.getAllMeasurements(
                session = session,
                datetimeFrom = query.datetimeFrom,
                datetimeTo = query.datetimeTo
            )
        }
    }

    suspend fun getMeasurementsByStationChannelPairs(
        session: SwsSession,
        request: RefreshMeasurementsCommand
    ): List<MeasurementDto> {
        return logged(
            operation = "getMeasurementsByStationChannelPairs",
            details = request.details()
        ) {
            soapClient.getMeasurementsByStationChannelPairs(
                session = session,
                stationChannelPairs = request.stationChannelPairs,
                datetimeFrom = request.datetimeFrom,
                datetimeTo = request.datetimeTo
            )
        }
    }

    suspend fun getAllMeasurementsDetailed(
        session: SwsSession,
        query: MeasurementRangeQuery
    ): List<MeasurementDto> {
        return logged(
            operation = "getAllMeasurementsDetailed",
            details = query.details()
        ) {
            soapClient.getAllMeasurementsDetailed(
                session = session,
                datetimeFrom = query.datetimeFrom,
                datetimeTo = query.datetimeTo
            )
        }
    }

    suspend fun getStationMeasurements(
        session: SwsSession,
        stationId: Long,
        query: MeasurementRangeQuery
    ): List<MeasurementDto> {
        return logged(
            operation = "getStationMeasurements",
            details = "stationId=$stationId ${query.details()}"
        ) {
            soapClient.getStationMeasurements(
                session = session,
                stationId = stationId,
                datetimeFrom = query.datetimeFrom,
                datetimeTo = query.datetimeTo
            )
        }
    }

    suspend fun getStationMeasurementsDetailed(
        session: SwsSession,
        stationId: Long,
        query: MeasurementRangeQuery
    ): List<MeasurementDto> {
        return logged(
            operation = "getStationMeasurementsDetailed",
            details = "stationId=$stationId ${query.details()}"
        ) {
            soapClient.getStationMeasurementsDetailed(
                session = session,
                stationId = stationId,
                datetimeFrom = query.datetimeFrom,
                datetimeTo = query.datetimeTo
            )
        }
    }

    suspend fun getChannelMeasurements(
        session: SwsSession,
        stationId: Long,
        channelId: Int,
        query: MeasurementRangeQuery
    ): List<MeasurementDto> {
        return logged(
            operation = "getChannelMeasurements",
            details = "stationId=$stationId channelId=$channelId ${query.details()}"
        ) {
            soapClient.getChannelMeasurements(
                session = session,
                stationId = stationId,
                channelId = channelId,
                datetimeFrom = query.datetimeFrom,
                datetimeTo = query.datetimeTo
            )
        }
    }

    suspend fun getChannelMeasurementsDetailed(
        session: SwsSession,
        stationId: Long,
        channelId: Int,
        query: MeasurementRangeQuery
    ): List<MeasurementDto> {
        return logged(
            operation = "getChannelMeasurementsDetailed",
            details = "stationId=$stationId channelId=$channelId ${query.details()}"
        ) {
            soapClient.getChannelMeasurementsDetailed(
                session = session,
                stationId = stationId,
                channelId = channelId,
                datetimeFrom = query.datetimeFrom,
                datetimeTo = query.datetimeTo
            )
        }
    }

    private fun MeasurementRangeQuery.details(): String {
        return "from=$datetimeFrom to=$datetimeTo"
    }

    private fun RefreshMeasurementsCommand.details(): String {
        return "pairs=${stationChannelPairs.size} from=$datetimeFrom to=$datetimeTo"
    }
}