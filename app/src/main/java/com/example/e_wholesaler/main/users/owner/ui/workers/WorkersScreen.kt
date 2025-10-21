package com.example.e_wholesaler.main.users.owner.ui.workers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.auth.utils.Gender
import com.example.e_wholesaler.main.users.owner.dtos.Worker

val TopBarBlue = Color(0xFF2196F3)
val BackgroundScreen = Color(0xFFF0F2F5)
val CardBackgroundWhite = Color.White
val IconColorWhite = Color.White
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)
val ActiveGreen = Color(0xFF4CAF50)
val InactiveRed = Color(0xFFF44336)
val DeleteRed = Color(0xFFD32F2F)

@Preview(showBackground = true)
@Composable
fun WorkersScreenPreview() {
    val sampleShops = mapOf(
        1L to "Sagar Traders",
        2L to "Ratan Traders"
    )
    val sampleWorkers = listOf(
        Worker(
            id = 1,
            name = "Rajesh Kumar",
            gender = Gender.MALE,
            mobNo = "+91 9876543210",
            address = "",
            city = "",
            state = "",
            shopId = 1,
            salary = 25000.0
        ),
        Worker(
            id = 2,
            name = "Priya Sharma",
            gender = Gender.FEMALE,
            mobNo = "+91 9876543211",
            address = "",
            city = "",
            state = "",
            shopId = 1,
            salary = 22000.0
        )
    )

    WorkersScreen(
        shops = sampleShops,
        onBackClicked = {},
        onAddClicked = {},
        getWorkersForShop = { sampleWorkers }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkersScreen(
    shops: Map<Long, String>,
    onBackClicked: () -> Unit,
    onAddClicked: (shopId: Long) -> Unit,
    getWorkersForShop: suspend (shopId: Long) -> List<Worker>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedShopId by remember { mutableStateOf(shops.keys.firstOrNull()) }

    val workers by produceState(initialValue = emptyList(), key1 = selectedShopId) {
        value = selectedShopId?.let { getWorkersForShop(it) } ?: emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workers", color = IconColorWhite) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = IconColorWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { selectedShopId?.let(onAddClicked) }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Worker",
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
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ShopSelector(
                shops = shops,
                selectedShopId = selectedShopId,
                onShopSelected = { selectedShopId = it }
            )
            SearchBar(searchQuery = searchQuery, onQueryChange = { searchQuery = it })

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val filteredWorkers = workers.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }
                items(filteredWorkers, key = { it.id }) { worker ->
                    WorkerCard(
                        worker = worker,
                        onEditClicked = { /* TODO: Handle Edit */ },
                        onDeleteClicked = { /* TODO: Handle Delete */ }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopSelector(
    shops: Map<Long, String>,
    selectedShopId: Long?,
    onShopSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedShopId?.let { shops[it] } ?: "Select a Shop",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardBackgroundWhite,
                    unfocusedContainerColor = CardBackgroundWhite,
                    disabledContainerColor = CardBackgroundWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                shops.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            onShopSelected(id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(searchQuery: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        placeholder = { Text("Search workers...", color = TextSecondary) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = TextSecondary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CardBackgroundWhite,
            unfocusedContainerColor = CardBackgroundWhite,
            disabledContainerColor = CardBackgroundWhite,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
fun WorkerCard(worker: Worker, onEditClicked: () -> Unit, onDeleteClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Worker Avatar",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .padding(8.dp),
                tint = TextSecondary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    worker.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(worker.mobNo, fontSize = 14.sp, color = TextSecondary)
            }

            Column {
                IconButton(onClick = onEditClicked) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TopBarBlue)
                }
                IconButton(onClick = onDeleteClicked) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DeleteRed)
                }
            }
        }
    }
}