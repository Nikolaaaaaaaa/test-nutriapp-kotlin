import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvmToolchain(21)

    // Sav kod stoji u commonMain; wasmJs je samo ulazna tacka.
    // Zbog toga se Android/desktop target kasnije dodaje bez premestanja fajlova.
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("nutriapp")
        browser {
            commonWebpackConfig {
                outputFileName = "nutriapp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    // React: 5173, Vue: 5174, Angular: 4200, Kotlin: 8080
                    port = 8080
                    open = false
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.backhandler)
            implementation(libs.compose.animation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.components.resources)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.navigation.compose)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
        }

        wasmJsMain.dependencies {
            // Samo za citanje `window.location.hash` pri startu — biranje pocetnog ekrana.
            implementation(libs.kotlinx.browser)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "rs.nutriapp.resources"
    generateResClass = always
}
