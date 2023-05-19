plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.weave-mc.weave-gradle") version "823628f548"
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
    compileOnly("com.github.weave-mc:weave-loader:c8c6186117")
    compileOnly("org.spongepowered:mixin:0.8.5")
}

minecraft.version("1.8.9")

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}