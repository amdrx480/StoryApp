package com.dicoding.picodiploma.loginwithanimation.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysStory(
    @PrimaryKey val id: String,
    val prevKeyStory: Int?,
    val nextKeyStory: Int?,
)