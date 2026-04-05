pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "Sensum"

include("apps:eltratec-gm")
include("apps:api-gateway")
include("apps:backend-core")
include("apps:desktop-demo")
include("libs:shared-models")
include("libs:shared-auth")
include("libs:sws-client")