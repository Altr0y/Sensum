import type { StationDto } from "../types"
import type { SimConfig } from "./types"

function pointInPolygon(
    point: [number, number],
    polygon: [number, number][]
): boolean {
    const [px, py] = point
    let inside = false
    const n = polygon.length
    for (let i = 0, j = n - 1; i < n; j = i++) {
        const [xi, yi] = polygon[i]
        const [xj, yj] = polygon[j]
        if ((yi > py) !== (yj > py) && px < ((xj - xi) * (py - yi)) / (yj - yi) + xi) {
            inside = !inside
        }
    }
    return inside
}

export function stationsInsidePolygon(
    stations: StationDto[],
    polygon: [number, number][]
): StationDto[] {
    if (polygon.length < 3) return []
    return stations.filter((s) => {
        if (s.latitude == null || s.longitude == null) return false
        return pointInPolygon([s.latitude, s.longitude], polygon)
    })
}

export function localInputToIso(local: string): string {
    return local + ":00"
}

export function makeDefaultConfig(): SimConfig {
    const to = new Date()
    const from = new Date(to.getTime() - 7 * 24 * 60 * 60 * 1000)
    return {
        channelKinds: ["water_level", "temperature"],
        from: from.toISOString().slice(0, 16),
        to: to.toISOString().slice(0, 16),
        intervalMinutes: 60,
        stationCount: 3,
        prefix: "Postaja",
    }
}
