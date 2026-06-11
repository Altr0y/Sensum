"use client"
import { useState, useRef, useEffect } from "react"
import { ChevronDown } from "lucide-react"
import type { ChannelDto, MeasurementDto } from "../shared/types"
import { formatDisplayDateTime, formatMeasurementValue } from "../shared/formatters"

type Props = {
    channel: ChannelDto
    measurements: MeasurementDto[]
}

export function ChannelCard({ channel, measurements }: Props) {
    const [open, setOpen] = useState(false)
    const contentRef = useRef<HTMLDivElement>(null)
    const [height, setHeight] = useState(0)

    useEffect(() => {
        if (contentRef.current) {
            setHeight(open ? contentRef.current.scrollHeight : 0)
        }
    }, [open, measurements])

    return (
        <section className="overflow-hidden rounded-xl border border-border bg-background">
            <button
                type="button"
                onClick={() => setOpen((v) => !v)}
                className="flex w-full items-center justify-between p-3 text-left hover:bg-muted transition-colors"
            >
                <div>
                    <h4 className="text-sm font-semibold text-foreground">
                        {channel.name ?? `Channel ${channel.channelId}`}
                    </h4>
                    <p className="text-xs text-muted-foreground">
                        Channel ID: {channel.channelId}
                    </p>
                </div>
                <ChevronDown
                    size={16}
                    className={`shrink-0 text-muted-foreground transition-transform duration-200 ${open ? "rotate-180" : ""}`}
                />
            </button>

            <div
                style={{ height }}
                className="overflow-hidden transition-[height] duration-300 ease-in-out"
            >
                <div ref={contentRef}>
                    {channel.description && (
                        <p className="px-3 pb-2 text-xs text-muted-foreground">
                            {channel.description}
                        </p>
                    )}
                    {measurements.length === 0 ? (
                        <p className="p-3 text-xs text-muted-foreground">
                            No recent measurements.
                        </p>
                    ) : (
                        <div className="overflow-x-auto border-t border-border">
                            <table className="w-full text-left text-xs">
                                <thead className="border-b border-border bg-muted text-muted-foreground">
                                <tr>
                                    <th className="px-3 py-2 font-medium">Date and time</th>
                                    <th className="px-3 py-2 text-right font-medium">Value</th>
                                    <th className="px-3 py-2 text-right font-medium">Status</th>
                                </tr>
                                </thead>
                                <tbody>
                                {measurements.map((measurement, index) => (
                                    <tr
                                        key={`${measurement.channelId}-${measurement.dateTime}-${index}`}
                                        className="border-b border-border/60 last:border-0"
                                    >
                                        <td className="whitespace-nowrap px-3 py-2 text-muted-foreground">
                                            {formatDisplayDateTime(measurement.dateTime)}
                                        </td>
                                        <td className="whitespace-nowrap px-3 py-2 text-right font-semibold text-foreground">
                                            {formatMeasurementValue(measurement.value)}
                                        </td>
                                        <td className="whitespace-nowrap px-3 py-2 text-right text-muted-foreground">
                                            {String(measurement.status)}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        </section>
    )
}