package org.parimal.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_wholesaler.auth.utils.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.parimal.auth.dtos.LoginResponse
import org.parimal.auth.dtos.TokenState

class TokenManager(private var dataStore: DataStore<Preferences>) : ViewModel() {

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
                    it.copy(
                        accessToken.value,
                        refreshToken.value,
                        userType.value,
                        userTypeId.value
                    )
                }
            }
        }
    }

    suspend fun saveTokens(data: LoginResponse) {
        dataStore.edit { pref ->
            pref[ACCESS_TOKEN_KEY] = "" + data.accessToken
            pref[REFRESH_TOKEN_KEY] = "" + data.refreshToken
            pref[USER_TYPE_KEY] = data.userType.name
            pref[USER_TYPE_ID_KEY] = data.userTypeId
        }
    }

    suspend fun updateTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { pref ->
            pref[ACCESS_TOKEN_KEY] = accessToken
            pref[REFRESH_TOKEN_KEY] = refreshToken
            println("Tokens refreshed")
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