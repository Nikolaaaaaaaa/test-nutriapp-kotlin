package rs.nutriapp

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window
import rs.nutriapp.ui.nav.Route
import rs.nutriapp.ui.nav.routeFromPath

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // `#/plan`, `#/recept/r-03` ... -> pocetni ekran. Ako hash ne odgovara nijednoj ruti,
    // ide se na onboarding kao i pri prvom otvaranju aplikacije.
    val startRoute: Route = routeFromPath(window.location.hash) ?: Route.Onboarding

    ComposeViewport(viewportContainerId = "composeApp") {
        App(startRoute = startRoute)
    }
}
