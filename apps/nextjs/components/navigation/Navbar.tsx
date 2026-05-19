"use client"

import {NAV_ITEMS} from "@/lib/constants"
import {NavItem} from "./NavItem"

export function Navbar() {
    return (
        <header className="h-14 bg-[#2B2B2B] border-b border-[#4A4D50] flex items-center px-6 gap-8 sticky top-0 z-50">
            {/* Logo */}
            <div className="flex items-center gap-2 mr-4">
        <span className="text-[#E8A838] font-bold text-lg tracking-wide">
          Sensum
        </span>
                <span className="text-[#4A4D50] text-xs mt-1">EL3</span>
            </div>

            {/* Nav items */}
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
        </header>
    )
}