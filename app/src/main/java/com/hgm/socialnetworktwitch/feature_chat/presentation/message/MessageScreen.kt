package com.hgm.socialnetworktwitch.feature_chat.presentation.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.hgm.socialnetworktwitch.R
import com.hgm.socialnetworktwitch.core.presentation.components.SendTextField
import com.hgm.socialnetworktwitch.core.presentation.components.StandardTopBar
import com.hgm.socialnetworktwitch.core.presentation.ui.theme.SpaceLarge
import com.hgm.socialnetworktwitch.core.presentation.ui.theme.SpaceMedium
import com.hgm.socialnetworktwitch.core.util.autoHideKeyboard
import com.hgm.socialnetworktwitch.feature_chat.domain.model.Message
import com.hgm.socialnetworktwitch.feature_chat.presentation.message.components.OwnMessage
import com.hgm.socialnetworktwitch.feature_chat.presentation.message.components.RemoteMessage
import kotlinx.coroutines.flow.collectLatest
import okio.ByteString.Companion.decodeBase64
import java.nio.charset.Charset


@Composable
fun MessageScreen(
      remoteUserId: String,
      remoteUsername: String,
      remoteUserProfilePictureUrl: String,
      onNavigateUp: () -> Unit = {},
      onNavigate: (String) -> Unit = {},
      viewModel: MessageViewModel = hiltViewModel()
) {
      val context = LocalContext.current
      val listState = rememberLazyListState()
      val pagingState = viewModel.pagingState.value
      val decodedRemoteUserProfilePictureUrl = remember {
            remoteUserProfilePictureUrl.decodeBase64()?.string(Charset.defaultCharset())
      }


      LaunchedEffect(key1 = pagingState) {
            viewModel.messageReceiver.collect { event ->
                  when (event) {
                        is MessageViewModel.MessageReceiverEvent.MessagePageLoaded,
                        is MessageViewModel.MessageReceiverEvent.SingleMessageReceiver -> {
                              if (pagingState.items.isEmpty()) {
                                    return@collect
                              }
                              listState.scrollToItem(pagingState.items.lastIndex)
                        }
                  }
            }
      }


      Column(
            modifier = Modifier
                  .fillMaxSize()
                  .autoHideKeyboard()
      ) {
            StandardTopBar(
                  showBackIcon = true,
                  onNavigateUp = onNavigateUp,
                  title = {
                        Text(text = remoteUsername)
                  }
            )

            LazyColumn(
                  state = listState,
                  modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(SpaceMedium),
                  verticalArrangement = Arrangement.spacedBy(SpaceLarge)
            ) {
                  items(pagingState.items.size) { index ->
                        val message = pagingState.items[index]
                        //满足刷新下一页的条件：列表前一位、数据没有到底、不在刷新状态
                        if (index >= pagingState.items.size - 1 && !pagingState.endReached && !pagingState.isLoading) {
                              viewModel.loadNextMessages()
                        }

                        if (message.sendId == remoteUserId) {
                              RemoteMessage(
                                    context = context,
                                    message = message.text,
                                    formattedTime = message.formattedTime,
                                    remoteUserProfilePictureUrl = decodedRemoteUserProfilePictureUrl
                              )
                              Spacer(modifier = Modifier.height(SpaceMedium))
                        } else {
                              OwnMessage(
                                    context = context,
                                    message = message.text,
                                    formattedTime = message.formattedTime,
                                    color = MaterialTheme.colorScheme.primary
                              )
                              Spacer(modifier = Modifier.height(SpaceMedium))
                        }
                  }
            }

            SendTextField(
                  hint = stringResource(id = R.string.comment_hint),
                  state = viewModel.messageTextFieldState.value,
                  canSendMessage = viewModel.buttonState.value,
                  onValueChange = {
                        viewModel.onEvent(MessageEvent.EnterMessage(it))
                  },
                  onSend = {
                        viewModel.onEvent(MessageEvent.SendMessage)
                  }
            )
      }
}