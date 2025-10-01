package com.example.e_wholesaler.main.users.owner.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.main.users.owner.dtos.Product
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.dtos.SubProduct
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.ProductSortType

val BackgroundScreen = Color(0xFFF0F4F8)
val TableHeaderBackground = Color(0xFFE3F2FD)
val TableHeaderText = Color(0xFF0D47A1)
val PriceColor = TopBarBlue


@Preview(showBackground = true)
@Composable
fun ShopProductsScreenPreview() {
    val sampleShops = listOf(
        Shop(
            id = 1,
            name = "Sagar Traders",
            gstNo = "GST1",
            address = "Addr1",
            city = "City1",
            state = "State1",
            createdAt = ""
        ),
    )
    val sampleProductsData = listOf(
        Product(
            name = "Lux Soap",
            category = "Soap",
            company = "Unilever",
            shopSubProducts = listOf(
                // Assuming MRP is for the pack. If MRP is per piece, adjust calculation.
                // For preview, let's assume MRP in SubProduct is total MRP for that quantity.
                SubProduct(
                    id = 101,
                    mrp = 5.0 * 1,
                    sellingPrice = 5.0,
                    quantity = 1,
                    stock = 500
                ), // Single Rs. 5 pc
                SubProduct(
                    id = 102,
                    mrp = 10.0 * 1,
                    sellingPrice = 10.0,
                    quantity = 1,
                    stock = 300
                ), // Single Rs. 10 pc
                SubProduct(
                    id = 103,
                    mrp = 35.0,
                    sellingPrice = 32.0,
                    quantity = 1,
                    stock = 100
                ), // "Large Pack" single item
                SubProduct(
                    id = 104,
                    mrp = 50.0,
                    sellingPrice = 45.0,
                    quantity = 5,
                    stock = 50
                ) // Pack of 5 (e.g. 5x10 MRP)
            )
        ),
        Product(
            name = "Parle-G Biscuits",
            category = "Biscuit",
            company = "Parle",
            shopSubProducts = listOf(
                SubProduct(
                    id = 201,
                    mrp = 5.0,
                    sellingPrice = 5.0,
                    quantity = 1,
                    stock = 1000
                ), // Small pack
                SubProduct(
                    id = 202,
                    mrp = 10.0,
                    sellingPrice = 9.0,
                    quantity = 1,
                    stock = 200
                ), // Medium pack
                SubProduct(
                    id = 203,
                    mrp = 60.0,
                    sellingPrice = 54.0,
                    quantity = 12,
                    stock = 100
                ) // Pack of 12 (Rs.5/pc MRP)
            )
        ),
        Product(
            name = "Balaji Wafers - Masala",
            category = "Snacks",
            company = "Balaji",
            shopSubProducts = listOf(
                SubProduct(id = 301, mrp = 5.0, sellingPrice = 5.0, quantity = 1, stock = 300),
                SubProduct(id = 302, mrp = 10.0, sellingPrice = 10.0, quantity = 1, stock = 200),
                SubProduct(id = 303, mrp = 30.0, sellingPrice = 28.0, quantity = 1, stock = 100)
            )
        )
    )
    ShopProductsScreen(
        shops = sampleShops,
        initialSelectedShop = sampleShops.first(),
        products = sampleProductsData,
        onBackClicked = {},
        onShopSelected = {},
        onAddProductClicked = {},
        onFilterChange = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopProductsScreen(
    shops: List<Shop>,
    initialSelectedShop: Shop?,
    products: List<Product>,
    onBackClicked: () -> Unit,
    onShopSelected: (Shop) -> Unit,
    onAddProductClicked: () -> Unit,
    onFilterChange: (ProductSortType) -> Unit
) {
    var selectedShop by remember { mutableStateOf(initialSelectedShop) }
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf(ProductSortType.NAME) }
    var shopDropdownExpanded by remember { mutableStateOf(false) }
    var expandedProductKey by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products", color = IconColorWhite) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = IconColorWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarBlue)
            )
        },
        containerColor = BackgroundScreen
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = shopDropdownExpanded,
                onExpandedChange = { shopDropdownExpanded = !shopDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedShop?.name ?: "Select Shop",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shopDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TopBarBlue,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = CardBackgroundWhite,
                        unfocusedContainerColor = CardBackgroundWhite,
                        disabledContainerColor = CardBackgroundWhite
                    ),
                )
                ExposedDropdownMenu(
                    expanded = shopDropdownExpanded,
                    onDismissRequest = { shopDropdownExpanded = false }
                ) {
                    shops.forEach { shop ->
                        DropdownMenuItem(
                            text = { Text(shop.name) },
                            onClick = {
                                selectedShop = shop
                                onShopSelected(shop)
                                shopDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search products...", color = HintGray) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TopBarBlue,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = CardBackgroundWhite,
                    unfocusedContainerColor = CardBackgroundWhite,
                    disabledContainerColor = CardBackgroundWhite
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddProductClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Product", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterButton("Name", activeFilter.name) {
                    activeFilter = ProductSortType.NAME
                    onFilterChange(activeFilter)
                }
                FilterButton("Category", activeFilter.name) {
                    activeFilter = ProductSortType.CATEGORY
                    onFilterChange(activeFilter)
                }
                FilterButton("Company", activeFilter.name) {
                    activeFilter = ProductSortType.COMPANY
                    onFilterChange(activeFilter)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProductTableHeader()
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(
                            products.filteredProducts(searchQuery),
                            key = { product -> product.name }) { product ->
                            ProductCardItem(
                                product = product,
                                isExpanded = product.name == expandedProductKey,
                                onToggleExpand = {
                                    expandedProductKey =
                                        if (product.name == expandedProductKey) null else product.name
                                },
                                onInfoButtonClick = { }
                            )
                            HorizontalDivider(
                                thickness = DividerDefaults.Thickness,
                                color = BorderColor.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun List<Product>.filteredProducts(searchQuery: String): List<Product> {
    return this.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.company.contains(searchQuery, ignoreCase = true)
    }
}

@Composable
fun FilterButton(text: String, activeFilter: String, onClick: () -> Unit) {
    val isActive = text.lowercase() == activeFilter.lowercase()
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) TopBarBlue else Color.White,
            contentColor = if (isActive) Color.White else TextPrimary
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, fontSize = 16.sp)
    }
}

@Composable
fun ProductTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TableHeaderBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Product Name",
            modifier = Modifier.weight(1.9f),
            fontWeight = FontWeight.SemiBold,
            color = TableHeaderText,
            fontSize = 17.sp
        )
        Text(
            "Category",
            modifier = Modifier.weight(1.5f),
            fontWeight = FontWeight.SemiBold,
            color = TableHeaderText,
            fontSize = 17.sp
        )
        Text(
            "Company",
            modifier = Modifier.weight(1.5f),
            fontWeight = FontWeight.SemiBold,
            color = TableHeaderText,
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.width(24.dp))
    }
    HorizontalDivider(thickness = DividerDefaults.Thickness, color = BorderColor)
}

@Composable
fun ProductCardItem(
    product: Product,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onInfoButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    product.name,
                    modifier = Modifier.weight(2.5f),
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    product.category,
                    modifier = Modifier.weight(1.5f),
                    color = TextSecondary,
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    product.company,
                    modifier = Modifier.weight(1.5f),
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }

            IconButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Product Info",
                    tint = TextSecondary
                )
            }
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 200),
                initialOffsetY = { -it / 2 })
                    + fadeIn(animationSpec = tween(durationMillis = 200)),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 200),
                targetOffsetY = { -it / 2 })
                    + fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            ExpandedSubProductsView(subProducts = product.shopSubProducts)
        }
    }
}

@Composable
fun ExpandedSubProductsView(subProducts: List<SubProduct>) {
    if (subProducts.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .background(CardBackgroundWhite.copy(alpha = 0.95f))
                .padding(start = 24.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
        ) {
            itemsIndexed(
                subProducts,
                key = { index, subProduct -> subProduct.id }) { index, subProduct ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MRP/Pc: ₹${subProduct.mrp}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = PriceColor,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Selling: ₹${subProduct.sellingPrice}",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = TextSecondary,
                            modifier = Modifier.weight(1.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quantity: ${subProduct.quantity}",
                            fontSize = 15.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                        )
                    }
                }

                if (index < subProducts.size - 1) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = BorderColor.copy(alpha = 0.3f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    } else {
        Text(
            text = "No specific pack details available.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
