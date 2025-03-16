package org.parimal.auth.dtos

import com.example.e_wholesaler.auth.dtos.UserType
import kotlinx.serialization.Serializable


@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userType: UserType
)