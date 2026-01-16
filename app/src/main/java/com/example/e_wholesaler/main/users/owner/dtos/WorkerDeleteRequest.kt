package com.example.e_wholesaler.main.users.owner.dtos

import kotlinx.serialization.Serializable

@Serializable
data class WorkerDeleteRequest(
    val workerId: Long = -1,
    val shopId: Long = -1
)