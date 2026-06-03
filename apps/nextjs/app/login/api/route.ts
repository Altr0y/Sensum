import { NextRequest, NextResponse } from "next/server";

type LoginResult = {
  token: string;
  expiresAt: string;
};

export async function POST(request: NextRequest) {
  const body = await request.json();

  const apiResponse = await fetch(`${process.env.API_INTERNAL_BASE_URL ?? "http://api-gateway:8080"}/api/v1/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

  const text = await apiResponse.text();

  if (!apiResponse.ok) {
    return NextResponse.json(
      { error: text || "Login failed" },
      { status: apiResponse.status }
    );
  }

  const loginResult = JSON.parse(text) as LoginResult;

  const response = NextResponse.json({
    ok: true,
    expiresAt: loginResult.expiresAt,
  });

  response.cookies.set("sensum_token", loginResult.token, {
    httpOnly: true,
    sameSite: "lax",
    secure: false,
    path: "/",
  });

  return response;
}