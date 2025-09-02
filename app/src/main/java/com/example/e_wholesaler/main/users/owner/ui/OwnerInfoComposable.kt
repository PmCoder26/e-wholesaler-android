package com.example.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_wholesaler.auth.dtos.Gender
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import com.example.e_wholesaler.navigation_viewmodel.NavigationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.java.KoinJavaComponent.inject
import org.parimal.auth.AuthClient


@Preview(showBackground = true)
@Composable
fun OwnerInfoScreenPreview() {
    OwnerInfoScreen(
        OwnerDetails(
            1,
            "Sagar Matte",
            Gender.MALE,
            9822543343,
            "address",
            "Pune",
            "Maharashtra"
        )
    )
}

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerInfoScreen(owner: OwnerDetails?) {
    val primaryColor = Color(0xFF2457EB) // Deep Blue
    val lightBlue = Color(0xFFE6EEFF) // Card Background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F9FF), Color(0xFFDDE9FF))
    )
    val activity = LocalContext.current as ComponentActivity
    val navigationViewModel = koinViewModel<NavigationViewModel>(
        viewModelStoreOwner = activity
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Information Dashboard", color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        navigationViewModel.getController("OwnerController")?.popBackStack()
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                val scope = rememberCoroutineScope()
                val context = LocalContext.current

                Button(
                    onClick = {
                        scope.launch(Dispatchers.Main) {
                            val authClient by inject<AuthClient>(AuthClient::class.java)
                            val loggedOut = authClient.logout()
                            if (loggedOut) {
                                navigationViewModel.updateIsLoggedIn()
                                navigationViewModel.updateHasNavigated()
                                navigationViewModel.getController("MainController")
                                    ?.popBackStack("LoginScreen", false)
                            } else withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Logout Failed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            border = BorderStroke(7.dp, primaryColor),
                            shape = RoundedCornerShape(11.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(primaryColor)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = lightBlue),
                    elevation = CardDefaults.elevatedCardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Owner",
                            tint = primaryColor,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = owner?.name ?: "null", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Owner", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.elevatedCardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Personal Information",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        InfoRow(Icons.Default.Person, owner?.name ?: "null")
                        InfoRow(Icons.Default.Person, owner?.gender?.name ?: "null")
                        InfoRow(Icons.Default.Phone, "+91 ${owner?.mobNo ?: "null"}")
                        InfoRow(Icons.Default.LocationOn, owner?.address ?: "null")
                        InfoRow(Icons.Default.MailOutline, owner?.city ?: "null")
                        InfoRow(Icons.Default.Info, "India", primaryColor)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String, iconColor: Color = Color.Black) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}