
val kotlin_version = "1.4.31"
buildscript {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha14")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
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