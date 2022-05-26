package com.dicoding.picodiploma.loginwithanimation.MapsStory

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.RepositoryStory.RepositoryStory

class MapsStoryViewModel(private val storyRepository: RepositoryStory) : ViewModel() {

    fun getStories(token: String) = storyRepository.mapStory(token)

}