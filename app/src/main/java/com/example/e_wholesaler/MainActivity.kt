package com.example.e_wholesaler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.e_wholesaler.auth.LoginScreen
import com.example.e_wholesaler.auth.SignUpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navCon = rememberNavController()

            NavHost(navController = navCon, startDestination = "SignUpScreen") {
                composable("SignUpScreen") {
                    SignUpScreen(navCon)
                }

                composable("LoginScreen") {
                    LoginScreen(navCon)
                }

            }
        }
    }
}
