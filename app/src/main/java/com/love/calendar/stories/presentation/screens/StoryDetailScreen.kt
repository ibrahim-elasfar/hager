// app/src/main/java/com/love/calendar/stories/presentation/screens/StoryDetailScreen.kt
package com.love.calendar.stories.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.love.calendar.stories.presentation.LoveCalendarViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryDetailScreen(
    day: Int,
    viewModel: LoveCalendarViewModel,
    onBack: () -> Unit
) {
    val story by viewModel.currentStory.collectAsStateWithLifecycle()
    val progress by viewModel.currentProgress.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    
    LaunchedEffect(day) {
        viewModel.loadStory(day)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = story?.title ?: "قصة اليوم",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "اليوم ${story?.day ?: day}",
                            fontSize = 12.sp,
                            color = Color(0xFF58a6ff)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleMusic() }) {
                        Icon(
                            if (viewModel.isMusicPlaying.value) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "موسيقى",
                            tint = Color(0xFF58a6ff)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
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
            if (story == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF58a6ff))
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Progress bar
                    val progressPercent = (progress?.lastReadSentence?.toFloat()?.div(story?.sentences?.size?.toFloat() ?: 1f) ?: 0f)
                    LinearProgressIndicator(
                        progress = { progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF58a6ff),
                        trackColor = Color(0xFF21262d)
                    )
                    
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(story?.sentences ?: emptyList()) { index, sentence ->
                            val isVisible = !isPlaying || index <= (progress?.lastReadSentence ?: -1)
                            
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn() + slideInHorizontally(
                                    initialOffsetX = { if (configuration.layoutDirection == androidx.compose.ui.unit.LayoutDirection.Rtl) -200 else 200 }
                                )
                            ) {
                                StorySentenceCard(
                                    sentence = sentence,
                                    isVisible = isVisible,
                                    onClick = {
                                        if (!isPlaying) {
                                            scope.launch {
                                                viewModel.updateProgress(index, index == (story?.sentences?.size?.minus(1)))
                                                listState.animateScrollToItem(index)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    
                    // Control buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    if (isPlaying) {
                                        viewModel.stopStory()
                                    } else {
                                        viewModel.startStory { index ->
                                            scope.launch {
                                                listState.animateScrollToItem(index)
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF238636)
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isPlaying) "إيقاف" else "تشغيل القصة")
                        }
                        
                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.resetProgress()
                                    listState.scrollToItem(0)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF21262d)
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إعادة")
                        }
                        
                        Button(
                            onClick = { viewModel.showSurprise() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFf14c4c)
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("مفاجأة")
                        }
                    }
                }
            }
        }
    }
    
    // Surprise dialog
    if (viewModel.showSurpriseDialog.value) {
        AlertDialog(
            onDismissRequest = { viewModel.showSurpriseDialog.value = false },
            title = {
                Text(
                    text = "💖 مفاجأة رومانسية 💖",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = story?.message ?: "أنتِ نجمتي الوحيدة",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "❤️",
                        fontSize = 48.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.showSurpriseDialog.value = false }) {
                    Text("💕", fontSize = 24.sp)
                }
            },
            containerColor = Color(0xFF1c2128),
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
fun StorySentenceCard(
    sentence: String,
    isVisible: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(enabled = !isVisible, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isVisible) Color(0xFF1a4a3a) else Color(0xFF1c2128)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = sentence,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 15.sp,
            lineHeight = 26.sp,
            color = if (isVisible) Color(0xFFc9d1d9) else Color(0xFF8b949e),
            textAlign = TextAlign.Right
        )
    }
}