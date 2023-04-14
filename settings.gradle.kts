pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        jcenter()
    }
    plugins {
        kotlin("multiplatform").version("1.8.10")
        id("org.jetbrains.compose").version("1.3.1")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

enableFeaturePreview("VERSION_CATALOGS")

include(
    ":libraries:customisations",
    ":libraries:interop-ribs",
    ":libraries:interop-rx2",
    ":libraries:interop-rx3",
    ":libraries:testing-junit4",
    ":libraries:testing-junit5",
    ":libraries:testing-ui",
    ":libraries:testing-ui-activity",
    ":libraries:testing-unit-common",
    ":samples:appyx-navigation",
    ":samples:common",
    ":samples:navigation-compose",
    ":appyx-interactions:android", ":appyx-interactions:desktop", ":appyx-interactions:common",
    ":appyx-navigation"
)

includeBuild("plugins")
