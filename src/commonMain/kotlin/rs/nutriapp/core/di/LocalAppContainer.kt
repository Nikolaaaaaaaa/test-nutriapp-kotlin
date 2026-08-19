package rs.nutriapp.core.di

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer nije postavljen — NutriTheme/App mora da obmota sadrzaj sa CompositionLocalProvider")
}
