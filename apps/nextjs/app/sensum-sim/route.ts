import { NextRequest, NextResponse } from "next/server"

const BACKEND_CORE_URL =
    process.env.BACKEND_CORE_INTERNAL_URL ?? "http://backend-core:8082"

export async function POST(request: NextRequest) {
    const token = request.cookies.get("sensum_token")?.value
    if (!token) {
        return NextResponse.json({ error: "Unauthorized" }, { status: 401 })
    }

    const body = await request.json()

    const response = await fetch(
        `${BACKEND_CORE_URL}/api/v1/simulator/generate`,
        {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body),
            cache: "no-store",
        }
    )

    const text = await response.text()
    return new NextResponse(text, {
        status: response.status,
        headers: {
            "content-type":
                response.headers.get("content-type") ?? "application/json",
        },
    })
}
