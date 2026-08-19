package rs.nutriapp.ui.screens.onboarding

/**
 * Pet koraka wizard-a kao sealed hijerarhija sa fiksnim redosledom (`ordinal`).
 * Iscrpan `when` u `OnboardingScreen.kt` garantuje da svaki korak ima svoj UI.
 */
sealed interface OnboardingStep {
    val index: Int
    val title: String

    data object Welcome : OnboardingStep {
        override val index = 0
        override val title = "Dobrodošao/la"
    }

    data object BodyMetrics : OnboardingStep {
        override val index = 1
        override val title = "O tebi"
    }

    data object ActivityGoal : OnboardingStep {
        override val index = 2
        override val title = "Aktivnost i cilj"
    }

    data object Allergies : OnboardingStep {
        override val index = 3
        override val title = "Alergije"
    }

    data object Preferences : OnboardingStep {
        override val index = 4
        override val title = "Preferencije"
    }

    companion object {
        val all = listOf(Welcome, BodyMetrics, ActivityGoal, Allergies, Preferences)
    }
}
