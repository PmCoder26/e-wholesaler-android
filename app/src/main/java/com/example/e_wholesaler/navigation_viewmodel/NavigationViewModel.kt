package com.example.e_wholesaler.navigation_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NavigationViewModel : ViewModel() {

    private var navHostControllerMap: MutableMap<String, NavHostController> = mutableMapOf()
    private var hasNavigatedFromLogin = MutableStateFlow(false)
    private var isLoggedIn = MutableStateFlow(false)

    val navigationData =
        combine(hasNavigatedFromLogin, isLoggedIn) { hasNavigatedFromLogin, isLoggedIn ->
            NavigationData(hasNavigatedFromLogin, isLoggedIn)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = NavigationData(false, false)
        )

    fun addController(controllerName: String, navController: NavHostController) {
        navHostControllerMap.put(controllerName, navController)
    }

    fun getController(controllerName: String) = navHostControllerMap[controllerName]

    fun updateHasNavigated() {
        viewModelScope.launch(Dispatchers.IO) {
            hasNavigatedFromLogin.value = !hasNavigatedFromLogin.value
        }
    }

    fun updateIsLoggedIn() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoggedIn.value = !isLoggedIn.value
        }
    }

}