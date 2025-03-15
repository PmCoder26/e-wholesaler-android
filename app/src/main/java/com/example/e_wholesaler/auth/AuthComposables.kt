package com.example.e_wholesaler.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


@Preview(showBackground = true)
@Composable
fun SignUpScreen(navCon: NavHostController = rememberNavController()) {
    var fullName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("Select state") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDF3FF)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
//                elevation = 10.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        style = TextStyle(
                            fontSize = 32.sp,
                            color = Color(0xFF1D4ED8),
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Create your account", fontSize = 18.sp, modifier = Modifier.align(
                        Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(24.dp))

                    CustomTextField(label = "Full Name", value = fullName, onValueChange = { fullName = it })
                    Spacer(modifier = Modifier.height(5.dp))

                    GenderSelection(gender) { gender = it }
                    Spacer(modifier = Modifier.height(5.dp))

                    CustomTextField(label = "Mobile Number", value = mobileNumber, onValueChange = { mobileNumber = it }, keyboardType = KeyboardType.Phone)
                    Spacer(modifier = Modifier.height(5.dp))

                    CustomTextField(label = "Address", value = address, onValueChange = { address = it })
                    Spacer(modifier = Modifier.height(5.dp))

                    CustomTextField(label = "City", value = city, onValueChange = { city = it })
                    Spacer(modifier = Modifier.height(5.dp))

                    DropdownMenuField(selectedState, listOf(
                        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
                        "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
                        "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
                        "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
                        "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
                        "Uttar Pradesh", "Uttarakhand", "West Bengal"
                    )) { selectedState = it }
                    Spacer(modifier = Modifier.height(5.dp))

                    PasswordField(password, passwordVisible) { password = it }
                    Spacer(modifier = Modifier.height(5.dp))

                    UserTypeSelection(userType) { userType = it }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { /* Handle Sign Up */ },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF1D4ED8)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Sign Up", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordField(value: String, isVisible: Boolean, onValueChange: (String) -> Unit) {
    Column {
        Text("Password", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(12.dp),
        )
    }
}

@Composable
fun GenderSelection(selectedGender: String, onGenderSelected: (String) -> Unit) {
    Text("Gender", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
    Row {
        listOf("Male", "Female", "Other").forEach { gender ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
                RadioButton(selected = selectedGender == gender, onClick = { onGenderSelected(gender) })
                Text(gender, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun UserTypeSelection(selectedUserType: String, onUserTypeSelected: (String) -> Unit) {
    Text("User Type", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
    Row {
        listOf("Owner", "Customer").forEach { userType ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
                RadioButton(selected = selectedUserType == userType, onClick = { onUserTypeSelected(userType) })
                Text(userType, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit, keyboardType: KeyboardType = KeyboardType.Text) {
    Text(label, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth()
            .height(40.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
    )
    Spacer(modifier = Modifier.height(10.dp))
}


@Preview(showBackground = true)
@Composable
fun DropdownMenuField(selectedItem: String? = null, items: List<String> = emptyList(), onItemSelected: (String) -> Unit = null!!) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text("State", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Text(selectedItem!!, fontSize = 16.sp)
            Icon(
                Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.align(
                    Alignment.CenterEnd
                )
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item, fontSize = 16.sp) }, // Correct way in Material 3
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun LoginScreen(navCon: NavHostController = rememberNavController()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB3DAFF)), // Sky Blue Background
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // App Title
            Text(
                text = "E Wholesaler",
                style = TextStyle(
                    color = Color(0xFF007AFF), // Bright Blue
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Cursive // Matching Font Style
                )
            )
            Spacer(modifier = Modifier.height(50.dp))

            // Rounded Card with Shadow
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp), // More Rounded Edges
                        clip = false
                    )
                    .background(
                        color = Color(0xFFE5F1FF), // Light Blue Card Background
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(2.dp))

                    // Username Field
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Icon",
                                tint = Color(0xFF007AFF) // Blue Icon Color
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(12.dp)),
//                        colors = TextFieldDefaults.outlinedTextFieldColors(
//                            backgroundColor = Color.White,
//                            focusedBorderColor = Color.LightGray,
//                            unfocusedBorderColor = Color.Gray
//                        )
                    )
                    Spacer(modifier = Modifier.height(25.dp))

                    // Password Field
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = Color(0xFF007AFF) // Blue Icon Color
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(12.dp)), // Rounded Corners
//                        colors = TextFieldDefaults.outlinedTextFieldColors(
//                            backgroundColor = Color.White,
//                            focusedBorderColor = Color.LightGray,
//                            unfocusedBorderColor = Color.Gray
//                        )
                    )
                    Spacer(modifier = Modifier.height(25.dp))

                    // Login Button
                    Button(
                        onClick = { /* Handle Login */ },
                        colors = ButtonDefaults.buttonColors(Color(0xD8007AFF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp) // Rounded Button
                    ) {
                        Text("Login", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(25.dp))

                    // Sign-up Text with Clickable Option
                    Row {
                        Text("New to us? ", color = Color.Gray)
                        Text(
                            "Sign up",
                            color = Color(0xFF007AFF),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { /* Navigate to Sign Up */ }
                        )
                    }
                }
            }
        }
    }
}