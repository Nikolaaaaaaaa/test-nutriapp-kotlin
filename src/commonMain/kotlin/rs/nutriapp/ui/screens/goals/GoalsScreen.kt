package rs.nutriapp.ui.screens.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.Challenge
import rs.nutriapp.core.model.ChallengeStatus
import rs.nutriapp.core.model.progressFraction
import rs.nutriapp.ui.components.NutrientBar
import rs.nutriapp.ui.nav.NutriTopBar
import rs.nutriapp.ui.theme.StatusColors

@Composable
fun GoalsScreen(onOpenNotifications: () -> Unit, onOpenMore: () -> Unit) {
    val viewModel = nutriViewModel { GoalsViewModel(it.repository) }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { NutriTopBar(title = "Golovi i izazovi", onNotifications = onOpenNotifications, onMore = onOpenMore) },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = 112.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (state.violated.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Van okvira danas", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onErrorContainer)
                            state.violated.forEach {
                                Text("• ${it.restriction.label}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                }
            }
            item { Text("Restrikcije", style = MaterialTheme.typography.titleMedium) }
            items(state.restrictions, key = { it.id.raw }) { restriction ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(restriction.label, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "${restriction.scope.label} · ${restriction.operator.symbol} ${restriction.value} ${restriction.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(checked = restriction.active, onCheckedChange = { viewModel.toggleRestriction(restriction.id) })
                }
            }

            if (state.challengeBoard.active.isNotEmpty()) {
                item { Text("Aktivni izazovi", style = MaterialTheme.typography.titleMedium) }
                items(state.challengeBoard.active, key = { it.id.raw }) { challenge ->
                    ChallengeCard(challenge)
                }
            }
            if (state.challengeBoard.suggested.isNotEmpty()) {
                item { Text("Predlozi", style = MaterialTheme.typography.titleMedium) }
                items(state.challengeBoard.suggested, key = { it.id.raw }) { challenge ->
                    SuggestedChallengeCard(
                        challenge = challenge,
                        onAccept = { viewModel.respond(challenge.id, true) },
                        onDecline = { viewModel.respond(challenge.id, false) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeCard(challenge: Challenge) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(challenge.title, style = MaterialTheme.typography.titleSmall)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
            NutrientBar(
                label = "Napredak",
                valueLabel = "${challenge.progress}/${challenge.target}",
                fraction = challenge.progressFraction,
                color = StatusColors.good,
            )
        }
    }
}

@Composable
private fun SuggestedChallengeCard(challenge: Challenge, onAccept: () -> Unit, onDecline: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(challenge.title, style = MaterialTheme.typography.titleSmall)
            Text(
                challenge.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAccept) { Text("Prihvati") }
                OutlinedButton(onClick = onDecline) { Text("Odbij") }
            }
        }
    }
}
