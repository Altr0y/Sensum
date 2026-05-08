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
    implementation(project(":libs:data-transform"))
    implementation(project(":libs:shared-models"))
    implementation("org.json:json:20240303")
    testImplementation(kotlin("test"))
    implementation("org.postgresql:postgresql:42.7.3")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
}

kotlin {
    jvmToolchain(21)
}

compose.desktop {
    application {
        mainClass = "si.sensum.demo.MainKt"

        nativeDistributions {
            modules("java.sql", "java.naming", "java.security.jgss", "java.management")
            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Rpm,
                TargetFormat.Msi,
                TargetFormat.Exe
            )

            packageName = "Sensum"
            packageVersion = "1.0.0"
            description = "Sensum Desktop App — EL3"
            copyright = "© 2025 EL3"

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

tasks.test {
    useJUnitPlatform()
}