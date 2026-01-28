package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
data class SubProduct(
    val id: Long = -1,
    val mrp: Double,
    val sellingPrice: Double,
    val quantity: Int,
    val stock: Long,
)