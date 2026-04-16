// app/src/main/java/com/love/calendar/stories/presentation/screens/CalendarScreen.kt
package com.love.calendar.stories.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.love.calendar.stories.data.models.Story
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    currentDay: Int,
    stories: List<Story>,
    isLoading: Boolean,
    onDayClick: (Int) -> Unit,
    onResetToToday: () -> Unit
) {
    val today = Calendar.getInstance()
    val currentMonth = today.get(Calendar.MONTH)
    val currentYear = today.get(Calendar.YEAR)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "⭐ تقويم الحب",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${getMonthName(currentMonth)} $currentYear",
                            fontSize = 12.sp,
                            color = Color(0xFF8b949e)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onResetToToday) {
                        Icon(
                            Icons.Default.Today,
                            contentDescription = "اليوم",
                            tint = Color(0xFF58a6ff)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color(0xFF1a1c23)
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0a0c10), Color(0xFF1a0a2a))
                    )
                )
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF58a6ff))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stories) { story ->
                        CalendarDayCard(
                            day = story.day,
                            title = story.title,
                            length = story.length,
                            isToday = story.day == currentDay,
                            onClick = { onDayClick(story.day) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDayCard(
    day: Int,
    title: String,
    length: String,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val cardColor = if (isToday) {
        Brush.linearGradient(
            colors = listOf(Color(0xFF238636), Color(0xFF2ea043))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFF1c2128), Color(0xFF21262d))
        )
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardColor)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title.take(2),
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (length) {
                        "قصيرة" -> "📖 قصيرة"
                        "طويلة" -> "📚 طويلة"
                        "طويلة جداً" -> "📚📚 طويلة جداً"
                        else -> "📖 متوسطة"
                    },
                    fontSize = 9.sp,
                    color = Color(0xFF8b949e),
                    textAlign = TextAlign.Center
                )
                if (isToday) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⭐ اليوم",
                        fontSize = 8.sp,
                        color = Color(0xFFf6d365)
                    )
                }
            }
        }
    }
}

private fun getMonthName(month: Int): String {
    return when (month) {
        0 -> "يناير"
        1 -> "فبراير"
        2 -> "مارس"
        3 -> "أبريل"
        4 -> "مايو"
        5 -> "يونيو"
        6 -> "يوليو"
        7 -> "أغسطس"
        8 -> "سبتمبر"
        9 -> "أكتوبر"
        10 -> "نوفمبر"
        11 -> "ديسمبر"
        else -> ""
    }
}