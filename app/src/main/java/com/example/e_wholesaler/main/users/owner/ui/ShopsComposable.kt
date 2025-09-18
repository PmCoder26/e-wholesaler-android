package com.example.e_wholesaler.main.users.owner.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_wholesaler.R
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.dtos.SortType
import com.example.e_wholesaler.main.users.owner.viewmodels.OwnerViewModel
import com.example.e_wholesaler.navigation_viewmodel.NavigationViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ShopsScreen() {
    // Sample data representing the shops

    val ownerViewModel = if (!getIsPreview()) {
        koinViewModel<OwnerViewModel>(
            viewModelStoreOwner = getViewModelStoreOwner()
        )
    } else null
    val navigationViewModel = if (!getIsPreview()) {
        koinViewModel<NavigationViewModel>(
            viewModelStoreOwner = getViewModelStoreOwner()
        )
    } else null
    val shopsState = ownerViewModel?.shopsState?.collectAsState()?.value
    val shops = shopsState?.shops ?: emptyList()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shops", color = IconColorWhite) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigationViewModel?.getController("OwnerController")?.popBackStack()
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
                    IconButton(onClick = { /* Handle add action */ }) {
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
                        ownerViewModel?.updateShopSortType(SortType.NAME)
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
                        ownerViewModel?.updateShopSortType(SortType.CITY)
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
                    ShopCard(shop = shop)
                }
            }
        }
    }
}

// Composable for displaying a single shop item in a Card
@Composable
fun ShopCard(shop: Shop) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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