package com.example.e_wholesaler.main.users.owner.viewmodels.utils

import com.example.e_wholesaler.main.users.owner.dtos.Product

data class ShopProductsState(
    val shopId: Long = -1,
    val products: List<Product> = emptyList(),
    val sortType: ProductSortType = ProductSortType.NAME
)
