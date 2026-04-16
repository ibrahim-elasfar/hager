// app/src/main/java/com/love/calendar/stories/data/repository/StoryRepository.kt
package com.love.calendar.stories.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.love.calendar.stories.data.database.StoryDao
import com.love.calendar.stories.data.models.Story
import com.love.calendar.stories.data.models.StoryProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "story_progress")

@Singleton
class StoryRepository @Inject constructor(
    private val storyDao: StoryDao,
    private val context: Context
) {
    companion object {
        private val CURRENT_DAY = intPreferencesKey("current_day")
        private val START_DATE = longPreferencesKey("start_date")
    }

    suspend fun insertAllStories(stories: List<Story>) {
        storyDao.insertAllStories(stories)
    }

    suspend fun getStory(day: Int): Story? {
        return storyDao.getStoryByDay(day)
    }

    suspend fun getAllStories(): List<Story> {
        return storyDao.getAllStories()
    }

    suspend fun getStoryCount(): Int {
        return storyDao.getCount()
    }

    fun getStoryProgress(day: Int): Flow<StoryProgress> {
        return context.dataStore.data.map { preferences ->
            val lastReadSentence = preferences[intPreferencesKey("day_${day}_sentence")] ?: 0
            val isCompleted = preferences[booleanPreferencesKey("day_${day}_completed")] ?: false
            val lastReadDate = preferences[longPreferencesKey("day_${day}_date")] ?: 0L
            StoryProgress(day, lastReadSentence, isCompleted, lastReadDate)
        }
    }

    suspend fun updateProgress(day: Int, sentenceIndex: Int, isCompleted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[intPreferencesKey("day_${day}_sentence")] = sentenceIndex
            preferences[booleanPreferencesKey("day_${day}_completed")] = isCompleted
            preferences[longPreferencesKey("day_${day}_date")] = System.currentTimeMillis()
        }
    }

    suspend fun getCurrentDay(): Int {
        val preferences = context.dataStore.data.collect { it } as? Preferences
        val startTime = preferences?.get(START_DATE) ?: run {
            context.dataStore.edit { it[START_DATE] = System.currentTimeMillis() }
            System.currentTimeMillis()
        }
        
        val daysPassed = ((System.currentTimeMillis() - startTime) / (1000 * 60 * 60 * 24)).toInt()
        return (daysPassed % 30).coerceIn(1..30)
    }

    suspend fun resetToToday() {
        context.dataStore.edit { preferences ->
            preferences[START_DATE] = System.currentTimeMillis()
        }
    }
}