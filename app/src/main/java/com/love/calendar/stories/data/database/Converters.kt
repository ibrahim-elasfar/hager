// app/src/main/java/com/love/calendar/stories/data/database/Converters.kt
package com.love.calendar.stories.data.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return value.split("|||").filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString("|||")
    }
}