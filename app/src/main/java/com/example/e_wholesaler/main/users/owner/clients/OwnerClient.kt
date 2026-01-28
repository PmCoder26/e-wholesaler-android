package com.example.e_wholesaler.main.users.owner.clients

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.e_wholesaler.ktor_client.RequestType
import com.example.e_wholesaler.ktor_client.RequestType.DELETE
import com.example.e_wholesaler.ktor_client.RequestType.GET
import com.example.e_wholesaler.ktor_client.RequestType.POST
import com.example.e_wholesaler.ktor_client.RequestType.PUT
import com.example.e_wholesaler.main.users.owner.dtos.AddProductForShopRequest
import com.example.e_wholesaler.main.users.owner.dtos.AddProductForShopResponse
import com.example.e_wholesaler.main.users.owner.dtos.AddSubProductsForShopRequest
import com.example.e_wholesaler.main.users.owner.dtos.AddSubProductsForShopResponse
import com.example.e_wholesaler.main.users.owner.dtos.DailyShopRevenue
import com.example.e_wholesaler.main.users.owner.dtos.HomeScreenDetails
import com.example.e_wholesaler.main.users.owner.dtos.Message
import com.example.e_wholesaler.main.users.owner.dtos.OwnerDetails
import com.example.e_wholesaler.main.users.owner.dtos.ProductIdentity
import com.example.e_wholesaler.main.users.owner.dtos.SellingUnit
import com.example.e_wholesaler.main.users.owner.dtos.SellingUnitRequest
import com.example.e_wholesaler.main.users.owner.dtos.SellingUnitUpdate
import com.example.e_wholesaler.main.users.owner.dtos.Shop
import com.example.e_wholesaler.main.users.owner.dtos.ShopAndWorkers
import com.example.e_wholesaler.main.users.owner.dtos.SubProduct2
import com.example.e_wholesaler.main.users.owner.dtos.Worker
import com.example.e_wholesaler.main.users.owner.dtos.WorkerDeleteRequest
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

    suspend fun getShopProducts(ownerId: Long, shopId: Long): List<ProductIdentity>? {
        return makeApiCall<List<ProductIdentity>, Any>(ownerId, GET, "/shop/$shopId/products", null)
    }

    suspend fun addProduct(
        ownerId: Long,
        shopId: Long,
        request: AddProductForShopRequest
    ): AddProductForShopResponse? {
        return makeApiCall(ownerId, POST, "/shop/$shopId/products", request)
    }

    suspend fun deleteProduct(ownerId: Long, shopId: Long, productId: Long): Boolean {
        return makeApiCall<Boolean, Any>(ownerId, DELETE, "/shop/$shopId/products/$productId", null)
            ?: false
    }

    suspend fun addShopSubProduct(
        ownerId: Long,
        shopId: Long,
        productId: Long,
        request: AddSubProductsForShopRequest
    ): AddSubProductsForShopResponse? {
        return makeApiCall(ownerId, POST, "/shop/$shopId/products/$productId/sub-products", request)
    }

    suspend fun getShopProductDetails(
        ownerId: Long,
        shopId: Long,
        productId: Long
    ): List<SubProduct2>? {
        return makeApiCall<List<SubProduct2>, Any>(
            ownerId,
            GET,
            "/shop/$shopId/products/$productId/sub-products",
            null
        )
    }

    suspend fun deleteShopSubProduct(ownerId: Long, shopId: Long, shopSubProductId: Long): Boolean {
        return makeApiCall<Boolean, Any>(
            ownerId,
            DELETE,
            "/shop/$shopId/sub-products/$shopSubProductId",
            null
        ) ?: false
    }

    suspend fun addProductSellingUnit(
        ownerId: Long,
        shopId: Long,
        shopSubProductId: Long,
        request: SellingUnitRequest
    ): SellingUnit? {
        return makeApiCall(
            ownerId,
            POST,
            "/shop/$shopId/sub-products/$shopSubProductId/selling-units",
            request
        )
    }

    suspend fun updateProductSellingUnit(
        ownerId: Long,
        shopId: Long,
        shopSubProductId: Long,
        sellingUnitId: Long,
        request: SellingUnitUpdate
    ): SellingUnit? {
        return makeApiCall(
            ownerId,
            PUT,
            "/shop/$shopId/sub-products/$shopSubProductId/selling-units/$sellingUnitId",
            request
        )
    }

    suspend fun deleteProductSellingUnit(
        ownerId: Long,
        shopId: Long,
        shopSubProductId: Long,
        sellingUnitId: Long
    ): Boolean {
        return makeApiCall<Boolean, Any>(
            ownerId,
            DELETE,
            "/shop/$shopId/sub-products/$shopSubProductId/selling-units/$sellingUnitId",
            null
        ) ?: false
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

    suspend fun deleteShopWorker(ownerId: Long, requestDTO: WorkerDeleteRequest): Message? {
        return makeApiCall(ownerId, DELETE, "/shops/worker", requestDTO)
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

            if (apiResponse.status == HttpStatusCode.NoContent) {
                return if (Boolean::class == ResponseType::class) true as ResponseType else null
            }
            
            val body = apiResponse.body<ApiResponse<ResponseType>>()

            if (apiResponse.status == HttpStatusCode.OK || apiResponse.status == HttpStatusCode.Created) return body.data
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
