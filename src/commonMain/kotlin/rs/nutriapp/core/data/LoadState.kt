package rs.nutriapp.core.data

/**
 * Stanje ucitavanja pocetnih podataka.
 *
 * Sealed interface + iscrpan `when` na mestu gde se `App()` grana (vidi `App.kt`) —
 * kompajler ne dozvoljava da se zaboravi `Failed` slucaj. U React/Vue verziji je ovo
 * obicno `isLoading: boolean` + `error: string | null`, gde nista ne spreci da oba
 * budu tacna u isto vreme.
 */
sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data class Ready<T>(val value: T) : LoadState<T>
    data class Failed(val message: String) : LoadState<Nothing>
}

inline fun <T, R> LoadState<T>.map(transform: (T) -> R): LoadState<R> = when (this) {
    is LoadState.Loading -> LoadState.Loading
    is LoadState.Ready -> LoadState.Ready(transform(value))
    is LoadState.Failed -> this
}
