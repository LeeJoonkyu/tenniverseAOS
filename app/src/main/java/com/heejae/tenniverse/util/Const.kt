package com.heejae.tenniverse.util


/**
 *  prefs
 */

const val PREFERENCE = "pref"
const val PREF_USER_UID = "PREF_USER_UID"

/**
 *  Retrofit
 */

const val BASE_URL = "https://fcm.googleapis.com/"
const val FCM_SEND = "fcm/send"
const val HEADER_KEY_CONTENT_TYPE = "Content-Type"
const val HEADER_KEY_AUTHORIZATION = "Authorization"
const val HEADER_VALUE_CONTENT_TYPE = "application/json"
const val HEADER_VALUE_AUTHORIZATION = "key=AAAAIwY_eoU:APA91bGK7P3UUODt9blOQ9PF3Ao0sFrVvZTrEPXu7o-vtG0BNFTeuJ_fTnWlPRf7zaol0TLODzGLsxqdOVP64RIdMztrcwI-pkx5df1S3Ql2nXu9AT7l5DNinTpG6Gk5T1xxCYw0-nl2"


/**
 *  FireStore
 */

const val FS_FIELD_REGISTERED = "registered"

const val RENT_MEMBER = "member"
const val RENT_WAITINGS = "waitings"
const val RENT_CLOSED = "closed"
const val RENT_TYPE = "type"
const val RENT_DATE = "date"

const val USER_RESERVATIONS = "reservations"
const val USER_DISPLAY_NAME = "displayName"
const val USER_GENDER = "gender"
const val USER_CAREER = "career"
const val USER_PROFILE_URL = "profileUrl"
const val USER_FCM_TOKEN = "fcmToken"
const val USER_REGISTERED = "registered"
const val USER_OS = "os"

/**
 *  bundle
 */

const val PUT_EXTRA_USER = "PUT_EXTRA_USER"
const val PUT_EXTRA_RENT = "PUT_EXTRA_RENT"
const val PUT_EXTRA_WEEKLY_CREATE_COUNT = "PUT_EXTRA_WEEKLY_CREATE_COUNT"
const val PUT_EXTRA_RENT_MODEL = "PUT_EXTRA_RENT_MODEL"
const val PUT_EXTRA_RENT_UID = "PUT_EXTRA_RENT_UID"
const val PUT_EXTRA_RENT_MEMBER_COUNT = "PUT_EXTRA_RENT_MEMBER_COUNT"
const val PUT_EXTRA_IS_WEEKLY = "PUT_EXTRA_IS_WEEKLY"
const val PUT_EXTRA_FROM_FCM_RENT_UID = "PUT_EXTRA_FROM_FCM"