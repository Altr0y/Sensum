"use client"

import { useEffect } from "react"
import { useMap } from "react-leaflet"

export function ResizeMapOnSidebarChange({ sidebarOpen }: { sidebarOpen: boolean }) {
    const map = useMap()

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            map.invalidateSize()
        }, 250)

        return () => window.clearTimeout(timeoutId)
    }, [map, sidebarOpen])

    return null
}
