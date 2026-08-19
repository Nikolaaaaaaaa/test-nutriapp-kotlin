package rs.nutriapp.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Sadrzaj je uvek u jednoj koloni sirine telefona (430dp) — mobile-only, bez desktop
 * layouta. Na sirem prozoru se ta kolona centrira preko zatamnjene pozadine, isti princip
 * kao `max-width: 430px` u README-u za React/Vue verziju, samo izveden preko `Modifier`
 * umesto Tailwind breakpointa (u kodu namerno nema grananja po sirini ekrana).
 */
@Composable
fun PhoneFrame(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceDim),
        contentAlignment = Alignment.TopCenter,
    ) {
        Surface(
            // Redosled je bitan: `widthIn` mora biti SPOLJA da bi stegao dolazna
            // ogranicenja, pa tek onda `fillMaxSize` puni to sto je ostalo. Obrnuto
            // (`fillMaxSize().widthIn(...)`) fiksira sirinu na celu sirinu prozora i
            // gornja granica se nikad ne primeni.
            modifier = Modifier
                .widthIn(max = 430.dp)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            content()
        }
    }
}
