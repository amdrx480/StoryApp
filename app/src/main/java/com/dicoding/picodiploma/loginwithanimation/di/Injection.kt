package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.RepositoryStory.RepositoryStory
import com.dicoding.picodiploma.loginwithanimation.room.DatabaseStory
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig

object Injection {
  fun provideStoryRepository(context: Context): RepositoryStory {
    val databaseStory = DatabaseStory.getInstance(context)
    val apiService = ApiConfig.ApiService()
    return RepositoryStory(databaseStory, apiService)
  }
}