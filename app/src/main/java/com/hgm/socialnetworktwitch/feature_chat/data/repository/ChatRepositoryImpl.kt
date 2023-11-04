package com.hgm.socialnetworktwitch.feature_chat.data.repository

import com.hgm.socialnetworktwitch.R
import com.hgm.socialnetworktwitch.core.presentation.util.UiText
import com.hgm.socialnetworktwitch.core.util.Resource
import com.hgm.socialnetworktwitch.feature_chat.data.remote.ChatApi
import com.hgm.socialnetworktwitch.feature_chat.data.remote.ChatService
import com.hgm.socialnetworktwitch.feature_chat.data.remote.dto.WsClientMessage
import com.hgm.socialnetworktwitch.feature_chat.domain.model.Chat
import com.hgm.socialnetworktwitch.feature_chat.domain.model.Message
import com.hgm.socialnetworktwitch.feature_chat.domain.repository.ChatRepository
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

/**
 * @auth：HGM
 * @date：2023-11-04 19:53
 * @desc：
 */
class ChatRepositoryImpl(
      private val chatService: ChatService,
      private val chatApi: ChatApi
) : ChatRepository {
      override suspend fun getChatsForUser(): Resource<List<Chat>> {
            return try {
                  val chats = chatApi.getChatsForUser().mapNotNull { it.toChat() }
                  println("Repository：$chats")
                  Resource.Success(data = chats)
            } catch (e: IOException) {
                  Resource.Error(
                        uiText = UiText.StringResource(R.string.error_couldnt_reach_srver)
                  )
            } catch (e: HttpException) {
                  Resource.Error(
                        uiText = UiText.StringResource(R.string.error_something_wrong)
                  )
            }
      }

      override fun sendMessage(receiveId: String, text: String, chatId: String?) {
            chatService.sendMessage(
                  WsClientMessage(
                        receiveId = receiveId,
                        chatId = chatId,
                        text = text
                  )
            )
      }

      override fun receiveMessage(): Flow<Message> {
            return chatService.observeMessages().map { it.toMessage() }
      }

      override fun observeChatEvents(): Flow<WebSocket.Event> {
            return chatService.observeEvents()
      }
}