package com.heejae.tenniverse.data.service

import com.heejae.tenniverse.data.model.FCMResponse
import com.heejae.tenniverse.data.model.FCMSendModel
import com.heejae.tenniverse.data.model.FCMSendModelIOS
import com.heejae.tenniverse.util.FCM_SEND
import retrofit2.http.Body
import retrofit2.http.POST

interface FCMApiService {

    @POST(FCM_SEND)
    suspend fun sendAskWantToParticipate(
        @Body model: FCMSendModel
    ): FCMResponse

    @POST(FCM_SEND)
    suspend fun sendAskWantToParticipateIOS(
        @Body model: FCMSendModelIOS
    ): FCMResponse
}