"use client"
import { Loader2 } from "lucide-react"
import type { StationDto, ChannelDto } from "../types"
import type { SimConfig, ChannelKind } from "./types"
import { ALL_CHANNEL_KINDS, CHANNEL_KIND_LABELS } from "./types"

interface Props {
    stationsInArea: StationDto[]
    channelsByStation: Record<number, ChannelDto[]>
    config: SimConfig
    loading: boolean
    error: string | null
    onConfigChange: (c: SimConfig) => void
    onRun: () => void
}

const INTERVAL_OPTIONS = [
    { value: 15, label: "15 min" },
    { value: 30, label: "30 min" },
    { value: 60, label: "1 hour" },
    { value: 360, label: "6 hours" },
    { value: 1440, label: "1 day" },
]

export function SimulationForm({
    stationsInArea,
    channelsByStation,
    config,
    loading,
    error,
    onConfigChange,
    onRun,
}: Props) {
    const hasExisting = stationsInArea.length > 0

    function update<K extends keyof SimConfig>(key: K, value: SimConfig[K]) {
        onConfigChange({ ...config, [key]: value })
    }

    function toggleKind(kind: ChannelKind) {
        const next = config.channelKinds.includes(kind)
            ? config.channelKinds.filter((k) => k !== kind)
            : [...config.channelKinds, kind]
        update("channelKinds", next as ChannelKind[])
    }

    const canRun =
        !loading &&
        config.from &&
        config.to &&
        config.from < config.to &&
        (hasExisting ||
            (config.stationCount >= 1 && config.channelKinds.length >= 1))

    return (
        <div className="p-4 space-y-5">
            {hasExisting ? (
                <div className="rounded-lg border border-[#4CAF7D]/30 bg-[#4CAF7D]/10 p-3 space-y-2">
                    <p className="text-xs font-medium text-[#4CAF7D]">
                        Stations in area ({stationsInArea.length})
                    </p>
                    <div className="space-y-1 max-h-28 overflow-y-auto">
                        {stationsInArea.map((s) => (
                            <div
                                key={s.stationId}
                                className="flex items-center justify-between text-xs"
                            >
                                <span className="text-foreground">
                                    {s.name ?? `Station ${s.stationId}`}
                                </span>
                                <span className="text-muted-foreground">
                                    {channelsByStation[s.stationId]?.length ?? "?"} ch.
                                </span>
                            </div>
                        ))}
                    </div>
                    <p className="text-xs text-muted-foreground">
                        Measurements will be generated for all existing channels.
                    </p>
                </div>
            ) : (
                <div className="rounded-lg border border-[#E8A838]/30 bg-[#E8A838]/10 p-3 space-y-3">
                    <p className="text-xs font-medium text-[#E8A838]">
                        No stations in area — new ones will be created
                    </p>
                    <div className="grid grid-cols-2 gap-3">
                        <label className="block">
                            <span className="text-xs text-muted-foreground">Station count</span>
                            <input
                                type="number"
                                min={1}
                                max={10}
                                value={config.stationCount}
                                onChange={(e) =>
                                    update(
                                        "stationCount",
                                        Math.max(1, Math.min(10, parseInt(e.target.value) || 1))
                                    )
                                }
                                className="mt-1 w-full rounded-md border border-border bg-background px-2 py-1.5 text-sm text-foreground focus:outline-none focus:ring-1 focus:ring-[#E8A838]"
                            />
                        </label>
                        <label className="block">
                            <span className="text-xs text-muted-foreground">Name prefix</span>
                            <input
                                type="text"
                                value={config.prefix}
                                onChange={(e) => update("prefix", e.target.value)}
                                className="mt-1 w-full rounded-md border border-border bg-background px-2 py-1.5 text-sm text-foreground focus:outline-none focus:ring-1 focus:ring-[#E8A838]"
                            />
                        </label>
                    </div>
                    <div>
                        <span className="text-xs text-muted-foreground">Channel types</span>
                        <div className="mt-1.5 grid grid-cols-2 gap-1.5">
                            {ALL_CHANNEL_KINDS.map((kind) => (
                                <button
                                    key={kind}
                                    type="button"
                                    onClick={() => toggleKind(kind)}
                                    className={[
                                        "px-2 py-1.5 rounded-md text-xs text-left transition-colors border",
                                        config.channelKinds.includes(kind)
                                            ? "border-[#E8A838] bg-[#E8A838]/10 text-[#E8A838]"
                                            : "border-border bg-background text-muted-foreground hover:text-foreground hover:bg-muted",
                                    ].join(" ")}
                                >
                                    {CHANNEL_KIND_LABELS[kind]}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>
            )}

            <div className="space-y-3">
                <p className="text-xs font-medium text-foreground">Time range</p>
                <div className="grid grid-cols-2 gap-3">
                    <label className="block">
                        <span className="text-xs text-muted-foreground">From</span>
                        <input
                            type="datetime-local"
                            value={config.from}
                            onChange={(e) => update("from", e.target.value)}
                            className="mt-1 w-full rounded-md border border-border bg-background px-2 py-1.5 text-xs text-foreground focus:outline-none focus:ring-1 focus:ring-[#E8A838]"
                        />
                    </label>
                    <label className="block">
                        <span className="text-xs text-muted-foreground">To</span>
                        <input
                            type="datetime-local"
                            value={config.to}
                            onChange={(e) => update("to", e.target.value)}
                            className="mt-1 w-full rounded-md border border-border bg-background px-2 py-1.5 text-xs text-foreground focus:outline-none focus:ring-1 focus:ring-[#E8A838]"
                        />
                    </label>
                </div>
            </div>

            <label className="block">
                <span className="text-xs text-muted-foreground">Measurement interval</span>
                <select
                    value={config.intervalMinutes}
                    onChange={(e) => update("intervalMinutes", parseInt(e.target.value))}
                    className="mt-1 w-full rounded-md border border-border bg-background px-3 py-1.5 text-sm text-foreground focus:outline-none focus:ring-1 focus:ring-[#E8A838]"
                >
                    {INTERVAL_OPTIONS.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                            {opt.label}
                        </option>
                    ))}
                </select>
            </label>

            {error && (
                <div className="rounded-lg border border-[#CF6679]/30 bg-[#CF6679]/10 p-3 text-xs text-[#CF6679]">
                    {error}
                </div>
            )}

            <button
                onClick={onRun}
                disabled={!canRun}
                className="w-full flex items-center justify-center gap-2 rounded-lg bg-[#E8A838] text-[#1E1E1E] py-2.5 text-sm font-medium hover:bg-[#d4962e] disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
            >
                {loading ? (
                    <>
                        <Loader2 size={14} className="animate-spin" />
                        Generating…
                    </>
                ) : (
                    "Generate measurements"
                )}
            </button>
        </div>
    )
}
