import Link from "next/link"

export default function HomePage() {
    return (
        <main
            className="grid min-h-[calc(100vh-3.5rem)] place-items-center bg-cover bg-center bg-no-repeat"
            style={{ backgroundImage: "url('/waves_transparent_vector.svg')" }}
        >
            <div className="w-full max-w-md rounded-xl border border-border bg-card p-8 text-center shadow-sm space-y-6">
                <div className="space-y-2">
                    <p className="text-sm text-muted-foreground">EL3 / SENSUM</p>
                    <h1 className="text-2xl font-bold text-foreground">
                        Digital Twin Platform
                    </h1>
                    <p className="text-sm text-muted-foreground">
                        Sign in to access measurement monitoring, map view, and station management.
                    </p>
                </div>

                <Link
                    href="/login"
                    className="inline-flex items-center justify-center rounded-md bg-primary px-6 py-2 text-sm font-semibold text-primary-foreground transition hover:opacity-90"
                >
                    Log in
                </Link>
            </div>
        </main>
    )
}
