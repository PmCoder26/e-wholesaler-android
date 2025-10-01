package com.example.e_wholesaler.main.users.owner.dtos

import com.example.e_wholesaler.auth.utils.Gender
import kotlinx.serialization.Serializable


@Serializable
data class OwnerDetails(
    val id: Long,
    val name: String,
    val gender: Gender,
    val mobNo: Long,
    val address: String,
    val city: String,
    val state: String
)
