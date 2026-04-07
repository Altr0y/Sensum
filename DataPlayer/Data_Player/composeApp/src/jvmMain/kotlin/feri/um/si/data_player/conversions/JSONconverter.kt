package feri.um.si.data_player.conversions

import feri.um.si.data_player.models.Measurement
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

object JSONconverter {
    private val json = Json{
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun jsonToMeasurements(jsonString: String): List<Measurement>{
        return json.decodeFromString(
            ListSerializer(Measurement.serializer()),
            jsonString
        )
    }

    fun measurementsToJson(measurements: List<Measurement>): String {
        return json.encodeToString(
            ListSerializer(Measurement.serializer()),
            measurements
        )
    }
}