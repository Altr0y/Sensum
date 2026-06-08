export type SimStep = "draw" | "configure" | "result"

export type ChannelKind = "water_level" | "temperature" | "rainfall" | "flow_rate"

export const CHANNEL_KIND_LABELS: Record<ChannelKind, string> = {
    water_level: "Water level",
    temperature: "Temperature",
    rainfall: "Rainfall",
    flow_rate: "Flow rate",
}

export const ALL_CHANNEL_KINDS: ChannelKind[] = [
    "water_level",
    "temperature",
    "rainfall",
    "flow_rate",
]

export interface SimConfig {
    channelKinds: ChannelKind[]
    from: string
    to: string
    intervalMinutes: number
    stationCount: number
    prefix: string
}

export interface SimResult {
    mode: "existing" | "new"
    stationCount: number
    from: string
    to: string
}
