package rs.nutriapp.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.Friend
import rs.nutriapp.core.model.bmi
import rs.nutriapp.core.model.bmiCategory
import rs.nutriapp.ui.nav.NutriTopBar
import rs.nutriapp.ui.theme.ThemeMode

@Composable
fun ProfileScreen(onOpenMore: () -> Unit) {
    val viewModel = nutriViewModel { ProfileViewModel(it.repository) }
    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

    Scaffold(
        topBar = { NutriTopBar(title = "Profil", onMore = onOpenMore) },
    ) { padding ->
        if (profile == null) return@Scaffold

        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(64.dp)) {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text(profile.initials, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                    Column {
                        Text(profile.displayName, style = MaterialTheme.typography.titleMedium)
                        Text("@${profile.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${profile.streakDays} dana niz", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                SectionCard(title = "Telo i ciljevi") {
                    StatLine("Težina", "${profile.weightKg} kg (cilj ${profile.targetWeightKg} kg)")
                    StatLine("Visina", "${profile.heightCm} cm")
                    StatLine("BMI", "${(profile.bmi * 10).toInt() / 10.0} — ${profile.bmiCategory}")
                    StatLine("Aktivnost", profile.activityLevel.label)
                }
            }

            item {
                SectionCard(title = "Alergije i preferencije") {
                    Text(profile.allergies.joinToString().ifBlank { "Nema unetih alergija" }, style = MaterialTheme.typography.bodyMedium)
                    Text(profile.preferences.joinToString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            item {
                SectionCard(title = "Podešavanja — tema") {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setThemeMode(mode) },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(selected = state.themeMode == mode, onClick = { viewModel.setThemeMode(mode) })
                                Text(mode.label, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }

            if (state.incomingRequests.isNotEmpty()) {
                item { Text("Zahtevi za prijateljstvo", style = MaterialTheme.typography.titleSmall) }
                items(state.incomingRequests, key = { it.id.raw }) { friend -> FriendRow(friend) }
            }

            item { Text("Prijatelji", style = MaterialTheme.typography.titleSmall) }
            items(state.friends, key = { it.id.raw }) { friend -> FriendRow(friend) }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
            content()
        }
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FriendRow(friend: Friend) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(40.dp)) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(friend.initials, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
        Column {
            Text(friend.displayName, style = MaterialTheme.typography.bodyLarge)
            Text("${friend.sharedRecipes} deljenih recepata", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
