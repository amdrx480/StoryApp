package com.dicoding.picodiploma.loginwithanimation.room

sealed class ResultResponseStory<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultResponseStory<T>()
    data class Error(val error: String) : ResultResponseStory<Nothing>()
    object Loading : ResultResponseStory<Nothing>()
}