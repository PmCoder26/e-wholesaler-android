package com.example.e_wholesaler.main.users.owner.dtos

import com.example.e_wholesaler.auth.dtos.Gender
import kotlinx.serialization.Serializable

@Serializable
data class Owner(
    val id: Long,
    val name: String,
    val gender: Gender,
    val mobNo: String,
    val address: String,
    val city: String,
    val state: String
)