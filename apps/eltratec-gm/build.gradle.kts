plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("application")
}

group = "si.sensum.gm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.4.2")
    implementation("io.ktor:ktor-server-netty:3.4.2")
    implementation("io.ktor:ktor-server-call-logging:3.4.2")
    implementation("io.ktor:ktor-server-call-id:3.4.2")
    implementation("io.ktor:ktor-server-status-pages:3.4.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.4.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.2")
    implementation("ch.qos.logback:logback-classic:1.5.32")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.4.2")
    implementation("io.ktor:ktor-client-core:3.4.2")
    implementation("io.ktor:ktor-client-cio:3.4.2")

    implementation(project(":libs:shared-models"))
    implementation(project(":libs:shared-auth"))
    implementation(project(":libs:sws-client"))
    implementation(project(":libs:logging"))

    constraints {
        implementation("io.netty:netty-codec-http:4.2.11.Final") {
            because("Fixes CVE-2026-33870")
        }
        implementation("io.netty:netty-codec-http2:4.2.11.Final") {
            because("Fixes CVE-2026-33871")
        }
    }
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("si.sensum.gm.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}