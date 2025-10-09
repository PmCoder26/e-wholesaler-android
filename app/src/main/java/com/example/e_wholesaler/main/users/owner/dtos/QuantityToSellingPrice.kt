package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
data class QuantityToSellingPrice(
    val quantity: Int,
    val sellingPrice: Double,
    val stock: Long
)
