package com.heejae.tenniverse.data.model

import com.google.gson.annotations.SerializedName

data class FCMSendModel(
    @SerializedName("to") val token: String,
    @SerializedName("data") val data: DataModel? = null
)

data class FCMSendModelIOS(
    @SerializedName("to") val token: String,
    @SerializedName("notification") val notificationModel: NotificationModel,
    @SerializedName("data") val data: DataModel? = null
) {
    data class NotificationModel(
        val title: String,
        val body: String,
        val rentId: String,
        val sound: String = "default"
    )
}

data class DataModel(
    val title: String,
    val body: String,
    val rentId: String
)
