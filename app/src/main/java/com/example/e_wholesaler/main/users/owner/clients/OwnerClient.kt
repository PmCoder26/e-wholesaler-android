package com.example.e_wholesaler.main.users.owner.clients

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.e_wholesaler.ktor_client.RequestType
import com.example.e_wholesaler.ktor_client.RequestType.DELETE
import com.example.e_wholesaler.ktor_client.RequestType.GET
import com.example.e_wholesaler.ktor_client.RequestType.POST
import com.example.e_wholesaler.ktor_client.RequestType.PUT
import com.example.e_wholesaler.main.users.owner.dtos.DailyShopRevenue
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.Message
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import com.example.e_wholesaler.main.users.owner.dtos.Product
import com.example.e_wholesaler.main.users.owner.dtos.ProductRemoveRequest
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.dtos.ShopAndWorkers
import com.example.e_wholesaler.main.users.owner.dtos.SubProductAddRequest
import com.example.e_wholesaler.main.users.owner.dtos.SubProductAddResponse
import com.example.e_wholesaler.main.users.owner.dtos.SubProductRemoveRequest
import com.example.e_wholesaler.main.users.owner.dtos.SubProductUpdateRequest
import com.example.e_wholesaler.main.users.owner.dtos.Worker
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.project.ktor_client.HOST_URL
import org.parimal.utils.ApiResponse

class OwnerClient(private val httpClient: HttpClient, private val context: Context) {

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
        return makeApiCall(ownerId, PUT, "/shop", shop)
    }

    suspend fun addNewShop(ownerId: Long, newShop: Shop): Shop? {
        return makeApiCall(ownerId, POST, "/shop", newShop)
    }

    suspend fun getShopProducts(ownerId: Long, shopId: Long): MutableList<Product>? {
        return makeApiCall<MutableList<Product>, Any>(ownerId, GET, "/shop/$shopId/products", null)
    }

    suspend fun removeShopSubProduct(ownerId: Long, requestDTO: SubProductRemoveRequest): Message? {
        return makeApiCall(ownerId, DELETE, "/shop/products/shop-sub-product", requestDTO)
    }

    suspend fun addShopSubProduct(
        ownerId: Long,
        requestDTO: SubProductAddRequest
    ): SubProductAddResponse? {
        return makeApiCall(ownerId, POST, "/shop/products/shop-sub-product", requestDTO)
    }

    suspend fun updateShopSubProduct(ownerId: Long, requestDTO: SubProductUpdateRequest): Message? {
        return makeApiCall(ownerId, PUT, "/shop/products/shop-sub-product", requestDTO)
    }

    suspend fun removeProduct(ownerId: Long, requestDTO: ProductRemoveRequest): Message? {
        return makeApiCall(ownerId, DELETE, "/shop/products/product", requestDTO)
    }

    suspend fun getShopWorkers(ownerId: Long, shopId: Long): ShopAndWorkers? {
        return makeApiCall<ShopAndWorkers, Any>(ownerId, GET, "/shop/$shopId/workers", null)
    }

    suspend fun addShopWorker(ownerId: Long, worker: Worker): Worker? {
        return makeApiCall(ownerId, POST, "/shops/worker", worker)
    }

    suspend fun updateShopWorker(ownerId: Long, worker: Worker): Worker? {
        return makeApiCall(ownerId, PUT, "/shops/worker", worker)
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
                }

                POST -> httpClient.post("$OWNER_BASE_URL/$ownerId$url") {
                    requestBody?.let {
                        contentType(ContentType.Application.Json)
                        setBody(it)
                    }
                }

                PUT -> httpClient.put("$OWNER_BASE_URL/$ownerId$url") {
                    requestBody?.let {
                        contentType(ContentType.Application.Json)
                        setBody(it)
                    }
                }

                DELETE -> httpClient.delete("$OWNER_BASE_URL/$ownerId$url") {
                    contentType(ContentType.Application.Json)
                    requestBody?.let { setBody(it) }
                }
            }
            val body = apiResponse.body<ApiResponse<ResponseType>>()

            if (apiResponse.status == HttpStatusCode.OK) return body.data
            else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        body.error?.message ?: "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return null
            }
        } catch (e: Exception) {
            Log.e("Api call error: ", e.message.toString())
            return null
        }
    }
}