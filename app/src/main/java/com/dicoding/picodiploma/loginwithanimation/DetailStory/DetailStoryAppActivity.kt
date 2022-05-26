package com.dicoding.picodiploma.loginwithanimation.DetailStory

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.api.helper
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryAppBinding
import com.dicoding.picodiploma.loginwithanimation.model.ListStoryItem
import java.util.*

class DetailStoryAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryAppBinding
    private lateinit var storyList: ListStoryItem

    private val detailViewModel: DetailStoryAppViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Post"
        storyList = intent.getParcelableExtra(EXTRA_STORY)!!
        detailViewModel.setDetailStory(storyList)

        detailDisplayResult()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun detailDisplayResult() {
        with(binding){
            txtVName.text = detailViewModel.storyItem.name
            txtVCreatedTime.text = getString(R.string.created_add, helper.dateFormat(detailViewModel.storyItem.createdAt,
                TimeZone.getDefault().id ))
            txtVDescription.text = detailViewModel.storyItem.description

            Glide.with(imgVStory)
                .load(detailViewModel.storyItem.photoUrl)
                .placeholder(R.drawable.ic_place_default_holder)
                .error(R.drawable.ic_broken_image)
                .into(imgVStory)
        }
    }

    companion object {
        const val EXTRA_STORY = "story"
    }
}