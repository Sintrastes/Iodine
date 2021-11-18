pluginManagement {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    resolutionStrategy {
        eachPlugin {
            if(requested.id.namespace == "io.arrow-kt") {
                useModule("io.arrow-kt:arrow-proofs-gradle-plugin:${requested.version}")
            }
        }
    }
}