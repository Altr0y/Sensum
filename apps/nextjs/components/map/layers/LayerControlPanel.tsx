"use client"

import {ChevronDown, ChevronRight, Minus, Plus} from "lucide-react"
import {useRef, useState} from "react"
import type {PointerEvent} from "react"
import type {PanelPosition} from "../shared/types"
import {
    getLayerLabel,
    layerGroups,
    layerShortLabels,
    stationSourceBadges,
    stationSourceColors,
    stationSourceLabels,
    stationSources
} from "./layerConfig"

type Props = {
    layerControlItems: string[]
    visibleLayers: Set<string>
    loadingLayers: Set<string>
    errors: Record<string, string>
    onToggleLayer: (layer: string) => void
    visibleStationSources: Set<string>
    onToggleStationSource: (source: string) => void
    mapContainerRef: React.RefObject<HTMLDivElement | null>
    hasPendingDslImport: boolean
    onConfirmDslImport: () => void
    dslSaveLoading: boolean
    dslSaveResult: string | null
    dslSaveError: string | null
}

const PANEL_WIDTH = 320
const PANEL_HEIGHT = 270

export function LayerControlPanel({
                                      layerControlItems,
                                      visibleLayers,
                                      loadingLayers,
                                      errors,
                                      onToggleLayer,
                                      visibleStationSources,
                                      onToggleStationSource,
                                      mapContainerRef,
                                      hasPendingDslImport,
                                      onConfirmDslImport,
                                      dslSaveLoading,
                                      dslSaveResult,
                                      dslSaveError,
                                  }: Props) {
    const [position, setPosition] = useState<PanelPosition>({x: 16, y: 16})
    const [collapsed, setCollapsed] = useState(false)
    const [sourcesExpanded, setSourcesExpanded] = useState(false)
    const [expandedGroups, setExpandedGroups] = useState<Set<string>>(new Set(["stations"]))
    const [isDragging, setIsDragging] = useState(false)
    const dragOffset = useRef<PanelPosition>({x: 0, y: 0})


    function toggleGroup(groupId: string) {
        setExpandedGroups((current) => {
            const next = new Set(current)
            next.has(groupId) ? next.delete(groupId) : next.add(groupId)
            return next
        })
    }

    function startDrag(event: PointerEvent<HTMLDivElement>) {
        if (event.button !== 0) return
        const panel = event.currentTarget.parentElement
        if (!panel) return
        const rect = panel.getBoundingClientRect()
        dragOffset.current = {x: event.clientX - rect.left, y: event.clientY - rect.top}
        setIsDragging(true)
        event.currentTarget.setPointerCapture(event.pointerId)
    }

    function onDrag(event: PointerEvent<HTMLDivElement>) {
        if (!isDragging) return
        const container = mapContainerRef.current
        if (!container) return
        const cr = container.getBoundingClientRect()
        const maxX = Math.max(0, cr.width - PANEL_WIDTH)
        const maxY = Math.max(0, cr.height - PANEL_HEIGHT)
        setPosition({
            x: Math.min(Math.max(0, event.clientX - cr.left - dragOffset.current.x), maxX),
            y: Math.min(Math.max(0, event.clientY - cr.top - dragOffset.current.y), maxY),
        })
    }

    function stopDrag(event: PointerEvent<HTMLDivElement>) {
        setIsDragging(false)
        if (event.currentTarget.hasPointerCapture(event.pointerId)) {
            event.currentTarget.releasePointerCapture(event.pointerId)
        }
    }

    const importedLayers = layerControlItems.filter((l) => l.startsWith("imported_"))

    function isGroupFullyVisible(layers: string[]) {
        return layers.length > 0 && layers.every((l) => visibleLayers.has(l))
    }

    function isGroupPartiallyVisible(layers: string[]) {
        return layers.some((l) => visibleLayers.has(l)) && !isGroupFullyVisible(layers)
    }

    function toggleGroupLayers(layers: string[]) {
        const allVisible = isGroupFullyVisible(layers)
        layers.forEach((l) => {
            const currentlyVisible = visibleLayers.has(l)
            if (allVisible && currentlyVisible) onToggleLayer(l)
            if (!allVisible && !currentlyVisible) onToggleLayer(l)
        })
    }

    const allSourcesSelected = stationSources.every((s) => visibleStationSources.has(s))
    const someSourcesSelected = stationSources.some((s) => visibleStationSources.has(s))
    return (
        <div
            className="absolute z-[1000] max-h-[calc(100%-2rem)] w-65 overflow-hidden rounded-xl border border-gray-300 bg-white shadow-lg dark:border-[#4A4D50] dark:bg-[#2B2B2B]"
            style={{left: position.x, top: position.y}}
        >
            <div
                className="cursor-move select-none border-b border-gray-300 px-4 py-3 dark:border-[#4A4D50]"
                style={{touchAction: "none"}}
                onPointerDown={startDrag}
                onPointerMove={onDrag}
                onPointerUp={stopDrag}
                onPointerCancel={stopDrag}
            >
                <div className="flex items-center justify-between gap-3">
                    <div>
                        <h2 className="text-sm font-semibold text-gray-900 dark:text-[#D4D4D4]">Map layers</h2>
                        <p className="text-xs text-gray-500 dark:text-[#7A7E82]">Layers are loaded only when
                            enabled.</p>
                    </div>
                    <div className="flex items-center gap-2">
                        <span
                            className="rounded-md border border-gray-300 bg-gray-50 px-2 py-1 text-[10px] font-medium text-gray-500 dark:border-[#4A4D50] dark:bg-[#1E1E1E] dark:text-[#7A7E82]">
                            Move
                        </span>
                        <button
                            type="button"
                            onPointerDown={(event) => event.stopPropagation()}
                            onClick={() => setCollapsed((c) => !c)}
                            className="flex h-5 w-5 items-center justify-center rounded text-gray-500 hover:text-gray-900 transition-colors dark:text-[#7A7E82] dark:hover:text-[#D4D4D4]"
                        >
                            {collapsed ? <Plus size={12}/> : <Minus size={12}/>}
                        </button>
                    </div>
                </div>
            </div>

            {!collapsed && (
                <div className="max-h-[calc(100vh-12rem)] overflow-y-auto p-4">
                    <div className="space-y-2">
                        {hasPendingDslImport && importedLayers.length > 0 && (
                            <div className="rounded-md border border-[#E8A838]/40 bg-[#E8A838]/10 p-2 space-y-1.5">
                                <p className="text-[11px] font-medium text-[#E8A838]">
                                    Pending DSL import
                                </p>
                                <p className="text-[10px] text-gray-600 dark:text-[#BBBBBB] leading-snug">
                                    Layers are previewed only. Save to add data to the database.
                                </p>
                                <button
                                    type="button"
                                    onClick={onConfirmDslImport}
                                    disabled={dslSaveLoading}
                                    className="w-full rounded-md bg-[#E8A838] text-[#1E1E1E] py-1 text-[11px] font-medium hover:bg-[#d4962e] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                                >
                                    {dslSaveLoading ? "Saving..." : "Save to database"}
                                </button>
                                {dslSaveError && (
                                    <p className="text-[10px] text-[#CF6679]">{dslSaveError}</p>
                                )}
                            </div>
                        )}
                        {layerGroups.map((group) => {
                            const layers = group.id === "imported" ? importedLayers : group.layers
                            const isExpanded = expandedGroups.has(group.id)
                            const isSingleLayer = layers.length === 1

                            if (group.id === "imported" && layers.length === 0) {
                                return (
                                    <div key={group.id}>
                                        <div
                                            className="rounded-md border border-dashed border-gray-300 bg-gray-50 px-2 py-3 text-center dark:border-[#4A4D50] dark:bg-[#1E1E1E]">
                                            <p className="text-xs font-semibold text-gray-700 dark:text-[#D4D4D4]">{group.label}</p>
                                            <p className="mt-1 text-[11px] text-gray-500 dark:text-[#7A7E82]">
                                                Use Import Sensum DSL to add custom layers here.
                                            </p>
                                        </div>
                                    </div>
                                )
                            }

                            return (
                                <div key={group.id}>
                                    <button
                                        type="button"
                                        onClick={() => {
                                            if (isSingleLayer && group.id === "stations") {
                                                setSourcesExpanded((expanded) => !expanded)
                                            } else {
                                                toggleGroup(group.id)
                                            }
                                        }}
                                        className="flex w-full cursor-pointer items-center gap-2 rounded-md border border-gray-300 bg-gray-50 px-2 py-1.5 text-sm font-medium text-gray-900 transition-colors hover:text-black dark:border-[#4A4D50] dark:bg-[#1E1E1E] dark:text-[#D4D4D4] dark:hover:text-white"
                                    >
                                        <input
                                            type="checkbox"
                                            checked={
                                                group.id === "stations"
                                                    ? allSourcesSelected
                                                    : isGroupFullyVisible(layers)
                                            }
                                            ref={(el) => {
                                                if (!el) return
                                                el.indeterminate = group.id === "stations"
                                                    ? (someSourcesSelected && !allSourcesSelected)
                                                    : isGroupPartiallyVisible(layers)
                                            }}
                                            onChange={(event) => {
                                                event.stopPropagation()
                                                if (group.id === "stations") {
                                                    if (allSourcesSelected) {
                                                        stationSources.forEach((s) => {
                                                            if (visibleStationSources.has(s)) onToggleStationSource(s)
                                                        })
                                                    } else {
                                                        stationSources.forEach((s) => {
                                                            if (!visibleStationSources.has(s)) onToggleStationSource(s)
                                                        })
                                                    }
                                                } else {
                                                    toggleGroupLayers(layers)
                                                }
                                            }}
                                            onClick={(event) => event.stopPropagation()}
                                            className="shrink-0 accent-yellow-500"
                                        />
                                        <span className="flex-1 text-left">{group.label}</span>
                                        {loadingLayers.has(layers[0]) && (
                                            <span
                                                className="text-xs font-normal text-gray-500 dark:text-[#7A7E82]">Loading…</span>
                                        )}
                                        {errors[layers[0]] && (
                                            <span className="text-xs font-normal text-red-600 dark:text-[#CF6679]"
                                                  title={errors[layers[0]]}>
                    Error
                </span>
                                        )}
                                        {(isSingleLayer && group.id === "stations") ? (
                                            sourcesExpanded ? <ChevronDown size={14}/> : <ChevronRight size={14}/>
                                        ) : (
                                            isExpanded ? <ChevronDown size={14}/> : <ChevronRight size={14}/>
                                        )}
                                    </button>

                                    {isSingleLayer && group.id === "stations" ? (
                                        sourcesExpanded && (
                                            <div
                                                className="mt-1.5 ml-4 space-y-1 border-l border-gray-300 pl-3 dark:border-[#4A4D50]">
                                                <p className="text-[10px] font-semibold uppercase tracking-wide text-gray-500 dark:text-[#7A7E82]">
                                                    Filter by source
                                                </p>
                                                {stationSources.map((source) => (
                                                    <label
                                                        key={source}
                                                        className="flex cursor-pointer items-center gap-2 rounded-md px-2 py-1 text-xs font-medium text-gray-700 transition-colors hover:text-black dark:text-[#BBBBBB] dark:hover:text-white"
                                                    >
                                                        <input
                                                            type="checkbox"
                                                            checked={visibleStationSources.has(source)}
                                                            onChange={() => onToggleStationSource(source)}
                                                            className="shrink-0 accent-yellow-500"
                                                        />
                                                        <span
                                                            className="shrink-0 flex h-[18px] w-[18px] items-center justify-center rounded-full border-2 border-gray-900 dark:border-[#111827]"
                                                            style={{
                                                                background: stationSourceColors[source] ?? "#eab308",
                                                                fontSize: "8px",
                                                                fontWeight: 800,
                                                                fontFamily: "monospace",
                                                                color: "#111827",
                                                                lineHeight: 1,
                                                            }}
                                                        >
                                {stationSourceBadges[source]}
                            </span>
                                                        <span className="flex-1">{stationSourceLabels[source]}</span>
                                                    </label>
                                                ))}
                                            </div>
                                        )
                                    ) : (
                                        isExpanded && (
                                            <div
                                                className="mt-1.5 ml-4 space-y-1 border-l border-gray-300 pl-3 dark:border-[#4A4D50]">
                                                {layers.map((layer) => (
                                                    <label key={layer}
                                                           className="flex cursor-pointer items-center gap-2 rounded-md px-2 py-1 text-xs font-medium text-gray-700 transition-colors hover:text-black dark:text-[#BBBBBB] dark:hover:text-white">
                                                        <input
                                                            type="checkbox"
                                                            checked={visibleLayers.has(layer)}
                                                            onChange={() => onToggleLayer(layer)}
                                                            className="shrink-0 accent-yellow-500"
                                                        />
                                                        <span className="flex-1">
                                {layerShortLabels[layer] ?? getLayerLabel(layer)}
                            </span>
                                                        {loadingLayers.has(layer) && (
                                                            <span
                                                                className="text-[10px] font-normal text-gray-500 dark:text-[#7A7E82]">Loading…</span>
                                                        )}
                                                        {errors[layer] && (
                                                            <span
                                                                className="text-[10px] font-normal text-red-600 dark:text-[#CF6679]"
                                                                title={errors[layer]}>
                                    Error
                                </span>
                                                        )}
                                                    </label>
                                                ))}
                                            </div>
                                        )
                                    )}
                                </div>
                            )
                        })}
                    </div>
                </div>
            )}
        </div>
    )
}
