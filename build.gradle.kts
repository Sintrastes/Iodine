
buildscript {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.5.30")
    }
}

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

plugins {
    id("org.jetbrains.dokka") version "1.5.30"
}

tasks.dokkaHtmlMultiModule.configure {
   outputDirectory.set(buildDir.resolve("dokkaCustomMultiModuleOutput"))
}