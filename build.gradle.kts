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
    testImplementation("io.kotest:kotest-assertions-core:5.5.5")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")

    // used for getting an instance of a reified Dimension type
    implementation(kotlin("reflect"))

    // coroutines
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.4")

}

tasks.test {
    testLogging.showStandardStreams = true
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}
