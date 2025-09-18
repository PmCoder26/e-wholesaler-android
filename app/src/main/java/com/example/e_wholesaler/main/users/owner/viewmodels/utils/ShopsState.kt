package com.example.e_wholesaler.main.users.owner.viewmodels.utils

import com.example.e_wholesaler.main.users.owner.dtos.Shop

data class ShopsState(
    val shops: List<Shop> = emptyList(),
    val sortType: SortType = SortType.NAME
)