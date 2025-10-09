package com.example.e_wholesaler.main.users.owner.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.main.users.owner.dtos.Product
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.dtos.SubProduct
import com.example.e_wholesaler.main.users.owner.dtos.existsByMrp
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.ProductSortType


val BackgroundScreen = Color(0xFFF0F4F8)
val TableHeaderBackground = Color(0xFFE3F2FD)
val TableHeaderText = Color(0xFF0D47A1)
val PriceColor = TopBarBlue
val StockGreen = Color(0xFF4CAF50)
val StockRed = Color(0xFFF44336)
val DeleteRed = Color(0xFFE53935)


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
            shopSubProducts = mutableListOf(
                SubProduct(id = 101, mrp = 5.0 * 1, sellingPrice = 5.0, quantity = 1, stock = 500),
                SubProduct(
                    id = 102,
                    mrp = 10.0 * 1,
                    sellingPrice = 10.0,
                    quantity = 1,
                    stock = 300
                ),
                SubProduct(id = 103, mrp = 35.0, sellingPrice = 32.0, quantity = 1, stock = 100),
                SubProduct(id = 104, mrp = 50.0, sellingPrice = 45.0, quantity = 5, stock = 50)
            )
        ),
        Product(
            name = "Parle-G Biscuits",
            category = "Biscuit",
            company = "Parle",
            shopSubProducts = mutableListOf(
                SubProduct(id = 201, mrp = 5.0, sellingPrice = 5.0, quantity = 1, stock = 1000),
                SubProduct(id = 202, mrp = 10.0, sellingPrice = 9.0, quantity = 1, stock = 200),
                SubProduct(id = 203, mrp = 60.0, sellingPrice = 54.0, quantity = 12, stock = 100)
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
        onFilterChange = {},
        onInfoButtonClick = {}
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
    onFilterChange: (ProductSortType) -> Unit,
    onInfoButtonClick: (Product) -> Unit
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
                    activeFilter = ProductSortType.NAME; onFilterChange(activeFilter)
                }
                FilterButton("Category", activeFilter.name) {
                    activeFilter = ProductSortType.CATEGORY; onFilterChange(activeFilter)
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
                            products.filter { it.name.contains(searchQuery, true) },
                            key = { it.name }) { product ->
                            ProductCardItem(
                                product = product,
                                isExpanded = product.name == expandedProductKey,
                                onToggleExpand = {
                                    expandedProductKey =
                                        if (product.name == expandedProductKey) null else product.name
                                },
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
    Column(modifier = Modifier.fillMaxWidth()) {
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
            IconButton(onClick = onInfoButtonClick) {
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
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    Spring.StiffnessLow
                ),
                initialOffsetY = { -it / 2 }) + fadeIn(animationSpec = tween(durationMillis = 200)),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 200),
                targetOffsetY = { -it / 2 }) + fadeOut(animationSpec = tween(durationMillis = 200))
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
                key = { _, subProduct -> subProduct.id }) { index, subProduct ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "MRP/Pc: ₹${subProduct.mrp}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = PriceColor,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Selling: ₹${subProduct.sellingPrice}",
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
                            "Quantity: ${subProduct.quantity}",
                            fontSize = 15.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
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
            "No specific pack details available.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailsScreenPreview() {
    val sampleProduct = Product(
        name = "Lux Soap",
        category = "Soap",
        company = "Unilever",
        shopSubProducts = mutableListOf(
            SubProduct(id = 101, mrp = 10.0, sellingPrice = 10.0, quantity = 1, stock = 150),
            SubProduct(id = 102, mrp = 50.0, sellingPrice = 45.0, quantity = 5, stock = 80),
            SubProduct(id = 103, mrp = 100.0, sellingPrice = 85.0, quantity = 10, stock = 0)
        )
    )
    ProductDetailsScreen(
        product = sampleProduct,
        onBackClicked = {},
        onAddSubProductConfirm = { subProduct -> },
        onEditSubProductConfirm = { _ -> },
        onDeleteSubProductConfirm = { _ -> }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product,
    onBackClicked: () -> Unit,
    onAddSubProductConfirm: (SubProduct) -> Unit,
    onEditSubProductConfirm: (SubProduct) -> Unit,
    onDeleteSubProductConfirm: (SubProduct) -> Unit
) {
    var subProductToDelete by remember { mutableStateOf<SubProduct?>(null) }
    var showAddVariantDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product", color = IconColorWhite) },
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
        containerColor = BackgroundScreen,
        bottomBar = {
            Button(
                onClick = { showAddVariantDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Variant", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Variant", color = Color.White, fontSize = 16.sp)
            }
        }
    ) { paddingValues ->
        if (showAddVariantDialog) {
            AddVariantDialog(
                onDismissRequest = { showAddVariantDialog = false },
                onConfirm = { subProduct ->
                    val doesSubProductExists = product.shopSubProducts.existsByMrp(subProduct.mrp)
                    if (doesSubProductExists) showToast(context, "Product variant already exists")
                    else onAddSubProductConfirm(subProduct)
                    showAddVariantDialog = false
                }
            )
        }

        subProductToDelete?.let {
            DeleteConfirmationDialog(
                subProduct = it,
                onConfirm = { onDeleteSubProductConfirm(it); subProductToDelete = null },
                onDismiss = { subProductToDelete = null }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            product.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Category: ${product.category}",
                            fontSize = 16.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Company: ${product.company}",
                            fontSize = 16.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            items(product.shopSubProducts, key = { it.id }) { subProduct ->
                SubProductCard(
                    subProduct = subProduct,
                    onEditConfirm = onEditSubProductConfirm,
                    onDeleteClicked = { subProductToDelete = subProduct }
                )
            }
        }
    }
}

@Composable
fun SubProductCard(
    subProduct: SubProduct,
    onEditConfirm: (SubProduct) -> Unit,
    onDeleteClicked: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Product info column takes the main space
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Quantity ${subProduct.quantity}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoColumn(
                        label = "MRP",
                        value = "₹${subProduct.mrp}",
                        modifier = Modifier.weight(1f)
                    )
                    InfoColumn(
                        label = "Selling Price",
                        value = "₹${subProduct.sellingPrice}",
                        valueColor = PriceColor,
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Stock Status", fontSize = 14.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val stockStatusColor =
                                if (subProduct.stock > 0) StockGreen else StockRed
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(stockStatusColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (subProduct.stock > 0) "In Stock" else "Out of Stock",
                                color = stockStatusColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    InfoColumn(
                        label = "Available Units",
                        value = "${subProduct.stock}",
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    )
                }

                // Edit form below product info
                AnimatedVisibility(visible = isEditing) {
                    Spacer(modifier = Modifier.height(12.dp))
                    EditSubProductForm(
                        subProduct = subProduct,
                        onSaveConfirm = {
                            onEditConfirm(it)
                            isEditing = false
                        }
                    )
                }
            }

            // Buttons column on the right
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { isEditing = !isEditing },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isEditing) TextSecondary.copy(alpha = 0.1f)
                            else TopBarBlue.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                        contentDescription = if (isEditing) "Cancel" else "Edit",
                        tint = if (isEditing) TextSecondary else TopBarBlue
                    )
                }

                IconButton(
                    onClick = onDeleteClicked,
                    enabled = !isEditing,
                    modifier = Modifier
                        .size(40.dp)
                        .background(DeleteRed.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = DeleteRed
                    )
                }
            }
        }
    }
}

@Composable
fun AddVariantDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (SubProduct) -> Unit
) {
    var mrp by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    val isFormValid =
        mrp.isNotBlank() && sellingPrice.isNotBlank() && quantity.isNotBlank() && stock.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Add New Variant") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = mrp,
                    onValueChange = { mrp = it },
                    label = { Text("MRP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = sellingPrice,
                    onValueChange = { sellingPrice = it },
                    label = { Text("Selling Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val mrpDouble = mrp.toDoubleOrNull() ?: 0.0
                    val spDouble = sellingPrice.toDoubleOrNull() ?: 0.0
                    val qtyInt = quantity.toIntOrNull() ?: 0
                    val stockLong = stock.toLongOrNull() ?: 0L

                    onConfirm(
                        SubProduct(
                            mrp = mrpDouble,
                            sellingPrice = spDouble,
                            quantity = qtyInt,
                            stock = stockLong
                        )
                    )
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditSubProductForm(
    subProduct: SubProduct,
    onSaveConfirm: (SubProduct) -> Unit
) {
    var mrp by remember { mutableStateOf(subProduct.mrp.toString()) }
    var sellingPrice by remember { mutableStateOf(subProduct.sellingPrice.toString()) }
    var stock by remember { mutableStateOf(subProduct.stock.toString()) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Changes") },
            text = { Text("Are you sure you want to save these changes?") },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedSubProduct = subProduct.copy(
                            mrp = mrp.toDoubleOrNull() ?: subProduct.mrp,
                            sellingPrice = sellingPrice.toDoubleOrNull() ?: subProduct.sellingPrice,
                            stock = stock.toLongOrNull() ?: subProduct.stock
                        )
                        onSaveConfirm(updatedSubProduct)
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue)
                ) { Text("Save") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmDialog = false
                }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Edit Variant Details",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = mrp,
            onValueChange = { mrp = it },
            label = { Text("MRP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = sellingPrice,
            onValueChange = { sellingPrice = it },
            label = { Text("Selling Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { showConfirmDialog = true },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue)
        ) { Text("Save Changes") }
    }
}

@Composable
private fun InfoColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment) {
        Text(label, fontSize = 14.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 20.sp, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DeleteConfirmationDialog(
    subProduct: SubProduct,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete the variant with selling price ₹${subProduct.sellingPrice}?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = DeleteRed)
            ) { Text("Confirm", color = Color.White) }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}