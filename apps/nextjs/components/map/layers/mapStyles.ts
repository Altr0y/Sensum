import type { Feature, GeoJsonProperties, Geometry } from "geojson"
import type { Layer } from "leaflet"
import L from "leaflet"
import { getFeatureName, getLayer, stationSourceBadges } from "./layerConfig"

function stationIcon(source?: string | null): L.DivIcon {
    const letter = source ? (stationSourceBadges[source.toUpperCase()] ?? "") : ""

    const html = `<div style="
        width:26px;height:26px;
        border-radius:50%;
        background:#eab308;
        border:2px solid #111827;
        box-shadow:0 1px 3px rgba(0,0,0,0.5);
        display:flex;align-items:center;justify-content:center;
        font-size:10px;font-weight:800;font-family:monospace;
        color:#111827;
        line-height:1;
        user-select:none;
    ">${letter}</div>`

    return L.divIcon({
        html,
        className: "",
        iconSize: [26, 26],
        iconAnchor: [13, 13],
    })
}

export function getStyle(feature?: Feature<Geometry, GeoJsonProperties>) {
    const layer = getLayer(feature)

    if (layer === "regions") {
        return { color: "#2563eb", weight: 2, fillColor: "#60a5fa", fillOpacity: 0.12 }
    }
    if (layer === "municipalities") {
        return { color: "#7c3aed", weight: 1, fillColor: "#c4b5fd", fillOpacity: 0.14 }
    }
    if (layer === "rivers") {
        return { color: "#0284c7", weight: 4, opacity: 0.9 }
    }
    if (layer === "lakes") {
        return { color: "#0891b2", weight: 2, fillColor: "#67e8f9", fillOpacity: 0.45 }
    }
    if (layer === "stations") {
        return { color: "#111827", weight: 2, fillColor: "#eab308", fillOpacity: 1 }
    }
    if (layer === "floods_often") {
        return { color: "#b91c1c", weight: 2, fillColor: "#ef4444", fillOpacity: 0.32 }
    }
    if (layer === "floods_rare") {
        return { color: "#c2410c", weight: 2, fillColor: "#f97316", fillOpacity: 0.25 }
    }
    if (layer === "floods_very_rare") {
        return { color: "#ca8a04", weight: 2, fillColor: "#facc15", fillOpacity: 0.2 }
    }

    return { color: "#111827", weight: 2, fillOpacity: 0.25 }
}

export function pointToLayer(
    feature: Feature<Geometry, GeoJsonProperties>,
    latlng: L.LatLng
) {
    const layer = getLayer(feature)

    if (layer === "stations") {
        const source = feature.properties?.source as string | null | undefined
        return L.marker(latlng, { icon: stationIcon(source), pane: "markerPane" })
    }

    return L.circleMarker(latlng, {
        radius: 5,
        color: "#111827",
        weight: 1,
        fillColor: "#ffffff",
        fillOpacity: 1,
    })
}

export function bindFeatureActions(onStationClick: (stationId: number) => void) {
    return function onEachFeature(
        feature: Feature<Geometry, GeoJsonProperties>,
        layer: Layer
    ) {
        const props = feature.properties ?? {}
        const name = getFeatureName(feature)
        const type = String(props.type ?? "-")
        const sourceFile = String(props.sourceFile ?? "-")
        const risk = props.risk ? `<br/><b>Risk:</b> ${props.risk}` : ""

        if (type === "station") {
            layer.on("click", () => {
                const stationId = Number(props.stationId ?? props.id)
                if (!Number.isNaN(stationId)) {
                    onStationClick(stationId)
                }
            })

            layer.bindTooltip(name)

            if ("bringToFront" in layer && typeof layer.bringToFront === "function") {
                layer.bringToFront()
            }

            return
        }

        layer.bindPopup(`
            <b>${name}</b><br/>
            <b>Type:</b> ${type}<br/>
            <b>Layer:</b> ${getLayer(feature)}<br/>
            <b>Source:</b> ${sourceFile}
            ${risk}
        `)
    }
}
