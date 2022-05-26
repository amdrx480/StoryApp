package com.dicoding.picodiploma.loginwithanimation.view.listStory

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.DetailStory.DetailStoryAppActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.api.helper
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemRowPhotoBinding
import com.dicoding.picodiploma.loginwithanimation.model.ListStoryItem
import java.util.*

class ListStoryAppAdapter :
    PagingDataAdapter<ListStoryItem, ListStoryAppAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class ViewHolder(private var binding: ItemRowPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            with(binding) {
                Glide.with(imgVPhoto)
                    .load(story.photoUrl) // URL Avatar
                    .placeholder(R.drawable.ic_place_default_holder)
                    .error(R.drawable.ic_broken_image)
                    .into(imgVPhoto)
                tvName.text = story.name
                tvDescription.text = story.description
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tvCreatedATime.text =
                        binding.root.resources.getString(R.string.created_add,
                            helper.dateFormat(story.createdAt, TimeZone.getDefault().id))
                }
                cardPhoto.setOnClickListener {
                    val intent = Intent(it.context, DetailStoryAppActivity::class.java)
                    intent.putExtra(DetailStoryAppActivity.EXTRA_STORY, story)
                    it.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}