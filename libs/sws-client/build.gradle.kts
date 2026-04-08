plugins {
    kotlin("jvm")
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.4.2")
    implementation("io.ktor:ktor-client-cio:3.4.2")
    implementation(project(":libs:logging"))
}