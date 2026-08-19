package rs.nutriapp.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Pet glavnih ruta u donjem tab baru + centralno FAB dugme za brz unos.
 * Ostali ekrani su u "Jos" meniju (vidi `MoreSheet.kt`) — ista sema kao README.
 */
private data class TabSpec(
    val route: Route,
    val label: String,
    val filled: ImageVector,
    val outlined: ImageVector,
)

// Dva taba levo, dva desno — prazan slot ostaje tacno u sredini, gde stoji FAB.
// (Sa tri levo i jednim desno FAB bi se centrirao preko treceg taba i zaklonio ga.)
private val leadingTabs = listOf(
    TabSpec(Route.Home, "Početna", Icons.Filled.Home, Icons.Outlined.Home),
    TabSpec(Route.Discovery, "Otkrij", Icons.Filled.Explore, Icons.Outlined.Explore),
)

private val trailingTabs = listOf(
    TabSpec(Route.Plan, "Plan", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    TabSpec(Route.Grocery, "Lista", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
)

@Composable
fun BottomTabBar(
    currentRoute: Route,
    onNavigate: (Route) -> Unit,
    onQuickAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            leadingTabs.forEach { tab -> TabItem(tab, currentRoute, onNavigate) }
            // Prazan slot za FAB u sredini
            NavigationBarItem(
                selected = false,
                onClick = {},
                enabled = false,
                icon = {},
                label = null,
            )
            trailingTabs.forEach { tab -> TabItem(tab, currentRoute, onNavigate) }
        }
        FloatingActionButton(
            onClick = onQuickAdd,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(56.dp),
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Brz unos obroka")
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TabItem(
    tab: TabSpec,
    currentRoute: Route,
    onNavigate: (Route) -> Unit,
) {
    val selected = currentRoute == tab.route
    NavigationBarItem(
        selected = selected,
        onClick = { onNavigate(tab.route) },
        icon = { Icon(if (selected) tab.filled else tab.outlined, contentDescription = tab.label) },
        label = { Text(tab.label, style = MaterialTheme.typography.labelSmall) },
    )
}
