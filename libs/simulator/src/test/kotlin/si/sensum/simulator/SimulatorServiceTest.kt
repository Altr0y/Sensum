package si.sensum.simulator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

class SimulatorServiceTest {

    private val service = SimulatorService()

    @Test
    fun `isRealDataAvailable returns true for january interval`() {
        val from = LocalDateTime.of(2026, 1, 10, 0, 0)
        val to = LocalDateTime.of(2026, 1, 10, 1, 0)
        assertTrue(service.isRealDataAvailable(from, to))
    }

    @Test
    fun `isRealDataAvailable returns false for summer interval`() {
        val from = LocalDateTime.of(2026, 6, 1, 0, 0)
        val to = LocalDateTime.of(2026, 6, 1, 1, 0)
        assertFalse(service.isRealDataAvailable(from, to))
    }

    @Test
    fun `generateMeasurements returns correct number of measurements`() {
        val from = LocalDateTime.of(2026, 6, 1, 0, 0)
        val to = LocalDateTime.of(2026, 6, 1, 9, 0) // 10 ur
        val results = service.generateMeasurements(from, to)
        // 10 ur × 9 kanalov = 90 meritev
        assertEquals(90, results.size)
    }

    @Test
    fun `generated values are within absolute bounds`() {
        val from = LocalDateTime.of(2026, 6, 1, 0, 0)
        val to = LocalDateTime.of(2026, 6, 1, 23, 59)
        val results = service.generateMeasurements(from, to)

        // Kanal 127 — globina, meje [2.8315, 3.1525]
        val channel127 = results.filter { it.channelId == 127 }
        channel127.forEach { measurement ->
            assertTrue(measurement.value >= 2.8315) { "Vrednost ${measurement.value} je pod minimumom" }
            assertTrue(measurement.value <= 3.1525) { "Vrednost ${measurement.value} je nad maksimumom" }
        }

        // Kanal 131 — temperatura, meje [10.1, 11.4] za januar
        // Za poletje bo višja zaradi seasonal offset — samo preverimo da ni negativna
        val channel131 = results.filter { it.channelId == 131 }
        channel131.forEach { measurement ->
            assertTrue(measurement.value > 0.0) { "Temperatura ${measurement.value} je negativna" }
        }
    }

    @Test
    fun `constant channel 129 always returns 4_52`() {
        val from = LocalDateTime.of(2026, 3, 1, 0, 0)
        val to = LocalDateTime.of(2026, 3, 1, 1, 0)
        val results = service.generateMeasurements(from, to)

        val channel129 = results.filter { it.channelId == 129 }
        channel129.forEach { measurement ->
            assertEquals(4.52, measurement.value)
        }
    }

    @Test
    fun `print sample generated data`() {
        val from = LocalDateTime.of(2026, 6, 1, 0, 0)
        val to = LocalDateTime.of(2026, 6, 1, 1, 0) // ena ura = 60 meritev na kanal
        val results = service.generateMeasurements(from, to)

        // Izpiši vsak kanal posebej
        results.groupBy { it.channelId }.forEach { (channelId, measurements) ->
            println("\n=== Kanal $channelId ===")
            println("Min: ${measurements.minOf { it.value }}")
            println("Max: ${measurements.maxOf { it.value }}")
            println("Mean: ${measurements.map { it.value }.average()}")
            println("Prvih 5 vrednosti:")
            measurements.take(5).forEach { println("  ${it.dateTime} → ${it.value}") }
        }
    }
}

