package si.sensum.transform

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import si.sensum.shared.models.measurements.MeasurementDto

object JsonConverter {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun jsonToMeasurements(jsonString: String): List<MeasurementDto> {
        return json.decodeFromString(
            ListSerializer(MeasurementDto.serializer()),
            jsonString
        )
    }

    fun measurementsToJson(measurements: List<MeasurementDto>): String {
        return json.encodeToString(
            ListSerializer(MeasurementDto.serializer()),
            measurements
        )
    }
}