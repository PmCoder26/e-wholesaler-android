package org.parimal.auth.dtos

import kotlinx.serialization.Serializable


@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userType: String
)