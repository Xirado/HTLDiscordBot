application {
    mainClass.set("at.xirado.htl.Main")
}

group = "at.xirado"
version = "1.0.0"

plugins {
    kotlin("jvm") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "7.1.1"
    application
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // JDA
    implementation("com.github.Xirado:JDA:bc50c87")
    implementation("com.github.minndevelopment:jda-ktx:9f01b74")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha14")

    // General Utilities
    implementation("io.github.classgraph:classgraph:4.8.141")


}