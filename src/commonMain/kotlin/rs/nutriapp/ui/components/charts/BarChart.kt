package rs.nutriapp.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Bar-grafik nacrtan direktno preko `Canvas` — nema chart biblioteke.
 * Opciona linija golova (`goalLine`) se crta preko stubova, isprekidano.
 */
@Composable
fun BarChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    goalLine: Float? = null,
    goalLineColor: Color = MaterialTheme.colorScheme.tertiary,
    height: androidx.compose.ui.unit.Dp = 140.dp,
) {
    val maxValue = (values.maxOrNull() ?: 1f).coerceAtLeast(goalLine ?: 0f).coerceAtLeast(1f)
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 90f),
        label = "barChart",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
        ) {
            val barCount = values.size
            if (barCount == 0) return@Canvas
            val gap = 6.dp.toPx()
            val barWidth = (size.width - gap * (barCount - 1)) / barCount

            values.forEachIndexed { index, value ->
                val barHeight = (value / maxValue) * size.height * animatedProgress
                val left = index * (barWidth + gap)
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, size.height - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                )
            }

            if (goalLine != null) {
                val y = size.height - (goalLine / maxValue) * size.height
                drawGoalLine(y, goalLineColor)
            }
        }
        if (labels.isNotEmpty()) {
            androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth()) {
                labels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawGoalLine(y: Float, color: Color) {
    val dash = 8.dp.toPx()
    var x = 0f
    while (x < size.width) {
        drawLine(
            color = color,
            start = Offset(x, y),
            end = Offset((x + dash).coerceAtMost(size.width), y),
            strokeWidth = 2.dp.toPx(),
        )
        x += dash * 1.6f
    }
}
