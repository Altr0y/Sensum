import { NextRequest, NextResponse } from "next/server"

const PROTECTED = ["/monitoring", "/digital-twin", "/map"]
const REDIRECT_IF_AUTHED = ["/home", "/login"]

export function proxy(request: NextRequest) {
    const token = request.cookies.get("sensum_token")?.value
    const { pathname } = request.nextUrl

    const isProtected =
        pathname === "/" ||
        PROTECTED.some((p) => pathname === p || pathname.startsWith(p + "/"))

    if (isProtected && !token) {
        return NextResponse.redirect(new URL("/home", request.url))
    }

    if (REDIRECT_IF_AUTHED.includes(pathname) && token) {
        return NextResponse.redirect(new URL("/monitoring", request.url))
    }

    return NextResponse.next()
}

export const config = {
    matcher: [
        "/",
        "/home",
        "/login",
        "/monitoring/:path*",
        "/digital-twin/:path*",
        "/map/:path*",
    ],
}
