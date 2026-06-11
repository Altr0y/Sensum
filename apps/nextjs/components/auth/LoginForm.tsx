"use client"

import { FormEvent, useState } from "react"
import { useRouter } from "next/navigation"

type LoginCommand = {
    username: string
    password: string
}

type LoginApiResponse = {
    ok?: boolean
    error?: string
    expiresAt?: string
}

export function LoginForm() {
    const router = useRouter()

    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [error, setError] = useState<string | null>(null)
    const [isLoading, setIsLoading] = useState(false)

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault()
        setError(null)
        setIsLoading(true)

        const requestBody: LoginCommand = {
            username,
            password,
        }

        try {
            const response = await fetch("/login/api", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(requestBody),
            })

            const data = (await response.json()) as LoginApiResponse

            if (!response.ok) {
                setError(data.error ?? "Login failed.")
                return
            }

            router.push("/map")
            router.refresh()
        } catch {
            setError("Login request failed.")
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <form
            onSubmit={handleSubmit}
            className="w-full max-w-md rounded-xl border border-border bg-card p-6 shadow-sm"
        >
            <div className="mb-6">
                <p className="text-sm text-muted-foreground">
                    EL3 / SENSUM
                </p>
                <h1 className="mt-1 text-2xl font-bold text-foreground">
                    Sign in
                </h1>
                <p className="mt-2 text-sm text-muted-foreground">
                    Login to access the digital twin dashboard.
                </p>
            </div>

            <div className="space-y-4">
                <div className="space-y-2">
                    <label
                        htmlFor="username"
                        className="text-sm font-medium text-foreground"
                    >
                        Username
                    </label>
                    <input
                        id="username"
                        value={username}
                        onChange={(event) => setUsername(event.target.value)}
                        className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm text-foreground outline-none transition focus:ring-2 focus:ring-ring"
                        placeholder="sensum"
                        autoComplete="username"
                    />
                </div>

                <div className="space-y-2">
                    <label
                        htmlFor="password"
                        className="text-sm font-medium text-foreground"
                    >
                        Password
                    </label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm text-foreground outline-none transition focus:ring-2 focus:ring-ring"
                        placeholder="••••••••"
                        autoComplete="current-password"
                    />
                </div>

                {error && (
                    <div className="rounded-md border border-destructive/40 bg-destructive/10 px-3 py-2 text-sm text-destructive">
                        {error}
                    </div>
                )}

                <button
                    type="submit"
                    disabled={isLoading}
                    className="w-full rounded-md bg-primary px-4 py-2 text-sm font-semibold text-primary-foreground transition hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-60"
                >
                    {isLoading ? "Signing in..." : "Sign in"}
                </button>
            </div>
        </form>
    )
}