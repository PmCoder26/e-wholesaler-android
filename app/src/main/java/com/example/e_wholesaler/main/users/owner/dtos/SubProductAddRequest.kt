package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable


@Serializable
data class SubProductAddRequest(
    val productName: String,
    val category: String,
    val company: String,
    val mrpToSelling: Map<Double, QuantityToSellingPrice>,
    val shopId: Long,
)