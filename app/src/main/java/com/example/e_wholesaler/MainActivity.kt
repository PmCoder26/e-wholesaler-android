package com.example.e_wholesaler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.e_wholesaler.auth.LoginScreen
import com.example.e_wholesaler.auth.SignUpScreen
import com.example.e_wholesaler.auth.dtos.UserType
import com.example.e_wholesaler.main.users.owner.ui.OwnerScreen
import com.example.e_wholesaler.navigation_viewmodel.NavigationViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.parimal.auth.AuthClient
import org.parimal.auth.TokenManager
import org.parimal.auth.dtos.TokenState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenManager by inject<TokenManager>()
        val authClient by inject<AuthClient>()

        setContent {
            val navCon = rememberNavController()
            val tokenState by tokenManager.tokenState2.collectAsState()
            val navigationViewModel = koinViewModel<NavigationViewModel>(
                viewModelStoreOwner = LocalContext.current as ComponentActivity
            )
            val navigationData by navigationViewModel.navigationData.collectAsState()
            val hasNavigated = navigationData.hasNavigatedFromLogin
            val isLoggedIn = navigationData.isLoggedIn

            // ✅ Ensure LaunchedEffect runs only once when needed
            LaunchedEffect(tokenState, hasNavigated) {
                if (!hasNavigated && tokensAndCredentialsCheck(tokenState)) {
                    navigationViewModel.updateIsLoggedIn()
                }
            }

            LaunchedEffect(Unit) {
                navigationViewModel.addController("MainController", navCon)
            }

            NavHost(navController = navCon, startDestination = "LoginScreen") {
                composable("LoginScreen") {
                    if (isLoggedIn && !hasNavigated) {
                        navigationViewModel.updateHasNavigated()
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