"use client"

import {CheckCircle, RefreshCw, MapPin} from "lucide-react"
import type {SimResult} from "./types"

interface Props {
    result: SimResult
    onRunAnother: () => void
    onSelectStation: (stationId: number) => void
}

export function SimulationResult({result, onRunAnother, onSelectStation}: Props) {
    return (
        <div className="flex flex-col h-full p-4 gap-3">
            <div className="rounded-lg border border-[#4CAF7D]/30 bg-[#4CAF7D]/10 p-3 flex items-start gap-3 shrink-0">
                <CheckCircle size={18} className="text-[#4CAF7D] shrink-0 mt-0.5"/>
                <div className="space-y-0.5">
                    <p className="text-sm font-medium text-[#4CAF7D]">
                        Successfully generated
                    </p>
                    <p className="text-xs text-muted-foreground">
                        {result.mode === "new"
                            ? `${result.stationCount} new stations with measurements`
                            : `Measurements for ${result.stationCount} existing stations`}
                    </p>
                    <p className="text-xs text-muted-foreground font-mono">
                        {result.from} → {result.to}
                    </p>
                </div>
            </div>

            <button
                onClick={onRunAnother}
                className="flex items-center justify-center gap-2 shrink-0 rounded-lg border border-border bg-background text-foreground py-2 text-sm hover:bg-muted transition-colors"
            >
                <RefreshCw size={14}/>
                New simulation
            </button>

            <div className="flex-1 overflow-y-auto space-y-2 min-h-0">
                <p className="text-xs font-medium text-muted-foreground">
                    Stations ({result.stations.length})
                </p>
                {result.stations.map((station) => (
                    <button
                        key={station.stationId}
                        onClick={() => onSelectStation(station.stationId)}
                        className="w-full flex items-center justify-between gap-2 rounded-lg border border-border bg-background px-3 py-2.5 text-left text-sm text-foreground hover:border-[#E8A838] hover:bg-[#E8A838]/5 transition-colors"
                    >
                        <span className="truncate">{station.name || `Station ${station.stationId}`}</span>
                        <MapPin size={14} className="text-muted-foreground shrink-0"/>
                    </button>
                ))}
            </div>
        </div>
    )
}