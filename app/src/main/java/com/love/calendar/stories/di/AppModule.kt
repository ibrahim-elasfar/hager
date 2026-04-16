// app/src/main/java/com/love/calendar/stories/di/AppModule.kt
package com.love.calendar.stories.di

import android.content.Context
import androidx.room.Room
import com.love.calendar.stories.data.database.StoryDatabase
import com.love.calendar.stories.data.database.StoryDao
import com.love.calendar.stories.data.repository.StoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideStoryDatabase(@ApplicationContext context: Context): StoryDatabase {
        return StoryDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideStoryDao(database: StoryDatabase): StoryDao {
        return database.storyDao()
    }
    
    @Provides
    @Singleton
    fun provideStoryRepository(
        storyDao: StoryDao,
        @ApplicationContext context: Context
    ): StoryRepository {
        return StoryRepository(storyDao, context)
    }
}