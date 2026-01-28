package com.example.e_wholesaler.main.users.owner.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.main.users.owner.dtos.ProductIdentity
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.ui.shops.BorderColor
import com.example.e_wholesaler.main.users.owner.ui.shops.CardBackgroundWhite
import com.example.e_wholesaler.main.users.owner.ui.shops.HintGray
import com.example.e_wholesaler.main.users.owner.ui.shops.IconColorWhite
import com.example.e_wholesaler.main.users.owner.ui.shops.TextPrimary
import com.example.e_wholesaler.main.users.owner.ui.shops.TextSecondary
import com.example.e_wholesaler.main.users.owner.ui.shops.TopBarBlue
import com.example.e_wholesaler.main.users.owner.ui.workers.BackgroundScreen
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.ProductSortType

@Preview(showBackground = true)
@Composable
fun ShopProductsScreenPreview() {
    val sampleShops = listOf(
        Shop(id = 1, name = "Sagar Traders")
    )
    val sampleProducts = listOf(
        ProductIdentity(productId = 1, productName = "Lux Soap", companyName = "Unilever"),
        ProductIdentity(productId = 2, productName = "Parle-G", companyName = "Parle")
    )
    ShopProductsScreen(
        shops = sampleShops,
        initialSelectedShop = sampleShops.first(),
        products = sampleProducts,
        onBackClicked = {},
        onShopSelected = {},
        onAddProductClicked = {},
        onFilterChange = {},
        onInfoButtonClick = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopProductsScreen(
    shops: List<Shop>,
    initialSelectedShop: Shop?,
    products: List<ProductIdentity>,
    onBackClicked: () -> Unit,
    onShopSelected: (Shop) -> Unit,
    onAddProductClicked: () -> Unit,
    onFilterChange: (ProductSortType) -> Unit,
    onInfoButtonClick: (ProductIdentity) -> Unit
) {
    var selectedShop by remember { mutableStateOf(initialSelectedShop) }
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf(ProductSortType.NAME) }
    var shopDropdownExpanded by remember { mutableStateOf(false) }

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
                    textStyle = TextStyle(fontSize = 16.sp)
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
                    activeFilter = ProductSortType.NAME; onFilterChange(activeFilter)
                }
                FilterButton("Company", activeFilter.name) {
                    activeFilter = ProductSortType.COMPANY; onFilterChange(activeFilter)
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
                            products.filter { it.productName.contains(searchQuery, true) },
                            key = { it.productId }) { product ->
                            ProductCardItem(
                                product = product,
                                onInfoButtonClick = { onInfoButtonClick(product) }
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

@Composable
fun FilterButton(text: String, activeFilter: String, onClick: () -> Unit) {
    val isActive = text.equals(activeFilter, ignoreCase = true)
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
            .background(Color(0xFFE3F2FD))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Product Name",
            modifier = Modifier.weight(2f),
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0D47A1),
            fontSize = 17.sp
        )
        Text(
            "Company",
            modifier = Modifier.weight(1.5f),
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0D47A1),
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.width(48.dp))
    }
    HorizontalDivider(thickness = DividerDefaults.Thickness, color = BorderColor)
}

@Composable
fun ProductCardItem(
    product: ProductIdentity,
    onInfoButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            product.productName,
            modifier = Modifier.weight(2f),
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            product.companyName,
            modifier = Modifier.weight(1.5f),
            color = TextSecondary,
            fontSize = 16.sp
        )
        IconButton(onClick = onInfoButtonClick) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Product Info",
                tint = TopBarBlue
            )
        }
    }
}
