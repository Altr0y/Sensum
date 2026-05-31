import type { Metadata } from "next"
import { JetBrains_Mono } from "next/font/google"
import "./globals.css"
import { Navbar } from "@/components/navigation/Navbar"
import { ThemeProvider } from "@/components/theme/ThemeProvider"

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
        <html lang="en" suppressHydrationWarning>
        <body className={`${jetbrainsMono.variable} font-mono min-h-screen bg-background text-foreground`}>
        <ThemeProvider>
            <Navbar />
            <main className="p-6">
                {children}
            </main>
        </ThemeProvider>
        </body>
        </html>
    )
}