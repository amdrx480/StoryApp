package com.dicoding.picodiploma.loginwithanimation.DetailStory


import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.model.ListStoryItem

class DetailStoryAppViewModel: ViewModel() {
  lateinit var storyItem: ListStoryItem

  fun setDetailStory(story: ListStoryItem) : ListStoryItem{
    storyItem = story
    return storyItem
  }

}