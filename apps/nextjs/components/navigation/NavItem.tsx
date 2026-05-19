"use client"

import Link from "next/link"
import {usePathname} from "next/navigation"
import {Monitor, Map, Activity, LucideIcon} from "lucide-react"

const ICONS: Record<string, LucideIcon> = {
    monitor: Monitor,
    map: Map,
    activity: Activity,
}

interface NavItemProps {
    href: string
    label: string
    icon: string
}

export function NavItem({href, label, icon}: NavItemProps) {
    const pathname = usePathname()
    const isActive = pathname === href
    const Icon = ICONS[icon] ?? Monitor

    return (
        <Link
            href={href}
            className={`
        flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium
        transition-colors duration-150 relative
        ${isActive
                ? "bg-[#3C3F41] text-[#E8A838]"
                : "text-[#7A7E82] hover:bg-[#2B2B2B] hover:text-[#D4D4D4]"
            }
      `}
        >
            {isActive && (
                <div className="absolute bottom-0 left-2 right-2 h-[2px] bg-[#E8A838] rounded-t"/>
            )}
            <Icon size={16}/>
            {label}
        </Link>
    )
}