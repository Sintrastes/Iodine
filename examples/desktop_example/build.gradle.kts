import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    kotlin("jvm") version "1.5.30"
    id("org.jetbrains.compose") version "1.0.0-alpha4-build348"
}

group = "com.bedelln"
version = "1.0.0"

repositories {
    jcenter()
    mavenCentral()
    google()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(files("$projectDir/libs/iodine-core.jar"))
    implementation(files("$projectDir/libs/iodine-desktop.jar"))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            *freeCompilerArgs.toTypedArray(),
            // "-Xallow-jvm-ir-dependencies",
            "-Xskip-prerelease-check"
        )
        useIR = true
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "iodine_desktop_example_project"
            // packageVersion = "1.0.0"
        }
    }
}