package si.sensum.geodsl.spatial

import si.sensum.geodsl.ast.FloodZoneNode
import si.sensum.geodsl.ast.PointNode
import kotlin.math.*

object SpatialUtils {

    // Haversine - razdalja med dvema GPS tockama v metrih
    fun distanceMeters(a: PointNode, b: PointNode): Double {
        val R = 6371000.0
        val lat1 = Math.toRadians(a.lat)
        val lat2 = Math.toRadians(b.lat)
        val dLat = Math.toRadians(b.lat - a.lat)
        val dLon = Math.toRadians(b.lon - a.lon)
        val h = sin(dLat / 2).pow(2) +
                cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        return 2 * R * asin(sqrt(h))
    }

    // Ray casting - ali je tocka znotraj poligona
    fun isPointInPolygon(point: PointNode, polygon: List<PointNode>): Boolean {
        if (polygon.size < 3) return false
        var inside = false
        var j = polygon.size - 1
        for (i in polygon.indices) {
            val xi = polygon[i].lon; val yi = polygon[i].lat
            val xj = polygon[j].lon; val yj = polygon[j].lat
            if ((yi > point.lat) != (yj > point.lat) &&
                point.lon < (xj - xi) * (point.lat - yi) / (yj - yi) + xi
            ) inside = !inside
            j = i
        }
        return inside
    }

    // Ali je tocka v blizini polilinija (reke) v metrih
    fun isPointNearPolyline(
        point: PointNode,
        line: List<PointNode>,
        withinMeters: Int
    ): Boolean {
        for (i in 0 until line.size - 1) {
            if (distanceToSegment(point, line[i], line[i + 1]) <= withinMeters)
                return true
        }
        return false
    }

    // Doloci flood risk za koordinato glede na vse flood zone
    fun detectFloodRisk(point: PointNode, floodZones: List<FloodZoneNode>): String? {
        val priority = listOf("often", "rare", "very_rare")
        for (risk in priority) {
            val inZone = floodZones
                .filter { it.risk == risk }
                .any { isPointInPolygon(point, it.points) }
            if (inZone) return risk
        }
        return null
    }

    // Nakljucna tocka znotraj poligona (bbox + rejection sampling)
    fun randomPointInPolygon(polygon: List<PointNode>): PointNode {
        val minLon = polygon.minOf { it.lon }
        val maxLon = polygon.maxOf { it.lon }
        val minLat = polygon.minOf { it.lat }
        val maxLat = polygon.maxOf { it.lat }
        var candidate: PointNode
        do {
            candidate = PointNode(
                lon = minLon + Math.random() * (maxLon - minLon),
                lat = minLat + Math.random() * (maxLat - minLat)
            )
        } while (!isPointInPolygon(candidate, polygon))
        return candidate
    }

    // Razdalja od tocke do daljice
    private fun distanceToSegment(p: PointNode, a: PointNode, b: PointNode): Double {
        val dx = b.lon - a.lon
        val dy = b.lat - a.lat
        if (dx == 0.0 && dy == 0.0) return distanceMeters(p, a)
        val t = ((p.lon - a.lon) * dx + (p.lat - a.lat) * dy) / (dx * dx + dy * dy)
        val nearest = when {
            t < 0 -> a
            t > 1 -> b
            else  -> PointNode(a.lon + t * dx, a.lat + t * dy)
        }
        return distanceMeters(p, nearest)
    }
}