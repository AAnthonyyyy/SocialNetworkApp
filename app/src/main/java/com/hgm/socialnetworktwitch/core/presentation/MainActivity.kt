package com.hgm.socialnetworktwitch.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hgm.socialnetworktwitch.core.presentation.components.StandardScaffold
import com.hgm.socialnetworktwitch.core.presentation.ui.theme.SocialNetworkTwitchTheme
import com.hgm.socialnetworktwitch.core.presentation.route.Navigation
import com.hgm.socialnetworktwitch.core.presentation.route.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                  SocialNetworkTwitchTheme {
                        Surface(
                              modifier = Modifier.fillMaxSize(),
                              color = MaterialTheme.colorScheme.background
                        ) {
                              val navController = rememberNavController()
                              val snackBarState = remember { SnackbarHostState() }
                              val navBackStackEntry by navController.currentBackStackEntryAsState()

                              StandardScaffold(
                                    navController = navController,
                                    modifier = Modifier.fillMaxSize(),
                                    snackBarState = snackBarState,
                                    showBottomBar = navBackStackEntry?.destination?.route in listOf(
                                          // 当前路由属于以下类型的就显示底部栏
                                          Screen.MainFeedScreen.route,
                                          Screen.ChatScreen.route,
                                          Screen.ActivityScreen.route,
                                          Screen.ProfileScreen.route,
                                    ),
                                    showFAB = navBackStackEntry?.destination?.route in listOf(
                                          Screen.MainFeedScreen.route
                                    ),
                                    onFabClick = {
                                          navController.navigate(Screen.CreatePostScreen.route)
                                    }
                              ) {
                                    Navigation(
                                          paddingValues = it,
                                          navController = navController,
                                          snackBarState = snackBarState
                                    )
                              }
                        }
                  }
            }
      }
}
