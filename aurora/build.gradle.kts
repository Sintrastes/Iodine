import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev748"
    id("org.jetbrains.dokka")
}

group = "com.bedelln"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.32")
}

val arrow_version = "1.0.1"

dependencies {
    implementation(project(":base"))
    implementation(project(":core"))
    implementation(project(":desktop"))
    implementation(compose.desktop.currentOs)
    implementation("org.pushing-pixels:aurora-theming:1.0.0-beta5")
    implementation("org.pushing-pixels:aurora-component:1.0.0-beta5")
    implementation("org.pushing-pixels:aurora-window:1.0.0-beta5")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrow_version")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf(
            "-Xcontext-receivers"
    )
}

tasks.jar {
    archiveFileName.set("iodine-desktop-aurora.jar")
}
