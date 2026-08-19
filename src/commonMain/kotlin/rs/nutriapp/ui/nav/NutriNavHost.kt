package rs.nutriapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import rs.nutriapp.core.model.RecipeId
import rs.nutriapp.ui.screens.calculator.CalculatorScreen
import rs.nutriapp.ui.screens.discovery.DiscoveryScreen
import rs.nutriapp.ui.screens.goals.GoalsScreen
import rs.nutriapp.ui.screens.grocery.GroceryScreen
import rs.nutriapp.ui.screens.home.HomeScreen
import rs.nutriapp.ui.screens.library.LibraryScreen
import rs.nutriapp.ui.screens.logging.LoggingScreen
import rs.nutriapp.ui.screens.login.LoginScreen
import rs.nutriapp.ui.screens.newproduct.NewProductScreen
import rs.nutriapp.ui.screens.onboarding.OnboardingScreen
import rs.nutriapp.ui.screens.plan.PlanScreen
import rs.nutriapp.ui.screens.profile.ProfileScreen
import rs.nutriapp.ui.screens.recipedetail.RecipeDetailScreen
import rs.nutriapp.ui.screens.recipeform.RecipeFormScreen
import rs.nutriapp.ui.screens.stats.StatsScreen
import rs.nutriapp.ui.screens.substitutions.SubstitutionsScreen

/**
 * Svih 17 ekrana registrovano na tipiziranim rutama iz `Route.kt`. `composable<Route.X>`
 * (KMP navigation-compose) dekodira argumente direktno u tip preko `toRoute<>()` —
 * nema rucnog parsiranja `id` iz stringa niti `?.let { }` bezbednosne mreže za slucaj
 * da parametar nedostaje, kao sto bi bilo sa string-bazranim rutama.
 */
@Composable
fun NutriNavHost(
    navController: NavHostController,
    startRoute: Route,
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenMore: () -> Unit,
) {
    fun openRecipe(id: RecipeId) = navController.navigate(Route.RecipeDetail(id))
    fun back() = navController.popBackStack()

    NavHost(navController = navController, startDestination = startRoute) {
        composable<Route.Onboarding> {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                },
            )
        }
        composable<Route.Login> {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                },
            )
        }
        composable<Route.Home> {
            HomeScreen(
                onOpenSearch = onOpenSearch,
                onOpenNotifications = onOpenNotifications,
                onOpenMore = onOpenMore,
                onOpenRecipe = ::openRecipe,
            )
        }
        composable<Route.Discovery> {
            DiscoveryScreen(
                onOpenRecipe = ::openRecipe,
                onOpenNotifications = onOpenNotifications,
                onOpenMore = onOpenMore,
            )
        }
        composable<Route.RecipeDetail> { backStackEntry ->
            val route: Route.RecipeDetail = backStackEntry.toRoute()
            RecipeDetailScreen(recipeId = route.id, onBack = ::back)
        }
        composable<Route.RecipeNew> {
            RecipeFormScreen(editingId = null, onSaved = { openRecipe(it) }, onBack = ::back)
        }
        composable<Route.RecipeEdit> { backStackEntry ->
            val route: Route.RecipeEdit = backStackEntry.toRoute()
            RecipeFormScreen(editingId = route.id, onSaved = { openRecipe(it) }, onBack = ::back)
        }
        composable<Route.Plan> {
            PlanScreen(onOpenNotifications = onOpenNotifications, onOpenMore = onOpenMore)
        }
        composable<Route.Logging> {
            LoggingScreen(onOpenNotifications = onOpenNotifications, onOpenMore = onOpenMore)
        }
        composable<Route.Goals> {
            GoalsScreen(onOpenNotifications = onOpenNotifications, onOpenMore = onOpenMore)
        }
        composable<Route.Stats> {
            StatsScreen(onOpenMore = onOpenMore)
        }
        composable<Route.Grocery> {
            GroceryScreen(onOpenNotifications = onOpenNotifications, onOpenMore = onOpenMore)
        }
        composable<Route.Substitutions> {
            SubstitutionsScreen(onOpenMore = onOpenMore)
        }
        composable<Route.Library> {
            LibraryScreen(onOpenRecipe = ::openRecipe, onOpenMore = onOpenMore)
        }
        composable<Route.Profile> {
            ProfileScreen(onOpenMore = onOpenMore)
        }
        composable<Route.Calculator> {
            CalculatorScreen(onOpenMore = onOpenMore)
        }
        composable<Route.NewProduct> {
            NewProductScreen(onSaved = { back() }, onBack = ::back)
        }
    }
}
