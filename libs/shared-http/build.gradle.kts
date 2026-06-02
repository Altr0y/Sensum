plugins {
    kotlin("jvm")
}

group = "si.sensum.shared.http"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.4.2")
    implementation("io.ktor:ktor-client-content-negotiation:3.4.2")
    implementation("io.ktor:ktor-client-cio:3.4.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.2")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}