package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.*
import com.dicoding.picodiploma.loginwithanimation.RepositoryStory.RepositoryStory

class LoginViewModel(private val repositoryStory: RepositoryStory): ViewModel()  {

    fun login(email: String, pass: String) =
        repositoryStory.login(email, pass)

}