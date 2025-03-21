package org.parimal.auth.dtos

import com.example.e_wholesaler.auth.dtos.UserType
import kotlinx.coroutines.flow.MutableStateFlow

data class TokenState(
    val accessToken: String?,
    val refreshToken: String?,
    val userType: UserType?,
    val userTypeId: Long?
)
