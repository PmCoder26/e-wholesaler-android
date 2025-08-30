package com.example.e_wholesaler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.e_wholesaler.auth.LoginScreen
import com.example.e_wholesaler.auth.SignUpScreen
import com.example.e_wholesaler.auth.dtos.UserType
import com.example.e_wholesaler.main.users.owner.ui.OwnerScreen
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import org.parimal.auth.AuthClient
import org.parimal.auth.TokenManager
import org.parimal.auth.dtos.TokenState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenManager by inject<TokenManager>(named("token-manager"))
        val authClient by inject<AuthClient>(named("auth-client"))

        setContent {
            val navCon = rememberNavController()
            val tokenState by tokenManager.tokenState2.collectAsState()
            var isLoggedIn by remember { mutableStateOf(false) }
            var hasNavigated by remember { mutableStateOf(false) } // ✅ Prevent duplicate navigation

            // ✅ Ensure LaunchedEffect runs only once when needed
            LaunchedEffect(tokenState, hasNavigated) {
                if (!hasNavigated && tokensAndCredentialsCheck(tokenState)) {
                    isLoggedIn = true
                }
            }

            NavHost(navController = navCon, startDestination = "LoginScreen") {
                composable("LoginScreen") {
                    if (isLoggedIn && !hasNavigated) {
                        hasNavigated = true
                        when (tokenState.userType) {
                            UserType.OWNER -> navCon.navigate("OwnerScreen")
                            UserType.WORKER -> TODO()
                            UserType.CUSTOMER -> TODO()
                            UserType.NONE -> TODO()
                            null -> TODO()
                        }
                    } else if(!isLoggedIn){
                        LoginScreen(navCon, authClient)
                    }
                }

                composable("SignUpScreen") {
                    SignUpScreen(navCon, authClient)
                }
                composable("OwnerScreen") { OwnerScreen() }
            }
        }
    }

    private fun tokensAndCredentialsCheck(tokenState: TokenState): Boolean {
        return !(tokenState.accessToken.isNullOrBlank()
                || tokenState.refreshToken.isNullOrBlank()
                || tokenState.userType == null
                || tokenState.userTypeId == null)
    }
}