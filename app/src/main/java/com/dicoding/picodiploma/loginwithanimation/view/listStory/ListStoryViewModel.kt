package com.dicoding.picodiploma.loginwithanimation.view.listStory

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.RepositoryStory.RepositoryStory
import com.dicoding.picodiploma.loginwithanimation.model.ListStoryItem

class ListStoryViewModel(
  private val storyRepository: RepositoryStory,
) : ViewModel() {

  fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
    return storyRepository.pagingStories(token).cachedIn(viewModelScope).asLiveData()
  }
}