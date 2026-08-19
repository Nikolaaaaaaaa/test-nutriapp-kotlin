package rs.nutriapp.ui.nav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private data class MoreItem(val route: Route, val label: String, val icon: ImageVector, val description: String)

private val moreItems = listOf(
    MoreItem(Route.Goals, "Golovi i izazovi", Icons.Filled.Flag, "Restrikcije, dnevni golovi, izazovi"),
    MoreItem(Route.Stats, "Statistika", Icons.Filled.QueryStats, "30 dana napretka i makro raspodela"),
    MoreItem(Route.Substitutions, "Supstitucije", Icons.Filled.SwapHoriz, "Katalog zamena sastojaka"),
    MoreItem(Route.Library, "Biblioteka", Icons.Outlined.Bookmarks, "Sačuvani recepti, proizvodi, zamene"),
    MoreItem(Route.Calculator, "Kalkulator kalorija", Icons.Outlined.Calculate, "Tri formule za dnevne potrebe"),
    MoreItem(Route.Profile, "Profil", Icons.Filled.Person, "Nalog, prijatelji, podešavanja teme"),
)

/**
 * Sheet sa desne strane — README ekvivalent Vue "sheet sa desne strane" za sve ekrane
 * koji nisu u donjem tab baru. Custom `AnimatedVisibility` overlay umesto gotovog drawer-a,
 * da bi zaista klizio sa desna (M3 `ModalNavigationDrawer` je levo-usidren po defaultu).
 */
@Composable
fun MoreSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onNavigate: (Route) -> Unit,
    unreadNotifications: Int,
    onNotifications: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss,
                ),
            contentAlignment = Alignment.CenterEnd,
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(
                    animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f),
                    initialOffsetX = { it },
                ),
                exit = slideOutHorizontally(
                    animationSpec = spring(dampingRatio = 0.9f, stiffness = 400f),
                    targetOffsetX = { it },
                ),
            ) {
                Surface(
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .fillMaxHeight()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {},
                        ),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Još", style = MaterialTheme.typography.headlineSmall)
                            NotificationBell(unreadNotifications) {
                                onDismiss()
                                onNotifications()
                            }
                        }
                        HorizontalDivider()
                        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                            items(moreItems) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismiss()
                                            onNavigate(item.route)
                                        }
                                        .padding(horizontal = 20.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Column {
                                        Text(item.label, style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            item.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationBell(count: Int, onClick: () -> Unit) {
    Box(modifier = Modifier.clickable(onClick = onClick)) {
        if (count > 0) {
            BadgedBox(badge = { Badge { Text("$count") } }) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifikacije")
            }
        } else {
            Icon(Icons.Filled.Notifications, contentDescription = "Notifikacije")
        }
    }
}
