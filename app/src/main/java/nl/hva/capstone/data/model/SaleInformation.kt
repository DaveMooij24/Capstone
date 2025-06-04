package nl.hva.capstone.data.model

data class SaleInformation (
    val saleId: Long = 0L,
    val name: String,
    val price: Double,
    val tax: Int,
    val id: String = ""
)