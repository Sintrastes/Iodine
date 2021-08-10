// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
<<<<<<< HEAD
    val compose_version by extra("1.0.0")
=======
    val compose_version by extra("1.0.0-beta06")
>>>>>>> 95c4949ba8bf00c034752451b76faa01050fe7b2
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
<<<<<<< HEAD
        classpath("com.android.tools.build:gradle:7.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
=======
        classpath("com.android.tools.build:gradle:7.0.0-alpha15")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
>>>>>>> 95c4949ba8bf00c034752451b76faa01050fe7b2

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}