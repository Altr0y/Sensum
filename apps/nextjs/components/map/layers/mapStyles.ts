import type {Feature, GeoJsonProperties, Geometry} from "geojson"
import type {Layer} from "leaflet"
import L from "leaflet"
import {getFeatureName, getLayer, stationSourceBadges, stationSourceColors} from "./layerConfig"

function stationIcon(source: string | null | undefined, isSelected: boolean): L.DivIcon {
    const sourceKey = source ? source.toUpperCase() : "UNKNOWN"
    const letter = stationSourceBadges[sourceKey] ?? ""
    const fill = stationSourceColors[sourceKey] ?? "#eab308"
    const size = isSelected ? 38 : 30
    const border = isSelected ? "3px solid #E8A838" : "2px solid #111827"
    const shadow = isSelected
        ? "0 0 0 4px rgba(232,168,56,0.35), 0 1px 4px rgba(0,0,0,0.5)"
        : "0 1px 3px rgba(0,0,0,0.5)"
    const fontSize = isSelected ? 13 : 11
    const html = `<div style="
        width:${size}px;height:${size}px;
        border-radius:50%;
        background:${fill};
        border:${border};
        box-shadow:${shadow};
        display:flex;align-items:center;justify-content:center;
        font-size:${fontSize}px;font-weight:800;font-family:'JetBrains Mono',monospace;
        color:#111827;
        line-height:1;
        user-select:none;
        transition:all 0.15s ease;
    ">${letter}</div>`
    return L.divIcon({
        html,
        className: "",
        iconSize: [size, size],
        iconAnchor: [size / 2, size / 2],
    })
}

function previewStationIcon(): L.DivIcon {
    const size = 36
    const html = `<div style="
        width:${size}px;height:${size}px;
        border-radius:50%;
        background:#E8A838;
        border:3px dashed #1E1E1E;
        box-shadow:0 0 0 6px rgba(232,168,56,0.3), 0 2px 6px rgba(0,0,0,0.5);
        display:flex;align-items:center;justify-content:center;
        font-size:9px;font-weight:800;font-family:'JetBrains Mono',monospace;
        color:#1E1E1E;
        line-height:1;
        text-align:center;
        user-select:none;
    ">NEW</div>`
    return L.divIcon({
        html,
        className: "",
        iconSize: [size, size],
        iconAnchor: [size / 2, size / 2],
    })
}

export function getStyle(feature?: Feature<Geometry, GeoJsonProperties>) {
    const layer = getLayer(feature)

    if (layer === "regions") {
        return {color: "#2563eb", weight: 2, fillColor: "#60a5fa", fillOpacity: 0.12}
    }
    if (layer === "municipalities") {
        return {color: "#7c3aed", weight: 1, fillColor: "#c4b5fd", fillOpacity: 0.14}
    }
    if (layer === "rivers") {
        return {color: "#0284c7", weight: 4, opacity: 0.9}
    }
    if (layer === "lakes") {
        return {color: "#0891b2", weight: 2, fillColor: "#67e8f9", fillOpacity: 0.45}
    }
    if (layer === "stations") {
        return {color: "#111827", weight: 2, fillColor: "#eab308", fillOpacity: 1}
    }
    if (layer === "floods_often") {
        return {color: "#b91c1c", weight: 2, fillColor: "#ef4444", fillOpacity: 0.32}
    }
    if (layer === "floods_rare") {
        return {color: "#c2410c", weight: 2, fillColor: "#f97316", fillOpacity: 0.25}
    }
    if (layer === "floods_very_rare") {
        return {color: "#ca8a04", weight: 2, fillColor: "#facc15", fillOpacity: 0.2}
    }

    return {color: "#111827", weight: 2, fillOpacity: 0.25}
}

export function pointToLayer(
    feature: Feature<Geometry, GeoJsonProperties>,
    latlng: L.LatLng,
    selectedStationIds?: Set<number>,
    moveModeStationId?: number | null
) {
    const layer = getLayer(feature)
    const featureType = String(feature.properties?.type ?? "")
    const isPreviewStation = layer !== "stations" && featureType === "station"

    if (layer === "stations") {
        const source = feature.properties?.source as string | null | undefined
        const stationId = Number(feature.properties?.stationId ?? feature.properties?.id)
        const isSelected = selectedStationIds?.has(stationId) ?? false
        const isMoveTarget = moveModeStationId != null && stationId === moveModeStationId
        return L.marker(latlng, {
            icon: stationIcon(source, isSelected),
            pane: "markerPane",
            draggable: isMoveTarget,
        })
    }

    if (isPreviewStation) {
        return L.marker(latlng, {
            icon: previewStationIcon(),
            pane: "markerPane",
        })
    }

    return L.circleMarker(latlng, {
        radius: 5,
        color: "#111827",
        weight: 1,
        fillColor: "#ffffff",
        fillOpacity: 1,
    })
}

export function bindFeatureActions(
    onStationClick: (stationId: number, isMultiSelect: boolean) => void,
    onStationMoved?: (stationId: number, lat: number, lng: number) => void,
    moveModeStationId?: number | null
) {
    return function onEachFeature(
        feature: Feature<Geometry, GeoJsonProperties>,
        layer: Layer
    ) {
        const props = feature.properties ?? {}
        const name = getFeatureName(feature)
        const type = String(props.type ?? "-")
        const sourceFile = String(props.sourceFile ?? "-")
        const risk = props.risk ? `<br/><b>Risk:</b> ${props.risk}` : ""
        const featureLayer = getLayer(feature)
        const isPreviewStation = type === "station" && featureLayer !== "stations"

        if (isPreviewStation) {
            layer.bindTooltip(`${name} (preview - not saved)`, {
                className: "sensum-station-tooltip",
                direction: "top",
                offset: [0, -18],
            })
            if ("bringToFront" in layer && typeof layer.bringToFront === "function") {
                layer.bringToFront()
            }
            return
        }

        if (type === "station") {
            const stationId = Number(props.stationId ?? props.id)
            const isMoveTarget = moveModeStationId != null && stationId === moveModeStationId

            if (isMoveTarget && onStationMoved) {
                layer.on("dragend", (e: L.DragEndEvent) => {
                    const marker = e.target as L.Marker
                    const pos = marker.getLatLng()
                    onStationMoved(stationId, pos.lat, pos.lng)
                })
            } else {
                layer.on("click", (e: L.LeafletMouseEvent) => {
                    if (!Number.isNaN(stationId)) {
                        const isMultiSelect = e.originalEvent.ctrlKey || e.originalEvent.metaKey
                        onStationClick(stationId, isMultiSelect)
                    }
                })
            }

            layer.bindTooltip(name, {
                className: "sensum-station-tooltip",
                direction: "top",
                offset: [0, -14],
            })

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