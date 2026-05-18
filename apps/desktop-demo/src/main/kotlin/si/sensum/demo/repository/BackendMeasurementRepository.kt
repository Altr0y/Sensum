package si.sensum.demo.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import si.sensum.demo.model.Measurement
import si.sensum.demo.model.MeasurementRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
private data class BackendMeasurement(
    val id: Long,
    val channelId: Int,
    val dateTime: String,
    val value: Float,
    val status: Boolean
)

class BackendMeasurementRepository(
    private val baseUrl: String = "http://localhost:8082"
) : MeasurementRepository {

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override suspend fun getMeasurements(request: MeasurementRequest): Result<List<Measurement>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val from = request.datetimeFrom.format(formatter)
                val to = request.datetimeTo.format(formatter)

                request.pairs.flatMap { pair ->
                    val response: List<BackendMeasurement> = client.get(
                        "$baseUrl/api/measurements"
                    ) {
                        parameter("channelId", pair.channelId)
                        parameter("from", from)
                        parameter("to", to)
                    }.body()

                    response.map { m ->
                        Measurement(
                            id = m.id.toInt(),
                            stationId = pair.stationId,
                            stationName = "Radar test",
                            channelId = m.channelId,
                            channelName = channelName(m.channelId),
                            dateTime = LocalDateTime.parse(m.dateTime, formatter),
                            value = m.value.toDouble(),
                            status = if (m.status) 1 else 0
                        )
                    }
                }
            }
        }

    suspend fun regenerateAndGet(request: MeasurementRequest): Result<List<Measurement>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val from = request.datetimeFrom.format(formatter)
                val to = request.datetimeTo.format(formatter)

                // Pobriši vse obstoječe simulirane podatke (cel letni razpon)
                client.post("$baseUrl/api/measurements/clear") {
                    parameter("from", "2026-01-01T00:00:00")
                    parameter("to", "2026-12-31T23:00:00")
                }

                // Regeneriraj za zahtevani interval
                client.post("$baseUrl/api/measurements/regenerate") {
                    parameter("from", from)
                    parameter("to", to)
                }

                // Fetchaj rezultate
                request.pairs.flatMap { pair ->
                    val response: List<BackendMeasurement> = client.get(
                        "$baseUrl/api/measurements"
                    ) {
                        parameter("channelId", pair.channelId)
                        parameter("from", from)
                        parameter("to", to)
                    }.body()

                    response.map { m ->
                        Measurement(
                            id = m.id.toInt(),
                            stationId = pair.stationId,
                            stationName = "Radar test",
                            channelId = m.channelId,
                            channelName = channelName(m.channelId),
                            dateTime = LocalDateTime.parse(m.dateTime, formatter),
                            value = m.value.toDouble(),
                            status = if (m.status) 1 else 0
                        )
                    }
                }
            }
        }


    private fun channelName(channelId: Int): String = when (channelId) {
        127 -> "L8001H - globina voda-radar [m]"
        128 -> "L8001H - višina vode [m]"
        129 -> "L8001H - globina vodnjaka (PPI220) [m]"
        130 -> "PPI220 - Nivo [m]"
        131 -> "PPI220 - Temperatura [°C]"
        132 -> "PPI220 - globina vode-nivo [m]"
        133 -> "L8001H - globina vode-nivo [-]"
        134 -> "L8001H - Nivo [-]"
        else -> "Unknown ($channelId)"
    }
}