package rs.nutriapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.nutriapp.core.util.monogram
import rs.nutriapp.core.util.seedGradient

/**
 * Placeholder "slika" recepta/proizvoda: gradijent izveden iz naziva + monogram.
 *
 * Nema slika-fajlova ni eksternih URL-ova — radi offline, nema polomljenih slika.
 * React i Vue verzija na ovom mestu koriste emodji; ovde je namerno monogram, jer je
 * bundlovan Inter (bez emodji glifova) pa bi emodji bio prazan kvadrat, a monogram uz
 * gradijent bolje lezi Material 3 izgledu ostatka aplikacije.
 */
@Composable
fun PlaceholderImage(
    seed: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    textSize: TextUnit = 28.sp,
) {
    val (start, end) = remember(seed) { seed.seedGradient() }
    val label = remember(seed) { seed.monogram() }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(Brush.linearGradient(listOf(start, end)))
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = textSize,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.92f),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
