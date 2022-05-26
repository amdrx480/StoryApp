package com.dicoding.picodiploma.loginwithanimation.RepositoryStory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.api.ApiResponse
import com.dicoding.picodiploma.loginwithanimation.model.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.room.DatabaseStory
import com.dicoding.picodiploma.loginwithanimation.room.ResultResponseStory
import com.dicoding.picodiploma.loginwithanimation.util.wrapEspressoIdlingResource
import com.dicoding.picodiploma.loginwithanimation.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.model.LoginResult
import com.dicoding.picodiploma.loginwithanimation.remote.RemoteMediatorStory
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RepositoryStory (
    private val DatabaseStory: DatabaseStory,
    private val apiService: ApiService,
) {
    fun login(email: String, pass: String): LiveData<ResultResponseStory<LoginResult>> =
        liveData {
            emit(ResultResponseStory.Loading)
            try {
                val response = apiService.login(email, pass)
                if (!response.error) {
                    emit(ResultResponseStory.Success(response.loginResult))
                } else {
                    Log.e(TAG, "Register Fail: ${response.message}")
                    emit(ResultResponseStory.Error(response.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Register Exception: ${e.message.toString()} ")
                emit(ResultResponseStory.Error(e.message.toString()))
            }
        }

    fun signup(name: String, email: String, pass: String): LiveData<ResultResponseStory<ApiResponse>> =
        liveData {
            emit(ResultResponseStory.Loading)
            try {
                val response = apiService.register(name, email, pass)
                if (!response.error) {
                    emit(ResultResponseStory.Success(response))
                } else {
                    Log.e(TAG, "Register Fail: ${response.message}")
                    emit(ResultResponseStory.Error(response.message))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Register Exception: ${e.message.toString()} ")
                emit(ResultResponseStory.Error(e.message.toString()))
            }
        }

    fun mapStory(token: String): LiveData<ResultResponseStory<List<ListStoryItem>>> =
        liveData {
            emit(ResultResponseStory.Loading)
            try {
                val response = apiService.getAllStoriesLocation("Bearer $token")
                if (!response.error) {
                    emit(ResultResponseStory.Success(response.listStory))
                } else {
                    Log.e(TAG, "GetStoryMap Fail: ${response.message}")
                    emit(ResultResponseStory.Error(response.message))
                }

            } catch (e: Exception) {
                Log.e(TAG, "GetStoryMap Exception: ${e.message.toString()} ")
                emit(ResultResponseStory.Error(e.message.toString()))
            }
        }

    fun postStory(
        token: String,
        description: RequestBody,
        imageMultipart: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): LiveData<ResultResponseStory<ApiResponse>> = liveData {
        emit(ResultResponseStory.Loading)
        try {
            val response = apiService.addStories("Bearer $token", description, imageMultipart, lat, lon)
            if (!response.error) {
                emit(ResultResponseStory.Success(response))
            } else {
                Log.e(TAG, "PostStory Fail: ${response.message}")
                emit(ResultResponseStory.Error(response.message))
            }
        } catch (e: Exception) {
            Log.e(TAG, "PostStory Exception: ${e.message.toString()} ")
            emit(ResultResponseStory.Error(e.message.toString()))
        }
    }

    fun pagingStories(token: String): Flow<PagingData<ListStoryItem>> {
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = RemoteMediatorStory(DatabaseStory, apiService, token),
                pagingSourceFactory = {
                    DatabaseStory.storyDao().getStory()
                }
            ).flow
        }
    }

    companion object {
        private const val TAG = "StoryRepository"
    }
}