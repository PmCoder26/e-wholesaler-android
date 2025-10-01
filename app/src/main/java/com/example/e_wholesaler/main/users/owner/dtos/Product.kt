package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val name: String,
    val category: String,
    val company: String,
    val shopSubProducts: List<SubProduct>
)
