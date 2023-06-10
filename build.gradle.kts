plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "club.maxstats.kolour"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/maven/")
    mavenCentral()
}

dependencies {
    implementation("org.lwjgl.lwjgl:lwjgl:2.9.3")
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}