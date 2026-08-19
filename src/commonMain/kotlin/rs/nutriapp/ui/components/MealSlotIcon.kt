package rs.nutriapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import rs.nutriapp.core.model.MealSlot

/**
 * Ikona po tipu obroka.
 *
 * Extension property nad enumom iz `core.model` — model ostaje bez ijedne UI zavisnosti,
 * a poziv se i dalje cita kao svojstvo (`slot.icon`). U TypeScript-u bi ovo bila mapa
 * ili `switch` funkcija koja se poziva sa strane.
 *
 * `when` je iscrpan: doda li se peti obrok, ovo prestaje da se kompajlira.
 */
val MealSlot.icon: ImageVector
    get() = when (this) {
        MealSlot.DORUCAK -> Icons.Outlined.WbSunny
        MealSlot.UZINA -> Icons.Outlined.Cookie
        MealSlot.RUCAK -> Icons.Outlined.LunchDining
        MealSlot.VECERA -> Icons.Outlined.Bedtime
    }
