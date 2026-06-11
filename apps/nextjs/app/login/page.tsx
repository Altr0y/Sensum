import { LoginForm } from "@/components/auth/LoginForm"

export default function LoginPage() {
    return (
        <div
            className="grid h-[calc(100vh-3.5rem)] place-items-center bg-cover bg-center bg-no-repeat -m-6 overflow-hidden"
            style={{ backgroundImage: "url('/waves_transparent_vector.svg')" }}
        >
            <LoginForm />
        </div>
    )
}