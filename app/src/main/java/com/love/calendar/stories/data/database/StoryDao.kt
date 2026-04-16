// app/src/main/java/com/love/calendar/stories/data/database/StoryDao.kt
package com.love.calendar.stories.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.love.calendar.stories.data.models.Story

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories WHERE day = :day")
    suspend fun getStoryByDay(day: Int): Story?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: Story)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStories(stories: List<Story>)
    
    @Query("SELECT * FROM stories ORDER BY day")
    suspend fun getAllStories(): List<Story>
    
    @Query("SELECT COUNT(*) FROM stories")
    suspend fun getCount(): Int
}