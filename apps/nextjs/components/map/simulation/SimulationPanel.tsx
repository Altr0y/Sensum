"use client"
import {CheckCircle2, Minus, RotateCcw, Trash2, X} from "lucide-react"
import type {StationDto, ChannelDto} from "../shared/types"
import type {SimStep, SimConfig, SimResult} from "./types"
import {SIM_MIN_POINTS, SIM_MAX_POINTS} from "./useSimulation"
import {SimulationForm} from "./SimulationForm"
import {SimulationResult} from "./SimulationResult"

interface Props {
    token: string
    step: SimStep
    points: [number, number][]
    stationsInArea: StationDto[]
    channelsByStation: Record<number, ChannelDto[]>
    config: SimConfig
    loading: boolean
    error: string | null
    result: SimResult | null
    onRemoveLast: () => void
    onRemovePoint: (index: number) => void
    onReset: () => void
    onConfirm: () => void
    onRun: (token?: string) => void
    onConfigChange: (c: SimConfig) => void
    onClose: () => void
    onRunAnother: () => void
    onSelectStation: (stationId: number) => void
}

const STEPS: SimStep[] = ["draw", "configure", "result"]

export function SimulationPanel({
                                    token,
                                    step, points, stationsInArea, channelsByStation,
                                    config, loading, error, result,
                                    onRemoveLast, onRemovePoint, onReset, onConfirm, onRun, onConfigChange,
                                    onClose, onRunAnother, onSelectStation,
                                }: Props) {
    const stepLabel =
        step === "draw"
            ? "Click the map to place polygon vertices"
            : step === "configure"
                ? "Configure generation parameters"
                : "Measurements generated"

    return (
        <aside
            className="h-full w-[430px] shrink-0 flex flex-col border-l border-border bg-card text-foreground shadow-xl overflow-hidden">
            <div className="shrink-0 border-b border-border p-4">
                <div className="flex items-start justify-between gap-3">
                    <div>
                        <h2 className="text-base font-semibold text-[#E8A838]">
                            Simulate measurements
                        </h2>
                        <p className="text-xs text-muted-foreground mt-0.5">{stepLabel}</p>
                    </div>
                    <button
                        onClick={onClose}
                        title="Close simulation"
                        className="flex h-9 w-9 items-center justify-center rounded-lg border border-border bg-background text-muted-foreground hover:text-foreground hover:bg-muted transition-colors shrink-0"
                    >
                        <X size={16}/>
                    </button>
                </div>

                <div className="flex gap-1 mt-3">
                    {STEPS.map((s, i) => (
                        <div
                            key={s}
                            className={[
                                "h-1 flex-1 rounded-full transition-colors",
                                step === s
                                    ? "bg-[#E8A838]"
                                    : i < STEPS.indexOf(step)
                                        ? "bg-[#4CAF7D]"
                                        : "bg-border",
                            ].join(" ")}
                        />
                    ))}
                </div>
            </div>

            <div className="flex-1 overflow-y-auto">
                {step === "draw" && (
                    <DrawStep
                        points={points}
                        onRemoveLast={onRemoveLast}
                        onRemovePoint={onRemovePoint}
                        onReset={onReset}
                        onConfirm={onConfirm}
                    />
                )}
                {step === "configure" && (
                    <SimulationForm
                        stationsInArea={stationsInArea}
                        channelsByStation={channelsByStation}
                        config={config}
                        loading={loading}
                        error={error}
                        onConfigChange={onConfigChange}
                        onRun={onRun}
                        onSelectStation={onSelectStation}
                    />
                )}
                {step === "result" && result && (
                    <SimulationResult
                        result={result}
                        onRunAnother={onRunAnother}
                        onSelectStation={onSelectStation}
                    />
                )}
            </div>
        </aside>
    )
}

function DrawStep({
                      points,
                      onRemoveLast,
                      onRemovePoint,
                      onReset,
                      onConfirm,
                  }: {
    points: [number, number][]
    onRemoveLast: () => void
    onRemovePoint: (index: number) => void
    onReset: () => void
    onConfirm: () => void
}) {
    const canConfirm = points.length >= SIM_MIN_POINTS
    const maxReached = points.length >= SIM_MAX_POINTS

    return (
        <div className="p-4 space-y-4">
            <div className="rounded-lg border border-border bg-background p-3 space-y-2">
                <div className="flex items-center justify-between text-xs text-muted-foreground">
                    <span>Polygon vertices</span>
                    <span className={canConfirm ? "text-[#4CAF7D]" : "text-[#E8A838]"}>
                        {points.length} / {SIM_MAX_POINTS}
                    </span>
                </div>
                <div className="flex gap-1">
                    {Array.from({length: SIM_MAX_POINTS}).map((_, i) => (
                        <div
                            key={i}
                            className={[
                                "h-1.5 flex-1 rounded-full transition-colors",
                                i < points.length
                                    ? i < SIM_MIN_POINTS
                                        ? "bg-[#E8A838]"
                                        : "bg-[#4CAF7D]"
                                    : "bg-border",
                            ].join(" ")}
                        />
                    ))}
                </div>
            </div>

            <div className="text-sm text-muted-foreground space-y-1.5">
                <p>• Click on the map to add vertices</p>
                <p>
                    • Minimum {SIM_MIN_POINTS}, maximum {SIM_MAX_POINTS} vertices
                </p>
                <p>• Drag any vertex to reposition it</p>
                <p>• Right-click a vertex to remove it</p>
                {maxReached && (
                    <p className="text-[#CF6679]">• Maximum vertex count reached</p>
                )}
            </div>

            <div className="flex gap-2">
                <button
                    onClick={onRemoveLast}
                    disabled={points.length === 0}
                    className="flex items-center gap-1.5 px-3 py-2 text-xs rounded-lg border border-border bg-background text-foreground hover:bg-muted disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                >
                    <Minus size={13}/> Undo last
                </button>
                <button
                    onClick={onReset}
                    disabled={points.length === 0}
                    className="flex items-center gap-1.5 px-3 py-2 text-xs rounded-lg border border-border bg-background text-foreground hover:bg-muted disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                >
                    <RotateCcw size={13}/> Reset
                </button>
                <button
                    onClick={onConfirm}
                    disabled={!canConfirm}
                    className="ml-auto flex items-center gap-1.5 px-4 py-2 text-xs rounded-lg bg-[#E8A838] text-[#1E1E1E] font-medium hover:bg-[#d4962e] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                >
                    <CheckCircle2 size={13}/> Confirm area
                </button>
            </div>

            {points.length > 0 && (
                <div className="space-y-1">
                    <p className="text-xs text-muted-foreground font-medium">Vertices:</p>
                    <div className="max-h-44 overflow-y-auto space-y-1">
                        {points.map((pt, i) => (
                            <div
                                key={i}
                                className="group flex items-center gap-2 text-xs font-mono bg-background rounded px-2 py-1.5"
                            >
                                <span
                                    className={[
                                        "w-5 h-5 rounded-full flex items-center justify-center text-[10px] font-bold shrink-0",
                                        i === 0
                                            ? "bg-[#E8A838] text-[#1E1E1E]"
                                            : "border border-[#E8A838] text-[#E8A838]",
                                    ].join(" ")}
                                >
                                    {i + 1}
                                </span>
                                <span className="flex-1 text-foreground">
                                    {pt[0].toFixed(5)}, {pt[1].toFixed(5)}
                                </span>
                                <button
                                    onClick={() => onRemovePoint(i)}
                                    className="opacity-0 group-hover:opacity-100 p-0.5 rounded text-muted-foreground hover:text-[#CF6679] transition-all"
                                    title="Remove vertex"
                                >
                                    <Trash2 size={12}/>
                                </button>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    )
}
