package com.example.e_wholesaler.main.users.owner.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.R
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.viewmodels.NullOwnerViewModel
import com.example.e_wholesaler.main.users.owner.viewmodels.OwnerViewModel
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.ShopSortType
import com.example.e_wholesaler.navigation_viewmodel.NavigationViewModel
import com.example.e_wholesaler.navigation_viewmodel.NullNavigationViewModel
import org.koin.androidx.compose.koinViewModel


// Define Colors based on the image
val TopBarBlue = Color(0xFF0052D4) // A standard Material Blue, adjust if needed
val BackgroundLightBlue = Color(0x41E3F2FD) // A light blue
val CardBackgroundWhite = Color.White
val TextPrimary = Color.Black
val TextSecondary = Color.DarkGray
val IconColorGray = Color.Gray
val IconColorWhite = Color.White
val ButtonIconColor = TopBarBlue // Assuming button icons match top bar blue
val HintGray = Color.Gray
val BorderColor = Color(0xFFD0D0D0)

val indianStates = listOf(
    "Select state", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
    "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka",
    "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram",
    "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana",
    "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal"
)


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ShopsScreen() {

    val ownerViewModel = if (!getIsPreview()) {
        koinViewModel<OwnerViewModel>(
            viewModelStoreOwner = getViewModelStoreOwner()
        )
    } else NullOwnerViewModel()
    val navigationViewModel = if (!getIsPreview()) {
        koinViewModel<NavigationViewModel>(
            viewModelStoreOwner = getViewModelStoreOwner()
        )
    } else NullNavigationViewModel()
    val shopsState by ownerViewModel.shopsState.collectAsState()
    val shops = shopsState.shops
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shops", color = IconColorWhite) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigationViewModel.getController("OwnerController")?.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = IconColorWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navigationViewModel.getController("OwnerController")
                            ?.navigate("AddShopScreen")
                    }) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add Shop",
                            tint = IconColorWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(TopBarBlue),
            )
        },
        containerColor = BackgroundLightBlue
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(16.dp) // Add overall padding for content area
        ) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search shops by name", color = TextSecondary) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search Icon",
                        tint = IconColorGray
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardBackgroundWhite,    // Background when focused
                    unfocusedContainerColor = CardBackgroundWhite,  // Background when not focused
                    disabledContainerColor = CardBackgroundWhite,   // Background when disabled
                    focusedIndicatorColor = Color.Transparent,      // Hide the focus indicator line
                    unfocusedIndicatorColor = Color.Transparent,    // Hide the unfocused indicator line
                    disabledIndicatorColor = Color.Transparent      // Hide the disabled indicator line
                ),
                shape = RoundedCornerShape(8.dp) // Basic rounded corners
            )

            Spacer(modifier = Modifier.height(16.dp)) // Space between search and buttons

            // Filter Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between buttons
            ) {
                // Name Filter Button
                OutlinedButton(
                    onClick = {
                        ownerViewModel.updateShopSortType(ShopSortType.NAME)
                    },
                    modifier = Modifier.weight(1f), // Make buttons share width equally
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CardBackgroundWhite // White background for Button
                    ),
                    shape = RoundedCornerShape(8.dp) // Basic rounded corners
                ) {
                    Icon(
                        painter = painterResource(R.drawable.name_logo), // Icon for sorting
                        contentDescription = "Sort by Name",
                        tint = ButtonIconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp)) // Space between icon and text
                    Text("Name", color = ButtonIconColor)
                }

                // City Filter Button
                OutlinedButton(
                    onClick = {
                        ownerViewModel.updateShopSortType(ShopSortType.CITY)
                    },
                    modifier = Modifier.weight(1f), // Make buttons share width equally
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CardBackgroundWhite // White background for Button
                    ),
                    shape = RoundedCornerShape(8.dp) // Basic rounded corners
                ) {
                    Icon(
                        Icons.Filled.LocationOn, // Icon for location/city
                        contentDescription = "Filter by City",
                        tint = ButtonIconColor
                    )
                    Spacer(Modifier.width(4.dp)) // Space between icon and text
                    Text("City", color = ButtonIconColor)
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Space between buttons and list

            // Shops List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp) // Space between shop items
            ) {
                items(shops.filter {
                    it.name.contains(
                        searchQuery,
                        ignoreCase = true
                    ) || it.city.contains(searchQuery, ignoreCase = true)
                }) { shop ->
                    ShopCard(
                        shop = shop,
                        onClick = {
                            navigationViewModel.getController("OwnerController")
                                ?.navigate("ShopDetailsScreen/${shop.id}")
                        })
                }
            }
        }
    }
}

// Composable for displaying a single shop item in a Card
@Composable
fun ShopCard(shop: Shop, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp), // Basic rounded corners for card
        colors = CardDefaults.cardColors(
            CardBackgroundWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Internal padding within the card
        ) {
            Text(
                text = shop.name,
                fontWeight = FontWeight.Bold, // Make name bold
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp)) // Space between name and city
            Text(
                text = shop.city,
                color = TextSecondary
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ShopDetailsScreenPreview() {
    val sampleShopDetail = Shop(
        id = 0,
        name = "Sagar Traders",
        gstNo = "27AADCUSTOMER1Z5",
        createdAt = "March 15, 2024",
        address = "Shop No. 42, Ganesh Market, M.G. Road, Borivali West",
        city = "Mumbai",
        state = "Maharashtra"
    )
    // You might need to provide a MaterialTheme wrapper if not already present in your previews
    ShopDetailsScreen(
        shopDetail = sampleShopDetail,
        onBackClicked = { /* Preview action */ },
        onEditDetailsClicked = { /* Preview action */ }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailsScreen(
    shopDetail: Shop,
    onBackClicked: () -> Unit,
    onEditDetailsClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop Details", color = IconColorWhite) },
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
        containerColor = BackgroundLightBlue,
        bottomBar = {
            ExtendedFloatingActionButton(
                onClick = onEditDetailsClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                containerColor = TopBarBlue,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Edit Details", color = Color.White, fontSize = 18.sp)
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Shop Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = shopDetail.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Text("GST Number: ", fontSize = 14.sp, color = TextSecondary)
                        Text(
                            shopDetail.gstNo,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text("Added: ", fontSize = 14.sp, color = TextSecondary)
                        Text(shopDetail.createdAt, fontSize = 14.sp, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Location Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Address",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = shopDetail.address,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "City",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(shopDetail.city, fontSize = 14.sp, color = TextPrimary)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "State",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(shopDetail.state, fontSize = 14.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditShopDetailsScreenPreview() {
    val sampleShop = Shop(
        id = 1,
        name = "Sagar Traders",
        gstNo = "27AADCUSTOMER1Z5",
        createdAt = "March 15, 2024", // This field is not editable in this screen
        address = "Shop No. 42, Ganesh Market, M.G. Road, Borivali West",
        city = "Mumbai",
        state = "Maharashtra"
    )
    MaterialTheme { // Assuming you have a MaterialTheme wrapper for previews
        EditShopDetailsScreen(
            shop = sampleShop,
            onSaveClicked = { /* Preview action */ },
            onBackClicked = { /* Preview action */ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditShopDetailsScreen(
    shop: Shop,
    onSaveClicked: (Shop) -> Unit,
    onBackClicked: () -> Unit
) {
    var name by remember(shop.name) { mutableStateOf(shop.name) }
    var gstNo by remember(shop.gstNo) { mutableStateOf(shop.gstNo) }
    var address by remember(shop.address) { mutableStateOf(shop.address) }
    var city by remember(shop.city) { mutableStateOf(shop.city) }
    var state by remember(shop.state) { mutableStateOf(shop.state) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Shop Details", color = IconColorWhite) },
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
        containerColor = BackgroundLightBlue,
        bottomBar = {
            Button(
                onClick = {
                    val updatedShop = shop.copy(
                        name = name,
                        gstNo = gstNo,
                        address = address,
                        city = city,
                        state = state
                    )
                    onSaveClicked(updatedShop)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TopBarBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Changes", color = Color.White, fontSize = 16.sp)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp) // Adjusted padding for scroll
                .verticalScroll(rememberScrollState()) // Enable scrolling for form
        ) {
            EditTextField(
                value = name,
                onValueChange = { name = it },
                label = "Shop Name"
            )
            Spacer(modifier = Modifier.height(16.dp))
            EditTextField(
                value = gstNo,
                onValueChange = { gstNo = it },
                label = "GST Number"
            )
            Spacer(modifier = Modifier.height(16.dp))
            EditTextField(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                singleLine = false,
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            EditTextField(
                value = city,
                onValueChange = { city = it },
                label = "City"
            )
            Spacer(modifier = Modifier.height(16.dp))
            EditTextField(
                value = state,
                onValueChange = { state = it },
                label = "State"
            )
            Spacer(modifier = Modifier.height(16.dp)) // Space before bottom bar takes over
        }
    }
}

@Composable
private fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TopBarBlue,
            unfocusedBorderColor = IconColorGray,
            cursorColor = TopBarBlue,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = CardBackgroundWhite,
            unfocusedContainerColor = CardBackgroundWhite,
            disabledContainerColor = CardBackgroundWhite,
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AddShopScreenPreview() {
    AddShopScreen(
        onSaveClicked = { /* shop -> Log.d("Preview", "Save: $shop") */ },
        onCancelClicked = { /* Log.d("Preview", "Cancel clicked") */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShopScreen(
    onSaveClicked: (Shop) -> Unit,
    onCancelClicked: () -> Unit
) {
    var shopName by remember { mutableStateOf("") }
    var gstNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf(indianStates[0]) }
    var expandedStateDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLightBlue)
            .padding(16.dp)
    ) {
        Text(
            text = "Add New Shop",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Card takes available space, pushing buttons down
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                InputFieldWithLabel(
                    label = "Shop Name",
                    value = shopName,
                    onValueChange = { shopName = it },
                    placeholder = "Enter shop name"
                )
                Spacer(modifier = Modifier.height(16.dp))

                InputFieldWithLabel(
                    label = "GST Number",
                    value = gstNumber,
                    onValueChange = { gstNumber = it.uppercase() },
                    placeholder = "Enter GST number",
                    supportingText = "Format: 22AAAAA0000A1Z5",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                )
                Spacer(modifier = Modifier.height(16.dp))

                InputFieldWithLabel(
                    label = "Address",
                    value = address,
                    onValueChange = { address = it },
                    placeholder = "Enter complete address",
                    singleLine = false,
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(16.dp))

                InputFieldWithLabel(
                    label = "City",
                    value = city,
                    onValueChange = { city = it },
                    placeholder = "Enter city name"
                )
                Spacer(modifier = Modifier.height(16.dp))

                // State Dropdown
                Text(
                    "State",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expandedStateDropdown,
                    onExpandedChange = { expandedStateDropdown = !expandedStateDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedState,
                        onValueChange = { value -> selectedState = value },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStateDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryEditable),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TopBarBlue,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = CardBackgroundWhite,
                            unfocusedContainerColor = CardBackgroundWhite,
                            disabledContainerColor = CardBackgroundWhite,
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStateDropdown,
                        onDismissRequest = { expandedStateDropdown = false }
                    ) {
                        indianStates.forEach { state ->
                            DropdownMenuItem(
                                text = { Text(state) },
                                onClick = {
                                    selectedState = state
                                    expandedStateDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancelClicked,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cancel", color = Color.White, fontSize = 16.sp)
            }
            Button(
                onClick = {
                    val newShop = Shop(
                        id = 0L, // Backend will typically assign ID
                        name = shopName,
                        gstNo = gstNumber,
                        address = address,
                        city = city,
                        state = selectedState,
                        createdAt = "" // Backend will typically set timestamp
                    )
                    onSaveClicked(newShop)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(TopBarBlue),
                enabled = shopName.isNotBlank() && gstNumber.isNotBlank() && address.isNotBlank()
                        && city.isNotBlank() && selectedState != indianStates[0]
            ) {
                Text("Save", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun InputFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column {
        Text(
            label,
            fontSize = 16.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = HintGray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = TopBarBlue,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = CardBackgroundWhite,
                unfocusedContainerColor = CardBackgroundWhite,
                disabledContainerColor = CardBackgroundWhite,
            ),
            keyboardOptions = keyboardOptions,
        )
        if (supportingText != null) {
            Text(
                text = supportingText,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}