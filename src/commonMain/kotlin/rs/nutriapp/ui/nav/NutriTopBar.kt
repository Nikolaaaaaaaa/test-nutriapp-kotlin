package rs.nutriapp.ui.nav

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Sticky header koji na skrol menja elevaciju/pozadinu — vezan direktno za `LazyListState`
 * preko `derivedStateOf`, bez posebnog scroll-listener callback-a.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriTopBar(
    title: String,
    modifier: Modifier = Modifier,
    listState: LazyListState? = null,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    onSearch: (() -> Unit)? = null,
    onNotifications: (() -> Unit)? = null,
    onMore: (() -> Unit)? = null,
    unreadNotifications: Int = 0,
) {
    val isScrolled by remember(listState) {
        derivedStateOf {
            listState != null && (listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 8)
        }
    }
    val containerColor by animateColorAsState(
        targetValue = if (isScrolled) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.background,
        label = "topBarContainer",
    )

    TopAppBar(
        modifier = modifier,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = { onBack?.invoke() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Nazad")
                }
            }
        },
        actions = {
            if (onSearch != null) {
                IconButton(onClick = onSearch) {
                    Icon(Icons.Filled.Search, contentDescription = "Pretraga")
                }
            }
            if (onNotifications != null) {
                IconButton(onClick = onNotifications) {
                    BadgedIcon(Icons.Filled.Notifications, "Notifikacije", unreadNotifications)
                }
            }
            if (onMore != null) {
                IconButton(onClick = onMore) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Još")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
    )
}

@Composable
private fun BadgedIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, count: Int) {
    if (count > 0) {
        androidx.compose.material3.BadgedBox(
            badge = {
                androidx.compose.material3.Badge { Text(if (count > 9) "9+" else "$count") }
            },
        ) {
            Icon(icon, contentDescription = description)
        }
    } else {
        Icon(icon, contentDescription = description)
    }
}
