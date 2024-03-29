package com.dicoding.picodiploma.loginwithanimation.api

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.picodiploma.loginwithanimation.model.ListStoryItem


class DiffCallback(
  private val mOldFavList: List<ListStoryItem>,
  private val mNewFavList: List<ListStoryItem>
) : DiffUtil.Callback() {

  override fun getOldListSize() = mOldFavList.size

  override fun getNewListSize() = mNewFavList.size

  override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
     mOldFavList[oldItemPosition].id == mNewFavList[newItemPosition].id

  override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
    val oldEmployee = mOldFavList[oldItemPosition]
    val newEmployee = mNewFavList[newItemPosition]
    return oldEmployee.id == newEmployee.id
  }
}