"use client"

import { useEffect, useMemo, useRef, useState } from "react"
import type { ChangeEvent } from "react"
import { CircleMarker, GeoJSON, MapContainer, TileLayer, useMapEvents } from "react-leaflet"
import { createStation, fetchChannels, fetchMeasurements, fetchStations, fetchStationsGeoJson, processDsl } from "./api"
import { AlertTriangle, X } from "lucide-react"
import { useSimulation } from "./simulation/useSimulation"
import { PolygonDrawOverlay } from "./simulation/PolygonDrawOverlay"
import { SimulationPanel } from "./simulation/SimulationPanel"
import { MapLegend } from "./MapLegend"
import { defaultVisibleLayers, defaultVisibleStationSources, getStationSource, layerLabels, layerSources, normalizeLayerData, getLayerControlSortIndex, getLayerRenderSortIndex } from "./layerConfig"
import { bindFeatureActions, getStyle, pointToLayer } from "./mapStyles"
import { formatApiLocalDateTime } from "./formatters"
import { downloadTextFile } from "./utils"
import { ResizeMapOnSidebarChange } from "./ResizeMapOnSidebarChange"
import { LayerControlPanel } from "./LayerControlPanel"
import { StationSidebar } from "./StationSidebar"
import type { PendingStationPoint, SelectedStationDetails, SensumGeoJson, StationDto } from "./types"

function ErrorBanner({
    errors,
    onDismiss,
}: {
    errors: Record<string, string>
    onDismiss: (key: string) => void
}) {
    const entries = Object.entries(errors)
    if (entries.length === 0) return null
    return (
        <div className="absolute top-4 left-1/2 -translate-x-1/2 z-[1100] flex flex-col gap-2 w-[420px] max-w-[calc(100%-2rem)]">
            {entries.map(([key, msg]) => (
                <div
                    key={key}
                    className="flex items-start gap-3 rounded-lg border border-[#CF6679]/40 px-3 py-2.5 shadow-lg text-sm"
                    style={{ backgroundColor: "#2B2B2B" }}
                >
                    <AlertTriangle size={15} className="text-[#CF6679] shrink-0 mt-0.5" />
                    <div className="flex-1 min-w-0">
                        <span className="font-medium text-[#CF6679]">{key}: </span>
                        <span className="text-[#BBBBBB]">{msg}</span>
                        {msg.includes("log in") && (
                            <a href="/login" className="ml-2 underline text-[#E8A838] hover:text-[#d4962e]">
                                Log in
                            </a>
                        )}
                    </div>
                    <button
                        onClick={() => onDismiss(key)}
                        className="shrink-0 text-[#7A7E82] hover:text-[#D4D4D4] transition-colors"
                    >
                        <X size={14} />
                    </button>
                </div>
            ))}
        </div>
    )
}

function MapClickHandler({
    addMode,
    simMode,
    onMapClick,
    onSimClick,
}: {
    addMode: boolean
    simMode: boolean
    onMapClick: (lat: number, lng: number) => void
    onSimClick: (lat: number, lng: number) => void
}) {
    useMapEvents({
        click(e) {
            if (addMode) {
                onMapClick(e.latlng.lat, e.latlng.lng)
            } else if (simMode) {
                onSimClick(e.latlng.lat, e.latlng.lng)
            }
        },
    })
    return null
}

export function SensumMap({ token }: { token: string }) {
    const mapShellRef = useRef<HTMLDivElement | null>(null)

    const [layerData, setLayerData] = useState<Record<string, SensumGeoJson>>({})
    const [visibleLayers, setVisibleLayers] = useState<Set<string>>(
        () => new Set(defaultVisibleLayers)
    )
    const [loadingLayers, setLoadingLayers] = useState<Set<string>>(() => new Set())
    const [errors, setErrors] = useState<Record<string, string>>({})
    const [visibleStationSources, setVisibleStationSources] = useState<Set<string>>(
        () => new Set(defaultVisibleStationSources)
    )

    const [stations, setStations] = useState<StationDto[]>([])
    const [selectedStation, setSelectedStation] = useState<SelectedStationDetails | null>(null)
    const [stationDetailsLoading, setStationDetailsLoading] = useState(false)
    const [stationDetailsError, setStationDetailsError] = useState<string | null>(null)
    const [sidebarOpen, setSidebarOpen] = useState(true)

    const [addMode, setAddMode] = useState(false)
    const [pendingStation, setPendingStation] = useState<PendingStationPoint | null>(null)
    const [addStationLoading, setAddStationLoading] = useState(false)
    const [addStationError, setAddStationError] = useState<string | null>(null)

    const sim = useSimulation(stations)

    useEffect(() => {
        for (const layer of visibleLayers) {
            if (layer === "stations") continue
            if (layerData[layer]) continue
            if (loadingLayers.has(layer)) continue
            if (errors[layer]) continue

            const source = layerSources[layer]
            if (!source) continue

            setLoadingLayers((current) => new Set(current).add(layer))

            fetch(source)
                .then((response) => {
                    if (!response.ok) throw new Error(`HTTP ${response.status}`)
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
                        [layer]: error instanceof Error ? error.message : "Failed to load layer",
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
    }, [visibleLayers, layerData, loadingLayers, errors])

    useEffect(() => {
        if (!visibleLayers.has("stations")) return
        if (layerData.stations) return
        if (loadingLayers.has("stations")) return
        if (errors.stations) return

        setLoadingLayers((current) => new Set(current).add("stations"))

        Promise.all([fetchStations(), fetchStationsGeoJson()])
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
                const msg = error instanceof Error ? error.message : "Failed to load stations"
                setErrors((current) => ({
                    ...current,
                    stations: msg === "HTTP 401"
                        ? "Session expired — please log in again"
                        : `Failed to load stations: ${msg}`,
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
            next.has(layer) ? next.delete(layer) : next.add(layer)
            return next
        })
    }

    function toggleStationSource(source: string) {
        setVisibleStationSources((current) => {
            const next = new Set(current)
            next.has(source) ? next.delete(source) : next.add(source)
            return next
        })
    }

    async function openStationDetails(stationId: number) {
        const station = stations.find((item) => item.stationId === stationId)
        if (!station) return

        setSidebarOpen(true)
        setSelectedStation({ station, channels: [], measurementsByChannel: {} })
        setStationDetailsLoading(true)
        setStationDetailsError(null)

        try {
            const channels = await fetchChannels(stationId)

            const now = new Date()
            const to = formatApiLocalDateTime(now)
            const fromDate = new Date(now)
            fromDate.setDate(fromDate.getDate() - 7)
            const from = formatApiLocalDateTime(fromDate)

            const measurementsByChannelEntries = await Promise.all(
                channels.map(async (channel) => {
                    const measurements = await fetchMeasurements(channel.channelId, from, to)
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
                error instanceof Error ? error.message : "Failed to load station details"
            )
        } finally {
            setStationDetailsLoading(false)
        }
    }

    function exportVisibleGeoJson() {
        const features = Array.from(visibleLayers)
            .sort((a, b) => getLayerRenderSortIndex(a) - getLayerRenderSortIndex(b))
            .flatMap((layer) => layerData[layer]?.features ?? [])

        const geoJson: SensumGeoJson = { type: "FeatureCollection", features }

        downloadTextFile(
            "sensum-visible-layers.geojson",
            JSON.stringify(geoJson, null, 2),
            "application/geo+json"
        )
    }

    async function handleSensumDslImport(event: ChangeEvent<HTMLInputElement>) {
        const file = event.target.files?.[0]
        if (!file) return

        const source = await file.text()
        event.target.value = ""

        const importKey = `imported_${Date.now()}`

        setLoadingLayers((current) => new Set(current).add(importKey))
        setErrors((current) => {
            const next = { ...current }
            delete next[importKey]
            return next
        })

        try {
            const result = await processDsl(source)
            const geoJsonData = JSON.parse(result.geoJson) as SensumGeoJson

            setLayerData((current) => ({
                ...current,
                [importKey]: normalizeLayerData(geoJsonData, importKey),
            }))
            setVisibleLayers((current) => {
                const next = new Set(current)
                next.add(importKey)
                return next
            })
        } catch (error) {
            setErrors((current) => ({
                ...current,
                [importKey]: error instanceof Error ? error.message : "DSL import failed",
            }))
        } finally {
            setLoadingLayers((current) => {
                const next = new Set(current)
                next.delete(importKey)
                return next
            })
        }
    }

    function handleToggleAddMode() {
        setAddMode((current) => !current)
        setPendingStation(null)
        setAddStationError(null)
    }

    function handleMapClick(lat: number, lng: number) {
        setPendingStation({ lat, lng })
        setAddStationError(null)
        setSidebarOpen(true)
        setSelectedStation(null)
    }

    async function handleSaveStation(name: string, description: string, serialNumber: string) {
        if (!pendingStation) return

        setAddStationLoading(true)
        setAddStationError(null)

        try {
            await createStation({
                name,
                latitude: pendingStation.lat,
                longitude: pendingStation.lng,
                description,
                serialNumber,
                source: "MANUAL",
            })

            // Force reload of stations layer
            setStations([])
            setLayerData((current) => {
                const next = { ...current }
                delete next.stations
                return next
            })

            setPendingStation(null)
            setAddMode(false)
        } catch (error) {
            setAddStationError(error instanceof Error ? error.message : "Failed to create station")
        } finally {
            setAddStationLoading(false)
        }
    }

    function handleCancelAddStation() {
        setPendingStation(null)
        setAddStationError(null)
    }

    const drawingMode = addMode || (sim.active && sim.step === "draw")

    const visibleLayerList = useMemo(
        () =>
            Array.from(visibleLayers).sort(
                (a, b) => getLayerRenderSortIndex(a) - getLayerRenderSortIndex(b)
            ),
        [visibleLayers]
    )

    const layerControlItems = useMemo(
        () =>
            Array.from(new Set([...Object.keys(layerLabels), ...Object.keys(layerData)])).sort(
                (a, b) => getLayerControlSortIndex(a) - getLayerControlSortIndex(b)
            ),
        [layerData]
    )

    return (
        <div
            className="-m-6 flex h-[calc(100vh-3.5rem)] w-full overflow-hidden bg-background"
            style={{ cursor: drawingMode ? "crosshair" : undefined }}
        >
            <div ref={mapShellRef} className="relative h-full min-w-0 flex-1 overflow-hidden">
                <MapContainer
                    center={[46.1512, 14.9955]}
                    zoom={8}
                    scrollWheelZoom
                    preferCanvas={false}
                    className="h-full w-full"
                >
                    <ResizeMapOnSidebarChange sidebarOpen={sidebarOpen} />
                    <MapClickHandler
                        addMode={addMode}
                        simMode={sim.active}
                        onMapClick={handleMapClick}
                        onSimClick={sim.addPoint}
                    />

                    <TileLayer
                        attribution="&copy; OpenStreetMap contributors"
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />

                    {visibleLayerList.map((layer) => {
                        const data = layerData[layer]
                        if (!data) return null

                        const renderedData =
                            layer === "stations"
                                ? {
                                      ...data,
                                      features: data.features.filter((feature) =>
                                          visibleStationSources.has(getStationSource(feature))
                                      ),
                                  }
                                : data

                        const geoJsonKey =
                            layer === "stations"
                                ? `${layer}:${Array.from(visibleStationSources).sort().join(",")}`
                                : `${layer}:${drawingMode}`

                        return (
                            <GeoJSON
                                key={geoJsonKey}
                                data={renderedData}
                                style={getStyle}
                                interactive={layer === "stations" || !drawingMode}
                                pointToLayer={pointToLayer}
                                onEachFeature={bindFeatureActions(openStationDetails)}
                            />
                        )
                    })}

                    {pendingStation && (
                        <CircleMarker
                            center={[pendingStation.lat, pendingStation.lng]}
                            radius={10}
                            pathOptions={{
                                color: "#111827",
                                weight: 2,
                                fillColor: "#ffffff",
                                fillOpacity: 0.9,
                                dashArray: "4 3",
                            }}
                        />
                    )}

                    <PolygonDrawOverlay
                        points={sim.points}
                        drawing={sim.active && sim.step === "draw"}
                        onPointUpdate={sim.updatePoint}
                        onPointRemove={sim.removePoint}
                    />
                </MapContainer>

                {!sidebarOpen && (
                    <button
                        type="button"
                        onClick={() => setSidebarOpen(true)}
                        className="absolute right-4 top-4 z-[1000] rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-900 shadow-lg hover:bg-gray-100 dark:border-[#4A4D50] dark:bg-[#2B2B2B] dark:text-[#D4D4D4] dark:hover:bg-[#1E1E1E]"
                    >
                        Show sidebar
                    </button>
                )}

                <LayerControlPanel
                    layerControlItems={layerControlItems}
                    visibleLayers={visibleLayers}
                    loadingLayers={loadingLayers}
                    errors={errors}
                    onToggleLayer={toggleLayer}
                    visibleStationSources={visibleStationSources}
                    onToggleStationSource={toggleStationSource}
                    mapContainerRef={mapShellRef}
                />

                <MapLegend mapContainerRef={mapShellRef} />

                <ErrorBanner
                    errors={errors}
                    onDismiss={(key: string) =>
                        setErrors((e) => { const n = { ...e }; delete n[key]; return n })
                    }
                />
            </div>

            {sidebarOpen && (
                sim.active ? (
                    <SimulationPanel
                        token={token}
                        step={sim.step}
                        points={sim.points}
                        stationsInArea={sim.stationsInArea}
                        channelsByStation={sim.channelsByStation}
                        config={sim.config}
                        loading={sim.loading}
                        error={sim.error}
                        result={sim.result}
                        onRemoveLast={sim.removeLastPoint}
                        onRemovePoint={sim.removePoint}
                        onReset={sim.reset}
                        onConfirm={sim.confirm}
                        onRun={sim.run}
                        onConfigChange={sim.setConfig}
                        onClose={sim.stop}
                        onRunAnother={sim.start}
                    />
                ) : (
                    <StationSidebar
                        selectedStation={selectedStation}
                        stationDetailsLoading={stationDetailsLoading}
                        stationDetailsError={stationDetailsError}
                        onCloseStation={() => setSelectedStation(null)}
                        onHideSidebar={() => setSidebarOpen(false)}
                        onExportGeoJson={exportVisibleGeoJson}
                        onImportDsl={handleSensumDslImport}
                        addMode={addMode}
                        onToggleAddMode={handleToggleAddMode}
                        pendingStation={pendingStation}
                        addStationLoading={addStationLoading}
                        addStationError={addStationError}
                        onSaveStation={handleSaveStation}
                        onCancelAddStation={handleCancelAddStation}
                        onToggleSim={() => { sim.start(); setSidebarOpen(true) }}
                    />
                )
            )}
        </div>
    )
}
