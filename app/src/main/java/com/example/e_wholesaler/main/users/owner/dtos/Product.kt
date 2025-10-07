package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val name: String = "Product Name",
    val category: String = "Category",
    val company: String = "Company",
    var shopSubProducts: List<SubProduct> = emptyList()
)