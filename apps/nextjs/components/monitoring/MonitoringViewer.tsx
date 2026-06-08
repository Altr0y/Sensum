"use client"

import { useState, useCallback } from "react"
import { AlertTriangle, Loader2, RefreshCw } from "lucide-react"

const DASHBOARDS = [
    {
        id: "measurements",
        uid: "sensum-measurements",
        slug: "measurements",
        label: "Measurements",
    },
    {
        id: "overview",
        uid: "sensum-overview",
        slug: "overview",
        label: "Overview",
    },
]

type IframeState = "loading" | "ready" | "error"

function isTokenExpired(token: string): boolean {
    try {
        const payload = JSON.parse(atob(token.split(".")[1]))
        return payload.exp * 1000 < Date.now()
    } catch {
        return true
    }
}

interface MonitoringViewerProps {
    token: string
    dashboardId?: string
    from?: string
    to?: string
}

export function MonitoringViewer({ token, dashboardId, from, to }: MonitoringViewerProps) {
    const [activeId, setActiveId] = useState(
        () => DASHBOARDS.find((d) => d.id === dashboardId)?.id ?? DASHBOARDS[0].id
    )
    const [iframeState, setIframeState] = useState<IframeState>("loading")
    const [refreshKey, setRefreshKey] = useState(0)

    const tokenExpired = token ? isTokenExpired(token) : true
    const dashboard = DASHBOARDS.find((d) => d.id === activeId) ?? DASHBOARDS[0]

    const timeRange =
        from && to
            ? `from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`
            : `from=now-7d&to=now`

    const grafanaUrl =
        `/grafana/d/${dashboard.uid}/${dashboard.slug}` +
        `?orgId=1&kiosk&theme=dark&${timeRange}` +
        (token && !tokenExpired ? `&auth_token=${encodeURIComponent(token)}` : "")

    const handleTabChange = useCallback((id: string) => {
        setActiveId(id)
        setIframeState("loading")
    }, [])

    const handleRefresh = useCallback(() => {
        setIframeState("loading")
        setRefreshKey((k) => k + 1)
    }, [])

    const handleLoad = useCallback(() => {
        setIframeState("ready")
    }, [])

    const handleError = useCallback(() => {
        setIframeState("error")
    }, [])

    return (
        <div className="-m-6 flex flex-col h-[calc(100vh-3.5rem)]">
            {/* Tab bar */}
            <div className="flex items-center border-b border-border bg-card px-4 shrink-0">
                <div className="flex flex-1">
                    {DASHBOARDS.map((d) => {
                        const isActive = d.id === activeId
                        return (
                            <button
                                key={d.id}
                                onClick={() => handleTabChange(d.id)}
                                className={`
                                    relative px-4 py-3 text-sm font-medium transition-colors duration-150
                                    ${isActive
                                        ? "text-[#E8A838]"
                                        : "text-[#7A7E82] hover:text-[#D4D4D4]"
                                    }
                                `}
                            >
                                {d.label}
                                {isActive && (
                                    <div className="absolute bottom-0 left-2 right-2 h-[2px] bg-[#E8A838] rounded-t" />
                                )}
                            </button>
                        )
                    })}
                </div>

                <button
                    onClick={handleRefresh}
                    className="p-2 text-[#7A7E82] hover:text-[#D4D4D4] transition-colors"
                    title="Refresh"
                >
                    <RefreshCw size={15} />
                </button>
            </div>

            {/* Token expired banner */}
            {tokenExpired && (
                <div className="flex items-center gap-2 px-4 py-2 bg-yellow-900/30 border-b border-yellow-700/50 text-yellow-400 text-xs shrink-0">
                    <AlertTriangle size={14} />
                    Session expired. Please{" "}
                    <a href="/login" className="underline hover:text-yellow-300">
                        log in again
                    </a>
                    .
                </div>
            )}

            {/* Iframe area */}
            <div className="relative flex-1">
                {iframeState === "loading" && (
                    <div className="absolute inset-0 flex items-center justify-center bg-background z-10">
                        <div className="flex flex-col items-center gap-3 text-muted-foreground">
                            <Loader2 size={28} className="animate-spin text-[#E8A838]" />
                            <span className="text-sm">Loading dashboard…</span>
                        </div>
                    </div>
                )}

                {iframeState === "error" && (
                    <div className="absolute inset-0 flex items-center justify-center bg-background z-10">
                        <div className="flex flex-col items-center gap-4 text-center max-w-sm">
                            <AlertTriangle size={36} className="text-[#CF6679]" />
                            <div>
                                <p className="text-sm font-medium text-foreground mb-1">
                                    Grafana is unreachable
                                </p>
                                <p className="text-xs text-muted-foreground">
                                    Check that all services are running.
                                </p>
                            </div>
                            <button
                                onClick={handleRefresh}
                                className="flex items-center gap-2 px-4 py-2 text-sm bg-[#3C3F41] hover:bg-[#4A4D50] text-foreground rounded-md transition-colors"
                            >
                                <RefreshCw size={14} />
                                Try again
                            </button>
                        </div>
                    </div>
                )}

                <iframe
                    key={`${dashboard.uid}-${refreshKey}`}
                    src={grafanaUrl}
                    className="h-full w-full border-0"
                    title={dashboard.label}
                    onLoad={handleLoad}
                    onError={handleError}
                />
            </div>
        </div>
    )
}
