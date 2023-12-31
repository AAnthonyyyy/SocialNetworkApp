package com.hgm.socialnetworktwitch.feature_post.presentation.post_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hgm.socialnetworktwitch.R
import com.hgm.socialnetworktwitch.core.domain.states.StandardTextFieldState
import com.hgm.socialnetworktwitch.core.domain.use_case.GetOwnUserIdUseCase
import com.hgm.socialnetworktwitch.core.presentation.util.UiEvent
import com.hgm.socialnetworktwitch.core.presentation.util.UiText
import com.hgm.socialnetworktwitch.core.util.Resource
import com.hgm.socialnetworktwitch.feature_auth.domain.use_case.AuthenticateUseCase
import com.hgm.socialnetworktwitch.feature_post.domain.use_case.PostUseCases
import com.hgm.socialnetworktwitch.feature_post.util.CommentError
import com.hgm.socialnetworktwitch.feature_post.util.ParentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostDetailViewModel @Inject constructor(
      private val postUseCases: PostUseCases,
      private val authUseCase: AuthenticateUseCase,
      private val savedStateHandle: SavedStateHandle,
      private val getOwnUserIdUseCase: GetOwnUserIdUseCase
) : ViewModel() {

      private var isUserLoggedIn = false

      private val _state = mutableStateOf(PostDetailState())
      val state: State<PostDetailState> = _state

      //评论输入框状态
      private val _commentTextState = mutableStateOf(StandardTextFieldState())
      val commentTextState: State<StandardTextFieldState> = _commentTextState

      //发表评论状态
      private val _commentState = mutableStateOf(CommentState())
      val commentState: State<CommentState> = _commentState

      private val _eventFlow = MutableSharedFlow<UiEvent>()
      val eventFlow = _eventFlow.asSharedFlow()

      init {
            savedStateHandle.get<String>("postId")?.let { postId ->
                  loadPostDetail(getOwnUserIdUseCase(), postId)
                  loadCommentsForPost(postId)
            }
      }


      fun onEvent(event: PostDetailEvent) {
            when (event) {
                  is PostDetailEvent.LikePost -> {
                        val isLiked = state.value.post?.isLiked == true
                        updateLikeForParent(
                              parentId = state.value.post?.id ?: return,
                              parentType = ParentType.Post.type,
                              isLiked = isLiked
                        )
                  }

                  is PostDetailEvent.LikeComment -> {
                        val isLiked = state.value.comments.find {
                              it.id == event.commentId
                        }?.isLiked == true
                        updateLikeForParent(
                              parentId = event.commentId,
                              parentType = ParentType.Comment.type,
                              isLiked = isLiked
                        )
                  }

                  is PostDetailEvent.Comment -> {
                        addComment(
                              postId = savedStateHandle.get<String>("postId") ?: "",
                              comment = commentTextState.value.text
                        )
                  }

                  is PostDetailEvent.EnteredComment -> {
                        _commentTextState.value = commentTextState.value.copy(
                              text = event.comment,
                              error = if (event.comment.isBlank()) CommentError.FieldEmpty else null
                        )
                  }
            }
      }


      private fun loadPostDetail(userId: String, postId: String) {
            viewModelScope.launch {
                  _state.value = state.value.copy(
                        isLoadingPost = true
                  )
                  when (val result = postUseCases.getPostDetail(userId, postId)) {
                        is Resource.Success -> {
                              _state.value = state.value.copy(
                                    post = result.data,
                                    isLoadingPost = false
                              )
                        }

                        is Resource.Error -> {
                              _state.value = state.value.copy(
                                    isLoadingPost = false
                              )
                              _eventFlow.emit(
                                    UiEvent.ShowSnackBar(
                                          result.uiText
                                                ?: UiText.StringResource(R.string.error_unknown)
                                    )
                              )
                        }
                  }
            }
      }

      private fun loadCommentsForPost(postId: String) {
            viewModelScope.launch {
                  _state.value = state.value.copy(
                        isLoadingComment = true
                  )
                  when (val result = postUseCases.getCommentForPost(postId)) {
                        is Resource.Success -> {
                              _state.value = state.value.copy(
                                    comments = result.data ?: emptyList(),
                                    isLoadingComment = false
                              )
                        }

                        is Resource.Error -> {
                              _state.value = state.value.copy(
                                    isLoadingComment = false
                              )
                              _eventFlow.emit(
                                    UiEvent.ShowSnackBar(
                                          result.uiText
                                                ?: UiText.StringResource(R.string.error_unknown)
                                    )
                              )
                        }
                  }
            }
      }

      private fun addComment(postId: String, comment: String) {
            viewModelScope.launch {
                  isUserLoggedIn = authUseCase() is Resource.Success
                  if (!isUserLoggedIn) {
                        _eventFlow.emit(UiEvent.ShowSnackBar(UiText.StringResource(R.string.user_not_login)))
                        return@launch
                  }

                  _commentState.value = commentState.value.copy(
                        isLoading = true
                  )

                  val result = postUseCases.addComment(
                        postId = postId,
                        comment = comment
                  )

                  when (result) {
                        is Resource.Error -> {
                              _eventFlow.emit(
                                    UiEvent.ShowSnackBar(
                                          result.uiText ?: UiText.unknownError()
                                    )
                              )
                              _commentState.value = commentState.value.copy(
                                    isLoading = false
                              )
                        }

                        is Resource.Success -> {
                              _eventFlow.emit(
                                    UiEvent.ShowSnackBar(
                                          UiText.StringResource(R.string.send_comment_successful)
                                    )
                              )
                              _commentState.value = commentState.value.copy(
                                    isLoading = false
                              )
                              _commentTextState.value = StandardTextFieldState()
                              loadCommentsForPost(postId)
                        }
                  }
            }
      }

      private fun updateLikeForParent(
            parentId: String,
            parentType: Int,
            isLiked: Boolean
      ) {
            viewModelScope.launch {
                  isUserLoggedIn = authUseCase() is Resource.Success
                  if (!isUserLoggedIn) {
                        _eventFlow.emit(UiEvent.ShowSnackBar(UiText.StringResource(R.string.user_not_login)))
                        return@launch
                  }


                  val currentLikeCount = state.value.post?.likeCount ?: 0

                  when (parentType) {
                        ParentType.Post.type -> {
                              val post = state.value.post
                              _state.value = state.value.copy(
                                    post = state.value.post?.copy(
                                          isLiked = !isLiked,
                                          likeCount = if (isLiked) {
                                                post?.likeCount?.minus(1) ?: 0
                                          } else post?.likeCount?.plus(1) ?: 0
                                    )
                              )
                        }

                        ParentType.Comment.type -> {
                              _state.value = state.value.copy(
                                    comments = state.value.comments.map { comment ->
                                          if (comment.id == parentId) {
                                                comment.copy(
                                                      isLiked = !isLiked,
                                                      likeCount = if (isLiked) {
                                                            comment.likeCount - 1
                                                      } else comment.likeCount + 1
                                                )
                                          } else comment
                                    }
                              )
                        }
                  }
                  val result = postUseCases.updateLikeParent(
                        parentId = parentId,
                        parentType = parentType,
                        isLiked = isLiked
                  )
                  when (result) {
                        is Resource.Success -> Unit
                        is Resource.Error -> {
                              when (parentType) {
                                    ParentType.Post.type -> {
                                          val post = state.value.post
                                          _state.value = state.value.copy(
                                                post = state.value.post?.copy(
                                                      isLiked = isLiked,
                                                      likeCount = currentLikeCount
                                                )
                                          )
                                    }

                                    ParentType.Comment.type -> {
                                          _state.value = state.value.copy(
                                                comments = state.value.comments.map { comment ->
                                                      if (comment.id == parentId) {
                                                            comment.copy(
                                                                  isLiked = isLiked,
                                                                  likeCount = if (comment.isLiked) {
                                                                        comment.likeCount - 1
                                                                  } else comment.likeCount + 1
                                                            )
                                                      } else comment
                                                }
                                          )
                                    }
                              }
                        }
                  }
            }
      }
}