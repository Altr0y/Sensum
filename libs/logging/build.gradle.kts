plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    api("io.github.microutils:kotlin-logging-jvm:3.0.5")

    implementation("io.ktor:ktor-server-core:3.4.2")
    implementation("io.ktor:ktor-server-call-id:3.4.2")
}