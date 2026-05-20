import type {Metadata} from "next"
import {JetBrains_Mono} from "next/font/google"
import "./globals.css"
import {Navbar} from "@/components/navigation/Navbar"

const jetbrainsMono = JetBrains_Mono({
    subsets: ["latin"],
    variable: "--font-mono",
})

export const metadata: Metadata = {
    title: "Sensum",
    description: "Sensum EL3 — Digital Twin Platform",
}

export default function RootLayout({
                                       children,
                                   }: {
    children: React.ReactNode
}) {
    return (
        <html lang="en" className="dark">
        <body className={`${jetbrainsMono.variable} font-mono bg-[#1E1E1E] text-[#D4D4D4] min-h-screen`}>
        <Navbar/>
        <main className="p-6">
            {children}
        </main>
        </body>
        </html>
    )
}