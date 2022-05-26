package com.dicoding.picodiploma.loginwithanimation.AddStory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.RepositoryStory.RepositoryStory
import com.dicoding.picodiploma.loginwithanimation.api.ApiResponse
import com.dicoding.picodiploma.loginwithanimation.api.helper
import com.dicoding.picodiploma.loginwithanimation.model.UserModel
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val storyRepository: RepositoryStory) : ViewModel() {
  fun uploadImage(
    token: String,
    description: RequestBody,
    imageMultipart: MultipartBody.Part,
    lat: RequestBody? = null,
    lon: RequestBody? = null
  ) = storyRepository.postStory(token, description, imageMultipart, lat, lon)
}