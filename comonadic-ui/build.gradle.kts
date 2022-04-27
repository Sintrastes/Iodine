import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
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
    google()
    maven(url="https://dl.bintray.com/kotlin/dokka")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

val arrow_version = "1.0.1"

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.21")
}

dependencies {
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(project(":base"))
    implementation(project(":core"))
    implementation("io.kindedj:kindedj:1.1.0")
    implementation("io.arrow-kt:arrow-core:1.0.1")
}

tasks.jar {
    archiveFileName.set("iodine-core.jar")
}