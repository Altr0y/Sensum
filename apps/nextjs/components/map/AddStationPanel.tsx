"use client"

import { useState } from "react"
import type { PendingStationPoint } from "./types"

type Props = {
    pending: PendingStationPoint
    loading: boolean
    error: string | null
    onSave: (name: string, description: string, serialNumber: string) => void
    onCancel: () => void
}

export function AddStationPanel({ pending, loading, error, onSave, onCancel }: Props) {
    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [serialNumber, setSerialNumber] = useState("")

    function handleSubmit(e: React.FormEvent) {
        e.preventDefault()
        if (!name.trim()) return
        onSave(name.trim(), description.trim(), serialNumber.trim())
    }

    return (
        <div className="flex flex-col gap-4 p-4">
            <div>
                <h3 className="text-sm font-semibold text-foreground">New station</h3>
                <p className="text-xs text-muted-foreground">
                    Source: <span className="font-medium text-forFeground">MANUAL</span>
                </p>
            </div>

            <div className="grid grid-cols-2 gap-2 text-xs">
                <div className="rounded-lg border border-border bg-background p-2">
                    <div className="text-muted-foreground">Latitude</div>
                    <div className="font-medium font-mono">{pending.lat.toFixed(6)}</div>
                </div>
                <div className="rounded-lg border border-border bg-background p-2">
                    <div className="text-muted-foreground">Longitude</div>
                    <div className="font-medium font-mono">{pending.lng.toFixed(6)}</div>
                </div>
            </div>

            <form onSubmit={handleSubmit} className="flex flex-col gap-3">
                <div className="flex flex-col gap-1">
                    <label className="text-xs font-medium text-muted-foreground">
                        Name <span className="text-red-500">*</span>
                    </label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="Station name"
                        className="rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring"
                        disabled={loading}
                        autoFocus
                    />
                </div>

                <div className="flex flex-col gap-1">
                    <label className="text-xs font-medium text-muted-foreground">
                        Description
                    </label>
                    <input
                        type="text"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        placeholder="Optional description"
                        className="rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring"
                        disabled={loading}
                    />
                </div>

                <div className="flex flex-col gap-1">
                    <label className="text-xs font-medium text-muted-foreground">
                        Serial number
                    </label>
                    <input
                        type="text"
                        value={serialNumber}
                        onChange={(e) => setSerialNumber(e.target.value)}
                        placeholder="Optional serial number"
                        className="rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring"
                        disabled={loading}
                    />
                </div>

                {error && (
                    <p className="rounded-lg border border-red-500/30 bg-red-500/10 px-3 py-2 text-xs text-red-600 dark:text-red-400">
                        {error}
                    </p>
                )}

                <div className="flex gap-2">
                    <button
                        type="submit"
                        disabled={loading || !name.trim()}
                        className="flex-1 rounded-lg bg-foreground px-3 py-2 text-sm font-medium text-background hover:opacity-90 disabled:opacity-40"
                    >
                        {loading ? "Saving…" : "Save station"}
                    </button>
                    <button
                        type="button"
                        onClick={onCancel}
                        disabled={loading}
                        className="rounded-lg border border-border px-3 py-2 text-sm font-medium text-foreground hover:bg-muted disabled:opacity-40"
                    >
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    )
}
