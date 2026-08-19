package rs.nutriapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Tonalna paleta izvedena iz Kotlin brend boje `#7F52FF`.
 *
 * Sekundarna je prigusena ljubicasto-siva (isti ton, niza zasicenost — standardna M3
 * tehnika za "tihu" pratecu boju), tercijarna je toplo cadjavo-narandzasta iz Kotlin
 * gradijenta (ljubicasta -> magenta -> narandzasta na logotipu), koriscena stedljivo
 * na hero elementima i progres prstenovima.
 *
 * Sve tri sheme (Light/Dark/Black) dele isti seed — to je konkretna M3 prednost koju
 * React (Tailwind, rucno biran svaki ton) i Vue (Vuetify, jedna svetla paleta) nemaju:
 * ovde je citava tema jedan poziv funkcije, ne drugi set stilova.
 */
internal object KotlinColors {
    // ── Primary (Kotlin ljubicasta) ─────────────────────────────────────────────────
    val primaryLight = Color(0xFF7F52FF)
    val onPrimaryLight = Color(0xFFFFFFFF)
    val primaryContainerLight = Color(0xFFE9DDFF)
    val onPrimaryContainerLight = Color(0xFF26005C)

    val primaryDark = Color(0xFFCFBCFF)
    val onPrimaryDark = Color(0xFF3B0090)
    val primaryContainerDark = Color(0xFF5A2FB8)
    val onPrimaryContainerDark = Color(0xFFE9DDFF)

    // ── Secondary (prigusena ljubicasto-siva) ───────────────────────────────────────
    val secondaryLight = Color(0xFF625B71)
    val onSecondaryLight = Color(0xFFFFFFFF)
    val secondaryContainerLight = Color(0xFFE8DEF8)
    val onSecondaryContainerLight = Color(0xFF1E192B)

    val secondaryDark = Color(0xFFCBC2DB)
    val onSecondaryDark = Color(0xFF332D41)
    val secondaryContainerDark = Color(0xFF4A4458)
    val onSecondaryContainerDark = Color(0xFFE8DEF8)

    // ── Tertiary (topao akcenat iz Kotlin gradijenta) ───────────────────────────────
    val tertiaryLight = Color(0xFFB34700)
    val onTertiaryLight = Color(0xFFFFFFFF)
    val tertiaryContainerLight = Color(0xFFFFDBC8)
    val onTertiaryContainerLight = Color(0xFF3A1400)

    val tertiaryDark = Color(0xFFFFB68C)
    val onTertiaryDark = Color(0xFF5C1A00)
    // Tamniji i manje zasicen od standardnog M3 tona: na tamnoj temi je jaka narandzasta
    // podloga previse "vikala" pored ljubicaste hero kartice.
    val tertiaryContainerDark = Color(0xFF55240B)
    val onTertiaryContainerDark = Color(0xFFFFDBC8)

    // ── Error (M3 standard) ──────────────────────────────────────────────────────────
    val errorLight = Color(0xFFBA1A1A)
    val onErrorLight = Color(0xFFFFFFFF)
    val errorContainerLight = Color(0xFFFFDAD6)
    val onErrorContainerLight = Color(0xFF410002)

    val errorDark = Color(0xFFFFB4AB)
    val onErrorDark = Color(0xFF690005)
    val errorContainerDark = Color(0xFF93000A)
    val onErrorContainerDark = Color(0xFFFFDAD6)

    // ── Neutral — Light ──────────────────────────────────────────────────────────────
    val backgroundLight = Color(0xFFFFFBFF)
    val onBackgroundLight = Color(0xFF1C1B1F)
    val surfaceLight = Color(0xFFFFFBFF)
    val onSurfaceLight = Color(0xFF1C1B1F)
    val surfaceVariantLight = Color(0xFFE7E0EB)
    val onSurfaceVariantLight = Color(0xFF49454E)
    val outlineLight = Color(0xFF7A7580)
    val outlineVariantLight = Color(0xFFCAC4CF)
    val surfaceContainerLowestLight = Color(0xFFFFFFFF)
    val surfaceContainerLowLight = Color(0xFFF7F2FA)
    val surfaceContainerLight = Color(0xFFF1ECF4)
    val surfaceContainerHighLight = Color(0xFFECE6EF)
    val surfaceContainerHighestLight = Color(0xFFE6E0E9)
    val surfaceDimLight = Color(0xFFDED8E0)
    val surfaceBrightLight = Color(0xFFFFFBFF)
    val inverseSurfaceLight = Color(0xFF313033)
    val inverseOnSurfaceLight = Color(0xFFF4EFF4)
    val inversePrimaryLight = Color(0xFFCFBCFF)

    // ── Neutral — Dark ───────────────────────────────────────────────────────────────
    val backgroundDark = Color(0xFF141218)
    val onBackgroundDark = Color(0xFFE6E0E9)
    val surfaceDark = Color(0xFF141218)
    val onSurfaceDark = Color(0xFFE6E0E9)
    val surfaceVariantDark = Color(0xFF49454E)
    val onSurfaceVariantDark = Color(0xFFCAC4CF)
    val outlineDark = Color(0xFF948F99)
    val outlineVariantDark = Color(0xFF49454E)
    val surfaceContainerLowestDark = Color(0xFF0F0D13)
    val surfaceContainerLowDark = Color(0xFF1D1B20)
    val surfaceContainerDark = Color(0xFF211F26)
    val surfaceContainerHighDark = Color(0xFF2B2930)
    val surfaceContainerHighestDark = Color(0xFF36343B)
    val surfaceDimDark = Color(0xFF141218)
    val surfaceBrightDark = Color(0xFF3B383E)
    val inverseSurfaceDark = Color(0xFFE6E0E9)
    val inverseOnSurfaceDark = Color(0xFF313033)
    val inversePrimaryDark = Color(0xFF7F52FF)

    // ── Neutral — Black (AMOLED) ─────────────────────────────────────────────────────
    // Isti brend tonovi kao Dark; menja se samo pozadina/povrsine na cistu crnu —
    // ustedi bateriju na OLED ekranu i daje maksimalan kontrast.
    val backgroundBlack = Color(0xFF000000)
    val surfaceBlack = Color(0xFF000000)
    val surfaceContainerLowestBlack = Color(0xFF000000)
    val surfaceContainerLowBlack = Color(0xFF0A0A0D)
    val surfaceContainerBlack = Color(0xFF121014)
    val surfaceContainerHighBlack = Color(0xFF1C1A1F)
    val surfaceContainerHighestBlack = Color(0xFF26232A)
    val surfaceDimBlack = Color(0xFF000000)
    val surfaceBrightBlack = Color(0xFF2B2930)

    // ── Kotlin gradijent — koristi se stedljivo, na hero elementima i prstenovima ────
    val gradientStart = Color(0xFF7F52FF)
    val gradientMid = Color(0xFFC711E1)
    val gradientEnd = Color(0xFFE44857)
}

/**
 * Boje statusa — namerno ODVOJENE od brend palete gore.
 *
 * "U okviru / granicno / van okvira" i grafici u Statistici koriste ovu, semanticku
 * paletu (zelena -> amber -> plava -> rose), a ne primary/tertiary. Ljubicasta je
 * namerno izbacena iz serija grafika jer se kod deuteranopije stapa sa plavom.
 */
internal object StatusColors {
    val good = Color(0xFF2E7D5B) // zelena — u okviru / ispunjeno
    val warn = Color(0xFFB8860B) // amber — granicno
    val bad = Color(0xFFC13515) // rose/crvena — van okvira
    val info = Color(0xFF3567C9) // plava — neutralna informacija

    val chartSeries = listOf(good, warn, info, Color(0xFFC13584)) // rose bez ljubicaste
}
