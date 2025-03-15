package org.parimal.utils

import kotlinx.serialization.Serializable


@Serializable
data class ApiError (
    val message: String,
    val status: String,
    val subErrors: List<String>
)