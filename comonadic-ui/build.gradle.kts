import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
    // dependencies {
    //     classpath("io.arrow-kt:arrow-proofs-gradle-plugin:1.5.31-SNAPSHOT")
    // }
}

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-beta6-dev462"
    id("org.jetbrains.dokka")
    // id("io.arrow-kt.proofs") version "1.5.31-SNAPSHOT"
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

val arrow_version = "0.13.2"

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.0")
}

dependencies {
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(project(":base"))
    implementation(project(":core"))
    implementation("io.kindedj:kindedj:1.1.0")
    implementation("io.arrow-kt:arrow-core:1.0.0")
}

tasks.jar {
    archiveFileName.set("iodine-core.jar")
}