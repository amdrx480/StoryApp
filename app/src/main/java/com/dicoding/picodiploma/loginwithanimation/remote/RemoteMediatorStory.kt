package com.dicoding.picodiploma.loginwithanimation.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.picodiploma.loginwithanimation.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.model.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.room.DatabaseStory
import com.dicoding.picodiploma.loginwithanimation.room.RemoteKeysStory


@OptIn(ExperimentalPagingApi::class)
class RemoteMediatorStory(
  private val databaseStory: DatabaseStory,
  private val apiService: ApiService,
  private val token: String
) : RemoteMediator<Int, ListStoryItem>() {
  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, ListStoryItem>
  ): MediatorResult {

    val page = when (loadType) {
      LoadType.REFRESH -> {
        val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
        remoteKeys?.nextKeyStory?.minus(1) ?: INITIAL_PAGE_INDEX
      }
      LoadType.PREPEND -> {
        val remoteKeys = getRemoteKeyForFirstItem(state)
        val prevKey = remoteKeys?.prevKeyStory
          ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
        prevKey
      }
      LoadType.APPEND -> {
        val remoteKeys = getRemoteKeyForLastItem(state)
        val nextKey = remoteKeys?.nextKeyStory
          ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
        nextKey
      }
    }

    return try {
      val responseData =
        apiService.getAllStories("Bearer $token", page, state.config.pageSize).listStory
      val endOfPaginationReached = responseData.isEmpty()

      databaseStory.withTransaction {
        if (loadType == LoadType.REFRESH) {
          databaseStory.remoteKeysStoryDao().deleteRemoteKeys()
          databaseStory.storyDao().deleteAll()
        }

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1

        val keys = responseData.map {
          RemoteKeysStory(id = it.id, prevKeyStory = prevKey, nextKeyStory = nextKey)
        }

        databaseStory.remoteKeysStoryDao().insertAll(keys)
        databaseStory.storyDao().insertStory(responseData)
      }

      MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    } catch (exception: Exception) {
      MediatorResult.Error(exception)
    }
  }

  private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): RemoteKeysStory? {
    return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
      databaseStory.remoteKeysStoryDao().getRemoteKeysId(it.id)
    }
  }

  private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): RemoteKeysStory? {
    return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
      databaseStory.remoteKeysStoryDao().getRemoteKeysId(it.id)
    }
  }

  private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): RemoteKeysStory? {
    return state.anchorPosition?.let { position ->
      state.closestItemToPosition(position)?.id?.let {
        databaseStory.remoteKeysStoryDao().getRemoteKeysId(it)
      }
    }
  }

  override suspend fun initialize(): InitializeAction {
    return InitializeAction.LAUNCH_INITIAL_REFRESH
  }

  private companion object {
    const val INITIAL_PAGE_INDEX = 1
  }

}