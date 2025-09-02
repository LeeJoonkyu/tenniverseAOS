package com.heejae.tenniverse.data.model

import com.google.gson.annotations.SerializedName

data class FCMResponse (
    @SerializedName("multicast_id")
    val multicastID: Double,

    val success: Long,
    val failure: Long,

    @SerializedName("canonical_ids")
    val canonicalIDS: Long,

    val results: List<Result>
)

data class Result (
    @SerializedName("message_id")
    val messageID: String
)
