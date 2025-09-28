package com.example.e_wholesaler.main.users.owner.dtos

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Serializable
data class Shop(
    val id: Long = -1,
    val name: String = "Shop Name",
    val gstNo: String = "GST Number",
    val address: String = "Address",
    val city: String = "City",
    val state: String = "State",
    var createdAt: String = "CreatedAt",
)

@RequiresApi(Build.VERSION_CODES.O)
fun Shop.formatDateAndGet(): Shop {
    val formattedDate = try {
        val dateTime = LocalDateTime.parse(this.createdAt)
        dateTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    } catch (e: DateTimeParseException) {
        this.createdAt
    }
    this.createdAt = formattedDate
    return this
}

fun Shop.hasDifferentData(shop: Shop): Boolean {
    return this.name != shop.name || this.gstNo != shop.gstNo || this.address != shop.address
            || this.city != shop.city || this.state != shop.state
}

fun Shop.hasNoBlankField(): Boolean {
    return this.name.isNotBlank() && this.gstNo.isNotBlank() && this.address.isNotBlank()
            && this.city.isNotBlank() && this.state.isNotBlank()
}