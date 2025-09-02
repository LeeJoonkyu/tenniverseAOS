package com.heejae.tenniverse.data.model

import java.lang.Exception

sealed interface Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val exception: Exception): Resource<Nothing>
}