package com.example.e_wholesaler.auth.dtos

import kotlinx.serialization.Serializable

@Serializable
enum class UserType {
    OWNER,
    WORKER,
    CUSTOMER,
}