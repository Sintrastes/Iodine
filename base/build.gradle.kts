import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    // dependencies {
    //     classpath("io.arrow-kt:arrow-proofs-gradle-plugin:1.5.31-SNAPSHOT")
    // }
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
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val arrow_version = "1.0.1"

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.0")
}

dependencies {
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation("io.kindedj:kindedj:1.1.0")
    implementation("io.arrow-kt:arrow-core:0.13.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf(
            "-Xcontext-receivers"
    )
}

tasks.jar {
    archiveFileName.set("iodine-base.jar")
}