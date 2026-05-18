package si.sensum.demo.repository

import si.sensum.demo.model.Measurement
import si.sensum.demo.model.MeasurementRequest

class SwsMeasurementRepository : MeasurementRepository {

    override suspend fun getMeasurements(request: MeasurementRequest): Result<List<Measurement>> {
        // TODO: DEV-XX — implementirati SWS API klic [Maruša?]
        // Ko bo implementirano, bo ta metoda fetchala dejanske meritve iz SWS
        // za zahtevani interval in kanale.
        return Result.failure(
            NotImplementedError("SWS API klic še ni implementiran. Podatki so na voljo od 1.1.2026 do 2.2.2026.")
        )
    }
}