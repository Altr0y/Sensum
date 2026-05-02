plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("application")
}

group = "si.sensum.backend"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.4.2")
    implementation("io.ktor:ktor-server-netty:3.4.2")
    implementation("io.ktor:ktor-server-call-logging:3.4.2")
    implementation("io.ktor:ktor-server-call-id:3.4.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.4.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.2")
    implementation("ch.qos.logback:logback-classic:1.5.32")
    implementation("org.jetbrains.exposed:exposed-core:1.1.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.1.1")
    implementation("org.postgresql:postgresql:42.7.11")

    implementation(project(":libs:shared-models"))
    implementation(project(":libs:logging"))

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.4.2")

    constraints {
        implementation("io.netty:netty-codec-http:4.2.11.Final") {
            because("Fixes CVE-2026-33870")
        }
        implementation("io.netty:netty-codec-http2:4.2.11.Final") {
            because("Fixes CVE-2026-33871")
        }
    }
}

application {
    mainClass.set("si.sensum.backend.ApplicationKt")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}