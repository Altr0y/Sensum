import {NextRequest, NextResponse} from "next/server"

const API_GATEWAY_URL =
    process.env.API_INTERNAL_BASE_URL ?? "http://localhost:8080"

type RouteContext = {
    params: Promise<{
        path: string[]
    }>
}

async function proxyRequest(
    request: NextRequest,
    context: RouteContext
) {
    const {path} = await context.params

    const token = request.cookies.get("sensum_token")?.value

    if (!token) {
        return NextResponse.json(
            {error: "Missing authentication token"},
            {status: 401}
        )
    }

    const targetPath = path.join("/")

    const targetUrl = new URL(
        `/api/${targetPath}${request.nextUrl.search}`,
        API_GATEWAY_URL
    )

    console.log("SENSUM API TARGET", targetUrl.toString())

    const headers = new Headers()

    const contentType = request.headers.get("content-type")
    if (contentType) {
        headers.set("content-type", contentType)
    }

    headers.set("authorization", `Bearer ${token}`)

    const method = request.method.toUpperCase()

    const response = await fetch(targetUrl, {
        method,
        headers,
        body:
            method === "GET" || method === "HEAD"
                ? undefined
                : await request.text(),
        cache: "no-store",
    })

    const responseBody = await response.text()

    return new NextResponse(responseBody, {
        status: response.status,
        headers: {
            "content-type":
                response.headers.get("content-type") ?? "application/json",
        },
    })
}

export async function GET(
    request: NextRequest,
    context: RouteContext
) {
    return proxyRequest(request, context)
}

export async function POST(
    request: NextRequest,
    context: RouteContext
) {
    return proxyRequest(request, context)
}

export async function PUT(
    request: NextRequest,
    context: RouteContext
) {
    return proxyRequest(request, context)
}

export async function DELETE(
    request: NextRequest,
    context: RouteContext
) {
    return proxyRequest(request, context)
}

export async function PATCH(
    request: NextRequest,
    context: RouteContext
) {
    return proxyRequest(request, context)
}