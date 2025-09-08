package com.example.e_wholesaler.main.users.owner.clients

import android.util.Log
import com.example.e_wholesaler.main.users.owner.dtos.DailyShopRevenue
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import org.example.project.ktor_client.HOST_URL
import org.parimal.auth.TokenManager
import org.parimal.utils.ApiResponse

class OwnerClient(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) {

    suspend fun getOwnerIdByMobileNumber(username: String): Long? {
        val response = try {
            httpClient.get(
                urlString = "http://$HOST_URL:8090/api/v1/shops/owner/$username/id"
            ) {
                header("Authorization", tokenManager.tokenState2.value.accessToken)
            }.body<ApiResponse<Long>>()
        } catch (e: Exception) {
            Log.e("Home screen details fetch error: ", e.message.toString())
            null
        }
        return response?.data
    }

    suspend fun getHomeScreenDetails(ownerId: Long): HomeScreenDetails? {
        val response = try {
            httpClient.get(
                urlString = "http://$HOST_URL:8090/api/v1/shops/owner/$ownerId/home"
            ) {
                header("Authorization", tokenManager.tokenState2.value.accessToken)
            }.body<ApiResponse<HomeScreenDetails>>()
        } catch (e: Exception) {
            generateLog("Home screen details fetch error: ", e)
            null
        }
        return response?.data
    }

    suspend fun getOwnerDetails(ownerId: Long): OwnerDetails? {
        val response = try {
            httpClient.get(
                urlString = "http://$HOST_URL:8090/api/v1/shops/owner/$ownerId"
            ) {
                header("Authorization", tokenManager.tokenState2.value.accessToken)
            }.body< ApiResponse<OwnerDetails>>()
        } catch (e: Exception) {
            generateLog("Owner details fetch error: ", e)
            null
        }
        return response?.data
    }

    suspend fun getDailyRevenue(ownerId: Long): List<DailyShopRevenue>? {
        val response = try {
            httpClient.get(
                urlString = "http://$HOST_URL:8090/api/v1/shops/owner/$ownerId/daily-revenue"
            ) {
                header("Authorization", tokenManager.tokenState2.value.accessToken)
            }.body<ApiResponse<List<DailyShopRevenue>>>()
        } catch (e: Exception) {
            generateLog("Owner daily revenue fetch error: ", e)
            null
        }
        return response?.data
    }

    private fun generateLog(title: String, e: Exception) {
        Log.e(title , e.message.toString())
    }

}