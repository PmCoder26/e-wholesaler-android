package com.example.e_wholesaler.main.users.owner.dtos

import com.example.e_wholesaler.auth.utils.Gender
import kotlinx.serialization.Serializable

@Serializable
data class Worker(
    val id: Long,
    val name: String,
    val gender: Gender,
    val mobNo: String,
    val address: String,
    val city: String,
    val state: String,
    val shopId: Long,
    val salary: Double
)
