"use client"

import {useEffect, useMemo, useRef, useState} from "react"
import type {ChangeEvent} from "react"
import {CircleMarker, GeoJSON, MapContainer, TileLayer, useMapEvents} from "react-leaflet"
import {
    createStation,
    deleteStation,
    fetchChannels,
    fetchMeasurements,
    fetchStations,
    fetchStationsGeoJson,
    processDsl
} from "../shared/api"
import {AlertTriangle, X, PanelRightOpen, Trash2} from "lucide-react"
import {useSimulation} from "../simulation/useSimulation"
import {PolygonDrawOverlay} from "./PolygonDrawOverlay"
import {SimulationPanel} from "../simulation/SimulationPanel"
import {MapLegend} from "./MapLegend"
import {
    defaultVisibleLayers,
    defaultVisibleStationSources,
    getStationSource,
    layerLabels,
    layerSources,
    normalizeLayerData,
    getLayerControlSortIndex,
    getLayerRenderSortIndex
} from "../layers/layerConfig"
import {bindFeatureActions, getStyle, pointToLayer} from "../layers/mapStyles"
import {formatApiLocalDateTime} from "../shared/formatters"
import {downloadTextFile} from "../shared/utils"
import {ResizeMapOnSidebarChange} from "./ResizeMapOnSidebarChange"
import {LayerControlPanel} from "../layers/LayerControlPanel"
import {StationSidebar} from "../sidebar/StationSidebar"
import type {PendingStationPoint, SelectedStationDetails, SensumGeoJson, StationDto} from "../shared/types"

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
        <div
            className="absolute top-4 left-1/2 -translate-x-1/2 z-[1100] flex flex-col gap-2 w-[420px] max-w-[calc(100%-2rem)]">
            {entries.map(([key, msg]) => (
                <div
                    key={key}
                    className="flex items-start gap-3 rounded-lg border border-destructive/40 bg-card px-3 py-2.5 shadow-lg text-sm"
                >
                    <AlertTriangle size={15} className="text-destructive shrink-0 mt-0.5"/>
                    <div className="flex-1 min-w-0">
                        <span className="font-medium text-destructive">{key}: </span>
                        <span className="text-muted-foreground">{msg}</span>
                        {msg.includes("log in") && (
                            <a href="/login" className="ml-2 underline text-[#E8A838] hover:text-[#d4962e]">
                                Log in
                            </a>
                        )}
                    </div>
                    <button
                        onClick={() => onDismiss(key)}
                        className="shrink-0 text-muted-foreground hover:text-foreground transition-colors"
                    >
                        <X size={14}/>
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

const LAYERS_STORAGE_KEY = "sensum-visible-layers"
const STATION_SOURCES_STORAGE_KEY = "sensum-visible-station-sources"

function loadStoredSet(key: string, fallback: Set<string>): Set<string> {
    if (typeof window === "undefined") return fallback
    try {
        const raw = window.localStorage.getItem(key)
        if (!raw) return fallback
        const arr = JSON.parse(raw)
        if (Array.isArray(arr)) return new Set(arr)
    } catch {
        // ignore
    }
    return fallback
}

function saveStoredSet(key: string, value: Set<string>) {
    if (typeof window === "undefined") return
    try {
        window.localStorage.setItem(key, JSON.stringify(Array.from(value)))
    } catch {
        // ignore
    }
}

export function SensumMap({token}: { token: string }) {
    const mapShellRef = useRef<HTMLDivElement | null>(null)

    const [layerData, setLayerData] = useState<Record<string, SensumGeoJson>>({})
    const [visibleLayers, setVisibleLayers] = useState<Set<string>>(
        () => loadStoredSet(LAYERS_STORAGE_KEY, new Set(defaultVisibleLayers))
    )
    const [loadingLayers, setLoadingLayers] = useState<Set<string>>(() => new Set())
    const [errors, setErrors] = useState<Record<string, string>>({})
    const [visibleStationSources, setVisibleStationSources] = useState<Set<string>>(
        () => loadStoredSet(STATION_SOURCES_STORAGE_KEY, new Set(defaultVisibleStationSources))
    )

    const [stations, setStations] = useState<StationDto[]>([])
    const [selectedStation, setSelectedStation] = useState<SelectedStationDetails | null>(null)
    const [selectedStationIds, setSelectedStationIds] = useState<Set<number>>(new Set())
    const [bulkDeleteLoading, setBulkDeleteLoading] = useState(false)
    const [stationDetailsLoading, setStationDetailsLoading] = useState(false)
    const [stationDetailsError, setStationDetailsError] = useState<string | null>(null)
    const [sidebarOpen, setSidebarOpen] = useState(true)

    const [addMode, setAddMode] = useState(false)
    const [pendingStation, setPendingStation] = useState<PendingStationPoint | null>(null)
    const [addStationLoading, setAddStationLoading] = useState(false)
    const [addStationError, setAddStationError] = useState<string | null>(null)
    const [moveModeStationId, setMoveModeStationId] = useState<number | null>(null)
    const [editCoords, setEditCoords] = useState<{ latitude: number, longitude: number } | null>(null)

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
                        const next = {...current}
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
                    const next = {...current}
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

    useEffect(() => {
        if (selectedStationIds.size === 1) {
            const [onlyId] = selectedStationIds
            if (selectedStation?.station.stationId !== onlyId) {
                openStationDetails(onlyId)
            }
        } else if (selectedStationIds.size === 0 && selectedStation) {
            setSelectedStation(null)
        } else if (selectedStationIds.size > 1 && selectedStation) {
            setSelectedStation(null)
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [selectedStationIds])

    useEffect(() => {
        function handleKeyDown(e: KeyboardEvent) {
            if ((e.key === "Delete" || e.key === "Backspace") && selectedStationIds.size > 1) {
                const target = e.target as HTMLElement
                if (target.tagName === "INPUT" || target.tagName === "TEXTAREA") return
                e.preventDefault()
                handleBulkDelete()
            }
        }

        window.addEventListener("keydown", handleKeyDown)
        return () => window.removeEventListener("keydown", handleKeyDown)
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [selectedStationIds])

    useEffect(() => {
        setMoveModeStationId(null)
        setEditCoords(null)
    }, [selectedStation?.station.stationId])

    useEffect(() => {
        saveStoredSet(LAYERS_STORAGE_KEY, visibleLayers)
    }, [visibleLayers])

    useEffect(() => {
        saveStoredSet(STATION_SOURCES_STORAGE_KEY, visibleStationSources)
    }, [visibleStationSources])

    function handleStationMoved(stationId: number, lat: number, lng: number) {
        setEditCoords({latitude: lat, longitude: lng})
    }

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

    function handleStationClick(stationId: number, isMultiSelect: boolean) {
        if (isMultiSelect) {
            setSelectedStationIds((current) => {
                const next = new Set(current)
                if (next.has(stationId)) {
                    next.delete(stationId)
                } else {
                    next.add(stationId)
                }
                return next
            })
            // ce po toggle ostane samo 1 ali 0, posodobi tudi single-detail sidebar
            return
        }
        setSelectedStationIds(new Set([stationId]))
        openStationDetails(stationId)
    }

    async function openStationDetails(stationId: number) {
        const station = stations.find((item) => item.stationId === stationId)
        if (!station) return

        setSidebarOpen(true)
        setSelectedStation({station, channels: [], measurementsByChannel: {}})
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

    async function handleBulkDelete() {
        if (selectedStationIds.size === 0) return
        if (!confirm(`Are you sure you want to delete ${selectedStationIds.size} stations?`)) return
        setBulkDeleteLoading(true)
        try {
            for (const id of selectedStationIds) {
                await deleteStation(id)
            }
            setSelectedStationIds(new Set())
            setSelectedStation(null)
            setStations([])
            setLayerData((current) => {
                const next = {...current}
                delete next.stations
                return next
            })
        } catch (e) {
            console.error("Bulk delete failed", e)
        } finally {
            setBulkDeleteLoading(false)
        }
    }

    function exportVisibleGeoJson() {
        const features = Array.from(visibleLayers)
            .sort((a, b) => getLayerRenderSortIndex(a) - getLayerRenderSortIndex(b))
            .flatMap((layer) => layerData[layer]?.features ?? [])

        const geoJson: SensumGeoJson = {type: "FeatureCollection", features}

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
            const next = {...current}
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
        setPendingStation({lat, lng})
        setAddStationError(null)
        setSidebarOpen(true)
        setSelectedStation(null)
    }

    function handleStationUpdated(updated: StationDto) {
        setSelectedStation((current) =>
            current ? {...current, station: updated} : current
        )
        setStations([])
        setLayerData((current) => {
            const next = {...current}
            delete next.stations
            return next
        })
    }

    function onExitMoveMode() {
        setMoveModeStationId(null)
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
                const next = {...current}
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

    async function handleDeleteStation(stationId: number) {
        if (!confirm("Are you sure you want to delete this station?")) return
        try {
            await deleteStation(stationId)
            setSelectedStation(null)
            // reload stations
            setStations([])
            setLayerData((current) => {
                const next = {...current}
                delete next.stations
                return next
            })
        } catch (e) {
            console.error("Delete failed", e)
        }
    }

    async function refreshSelectedStationMeasurements() {
        if (!selectedStation) return
        const stationId = selectedStation.station.stationId
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

            setSelectedStation((current) =>
                current ? {
                    ...current,
                    channels,
                    measurementsByChannel: Object.fromEntries(measurementsByChannelEntries),
                } : current
            )
        } catch (error) {
            setStationDetailsError(
                error instanceof Error ? error.message : "Failed to refresh measurements"
            )
        } finally {
            setStationDetailsLoading(false)
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
            className="flex h-[calc(100vh-3.5rem)] w-full overflow-hidden bg-background"
            style={{cursor: drawingMode ? "crosshair" : undefined}}
        >
            <div ref={mapShellRef} className="relative h-full min-w-0 flex-1 overflow-hidden">
                <MapContainer
                    center={[46.1512, 14.9955]}
                    zoom={8}
                    scrollWheelZoom
                    preferCanvas={false}
                    className="h-full w-full"
                >
                    <ResizeMapOnSidebarChange sidebarOpen={sidebarOpen}/>
                    <MapClickHandler
                        addMode={addMode}
                        simMode={sim.active && sim.step === "draw"}
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

                        if (layer === "stations" && visibleStationSources.size === 0) return null

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
                                ? `${layer}:${Array.from(visibleStationSources).sort().join(",")}:${Array.from(selectedStationIds).sort().join(",")}:${moveModeStationId ?? "none"}`
                                : `${layer}:${drawingMode}`

                        return (
                            <GeoJSON
                                key={geoJsonKey}
                                data={renderedData}
                                style={getStyle}
                                interactive={layer === "stations" || !drawingMode}
                                pointToLayer={(feature, latlng) =>
                                    pointToLayer(feature, latlng, selectedStationIds, moveModeStationId)
                                }
                                onEachFeature={bindFeatureActions(handleStationClick, handleStationMoved, moveModeStationId)}
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

                {selectedStationIds.size > 1 && (
                    <div
                        className="absolute bottom-4 left-1/2 -translate-x-1/2 z-[1000] flex items-center gap-3 rounded-lg border border-border bg-card px-4 py-2.5 shadow-lg">
        <span className="text-sm font-medium text-foreground">
            {selectedStationIds.size} stations selected
        </span>
                        <button
                            type="button"
                            onClick={handleBulkDelete}
                            disabled={bulkDeleteLoading}
                            className="flex items-center gap-1.5 rounded-md bg-destructive px-3 py-1.5 text-sm font-medium text-white transition-colors hover:opacity-90 disabled:opacity-50"
                        >
                            <Trash2 size={16}/>
                            {bulkDeleteLoading ? "Deleting..." : "Delete selected"}
                        </button>
                        <button
                            type="button"
                            onClick={() => setSelectedStationIds(new Set())}
                            className="text-sm text-muted-foreground hover:text-foreground"
                        >
                            <X size={18}/>
                        </button>
                    </div>
                )}

                {!sidebarOpen && (
                    <button
                        type="button"
                        onClick={() => setSidebarOpen(true)}
                        className="absolute right-4 top-4 z-[1000] flex h-10 w-10 items-center justify-center rounded-lg border border-border bg-card text-foreground shadow-lg hover:bg-muted hover:border-accent hover:text-accent transition-colors"
                        title="Show sidebar"
                    >
                        <PanelRightOpen size={22}/>
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

                <MapLegend mapContainerRef={mapShellRef}/>

                <ErrorBanner
                    errors={errors}
                    onDismiss={(key: string) =>
                        setErrors((e) => {
                            const n = {...e};
                            delete n[key];
                            return n
                        })
                    }
                />
            </div>

            <div
                className="h-full shrink-0 overflow-hidden transition-all duration-300 ease-in-out"
                style={{width: sidebarOpen ? "430px" : "0px"}}
            >
                {sim.active ? (
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
                        onRun={async () => {
                            await sim.run(token)
                            // reload stations layer
                            setStations([])
                            setLayerData((current) => {
                                const next = {...current}
                                delete next.stations
                                return next
                            })
                        }}
                        onConfigChange={sim.setConfig}
                        onClose={sim.stop}
                        onRunAnother={sim.start}
                    />
                ) : (
                    <StationSidebar
                        selectedStation={selectedStation}
                        stationDetailsLoading={stationDetailsLoading}
                        stationDetailsError={stationDetailsError}
                        onCloseStation={() => {
                            setSelectedStation(null)
                            setSelectedStationIds(new Set())
                        }}
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
                        onDeleteStation={handleDeleteStation}
                        token={token}
                        onRegenerateComplete={refreshSelectedStationMeasurements}
                        onStationUpdated={handleStationUpdated}
                        moveModeStationId={moveModeStationId}
                        onToggleMoveMode={() => setMoveModeStationId((current) =>
                            current === selectedStation?.station.stationId ? null : (selectedStation?.station.stationId ?? null)
                        )}
                        editCoords={editCoords}
                        onClearEditCoords={() => setEditCoords(null)}
                        onExitMoveMode={onExitMoveMode}
                        onToggleSim={() => {
                            setAddMode(false);
                            setPendingStation(null);
                            setAddStationError(null);
                            sim.start();
                            setSidebarOpen(true)
                        }}
                    />
                )}
            </div>
        </div>
    )
}
