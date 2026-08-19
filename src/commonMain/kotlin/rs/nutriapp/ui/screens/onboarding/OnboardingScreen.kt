package rs.nutriapp.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.ActivityLevel
import rs.nutriapp.core.model.Gender
import rs.nutriapp.core.model.PrimaryGoal
import rs.nutriapp.ui.components.FilterChipRow

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val viewModel = nutriViewModel { OnboardingViewModel(it.repository) }

    Scaffold { padding ->
        Column(Modifier.padding(padding).padding(20.dp)) {
            LinearProgressIndicator(
                progress = { (viewModel.step.index + 1) / OnboardingStep.all.size.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 10.dp))
            Text(
                "Korak ${viewModel.step.index + 1} od ${OnboardingStep.all.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(viewModel.step.title, style = MaterialTheme.typography.headlineSmall)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 16.dp))

            AnimatedContent(
                targetState = viewModel.step,
                transitionSpec = {
                    val forward = targetState.index >= initialState.index
                    (slideInHorizontally(spring(dampingRatio = 0.85f)) { w -> if (forward) w else -w }) togetherWith
                        (slideOutHorizontally(spring(dampingRatio = 0.85f)) { w -> if (forward) -w else w })
                },
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                label = "onboardingStep",
            ) { step ->
                when (step) {
                    OnboardingStep.Welcome -> WelcomeStep(viewModel)
                    OnboardingStep.BodyMetrics -> BodyMetricsStep(viewModel)
                    OnboardingStep.ActivityGoal -> ActivityGoalStep(viewModel)
                    OnboardingStep.Allergies -> AllergiesStep(viewModel)
                    OnboardingStep.Preferences -> PreferencesStep(viewModel)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = viewModel::back, enabled = viewModel.step != OnboardingStep.Welcome) {
                    Text("Nazad")
                }
                Button(
                    onClick = {
                        if (viewModel.step == OnboardingStep.Preferences) {
                            viewModel.finish()
                            onFinished()
                        } else {
                            viewModel.next()
                        }
                    },
                    enabled = viewModel.canAdvance,
                ) {
                    Text(if (viewModel.step == OnboardingStep.Preferences) "Završi" else "Dalje")
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(vm: OnboardingViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(96.dp),
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    Icons.Outlined.RestaurantMenu,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Text(
            "Planiraj obroke, prati unos i drži se svojih golova — sve na jednom mestu.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 4.dp))
        OutlinedTextField(
            value = vm.displayName,
            onValueChange = { vm.displayName = it },
            label = { Text("Ime i prezime") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = MaterialTheme.shapes.large,
        )
        Text(
            "Koristi se samo za pozdrav na početnom ekranu.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BodyMetricsStep(vm: OnboardingViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Gender.entries.forEach { g ->
                FilterChip(selected = vm.gender == g, onClick = { vm.gender = g }, label = { Text(g.label) })
            }
        }
        OutlinedTextField(
            value = vm.age,
            onValueChange = { vm.age = it },
            label = { Text("Godine") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = vm.heightCm,
            onValueChange = { vm.heightCm = it },
            label = { Text("Visina (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = vm.weightKg,
            onValueChange = { vm.weightKg = it },
            label = { Text("Težina (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
    }
}

@Composable
private fun ActivityGoalStep(vm: OnboardingViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Nivo aktivnosti", style = MaterialTheme.typography.titleSmall)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ActivityLevel.entries.forEach { level ->
                // `description` stoji uz enum konstantu u modelu — nema odvojene mape.
                SelectableRow(
                    selected = vm.activityLevel == level,
                    title = level.label,
                    subtitle = level.description,
                    onClick = { vm.activityLevel = level },
                )
            }
        }
        Text("Primarni cilj", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PrimaryGoal.entries.forEach { goal ->
                FilterChip(
                    selected = vm.primaryGoal == goal,
                    onClick = { vm.primaryGoal = goal },
                    label = { Text(goal.label) },
                )
            }
        }
    }
}

/** Red sa naslovom, opisom i radio dugmetom — koristi se za izbor nivoa aktivnosti. */
@Composable
private fun SelectableRow(
    selected: Boolean,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RadioButton(selected = selected, onClick = onClick)
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AllergiesStep(vm: OnboardingViewModel) {
    Column {
        Text("Izaberi ako imaš alergije", style = MaterialTheme.typography.bodyLarge)
        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
        FilterChipRow(items = vm.allergyOptions, selected = vm.allergies, onToggle = vm::toggleAllergy, label = { it })
    }
}

@Composable
private fun PreferencesStep(vm: OnboardingViewModel) {
    Column {
        Text("Ima li nešto što voliš da istakneš?", style = MaterialTheme.typography.bodyLarge)
        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
        FilterChipRow(items = vm.preferenceOptions, selected = vm.preferences, onToggle = vm::togglePreference, label = { it })
    }
}
