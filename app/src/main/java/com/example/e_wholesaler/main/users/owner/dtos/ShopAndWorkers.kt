package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ShopAndWorkers(
    val shopId: Long,
    val workerList: List<Worker>
)
