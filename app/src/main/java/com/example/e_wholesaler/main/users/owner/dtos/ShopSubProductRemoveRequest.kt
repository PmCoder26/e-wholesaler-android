package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ShopSubProductRemoveRequest(
    val shopSubProductId: Long,
    val shopId: Long
)
