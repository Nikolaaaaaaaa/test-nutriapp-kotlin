pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        // Google Maven objavljuje androidx.lifecycle/androidx.savedstate artefakte i za
        // wasmJs/js targete — bez ovog repoa Gradle ne moze da razresi tranzitivne
        // zavisnosti navigation-compose i Compose UI biblioteka na Kotlin/Wasm.
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "nutriapp-kotlin"
