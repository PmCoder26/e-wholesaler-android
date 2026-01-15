package com.example.e_wholesaler.main.users.owner.dtos

import com.example.e_wholesaler.auth.utils.Gender
import kotlinx.serialization.Serializable

@Serializable
data class Worker(
    val id: Long = -1,
    val name: String = "Worker Name",
    val gender: Gender = Gender.MALE,
    val mobNo: String = "Mobile Number",
    val address: String = "Address",
    val city: String = "City",
    val state: String = "State",
    val shopId: Long = -1,
    val salary: Double = 0.0
)
