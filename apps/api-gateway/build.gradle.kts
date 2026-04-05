plugins {
    kotlin("jvm")
    id("application")
}

group = "si.sensum.api"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
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