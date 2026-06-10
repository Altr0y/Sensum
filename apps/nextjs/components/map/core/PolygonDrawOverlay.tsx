"use client"
import L from "leaflet"
import { useEffect } from "react"
import { Marker, Polygon, Polyline, useMap } from "react-leaflet"

interface Props {
    points: [number, number][]
    drawing: boolean
    onPointUpdate: (index: number, lat: number, lng: number) => void
    onPointRemove: (index: number) => void
}

const ACCENT = "#E8A838"
const DRAW_PANE = "sim-draw-pane"

function makeVertexIcon(isFirst: boolean, index: number): L.DivIcon {
    const bg = isFirst ? ACCENT : "#2B2B2B"
    const color = isFirst ? "#1E1E1E" : ACCENT
    const label = isFirst ? "★" : String(index + 1)
    return L.divIcon({
        className: "",
        html: `<div style="
            width:22px;height:22px;
            background:${bg};
            border:2px solid ${ACCENT};
            border-radius:50%;
            display:flex;align-items:center;justify-content:center;
            cursor:move;
            font-size:9px;font-weight:700;
            color:${color};
            user-select:none;
            box-shadow:0 0 0 2px rgba(0,0,0,0.4);
        ">${label}</div>`,
        iconSize: [22, 22],
        iconAnchor: [11, 11],
    })
}

export function PolygonDrawOverlay({ points, drawing, onPointUpdate, onPointRemove }: Props) {
    const map = useMap()

    // Keep the polygon being drawn above every other layer (rivers, regions,
    // municipalities, flood zones, ...) regardless of which order they were toggled on.
    useEffect(() => {
        if (!map.getPane(DRAW_PANE)) {
            const pane = map.createPane(DRAW_PANE)
            pane.style.zIndex = "450"
        }
    }, [map])

    // While actively drawing, let clicks/drags pass through other vector layers
    // straight to the map — otherwise a region/municipality polygon underneath
    // the cursor swallows the click and no vertex gets placed.
    useEffect(() => {
        const overlayPane = map.getPane("overlayPane")
        if (!overlayPane) return
        overlayPane.style.pointerEvents = drawing ? "none" : ""
        return () => {
            overlayPane.style.pointerEvents = ""
        }
    }, [map, drawing])

    if (points.length === 0) return null

    return (
        <>
            {points.length >= 3 ? (
                <Polygon
                    pane={DRAW_PANE}
                    positions={points}
                    pathOptions={{
                        color: ACCENT,
                        fillColor: ACCENT,
                        fillOpacity: 0.12,
                        weight: 2,
                        dashArray: "6 3",
                    }}
                />
            ) : (
                <Polyline
                    pane={DRAW_PANE}
                    positions={points}
                    pathOptions={{ color: ACCENT, weight: 2, dashArray: "6 3" }}
                />
            )}

            {points.map((pt, i) => (
                <Marker
                    key={i}
                    position={pt}
                    draggable
                    icon={makeVertexIcon(i === 0, i)}
                    eventHandlers={{
                        dragend(e) {
                            const ll = (e.target as L.Marker).getLatLng()
                            onPointUpdate(i, ll.lat, ll.lng)
                        },
                        contextmenu() {
                            onPointRemove(i)
                        },
                    }}
                />
            ))}
        </>
    )
}
