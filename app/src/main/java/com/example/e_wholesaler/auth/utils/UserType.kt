package com.example.e_wholesaler.auth.utils

import kotlinx.serialization.Serializable

@Serializable
enum class UserType {
    OWNER,
    WORKER,
    CUSTOMER,
    NONE
}