import type { Feature, GeoJsonProperties, Geometry } from "geojson"
import type { SensumGeoJson } from "../shared/types"


export const layerLabels: Record<string, string> = {
    stations: "Stations",
    rivers: "Rivers",
    lakes: "Lakes",
    regions: "Regions",
    municipalities: "Municipalities",
    floods_often: "Flood zones - frequent",
    floods_rare: "Flood zones - rare",
    floods_very_rare: "Flood zones - very rare",
}

export const layerSources: Record<string, string> = {
    rivers: "/geojson/arso/rivers.geojson",
    lakes: "/geojson/arso/lakes.geojson",
    regions: "/geojson/arso/regions.geojson",
    municipalities: "/geojson/arso/municipalities.geojson",
    floods_often: "/geojson/arso/floods-often.geojson",
    floods_rare: "/geojson/arso/floods-rare.geojson",
    floods_very_rare: "/geojson/arso/floods-very-rare.geojson",
}

export const layerTypes: Record<string, string> = {
    stations: "station",
    rivers: "river",
    lakes: "lake",
    regions: "region",
    municipalities: "municipality",
    floods_often: "flood_zone",
    floods_rare: "flood_zone",
    floods_very_rare: "flood_zone",
}

export const stationSources = ["SWS", "MANUAL", "DSL", "SIM", "UNKNOWN"] as const

export const stationSourceLabels: Record<string, string> = {
    SWS: "SWS — water monitoring",
    MANUAL: "Manual",
    DSL: "DSL",
    SIM: "Simulated",
    UNKNOWN: "Unknown",
}

export const stationSourceBadges: Record<string, string> = {
    SWS: "W",
    MANUAL: "M",
    DSL: "D",
    SIM: "S",
    UNKNOWN: "",
}

export const stationSourceColors: Record<string, string> = {
    SWS: "#3b82f6",
    MANUAL: "#9ca3af",
    DSL: "#a855f7",
    SIM: "#eab308",
    UNKNOWN: "#eab308",
}

export const defaultVisibleStationSources = new Set<string>(stationSources)

export function getStationSource(feature?: Feature<Geometry, GeoJsonProperties>): string {
    const source = feature?.properties?.source
    return typeof source === "string" && source.trim().length > 0
        ? source.toUpperCase()
        : "UNKNOWN"
}

export const defaultVisibleLayers = new Set<string>(["rivers", "lakes", "stations"])

export const layerRenderOrder = [
    "regions",
    "municipalities",
    "floods_very_rare",
    "floods_rare",
    "floods_often",
    "lakes",
    "rivers",
    "stations",
]

export const layerControlOrder = [
    "stations",
    "rivers",
    "lakes",
    "regions",
    "municipalities",
    "floods_often",
    "floods_rare",
    "floods_very_rare",
]

export function getLayer(feature?: Feature<Geometry, GeoJsonProperties>) {
    return String(feature?.properties?.layer ?? feature?.properties?.type ?? "unknown")
}

export function getFeatureName(feature?: Feature<Geometry, GeoJsonProperties>) {
    return String(
        feature?.properties?.name ??
        feature?.properties?.NAZIV ??
        feature?.properties?.IME ??
        feature?.properties?.NAME ??
        feature?.properties?.VTPV_IME ??
        feature?.properties?.OB_UIME ??
        "No name"
    )
}

export function getLayerLabel(layer: string) {
    if (layerLabels[layer]) {
        return layerLabels[layer]
    }

    if (layer.startsWith("imported_")) {
        return "Imported GeoJSON"
    }

    return layer
        .replaceAll("_", " ")
        .replaceAll("-", " ")
        .replace(/\b\w/g, (char) => char.toUpperCase())
}

export function getLayerRenderSortIndex(layer: string) {
    const index = layerRenderOrder.indexOf(layer)
    return index >= 0 ? index : layerRenderOrder.length
}

export function getLayerControlSortIndex(layer: string) {
    const index = layerControlOrder.indexOf(layer)
    return index >= 0 ? index : layerControlOrder.length
}

export function normalizeLayerData(geojson: SensumGeoJson, layer: string): SensumGeoJson {
    return {
        ...geojson,
        features: geojson.features.map((feature) => ({
            ...feature,
            properties: {
                ...(feature.properties ?? {}),
                layer,
                type: feature.properties?.type ?? layerTypes[layer] ?? layer,
            },
        })),
    }
}

export type LayerGroup = {
    id: string
    label: string
    layers: string[]
    isStationLike?: boolean
}

export const layerGroups: LayerGroup[] = [
    {
        id: "stations",
        label: "Stations",
        layers: ["stations"],
    },
    {
        id: "geographical",
        label: "Geographical",
        layers: ["rivers", "lakes", "regions", "municipalities"],
    },
    {
        id: "flood_zones",
        label: "Flood zones",
        layers: ["floods_often", "floods_rare", "floods_very_rare"],
    },
    {
        id: "imported",
        label: "Imported layers",
        layers: [],
    },
]

export const layerShortLabels: Record<string, string> = {
    floods_often: "Frequent",
    floods_rare: "Rare",
    floods_very_rare: "Very rare",
    rivers: "Rivers",
    lakes: "Lakes",
    regions: "Regions",
    municipalities: "Municipalities",
}
