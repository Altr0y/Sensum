pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://packages.jetbrains.team/maven/p/firework/dev")
    }
}

rootProject.name = "Sensum"

include("apps:eltratec-gm")
include("apps:api-gateway")
include("apps:backend-core")
include("apps:desktop-demo")

include("libs:data-transform")
include("libs:logging")
include("libs:shared-auth")
include("libs:shared-http")
include("libs:shared-ktor")
include("libs:shared-models")
include("libs:sws-client")
include("libs:simulator")
