package com.dicoding.picodiploma.loginwithanimation.view.signup


import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.RepositoryStory.RepositoryStory

class SignupViewModel(private val storyRepository: RepositoryStory) : ViewModel() {

    fun saveUser(name: String, email: String, pass: String) =
        storyRepository.signup(name,email, pass)

}