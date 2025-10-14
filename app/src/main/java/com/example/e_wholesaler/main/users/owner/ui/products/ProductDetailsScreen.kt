package com.example.e_wholesaler.main.users.owner.ui.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.main.users.owner.dtos.Product
import com.example.e_wholesaler.main.users.owner.dtos.SubProduct
import com.example.e_wholesaler.main.users.owner.dtos.existsByMrp
import com.example.e_wholesaler.main.users.owner.ui.CardBackgroundWhite
import com.example.e_wholesaler.main.users.owner.ui.IconColorWhite
import com.example.e_wholesaler.main.users.owner.ui.TextPrimary
import com.example.e_wholesaler.main.users.owner.ui.TextSecondary
import com.example.e_wholesaler.main.users.owner.ui.TopBarBlue
import com.example.e_wholesaler.main.users.owner.ui.owner.showToast

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
                verticalArrangement = Arrangement.spacedBy(24.dp),
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