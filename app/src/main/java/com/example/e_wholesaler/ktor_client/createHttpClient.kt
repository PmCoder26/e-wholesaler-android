package org.example.project.ktor_client

import android.util.Log
import com.example.e_wholesaler.auth.dtos.AccessToken
import com.example.e_wholesaler.auth.dtos.RefreshToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.parimal.auth.TokenManager
import org.parimal.utils.ApiResponse

fun createHttpClient(engine: HttpClientEngine, tokenManager: TokenManager): HttpClient {
    return HttpClient(engine) {
        install(Logging){
            level = LogLevel.ALL
            logger = object: Logger {
                override fun log(message: String) {
                    Log.v("KtorLogger", message)
                }
            }
        }
        install(ContentNegotiation){
            json(
                json = Json {
                    ignoreUnknownKeys = true        // helpful if unknown data comes, hence preventing from crashing.
                },
                contentType = ContentType.Application.Json
            )
        }
        install(Auth){
            bearer {
                val tempClient = HttpClient(engine) {
                    install(ContentNegotiation) {
                        json(
                            json = Json {
                                ignoreUnknownKeys = true
                            },
                            contentType = ContentType.Application.Json
                        )
                    }
                }
                loadTokens {
                    val tokenState = tokenManager.tokenState2.value
                    BearerTokens(
                        tokenState.accessToken ?: "",
                        tokenState.refreshToken ?: ""
                    )
                }
                refreshTokens {
                    val refreshToken = oldTokens?.refreshToken ?: ""
                    val newAccessToken = refreshTokens(tempClient, refreshToken)
                    newAccessToken?.let { newToken ->
                        tokenManager.updateTokens(newAccessToken, refreshToken)
                        return@refreshTokens BearerTokens(newToken, refreshToken)
                    }
                }
            }
        }
    }
}

private suspend fun refreshTokens(httpClient: HttpClient, refreshToken: String): String? {
    val response = try {
        httpClient.post(
            urlString = "http://$HOST_URL:8090/api/v1/users/auth/refresh-token"
        ) {
            headers.remove("Authorization")
            contentType(ContentType.Application.Json)
            setBody(
                RefreshToken(refreshToken)
            )
        }.body<ApiResponse<AccessToken>>()
    } catch (e: Exception) {
        println("Token refresh error: ${e.message}")
        null
    }
    return response?.data?.accessToken
}