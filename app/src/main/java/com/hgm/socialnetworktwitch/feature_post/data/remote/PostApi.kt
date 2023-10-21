package com.hgm.socialnetworktwitch.feature_post.data.remote

import com.hgm.socialnetworktwitch.core.data.dto.BaseResponse
import com.hgm.socialnetworktwitch.feature_post.data.dto.CreatePostRequest
import com.hgm.socialnetworktwitch.feature_post.domain.model.Post
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * @auth：HGM
 * @date：2023-10-19 15:42
 * @desc：
 */
interface PostApi {

      companion object {
            const val BASE_URL = "http://10.0.2.2:8080"
      }

      @GET("/api/post/get")
      suspend fun getPostsForFollows(
            @Query("page") page: Int,
            @Query("pageSize") pageSize: Int
      ): List<Post>

      //多部分上传
      @Multipart
      @POST("/api/post/create")
      suspend fun createPost(
            @Part postData:MultipartBody.Part,
            @Part postImage:MultipartBody.Part
      ): BaseResponse<Unit>
}