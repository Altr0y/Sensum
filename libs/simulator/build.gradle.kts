plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "si.sensum"
version = "unspecified"

dependencies {
    implementation(project(":libs:shared-models"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}