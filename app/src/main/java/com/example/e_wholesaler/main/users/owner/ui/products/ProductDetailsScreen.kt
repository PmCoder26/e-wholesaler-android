package com.example.e_wholesaler.main.users.owner.ui.products

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.main.users.owner.dtos.ProductIdentity
import com.example.e_wholesaler.main.users.owner.dtos.SellingUnit
import com.example.e_wholesaler.main.users.owner.dtos.SellingUnitRequest
import com.example.e_wholesaler.main.users.owner.dtos.SellingUnitUpdate
import com.example.e_wholesaler.main.users.owner.dtos.SubProduct2
import com.example.e_wholesaler.main.users.owner.dtos.UnitType
import com.example.e_wholesaler.main.users.owner.ui.shops.CardBackgroundWhite
import com.example.e_wholesaler.main.users.owner.ui.shops.IconColorWhite
import com.example.e_wholesaler.main.users.owner.ui.shops.TextPrimary
import com.example.e_wholesaler.main.users.owner.ui.shops.TextSecondary
import com.example.e_wholesaler.main.users.owner.ui.shops.TopBarBlue
import com.example.e_wholesaler.main.users.owner.ui.workers.BackgroundScreen
import com.example.e_wholesaler.main.users.owner.ui.workers.DeleteRed

@Preview(showBackground = true)
@Composable
fun ProductDetailsScreenPreview() {
    val sampleProduct = ProductIdentity(
        productId = 1,
        productName = "Lux Soap",
        companyName = "Unilever"
    )
    val sampleSubProducts = listOf(
        SubProduct2(
            id = 101,
            mrp = 50.0,
            sellingUnits = listOf(
                SellingUnit(id = 1, unitType = UnitType.BOX, packets = 10, sellingPrice = 450.0),
                SellingUnit(id = 2, unitType = UnitType.PIECE, packets = 1, sellingPrice = 48.0)
            )
        )
    )
    ProductDetailsScreen(
        product = sampleProduct,
        subProducts = sampleSubProducts,
        onBackClicked = {},
        onAddSubProductConfirm = {},
        onDeleteSubProductConfirm = {},
        onDeleteProductConfirm = {},
        onDeleteSellingUnitConfirm = { _, _ -> },
        onUpdateSellingUnitConfirm = { _, _, _ -> },
        onAddSellingUnitConfirm = { _, _ -> }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: ProductIdentity,
    subProducts: List<SubProduct2>,
    onBackClicked: () -> Unit,
    onAddSubProductConfirm: (SubProduct2) -> Unit,
    onDeleteSubProductConfirm: (SubProduct2) -> Unit,
    onDeleteProductConfirm: (ProductIdentity) -> Unit,
    onDeleteSellingUnitConfirm: (SubProduct2, SellingUnit) -> Unit,
    onUpdateSellingUnitConfirm: (SubProduct2, SellingUnit, SellingUnitUpdate) -> Unit,
    onAddSellingUnitConfirm: (SubProduct2, SellingUnitRequest) -> Unit
) {
    var subProductToDelete by remember { mutableStateOf<SubProduct2?>(null) }
    var subProductToEdit by remember { mutableStateOf<SubProduct2?>(null) }
    var sellingUnitToDelete by remember { mutableStateOf<Pair<SubProduct2, SellingUnit>?>(null) }
    var sellingUnitToEdit by remember { mutableStateOf<Pair<SubProduct2, SellingUnit>?>(null) }
    var variantForNewUnit by remember { mutableStateOf<SubProduct2?>(null) }
    var showAddVariantDialog by remember { mutableStateOf(false) }
    var showDeleteProductDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details", color = IconColorWhite) },
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { showAddVariantDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Variant", color = Color.White)
                }
                Button(
                    onClick = { showDeleteProductDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Product", color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        // Dialogs
        if (showDeleteProductDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteProductDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Delete ${product.productName}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteProductConfirm(product); showDeleteProductDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeleteRed)
                    ) { Text("Delete") }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        showDeleteProductDialog = false
                    }) { Text("Cancel") }
                })
        }

        if (showAddVariantDialog) {
            EditVariantDialog(
                title = "Add New Variant",
                subProduct = null,
                onDismiss = { showAddVariantDialog = false },
                onConfirm = { onAddSubProductConfirm(it); showAddVariantDialog = false })
        }

        subProductToDelete?.let { variant ->
            AlertDialog(
                onDismissRequest = { subProductToDelete = null },
                title = { Text("Delete Variant") },
                text = { Text("Delete variant MRP ₹${variant.mrp}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteSubProductConfirm(variant); subProductToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeleteRed)
                    ) { Text("Delete") }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        subProductToDelete = null
                    }) { Text("Cancel") }
                })
        }

        sellingUnitToDelete?.let { (variant, unit) ->
            AlertDialog(
                onDismissRequest = { sellingUnitToDelete = null },
                title = { Text("Delete Unit") },
                text = { Text("Delete ${unit.unitType.name} unit?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteSellingUnitConfirm(
                                variant,
                                unit
                            ); sellingUnitToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DeleteRed)
                    ) { Text("Delete") }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        sellingUnitToDelete = null
                    }) { Text("Cancel") }
                })
        }

        sellingUnitToEdit?.let { (variant, unit) ->
            UpdateSellingUnitDialog(
                unit = unit,
                onDismiss = { sellingUnitToEdit = null },
                onConfirm = { update ->
                    onUpdateSellingUnitConfirm(
                        variant,
                        unit,
                        update
                    ); sellingUnitToEdit = null
                })
        }

        variantForNewUnit?.let { variant ->
            AddSellingUnitDialog(
                variant = variant,
                onDismiss = { variantForNewUnit = null },
                onConfirm = { request ->
                    onAddSellingUnitConfirm(variant, request)
                    variantForNewUnit = null
                }
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            product.productName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(product.companyName, fontSize = 16.sp, color = TextSecondary)
                    }
                }
            }
            item {
                Text(
                    text = "Pricing & Packaging",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TopBarBlue,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(subProducts, key = { it.id ?: 0 }) { subProduct ->
                VariantTableCard(
                    subProduct = subProduct,
                    onDeleteClicked = { subProductToDelete = subProduct },
                    onEditSellingUnit = { unit -> sellingUnitToEdit = subProduct to unit },
                    onDeleteSellingUnit = { unit -> sellingUnitToDelete = subProduct to unit },
                    onAddSellingUnit = { variantForNewUnit = subProduct }
                )
            }
        }
    }
}

@Composable
fun VariantTableCard(
    subProduct: SubProduct2,
    onDeleteClicked: () -> Unit,
    onEditSellingUnit: (SellingUnit) -> Unit,
    onDeleteSellingUnit: (SellingUnit) -> Unit,
    onAddSellingUnit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TopBarBlue.copy(alpha = 0.08f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "MRP: ₹${subProduct.mrp}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TopBarBlue
                )
                IconButton(
                    onClick = onDeleteClicked,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = DeleteRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Unit Type",
                        modifier = Modifier.weight(1.2f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        "Packets",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        "Price",
                        modifier = Modifier.weight(1.2f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    IconButton(onClick = onAddSellingUnit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Unit",
                            tint = TopBarBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                subProduct.sellingUnits.forEach { unit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(unit.unitType.name, modifier = Modifier.weight(1.2f), fontSize = 15.sp)
                        Text("${unit.packets}", modifier = Modifier.weight(1f), fontSize = 15.sp)
                        Text(
                            "₹${unit.sellingPrice}",
                            modifier = Modifier.weight(1.2f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Row(
                            modifier = Modifier.width(80.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { onEditSellingUnit(unit) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = TopBarBlue.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onDeleteSellingUnit(unit) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = DeleteRed.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSellingUnitDialog(
    variant: SubProduct2,
    onDismiss: () -> Unit,
    onConfirm: (SellingUnitRequest) -> Unit
) {
    var packets by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    var unitType by remember { mutableStateOf(UnitType.PIECE) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add Selling Unit to MRP ₹${variant.mrp}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = unitType.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        UnitType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = { unitType = type; expanded = false })
                        }
                    }
                }
                OutlinedTextField(
                    value = packets,
                    onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) packets = it },
                    label = { Text("Packets") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null || it == ".") price = it
                    },
                    label = { Text("Selling Price") },
                    placeholder = { Text("Enter Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        SellingUnitRequest(
                            unitType,
                            packets.toIntOrNull() ?: 1,
                            price.toDoubleOrNull() ?: 0.0
                        )
                    )
                },
                enabled = packets.isNotBlank() && price.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue)
            ) { Text("Add Unit") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateSellingUnitDialog(
    unit: SellingUnit,
    onDismiss: () -> Unit,
    onConfirm: (SellingUnitUpdate) -> Unit
) {
    var packets by remember { mutableStateOf(unit.packets.toString()) }
    var price by remember { mutableStateOf(unit.sellingPrice.toString()) }
    var unitType by remember { mutableStateOf(unit.unitType) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Unit", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = unitType.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        UnitType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = { unitType = type; expanded = false })
                        }
                    }
                }
                OutlinedTextField(
                    value = packets,
                    onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) packets = it },
                    label = { Text("Packets") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null || it == ".") price = it
                    },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val update = SellingUnitUpdate(
                        unitType = unitType.name,
                        packets = packets.toInt(),
                        sellingPrice = price.toDouble()
                    )
                    onConfirm(update)
                },
                enabled = packets.isNotBlank() && price.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue)
            ) { Text("Update") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVariantDialog(
    title: String = "Edit Variant",
    subProduct: SubProduct2?,
    onDismiss: () -> Unit,
    onConfirm: (SubProduct2) -> Unit
) {
    var mrpStr by remember {
        mutableStateOf(subProduct?.mrp?.let { if (it == 0.0) "" else it.toString() } ?: "")
    }
    var unitsState by remember {
        mutableStateOf(subProduct?.sellingUnits?.map {
            SellingUnitEditState(
                it.id,
                it.unitType,
                it.packets.toString(),
                it.sellingPrice.toString().takeIf { p -> p != "0.0" } ?: "")
        } ?: listOf(SellingUnitEditState(null, UnitType.PIECE, "1", "")))
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = mrpStr,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null || it == ".") mrpStr = it
                    },
                    label = { Text("MRP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Packaging Units", fontWeight = FontWeight.Bold, color = TopBarBlue)
                    IconButton(onClick = {
                        unitsState =
                            unitsState + SellingUnitEditState(null, UnitType.PIECE, "1", "")
                    }) { Icon(Icons.Default.Add, contentDescription = null, tint = TopBarBlue) }
                }
                unitsState.forEachIndexed { index, unit ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var exp by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = exp,
                                onExpandedChange = { exp = !exp },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = unit.unitType.name,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Unit") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(exp) },
                                    modifier = Modifier
                                        .menuAnchor(
                                            MenuAnchorType.PrimaryNotEditable,
                                            true
                                        )
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = exp,
                                    onDismissRequest = { exp = false }) {
                                    UnitType.entries.forEach { type ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    type.name
                                                )
                                            },
                                            onClick = {
                                                unitsState = unitsState.toMutableList().also {
                                                    it[index] = it[index].copy(unitType = type)
                                                }; exp = false
                                            })
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = unit.packets,
                                onValueChange = { newVal ->
                                    if (newVal.isEmpty() || newVal.toIntOrNull() != null) unitsState =
                                        unitsState.toMutableList()
                                            .also { it[index] = it[index].copy(packets = newVal) }
                                },
                                label = { Text("Qty") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.8f)
                            )
                            IconButton(onClick = {
                                unitsState = unitsState.filterIndexed { i, _ -> i != index }
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = DeleteRed
                                )
                            }
                        }
                        OutlinedTextField(
                            value = unit.sellingPrice,
                            onValueChange = { newVal ->
                                if (newVal.isEmpty() || newVal.toDoubleOrNull() != null || newVal == ".") unitsState =
                                    unitsState.toMutableList()
                                        .also { it[index] = it[index].copy(sellingPrice = newVal) }
                            },
                            label = { Text("Selling Price") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val final = unitsState.map {
                        SellingUnit(
                            it.id,
                            it.unitType,
                            it.packets.toIntOrNull() ?: 0,
                            it.sellingPrice.toDoubleOrNull() ?: 0.0
                        )
                    }
                    onConfirm(SubProduct2(subProduct?.id, mrpStr.toDoubleOrNull() ?: 0.0, final))
                },
                enabled = mrpStr.isNotBlank() && unitsState.all { it.sellingPrice.isNotBlank() },
                colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue)
            ) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private data class SellingUnitEditState(
    val id: Long?,
    val unitType: UnitType,
    val packets: String,
    val sellingPrice: String
)
