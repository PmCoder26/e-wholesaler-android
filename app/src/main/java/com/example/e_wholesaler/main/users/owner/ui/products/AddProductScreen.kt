package com.example.e_wholesaler.main.users.owner.ui.products

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.main.users.owner.dtos.Product
import com.example.e_wholesaler.main.users.owner.dtos.SubProduct
import com.example.e_wholesaler.main.users.owner.ui.CardBackgroundWhite
import com.example.e_wholesaler.main.users.owner.ui.IconColorWhite
import com.example.e_wholesaler.main.users.owner.ui.TopBarBlue
import java.util.UUID

private data class SubProductFormState(
    val id: UUID = UUID.randomUUID(),
    var mrp: String = "",
    var sellingPrice: String = "",
    var quantity: String = "",
    var stock: String = ""
)

@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    AddProductScreen(
        onBackClicked = {},
        onSaveProduct = { _ -> }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBackClicked: () -> Unit,
    onSaveProduct: (Product) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("") }
    var productCompany by remember { mutableStateOf("") }
    // Start with one sub-product form for better UX
    var subProductForms by remember { mutableStateOf(emptyList<SubProductFormState>()) }

    val isFormValid by remember {
        derivedStateOf {
            productName.isNotBlank() &&
            productCategory.isNotBlank() &&
            productCompany.isNotBlank() &&
            subProductForms.isNotEmpty() &&
            subProductForms.all {
                it.mrp.isNotBlank() && it.sellingPrice.isNotBlank() && it.quantity.isNotBlank() && it.stock.isNotBlank()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Product", color = IconColorWhite) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = IconColorWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarBlue)
            )
        },
        containerColor = BackgroundScreen,
        bottomBar = {
                Button(
                    onClick = {
                        val finalSubProducts = subProductForms.map {
                            SubProduct(
                                mrp = it.mrp.toDoubleOrNull() ?: 0.0,
                                sellingPrice = it.sellingPrice.toDoubleOrNull() ?: 0.0,
                                quantity = it.quantity.toIntOrNull() ?: 0,
                                stock = it.stock.toLongOrNull() ?: 0L
                            )
                        }
                        val newProduct = Product(
                            name = productName,
                            category = productCategory,
                            company = productCompany,
                            shopSubProducts = finalSubProducts
                        )
                        onSaveProduct(newProduct)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    Text("Add Product", color = Color.White, fontSize = 16.sp)
                }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text("Product Information", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = productCategory, onValueChange = { productCategory = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = productCompany, onValueChange = { productCompany = it }, label = { Text("Company / Brand") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Product Variants", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 4.dp))
            }

            itemsIndexed(subProductForms, key = { _, item -> item.id }) { index, formState ->
                SubProductInputCard(
                    formState = formState,
                    onStateChange = { newState ->
                        subProductForms =
                            subProductForms.toMutableList().also { it[index] = newState }
                    },
                    onRemove = {
                        if (subProductForms.isNotEmpty()) {
                            subProductForms = subProductForms.filterNot { it.id == formState.id }
                        }
                    },
                    canBeRemoved = subProductForms.isNotEmpty(),
                    variantNumber = index + 1
                )
            }

            item {
                Button(
                    onClick = { subProductForms = subProductForms + SubProductFormState() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TopBarBlue.copy(alpha = 0.1f),
                        contentColor = TopBarBlue
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Variant")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (subProductForms.isEmpty()) "Add Variant" else "Add Another Variant")
                }
            }
        }
    }
}

@Composable
private fun SubProductInputCard(
    formState: SubProductFormState,
    onStateChange: (SubProductFormState) -> Unit,
    onRemove: () -> Unit,
    canBeRemoved: Boolean,
    variantNumber: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Variant #$variantNumber", style = MaterialTheme.typography.titleMedium)
                if (canBeRemoved) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove Variant", tint = DeleteRed)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = formState.mrp,
                        onValueChange = { onStateChange(formState.copy(mrp = it)) },
                        label = { Text("MRP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = formState.sellingPrice,
                        onValueChange = { onStateChange(formState.copy(sellingPrice = it)) },
                        label = { Text("Selling Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = formState.quantity,
                        onValueChange = { onStateChange(formState.copy(quantity = it)) },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = formState.stock,
                        onValueChange = { onStateChange(formState.copy(stock = it)) },
                        label = { Text("Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
