package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Challenge(
    val id: ChallengeId,
    val title: String,
    val description: String,
    val durationDays: Int,
    val difficulty: Difficulty,
    val category: String,
    val icon: String,
    val status: ChallengeStatus,
    val progress: Int = 0,
    val target: Int = 1,
    val reward: String = "",
    val participants: Int = 0,
)

val Challenge.progressFraction: Float
    get() = if (target <= 0) 0f else (progress.toFloat() / target).coerceIn(0f, 1f)

val Challenge.isComplete: Boolean get() = progress >= target

/**
 * Izazovi razvrstani u tri grupe jednim prolazom kroz `partition` + `groupBy`.
 *
 * React/Vue verzija ovo radi sa tri odvojena `.filter()` poziva nad istim nizom.
 */
data class ChallengeBoard(
    val active: List<Challenge>,
    val suggested: List<Challenge>,
    val archived: List<Challenge>,
)

fun List<Challenge>.toBoard(): ChallengeBoard {
    val byStatus = groupBy { it.status }
    return ChallengeBoard(
        active = byStatus[ChallengeStatus.AKTIVAN].orEmpty(),
        suggested = byStatus[ChallengeStatus.PREDLOG].orEmpty(),
        archived = (byStatus[ChallengeStatus.ZAVRSEN].orEmpty() + byStatus[ChallengeStatus.ODBIJEN].orEmpty())
            .sortedByDescending { it.status == ChallengeStatus.ZAVRSEN },
    )
}
