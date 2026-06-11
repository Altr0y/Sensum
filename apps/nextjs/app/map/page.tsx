import {cookies} from "next/headers"
import {MapClient} from "@/components/map/core/MapClient"

export default async function MapPage() {
    const cookieStore = await cookies()
    const token = cookieStore.get("sensum_token")?.value ?? ""

    return (
        <div className="-m-6">
            <MapClient token={token} />
        </div>
    )
}