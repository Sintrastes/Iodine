
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url="https://dl.bintray.com/kotlin/dokka")
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

rootProject.name = "iodine"
include("base")
include("comonadic-ui")
include("core")
include("desktop")
include("aurora")
include("android")