package com.example.githubuser2.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class DarkViewModelFactory(private val pref: DarkPreferences): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DarkViewModel::class.java)) {
            return DarkViewModel(pref) as T
        }
        throw IllegalArgumentException("Unkow ViewModel Class "+ modelClass.name)
    }
}