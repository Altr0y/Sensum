plugins {
    kotlin("jvm")
}

group = "si.sensum.shared.ktor"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.4.2")
    implementation("io.ktor:ktor-server-call-id:3.4.2")
    implementation("io.ktor:ktor-server-status-pages:3.4.2")

    implementation(project(":libs:shared-models"))
    implementation(project(":libs:logging"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}