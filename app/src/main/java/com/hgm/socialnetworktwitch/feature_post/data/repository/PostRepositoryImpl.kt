package com.hgm.socialnetworktwitch.feature_post.data.repository

import android.net.Uri
import androidx.core.net.toFile
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.hgm.socialnetworktwitch.R
import com.hgm.socialnetworktwitch.core.presentation.util.UiText
import com.hgm.socialnetworktwitch.core.util.Resource
import com.hgm.socialnetworktwitch.core.util.SimpleResource
import com.hgm.socialnetworktwitch.feature_post.data.dto.CreatePostRequest
import com.hgm.socialnetworktwitch.feature_post.data.remote.PostApi
import com.hgm.socialnetworktwitch.feature_post.domain.model.Comment
import com.hgm.socialnetworktwitch.core.util.Constants
import com.hgm.socialnetworktwitch.feature_post.data.paging.PostPagingSource
import com.hgm.socialnetworktwitch.core.domain.model.Post
import com.hgm.socialnetworktwitch.core.domain.model.UserItem
import com.hgm.socialnetworktwitch.feature_post.data.dto.AddCommentRequest
import com.hgm.socialnetworktwitch.feature_post.data.dto.LikeUpdateRequest
import com.hgm.socialnetworktwitch.feature_post.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.IOException


class PostRepositoryImpl(
      private val api: PostApi,
      private val gson: Gson,
) : PostRepository {

      override suspend fun getPostsForFollows(page: Int, pageSize: Int): Resource<List<Post>> {
            return try {
                  val posts = api.getPostsForFollows(page, pageSize)
                  Resource.Success(posts)
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

      override suspend fun createPost(description: String, imageUri: Uri): SimpleResource {
            val request = CreatePostRequest(description)
            val file = imageUri.toFile()

            return try {
                  val response = api.createPost(
                        postData = MultipartBody.Part.createFormData(
                              "post_data",
                              gson.toJson(request)
                        ),
                        postImage = MultipartBody.Part.createFormData(
                              "post_image",
                              filename = file.name,
                              body = file.asRequestBody()
                        ),
                  )
                  if (response.successful) {
                        Resource.Success(Unit)
                  } else {
                        response.message?.let { msg ->
                              Resource.Error(UiText.DynamicString(msg))
                        } ?: Resource.Error(UiText.StringResource(R.string.error_unknown))
                  }
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

      override suspend fun getPostDetail(userId: String, postId: String): Resource<Post> {
            return try {
                  val response = api.getPostDetail(userId = userId, postId = postId)
                  if (response.successful) {
                        Resource.Success(response.data)
                  } else {
                        response.message?.let { msg ->
                              Resource.Error(UiText.DynamicString(msg))
                        } ?: Resource.Error(UiText.StringResource(R.string.error_unknown))
                  }
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

      override suspend fun getCommentForPost(postId: String): Resource<List<Comment>> {
            return try {
                  val comments = api.getCommentForPost(postId = postId).map { it.toComment() }
                  Resource.Success(comments)
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

      override suspend fun addComment(postId: String, comment: String): SimpleResource {
            return try {
                  val response = api.addComment(
                        AddCommentRequest(postId, comment)
                  )
                  if (response.successful) {
                        Resource.Success(response.data)
                  } else {
                        response.message?.let { msg ->
                              Resource.Error(UiText.DynamicString(msg))
                        } ?: Resource.Error(UiText.StringResource(R.string.error_unknown))
                  }
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


      override suspend fun likeParent(parentId: String, parentType: Int): SimpleResource {
            return try {
                  val response = api.likeParent(
                        LikeUpdateRequest(parentId = parentId, parentType = parentType)
                  )
                  if (response.successful) {
                        Resource.Success(Unit)
                  } else {
                        response.message?.let { msg ->
                              Resource.Error(UiText.DynamicString(msg))
                        } ?: Resource.Error(UiText.StringResource(R.string.error_unknown))
                  }
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

      override suspend fun unlikeParent(parentId: String, parentType: Int): SimpleResource {
            return try {
                  val response = api.unlikeParent(
                        parentId = parentId,
                        parentType = parentType
                  )
                  if (response.successful) {
                        Resource.Success(Unit)
                  } else {
                        response.message?.let { msg ->
                              Resource.Error(UiText.DynamicString(msg))
                        } ?: Resource.Error(UiText.StringResource(R.string.error_unknown))
                  }
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

      override suspend fun getLikesForParent(parentId: String): Resource<List<UserItem>> {
            return try {
                  val response = api.getLikesForParent(parentId)
                  Resource.Success(response.map { it.toUserItem() })
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

      override suspend fun deletePost(postId: String): SimpleResource {
            return try {
                  val response = api.deletePost(postId)
                  if (response.successful) {
                        Resource.Success(Unit)
                  } else {
                        response.message?.let { msg ->
                              Resource.Error(UiText.DynamicString(msg))
                        } ?: Resource.Error(UiText.StringResource(R.string.error_unknown))
                  }
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
}