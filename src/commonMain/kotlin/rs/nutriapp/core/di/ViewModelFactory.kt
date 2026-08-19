package rs.nutriapp.core.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Tanak helper preko `viewModel { }` iz `androidx.lifecycle` (KMP) — pravi ViewModel
 * vezan za trenutni `NavBackStackEntry`, prezivljava rekompoziciju, unisten kad se ekran
 * skloni sa steka. Nema DI biblioteke: `AppContainer` dolazi iz `LocalAppContainer`, pa
 * svaki ekran pise samo `nutriViewModel { HomeViewModel(it.repository) }`.
 */
@Composable
inline fun <reified VM : ViewModel> nutriViewModel(
    key: String? = null,
    crossinline create: (AppContainer) -> VM,
): VM {
    val container = LocalAppContainer.current
    return viewModel(key = key) { create(container) }
}
