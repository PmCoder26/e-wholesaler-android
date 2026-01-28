package com.example.e_wholesaler.main.users.owner.viewmodels.utils

import com.example.e_wholesaler.main.users.owner.dtos.ProductIdentity
import com.example.e_wholesaler.main.users.owner.dtos.Shop

data class ShopProductsState(
    val shopId: Long = -1,
    val currentShop: Shop? = Shop(),
    val products: List<ProductIdentity> = emptyList(),
    val sortType: ProductSortType = ProductSortType.NAME
)
