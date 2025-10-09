package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
data class SubProductAddResponse(
    val productId: Long,
    val idToPriceMap: Map<Long, Double>
)
