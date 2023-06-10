plugins {
    kotlin("jvm") version "1.8.21"
    `maven-publish`
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
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}