"use client"
import { useState, useCallback } from "react"
import type { StationDto, ChannelDto } from "../types"
import type { SimStep, SimConfig, SimResult } from "./types"
import { stationsInsidePolygon, makeDefaultConfig, localInputToIso } from "./utils"
import { fetchChannels, runSimulation } from "../api"

export const SIM_MIN_POINTS = 3
export const SIM_MAX_POINTS = 10

export function useSimulation(allStations: StationDto[]) {
    const [active, setActive] = useState(false)
    const [step, setStep] = useState<SimStep>("draw")
    const [points, setPoints] = useState<[number, number][]>([])
    const [stationsInArea, setStationsInArea] = useState<StationDto[]>([])
    const [channelsByStation, setChannelsByStation] = useState<
        Record<number, ChannelDto[]>
    >({})
    const [config, setConfig] = useState<SimConfig>(makeDefaultConfig)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [result, setResult] = useState<SimResult | null>(null)

    const start = useCallback(() => {
        setActive(true)
        setStep("draw")
        setPoints([])
        setError(null)
        setResult(null)
        setConfig(makeDefaultConfig())
    }, [])

    const stop = useCallback(() => {
        setActive(false)
        setPoints([])
        setStep("draw")
        setResult(null)
        setError(null)
    }, [])

    const addPoint = useCallback((lat: number, lng: number) => {
        setPoints((prev) =>
            prev.length >= SIM_MAX_POINTS ? prev : [...prev, [lat, lng]]
        )
    }, [])

    const removeLastPoint = useCallback(() => {
        setPoints((prev) => prev.slice(0, -1))
    }, [])

    const removePoint = useCallback((index: number) => {
        setPoints((prev) => prev.filter((_, i) => i !== index))
    }, [])

    const updatePoint = useCallback((index: number, lat: number, lng: number) => {
        setPoints((prev) => prev.map((pt, i) => (i === index ? [lat, lng] : pt)))
    }, [])

    const reset = useCallback(() => setPoints([]), [])

    const confirm = useCallback(async () => {
        const found = stationsInsidePolygon(allStations, points)
        setStationsInArea(found)
        if (found.length > 0) {
            const entries = await Promise.all(
                found.map(async (s) => {
                    const ch = await fetchChannels(s.stationId).catch(
                        () => [] as ChannelDto[]
                    )
                    return [s.stationId, ch] as [number, ChannelDto[]]
                })
            )
            setChannelsByStation(Object.fromEntries(entries))
        }
        setStep("configure")
    }, [allStations, points])

    const run = useCallback(async () => {
        setLoading(true)
        setError(null)
        try {
            const hasExisting = stationsInArea.length > 0
            const from = localInputToIso(config.from)
            const to = localInputToIso(config.to)

            if (hasExisting) {
                await Promise.all(
                    stationsInArea.map((s) =>
                        runSimulation({
                            mode: "measurements_only",
                            stationId: s.stationId,
                            channelIds: (channelsByStation[s.stationId] ?? []).map(
                                (c) => c.channelId
                            ),
                            from,
                            to,
                            intervalMinutes: config.intervalMinutes,
                        })
                    )
                )
            } else {
                await runSimulation({
                    mode: "full",
                    count: config.stationCount,
                    prefix: config.prefix,
                    channelKinds: config.channelKinds,
                    from,
                    to,
                    intervalMinutes: config.intervalMinutes,
                })
            }

            setResult({
                mode: hasExisting ? "existing" : "new",
                stationCount: hasExisting
                    ? stationsInArea.length
                    : config.stationCount,
                from: config.from,
                to: config.to,
            })
            setStep("result")
        } catch (e) {
            setError(e instanceof Error ? e.message : "Error while generating")
        } finally {
            setLoading(false)
        }
    }, [stationsInArea, channelsByStation, config])

    return {
        active,
        step,
        points,
        stationsInArea,
        channelsByStation,
        config,
        loading,
        error,
        result,
        start,
        stop,
        addPoint,
        removeLastPoint,
        removePoint,
        updatePoint,
        reset,
        confirm,
        run,
        setConfig,
    }
}
