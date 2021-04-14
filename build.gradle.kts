
val kotlin_version = "1.4.30"
buildscript {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha14")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
    }
}

repositories {
    maven(url="https://dl.bintray.com/kotlin/dokka")
}

//tasks.dokkaHtmlMultiModule.configure {
//    outputDirectory.set(buildDir.resolve("dokkaCustomMultiModuleOutput"))
//}