package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable


@Serializable
data class HomeScreenDetails(
    val shopCount: Int,
    val creatingOrderCount: Long,
    val workerCount: Long,
    val salesAmount: Double
)