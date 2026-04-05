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