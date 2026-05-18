import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose.hot-reload")
}

group = "si.sensum.demo"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.components.resources)

    implementation(project(":libs:data-transform"))
    implementation(project(":libs:shared-models"))

    implementation("org.json:json:20240303")
    implementation("org.postgresql:postgresql:42.7.3")

    testImplementation(kotlin("test"))

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:4.12.1")
    implementation("org.jetbrains.lets-plot:lets-plot-common:4.8.2")
    implementation("org.jetbrains.lets-plot:canvas:4.8.2")
    implementation("org.jetbrains.lets-plot:plot-raster:4.8.2")
    implementation("org.jetbrains.lets-plot:lets-plot-compose:3.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("io.ktor:ktor-client-core:3.4.2")
    implementation("io.ktor:ktor-client-okhttp:3.4.2")
    implementation("io.ktor:ktor-client-content-negotiation:3.4.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

kotlin {
    jvmToolchain(21)
}

compose.desktop {
    application {
        mainClass = "si.sensum.demo.MainKt"

        nativeDistributions {
            modules(
                "java.sql",
                "java.naming",
                "java.security.jgss",
                "java.management"
            )

            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Rpm,
                TargetFormat.Msi,
                TargetFormat.Exe
            )

            packageName = "Sensum"
            packageVersion = "1.0.0"
            description = "Sensum Desktop App — EL3"
            copyright = "© 2026 EL3"

            linux {
                packageName = "sensum"
                menuGroup = "Science"
                appCategory = "Science"
            }

            windows {
                menuGroup = "Sensum"
                perUserInstall = true
                dirChooser = true
                shortcut = true
                menu = true
            }
        }
    }
}

compose.resources {
    packageOfResClass = "si.sensum.demo.resources"
}

tasks.test {
    useJUnitPlatform()
}