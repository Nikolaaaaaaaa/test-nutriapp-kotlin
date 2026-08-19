package rs.nutriapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * M3 Expressive radijusi — primetno vece zaobljenje nego M3 baseline (koji staje na 16dp
 * za "large"). Tonalne `surfaceContainer` povrsine + ovi radijusi zamenjuju bordere:
 * React verzija razdvaja kartice linijom, ovde ih razdvaja ton i senka.
 */
internal val NutriShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/** Jos vece zaobljenje za hero elemente (naslovna kartica dana, sheet handle). */
internal val ExtraExpressiveShape = RoundedCornerShape(32.dp)
