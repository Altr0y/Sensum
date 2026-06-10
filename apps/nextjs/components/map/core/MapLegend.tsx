"use client"

import { Minus, Plus } from "lucide-react"
import { useEffect, useRef, useState } from "react"
import type { PointerEvent } from "react"
import type { PanelPosition } from "../shared/types"

const LAYER_COLORS = [
    { color: "#0284c7", fill: "#0284c7", line: true, label: "Rivers" },
    { color: "#0891b2", fill: "#67e8f9", label: "Lakes" },
    { color: "#2563eb", fill: "#60a5fa", label: "Regions" },
    { color: "#7c3aed", fill: "#c4b5fd", label: "Municipalities" },
    { color: "#b91c1c", fill: "#ef4444", label: "Flood zones — frequent" },
    { color: "#c2410c", fill: "#f97316", label: "Flood zones — rare" },
    { color: "#ca8a04", fill: "#facc15", label: "Flood zones — very rare" },
]

export function MapLegend({
    mapContainerRef,
}: {
    mapContainerRef: React.RefObject<HTMLDivElement | null>
}) {
    const [position, setPosition] = useState<PanelPosition>({ x: 16, y: 16 })
    const [hasPositioned, setHasPositioned] = useState(false)
    const [collapsed, setCollapsed] = useState(false)
    const [isDragging, setIsDragging] = useState(false)
    const dragOffset = useRef<PanelPosition>({ x: 0, y: 0 })
    const panelRef = useRef<HTMLDivElement | null>(null)

    useEffect(() => {
        if (hasPositioned) return
        const container = mapContainerRef.current
        const panel = panelRef.current
        if (!container || !panel) return
        const cr = container.getBoundingClientRect()
        const pr = panel.getBoundingClientRect()
        setPosition({ x: 16, y: Math.max(16, cr.height - pr.height - 16) })
        setHasPositioned(true)
    }, [hasPositioned, mapContainerRef])

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
        setPosition({
            x: Math.min(Math.max(0, event.clientX - cr.left - dragOffset.current.x), cr.width - 220),
            y: Math.min(Math.max(0, event.clientY - cr.top - dragOffset.current.y), cr.height - 48),
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
            ref={panelRef}
            className="absolute z-[1000] w-52 rounded-xl border border-gray-300 bg-white shadow-lg overflow-hidden dark:border-[#4A4D50] dark:bg-[#2B2B2B]"
            style={{ left: position.x, top: position.y }}
        >
            {/* Header — draggable */}
            <div
                className="cursor-move select-none flex items-center justify-between px-3 py-2 border-b border-gray-300 dark:border-[#4A4D50]"
                style={{ touchAction: "none" }}
                onPointerDown={startDrag}
                onPointerMove={onDrag}
                onPointerUp={stopDrag}
                onPointerCancel={stopDrag}
            >
                <span className="text-xs font-semibold text-gray-900 dark:text-[#D4D4D4]">Legend</span>
                <button
                    type="button"
                    onPointerDown={(event) => event.stopPropagation()}
                    onClick={() => setCollapsed((c) => !c)}
                    className="flex h-5 w-5 items-center justify-center rounded text-gray-500 hover:text-gray-900 transition-colors dark:text-[#7A7E82] dark:hover:text-[#D4D4D4]"
                >
                    {collapsed ? <Plus size={12} /> : <Minus size={12} />}
                </button>
            </div>

            {!collapsed && (
                <div className="p-3 space-y-3">
                    {/* Layer colors */}
                    <div>
                        <p className="text-[10px] font-semibold uppercase tracking-wide text-gray-500 mb-2 dark:text-[#7A7E82]">
                            Layers
                        </p>
                        <div className="space-y-1.5">
                            {LAYER_COLORS.map(({ color, fill, line, label }) => (
                                <div key={label} className="flex items-center gap-2">
                                    {line ? (
                                        <div
                                            className="shrink-0 w-[22px] h-[4px] rounded-full"
                                            style={{ backgroundColor: fill }}
                                        />
                                    ) : (
                                        <div
                                            className="shrink-0 w-[22px] h-[13px] rounded-sm"
                                            style={{
                                                backgroundColor: fill,
                                                border: `1.5px solid ${color}`,
                                                opacity: 0.85,
                                            }}
                                        />
                                    )}
                                    <span className="text-xs leading-tight text-gray-700 dark:text-[#BBBBBB]">{label}</span>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}
