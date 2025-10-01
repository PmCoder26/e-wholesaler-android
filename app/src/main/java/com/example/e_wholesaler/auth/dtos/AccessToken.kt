package com.example.e_wholesaler.auth.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AccessToken(
    val accessToken: String
)
