package com.example.e_wholesaler.main.users.owner.dtos

data class ShopsState(
    val shops: List<Shop> = emptyList(),
    val sortType: SortType = SortType.NAME
)