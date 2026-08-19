package rs.nutriapp.ui.screens.stats

import androidx.lifecycle.ViewModel
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.Stats

/**
 * Statistika je istorijski snimak (`repository.stats` je obican `val`, ne `Flow`) —
 * nema nista sto bi je promenilo tokom sesije, pa nema potrebe za `combine`/`StateFlow`
 * ovde. I dalje ide kroz ViewModel radi doslednosti sa ostalim ekranima.
 */
class StatsViewModel(repository: NutriRepository) : ViewModel() {
    val stats: Stats = repository.stats
}
