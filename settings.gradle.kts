
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url="https://dl.bintray.com/kotlin/dokka")
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

rootProject.name = "iodine"
include("core")
include("desktop")
include("android")