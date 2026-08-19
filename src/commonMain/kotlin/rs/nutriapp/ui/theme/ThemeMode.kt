package rs.nutriapp.ui.theme

/**
 * Cetiri rezima teme — sealed-ovan kao enum jer nema dodatnih podataka po varijanti.
 *
 * `SYSTEM` prati `isSystemInDarkTheme()`, `BLACK` je AMOLED varijanta DARK-a sa cistom
 * crnom pozadinom (`#000000`) umesto tonalne tamno-ljubicaste povrsine — stedi bateriju
 * na OLED ekranima i cesto je trazena opcija u pravim mobilnim aplikacijama.
 */
enum class ThemeMode(val label: String) {
    SYSTEM("Prati sistem"),
    LIGHT("Svetla"),
    DARK("Tamna"),
    BLACK("Crna (AMOLED)"),
}
