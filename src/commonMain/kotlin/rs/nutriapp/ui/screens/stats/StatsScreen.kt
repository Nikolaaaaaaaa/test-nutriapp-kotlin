package rs.nutriapp.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.DeviationStatus
import rs.nutriapp.core.model.lastDays
import rs.nutriapp.ui.components.charts.BarChart
import rs.nutriapp.ui.components.charts.DonutChart
import rs.nutriapp.ui.components.charts.DonutSlice
import rs.nutriapp.ui.nav.NutriTopBar
import rs.nutriapp.ui.theme.StatusColors

@Composable
fun StatsScreen(onOpenMore: () -> Unit) {
    val viewModel = nutriViewModel { StatsViewModel(it.repository) }
    val stats = viewModel.stats

    Scaffold(
        topBar = { NutriTopBar(title = "Statistika", onMore = onOpenMore) },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Text(stats.rangeLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) }

            item {
                SectionCard(title = "Kalorije po danu") {
                    val recent = stats.daily.lastDays(7)
                    BarChart(
                        values = recent.map { it.calories.value.toFloat() },
                        labels = recent.map { it.dayShort },
                        goalLine = stats.summary.goalCalories.value.toFloat(),
                    )
                }
            }

            item {
                SectionCard(title = "Raspodela makronutrijenata") {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        DonutChart(
                            slices = stats.macroSplit.map {
                                DonutSlice(it.value.toFloat() / 100f, androidx.compose.ui.graphics.Color(parseHex(it.color)))
                            },
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            stats.macroSplit.forEach { slice ->
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(androidx.compose.ui.graphics.Color(parseHex(slice.color)), androidx.compose.foundation.shape.CircleShape),
                                    )
                                    Text("${slice.name} ${slice.value.toInt()}%", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }

            item {
                SectionCard(title = "Odstupanja od golova") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        stats.deviations.forEach { dev ->
                            val color = when (dev.status) {
                                DeviationStatus.U_OKVIRU -> StatusColors.good
                                DeviationStatus.GRANICNO -> StatusColors.warn
                                DeviationStatus.VAN_OKVIRA -> StatusColors.bad
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(dev.nutrient, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${dev.actual.toInt()} / ${dev.goal.toInt()} (${if (dev.deviationPct >= 0) "+" else ""}${dev.deviationPct.toInt()}%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = color,
                                )
                            }
                        }
                    }
                }
            }

            item {
                SectionCard(title = "Najčešće logovani recepti") {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        stats.topLoggedRecipes.take(5).forEach { top ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(top.name, style = MaterialTheme.typography.bodyMedium)
                                Text("${top.count}×", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                SectionCard(title = "Pregled") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        StatRow("Prosečne kalorije", "${stats.summary.avgCalories.rounded} kcal")
                        StatRow("Dana u okviru cilja", "${stats.summary.daysOnTarget}/${stats.summary.daysTracked}")
                        StatRow("Promena težine", "${stats.summary.weightChange} kg")
                        StatRow("Poštovanje plana", "${stats.summary.adherencePct.toInt()}%")
                        StatRow("Najduži niz", "${stats.summary.longestStreak} dana")
                    }
                }
            }
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
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 10.dp))
            content()
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun parseHex(hex: String): Long {
    val clean = hex.removePrefix("#")
    return ("FF$clean").toLong(16)
}
