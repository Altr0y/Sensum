import { cookies } from "next/headers"
import { MonitoringViewer } from "@/components/monitoring/MonitoringViewer"

export default async function MonitoringPage({
    searchParams,
}: {
    searchParams: Promise<{ dashboard?: string; from?: string; to?: string }>
}) {
    const cookieStore = await cookies()
    const token = cookieStore.get("sensum_token")?.value ?? ""
    const params = await searchParams

    return (
        <MonitoringViewer
            token={token}
            dashboardId={params.dashboard}
            from={params.from}
            to={params.to}
        />
    )
}
