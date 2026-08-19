package rs.nutriapp.ui.screens.newproduct

import androidx.lifecycle.ViewModel
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.Grams
import rs.nutriapp.core.model.Kcal
import rs.nutriapp.core.model.Nutrition
import rs.nutriapp.core.model.Product
import rs.nutriapp.core.model.ProductId

/** Rezultat validacije — sealed umesto bool, nosi tacnu poruku po polju. */
sealed interface ProductFormResult {
    data class Valid(val product: Product) : ProductFormResult
    data class Invalid(val message: String) : ProductFormResult
}

data class ProductFormInput(
    val name: String,
    val category: String,
    val caloriesPer100g: String,
    val proteinPer100g: String,
    val carbsPer100g: String,
    val fatPer100g: String,
)

class NewProductViewModel(private val repository: NutriRepository) : ViewModel() {

    val categories = repository.categories

    /**
     * Validacija sa `require`: baca `IllegalArgumentException` sa jasnom porukom na prvom
     * neispravnom uslovu — ovde se hvata i pretvara u sealed rezultat, umesto da ekran
     * mora rucno da proverava svako polje pre poziva.
     */
    fun validate(input: ProductFormInput): ProductFormResult {
        return try {
            require(input.name.isNotBlank()) { "Naziv je obavezan." }
            require(input.category.isNotBlank()) { "Izaberi kategoriju." }

            val calories = input.caloriesPer100g.trim().toDoubleOrNull()
            requireNotNull(calories) { "Kalorije moraju biti broj." }
            require(calories >= 0) { "Kalorije ne mogu biti negativne." }

            val protein = input.proteinPer100g.trim().toDoubleOrNull() ?: 0.0
            val carbs = input.carbsPer100g.trim().toDoubleOrNull() ?: 0.0
            val fat = input.fatPer100g.trim().toDoubleOrNull() ?: 0.0
            require(protein >= 0 && carbs >= 0 && fat >= 0) { "Nutrijenti ne mogu biti negativni." }

            val product = Product(
                id = ProductId("p-custom-${kotlin.random.Random.nextInt(10000, 99999)}"),
                name = input.name.trim(),
                category = input.category,
                unit = "100 g",
                nutrition = Nutrition(
                    calories = Kcal(calories),
                    protein = Grams(protein),
                    carbs = Grams(carbs),
                    fat = Grams(fat),
                ),
                custom = true,
            )
            ProductFormResult.Valid(product)
        } catch (e: IllegalArgumentException) {
            ProductFormResult.Invalid(e.message ?: "Neispravan unos.")
        }
    }

    fun save(product: Product) = repository.addCustomProduct(product)
}
