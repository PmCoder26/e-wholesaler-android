package org.parimal.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_wholesaler.auth.dtos.UserType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.ktor_client.HOST_URL
import org.parimal.auth.dtos.TokenState
import org.parimal.utils.ApiResponse
import org.parimal.auth.dtos.LoginResponse
import org.parimal.auth.dtos.Tokens

class TokenManager(
    private var dataStore: DataStore<Preferences>,
    private val httpClient: HttpClient?
) : ViewModel() {

    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    private val USER_TYPE_KEY = stringPreferencesKey("user_type")
    private var accessToken = MutableStateFlow<String?>(null)
    private var refreshToken = MutableStateFlow<String?>(null)
    private var userType = MutableStateFlow<UserType?>(null)

    private var _state = MutableStateFlow(TokenState(accessToken.value, refreshToken.value, userType.value))
    private val tokenState =
        combine(accessToken, refreshToken, userType, _state) { accessToken, refreshToken, userType, _state ->
            _state.copy(
                accessToken = accessToken,
                refreshToken = refreshToken,
                userType = userType
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            TokenState(accessToken.value, refreshToken.value, userType.value)
        )
    val tokenState2 = _state


    init {
        viewModelScope.launch {
            dataStore.data.collectLatest { pref ->
                val access = pref[ACCESS_TOKEN_KEY]
                val refresh = pref[REFRESH_TOKEN_KEY]
                val type = pref[USER_TYPE_KEY]

                accessToken.update {
                    access
                }
                refreshToken.update {
                    refresh
                }
                userType.update {
                    var tempType: UserType = UserType.NONE
                    type?.let {
                        tempType = enumValueOf(it)
                    }
                    tempType
                }

                _state.update {
                    println("Tokens updated")
                    TokenState(accessToken.value, refreshToken.value, userType.value)
                }
            }
        }
    }

    suspend fun saveTokens(data: LoginResponse) {
        dataStore.edit { pref ->
            pref[ACCESS_TOKEN_KEY] = data.accessToken
            pref[REFRESH_TOKEN_KEY] = data.refreshToken
            pref[USER_TYPE_KEY] = data.userType.toString()
        }
    }

    suspend fun refreshTokens() {
        val response = try {
            httpClient!!.post(
                urlString = "http://$HOST_URL/auth/refresh-tokens"
            ) {
                headers.remove("Authorization")
                contentType(ContentType.Application.Json)
                setBody(
                    Tokens(
                        accessToken = accessToken.value!!,
                        refreshToken = refreshToken.value!!
                    )
                )
            }.body<ApiResponse<LoginResponse>>()
        } catch (e: Exception) {
            println("Token refresh error: ${e.message}")
            null
        }
        response?.data?.let { data ->
            println("Refreshing tokens response data: $data")
            saveTokens(
                LoginResponse(
                    accessToken = data.accessToken,
                    refreshToken = data.refreshToken,
                    userType = data.userType
                )
            )
        }
        response?.error?.let { error ->
            println("Token refresh response error: ${error.message}")
        }
    }

    fun tokensCheck(): Boolean {
        return accessToken.value != null && refreshToken.value != null
    }

    suspend fun clearTokens() {
        dataStore.edit { pref ->
            pref.clear()
        }
    }

}