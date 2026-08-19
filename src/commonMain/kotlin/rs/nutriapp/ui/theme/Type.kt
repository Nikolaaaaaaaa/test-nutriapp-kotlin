package rs.nutriapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font as ResFont
import rs.nutriapp.resources.Res
import rs.nutriapp.resources.inter_bold
import rs.nutriapp.resources.inter_medium
import rs.nutriapp.resources.inter_regular
import rs.nutriapp.resources.inter_semibold

/**
 * `Font()` iz `org.jetbrains.compose.resources` je @Composable — na Kotlin/Wasm cilju
 * bajtovi fonta se ucitavaju kroz resource sistem, pa se `Typography` mora graditi
 * unutar kompozicije (`remember`), a ne kao top-level `val`. Zato ovo nije objekat
 * nego funkcija koju `NutriTheme` poziva.
 *
 * Cetiri debljine (Regular/Medium/SemiBold/Bold) su unapred "zapecene" staticke
 * instance Inter varijabilnog fonta — vidi `THIRD-PARTY-LICENSES/README.md`.
 */
@Composable
internal fun nutriFontFamily(): FontFamily {
    val regular = ResFont(Res.font.inter_regular, FontWeight.Normal)
    val medium = ResFont(Res.font.inter_medium, FontWeight.Medium)
    val semiBold = ResFont(Res.font.inter_semibold, FontWeight.SemiBold)
    val bold = ResFont(Res.font.inter_bold, FontWeight.Bold)
    return remember(regular, medium, semiBold, bold) {
        FontFamily(regular, medium, semiBold, bold)
    }
}

@Composable
internal fun nutriTypography(): Typography {
    val family = nutriFontFamily()

    fun style(size: Int, lineHeight: Int, weight: FontWeight, tracking: Double = 0.0): TextStyle = TextStyle(
        fontFamily = family,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = tracking.sp,
    )

    return remember(family) {
        Typography(
            // Veliki brojevi (kalorije, makroi) — display koristi SemiBold umesto
            // M3 baseline Regular, "expressive" karakter.
            displayLarge = style(45, 52, FontWeight.SemiBold, -0.25),
            displayMedium = style(36, 44, FontWeight.SemiBold),
            displaySmall = style(32, 40, FontWeight.SemiBold),
            headlineLarge = style(28, 34, FontWeight.SemiBold),
            headlineMedium = style(24, 30, FontWeight.SemiBold),
            headlineSmall = style(21, 27, FontWeight.SemiBold),
            titleLarge = style(20, 26, FontWeight.SemiBold),
            titleMedium = style(16, 22, FontWeight.Medium, 0.15),
            titleSmall = style(14, 20, FontWeight.Medium, 0.1),
            bodyLarge = style(16, 24, FontWeight.Normal, 0.15),
            bodyMedium = style(14, 20, FontWeight.Normal, 0.25),
            bodySmall = style(12, 16, FontWeight.Normal, 0.4),
            labelLarge = style(14, 20, FontWeight.Medium, 0.1),
            labelMedium = style(12, 16, FontWeight.Medium, 0.5),
            labelSmall = style(11, 15, FontWeight.Medium, 0.5),
        )
    }
}
