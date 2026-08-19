package rs.nutriapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import rs.nutriapp.core.data.LoadState
import rs.nutriapp.core.data.MockData
import rs.nutriapp.core.data.loadMockData
import rs.nutriapp.core.di.AppContainer
import rs.nutriapp.core.di.LocalAppContainer
import rs.nutriapp.ui.components.EmptyState
import rs.nutriapp.ui.components.NotificationsSheet
import rs.nutriapp.ui.nav.BottomTabBar
import rs.nutriapp.ui.nav.MoreSheet
import rs.nutriapp.ui.nav.NutriNavHost
import rs.nutriapp.ui.nav.PhoneFrame
import rs.nutriapp.ui.nav.Route
import rs.nutriapp.ui.screens.search.SearchOverlay
import rs.nutriapp.ui.theme.NutriTheme

/**
 * Koren aplikacije: ucitava mock podatke jednom (`LoadState` sealed — vidi `LoadState.kt`),
 * pravi `AppContainer` i deli ga kroz `CompositionLocalProvider`, bira temu, i sklapa
 * navigaciju + donji tab bar + "Jos" sheet + pretragu + notifikacije oko `NutriNavHost`-a.
 */
@Composable
fun App(startRoute: Route = Route.Onboarding) {
    var loadState by remember { mutableStateOf<LoadState<MockData>>(LoadState.Loading) }

    LaunchedEffect(Unit) {
        loadState = try {
            LoadState.Ready(loadMockData())
        } catch (e: Throwable) {
            LoadState.Failed(e.message ?: "Nepoznata greška pri učitavanju podataka.")
        }
    }

    when (val state = loadState) {
        is LoadState.Loading -> LoadingScreen()
        is LoadState.Failed -> ErrorScreen(state.message)
        is LoadState.Ready -> ReadyApp(state.value, startRoute)
    }
}

@Composable
private fun ReadyApp(data: MockData, startRoute: Route) {
    val container = remember(data) { AppContainer(data) }

    CompositionLocalProvider(LocalAppContainer provides container) {
        val themeMode by container.repository.themeMode.collectAsState()

        NutriTheme(themeMode) {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination

            var searchOpen by remember { mutableStateOf(false) }
            var moreOpen by remember { mutableStateOf(false) }
            var notificationsOpen by remember { mutableStateOf(false) }

            val notifications by container.repository.notifications.collectAsState()

            // Donji tab bar/FAB se prikazuju samo na "glavnim" ekranima — Onboarding i
            // Login nemaju chrome, ostali ekrani iz "Jos" menija ga imaju ali bez isticanja taba.
            val showChrome = currentDestination != null &&
                currentDestination.hasRoute<Route.Onboarding>().not() &&
                currentDestination.hasRoute<Route.Login>().not()

            PhoneFrame {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        // Ctrl+K otvara globalnu pretragu i pokusava da preotme prečicu od
                        // browsera (koji je inace vezuje za adresnu traku); ako browser
                        // ipak zadrzi svoju precicu, dugme pretrage u top bar-u je uvek tu.
                        .onPreviewKeyEvent { event ->
                            if (event.type == KeyEventType.KeyDown && event.isCtrlPressed && event.key == Key.K) {
                                searchOpen = true
                                true
                            } else {
                                false
                            }
                        },
                ) {
                    Box(Modifier.fillMaxSize()) {
                        NutriNavHost(
                            navController = navController,
                            startRoute = startRoute,
                            onOpenSearch = { searchOpen = true },
                            onOpenNotifications = { notificationsOpen = true },
                            onOpenMore = { moreOpen = true },
                        )
                    }

                    if (showChrome) {
                        BottomTabBar(
                            currentRoute = currentTopLevelRoute(currentDestination),
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo(Route.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onQuickAdd = { navController.navigate(Route.Logging) },
                            modifier = Modifier.align(Alignment.BottomCenter),
                        )
                    }

                    MoreSheet(
                        visible = moreOpen,
                        onDismiss = { moreOpen = false },
                        onNavigate = { route -> navController.navigate(route) },
                        unreadNotifications = notifications.count { !it.read },
                        onNotifications = { notificationsOpen = true },
                    )

                    NotificationsSheet(
                        visible = notificationsOpen,
                        notifications = notifications,
                        onDismiss = { notificationsOpen = false },
                        onMarkRead = container.repository::markNotificationRead,
                        onMarkAllRead = container.repository::markAllNotificationsRead,
                    )

                    SearchOverlay(
                        visible = searchOpen,
                        onDismiss = { searchOpen = false },
                        onNavigate = { route -> navController.navigate(route) },
                    )
                }
            }
        }
    }
}

/**
 * Bottom tab bar treba da zna koji od PET glavnih ruta je "aktivna". hasRoute<T>() proverava
 * tip destinacije direktno -- nema pogadjanja iz stringa kao sto bi bilo sa string-baziranim rutama.
 * Kad je otvoren neki od preostalih 12 ekrana (iz "Jos" menija), nijedan tab se ne ističe.
 */
private fun currentTopLevelRoute(destination: NavDestination?): Route = when {
    destination?.hasRoute<Route.Discovery>() == true -> Route.Discovery
    destination?.hasRoute<Route.Plan>() == true -> Route.Plan
    destination?.hasRoute<Route.Logging>() == true -> Route.Logging
    destination?.hasRoute<Route.Grocery>() == true -> Route.Grocery
    else -> Route.Home
}

@Composable
private fun LoadingScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ErrorScreen(message: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(icon = Icons.Outlined.ErrorOutline, title = "Greška pri učitavanju", subtitle = message)
        }
    }
}
