import Image from "next/image"
import Link from "next/link"
import {cookies} from "next/headers"

import {NAV_ITEMS} from "@/lib/constants"
import {NavItem} from "./NavItem"
import {ThemeToggle} from "@/components/theme/ThemeToggle"
import {LogoutButton} from "./LogoutButton"

export async function Navbar() {
    const cookieStore = await cookies()
    const isLoggedIn = Boolean(cookieStore.get("sensum_token")?.value)

    return (
        <header className="h-14 bg-card border-b border-border flex items-center px-6 gap-7 sticky top-0 z-50">
            <div className="flex items-center gap-2">
                <Image
                    src="/eltratec_logo.png"
                    alt="Sensum"
                    width={40}
                    height={40}
                    className="object-contain"
                />
            </div>

            <div className="flex items-center gap-2 mr-4">
                <span className="text-[#E8A838] font-bold text-lg tracking-wide">
                    Sensum
                </span>
                <span className="text-muted-foreground text-xs mt-1">
                    EL3
                </span>
            </div>

            {isLoggedIn && (
                <nav className="flex items-center gap-1">
                    {NAV_ITEMS.map((item) => (
                        <NavItem
                            key={item.href}
                            href={item.href}
                            label={item.label}
                            icon={item.icon}
                        />
                    ))}
                </nav>
            )}

            <div className="ml-auto flex items-center gap-2">
                <ThemeToggle />

                {isLoggedIn ? (
                    <LogoutButton />
                ) : (
                    <Link
                        href="/login"
                        className="
                            flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium
                            transition-colors duration-150 relative
                            text-[#7A7E82] hover:bg-[#2B2B2B] hover:text-[#D4D4D4]
                        "
                    >
                        Login
                    </Link>
                )}
            </div>
        </header>
    )
}