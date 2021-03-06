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
    kotlin("jvm") version "1.7.0"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev748"
}

group = "com.bedelln"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("io.arrow-kt:arrow-fx-coroutines:0.13.2")
    implementation("org.pushing-pixels:aurora-theming:1.0.0-beta5")
    implementation("org.pushing-pixels:aurora-component:1.0.0-beta5")
    implementation("org.pushing-pixels:aurora-window:1.0.0-beta5")
    implementation(files("$projectDir/libs/iodine-base.jar"))
    implementation(files("$projectDir/libs/iodine-core.jar"))
    implementation(files("$projectDir/libs/iodine-desktop.jar"))
    implementation(files("$projectDir/libs/iodine-desktop-aurora.jar"))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            *freeCompilerArgs.toTypedArray(),
            // "-Xallow-jvm-ir-dependencies",
            "-Xskip-prerelease-check"
        )
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