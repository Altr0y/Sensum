"use client"

import { useEffect } from "react"
import { AlertTriangle, RefreshCw } from "lucide-react"

export default function ErrorPage({
    error,
    reset,
}: {
    error: Error & { digest?: string }
    reset: () => void
}) {
    useEffect(() => {
        console.error(error)
    }, [error])

    return (
        <main className="grid min-h-[calc(100vh-3.5rem)] place-items-center">
            <div className="w-full max-w-md rounded-xl border border-border bg-card p-6 text-center shadow-sm">
                <div className="mb-4 flex justify-center">
                    <AlertTriangle size={36} className="text-[#CF6679]" />
                </div>
                <p className="text-sm text-muted-foreground">EL3 / SENSUM</p>
                <h1 className="mt-1 text-2xl font-bold text-foreground">Something went wrong</h1>
                <p className="mt-2 text-sm text-muted-foreground">
                    An unexpected error occurred while loading this page.
                </p>
                <button
                    type="button"
                    onClick={reset}
                    className="mt-6 inline-flex items-center justify-center gap-2 rounded-md bg-[#E8A838] px-4 py-2 text-sm font-medium text-[#111827] transition-colors hover:bg-[#d4962e]"
                >
                    <RefreshCw size={14} />
                    Try again
                </button>
            </div>
        </main>
    )
}
