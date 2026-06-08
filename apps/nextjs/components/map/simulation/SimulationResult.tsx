"use client"
import Link from "next/link"
import { CheckCircle, ExternalLink, RefreshCw } from "lucide-react"
import type { SimResult } from "./types"

interface Props {
    token: string
    result: SimResult
    onRunAnother: () => void
}

const DASHBOARD_UID = "sensum-measurements"
const DASHBOARD_SLUG = "measurements"

export function SimulationResult({ token, result, onRunAnother }: Props) {
    const from = result.from.replace("T", " ") + ":00"
    const to = result.to.replace("T", " ") + ":00"

    const authParam = token ? `&auth_token=${encodeURIComponent(token)}` : ""
    const timeParams = `from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`

    const previewUrl =
        `/grafana/d/${DASHBOARD_UID}/${DASHBOARD_SLUG}` +
        `?orgId=1&theme=dark&kiosk&viewPanel=1&${timeParams}${authParam}`

    const monitoringHref =
        `/monitoring?dashboard=measurements&${timeParams}`

    return (
        <div className="p-4 space-y-4">
            <div className="rounded-lg border border-[#4CAF7D]/30 bg-[#4CAF7D]/10 p-4 flex items-start gap-3">
                <CheckCircle size={18} className="text-[#4CAF7D] shrink-0 mt-0.5" />
                <div className="space-y-1">
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

            <div className="space-y-2">
                <p className="text-xs font-medium text-muted-foreground">Preview</p>
                <div className="overflow-hidden rounded-lg border border-border h-48">
                    <iframe
                        src={previewUrl}
                        className="h-full w-full border-0"
                        title="Measurements preview"
                    />
                </div>
                <Link
                    href={monitoringHref}
                    className="flex items-center justify-center gap-2 w-full rounded-lg border border-[#E8A838] text-[#E8A838] py-2.5 text-sm font-medium hover:bg-[#E8A838]/10 transition-colors"
                >
                    <ExternalLink size={14} />
                    View full graph in Monitoring
                </Link>
            </div>

            <button
                onClick={onRunAnother}
                className="flex items-center justify-center gap-2 w-full rounded-lg border border-border bg-background text-foreground py-2.5 text-sm hover:bg-muted transition-colors"
            >
                <RefreshCw size={14} />
                New simulation
            </button>
        </div>
    )
}
