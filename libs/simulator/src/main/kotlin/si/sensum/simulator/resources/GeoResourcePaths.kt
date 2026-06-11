package si.sensum.simulator.resources

import java.io.File

object GeoResourcePaths {
    private val baseDir: File = File(
        System.getenv("SENSUM_GEO_RESOURCES_DIR")
            ?: "resources/geo/geojson"
    )

    val riversDir: File = File(baseDir, "rivers")
    val lakesDir: File = File(baseDir, "lakes")
    val regionsDir: File = File(baseDir, "regions")
    val municipalitiesDir: File = File(baseDir, "municipalities")
    val floodZonesDir: File = File(baseDir, "flood-zones")

    val floodOften: File = File(floodZonesDir, "flood-often.geojson")
    val floodRare: File = File(floodZonesDir, "flood-rare.geojson")
    val floodVeryRare: File = File(floodZonesDir, "flood-very-rare.geojson")

    fun requireExists(file: File): File {
        require(file.exists()) {
            "Geo resource file does not exist: ${file.absolutePath}. " +
                    "Check SENSUM_GEO_RESOURCES_DIR or resources/geo/geojson folder."
        }

        return file
    }

    fun requireDirectoryExists(directory: File): File {
        require(directory.exists() && directory.isDirectory) {
            "Geo resource directory does not exist: ${directory.absolutePath}. " +
                    "Check SENSUM_GEO_RESOURCES_DIR or resources/geo/geojson folder."
        }

        return directory
    }
}