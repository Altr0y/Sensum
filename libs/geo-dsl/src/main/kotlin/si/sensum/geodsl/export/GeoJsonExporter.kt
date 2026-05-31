package si.sensum.geodsl.export

import si.sensum.geodsl.ast.FloodZoneNode
import si.sensum.geodsl.ast.LakeNode
import si.sensum.geodsl.ast.MunicipalityNode
import si.sensum.geodsl.ast.PointNode
import si.sensum.geodsl.ast.ProgramNode
import si.sensum.geodsl.ast.RegionNode
import si.sensum.geodsl.ast.RiverNode
import si.sensum.geodsl.ast.StationNode

fun export(program: ProgramNode): String {
    val features = mutableListOf<String>()

    for(country in program.countries) {
        for(layer in country.layers) {
            for(element in layer.elements) {
                when(element){
                    is RiverNode -> features.add(exportRiver(element, layer.name))
                    is LakeNode -> features.add(exportLake(element, layer.name))
                    is FloodZoneNode -> features.add(exportFloodZone(element, layer.name))
                    is RegionNode -> features.add(exportRegion(element, layer.name))
                }
            }
        }
        for (element in country.geoElements) {
            when (element) {
                is RiverNode     -> features.add(exportRiver(element, ""))
                is LakeNode      -> features.add(exportLake(element, ""))
                is FloodZoneNode -> features.add(exportFloodZone(element, ""))
                is RegionNode    -> features.add(exportRegion(element, ""))
            }
        }
        for (municipality in country.municipalities) {
            features.add(exportMunicipality(municipality))
        }
        for(station in country.stations) {
            features.add(exportStation(station))
        }
    }
    return """
        {
            "type": "FeatureCollection",
            "features": [
                ${features.joinToString(",\n")}
            ]
        }
    """.trimIndent()
}

private fun exportRiver(river: RiverNode, layerName: String) : String {
    val layerProp = if (layerName.isNotEmpty()) """
                "layer": "${escape(layerName)}"""" else ""

    return """
        {
            "type":"Feature",
            "properties":{
                "name": "${escape(river.name)}",
                "type": "river"$layerProp
            },
            "geometry":{
                "type": "LineString",
                "coordinates": ${lineCoordinates(river.points)}
            }
        }
    """.trimIndent()
}

private fun exportLake(lake: LakeNode, layerName: String) : String {
    val layerProp = if (layerName.isNotEmpty()) """
                "layer": "${escape(layerName)}"""" else ""

    return """
        {
            "type":"Feature",
            "properties":{
                "name": "${escape(lake.name)}",
                "type": "lake"$layerProp
            },
            "geometry":{
                "type": "Polygon",
                "coordinates": ${polygonCoordinates(lake.points)}
            }
        }
    """.trimIndent()
}

private fun exportRegion (region: RegionNode, layerName: String) : String {
    val layerProp = if (layerName.isNotEmpty()) """
                "layer": "${escape(layerName)}"""" else ""

    return """
        {
            "type": "Feature",
            "properties": {
                "name": "${escape(region.name)}",
                "type": "region"$layerProp
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": ${polygonCoordinates(region.points)}
            }
        }
    """.trimIndent()
}

private fun exportFloodZone (floodZone: FloodZoneNode, layerName: String) : String {
    val layerProp = if (layerName.isNotEmpty()) """
                "layer": "${escape(layerName)}"""" else ""

    return """
        {
            "type": "Feature",
            "properties": {
                "name": "${escape(floodZone.name)}",
                "type": "flood_zone",
                "risk": "${escape(floodZone.risk)}"$layerProp
            },
            "geometry": {
                "type": "Polygon",
                "coordinates": ${polygonCoordinates(floodZone.points)}
            }
        }
    """.trimIndent()
}

private fun exportMunicipality (municipality: MunicipalityNode): String {

    return """
    {
        "type": "Feature",
        "properties": {
            "name": "${escape(municipality.name)}",
            "type": "municipality"
        },
        "geometry": {
            "type": "Polygon",
            "coordinates": ${polygonCoordinates(municipality.polygon)}
        }
    }
    """.trimIndent()
}

private fun exportStation (station: StationNode) : String {

    return """
        {
            "type":"Feature",
            "properties":{
                "id": ${station.id},
                "name": "${escape(station.name)}",
                "type": "station"
            },
            "geometry":{
                "type":"Point",
                "coordinates": [${station.location.lon}, ${station.location.lat}]
            }
        }
    """.trimIndent()
}

private fun lineCoordinates(points: List<PointNode>): String {
    return points.joinToString (
        prefix = "[",
        postfix = "]"
    ) { point ->
            "[${point.lon}, ${point.lat}]"
    }
}

private fun polygonCoordinates(points: List<PointNode>): String {
    val closedPoints =
        if (points.isNotEmpty() && points.first() != points.last()) {
            points + points.first()
        } else {
            points
        }

    val ring = closedPoints.joinToString(
        prefix = "[",
        postfix = "]"
    ) { point ->
        "[${point.lon}, ${point.lat}]"
    }

    return "[$ring]"
}

private fun escape(value: String): String {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
}
