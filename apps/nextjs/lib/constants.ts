export const SENSUM_COLORS = {
    background: "#1E1E1E",
    surface: "#2B2B2B",
    surfaceVariant: "#3C3F41",
    border: "#4A4D50",
    onBackground: "#BBBBBB",
    onSurface: "#D4D4D4",
    muted: "#7A7E82",
    accent: "#E8A838",
    accentMuted: "#7A4F10",
    onAccent: "#FFFFFF",
    success: "#4CAF7D",
    warning: "#E8A838",
    error: "#CF6679",
    info: "#4E9FD1",
} as const

export const API_URLS = {
    backendCore: process.env.NEXT_PUBLIC_BACKEND_CORE_URL ?? "http://localhost:8082",
    grafana: process.env.NEXT_PUBLIC_GRAFANA_URL ?? "http://localhost:3000",
} as const

export const NAV_ITEMS = [
    {href: "/monitoring", label: "Monitoring", icon: "monitor"},
    {href: "/map", label: "Map", icon: "map"},
    {href: "/digital-twin", label: "Digital Twin", icon: "activity"},
] as const