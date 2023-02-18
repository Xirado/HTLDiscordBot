import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.1"
    application
}

application {
    mainClass.set("at.xirado.htl.Main")

    tasks.run.get().workingDir = File(rootProject.projectDir, "build/libs")
}

group = "at.xirado"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.athena:athena-core:1.0.2")
    implementation("org.athena:athena-commands:1.0.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}