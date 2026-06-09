import { LoginForm } from "@/components/auth/LoginForm"

export default function LoginPage() {
    return (
        <main
            className="grid min-h-[calc(100vh-3.5rem)] place-items-center bg-cover bg-center bg-no-repeat"
            style={{ backgroundImage: "url('/waves_transparent_vector.svg')" }}
        >
            <LoginForm />
        </main>
    )
}