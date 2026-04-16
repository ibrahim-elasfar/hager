// app/src/main/java/com/love/calendar/stories/presentation/MainActivity.kt
package com.love.calendar.stories.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.love.calendar.stories.presentation.screens.CalendarScreen
import com.love.calendar.stories.presentation.screens.StoryDetailScreen
import com.love.calendar.stories.presentation.theme.LoveCalendarTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
        
        setContent {
            LoveCalendarTheme {
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = false
                    )
                    systemUiController.setNavigationBarColor(
                        color = Color(0xFF0a0c10),
                        darkIcons = false
                    )
                }
                
                LoveCalendarApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoveCalendarApp(
    viewModel: LoveCalendarViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentDay by viewModel.currentDay.collectAsStateWithLifecycle()
    val allStories by viewModel.allStories.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    
    var showSplash by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        delay(2500)
        showSplash = false
        viewModel.loadStories()
    }
    
    if (showSplash) {
        SplashScreen()
    } else {
        NavHost(
            navController = navController,
            startDestination = "calendar"
        ) {
            composable("calendar") {
                CalendarScreen(
                    currentDay = currentDay,
                    stories = allStories,
                    isLoading = isLoading,
                    onDayClick = { day ->
                        navController.navigate("story/$day")
                    },
                    onResetToToday = {
                        viewModel.resetToToday()
                    }
                )
            }
            
            composable(
                route = "story/{day}",
                arguments = listOf(
                    androidx.navigation.navArgument("day") { 
                        type = androidx.navigation.NavType.IntType 
                    }
                )
            ) { backStackEntry ->
                val day = backStackEntry.arguments?.getInt("day") ?: 1
                StoryDetailScreen(
                    day = day,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun SplashScreen() {
    var scale by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        scale = 1f
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1a0a2a), Color(0xFF0a0c10)),
                    radius = 1000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale),
                shape = CircleShape,
                color = Color.Transparent,
                shadowElevation = 20.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "⭐",
                        fontSize = 80.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "تقويم الحب",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "30 يوم - 30 قصة",
                fontSize = 16.sp,
                color = Color(0xFF58a6ff)
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Color(0xFF58a6ff),
                strokeWidth = 3.dp
            )
        }
    }
}