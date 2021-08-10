import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        gradlePluginPortal()
    }
}

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-alpha3"
    id("org.jetbrains.dokka")
}

group = "com.bedelln"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    // TODO: It seems like dokka doesn't build without jcenter.
    jcenter()
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
}

tasks.jar {
    archiveFileName.set("iodine-core.jar")
}