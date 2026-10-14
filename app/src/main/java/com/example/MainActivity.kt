package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.ChatViewModel
import com.example.ui.MaieutikaViewModel
import com.example.ui.MainViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.screens.ArchiveScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DiagnosisScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.RealityTrendsScreen
import com.example.ui.screens.ScenarioScreen
import com.example.ui.screens.ZebraScreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.SlateGray
import androidx.compose.ui.platform.LocalContext
import com.example.data.repository.UserSessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    var isLoggedIn by remember { mutableStateOf(sessionManager.isLoggedIn()) }

    if (!isLoggedIn) {
        AuthScreen(
            onAuthSuccess = { name ->
                sessionManager.saveUser(name)
                isLoggedIn = true
            }
        )
    } else {
        val viewModel: MainViewModel = viewModel()
        AppMainContent(
            viewModel = viewModel,
            onLogout = {
                sessionManager.logout()
                viewModel.logout()
                isLoggedIn = false
            }
        )
    }
}

@Composable
fun AppMainContent(
    viewModel: MainViewModel,
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = ObsidianBlack,
        bottomBar = {
            NavigationBar(
                containerColor = ObsidianCard,
                contentColor = SlateGray,
                tonalElevation = 0.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.ONBOARDING,
                    onClick = { viewModel.navigateTo(AppScreen.ONBOARDING) },
                    icon = { Icon(Icons.Default.Create, contentDescription = "Diagnosis Baru", modifier = Modifier.size(20.dp)) },
                    label = { Text("Baru", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianBlack,
                        selectedTextColor = GoldLight,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    )
                )

                if (uiState.currentSession != null) {
                    NavigationBarItem(
                        selected = uiState.currentScreen == AppScreen.DIAGNOSIS,
                        onClick = { viewModel.navigateTo(AppScreen.DIAGNOSIS) },
                        icon = { Icon(Icons.Default.CompassCalibration, contentDescription = "Diagnosis", modifier = Modifier.size(20.dp)) },
                        label = { Text("Diagnosis", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianBlack,
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = SlateGray,
                            unselectedTextColor = SlateGray
                        )
                    )

                    NavigationBarItem(
                        selected = uiState.currentScreen == AppScreen.SCENARIO,
                        onClick = { viewModel.navigateTo(AppScreen.SCENARIO) },
                        icon = { Icon(Icons.Default.ViewCarousel, contentDescription = "Skenario", modifier = Modifier.size(20.dp)) },
                        label = { Text("Skenario", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianBlack,
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = SlateGray,
                            unselectedTextColor = SlateGray
                        )
                    )

                    NavigationBarItem(
                        selected = uiState.currentScreen == AppScreen.ZEBRA,
                        onClick = { viewModel.navigateTo(AppScreen.ZEBRA) },
                        icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "ZEBRA", modifier = Modifier.size(20.dp)) },
                        label = { Text("ZEBRA", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianBlack,
                            selectedTextColor = GoldLight,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = SlateGray,
                            unselectedTextColor = SlateGray
                        )
                    )
                }

                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.ARCHIVE,
                    onClick = { viewModel.navigateTo(AppScreen.ARCHIVE) },
                    icon = { Icon(Icons.Default.History, contentDescription = "Arsip", modifier = Modifier.size(20.dp)) },
                    label = { Text("Arsip", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianBlack,
                        selectedTextColor = GoldLight,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    )
                )

                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.CHAT,
                    onClick = { viewModel.navigateTo(AppScreen.CHAT) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Dialog", modifier = Modifier.size(20.dp)) },
                    label = { Text("Dialog", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianBlack,
                        selectedTextColor = GoldLight,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    )
                )

                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.GUIDE,
                    onClick = { viewModel.navigateTo(AppScreen.GUIDE) },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Panduan", modifier = Modifier.size(20.dp)) },
                    label = { Text("Panduan", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ObsidianBlack,
                        selectedTextColor = GoldLight,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBlack)
        ) {
            when (uiState.currentScreen) {
                AppScreen.ONBOARDING -> OnboardingScreen(viewModel = viewModel, uiState = uiState)
                AppScreen.DIAGNOSIS -> DiagnosisScreen(viewModel = viewModel, uiState = uiState)
                AppScreen.SCENARIO -> ScenarioScreen(viewModel = viewModel, uiState = uiState)
                AppScreen.ZEBRA -> ZebraScreen(viewModel = viewModel, uiState = uiState)
                AppScreen.ARCHIVE -> ArchiveScreen(viewModel = viewModel, uiState = uiState, onLogout = onLogout)
                AppScreen.REALITY_TRENDS -> RealityTrendsScreen(viewModel = viewModel, uiState = uiState)
                AppScreen.GUIDE -> GuideScreen(viewModel = viewModel)
                AppScreen.CHAT -> {
                    val maieutikaViewModel: MaieutikaViewModel = viewModel()
                    ChatScreen(
                        viewModel = maieutikaViewModel,
                        onBack = { viewModel.navigateTo(AppScreen.ONBOARDING) }
                    )
                }
            }
        }
    }
}
