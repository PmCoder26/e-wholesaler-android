package org.parimal.auth.dtos

import com.example.e_wholesaler.auth.utils.Gender
import com.example.e_wholesaler.auth.utils.UserType
import kotlinx.serialization.Serializable


@Serializable
data class SignUpRequest(
    val username: String,
    val password: String,
    val userType: UserType,
    val name: String,
    val gender: Gender,
    val mobNo: String,
    val address: String,
    val city: String,
    val state: String
)