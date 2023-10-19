package com.hgm.socialnetworktwitch.feature_profile.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hgm.socialnetworktwitch.R
import com.hgm.socialnetworktwitch.feature_post.domain.Post
import com.hgm.socialnetworktwitch.feature_auth.domain.model.User
import com.hgm.socialnetworktwitch.feature_post.presentation.main_feed.PostView
import com.hgm.socialnetworktwitch.feature_profile.presentation.profile.components.BannerSection
import com.hgm.socialnetworktwitch.feature_profile.presentation.profile.components.ProfileHeaderSection
import com.hgm.socialnetworktwitch.core.presentation.ui.theme.ProfilePictureSizeLarge
import com.hgm.socialnetworktwitch.core.presentation.ui.theme.SpaceMedium
import com.hgm.socialnetworktwitch.core.presentation.ui.theme.SpaceSmall
import com.hgm.socialnetworktwitch.core.presentation.route.Screen
import com.hgm.socialnetworktwitch.core.util.toPx

/**
 * @auth：HGM
 * @date：2023-10-10 16:14
 * @desc：
 */
@Composable
fun ProfileScreen(
      navController: NavController,
      profilePictureSize: Dp = ProfilePictureSizeLarge,
      viewModel: ProfileViewModel = hiltViewModel()
) {
      val lazyListState = rememberLazyListState()
      val toolbarState = viewModel.toolbarState.value
      val iconHorizontalCenterLength =
            (LocalConfiguration.current.screenWidthDp.dp.toPx() / 4f -
                    (profilePictureSize / 4f).toPx() -
                    SpaceSmall.toPx()) / 2f
      val iconSizeExpanded = 35.dp
      val toolbarHeightCollapsed = 75.dp
      val imageCollapsedOffsetY = remember {
            (toolbarHeightCollapsed - profilePictureSize / 2f) / 2f
      }
      val iconCollapsedOffsetY = remember {
            (toolbarHeightCollapsed - iconSizeExpanded) / 2f
      }
      val bannerHeight = (LocalConfiguration.current.screenWidthDp / 2.5f).dp
      val toolbarHeightExpanded = remember {
            bannerHeight + profilePictureSize
      }
      val maxOffset = remember {
            toolbarHeightExpanded - toolbarHeightCollapsed
      }
      val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                  override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        val delta = available.y
                        if(delta > 0f && lazyListState.firstVisibleItemIndex != 0) {
                              return Offset.Zero
                        }
                        val newOffset = viewModel.toolbarState.value.toolbarOffsetY + delta
                        viewModel.setToolbarOffsetY(newOffset.coerceIn(
                              minimumValue = -maxOffset.toPx(),
                              maximumValue = 0f
                        ))
                        viewModel.setExpandedRatio((viewModel.toolbarState.value.toolbarOffsetY + maxOffset.toPx()) / maxOffset.toPx())
                        return Offset.Zero
                  }
            }
      }


      Box(
            modifier = Modifier
                  .fillMaxSize()
                  .nestedScroll(nestedScrollConnection)
      ) {
            LazyColumn(
                  modifier = Modifier
                        .fillMaxSize(),
                  state = lazyListState
            ) {
                  item {
                        Spacer(modifier = Modifier.height(
                              toolbarHeightExpanded - profilePictureSize / 2f
                        ))
                  }
                  item {
                        ProfileHeaderSection(
                              user = User(
                                    profilePictureUrl = "",
                                    username = "Philipp Lackner",
                                    description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed\n" +
                                            "diam nonumy eirmod tempor invidunt ut labore et dolore \n" +
                                            "magna aliquyam erat, sed diam voluptua",
                                    followerCount = 234,
                                    followingCount = 534,
                                    postCount = 65
                              )
                        ){
                              navController.navigate(Screen.EditProfileScreen.route)
                        }
                  }
                  items(20) {
                        Spacer(
                              modifier = Modifier
                                    .height(SpaceMedium)
                        )
                        PostView(
                              post = Post(
                                    username = "Philipp Lackner",
                                    imageUrl = "",
                                    profilePictureUrl = "",
                                    description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed\n" +
                                            "diam nonumy eirmod tempor invidunt ut labore et dolore \n" +
                                            "magna aliquyam erat, sed diam voluptua...",
                                    likeCount = 17,
                                    commentCount = 7,
                              ),
                              showProfileImage = false,
                              onPostClick = {
                                    navController.navigate(Screen.PostDetailScreen.route)
                              },
                        )
                  }
            }
            Column(
                  modifier = Modifier
                        .align(Alignment.TopCenter)
            ) {
                  BannerSection(
                        modifier = Modifier
                              .height(
                                    (bannerHeight * toolbarState.expandedRatio).coerceIn(
                                          minimumValue = toolbarHeightCollapsed,
                                          maximumValue = bannerHeight
                                    )
                              ),
                        leftIconModifier = Modifier
                              .graphicsLayer {
                                    translationY = (1f - toolbarState.expandedRatio) *
                                            -iconCollapsedOffsetY.toPx()
                                    translationX = (1f - toolbarState.expandedRatio) *
                                            iconHorizontalCenterLength
                              },
                        rightIconModifier = Modifier
                              .graphicsLayer {
                                    translationY = (1f - toolbarState.expandedRatio) *
                                            -iconCollapsedOffsetY.toPx()
                                    translationX = (1f - toolbarState.expandedRatio) *
                                            -iconHorizontalCenterLength
                              }
                  )
                  Image(
                        painter = painterResource(id = R.drawable.germen),
                        contentDescription = stringResource(id = R.string.profile_image),
                        modifier = Modifier
                              .align(CenterHorizontally)
                              .graphicsLayer {
                                    translationY = -profilePictureSize.toPx() / 2f -
                                            (1f - toolbarState.expandedRatio) * imageCollapsedOffsetY.toPx()
                                    transformOrigin = TransformOrigin(
                                          pivotFractionX = 0.5f,
                                          pivotFractionY = 0f
                                    )
                                    val scale = 0.5f + toolbarState.expandedRatio * 0.5f
                                    scaleX = scale
                                    scaleY = scale
                              }
                              .size(profilePictureSize)
                              .clip(CircleShape)
                              .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape
                              )
                  )
            }
      }
}