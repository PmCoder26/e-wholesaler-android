package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Shop(
    val id: Long,
    val name: String,
    val gstNo: String,
    val address: String,
    val city: String,
    val state: String,
    val createdAt: String,
)