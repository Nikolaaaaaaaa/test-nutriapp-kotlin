package rs.nutriapp.ui.nav

import kotlinx.serialization.Serializable
import rs.nutriapp.core.model.RecipeId

/**
 * Sve rute u aplikaciji, tipizirane.
 *
 * `navigation-compose` (KMP) prihvata `@Serializable` objekte direktno kao destinacije —
 * `navController.navigate(Route.RecipeDetail(id))` prosledjuje pravi `RecipeId`, ne string
 * koji se posle parsira regexom iz URL-a. Ako se doda novo polje ruti, poziv na svakom
 * mestu koje ne prosledi vrednost prestaje da se kompajlira.
 *
 * Rute sa argumentom cuvaju `String` u primarnom konstruktoru, a sekundarni prima
 * `RecipeId`: biblioteka zna da spakuje `String` u URL bez dodatnog `NavType`, dok
 * pozivalac i dalje barata tipom. To je konkretna granica gde value klase traze prevod —
 * posteno je zabeleziti je, jer se ista granica pojavljuje i u React/Vue ruterima.
 *
 * Iscrpan `when` nad `Route` (u `NutriNavHost.kt`) garantuje da nijedan ekran nije
 * zaboravljen kad se ruta doda — ovo React Router / Vue Router string-bazirane rute
 * ne mogu da provere u kompajl vremenu.
 */
sealed interface Route {
    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Discovery : Route

    @Serializable
    data class RecipeDetail(val rawId: String) : Route {
        constructor(id: RecipeId) : this(id.raw)

        val id: RecipeId get() = RecipeId(rawId)
    }

    @Serializable
    data object RecipeNew : Route

    @Serializable
    data class RecipeEdit(val rawId: String) : Route {
        constructor(id: RecipeId) : this(id.raw)

        val id: RecipeId get() = RecipeId(rawId)
    }

    @Serializable
    data object Plan : Route

    @Serializable
    data object Logging : Route

    @Serializable
    data object Goals : Route

    @Serializable
    data object Stats : Route

    @Serializable
    data object Grocery : Route

    @Serializable
    data object Substitutions : Route

    @Serializable
    data object Library : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data object Calculator : Route

    @Serializable
    data object NewProduct : Route
}

/** Donji tab bar pokazuje samo pet glavnih ruta; ostalo je u "Jos" meniju. */
val Route.isTopLevel: Boolean
    get() = this == Route.Home || this == Route.Discovery || this == Route.Plan ||
        this == Route.Logging || this == Route.Grocery

/**
 * Putanje iz README tabele ekrana -> ruta.
 *
 * Tipizirane rute u `navigation-compose` interno koriste puno ime klase kao obrazac
 * (`rs.nutriapp.ui.nav.Route.Home`), sto u adresnoj traci izgleda ruzno. Zato aplikacija
 * cita hash iz URL-a pri startu i sa njega bira pocetni ekran: `#/plan`, `#/grocery`,
 * `#/recept/r-03`. Tako `/plan` iz README-a i dalje otvara plan, a deljenje linka radi.
 */
fun routeFromPath(path: String): Route? {
    val clean = path.removePrefix("#").removePrefix("/").removeSuffix("/")
    val parts = clean.split("/")
    return when (parts.firstOrNull().orEmpty()) {
        "" -> Route.Home
        "onboarding" -> Route.Onboarding
        "login" -> Route.Login
        "discovery" -> Route.Discovery
        "plan" -> Route.Plan
        "logovanje" -> Route.Logging
        "golovi" -> Route.Goals
        "statistika" -> Route.Stats
        "grocery" -> Route.Grocery
        "supstitucije" -> Route.Substitutions
        "biblioteka" -> Route.Library
        "profil" -> Route.Profile
        "kalkulator" -> Route.Calculator
        "namirnica" -> if (parts.getOrNull(1) == "nova") Route.NewProduct else null
        "recept" -> when {
            parts.getOrNull(1) == "novi" -> Route.RecipeNew
            parts.size >= 3 && parts[2] == "izmena" -> Route.RecipeEdit(RecipeId(parts[1]))
            parts.size >= 2 && parts[1].isNotBlank() -> Route.RecipeDetail(RecipeId(parts[1]))
            else -> null
        }
        else -> null
    }
}
