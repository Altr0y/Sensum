"use client"
import { usePathname, useRouter } from "next/navigation"
import { Monitor, ChevronDown, RefreshCw } from "lucide-react"
import { useState, useRef, useEffect } from "react"

export function MonitoringNavItem() {
    const pathname = usePathname()
    const router = useRouter()
    const isActive = pathname === "/monitoring"
    const [open, setOpen] = useState(false)
    const ref = useRef<HTMLDivElement>(null)

    useEffect(() => {
        function handleClick(e: MouseEvent) {
            if (ref.current && !ref.current.contains(e.target as Node)) {
                setOpen(false)
            }
        }
        document.addEventListener("mousedown", handleClick)
        return () => document.removeEventListener("mousedown", handleClick)
    }, [])

    return (
        <div
            ref={ref}
            className="relative"
            onMouseEnter={() => setOpen(true)}
            onMouseLeave={() => setOpen(false)}
        >
            <button
                className={`
                    flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium
                    transition-colors duration-150 relative
                    ${isActive
                        ? "bg-muted text-[#E8A838]"
                        : "text-muted-foreground hover:bg-muted hover:text-foreground"
                    }
                `}
            >
                {isActive && (
                    <div className="absolute bottom-0 left-2 right-2 h-[2px] bg-[#E8A838] rounded-t" />
                )}
                <Monitor size={16} />
                Monitoring
                <ChevronDown size={14} className={`transition-transform duration-150 ${open ? "rotate-180" : ""}`} />
            </button>

            {open && (
                <div className="absolute left-0 top-full z-[200] w-44 rounded-xl border border-border bg-card p-1 shadow-xl">
                    <button
                        onClick={() => { router.push("/monitoring?dashboard=measurements"); setOpen(false) }}
                        className="block w-full text-left rounded-md px-3 py-2 text-sm text-foreground hover:bg-muted hover:text-[#E8A838] transition-colors"
                    >
                        Measurements
                    </button>
                    <button
                        onClick={() => { router.push("/monitoring?dashboard=overview"); setOpen(false) }}
                        className="block w-full text-left rounded-md px-3 py-2 text-sm text-foreground hover:bg-muted hover:text-[#E8A838] transition-colors"
                    >
                        Overview
                    </button>
                    <div className="my-1 border-t border-border" />
                    <button
                        onClick={() => { window.dispatchEvent(new CustomEvent("grafana-refresh")); setOpen(false) }}
                        className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm text-foreground hover:bg-muted hover:text-[#E8A838] transition-colors"
                    >
                        <RefreshCw size={14} />
                        Refresh
                    </button>
                </div>
            )}
        </div>
    )
}