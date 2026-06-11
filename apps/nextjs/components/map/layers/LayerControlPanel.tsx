"use client"

import { ChevronDown, ChevronRight, Minus, Plus } from "lucide-react"
import { useRef, useState } from "react"
import type { PointerEvent } from "react"
import type { PanelPosition } from "../shared/types"
import { getLayerLabel, stationSourceBadges, stationSourceLabels, stationSources } from "./layerConfig"

type Props = {
    layerControlItems: string[]
    visibleLayers: Set<string>
    loadingLayers: Set<string>
    errors: Record<string, string>
    onToggleLayer: (layer: string) => void
    visibleStationSources: Set<string>
    onToggleStationSource: (source: string) => void
    mapContainerRef: React.RefObject<HTMLDivElement | null>
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
}: Props) {
    const [position, setPosition] = useState<PanelPosition>({ x: 16, y: 16 })
    const [collapsed, setCollapsed] = useState(false)
    const [sourcesExpanded, setSourcesExpanded] = useState(false)
    const [isDragging, setIsDragging] = useState(false)
    const dragOffset = useRef<PanelPosition>({ x: 0, y: 0 })

    function startDrag(event: PointerEvent<HTMLDivElement>) {
        if (event.button !== 0) return
        const panel = event.currentTarget.parentElement
        if (!panel) return
        const rect = panel.getBoundingClientRect()
        dragOffset.current = { x: event.clientX - rect.left, y: event.clientY - rect.top }
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

    return (
        <div
            className="absolute z-[1000] max-h-[calc(100%-2rem)] w-80 overflow-hidden rounded-xl border border-gray-300 bg-white shadow-lg dark:border-[#4A4D50] dark:bg-[#2B2B2B]"
            style={{ left: position.x, top: position.y }}
        >
            <div
                className="cursor-move select-none border-b border-gray-300 px-4 py-3 dark:border-[#4A4D50]"
                style={{ touchAction: "none" }}
                onPointerDown={startDrag}
                onPointerMove={onDrag}
                onPointerUp={stopDrag}
                onPointerCancel={stopDrag}
            >
                <div className="flex items-center justify-between gap-3">
                    <div>
                        <h2 className="text-sm font-semibold text-gray-900 dark:text-[#D4D4D4]">Map layers</h2>
                        <p className="text-xs text-gray-500 dark:text-[#7A7E82]">Layers are loaded only when enabled.</p>
                    </div>
                    <div className="flex items-center gap-2">
                        <span className="rounded-md border border-gray-300 bg-gray-50 px-2 py-1 text-[10px] font-medium text-gray-500 dark:border-[#4A4D50] dark:bg-[#1E1E1E] dark:text-[#7A7E82]">
                            Move
                        </span>
                        <button
                            type="button"
                            onPointerDown={(event) => event.stopPropagation()}
                            onClick={() => setCollapsed((c) => !c)}
                            className="flex h-5 w-5 items-center justify-center rounded text-gray-500 hover:text-gray-900 transition-colors dark:text-[#7A7E82] dark:hover:text-[#D4D4D4]"
                        >
                            {collapsed ? <Plus size={12} /> : <Minus size={12} />}
                        </button>
                    </div>
                </div>
            </div>

            {!collapsed && (
                <div className="max-h-[calc(100vh-12rem)] overflow-y-auto p-4">
                    <div className="space-y-1.5">
                        {layerControlItems.map((layer) => (
                            <div key={layer}>
                                <label
                                    className="flex cursor-pointer items-center gap-2 rounded-md border border-gray-300 bg-gray-50 px-2 py-1.5 text-sm font-medium text-gray-900 transition-colors hover:text-black dark:border-[#4A4D50] dark:bg-[#1E1E1E] dark:text-[#D4D4D4] dark:hover:text-white"
                                >
                                    <input
                                        type="checkbox"
                                        checked={visibleLayers.has(layer)}
                                        onChange={() => onToggleLayer(layer)}
                                        className="shrink-0 accent-yellow-500"
                                    />
                                    <span className="flex-1">{getLayerLabel(layer)}</span>
                                    {loadingLayers.has(layer) && (
                                        <span className="text-xs font-normal text-gray-500 dark:text-[#7A7E82]">Loading…</span>
                                    )}
                                    {errors[layer] && (
                                        <span className="text-xs font-normal text-red-600 dark:text-[#CF6679]" title={errors[layer]}>
                                            Error
                                        </span>
                                    )}
                                    {layer === "stations" && (
                                        <button
                                            type="button"
                                            onClick={(event) => {
                                                event.preventDefault()
                                                setSourcesExpanded((expanded) => !expanded)
                                            }}
                                            className="flex h-5 w-5 shrink-0 items-center justify-center rounded text-gray-500 hover:text-gray-900 transition-colors dark:text-[#7A7E82] dark:hover:text-[#D4D4D4]"
                                            title="Filter by source"
                                        >
                                            {sourcesExpanded ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
                                        </button>
                                    )}
                                </label>

                                {layer === "stations" && sourcesExpanded && (
                                    <div className="mt-1.5 ml-4 space-y-1 border-l border-gray-300 pl-3 dark:border-[#4A4D50]">
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
                                                        background: "#eab308",
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
                                )}
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    )
}
