plugins {
    kotlin("jvm") version "1.8.0"
}

group = "fr.bananasmoothii"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // used for getting an instance of a reified Dimension type
    implementation(kotlin("reflect"))

    // coroutines
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.4")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
