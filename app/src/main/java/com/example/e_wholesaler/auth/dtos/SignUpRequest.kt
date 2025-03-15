package org.parimal.auth.dtos

import kotlinx.serialization.Serializable


@Serializable
data class SignUpRequest(
    val username: String,
    val password: String
)