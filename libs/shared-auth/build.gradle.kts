plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.auth0:java-jwt:4.5.2")

    constraints {
        implementation("com.fasterxml.jackson.core:jackson-core:2.21.3") {
            because("Avoid vulnerable transitive jackson-core 2.15.4 reported as WS-2026-0003")
        }
        implementation("com.fasterxml.jackson.core:jackson-databind:2.21.3") {
            because("Keep Jackson databind aligned with patched jackson-core")
        }
        implementation("com.fasterxml.jackson.core:jackson-annotations:2.21") {
            because("Use existing Jackson annotations version for Jackson 2.21 line")
        }
    }
}

kotlin {
    jvmToolchain(21)
}