import org.jetbrains.compose.compose

buildscript {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev675"
    id("org.jetbrains.dokka")
}

group = "com.bedelln"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    google()
    maven(url="https://dl.bintray.com/kotlin/dokka")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val arrow_version = "0.13.2"

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.0")
}

dependencies {
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrow_version")
    implementation(project(":base"))
    // implementation("io.github.sintrastes:buildable-kt-interfaces:1.0-SNAPSHOT")
}

tasks.jar {
    archiveFileName.set("iodine-core.jar")
}