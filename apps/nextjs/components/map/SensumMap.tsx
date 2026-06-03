"use client"

import { useEffect, useMemo, useRef, useState } from "react"
import type { ChangeEvent, PointerEvent } from "react"
import { GeoJSON, MapContainer, TileLayer, useMap } from "react-leaflet"
import type {
    Feature,
    FeatureCollection,
    GeoJsonProperties,
    Geometry,
} from "geojson"
import type { Layer } from "leaflet"
import L from "leaflet"

type SensumGeoJson = FeatureCollection<Geometry, GeoJsonProperties>

type StationDto = {
    stationId: number
    name?: string | null
    modbusAddress?: number | null
    serialNumber?: string | null
    stationType?: string | null
    description?: string | null
    latitude?: number | null
    longitude?: number | null
}

type ChannelDto = {
    stationId: number
    channelId: number
    name?: string | null
    unit?: string | null
    description?: string | null
}

type MeasurementDto = {
    id?: number | null
    stationId: number
    channelId: number
    dateTime: string
    value: number
    status: number | string
}

type SelectedStationDetails = {
    station: StationDto
    channels: ChannelDto[]
    measurementsByChannel: Record<number, MeasurementDto[]>
}

type PanelPosition = {
    x: number
    y: number
}

const layerLabels: Record<string, string> = {
    stations: "Stations",
    rivers: "Rivers",
    lakes: "Lakes",
    regions: "Regions",
    municipalities: "Municipalities",
    floods_often: "Flood zones - frequent",
    floods_rare: "Flood zones - rare",
    floods_very_rare: "Flood zones - very rare",
}

const layerSources: Record<string, string> = {
    rivers: "/geojson/arso/rivers.geojson",
    lakes: "/geojson/arso/lakes.geojson",
    regions: "/geojson/arso/regions.geojson",
    municipalities: "/geojson/arso/municipalities.geojson",
    floods_often: "/geojson/arso/floods-often.geojson",
    floods_rare: "/geojson/arso/floods-rare.geojson",
    floods_very_rare: "/geojson/arso/floods-very-rare.geojson",
}

const layerTypes: Record<string, string> = {
    stations: "station",
    rivers: "river",
    lakes: "lake",
    regions: "region",
    municipalities: "municipality",
    floods_often: "flood_zone",
    floods_rare: "flood_zone",
    floods_very_rare: "flood_zone",
}

const defaultVisibleLayers = new Set<string>([
    "rivers",
    "lakes",
    "stations",
])

const layerRenderOrder = [
    "regions",
    "municipalities",
    "floods_very_rare",
    "floods_rare",
    "floods_often",
    "lakes",
    "rivers",
    "stations",
]

const layerControlOrder = [
    "stations",
    "rivers",
    "lakes",
    "regions",
    "municipalities",
    "floods_often",
    "floods_rare",
    "floods_very_rare",
]

function ResizeMapOnSidebarChange({
    sidebarOpen,
}: {
    sidebarOpen: boolean
}) {
    const map = useMap()

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            map.invalidateSize()
        }, 250)

        return () => window.clearTimeout(timeoutId)
    }, [map, sidebarOpen])

    return null
}

function normalizeLayerData(
    geojson: SensumGeoJson,
    layer: string
): SensumGeoJson {
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

function getLayer(feature?: Feature<Geometry, GeoJsonProperties>) {
    return String(feature?.properties?.layer ?? feature?.properties?.type ?? "unknown")
}

function getFeatureName(feature?: Feature<Geometry, GeoJsonProperties>) {
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

function getLayerLabel(layer: string) {
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

function getLayerRenderSortIndex(layer: string) {
    const index = layerRenderOrder.indexOf(layer)

    if (index >= 0) {
        return index
    }

    return layerRenderOrder.length
}

function getLayerControlSortIndex(layer: string) {
    const index = layerControlOrder.indexOf(layer)

    if (index >= 0) {
        return index
    }

    return layerControlOrder.length
}

function getStyle(feature?: Feature<Geometry, GeoJsonProperties>) {
    const layer = getLayer(feature)

    if (layer === "regions") {
        return {
            color: "#2563eb",
            weight: 2,
            fillColor: "#60a5fa",
            fillOpacity: 0.12,
        }
    }

    if (layer === "municipalities") {
        return {
            color: "#7c3aed",
            weight: 1,
            fillColor: "#c4b5fd",
            fillOpacity: 0.14,
        }
    }

    if (layer === "rivers") {
        return {
            color: "#0284c7",
            weight: 4,
            opacity: 0.9,
        }
    }

    if (layer === "lakes") {
        return {
            color: "#0891b2",
            weight: 2,
            fillColor: "#67e8f9",
            fillOpacity: 0.45,
        }
    }

    if (layer === "stations") {
        return {
            color: "#111827",
            weight: 2,
            fillColor: "#eab308",
            fillOpacity: 1,
        }
    }

    if (layer === "floods_often") {
        return {
            color: "#b91c1c",
            weight: 2,
            fillColor: "#ef4444",
            fillOpacity: 0.32,
        }
    }

    if (layer === "floods_rare") {
        return {
            color: "#c2410c",
            weight: 2,
            fillColor: "#f97316",
            fillOpacity: 0.25,
        }
    }

    if (layer === "floods_very_rare") {
        return {
            color: "#ca8a04",
            weight: 2,
            fillColor: "#facc15",
            fillOpacity: 0.2,
        }
    }

    return {
        color: "#111827",
        weight: 2,
        fillOpacity: 0.25,
    }
}

function pointToLayer(
    feature: Feature<Geometry, GeoJsonProperties>,
    latlng: L.LatLng
) {
    const layer = getLayer(feature)

    if (layer === "stations") {
        return L.circleMarker(latlng, {
            radius: 8,
            color: "#111827",
            weight: 2,
            fillColor: "#eab308",
            fillOpacity: 1,
            pane: "markerPane",
        })
    }

    return L.circleMarker(latlng, {
        radius: 5,
        color: "#111827",
        weight: 1,
        fillColor: "#ffffff",
        fillOpacity: 1,
    })
}

function bindFeatureActions(
    onStationClick: (stationId: number) => void
) {
    return function onEachFeature(
        feature: Feature<Geometry, GeoJsonProperties>,
        layer: Layer
    ) {
        const props = feature.properties ?? {}
        const name = getFeatureName(feature)
        const type = String(props.type ?? "-")
        const sourceFile = String(props.sourceFile ?? "-")
        const risk = props.risk ? `<br/><b>Risk:</b> ${props.risk}` : ""

        if (type === "station") {
            layer.on("click", () => {
                const stationId = Number(props.stationId ?? props.id)

                if (!Number.isNaN(stationId)) {
                    onStationClick(stationId)
                }
            })

            layer.bindTooltip(name)

            if ("bringToFront" in layer && typeof layer.bringToFront === "function") {
                layer.bringToFront()
            }

            return
        }

        layer.bindPopup(`
            <b>${name}</b><br/>
            <b>Type:</b> ${type}<br/>
            <b>Layer:</b> ${getLayer(feature)}<br/>
            <b>Source:</b> ${sourceFile}
            ${risk}
        `)
    }
}

async function fetchJson<T>(url: string): Promise<T> {
    const response = await fetch(url, {
        credentials: "include",
    })

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
    }

    return response.json()
}

function downloadTextFile(
    filename: string,
    content: string,
    contentType: string
) {
    const blob = new Blob([content], { type: contentType })
    const url = URL.createObjectURL(blob)

    const link = document.createElement("a")
    link.href = url
    link.download = filename
    link.click()

    URL.revokeObjectURL(url)
}

function formatApiLocalDateTime(date: Date) {
    const pad = (value: number) => value.toString().padStart(2, "0")

    return [
        date.getFullYear(),
        "-",
        pad(date.getMonth() + 1),
        "-",
        pad(date.getDate()),
        "T",
        pad(date.getHours()),
        ":",
        pad(date.getMinutes()),
        ":",
        pad(date.getSeconds()),
    ].join("")
}

function formatDisplayDateTime(value: string) {
    const parsed = new Date(value)

    if (Number.isNaN(parsed.getTime())) {
        return value.replace("T", " ")
    }

    return new Intl.DateTimeFormat("en-GB", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
    }).format(parsed)
}

function formatMeasurementValue(value: number) {
    return value.toLocaleString("en-US", {
        minimumFractionDigits: 0,
        maximumFractionDigits: 6,
    })
}

function formatCoordinate(value?: number | null) {
    if (value == null) {
        return "-"
    }

    return value.toLocaleString("en-US", {
        minimumFractionDigits: 0,
        maximumFractionDigits: 6,
    })
}

export function SensumMap() {
    const mapShellRef = useRef<HTMLDivElement | null>(null)

    const [layerData, setLayerData] = useState<Record<string, SensumGeoJson>>({})
    const [visibleLayers, setVisibleLayers] = useState<Set<string>>(
        () => new Set(defaultVisibleLayers)
    )
    const [loadingLayers, setLoadingLayers] = useState<Set<string>>(() => new Set())
    const [errors, setErrors] = useState<Record<string, string>>({})

    const [stations, setStations] = useState<StationDto[]>([])
    const [selectedStation, setSelectedStation] = useState<SelectedStationDetails | null>(null)
    const [stationDetailsLoading, setStationDetailsLoading] = useState(false)
    const [stationDetailsError, setStationDetailsError] = useState<string | null>(null)
    const [dataMenuOpen, setDataMenuOpen] = useState(false)
    const [sidebarOpen, setSidebarOpen] = useState(true)

    const [layerPanelPosition, setLayerPanelPosition] = useState<PanelPosition>({
        x: 16,
        y: 16,
    })

    const [isLayerPanelDragging, setIsLayerPanelDragging] = useState(false)

    const layerPanelDragOffset = useRef<PanelPosition>({
        x: 0,
        y: 0,
    })

    useEffect(() => {
        for (const layer of visibleLayers) {
            if (layer === "stations") {
                continue
            }

            if (layerData[layer]) {
                continue
            }

            if (loadingLayers.has(layer)) {
                continue
            }

            const source = layerSources[layer]

            if (!source) {
                continue
            }

            setLoadingLayers((current) => {
                const next = new Set(current)
                next.add(layer)
                return next
            })

            fetch(source)
                .then((response) => {
                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}`)
                    }

                    return response.json()
                })
                .then((json: SensumGeoJson) => {
                    setLayerData((current) => ({
                        ...current,
                        [layer]: normalizeLayerData(json, layer),
                    }))

                    setErrors((current) => {
                        const next = { ...current }
                        delete next[layer]
                        return next
                    })
                })
                .catch((error) => {
                    setErrors((current) => ({
                        ...current,
                        [layer]: error instanceof Error
                            ? error.message
                            : "Failed to load layer",
                    }))
                })
                .finally(() => {
                    setLoadingLayers((current) => {
                        const next = new Set(current)
                        next.delete(layer)
                        return next
                    })
                })
        }
    }, [visibleLayers, layerData, loadingLayers])

    useEffect(() => {
        if (!visibleLayers.has("stations")) {
            return
        }

        if (layerData.stations) {
            return
        }

        if (loadingLayers.has("stations")) {
            return
        }

        setLoadingLayers((current) => {
            const next = new Set(current)
            next.add("stations")
            return next
        })

        Promise.all([
            fetchJson<StationDto[]>("/sensum-api/v1/stations"),
            fetchJson<SensumGeoJson>("/sensum-api/v1/stations/geojson"),
        ])
            .then(([loadedStations, stationsGeoJson]) => {
                setStations(loadedStations)

                setLayerData((current) => ({
                    ...current,
                    stations: normalizeLayerData(stationsGeoJson, "stations"),
                }))

                setErrors((current) => {
                    const next = { ...current }
                    delete next.stations
                    return next
                })
            })
            .catch((error) => {
                setErrors((current) => ({
                    ...current,
                    stations: error instanceof Error
                        ? error.message
                        : "Failed to load stations",
                }))
            })
            .finally(() => {
                setLoadingLayers((current) => {
                    const next = new Set(current)
                    next.delete("stations")
                    return next
                })
            })
    }, [visibleLayers, layerData.stations, loadingLayers])

    function toggleLayer(layer: string) {
        setVisibleLayers((current) => {
            const next = new Set(current)

            if (next.has(layer)) {
                next.delete(layer)
            } else {
                next.add(layer)
            }

            return next
        })
    }

    function startLayerPanelDrag(event: PointerEvent<HTMLDivElement>) {
        if (event.button !== 0) {
            return
        }

        const panel = event.currentTarget.parentElement

        if (!panel) {
            return
        }

        const panelRect = panel.getBoundingClientRect()

        layerPanelDragOffset.current = {
            x: event.clientX - panelRect.left,
            y: event.clientY - panelRect.top,
        }

        setIsLayerPanelDragging(true)
        event.currentTarget.setPointerCapture(event.pointerId)
    }

    function moveLayerPanel(event: PointerEvent<HTMLDivElement>) {
        if (!isLayerPanelDragging) {
            return
        }

        const container = mapShellRef.current

        if (!container) {
            return
        }

        const containerRect = container.getBoundingClientRect()
        const panelWidth = 320
        const panelHeight = 270

        const maxX = Math.max(0, containerRect.width - panelWidth)
        const maxY = Math.max(0, containerRect.height - panelHeight)

        const nextX =
            event.clientX -
            containerRect.left -
            layerPanelDragOffset.current.x

        const nextY =
            event.clientY -
            containerRect.top -
            layerPanelDragOffset.current.y

        setLayerPanelPosition({
            x: Math.min(Math.max(0, nextX), maxX),
            y: Math.min(Math.max(0, nextY), maxY),
        })
    }

    function stopLayerPanelDrag(event: PointerEvent<HTMLDivElement>) {
        setIsLayerPanelDragging(false)

        if (event.currentTarget.hasPointerCapture(event.pointerId)) {
            event.currentTarget.releasePointerCapture(event.pointerId)
        }
    }

    async function openStationDetails(stationId: number) {
        const station = stations.find((item) => item.stationId === stationId)

        if (!station) {
            return
        }

        setSidebarOpen(true)

        setSelectedStation({
            station,
            channels: [],
            measurementsByChannel: {},
        })

        setStationDetailsLoading(true)
        setStationDetailsError(null)

        try {
            const channels = await fetchJson<ChannelDto[]>(
                `/sensum-api/v1/stations/${stationId}/channels`
            )

            const now = new Date()
            const to = formatApiLocalDateTime(now)

            const fromDate = new Date(now)
            fromDate.setDate(fromDate.getDate() - 7)
            const from = formatApiLocalDateTime(fromDate)

            const measurementsByChannelEntries = await Promise.all(
                channels.map(async (channel) => {
                    const measurements = await fetchJson<MeasurementDto[]>(
                        `/sensum-api/v1/measurements/range?channelId=${channel.channelId}&from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`
                    )

                    return [channel.channelId, measurements.slice(-10).reverse()] as const
                })
            )

            setSelectedStation({
                station,
                channels,
                measurementsByChannel: Object.fromEntries(measurementsByChannelEntries),
            })
        } catch (error) {
            setStationDetailsError(
                error instanceof Error
                    ? error.message
                    : "Failed to load station details"
            )
        } finally {
            setStationDetailsLoading(false)
        }
    }

    function exportVisibleGeoJson() {
        const features = Array.from(visibleLayers)
            .sort((a, b) => getLayerRenderSortIndex(a) - getLayerRenderSortIndex(b))
            .flatMap((layer) => layerData[layer]?.features ?? [])

        const geoJson: SensumGeoJson = {
            type: "FeatureCollection",
            features,
        }

        downloadTextFile(
            "sensum-visible-layers.geojson",
            JSON.stringify(geoJson, null, 2),
            "application/geo+json"
        )
    }

    async function handleSensumDslImport(event: ChangeEvent<HTMLInputElement>) {
        const file = event.target.files?.[0]

        if (!file) {
            return
        }

        const content = await file.text()

        console.log("Imported Sensum DSL:", content)

        alert(
            "Sensum DSL import is prepared in the UI. Next step: connect this action to a backend endpoint that uses GeoDslProcessor.parseString()."
        )

        event.target.value = ""
    }

    const visibleLayerList = useMemo(() => {
        return Array.from(visibleLayers)
            .sort((a, b) => getLayerRenderSortIndex(a) - getLayerRenderSortIndex(b))
    }, [visibleLayers])

    const layerControlItems = useMemo(() => {
        return Array.from(
            new Set([
                ...Object.keys(layerLabels),
                ...Object.keys(layerData),
            ])
        ).sort((a, b) => getLayerControlSortIndex(a) - getLayerControlSortIndex(b))
    }, [layerData])

    return (
        <div className="flex h-[calc(100vh-3.5rem)] w-full overflow-hidden bg-background">
            <div
                ref={mapShellRef}
                className="relative h-full min-w-0 flex-1 overflow-hidden"
            >
                <MapContainer
                    center={[46.1512, 14.9955]}
                    zoom={8}
                    scrollWheelZoom
                    preferCanvas
                    className="h-full w-full"
                >
                    <ResizeMapOnSidebarChange sidebarOpen={sidebarOpen} />

                    <TileLayer
                        attribution='&copy; OpenStreetMap contributors'
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />

                    {visibleLayerList.map((layer) => {
                        const data = layerData[layer]

                        if (!data) {
                            return null
                        }

                        return (
                            <GeoJSON
                                key={layer}
                                data={data}
                                style={getStyle}
                                pointToLayer={pointToLayer}
                                onEachFeature={bindFeatureActions(openStationDetails)}
                            />
                        )
                    })}
                </MapContainer>

            {!sidebarOpen && (
    <button
        type="button"
        onClick={() => setSidebarOpen(true)}
        className="absolute right-4 top-4 z-[1000] rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-900 shadow-lg hover:bg-gray-100 dark:border-border dark:bg-card dark:text-foreground dark:hover:bg-muted"
    >
        Show sidebar
    </button>
)}

<div
    className="absolute z-[1000] max-h-[calc(100%-2rem)] w-80 overflow-hidden rounded-xl border border-gray-300 bg-white text-gray-900 shadow-lg dark:border-border dark:bg-card dark:text-foreground"
    style={{
        left: layerPanelPosition.x,
        top: layerPanelPosition.y,
    }}
>
    <div
        className="cursor-move select-none border-b border-gray-300 bg-white px-4 py-3 dark:border-border dark:bg-card"
        style={{
            touchAction: "none",
        }}
        onPointerDown={startLayerPanelDrag}
        onPointerMove={moveLayerPanel}
        onPointerUp={stopLayerPanelDrag}
        onPointerCancel={stopLayerPanelDrag}
    >
        <div className="flex items-center justify-between gap-3">
            <div>
                <h2 className="text-sm font-semibold text-gray-900 dark:text-foreground">
                    Map layers
                </h2>

                <p className="text-xs text-gray-600 dark:text-muted-foreground">
                    Layers are loaded only when enabled.
                </p>
            </div>

            <span className="rounded-md border border-gray-300 bg-gray-100 px-2 py-1 text-[10px] font-medium text-gray-700 dark:border-border dark:bg-background dark:text-muted-foreground">
                Move
            </span>
        </div>
    </div>

    <div className="max-h-[calc(100vh-12rem)] overflow-y-auto bg-white p-4 dark:bg-card">
        <div className="space-y-2">
            {layerControlItems.map((layer) => {
                const isLoading = loadingLayers.has(layer)
                const error = errors[layer]

                return (
                    <label
                        key={layer}
                        className="flex cursor-pointer items-center gap-2 rounded-md border border-gray-200 bg-gray-50 px-2 py-1.5 text-sm font-medium text-gray-900 hover:bg-gray-100 dark:border-border dark:bg-background dark:text-foreground dark:hover:bg-muted"
                    >
                        <input
                            type="checkbox"
                            checked={visibleLayers.has(layer)}
                            onChange={() => toggleLayer(layer)}
                            className="shrink-0 accent-yellow-500"
                        />

                        <span className="flex-1">
                            {getLayerLabel(layer)}
                        </span>

                        {isLoading && (
                            <span className="text-xs font-normal text-gray-600 dark:text-muted-foreground">
                                Loading...
                            </span>
                        )}

                        {error && (
                            <span className="text-xs font-normal text-red-600 dark:text-red-400">
                                {error}
                            </span>
                        )}
                    </label>
                )
            })}
        </div>
    </div>
</div>
            </div>

            {sidebarOpen && (
                <aside className="h-full w-[430px] shrink-0 overflow-hidden border-l border-border bg-card text-foreground shadow-xl dark:border-border dark:bg-card dark:text-foreground">
                    <div className="flex h-full flex-col">
                        <div className="border-b border-border p-4">
                            <div className="flex items-start justify-between gap-3">
                                <div>
                                    <h2 className="text-base font-semibold text-foreground">
                                        Station details
                                    </h2>

                                    <p className="text-xs text-muted-foreground">
                                        Click a station on the map to view channels and measurements.
                                    </p>
                                </div>

                                <div className="flex items-center gap-2">
                                    <div className="relative">
                                        <button
                                            type="button"
                                            onClick={() => setDataMenuOpen((current) => !current)}
                                            className="flex h-9 w-9 items-center justify-center rounded-lg border border-border bg-background text-lg text-foreground shadow-sm hover:bg-muted dark:bg-background dark:text-foreground dark:hover:bg-muted"
                                            title="Settings"
                                        >
                                            ⚙
                                        </button>

                                        {dataMenuOpen && (
                                            <div className="absolute right-0 top-11 z-[1200] w-56 rounded-xl border border-border bg-white p-2 text-foreground shadow-xl dark:bg-[#09090b] dark:text-foreground">
                                                <button
                                                    type="button"
                                                    className="block w-full rounded-md px-3 py-2 text-left text-sm text-foreground hover:bg-muted dark:hover:bg-muted"
                                                    onClick={() => {
                                                        setDataMenuOpen(false)
                                                        document.getElementById("sensum-dsl-import")?.click()
                                                    }}
                                                >
                                                    Import Sensum DSL
                                                </button>

                                                <div className="my-1 border-t border-border" />

                                                <button
                                                    type="button"
                                                    className="block w-full rounded-md px-3 py-2 text-left text-sm text-foreground hover:bg-muted dark:hover:bg-muted"
                                                    onClick={() => {
                                                        setDataMenuOpen(false)
                                                        exportVisibleGeoJson()
                                                    }}
                                                >
                                                    Export GeoJSON
                                                </button>
                                            </div>
                                        )}

                                        <input
                                            id="sensum-dsl-import"
                                            type="file"
                                            accept=".sensum,.txt"
                                            className="hidden"
                                            onChange={handleSensumDslImport}
                                        />
                                    </div>

                                    <button
                                        type="button"
                                        onClick={() => setSidebarOpen(false)}
                                        className="rounded-lg border border-border bg-background px-3 py-2 text-sm font-medium text-foreground shadow-sm hover:bg-muted dark:bg-background dark:text-foreground dark:hover:bg-muted"
                                    >
                                        Hide
                                    </button>
                                </div>
                            </div>
                        </div>

                        {!selectedStation && (
                            <div className="flex flex-1 items-center justify-center p-6 text-center">
                                <div>
                                    <div className="mb-3 text-4xl">📍</div>

                                    <h3 className="text-sm font-semibold text-foreground">
                                        No station selected
                                    </h3>

                                    <p className="mt-1 text-sm text-muted-foreground">
                                        Enable the Stations layer and click a yellow marker on the map.
                                    </p>
                                </div>
                            </div>
                        )}

                        {selectedStation && (
                            <>
                                <div className="border-b border-border p-4">
                                    <div className="flex items-start justify-between gap-3">
                                        <div>
                                            <h3 className="text-lg font-semibold text-foreground">
                                                {selectedStation.station.name ?? `Station ${selectedStation.station.stationId}`}
                                            </h3>

                                            <p className="text-xs text-muted-foreground">
                                                Station ID: {selectedStation.station.stationId}
                                            </p>
                                        </div>

                                        <button
                                            type="button"
                                            onClick={() => setSelectedStation(null)}
                                            className="rounded-md px-2 py-1 text-sm text-muted-foreground hover:bg-muted"
                                        >
                                            ×
                                        </button>
                                    </div>

                                    <div className="mt-4 grid grid-cols-2 gap-2 text-xs">
                                        <div className="rounded-lg border border-border bg-background p-2">
                                            <div className="text-muted-foreground">
                                                Type
                                            </div>

                                            <div className="font-medium">
                                                {selectedStation.station.stationType ?? "-"}
                                            </div>
                                        </div>

                                        <div className="rounded-lg border border-border bg-background p-2">
                                            <div className="text-muted-foreground">
                                                Modbus
                                            </div>

                                            <div className="font-medium">
                                                {selectedStation.station.modbusAddress ?? "-"}
                                            </div>
                                        </div>

                                        <div className="rounded-lg border border-border bg-background p-2">
                                            <div className="text-muted-foreground">
                                                Latitude
                                            </div>

                                            <div className="font-medium">
                                                {formatCoordinate(selectedStation.station.latitude)}
                                            </div>
                                        </div>

                                        <div className="rounded-lg border border-border bg-background p-2">
                                            <div className="text-muted-foreground">
                                                Longitude
                                            </div>

                                            <div className="font-medium">
                                                {formatCoordinate(selectedStation.station.longitude)}
                                            </div>
                                        </div>
                                    </div>

                                    {selectedStation.station.serialNumber && (
                                        <div className="mt-2 rounded-lg border border-border bg-background p-2 text-xs">
                                            <span className="text-muted-foreground">
                                                Serial number:{" "}
                                            </span>

                                            <span className="font-medium">
                                                {selectedStation.station.serialNumber}
                                            </span>
                                        </div>
                                    )}

                                    {selectedStation.station.description && (
                                        <p className="mt-2 rounded-lg border border-border bg-background p-2 text-xs text-muted-foreground">
                                            {selectedStation.station.description}
                                        </p>
                                    )}
                                </div>

                                <div className="flex-1 overflow-y-auto p-4">
                                    {stationDetailsLoading && (
                                        <p className="text-sm text-muted-foreground">
                                            Loading station details...
                                        </p>
                                    )}

                                    {stationDetailsError && (
                                        <p className="rounded-lg border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-600 dark:text-red-400">
                                            {stationDetailsError}
                                        </p>
                                    )}

                                    {!stationDetailsLoading && selectedStation.channels.length === 0 && (
                                        <p className="text-sm text-muted-foreground">
                                            No channels found for this station.
                                        </p>
                                    )}

                                    <div className="space-y-4">
                                        {selectedStation.channels.map((channel) => {
                                            const measurements =
                                                selectedStation.measurementsByChannel[channel.channelId] ?? []

                                            return (
                                                <section
                                                    key={channel.channelId}
                                                    className="overflow-hidden rounded-xl border border-border bg-background"
                                                >
                                                    <div className="border-b border-border p-3">
                                                        <h4 className="text-sm font-semibold text-foreground">
                                                            {channel.name ?? `Channel ${channel.channelId}`}
                                                        </h4>

                                                        <p className="text-xs text-muted-foreground">
                                                            Channel ID: {channel.channelId}
                                                        </p>

                                                        {channel.description && (
                                                            <p className="mt-1 text-xs text-muted-foreground">
                                                                {channel.description}
                                                            </p>
                                                        )}
                                                    </div>

                                                    {measurements.length === 0 ? (
                                                        <p className="p-3 text-xs text-muted-foreground">
                                                            No recent measurements.
                                                        </p>
                                                    ) : (
                                                        <div className="overflow-x-auto">
                                                            <table className="w-full text-left text-xs">
                                                                <thead className="border-b border-border bg-muted text-muted-foreground">
                                                                    <tr>
                                                                        <th className="px-3 py-2 font-medium">
                                                                            Date and time
                                                                        </th>

                                                                        <th className="px-3 py-2 text-right font-medium">
                                                                            Value
                                                                        </th>

                                                                        <th className="px-3 py-2 text-right font-medium">
                                                                            Status
                                                                        </th>
                                                                    </tr>
                                                                </thead>

                                                                <tbody>
                                                                    {measurements.map((measurement, index) => (
                                                                        <tr
                                                                            key={`${measurement.channelId}-${measurement.dateTime}-${index}`}
                                                                            className="border-b border-border/60 last:border-0"
                                                                        >
                                                                            <td className="whitespace-nowrap px-3 py-2 text-muted-foreground">
                                                                                {formatDisplayDateTime(measurement.dateTime)}
                                                                            </td>

                                                                            <td className="whitespace-nowrap px-3 py-2 text-right font-semibold text-foreground">
                                                                                {formatMeasurementValue(measurement.value)}
                                                                            </td>

                                                                            <td className="whitespace-nowrap px-3 py-2 text-right text-muted-foreground">
                                                                                {String(measurement.status)}
                                                                            </td>
                                                                        </tr>
                                                                    ))}
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    )}
                                                </section>
                                            )
                                        })}
                                    </div>
                                </div>
                            </>
                        )}
                    </div>
                </aside>
            )}
        </div>
    )
}