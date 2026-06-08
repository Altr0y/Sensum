import {cookies} from "next/headers"
import {MapClient} from "@/components/map/MapClient"

export default async function MapPage() {
    const cookieStore = await cookies()
    const token = cookieStore.get("sensum_token")?.value ?? ""

    return <MapClient token={token} />
}