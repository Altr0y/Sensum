import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
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

    implementation("io.ktor:ktor-client-core:3.4.2")
    implementation("io.ktor:ktor-client-cio:3.4.2")
    implementation("io.ktor:ktor-client-content-negotiation:3.4.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.2")

    implementation(project(":libs:data-transform"))
    implementation(project(":libs:shared-models"))

    implementation("org.json:json:20240303")
    implementation("org.postgresql:postgresql:42.7.3")

    testImplementation(kotlin("test"))
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