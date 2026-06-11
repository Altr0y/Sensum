package si.sensum.simulator.geo

import si.sensum.geodsl.import.convertFloodZones
import si.sensum.simulator.resources.GeoResourcePaths

object FloodZoneSensumConverter {
    fun convertOftenFloodZonesToSensum(): String {
        return convertFloodZones(
            path = GeoResourcePaths.requireExists(GeoResourcePaths.floodOften).path,
            risk = "often"
        )
    }

    fun convertRareFloodZonesToSensum(): String {
        return convertFloodZones(
            path = GeoResourcePaths.requireExists(GeoResourcePaths.floodRare).path,
            risk = "rare"
        )
    }

    fun convertVeryRareFloodZonesToSensum(): String {
        return convertFloodZones(
            path = GeoResourcePaths.requireExists(GeoResourcePaths.floodVeryRare).path,
            risk = "very_rare"
        )
    }
}