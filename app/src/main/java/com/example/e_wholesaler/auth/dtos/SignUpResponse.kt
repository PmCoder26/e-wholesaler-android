package org.parimal.auth.dtos

import kotlinx.serialization.Serializable


@Serializable
data class SignUpResponse (
    val id: Long,
    val username: String
)