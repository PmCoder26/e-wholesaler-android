package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
enum class UnitType {
    PIECE,
    BOX,
    SACK,
    DOZEN
}

@Serializable
data class SellingUnit(
    val id: Long? = null,
    val unitType: UnitType,
    val packets: Int,
    val sellingPrice: Double
)

@Serializable
data class SellingUnitUpdate(
    val unitType: String,
    val packets: Int,
    val sellingPrice: Double
)

@Serializable
data class SubProduct2(
    val id: Long? = null,
    val mrp: Double,
    val sellingUnits: List<SellingUnit> = emptyList()
)

@Serializable
data class Product2(
    val id: Long? = null,
    val name: String,
    val company: String,
    val subProducts: List<SubProduct2> = emptyList()
)

@Serializable
data class ProductIdentity(
    val productId: Long,
    val productName: String,
    val companyName: String
)

@Serializable
data class SellingUnitRequest(
    val unitType: UnitType,
    val packets: Int,
    val sellingPrice: Double
)

@Serializable
data class SubProductRequest(
    val mrp: Double,
    val sellingUnits: List<SellingUnitRequest>
)

@Serializable
data class AddProductForShopRequest(
    val productName: String,
    val company: String,
    val subProducts: List<SubProductRequest>
)

@Serializable
data class AddProductForShopResponse(
    val message: String,
    val product: Product2
)

@Serializable
data class AddSubProductsForShopRequest(
    val subProducts: List<SubProductRequest>
)

@Serializable
data class AddSubProductsForShopResponse(
    val message: String,
    val addedSubProducts: List<SubProduct2>? = emptyList()
)
