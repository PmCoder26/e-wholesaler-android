package com.example.e_wholesaler.main.users.owner.dtos

import com.example.e_wholesaler.auth.dtos.Gender
import com.example.e_wholesaler.auth.dtos.UserType


data class OwnerDetails(
    val id: Long,
    val name: String,
    val userType: UserType,
    val gender: Gender,
    val mobNo: String,
    val address: String,
    val city: String,
    val state: String
)
