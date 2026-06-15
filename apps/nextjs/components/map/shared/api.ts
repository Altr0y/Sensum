import type {ChannelDto, CreateStationCommand, MeasurementDto, SensumGeoJson, StationDto} from "./types"

const API_BASE = "/sensum-api/v1"

async function fetchJson<T>(url: string): Promise<T> {
    const response = await fetch(url, {credentials: "include"})
    if (!response.ok) throw new Error(`HTTP ${response.status}`)
    return response.json()
}

async function postJson<T>(url: string, body: unknown, token?: string): Promise<T> {
    const headers: Record<string, string> = {"Content-Type": "application/json"}
    if (token) headers["Authorization"] = `Bearer ${token}`

    const response = await fetch(url, {
        credentials: "include",
        method: "POST",
        headers,
        body: JSON.stringify(body),
    })
    if (!response.ok) {
        const text = await response.text().catch(() => "")
        const apiMessage = tryParseApiError(text)
        throw new Error(apiMessage ?? `HTTP ${response.status}${text ? `: ${text}` : ""}`)
    }
    return response.json()
}

async function patchJson<T>(url: string, body: unknown, token?: string): Promise<T> {
    const headers: Record<string, string> = {"Content-Type": "application/json"}
    if (token) headers["Authorization"] = `Bearer ${token}`
    const response = await fetch(url, {
        credentials: "include",
        method: "PATCH",
        headers,
        body: JSON.stringify(body),
    })
    if (!response.ok) {
        const text = await response.text().catch(() => "")
        const apiMessage = tryParseApiError(text)
        throw new Error(apiMessage ?? `HTTP ${response.status}${text ? `: ${text}` : ""}`)
    }
    return response.json()
}

function tryParseApiError(body: string): string | null {
    try {
        const parsed = JSON.parse(body)
        if (typeof parsed?.error === "string") return parsed.error
    } catch {
        // not JSON
    }
    return null
}

export function fetchStations(): Promise<StationDto[]> {
    return fetchJson(`${API_BASE}/stations`)
}

export function fetchStationsGeoJson(): Promise<SensumGeoJson> {
    return fetchJson(`${API_BASE}/stations/geojson`)
}

export function fetchChannels(stationId: number): Promise<ChannelDto[]> {
    return fetchJson(`${API_BASE}/stations/${stationId}/channels`)
}

export function fetchMeasurements(
    channelId: number,
    from: string,
    to: string
): Promise<MeasurementDto[]> {
    return fetchJson(
        `${API_BASE}/measurements/range?channelId=${channelId}&from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`
    )
}

export function createStation(command: CreateStationCommand): Promise<StationDto> {
    return postJson(`${API_BASE}/stations`, command)
}

export function createChannels(
    stationId: number,
    kinds: string[],
    token: string
): Promise<ChannelDto[]> {
    return postJson(`${API_BASE}/stations/${stationId}/channels`, {kinds}, token)
}

export type UpdateStationPatch = {
    name?: string
    description?: string
    serialNumber?: string
    latitude?: number
    longitude?: number
}

export function updateStation(
    stationId: number,
    patch: UpdateStationPatch,
    token: string
): Promise<StationDto> {
    return patchJson(`${API_BASE}/stations/${stationId}`, patch, token)
}

export type DslProcessResult = {
    geoJson: string
    featureCount: number
}

export function processDsl(source: string): Promise<DslProcessResult> {
    return postJson(`${API_BASE}/dsl/process`, {source})
}

export type SimulateRequest = {
    mode: "full" | "measurements_only"
    count?: number
    prefix?: string
    channelKinds?: string[]
    polygon?: number[][]
    stationId?: number
    channelIds?: number[]
    from: string
    to: string
    intervalMinutes: number
}

export function runSimulation(request: SimulateRequest, token?: string): Promise<unknown> {
    return postJson("/api/v1/simulator/generate-and-save", request, token)
}

export type StationTimeRangeDto = {
    from: string | null
    to: string | null
}

export function fetchStationTimeRange(stationId: number): Promise<StationTimeRangeDto> {
    return fetchJson(`${API_BASE}/stats/stations/${stationId}/timerange`)
}

export function deleteStation(stationId: number): Promise<void> {
    return fetch(`${API_BASE}/stations/${stationId}`, {
        method: "DELETE",
        credentials: "include",
    }).then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
    })
}
