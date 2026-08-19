package rs.nutriapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Traka napretka za jedan nutrijent (unos vs. gol) — koristi se na Dashboard-u i Statistici.
 *
 * Boje teksta i podloge su parametri sa podrazumevanim vrednostima za obicnu povrsinu.
 * Na obojenoj kartici (npr. ljubicasti hero na Dashboard-u) pozivalac prosledjuje boje
 * izvedene iz te kartice — inace bi podloga trake nestala u pozadini.
 */
@Composable
fun NutrientBar(
    label: String,
    valueLabel: String,
    fraction: Float,
    color: Color,
    modifier: Modifier = Modifier,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
) {
    val animated by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 140f),
        label = "nutrientBar",
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = labelColor)
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.labelMedium,
                color = valueColor,
            )
        }
        androidx.compose.foundation.layout.Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(trackColor),
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color),
            )
        }
    }
}
