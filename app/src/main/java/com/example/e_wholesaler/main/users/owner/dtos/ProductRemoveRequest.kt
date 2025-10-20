package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ProductRemoveRequest(
    val shopId: Long,
    val productName: String
)
