import { NextRequest, NextResponse } from "next/server"

const protectedRoutes = [
    "/monitoring",
    "/digital-twin",
    "/map",
]

export function proxy(request: NextRequest) {
    const token = request.cookies.get("sensum_token")?.value
    const pathname = request.nextUrl.pathname

    const isProtectedRoute = protectedRoutes.some((route) =>
        pathname.startsWith(route)
    )

    if (isProtectedRoute && !token) {
        return NextResponse.redirect(new URL("/login", request.url))
    }

    if (pathname === "/login" && token) {
        return NextResponse.redirect(new URL("/monitoring", request.url))
    }

    if (pathname === "/" && token) {
        return NextResponse.redirect(new URL("/monitoring", request.url))
    }

    if (pathname === "/" && !token) {
        return NextResponse.redirect(new URL("/login", request.url))
    }

    return NextResponse.next()
}

export const config = {
    matcher: [
        "/",
        "/login",
        "/monitoring/:path*",
        "/digital-twin/:path*",
        "/map/:path*",
    ],
}