plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":libs:shared-models"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.opencsv:opencsv:5.9") {
        exclude(group = "commons-beanutils", module = "commons-beanutils")
        exclude(group = "org.apache.commons", module = "commons-lang3")
    }
    implementation("org.json:json:20240303")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}