
val kotlin_version = "1.5.21"
buildscript {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    }
}

repositories {
    jcenter() // or (not currently working) maven(url="https://dl.bintray.com/kotlin/dokka")
}

plugins {
    id("org.jetbrains.dokka") version "1.4.30"
}

tasks.dokkaHtmlMultiModule.configure {
   outputDirectory.set(buildDir.resolve("dokkaCustomMultiModuleOutput"))
}