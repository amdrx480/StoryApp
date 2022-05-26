package com.dicoding.picodiploma.loginwithanimation.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.picodiploma.loginwithanimation.model.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKeysStory::class],
    version = 2,
    exportSchema = false
)
abstract class DatabaseStory : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysStoryDao(): RemoteKeysStoryDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseStory? = null

        @JvmStatic
        fun getInstance(context: Context): DatabaseStory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseStory::class.java, "story.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}