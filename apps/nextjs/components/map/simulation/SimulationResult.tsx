"use client"
import Link from "next/link"
import {CheckCircle, ExternalLink, RefreshCw} from "lucide-react"
import type {SimResult} from "./types"

interface Props {
    token: string
    result: SimResult
    onRunAnother: () => void
}

const DASHBOARD_UID = "sensum-measurements"
const DASHBOARD_SLUG = "measurements"

export function SimulationResult({token, result, onRunAnother}: Props) {
    const from = result.from + ":00"
    const to = result.to + ":00"

    const authParam = token ? `&auth_token=${encodeURIComponent(token)}` : ""
    const timeParams = `from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`

    const previewUrl =
        `/grafana/d/${DASHBOARD_UID}/${DASHBOARD_SLUG}` +
        `?orgId=1&theme=dark&kiosk&viewPanel=1&${timeParams}${authParam}`

    const monitoringHref =
        `/monitoring?dashboard=measurements&${timeParams}`

    return (
        <div className="flex flex-col h-full p-4 gap-3">
            {/* Success banner */}
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

            {/* Buttons */}
            <div className="flex gap-2 shrink-0">
                <Link
                    href={monitoringHref}
                    className="flex items-center justify-center gap-2 flex-1 rounded-lg border border-[#E8A838] text-[#E8A838] py-2 text-sm font-medium hover:bg-[#E8A838]/10 transition-colors"
                >
                    <ExternalLink size={14}/>
                    View in Monitoring
                </Link>
                <button
                    onClick={onRunAnother}
                    className="flex items-center justify-center gap-2 flex-1 rounded-lg border border-border bg-background text-foreground py-2 text-sm hover:bg-muted transition-colors"
                >
                    <RefreshCw size={14}/>
                    New simulation
                </button>
            </div>

            {/* Grafana preview - razpotegne do konca */}
            <div className="flex-1 overflow-hidden rounded-lg border border-border min-h-0">
                <iframe
                    src={previewUrl}
                    className="h-full w-full border-0"
                    title="Measurements preview"
                />
            </div>
        </div>
    )
}
