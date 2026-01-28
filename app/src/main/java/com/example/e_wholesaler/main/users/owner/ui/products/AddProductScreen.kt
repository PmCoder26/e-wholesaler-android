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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.main.users.owner.dtos.SellingUnit
import com.example.e_wholesaler.main.users.owner.dtos.SubProduct2
import com.example.e_wholesaler.main.users.owner.dtos.UnitType
import com.example.e_wholesaler.main.users.owner.ui.shops.CardBackgroundWhite
import com.example.e_wholesaler.main.users.owner.ui.shops.IconColorWhite
import com.example.e_wholesaler.main.users.owner.ui.shops.TopBarBlue
import com.example.e_wholesaler.main.users.owner.ui.workers.BackgroundScreen
import com.example.e_wholesaler.main.users.owner.ui.workers.DeleteRed
import java.util.UUID

private data class SellingUnitFormState(
    val id: UUID = UUID.randomUUID(),
    var unitType: UnitType = UnitType.PIECE,
    var packets: String = "",
    var sellingPrice: String = ""
)

private data class SubProductFormState(
    val id: UUID = UUID.randomUUID(),
    var mrp: String = "",
    var sellingUnits: List<SellingUnitFormState> = listOf(SellingUnitFormState())
)

@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    AddProductScreen(
        onBackClicked = {},
        onSaveProduct = { _, _, _ -> }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBackClicked: () -> Unit,
    onSaveProduct: (name: String, company: String, subProducts: List<SubProduct2>) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var productCompany by remember { mutableStateOf("") }
    var subProductForms by remember { mutableStateOf(listOf(SubProductFormState())) }

    val isFormValid by remember {
        derivedStateOf {
            productName.isNotBlank() &&
            productCompany.isNotBlank() &&
            subProductForms.isNotEmpty() &&
                    subProductForms.all { variant ->
                        variant.mrp.isNotBlank() &&
                                variant.sellingUnits.isNotEmpty() &&
                                variant.sellingUnits.all { unit ->
                                    unit.packets.isNotBlank() && unit.sellingPrice.isNotBlank()
                                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Wholesale Product", color = IconColorWhite) },
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
                    val finalSubProducts = subProductForms.map { variant ->
                        SubProduct2(
                            mrp = variant.mrp.toDoubleOrNull() ?: 0.0,
                            sellingUnits = variant.sellingUnits.map { unit ->
                                SellingUnit(
                                    unitType = unit.unitType,
                                    packets = unit.packets.toIntOrNull() ?: 0,
                                    sellingPrice = unit.sellingPrice.toDoubleOrNull() ?: 0.0
                                )
                            }
                        )
                    }
                    onSaveProduct(productName, productCompany, finalSubProducts)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue),
                shape = RoundedCornerShape(12.dp),
                enabled = isFormValid
            ) {
                Text(
                    "Save Product",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
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
                Text(
                    "Basic Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = { Text("Product Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = productCompany,
                            onValueChange = { productCompany = it },
                            label = { Text("Company / Brand") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Pricing Variants (MRP)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        subProductForms = subProductForms + SubProductFormState()
                    }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Variant",
                            tint = TopBarBlue
                        )
                    }
                }
            }

            itemsIndexed(subProductForms, key = { _, item -> item.id }) { index, formState ->
                SubProductInputCard(
                    formState = formState,
                    onStateChange = { newState ->
                        subProductForms =
                            subProductForms.toMutableList().also { it[index] = newState }
                    },
                    onRemove = {
                        subProductForms = subProductForms.filterNot { it.id == formState.id }
                    },
                    canBeRemoved = subProductForms.size > 1,
                    variantNumber = index + 1
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                Text(
                    "Variant #$variantNumber",
                    style = MaterialTheme.typography.titleMedium,
                    color = TopBarBlue,
                    fontWeight = FontWeight.Bold
                )
                if (canBeRemoved) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove Variant", tint = DeleteRed)
                    }
                }
            }

            OutlinedTextField(
                value = formState.mrp,
                onValueChange = { onStateChange(formState.copy(mrp = it)) },
                label = { Text("MRP (Base Price)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                prefix = { Text("₹") }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Selling Units",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        onStateChange(formState.copy(sellingUnits = formState.sellingUnits + SellingUnitFormState()))
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Selling Unit",
                        tint = TopBarBlue
                    )
                }
            }

            formState.sellingUnits.forEachIndexed { index, unit ->
                SellingUnitInputRow(
                    unit = unit,
                    onUnitChange = { updatedUnit ->
                        val newList =
                            formState.sellingUnits.toMutableList().also { it[index] = updatedUnit }
                        onStateChange(formState.copy(sellingUnits = newList))
                    },
                    onRemove = {
                        val newList = formState.sellingUnits.filterIndexed { i, _ -> i != index }
                        onStateChange(formState.copy(sellingUnits = newList))
                    },
                    canRemove = formState.sellingUnits.size > 1
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SellingUnitInputRow(
    unit: SellingUnitFormState,
    onUnitChange: (SellingUnitFormState) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = unit.unitType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unit") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TopBarBlue)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    UnitType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                onUnitChange(unit.copy(unitType = type))
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = unit.packets,
                onValueChange = { onUnitChange(unit.copy(packets = it)) },
                label = { Text("Packets") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(8.dp)
            )

            if (canRemove) {
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove Unit",
                        tint = DeleteRed.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = unit.sellingPrice,
            onValueChange = { onUnitChange(unit.copy(sellingPrice = it)) },
            label = { Text("Selling Price for this unit") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            prefix = { Text("₹") }
        )
    }
}
