"use client"

export default function MonitoringPage() {
    const grafanaUrl =
        "/grafana/d/adv7vjj/sensum-digital-twin" +
        "?orgId=1" +
        "&kiosk" +
        "&theme=dark" +
        "&from=2026-01-01T00:00:00.000Z" +
        "&to=2026-12-31T23:59:59.000Z"

    return (
        <div className="-m-6 h-[calc(100vh-3.5rem)] bg-background">
            <iframe
                src={grafanaUrl}
                className="h-full w-full border-0"
                title="Sensum Digital Twin Dashboard"
            />
        </div>
    )
}