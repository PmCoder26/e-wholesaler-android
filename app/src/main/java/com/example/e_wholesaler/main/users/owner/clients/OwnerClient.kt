package com.example.e_wholesaler.main.users.owner.clients

import android.util.Log
import com.example.e_wholesaler.ktor_client.RequestType
import com.example.e_wholesaler.ktor_client.RequestType.DELETE
import com.example.e_wholesaler.ktor_client.RequestType.GET
import com.example.e_wholesaler.ktor_client.RequestType.POST
import com.example.e_wholesaler.ktor_client.RequestType.PUT
import com.example.e_wholesaler.main.users.owner.dtos.DailyShopRevenue
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import com.example.e_wholesaler.main.users.owner.dtos.Product
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.ktor_client.HOST_URL
import org.parimal.auth.TokenManager
import org.parimal.utils.ApiResponse

class OwnerClient(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) {

    private val OWNER_BASE_URL = "http://$HOST_URL:8090/api/v1/shops/owner"

    suspend fun getHomeScreenDetails(ownerId: Long): HomeScreenDetails? {
        return makeApiCall<HomeScreenDetails, Any>(ownerId, GET, "/home", null)
    }

    suspend fun getOwnerDetails(ownerId: Long): OwnerDetails? {
        return makeApiCall<OwnerDetails, Any>(ownerId, GET, "", null)
    }

    suspend fun getDailyRevenue(ownerId: Long): List<DailyShopRevenue>? {
        return makeApiCall<List<DailyShopRevenue>, Any>(ownerId, GET, "/daily-revenue", null)
    }

    suspend fun getOwnerShops(ownerId: Long): List<Shop>? {
        return makeApiCall<List<Shop>, Any>(ownerId, GET, "/shops", null)
    }

    suspend fun updateShopDetails(ownerId: Long, shop: Shop): Shop? {
        return makeApiCall<Shop, Shop>(ownerId, PUT, "/shop", shop)
    }

    suspend fun addNewShop(ownerId: Long, newShop: Shop): Shop? {
        return makeApiCall<Shop, Shop>(ownerId, POST, "/shop", newShop)
    }

    suspend fun getShopProducts(ownerId: Long, shopId: Long): MutableList<Product>? {
        return makeApiCall<MutableList<Product>, Any>(ownerId, GET, "/shop/$shopId/products", null)
    }

    private suspend inline fun <reified ResponseType, reified RequestBodyType> makeApiCall(
        ownerId: Long, requestType: RequestType, url: String, requestBody: RequestBodyType?
    ): ResponseType? {
        try {
            val apiResponse = when (requestType) {
                GET -> httpClient.get("$OWNER_BASE_URL/$ownerId$url") {
                    requestBody?.let {
                        contentType(ContentType.Application.Json)
                        setBody(it)
                    }
                    authHeader()
                }

                POST -> httpClient.post("$OWNER_BASE_URL/$ownerId$url") {
                    requestBody?.let {
                        contentType(ContentType.Application.Json)
                        setBody(it)
                    }
                    authHeader()
                }

                PUT -> httpClient.put("$OWNER_BASE_URL/$ownerId$url") {
                    requestBody?.let {
                        contentType(ContentType.Application.Json)
                        setBody(it)
                    }
                    authHeader()
                }

                DELETE -> httpClient.delete("$OWNER_BASE_URL/$ownerId$url") {
                    requestBody?.let { setBody(it) }
                    authHeader()
                }
            }.body<ApiResponse<ResponseType>>()
            return apiResponse.data
        } catch (e: Exception) {
            Log.e("Api call error: ", e.message.toString())
            return null
        }
    }

    private fun HttpRequestBuilder.authHeader() {
        header("Authorization", "${tokenManager.tokenState2.value.accessToken}")
    }

}