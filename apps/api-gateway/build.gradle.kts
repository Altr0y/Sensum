plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("application")
}

group = "si.sensum.api"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.4.2")
    implementation("io.ktor:ktor-server-netty:3.4.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.4.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.2")
    implementation("io.ktor:ktor-server-call-id:3.4.2")
    implementation("io.ktor:ktor-server-status-pages:3.4.2")

    implementation("io.ktor:ktor-server-auth:3.4.2")
    implementation("io.ktor:ktor-server-auth-jwt:3.4.2")


    implementation("io.ktor:ktor-client-core:3.4.2")
    implementation("io.ktor:ktor-client-cio:3.4.2")
    implementation("io.ktor:ktor-client-content-negotiation:3.4.2")

    implementation("ch.qos.logback:logback-classic:1.5.32")

    implementation(project(":libs:shared-auth"))
    implementation(project(":libs:shared-models"))
    implementation(project(":libs:shared-http"))
    implementation(project(":libs:shared-ktor"))
    implementation(project(":libs:logging"))

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.4.2")
    testImplementation("io.ktor:ktor-client-mock:3.4.2")

    constraints {
        implementation("io.netty:netty-codec-http:4.2.11.Final") {
            because("Fixes CVE-2026-33870")
        }
        implementation("io.netty:netty-codec-http2:4.2.11.Final") {
            because("Fixes CVE-2026-33871")
        }
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

application {
    mainClass.set("si.sensum.api.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}