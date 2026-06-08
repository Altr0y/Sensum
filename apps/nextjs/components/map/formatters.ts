export function formatApiLocalDateTime(date: Date) {
    const pad = (value: number) => value.toString().padStart(2, "0")

    return [
        date.getFullYear(),
        "-",
        pad(date.getMonth() + 1),
        "-",
        pad(date.getDate()),
        "T",
        pad(date.getHours()),
        ":",
        pad(date.getMinutes()),
        ":",
        pad(date.getSeconds()),
    ].join("")
}

export function formatDisplayDateTime(value: string) {
    const parsed = new Date(value)

    if (Number.isNaN(parsed.getTime())) {
        return value.replace("T", " ")
    }

    return new Intl.DateTimeFormat("en-GB", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
    }).format(parsed)
}

export function formatMeasurementValue(value: number) {
    return value.toLocaleString("en-US", {
        minimumFractionDigits: 0,
        maximumFractionDigits: 6,
    })
}

export function formatCoordinate(value?: number | null) {
    if (value == null) {
        return "-"
    }

    return value.toLocaleString("en-US", {
        minimumFractionDigits: 0,
        maximumFractionDigits: 6,
    })
}
