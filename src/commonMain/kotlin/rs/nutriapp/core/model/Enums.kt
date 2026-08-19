package rs.nutriapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Sve sto je u `types.ts` bila string unija (`'dorucak' | 'rucak' | ...`) ovde je enum.
 *
 * Razlika koja se stvarno oseti: `when (slot)` nad enumom bez `else` grane kompajler
 * proverava na iscrpnost. Ako se sutra doda peti obrok, build pukne na svakom mestu
 * koje ga nije obradilo — umesto da aplikacija tiho prikaze prazno polje.
 *
 * `@SerialName` cuva tacne srpske vrednosti iz JSON-a, pa su podaci i dalje identicni
 * onima u React i Vue verziji.
 */
@Serializable
enum class MealSlot(val label: String) {
    @SerialName("dorucak")
    DORUCAK("Doručak"),

    @SerialName("uzina")
    UZINA("Užina"),

    @SerialName("rucak")
    RUCAK("Ručak"),

    @SerialName("vecera")
    VECERA("Večera");

    companion object {
        /** Redosled kojim se obroci prikazuju kroz dan. */
        val chronological = listOf(DORUCAK, UZINA, RUCAK, VECERA)
    }
}

@Serializable
enum class Difficulty(val label: String, val level: Int) {
    @SerialName("lako")
    LAKO("Lako", 1),

    @SerialName("srednje")
    SREDNJE("Srednje", 2),

    @SerialName("zahtevno")
    ZAHTEVNO("Zahtevno", 3),
}

@Serializable
enum class ChallengeStatus(val label: String) {
    @SerialName("aktivan")
    AKTIVAN("Aktivan"),

    @SerialName("predlog")
    PREDLOG("Predlog"),

    @SerialName("zavrsen")
    ZAVRSEN("Završen"),

    @SerialName("odbijen")
    ODBIJEN("Odbijen"),
}

@Serializable
enum class Visibility(val label: String) {
    @SerialName("javno")
    JAVNO("Javno"),

    @SerialName("prijatelji")
    PRIJATELJI("Prijatelji"),

    @SerialName("privatno")
    PRIVATNO("Privatno"),
}

@Serializable
enum class FriendStatus(val label: String) {
    @SerialName("prijatelj")
    PRIJATELJ("Prijatelj"),

    @SerialName("zahtev_poslat")
    ZAHTEV_POSLAT("Zahtev poslat"),

    @SerialName("zahtev_primljen")
    ZAHTEV_PRIMLJEN("Zahtev primljen"),
}

@Serializable
enum class Gender(val label: String) {
    @SerialName("muski")
    MUSKI("Muški"),

    @SerialName("zenski")
    ZENSKI("Ženski"),

    @SerialName("drugo")
    DRUGO("Drugo"),
}

/**
 * Nivo aktivnosti nosi i svoj Harris-Benedict/Mifflin mnozilac — podatak i formula
 * stoje na istom mestu, umesto u odvojenoj mapi negde u kalkulatoru.
 */
@Serializable
enum class ActivityLevel(val label: String, val description: String, val multiplier: Double) {
    @SerialName("sedentaran")
    SEDENTARAN("Sedentaran", "Kancelarijski posao, bez treninga", 1.2),

    @SerialName("lako_aktivan")
    LAKO_AKTIVAN("Lako aktivan", "Lagana aktivnost 1—3 dana nedeljno", 1.375),

    @SerialName("umereno_aktivan")
    UMERENO_AKTIVAN("Umereno aktivan", "Trening 3—5 dana nedeljno", 1.55),

    @SerialName("vrlo_aktivan")
    VRLO_AKTIVAN("Vrlo aktivan", "Trening 6—7 dana nedeljno", 1.725),

    @SerialName("ekstremno_aktivan")
    EKSTREMNO_AKTIVAN("Ekstremno aktivan", "Fizički posao ili dva treninga dnevno", 1.9),
}

@Serializable
enum class PrimaryGoal(val label: String, val calorieAdjustment: Double) {
    @SerialName("gubitak_tezine")
    GUBITAK_TEZINE("Gubitak težine", -0.20),

    @SerialName("odrzavanje")
    ODRZAVANJE("Održavanje", 0.0),

    @SerialName("dobijanje_mase")
    DOBIJANJE_MASE("Dobijanje mase", 0.15),
}

@Serializable
enum class RestrictionScope(val label: String) {
    @SerialName("obrok")
    OBROK("Po obroku"),

    @SerialName("dan")
    DAN("Dnevno"),

    @SerialName("nedelja")
    NEDELJA("Nedeljno"),
}

@Serializable
enum class RestrictionOperator(val label: String, val symbol: String) {
    @SerialName("min")
    MIN("Minimum", "≥"),

    @SerialName("max")
    MAX("Maksimum", "≤"),
}

@Serializable
enum class DeviationStatus(val label: String) {
    @SerialName("u_okviru")
    U_OKVIRU("U okviru"),

    @SerialName("granicno")
    GRANICNO("Granično"),

    @SerialName("van_okvira")
    VAN_OKVIRA("Van okvira"),
}

@Serializable
enum class NotificationType(val label: String) {
    @SerialName("podsetnik")
    PODSETNIK("Podsetnik"),

    @SerialName("izazov")
    IZAZOV("Izazov"),

    @SerialName("prijatelj")
    PRIJATELJ("Prijatelj"),

    @SerialName("cena")
    CENA("Cena"),

    @SerialName("gol")
    GOL("Gol"),

    @SerialName("recept")
    RECEPT("Recept"),
}
