# NutriApp — Kotlin / Compose Multiplatform (Wasm)

Treća implementacija NutriApp prototipa, pored React i Vue verzije. Isti podaci, isti skup
od 17 ekrana — ali napisana od nule u Kotlinu, oko onoga u čemu je Kotlin jak. Nije port
React ni Vue koda.

> Poredbeni prototip, ne proizvod. Nema backenda, baze ni autentifikacije. Sve se resetuje na refresh.

## Pokretanje

Potreban je **JDK 21**. Gradle stiže kroz wrapper — ništa se ne instalira globalno.

```bash
cd kotlin-app
./gradlew wasmJsBrowserDevelopmentRun     # http://localhost:8080
```

Ostale komande:

```bash
./gradlew wasmJsBrowserDistribution       # produkcijski build u build/dist/
./gradlew build                           # kompajlira sve
```

> **Prvi build traje 5—15 minuta** — skida Kotlin/Wasm toolchain, Node i Skia. Kasniji
> build-ovi su 2—9 minuta. Traži Chrome/Edge 119+ (WasmGC).

Projekat pokazuje na svoj JDK preko `org.gradle.java.home` u `gradle.properties`; globalni
`JAVA_HOME` se ne dira. Ako je JDK 21 na drugoj putanji, izmeni tu liniju.

## Ekrani

Svih 17 iz glavnog README-a. Aplikacija čita `window.location.hash` pri startu, pa se svaki
ekran može otvoriti direktno:

| Ekran | URL |
|---|---|
| Home / Dashboard | `#/` |
| Onboarding (5 koraka) | `#/onboarding` |
| Login / Register | `#/login` |
| Discovery | `#/discovery` |
| Detalj recepta | `#/recept/r-01` |
| Novi / izmena recepta | `#/recept/novi`, `#/recept/r-01/izmena` |
| Meal planning | `#/plan` |
| Logovanje | `#/logovanje` |
| Golovi i izazovi | `#/golovi` |
| Statistika | `#/statistika` |
| Lista za kupovinu | `#/grocery` |
| Supstitucije | `#/supstitucije` |
| Biblioteka | `#/biblioteka` |
| Profil (+ podešavanja teme) | `#/profil` |
| Kalkulator kalorija | `#/kalkulator` |
| Nova namirnica | `#/namirnica/nova` |

Globalna pretraga je overlay dostupan sa svakog ekrana (dugme u top bar-u ili `Ctrl+K`).

## Struktura

```
src/
  commonMain/kotlin/rs/nutriapp/
    App.kt                  koren: učitavanje podataka, tema, navigacija, chrome
    core/model/             @Serializable modeli, value klase, enumi, sealed hijerarhije
    core/data/              repozitorijum (StateFlow) + ugrađeni mock podaci
    core/filter/            type-safe DSL za filtriranje recepata
    core/di/                AppContainer + viewModel fabrike
    core/util/              gradijenti, monogrami, formatiranje
    ui/theme/               boje, tipografija, 4 režima teme
    ui/components/          deljene komponente + grafici crtani Canvas-om
    ui/nav/                 sealed Route, NavHost, tab bar, „Još" sheet, telefonski okvir
    ui/screens/<ekran>/     po ekranu: Screen.kt + ViewModel.kt
  commonMain/composeResources/font/   Inter (4 debljine)
  wasmJsMain/kotlin/Main.kt           ulazna tačka (~15 linija)
  wasmJsMain/resources/index.html
```

Sav kod je u `commonMain`; `wasmJsMain` je samo ulazna tačka. Zbog toga se Android ili
desktop target kasnije dodaje izmenom u Gradle-u, bez premeštanja ijednog fajla.

## Dizajn

Material 3, seed boja **Kotlin ljubičasta `#7F52FF`**, topao akcenat iz Kotlin gradijenta.
Veliki radijusi (20—28dp), tonalne `surfaceContainer` površine umesto bordera.

Četiri režima teme — **prati sistem / svetla / tamna / crna (AMOLED)** — biraju se u
Profil → Podešavanja. Sve četiri se generišu iz istog seed-a.

Mobile-only: sadržaj je uvek u koloni od 430dp, centriranoj na širem prozoru. Nigde nema
grananja po širini ekrana.

Nema eksternih URL-ova — placeholder „slike" su gradijent izveden iz naziva + monogram,
grafici su crtani `Canvas`-om. Aplikacija radi offline.

## Podaci

12 JSON fajlova iz `shared-mock-data/`, ugrađenih u izvorni kod kao Base64
(`core/data/EmbeddedMockJson.kt`, generisano skriptom — ne uređivati ručno). Bajtovi su
identični React i Vue verziji. Razlog za ugrađivanje umesto učitavanja kroz Compose Resources
je opisan u [KOTLIN-SPECIFICNOSTI.md](KOTLIN-SPECIFICNOSTI.md).

## Šta je specifično za Kotlin

Vidi **[KOTLIN-SPECIFICNOSTI.md](KOTLIN-SPECIFICNOSTI.md)** — value klase, operator
overloading, sealed hijerarhije, filter DSL, `StateFlow` + `combine`, Compose animacije,
i pošten spisak onoga što je na Compose/Wasm cilju bilo problematično.

## Licence

Inter font — SIL Open Font License 1.1, vidi `THIRD-PARTY-LICENSES/`.
