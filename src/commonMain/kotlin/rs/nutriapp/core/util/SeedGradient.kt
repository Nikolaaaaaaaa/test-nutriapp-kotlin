package rs.nutriapp.core.util

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

/**
 * Deterministicki gradijent izveden iz naziva — koristi se za placeholder slike recepata
 * i proizvoda. Nema eksternih URL-ova ni nasumicnosti: isti naziv uvek daje istu boju,
 * pa app radi offline i nema polomljenih slika (isti princip kao u React/Vue verziji,
 * samo bez slike-fajla — ovde je to cisto racunanje boje iz stringa).
 */
fun String.seedHue(): Float {
    val hash = fold(0) { acc, c -> (acc * 31 + c.code) }
    return (abs(hash) % 360).toFloat()
}

fun String.seedGradient(): Pair<Color, Color> {
    val hue = seedHue()
    val start = Color.hsv(hue, 0.55f, 0.92f)
    val end = Color.hsv((hue + 42f) % 360f, 0.65f, 0.78f)
    return start to end
}

/**
 * Monogram za placeholder: prva slova prve dve reci ("Punjene paprike" -> "PP"),
 * ili prva dva slova ako je naziv jedna rec.
 */
fun String.monogram(): String {
    val words = trim().split(' ', '-', '/').filter { it.isNotBlank() }
    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words[0].take(2).uppercase()
        else -> (words[0].take(1) + words[1].take(1)).uppercase()
    }
}
