# Šta je specifično za Kotlin verziju

Pandan sekciji „Šta je specifično za Vue verziju" u glavnom README-u. Aplikacija nije port
React ni Vue koda — pisana je od nule oko onoga u čemu je Kotlin jak, pa su ovde i stvari
koje su **dobro prošle** i one koje su se **pokazale kao problem** na Compose/Wasm cilju.

## 1. Inline value klase — jedinice se ne mogu pomešati

`Kcal`, `Grams`, `Ml`, `Rsd`, `Minutes` i identifikatori (`RecipeId`, `ProductId`, `StoreId`…)
su `@JvmInline value class`. Kompajler ih svede na običan `Double`/`String` — nema alokacije
ni runtime cene — ali odbija da sabere kalorije sa gramima ili da prosledi `ProductId` tamo
gde se čeka `RecipeId`.

```kotlin
@Serializable @JvmInline
value class Kcal(val value: Double) : Comparable<Kcal> {
    operator fun plus(other: Kcal) = Kcal(value + other.value)
    operator fun times(factor: Double) = Kcal(value * factor)
}
```

U TypeScript-u su i kalorije i grami `number`, pa takva greška prolazi do produkcije.
`core/model/Units.kt`, `core/model/Ids.kt`

## 2. Operator overloading — sabiranje i skaliranje nutrijenata

```kotlin
val dnevniUnos = obroci.map { it.nutrition }.sum()   // Nutrition + Nutrition
val zaTriPorcije = recept.nutrition * 3.0            // Nutrition * Double
```

React i Vue verzija na istim mestima imaju ručno ispisano sabiranje polje po polje.
`core/model/Nutrition.kt`

## 3. Sealed hijerarhije + iscrpan `when`

| Tip | Gde | Šta hvata |
|---|---|---|
| `Route` | navigacija, 17 destinacija | ruta bez ekrana ne prolazi kompilaciju |
| `LoadState<T>` | start aplikacije | `Loading` / `Ready` / `Failed`, nemoguće zaboraviti granu |
| `RestrictionTarget` | restrikcije | `cheatMeals` se ne može provući kroz granu koja očekuje nutrijent |
| `RestrictionCheck` | provera golova | `Satisfied` / `Violated` / `Inactive` / `NotTracked` |
| `SearchResult` | globalna pretraga | tri tipa rezultata u jednoj listi, bez `type` stringa |
| `Formula` | kalkulator | tri formule kao objekti, svaka sa svojim računom |

Kad se doda peti tip obroka, build pukne na **svakom** mestu koje ga nije obradilo —
umesto da aplikacija tiho prikaže prazno polje.

## 4. Enumi umesto string unija

`MealSlot`, `Difficulty`, `ChallengeStatus`, `ActivityLevel`… nose i podatke uz sebe:

```kotlin
enum class ActivityLevel(val label: String, val multiplier: Double) {
    @SerialName("umereno_aktivan") UMERENO_AKTIVAN("Umereno aktivan", 1.55),
    …
}
```

`@SerialName` čuva tačne srpske vrednosti iz JSON-a, pa su podaci identični React/Vue verziji,
a formula i njen množilac stoje na istom mestu umesto u odvojenoj mapi negde u kalkulatoru.

## 5. Type-safe DSL za filtriranje

```kotlin
recepti.filterBy {
    query(tekst)
    mealType(izabraniObroci)
    maxCalories(500)
}
```

Receiver lambda umesto niza `.filter()` poziva ili objekta opcija bez provere u kompajl vremenu.
`core/filter/RecipeFilter.kt`

## 6. Extension funkcije i svojstva

Ponašanje stoji uz model, ali van `data class` tela — model ostaje čist podatak, a logika se
dodaje bez nasleđivanja i bez wrapper klase:

```kotlin
val Recipe.totalTime: Minutes
fun Recipe.scaledTo(newServings: Int): Recipe
fun Recipe.conflictsWith(profile: Profile): List<String>
val PlanDay.totalNutrition: Nutrition
val MealSlot.icon: ImageVector      // u UI sloju — model ostaje bez UI zavisnosti
```

## 7. `StateFlow` kao jedini izvor istine

`NutriRepository` drži privatne `MutableStateFlow`-ove, izložene napolje kao read-only
`StateFlow`. Svaki ekran preko `combine()` sklapa svoje stanje u **jedan** `StateFlow`:

```kotlin
val uiState: StateFlow<HomeUiState> = combine(
    repository.mealPlan, repository.goals, repository.profile,
    repository.notifications, repository.challenges,
) { plan, goals, profile, notifications, challenges -> … }
    .stateIn(viewModelScope, SharingStarted.Eagerly, HomeUiState())
```

Zbog toga zvezdica na receptu odmah menja Biblioteku, a štiklirana stavka odmah menja brojač
na Dashboard-u — bez ijedne linije „javi drugom ekranu".

## 8. Compose animacije i gestovi, bez ijedne biblioteke

`AnimatedContent` između koraka onboarding-a, `AnimatedVisibility` za sheet-ove i pretragu,
`animateFloatAsState` na prstenovima i trakama, `SwipeToDismissBox` na listi za kupovinu,
`combinedClickable` za dugi pritisak.

## 9. Grafici crtani `Canvas`-om

Nema chart biblioteke — makro prsten, donut i stubičasti grafik su `drawArc`/`drawPath`/
`Brush.linearGradient` u `ui/components/charts/`. Paleta je ista daltonizam-bezbedna kao u
React/Vue verziji.

---

# Šta se pokazalo kao problem (Compose Multiplatform za Web, Beta)

Ovo je iskren deo poređenja — troškovi koje React i Vue verzija nisu imale.

## Vremenski zasnovane korutine nisu pouzdane

`debounce(250)` na pretrazi i `SharingStarted.WhileSubscribed(5_000)` na `stateIn` **povremeno
nisu okidali**. Posledica: `combine` ne bi emitovao nijednu vrednost i ekran bi zauvek ostao na
početnom stanju — „Učitavanje…" na listi za kupovinu, „0 recepata" na Discovery-ju.

Nije bilo izuzetka u konzoli; ekran je prosto stajao. Otkriveno je poređenjem accessibility
stabla (koje je pokazivalo tačne podatke) sa onim što je ViewModel emitovao.

**Rešenje:** izbačen `debounce` (filtriranje 18 recepata je ionako trenutno) i prelazak sa
`WhileSubscribed(5_000)` na `SharingStarted.Eagerly`. Posle toga svih 17 ekrana radi stabilno.

## `collectAsStateWithLifecycle()` ne pretplaćuje ekran

Standardna preporuka na Androidu je `collectAsStateWithLifecycle()`. Na Compose/Wasm cilju
lifecycle ne dolazi do `STARTED`, pa se flow **nikad ne sakuplja** i ekran ostaje na
`initialValue`. Zamenjeno običnim `collectAsState()` u svih 13 ekrana — na webu ionako nema
procesnog lifecycle-a zbog kog bi se štedela baterija.

## `Res.readBytes()` iz Compose Resources ume da se zaglavi

Učitavanje JSON-a iz `composeResources/files/` nikad ne bi razrešilo svoj `Promise` —
u mrežnom logu se ne vidi **nijedan** zahtev ka toj putanji, a u konzoli stoji
`Failed to execute 'open' on 'CacheStorage'`. Zaglavljivanje je unutar biblioteke, pre fetch-a.

**Rešenje:** mock podaci su ugrađeni u izvorni kod kao Base64 (`core/data/EmbeddedMockJson.kt`,
generisano skriptom iz `shared-mock-data/`), pa su dostupni sinhrono i bez ijednog suspend
poziva. Bajtovi su i dalje identični React/Vue verziji.

## Value klase staju na granici navigacije

`navigation-compose` ne zna da spakuje `RecipeId` u URL bez sopstvenog `NavType`. Rute sa
argumentom zato drže `String` u primarnom konstruktoru, a sekundarni prima `RecipeId`:

```kotlin
@Serializable
data class RecipeDetail(val rawId: String) : Route {
    constructor(id: RecipeId) : this(id.raw)
    val id: RecipeId get() = RecipeId(rawId)
}
```

Pozivalac i dalje barata tipom, biblioteka dobija `String`.

## Nema emodžija

Bundlovan je Inter (četiri debljine), koji nema emodži glifove — emodži se iscrtava kao prazan
kvadrat. React i Vue verzija se oslanjaju na sistemski emodži font. Ovde su zato Material ikone,
a placeholder „slike" recepata su gradijent + monogram (`PP` za „Punjene paprike") umesto emodžija.

## Ostalo

- **Veličina bundle-a:** development build je ~37 MB (28.9 MB `nutriapp.wasm` + 8.25 MB
  `skiko.wasm`) — Skia se šalje u browser. Produkcijski build je znatno manji, ali je ovo
  red veličine koji React/Vue verzije nemaju.
- **Vreme build-a:** prvi build 5—15 min; inkrementalni 2—9 min. Vite u React/Vue verziji
  radi u sekundama.
- **Canvas rendering:** nema DOM-a, nema selekcije teksta browserom, nema SEO. Deep-link radi
  jer aplikacija čita `window.location.hash` pri startu (`Main.kt` + `routeFromPath`).
