import type { FeatureCollection, GeoJsonProperties, Geometry } from "geojson"

export type SensumGeoJson = FeatureCollection<Geometry, GeoJsonProperties>

export type StationDto = {
    stationId: number
    name?: string | null
    modbusAddress?: number | null
    serialNumber?: string | null
    stationType?: string | null
    description?: string | null
    latitude?: number | null
    longitude?: number | null
    source?: "SWS" | "MANUAL" | "SIM" | "DSL" | "UNKNOWN" | null
}

export type CreateStationCommand = {
    name: string
    latitude: number
    longitude: number
    description?: string
    serialNumber?: string
    source?: "MANUAL"
}

export type PendingStationPoint = {
    lat: number
    lng: number
}

export type ChannelDto = {
    stationId: number
    channelId: number
    name?: string | null
    unit?: string | null
    description?: string | null
}

export type MeasurementDto = {
    id?: number | null
    stationId: number
    channelId: number
    dateTime: string
    value: number
    status: number | string
}

export type SimulatedStationDto = {
    stationId: number
    name: string
    latitude: number
    longitude: number
    floodRisk: string | null
    nearRiver: boolean
    channels: {
        channelId: number
        name: string
        kind: string
        unit: string
        measurementCount: number
    }[]
}

export type SelectedStationDetails = {
    station: StationDto
    channels: ChannelDto[]
    measurementsByChannel: Record<number, MeasurementDto[]>
}

export type PanelPosition = {
    x: number
    y: number
}
