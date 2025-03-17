package org.parimal.auth

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.withContext
import org.example.project.ktor_client.HOST_URL
import org.parimal.auth.dtos.LoginRequest
import org.parimal.auth.dtos.LoginResponse
import org.parimal.auth.dtos.SignUpRequest
import org.parimal.auth.dtos.SignUpResponse
import org.parimal.utils.ApiResponse

class AuthClient(
    private val httpClient: HttpClient? = null,
    private val tokenManager: TokenManager? = null,
) {

    suspend fun signUp(signUpRequest: SignUpRequest): Boolean {
        val response = try{
            httpClient!!.post(
                urlString = "http://$HOST_URL:8090/api/v1/users/user/signup",
            ){
                contentType(ContentType.Application.Json)
                setBody(signUpRequest)
            }.body<ApiResponse<SignUpResponse>>()
        } catch (e: Exception){
            println("SignUp error: ${e.message}")
            null
        }
        response?.error?.let { error ->
            println("SignUp response error: ${error.message}")
            return false
        }
        return true
    }

    suspend fun login(loginRequest: LoginRequest): Boolean {
        val response = try{
            httpClient!!.post(
                urlString = "http://$HOST_URL:8090/api/v1/users/auth/login"
            ){
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }.body<ApiResponse<LoginResponse>>()
        } catch (e: Exception) {
            println("Login error: ${e.message}")
            null
        }
        response?.data?.let { data ->
            tokenManager?.saveTokens(data)
            return true
        }
        response?.error?.let { error ->
            println("Login response error: ${error.message}")
        }
        return false
    }
//
//    fun checkTokens(): Boolean {
//        val tokenState = tokenManager?.tokenState2?.value
//        return tokenState?.accessToken != null && tokenState.refreshToken != null
//    }
//
//    suspend fun logout() {
//        tokenManager?.clearTokens()
//    }


}