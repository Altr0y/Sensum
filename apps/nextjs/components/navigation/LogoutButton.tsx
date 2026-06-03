"use client"

import { useRouter } from "next/navigation"
import { LogOut } from "lucide-react"

export function LogoutButton() {
    const router = useRouter()

    async function handleLogout() {
        await fetch("/logout", {
            method: "POST",
        })

        router.push("/login")
        router.refresh()
    }

    return (
        <button
            type="button"
            onClick={handleLogout}
            className="flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
        >
            <LogOut size={16} />
            Logout
        </button>
    )
}