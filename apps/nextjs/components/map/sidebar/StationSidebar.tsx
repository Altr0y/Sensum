"use client"

import {useEffect, useState} from "react"
import type {ChangeEvent} from "react"
import {Lasso, MapPin, FileCode2, PanelRightClose, Dices, X, Pencil} from "lucide-react"
import type {PendingStationPoint, SelectedStationDetails, StationDto} from "../shared/types"
import {formatCoordinate} from "../shared/formatters"
import {ChannelCard} from "./ChannelCard"
import {AddStationPanel} from "./AddStationPanel"
import Link from "next/link"
import {LayoutList, BarChart2, Trash2, ExternalLink} from "lucide-react"
import {useTheme} from "next-themes"
import {createChannels, fetchStationTimeRange} from "../shared/api"
import {SimulationForm} from "../simulation/SimulationForm"
import type {SimConfig} from "../simulation/types"
import {makeDefaultConfig, localInputToIso} from "../simulation/utils"
import {runSimulation} from "../shared/api"
import {updateStation} from "../shared/api"

type Props = {
    selectedStation: SelectedStationDetails | null
    stationDetailsLoading: boolean
    stationDetailsError: string | null
    onCloseStation: () => void
    onHideSidebar: () => void
    onExportGeoJson: () => void
    onImportDsl: (event: ChangeEvent<HTMLInputElement>) => void
    addMode: boolean
    onToggleAddMode: () => void
    pendingStation: PendingStationPoint | null
    addStationLoading: boolean
    addStationError: string | null
    onSaveStation: (name: string, description: string, serialNumber: string) => void
    onCancelAddStation: () => void
    onToggleSim: () => void
    onDeleteStation: (stationId: number) => void
    token: string
    onRegenerateComplete: () => void
    onStationUpdated: (station: StationDto) => void
    moveModeStationId: number | null
    onToggleMoveMode: () => void
    editCoords: { latitude: number, longitude: number } | null
    onClearEditCoords: () => void
    onExitMoveMode: () => void

}

export function StationSidebar({
                                   selectedStation,
                                   stationDetailsLoading,
                                   stationDetailsError,
                                   onCloseStation,
                                   onHideSidebar,
                                   onExportGeoJson,
                                   onImportDsl,
                                   addMode,
                                   onToggleAddMode,
                                   pendingStation,
                                   addStationLoading,
                                   addStationError,
                                   onSaveStation,
                                   onCancelAddStation,
                                   onToggleSim,
                                   onDeleteStation,
                                   token,
                                   onRegenerateComplete,
                                   onStationUpdated,
                                   moveModeStationId,
                                   onToggleMoveMode,
                                   editCoords,
                                   onClearEditCoords,
                                   onExitMoveMode
                               }: Props) {
    const [activeTab, setActiveTab] = useState<"channels" | "graph">("channels")
    const [dataMenuOpen, setDataMenuOpen] = useState(false)
    const [stationTimeRange, setStationTimeRange] = useState<{ from: string, to: string } | null>(null)
    const {resolvedTheme} = useTheme()
    const grafanaFrom = stationTimeRange ? new Date(stationTimeRange.from).getTime() : null
    const grafanaTo = stationTimeRange ? new Date(stationTimeRange.to).getTime() : null
    const [regenerateMode, setRegenerateMode] = useState(false)
    const [regenConfig, setRegenConfig] = useState<SimConfig>(makeDefaultConfig)
    const [regenLoading, setRegenLoading] = useState(false)
    const [regenError, setRegenError] = useState<string | null>(null)
    const [editMode, setEditMode] = useState(false)
    const [editName, setEditName] = useState("")
    const [editDescription, setEditDescription] = useState("")
    const [editSerialNumber, setEditSerialNumber] = useState("")
    const [editLoading, setEditLoading] = useState(false)
    const [editError, setEditError] = useState<string | null>(null)

    useEffect(() => {
        setActiveTab("channels")
        setStationTimeRange(null)
        setRegenerateMode(false)
        setRegenConfig(makeDefaultConfig())
        setRegenError(null)
        setEditMode(false)
        setEditError(null)
        if (selectedStation) {
            setEditName(selectedStation.station.name ?? "")
            setEditDescription(selectedStation.station.description ?? "")
            setEditSerialNumber(selectedStation.station.serialNumber ?? "")
        }
    }, [selectedStation?.station.stationId])

    useEffect(() => {
        if (!selectedStation || activeTab !== "graph") return
        setStationTimeRange(null) // reset na loading
        fetchStationTimeRange(selectedStation.station.stationId).then((range) => {
            if (range.from && range.to) {
                setStationTimeRange({from: range.from, to: range.to})
            }
        })
    }, [selectedStation?.station.stationId, activeTab])

    useEffect(() => {
        if (selectedStation) {
            setEditName(selectedStation.station.name ?? "")
            setEditDescription(selectedStation.station.description ?? "")
            setEditSerialNumber(selectedStation.station.serialNumber ?? "")
        }
        setEditMode(false)
        setEditError(null)
    }, [selectedStation?.station.stationId])

    async function handleRegenerate() {
        setRegenLoading(true)
        setRegenError(null)
        try {
            let channelIds = selectedStation!.channels.map((c) => c.channelId)

            if (channelIds.length === 0 && regenConfig.channelKinds.length > 0) {
                const newChannels = await createChannels(
                    selectedStation!.station.stationId,
                    regenConfig.channelKinds,
                    token
                )
                channelIds = newChannels.map((c) => c.channelId)
            }

            await runSimulation({
                mode: "measurements_only",
                stationId: selectedStation!.station.stationId,
                channelIds,
                from: localInputToIso(regenConfig.from),
                to: localInputToIso(regenConfig.to),
                intervalMinutes: regenConfig.intervalMinutes,
            }, token)

            setRegenerateMode(false)
            onRegenerateComplete()
        } catch (e) {
            setRegenError(e instanceof Error ? e.message : "Regeneration failed")
        } finally {
            setRegenLoading(false)
        }
    }

    async function handleSaveEdit() {
        if (!selectedStation) return
        setEditLoading(true)
        setEditError(null)
        try {
            const updated = await updateStation(selectedStation.station.stationId, {
                name: editName,
                description: editDescription,
                serialNumber: editSerialNumber,
                ...(editCoords ? {latitude: editCoords.latitude, longitude: editCoords.longitude} : {}),
            }, token)
            onStationUpdated(updated)
            onClearEditCoords()
            setEditMode(false)
            onExitMoveMode() // ce setter ni dostopen tu, uporabi onToggleMoveMode ali poseben clear // ce setter ni dostopen tu, uporabi onToggleMoveMode ali poseben clear
        } catch (e) {
            setEditError(e instanceof Error ? e.message : "Update failed")
        } finally {
            setEditLoading(false)
        }
    }

    return (
        <aside
            className="h-full w-[430px] shrink-0 overflow-hidden border-l border-border bg-card text-foreground shadow-xl dark:border-border dark:bg-card dark:text-foreground">
            <div className="flex h-full flex-col">
                <div className="border-b border-border p-4">
                    <div className="flex items-start justify-between gap-3">
                        <div>
                            <h2 className="text-base font-semibold text-foreground">
                                Station details
                            </h2>

                            <p className="text-xs text-muted-foreground">
                                {addMode
                                    ? "Click on the map to place a new station."
                                    : "Click a station on the map to view channels and measurements."}
                            </p>
                        </div>

                        <div className="flex items-center gap-1">
                            <button
                                type="button"
                                onClick={onToggleSim}
                                title="Area simulation tool"
                                className="flex h-10 w-10 items-center justify-center rounded-lg border border-border bg-background text-foreground shadow-sm hover:bg-muted hover:border-accent hover:text-accent transition-colors"
                            >
                                <Lasso size={22}/>
                            </button>

                            <button
                                type="button"
                                onClick={onToggleAddMode}
                                title={addMode ? "Exit add mode" : "Add station"}
                                className={[
                                    "flex h-10 w-10 items-center justify-center rounded-lg border text-sm shadow-sm transition-colors",
                                    addMode
                                        ? "border-foreground bg-foreground text-background"
                                        : "border-border bg-background text-foreground hover:bg-muted hover:border-accent hover:text-accent transition-colors",
                                ].join(" ")}
                            >
                                <MapPin size={22}/>
                            </button>

                            <div className="relative">
                                <button
                                    type="button"
                                    onClick={() => setDataMenuOpen((current) => !current)}
                                    className="flex h-10 w-10 items-center justify-center rounded-lg border border-border bg-background text-lg text-foreground shadow-sm hover:bg-muted hover:border-accent hover:text-accent transition-colors"
                                    title="Settings"
                                >
                                    <FileCode2 size={22}/>
                                </button>

                                {dataMenuOpen && (
                                    <div
                                        className="absolute right-0 top-11 z-[1200] w-56 rounded-xl border border-border bg-white p-2 text-foreground shadow-xl dark:bg-[#09090b] dark:text-foreground">
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

                                        <div className="my-1 border-t border-border"/>

                                        <button
                                            type="button"
                                            className="block w-full rounded-md px-3 py-2 text-left text-sm text-foreground hover:bg-muted dark:hover:bg-muted"
                                            onClick={() => {
                                                setDataMenuOpen(false)
                                                onExportGeoJson()
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
                                    onChange={onImportDsl}
                                />
                            </div>

                            <button
                                type="button"
                                onClick={onHideSidebar}
                                className="flex h-10 w-10 items-center justify-center rounded-lg border border-border bg-background text-foreground shadow-sm hover:bg-muted hover:border-accent hover:text-accent transition-colors"
                                title="Hide sidebar"
                            >
                                <PanelRightClose size={22}/>
                            </button>
                        </div>
                    </div>
                </div>

                {pendingStation ? (
                    <AddStationPanel
                        pending={pendingStation}
                        loading={addStationLoading}
                        error={addStationError}
                        onSave={onSaveStation}
                        onCancel={onCancelAddStation}
                    />
                ) : !selectedStation ? (
                    <div className="flex flex-1 items-center justify-center p-6 text-center">
                        <div>
                            <div className="mb-3 text-4xl">{addMode ? "🖱️" : "📍"}</div>

                            <h3 className="text-sm font-semibold text-foreground">
                                {addMode ? "Click on the map" : "No station selected"}
                            </h3>

                            <p className="mt-1 text-sm text-muted-foreground">
                                {addMode
                                    ? "Click anywhere on the map to place a new MANUAL station."
                                    : "Enable the Stations layer and click a marker on the map."}
                            </p>
                        </div>
                    </div>
                ) : (
                    <>
                        <div className="border-b border-border p-4">
                            <div className="flex items-start justify-between gap-3">
                                <div>
                                    <h3 className="text-lg font-semibold text-foreground">
                                        {selectedStation.station.name ?? `Station ${selectedStation.station.stationId}`}
                                    </h3>

                                    <div className="flex items-center gap-2">
                                        <p className="text-xs text-muted-foreground">
                                            ID: {selectedStation.station.stationId}
                                        </p>

                                        {selectedStation.station.source && selectedStation.station.source !== "UNKNOWN" && (
                                            <span
                                                className="rounded-full border border-border bg-background px-2 py-0.5 font-mono text-[10px] font-semibold text-foreground">
                                                {selectedStation.station.source}
                                            </span>
                                        )}
                                    </div>
                                </div>

                                <button
                                    type="button"
                                    onClick={onCloseStation}
                                    className="rounded-md px-2 py-1 text-sm text-muted-foreground hover:bg-muted"
                                >
                                    <X size={18}/>
                                </button>
                            </div>

                            <div className="mt-4 grid grid-cols-2 gap-2 text-xs">
                                <div className="rounded-lg border border-border bg-background p-2">
                                    <div className="text-muted-foreground">Type</div>
                                    <div className="font-medium">{selectedStation.station.stationType ?? "-"}</div>
                                </div>

                                <div className="rounded-lg border border-border bg-background p-2">
                                    <div className="text-muted-foreground">Modbus</div>
                                    <div className="font-medium">{selectedStation.station.modbusAddress ?? "-"}</div>
                                </div>

                                <div className="rounded-lg border border-border bg-background p-2">
                                    <div className="text-muted-foreground">Latitude</div>
                                    <div
                                        className="font-medium">{formatCoordinate(selectedStation.station.latitude)}</div>
                                </div>

                                <div className="rounded-lg border border-border bg-background p-2">
                                    <div className="text-muted-foreground">Longitude</div>
                                    <div
                                        className="font-medium">{formatCoordinate(selectedStation.station.longitude)}</div>
                                </div>
                            </div>

                            {selectedStation.station.serialNumber && (
                                <div className="mt-2 rounded-lg border border-border bg-background p-2 text-xs">
                                    <span className="text-muted-foreground">Serial number: </span>
                                    <span className="font-medium">{selectedStation.station.serialNumber}</span>
                                </div>
                            )}

                            {selectedStation.station.description && (
                                <p className="mt-2 rounded-lg border border-border bg-background p-2 text-xs text-muted-foreground">
                                    {selectedStation.station.description}
                                </p>
                            )}
                        </div>

                        {/* Tab bar */}
                        <div className="flex border-b border-border shrink-0">
                            <button
                                type="button"
                                onClick={() => {
                                    setActiveTab("channels")
                                    setRegenerateMode(false)
                                    setEditMode(false)
                                }}
                                className={`px-4 py-2.5 text-sm font-medium transition-colors relative ${
                                    !regenerateMode && !editMode && activeTab === "channels"
                                        ? "text-[#E8A838]"
                                        : "text-muted-foreground hover:text-foreground"
                                }`}
                            >
                                <LayoutList size={22}/>
                                {!regenerateMode && !editMode && activeTab === "channels" && (
                                    <div className="absolute bottom-0 left-2 right-2 h-[2px] bg-[#E8A838] rounded-t"/>
                                )}
                            </button>
                            <button
                                type="button"
                                onClick={() => {
                                    setActiveTab("graph")
                                    setRegenerateMode(false)
                                    setEditMode(false)
                                }}
                                className={`px-4 py-2.5 text-sm font-medium transition-colors relative ${
                                    !regenerateMode && !editMode && activeTab === "graph"
                                        ? "text-[#E8A838]"
                                        : "text-muted-foreground hover:text-foreground"
                                }`}
                            >
                                <BarChart2 size={22}/>
                                {!regenerateMode && !editMode && activeTab === "graph" && (
                                    <div className="absolute bottom-0 left-2 right-2 h-[2px] bg-[#E8A838] rounded-t"/>
                                )}
                            </button>
                            <Link
                                href={`/monitoring?dashboard=measurements&stationId=${selectedStation.station.stationId}${stationTimeRange ? `&from=${new Date(stationTimeRange.from).getTime()}&to=${new Date(stationTimeRange.to).getTime()}` : ""}`}
                                title="View in Monitoring"
                                className="px-4 py-2.5 text-sm font-medium text-muted-foreground hover:text-[#E8A838] transition-colors"
                            >
                                <ExternalLink size={22}/>
                            </Link>
                            <button
                                type="button"
                                onClick={() => {
                                    setEditMode((v) => !v)
                                    setRegenerateMode(false)
                                    if (moveModeStationId !== null) onExitMoveMode()
                                }}
                                title="Edit station details"
                                className={`px-4 py-2.5 text-sm font-medium transition-colors relative ${
                                    editMode
                                        ? "text-[#E8A838]"
                                        : "text-muted-foreground hover:text-foreground"
                                }`}
                            >
                                <Pencil size={22}/>
                                {editMode && (
                                    <div className="absolute bottom-0 left-2 right-2 h-[2px] bg-[#E8A838] rounded-t"/>
                                )}
                            </button>
                            <button
                                type="button"
                                onClick={() => {
                                    setRegenerateMode((v) => !v)
                                    setEditMode(false)
                                }}
                                title="Regenerate measurements"
                                className={`px-4 py-2.5 text-sm font-medium transition-colors relative ${
                                    regenerateMode
                                        ? "text-[#E8A838]"
                                        : "text-muted-foreground hover:text-foreground"
                                }`}
                            >
                                <Dices size={22}/>
                                {regenerateMode && (
                                    <div className="absolute bottom-0 left-2 right-2 h-[2px] bg-[#E8A838] rounded-t"/>
                                )}
                            </button>
                            <button
                                type="button"
                                onClick={() => onDeleteStation(selectedStation.station.stationId)}
                                title="Delete station"
                                className="ml-auto px-3 py-2.5 text-sm font-medium transition-colors relative text-muted-foreground hover:text-destructive"
                            >
                                <Trash2 size={22}/>
                            </button>
                        </div>

                        {editMode ? (
                            <div className="flex-1 overflow-y-auto p-4 space-y-3">

                                <p className="text-sm font-medium text-foreground">Edit station details</p>

                                <label className="block">
                                    <span className="text-xs text-muted-foreground">Name</span>
                                    <input
                                        type="text"
                                        value={editName}
                                        onChange={(e) => setEditName(e.target.value)}
                                        className="w-full rounded-md border border-border bg-background px-2 py-1.5 text-sm text-foreground focus:outline-none focus:ring-1 focus:ring-[#E8A838]"
                                    />
                                </label>

                                <label className="block">
                                    <span className="text-xs text-muted-foreground">Description</span>
                                    <textarea
                                        value={editDescription}
                                        onChange={(e) => setEditDescription(e.target.value)}
                                        rows={2}
                                        className="w-full rounded-md border border-border bg-background px-2 py-1.5 text-sm text-foreground focus:outline-none focus:ring-1 focus:ring-[#E8A838] resize-none"
                                    />
                                </label>

                                <label className="block">
                                    <span className="text-xs text-muted-foreground">Serial number</span>
                                    <input
                                        type="text"
                                        value={editSerialNumber}
                                        onChange={(e) => setEditSerialNumber(e.target.value)}
                                        className="w-full rounded-md border border-border bg-background px-2 py-1.5 text-sm text-foreground focus:outline-none focus:ring-1 focus:ring-[#E8A838]"
                                    />
                                </label>
                                <div className="space-y-1.5">
                                    <span className="text-xs text-muted-foreground">Location</span>
                                    <div className="grid grid-cols-2 gap-3">
                                        <div
                                            className="rounded-md border border-border bg-background px-2 py-1.5 text-sm text-foreground">
                                            {(editCoords?.latitude ?? selectedStation.station.latitude)?.toFixed(6) ?? "-"}
                                        </div>
                                        <div
                                            className="rounded-md border border-border bg-background px-2 py-1.5 text-sm text-foreground">
                                            {(editCoords?.longitude ?? selectedStation.station.longitude)?.toFixed(6) ?? "-"}
                                        </div>
                                    </div>
                                    <button
                                        type="button"
                                        onClick={onToggleMoveMode}
                                        className={[
                                            "w-full rounded-md border px-3 py-1.5 text-xs font-medium transition-colors",
                                            moveModeStationId === selectedStation.station.stationId
                                                ? "border-[#E8A838] bg-[#E8A838]/10 text-[#E8A838]"
                                                : "border-border bg-background text-muted-foreground hover:text-foreground hover:bg-muted",
                                        ].join(" ")}
                                    >
                                        {moveModeStationId === selectedStation.station.stationId
                                            ? "Drag the marker on the map to set new location..."
                                            : "Move on map"}
                                    </button>
                                </div>

                                {editError && (
                                    <div
                                        className="rounded-lg border border-[#CF6679]/30 bg-[#CF6679]/10 p-3 text-xs text-[#CF6679]">
                                        {editError}
                                    </div>
                                )}

                                <button
                                    onClick={handleSaveEdit}
                                    disabled={editLoading}
                                    className="w-full flex items-center justify-center gap-2 rounded-lg bg-[#E8A838] text-[#1E1E1E] py-2 text-sm font-medium hover:bg-[#d4962e] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                                >
                                    {editLoading ? "Saving..." : "Save changes"}
                                </button>
                            </div>
                        ) : regenerateMode ? (
                            <div className="flex-1 overflow-y-auto">
                                <div className="border-b border-border p-3">
                                    <p className="text-sm font-medium text-foreground">
                                        Regenerate measurements
                                    </p>
                                    <p className="text-xs text-muted-foreground mt-0.5">
                                        Existing data in this range will be regenerated for
                                        all {selectedStation.channels.length} channel(s).
                                    </p>
                                </div>
                                <SimulationForm
                                    stationsInArea={[selectedStation.station]}
                                    channelsByStation={{[selectedStation.station.stationId]: selectedStation.channels}}
                                    config={regenConfig}
                                    loading={regenLoading}
                                    error={regenError}
                                    onConfigChange={setRegenConfig}
                                    onRun={handleRegenerate}
                                    regenerateMode
                                />
                            </div>
                        ) : activeTab === "channels" ? (
                            <div className="flex-1 overflow-y-auto p-4">
                                {stationDetailsLoading && (
                                    <p className="text-sm text-muted-foreground">Loading station details...</p>
                                )}
                                {stationDetailsError && (
                                    <p className="rounded-lg border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-600 dark:text-red-400">
                                        {stationDetailsError}
                                    </p>
                                )}
                                {!stationDetailsLoading && selectedStation.channels.length === 0 && (
                                    <p className="text-sm text-muted-foreground">No channels found for this station.</p>
                                )}
                                <div className="space-y-4">
                                    {selectedStation.channels.map((channel) => (
                                        <ChannelCard
                                            key={channel.channelId}
                                            channel={channel}
                                            measurements={selectedStation.measurementsByChannel[channel.channelId] ?? []}
                                        />
                                    ))}
                                </div>
                            </div>
                        ) : stationTimeRange === null ? (
                            <div className="flex flex-1 items-center justify-center text-muted-foreground text-sm">
                                Loading graph...
                            </div>
                        ) : (
                            <div className="flex-1 overflow-hidden">


                                <iframe


                                    key={`${selectedStation.station.stationId}-${stationTimeRange?.from ?? "loading"}`}
                                    src={`/grafana/d/sensum-measurements/meritve?orgId=1&kiosk&theme=${resolvedTheme === "light" ? "light" : "dark"}&var-stationId=${selectedStation.station.stationId}${grafanaFrom && grafanaTo ? `&from=${grafanaFrom}&to=${grafanaTo}` : ""}`}
                                    className="h-full w-full border-0"
                                    title="Station graph"
                                />
                            </div>
                        )}
                    </>
                )}
            </div>
        </aside>
    )
}
