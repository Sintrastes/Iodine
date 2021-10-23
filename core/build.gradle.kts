import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenLocal()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-alpha4-build348"
    id("org.jetbrains.dokka")
}

group = "com.bedelln"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
    google()
    maven(url="https://dl.bintray.com/kotlin/dokka")
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
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
    implementation("io.github.sintrastes:buildable-kt-interfaces:1.0-SNAPSHOT")
}

tasks.jar {
    archiveFileName.set("iodine-core.jar")
}