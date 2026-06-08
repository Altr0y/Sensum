import Link from "next/link"
import { AlertTriangle } from "lucide-react"

export default function NotFound() {
    return (
        <main className="grid min-h-[calc(100vh-3.5rem)] place-items-center">
            <div className="w-full max-w-md rounded-xl border border-border bg-card p-6 text-center shadow-sm">
                <div className="mb-4 flex justify-center">
                    <AlertTriangle size={36} className="text-[#E8A838]" />
                </div>
                <p className="text-sm text-muted-foreground">EL3 / SENSUM</p>
                <h1 className="mt-1 text-2xl font-bold text-foreground">Page not found</h1>
                <p className="mt-2 text-sm text-muted-foreground">
                    The page you are looking for doesn&apos;t exist or has been moved.
                </p>
                <Link
                    href="/"
                    className="mt-6 inline-flex items-center justify-center rounded-md bg-[#E8A838] px-4 py-2 text-sm font-medium text-[#111827] transition-colors hover:bg-[#d4962e]"
                >
                    Back to home
                </Link>
            </div>
        </main>
    )
}
