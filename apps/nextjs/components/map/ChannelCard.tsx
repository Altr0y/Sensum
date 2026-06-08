"use client"

import type { ChannelDto, MeasurementDto } from "./types"
import { formatDisplayDateTime, formatMeasurementValue } from "./formatters"

type Props = {
    channel: ChannelDto
    measurements: MeasurementDto[]
}

export function ChannelCard({ channel, measurements }: Props) {
    return (
        <section className="overflow-hidden rounded-xl border border-border bg-background">
            <div className="border-b border-border p-3">
                <h4 className="text-sm font-semibold text-foreground">
                    {channel.name ?? `Channel ${channel.channelId}`}
                </h4>

                <p className="text-xs text-muted-foreground">
                    Channel ID: {channel.channelId}
                </p>

                {channel.description && (
                    <p className="mt-1 text-xs text-muted-foreground">
                        {channel.description}
                    </p>
                )}
            </div>

            {measurements.length === 0 ? (
                <p className="p-3 text-xs text-muted-foreground">
                    No recent measurements.
                </p>
            ) : (
                <div className="overflow-x-auto">
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
        </section>
    )
}
