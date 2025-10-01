package org.parimal.auth.dtos

import com.example.e_wholesaler.auth.utils.UserType

data class TokenState(
    val accessToken: String?,
    val refreshToken: String?,
    val userType: UserType?,
    val userTypeId: Long?
)
