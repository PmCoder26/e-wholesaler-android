package com.example.e_wholesaler.main.users.owner.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.main.users.owner.dtos.DailyShopRevenue
import com.example.e_wholesaler.main.users.owner.viewmodels.OwnerViewModel
import com.example.e_wholesaler.main.users.owner.viewmodels.utils.SortType
import com.example.e_wholesaler.navigation_viewmodel.NavigationViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RevenueScreen() {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Revenue", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    val navigationViewModel = if (!getIsPreview()) {
                        koinViewModel<NavigationViewModel>(
                            viewModelStoreOwner = getViewModelStoreOwner()
                        )
                    } else null
                    IconButton(onClick = {
                        navigationViewModel?.getController("OwnerController")?.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF2B54F8))
            )
        },
    ) { paddingValues ->

        val ownerViewModel = if (!getIsPreview()) {
            koinViewModel<OwnerViewModel>(
                viewModelStoreOwner = getViewModelStoreOwner()
            )
        } else null
        val totalShopRevenue = ownerViewModel?.totalRevenue?.collectAsState()?.value

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFEAF1FF))
                .padding(horizontal = 16.dp)
        ) {
            RevenueCard(totalShopRevenue?.totalRevenue ?: 0.0)
            Spacer(modifier = Modifier.height(16.dp))
            SearchBar()
            Spacer(modifier = Modifier.height(16.dp))
            FilterChips(ownerViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            RevenueList(totalShopRevenue?.dailyShopRevenueList)
        }
    }
}

@Composable
fun RevenueCard(totalRevenue: Double) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total Daily Revenue", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                totalRevenue.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "", onValueChange = {},
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        placeholder = { Text("Search shops...") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun FilterChips(ownerViewModel: OwnerViewModel?) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        items(listOf("Name", "City", "Revenue")) {
            FilterChip(it, updateFilter = {
                val sortType = when (it) {
                    "Name" -> SortType.NAME
                    "City" -> SortType.CITY
                    else -> SortType.REVENUE
                }
                ownerViewModel?.updateShopRevenueSortType(sortType)
            })
        }
    }
}

@Composable
fun FilterChip(label: String, updateFilter: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .padding(4.dp)
            .clickable { updateFilter() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun RevenueList(dailyShopRevenueList: List<DailyShopRevenue>?) {
    LazyColumn {
        dailyShopRevenueList?.let {
            itemsIndexed(
                it,
                key = { index, shopRevenue -> shopRevenue.shopName }) { index, shopRevenue ->
                RevenueItem(
                    name = shopRevenue.shopName,
                    "${shopRevenue.city} | ${shopRevenue.dailyTransactions} transactions today",
                    revenue = shopRevenue.dailyRevenue.toString()
                )
                Spacer(Modifier.height(5.dp))
            }
        }
    }
}

@Composable
fun RevenueItem(name: String, details: String, revenue: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(details, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(revenue, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}