package com.hgm.socialnetworktwitch.feature_post.presentation.main_feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.hgm.socialnetworktwitch.R
import com.hgm.socialnetworktwitch.feature_post.domain.model.Post
import com.hgm.socialnetworktwitch.core.presentation.components.StandardTopBar
import com.hgm.socialnetworktwitch.core.presentation.ui.theme.SpaceMedium
import com.hgm.socialnetworktwitch.core.presentation.route.Screen
import com.hgm.socialnetworktwitch.feature_post.presentation.main_feed.component.PostView

/**
 * @auth：HGM
 * @date：2023-10-10 11:22
 * @desc：
 */
@Composable
fun MainFeedScreen(
      navController: NavController
) {


      Column(
            modifier = Modifier.fillMaxSize(),
      ) {
            StandardTopBar(
                  navController = navController,
                  title = {
                        Text(
                              text = stringResource(id = R.string.your_feed),
                              fontWeight = FontWeight.Bold,
                              color = MaterialTheme.colorScheme.onBackground
                        )
                  },
                  modifier = Modifier.fillMaxWidth(),
                  navAction = {
                        IconButton(onClick = {
                              navController.navigate(Screen.SearchScreen.route)
                        }) {
                              Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(id = R.string.search_post),
                                    tint = MaterialTheme.colorScheme.onBackground
                              )
                        }
                  }
            )

            LazyColumn(
                  modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                  verticalArrangement = Arrangement.spacedBy(SpaceMedium)
            ) {
                  items(3) {
                        PostView(
                              post = Post(
                                    username = "Germen Wong",
                                    imageUrl = "",
                                    profilePictureUrl = "",
                                    description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed" +
                                            "diam nonumy eirmod tempor invidunt ut labore et dolore " +
                                            "magna aliquyam erat...",
                                    likeCount = 14,
                                    commentCount = 53
                              )
                        ){
                              navController.navigate(Screen.PostDetailScreen.route)
                        }
                  }
            }
      }


}