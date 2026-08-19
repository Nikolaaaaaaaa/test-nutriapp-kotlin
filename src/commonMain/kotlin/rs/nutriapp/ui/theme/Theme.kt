package rs.nutriapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

private val LightScheme: ColorScheme = lightColorScheme(
    primary = KotlinColors.primaryLight,
    onPrimary = KotlinColors.onPrimaryLight,
    primaryContainer = KotlinColors.primaryContainerLight,
    onPrimaryContainer = KotlinColors.onPrimaryContainerLight,
    secondary = KotlinColors.secondaryLight,
    onSecondary = KotlinColors.onSecondaryLight,
    secondaryContainer = KotlinColors.secondaryContainerLight,
    onSecondaryContainer = KotlinColors.onSecondaryContainerLight,
    tertiary = KotlinColors.tertiaryLight,
    onTertiary = KotlinColors.onTertiaryLight,
    tertiaryContainer = KotlinColors.tertiaryContainerLight,
    onTertiaryContainer = KotlinColors.onTertiaryContainerLight,
    error = KotlinColors.errorLight,
    onError = KotlinColors.onErrorLight,
    errorContainer = KotlinColors.errorContainerLight,
    onErrorContainer = KotlinColors.onErrorContainerLight,
    background = KotlinColors.backgroundLight,
    onBackground = KotlinColors.onBackgroundLight,
    surface = KotlinColors.surfaceLight,
    onSurface = KotlinColors.onSurfaceLight,
    surfaceVariant = KotlinColors.surfaceVariantLight,
    onSurfaceVariant = KotlinColors.onSurfaceVariantLight,
    outline = KotlinColors.outlineLight,
    outlineVariant = KotlinColors.outlineVariantLight,
    surfaceContainerLowest = KotlinColors.surfaceContainerLowestLight,
    surfaceContainerLow = KotlinColors.surfaceContainerLowLight,
    surfaceContainer = KotlinColors.surfaceContainerLight,
    surfaceContainerHigh = KotlinColors.surfaceContainerHighLight,
    surfaceContainerHighest = KotlinColors.surfaceContainerHighestLight,
    surfaceDim = KotlinColors.surfaceDimLight,
    surfaceBright = KotlinColors.surfaceBrightLight,
    inverseSurface = KotlinColors.inverseSurfaceLight,
    inverseOnSurface = KotlinColors.inverseOnSurfaceLight,
    inversePrimary = KotlinColors.inversePrimaryLight,
)

private val DarkScheme: ColorScheme = darkColorScheme(
    primary = KotlinColors.primaryDark,
    onPrimary = KotlinColors.onPrimaryDark,
    primaryContainer = KotlinColors.primaryContainerDark,
    onPrimaryContainer = KotlinColors.onPrimaryContainerDark,
    secondary = KotlinColors.secondaryDark,
    onSecondary = KotlinColors.onSecondaryDark,
    secondaryContainer = KotlinColors.secondaryContainerDark,
    onSecondaryContainer = KotlinColors.onSecondaryContainerDark,
    tertiary = KotlinColors.tertiaryDark,
    onTertiary = KotlinColors.onTertiaryDark,
    tertiaryContainer = KotlinColors.tertiaryContainerDark,
    onTertiaryContainer = KotlinColors.onTertiaryContainerDark,
    error = KotlinColors.errorDark,
    onError = KotlinColors.onErrorDark,
    errorContainer = KotlinColors.errorContainerDark,
    onErrorContainer = KotlinColors.onErrorContainerDark,
    background = KotlinColors.backgroundDark,
    onBackground = KotlinColors.onBackgroundDark,
    surface = KotlinColors.surfaceDark,
    onSurface = KotlinColors.onSurfaceDark,
    surfaceVariant = KotlinColors.surfaceVariantDark,
    onSurfaceVariant = KotlinColors.onSurfaceVariantDark,
    outline = KotlinColors.outlineDark,
    outlineVariant = KotlinColors.outlineVariantDark,
    surfaceContainerLowest = KotlinColors.surfaceContainerLowestDark,
    surfaceContainerLow = KotlinColors.surfaceContainerLowDark,
    surfaceContainer = KotlinColors.surfaceContainerDark,
    surfaceContainerHigh = KotlinColors.surfaceContainerHighDark,
    surfaceContainerHighest = KotlinColors.surfaceContainerHighestDark,
    surfaceDim = KotlinColors.surfaceDimDark,
    surfaceBright = KotlinColors.surfaceBrightDark,
    inverseSurface = KotlinColors.inverseSurfaceDark,
    inverseOnSurface = KotlinColors.inverseOnSurfaceDark,
    inversePrimary = KotlinColors.inversePrimaryDark,
)

/**
 * AMOLED varijanta: isti brend tonovi kao Dark, samo su povrsine spustene na cistu crnu.
 * `copy()` nad `ColorScheme` — menjaju se samo neutralni tonovi, primary/secondary/tertiary
 * ostaju identicni Dark shemi.
 */
private val BlackScheme: ColorScheme = DarkScheme.copy(
    background = KotlinColors.backgroundBlack,
    surface = KotlinColors.surfaceBlack,
    surfaceContainerLowest = KotlinColors.surfaceContainerLowestBlack,
    surfaceContainerLow = KotlinColors.surfaceContainerLowBlack,
    surfaceContainer = KotlinColors.surfaceContainerBlack,
    surfaceContainerHigh = KotlinColors.surfaceContainerHighBlack,
    surfaceContainerHighest = KotlinColors.surfaceContainerHighestBlack,
    surfaceDim = KotlinColors.surfaceDimBlack,
    surfaceBright = KotlinColors.surfaceBrightBlack,
)

/**
 * Razresava `ThemeMode` u konkretnu shemu.
 *
 * Cetiri rezima, jedan seed: `SYSTEM` prati `isSystemInDarkTheme()`, ostala tri su
 * eksplicitni izbor iz Podesavanja (Profil ekran). Iscrpan `when` — ako se doda peti
 * rezim, ovo mesto ne kompajlira dok se ne obradi.
 */
@Composable
@ReadOnlyComposable
private fun resolveScheme(mode: ThemeMode): ColorScheme = when (mode) {
    ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) DarkScheme else LightScheme
    ThemeMode.LIGHT -> LightScheme
    ThemeMode.DARK -> DarkScheme
    ThemeMode.BLACK -> BlackScheme
}

@Composable
fun NutriTheme(themeMode: ThemeMode, content: @Composable () -> Unit) {
    val scheme = resolveScheme(themeMode)
    MaterialTheme(
        colorScheme = scheme,
        // Font() iz Compose Resources je @Composable (bajtovi se ucitavaju kroz resource
        // sistem), pa tipografija mora da se gradi unutar kompozicije.
        typography = nutriTypography(),
        shapes = NutriShapes,
        content = content,
    )
}

/** Da li je trenutno aktivna sema tamna — koriste komponente koje crtaju rucno (Canvas grafici). */
@Composable
@ReadOnlyComposable
fun isEffectiveDarkTheme(mode: ThemeMode): Boolean = when (mode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK, ThemeMode.BLACK -> true
}
