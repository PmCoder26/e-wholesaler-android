package org.parimal.auth

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
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
    private val httpClient: HttpClient
) : ViewModel() {

    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    private val USER_TYPE_KEY = stringPreferencesKey("user_type")
    private val USER_TYPE_ID_KEY = longPreferencesKey("user_type_id")       // user-type wise(owner, customer, worker) id.
    private var accessToken = MutableStateFlow<String?>(null)
    private var refreshToken = MutableStateFlow<String?>(null)
    private var userType = MutableStateFlow<UserType?>(null)
    private var userTypeId = MutableStateFlow<Long?>(null)

    private var _state = MutableStateFlow(TokenState(
        accessToken.value,
        refreshToken.value,
        userType.value,
        userTypeId.value
    ))

    private val tokenState =
        combine(
            accessToken, refreshToken, userType, userTypeId, _state
        ) { accessToken, refreshToken, userType, userTypeId, _state ->
            _state.copy(
                accessToken = accessToken,
                refreshToken = refreshToken,
                userType = userType,
                userTypeId = userTypeId
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            TokenState(
                accessToken.value, refreshToken.value, userType.value, userTypeId.value
            )
        )
    val tokenState2 = _state


    init {
        viewModelScope.launch {
            dataStore.data.collectLatest { pref ->
                val access = pref[ACCESS_TOKEN_KEY]
                val refresh = pref[REFRESH_TOKEN_KEY]
                val type = pref[USER_TYPE_KEY]
                val typeId = pref[USER_TYPE_ID_KEY]

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
                userTypeId.update {
                    typeId
                }

                _state.update {
                    println("Tokens updated")
                    TokenState(
                        accessToken.value, refreshToken.value, userType.value, userTypeId.value
                    )
                }
            }
        }
    }

    suspend fun saveTokens(data: LoginResponse) {
        dataStore.edit { pref ->
            pref[ACCESS_TOKEN_KEY] = data.accessToken
            pref[REFRESH_TOKEN_KEY] = data.refreshToken
            pref[USER_TYPE_KEY] = data.userType.toString()
            pref[USER_TYPE_ID_KEY] = data.userTypeId
        }
    }

    suspend fun refreshTokens() {
        val response = try {
            httpClient.post(
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
                    userType = data.userType,
                    userTypeId = data.userTypeId
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