// app/src/main/java/com/love/calendar/stories/data/models/Story.kt
package com.love.calendar.stories.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey
    val day: Int,
    val title: String,
    val length: String,
    val message: String,
    val sentences: List<String>
)

data class StoryProgress(
    val day: Int,
    val lastReadSentence: Int,
    val isCompleted: Boolean,
    val lastReadDate: Long
)