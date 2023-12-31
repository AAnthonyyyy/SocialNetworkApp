package com.hgm.socialnetworktwitch.feature_chat.domain.repository

import com.hgm.socialnetworktwitch.core.util.Resource
import com.hgm.socialnetworktwitch.feature_chat.domain.model.Chat
import com.hgm.socialnetworktwitch.feature_chat.domain.model.Message
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow

/**
 * @auth：HGM
 * @date：2023-11-04 19:50
 * @desc：
 */
interface ChatRepository {

      fun initialize()

      suspend fun getChatsForUser(): Resource<List<Chat>>
      suspend fun getMessagesForChat(
            chatId: String,
            page: Int,
            pageSize: Int
      ): Resource<List<Message>>

      fun sendMessage(receiveId: String, text: String, chatId: String?)

      fun receiveMessage(): Flow<Message>

      fun observeChatEvents(): Flow<WebSocket.Event>
}