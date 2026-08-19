package rs.nutriapp.core.model

/**
 * Ulazni podaci za kalkulator kalorijskih potreba — jedan zajednicki oblik za sve formule.
 */
data class CalorieInput(
    val weightKg: Double,
    val heightCm: Double,
    val age: Int,
    val gender: Gender,
    val activityLevel: ActivityLevel,
    /** Samo Katch-McArdle koristi ovo; ostale formule ga ignorisu. */
    val bodyFatPct: Double? = null,
)

data class CalorieResult(
    val formula: Formula,
    val bmr: Kcal,
    val tdee: Kcal,
)

/**
 * Tri formule za bazalni metabolizam, kao sealed hijerarhija umesto `switch` po stringu.
 *
 * Svaki objekat nosi SVOJU implementaciju `compute()` — dodavanje cetvrte formule znaci
 * dodati jedan novi `data object` sa svojom formulom; postojece se ne diraju, i svaki
 * `when` koji ovo koristi (npr. za opis formule) kompajler ce oznaciti kao nepotpun dok
 * se novi slucaj ne obradi.
 */
sealed interface Formula {
    val label: String
    val description: String

    fun compute(input: CalorieInput): Kcal

    data object MifflinStJeor : Formula {
        override val label = "Mifflin-St Jeor"
        override val description = "Najtacnija za vecinu ljudi u opstoj populaciji (1990)."

        override fun compute(input: CalorieInput): Kcal {
            val base = 10 * input.weightKg + 6.25 * input.heightCm - 5 * input.age
            val genderOffset = when (input.gender) {
                Gender.MUSKI -> 5.0
                Gender.ZENSKI -> -161.0
                Gender.DRUGO -> -78.0 // prosek dva standardna ofseta
            }
            return Kcal(base + genderOffset)
        }
    }

    data object HarrisBenedict : Formula {
        override val label = "Harris-Benedict"
        override val description = "Klasicna formula (1919, revidirana 1984) — nesto vise procenjuje kod nizeg BMI."

        override fun compute(input: CalorieInput): Kcal {
            val w = input.weightKg
            val h = input.heightCm
            val a = input.age
            val value = when (input.gender) {
                Gender.MUSKI -> 88.362 + 13.397 * w + 4.799 * h - 5.677 * a
                Gender.ZENSKI -> 447.593 + 9.247 * w + 3.098 * h - 4.330 * a
                Gender.DRUGO -> {
                    val muski = 88.362 + 13.397 * w + 4.799 * h - 5.677 * a
                    val zenski = 447.593 + 9.247 * w + 3.098 * h - 4.330 * a
                    (muski + zenski) / 2
                }
            }
            return Kcal(value)
        }
    }

    data object KatchMcArdle : Formula {
        override val label = "Katch-McArdle"
        override val description = "Koristi telesnu mast — najpreciznija ako znas svoj procenat."

        override fun compute(input: CalorieInput): Kcal {
            val bodyFat = input.bodyFatPct ?: 20.0 // razumna pretpostavka ako nije uneto
            val leanMass = input.weightKg * (1 - bodyFat / 100.0)
            return Kcal(370 + 21.6 * leanMass)
        }
    }

    companion object {
        val all: List<Formula> = listOf(MifflinStJeor, HarrisBenedict, KatchMcArdle)
    }
}

fun Formula.evaluate(input: CalorieInput): CalorieResult {
    val bmr = compute(input)
    val tdee = bmr * input.activityLevel.multiplier
    return CalorieResult(this, bmr, tdee)
}

/** Sve tri formule odjednom, za poredjenje uz kalkulator. */
fun CalorieInput.evaluateAll(): List<CalorieResult> = Formula.all.map { it.evaluate(this) }
