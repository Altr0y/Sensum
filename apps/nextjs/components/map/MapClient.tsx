"use client"

import dynamic from "next/dynamic"

const SensumMap = dynamic(
    () => import("@/components/map/SensumMap").then((mod) => mod.SensumMap),
    {
        ssr: false,
        loading: () => (
            <div className="flex h-[calc(100vh-3.5rem)] items-center justify-center">
                Nalagam zemljevid...
            </div>
        ),
    }
)

export function MapClient() {
    return <SensumMap />
}