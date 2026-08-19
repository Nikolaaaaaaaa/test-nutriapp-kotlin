package rs.nutriapp.core.di

import rs.nutriapp.core.data.MockData
import rs.nutriapp.core.data.NutriRepository

/**
 * Rucni DI kontejner — bez Hilt/Koin/Dagger biblioteke.
 *
 * Za aplikaciju ove velicine (jedan repozitorijum, bez modula/feature-flagova) puna DI
 * biblioteka je vise ceremonije nego koristi. `AppContainer` se pravi jednom u `App.kt`
 * posle ucitavanja mock podataka, i prosledjuje kroz `CompositionLocal` (vidi `LocalAppContainer`).
 */
class AppContainer(data: MockData) {
    val repository = NutriRepository(data)
}
