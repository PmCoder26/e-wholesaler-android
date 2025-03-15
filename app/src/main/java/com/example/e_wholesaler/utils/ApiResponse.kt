package org.parimal.utils

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val error: ApiError? = null,
    val timeStamp: String
)