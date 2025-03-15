package org.parimal.auth.dtos

data class TokenState(
    val accessToken: String?,
    val refreshToken: String?,
    val userType: String?
)
