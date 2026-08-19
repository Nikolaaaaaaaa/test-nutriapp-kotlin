package rs.nutriapp.core.model

import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.serialization.Serializable

/**
 * Merne jedinice kao inline value klase.
 *
 * Kotlin ih kompajlira nazad u obican Double — nema alokacije, nema runtime cene — ali
 * kompajler odbija da sabere kalorije sa gramima ili dinare sa mililitrima. U TypeScript-u
 * su sve to `number`, pa takva greska prolazi do produkcije.
 *
 * Ovo je jedna od stvari koje React i Vue verzija nemaju kako da izraze.
 */
@Serializable
@JvmInline
value class Kcal(val value: Double) : Comparable<Kcal> {
    operator fun plus(other: Kcal) = Kcal(value + other.value)
    operator fun minus(other: Kcal) = Kcal(value - other.value)
    operator fun times(factor: Double) = Kcal(value * factor)
    operator fun div(other: Kcal): Double = value / other.value

    override fun compareTo(other: Kcal): Int = value.compareTo(other.value)

    val rounded: Int get() = value.roundToInt()

    companion object {
        val Zero = Kcal(0.0)
    }
}

@Serializable
@JvmInline
value class Grams(val value: Double) : Comparable<Grams> {
    operator fun plus(other: Grams) = Grams(value + other.value)
    operator fun minus(other: Grams) = Grams(value - other.value)
    operator fun times(factor: Double) = Grams(value * factor)
    operator fun div(other: Grams): Double = value / other.value

    override fun compareTo(other: Grams): Int = value.compareTo(other.value)

    val rounded: Int get() = value.roundToInt()

    /** "12.4 g" ali "12 g" kad je ceo broj — bez suvisne decimale. */
    fun format(): String =
        if (abs(value - value.roundToInt()) < 0.05) "${value.roundToInt()} g"
        else "${(value * 10).roundToInt() / 10.0} g"

    companion object {
        val Zero = Grams(0.0)
    }
}

@Serializable
@JvmInline
value class Ml(val value: Double) : Comparable<Ml> {
    operator fun plus(other: Ml) = Ml(value + other.value)
    operator fun times(factor: Double) = Ml(value * factor)

    override fun compareTo(other: Ml): Int = value.compareTo(other.value)

    val rounded: Int get() = value.roundToInt()

    /** 2500 ml -> "2.5 l" */
    fun formatLiters(): String = "${(value / 100).roundToInt() / 10.0} l"

    companion object {
        val Zero = Ml(0.0)
    }
}

/** Cena u dinarima. */
@Serializable
@JvmInline
value class Rsd(val value: Double) : Comparable<Rsd> {
    operator fun plus(other: Rsd) = Rsd(value + other.value)
    operator fun minus(other: Rsd) = Rsd(value - other.value)
    operator fun times(factor: Double) = Rsd(value * factor)

    override fun compareTo(other: Rsd): Int = value.compareTo(other.value)

    fun format(): String = "${value.roundToInt()} RSD"

    companion object {
        val Zero = Rsd(0.0)
    }
}

/** Trajanje u minutima — da se `prepTime` i `servings` ne pomesaju. */
@Serializable
@JvmInline
value class Minutes(val value: Int) : Comparable<Minutes> {
    operator fun plus(other: Minutes) = Minutes(value + other.value)

    override fun compareTo(other: Minutes): Int = value.compareTo(other.value)

    /** 95 -> "1 h 35 min", 45 -> "45 min" */
    fun format(): String = when {
        value < 60 -> "$value min"
        value % 60 == 0 -> "${value / 60} h"
        else -> "${value / 60} h ${value % 60} min"
    }

    companion object {
        val Zero = Minutes(0)
    }
}

// Zbirovi nad kolekcijama — `sumOf` iz standardne biblioteke vraca Double/Int,
// pa ovde stoje varijante koje cuvaju tip jedinice.

fun <T> Iterable<T>.sumOfKcal(selector: (T) -> Kcal): Kcal =
    fold(Kcal.Zero) { acc, item -> acc + selector(item) }

fun <T> Iterable<T>.sumOfGrams(selector: (T) -> Grams): Grams =
    fold(Grams.Zero) { acc, item -> acc + selector(item) }

fun <T> Iterable<T>.sumOfRsd(selector: (T) -> Rsd): Rsd =
    fold(Rsd.Zero) { acc, item -> acc + selector(item) }
