package com.dicoding.picodiploma.loginwithanimation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val name: String,
    val email: String,
    val token: String,
    val userId: String,
    val password: String,
    val isLogin: Boolean
): Parcelable