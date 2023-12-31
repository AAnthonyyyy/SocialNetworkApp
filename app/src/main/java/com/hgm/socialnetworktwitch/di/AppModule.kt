package com.hgm.socialnetworktwitch.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.gson.Gson
import com.hgm.socialnetworktwitch.core.domain.repository.ProfileRepository
import com.hgm.socialnetworktwitch.core.domain.use_case.GetOwnUserIdUseCase
import com.hgm.socialnetworktwitch.core.domain.use_case.UpdateFollowUseCase
import com.hgm.socialnetworktwitch.core.util.Constants
import com.hgm.socialnetworktwitch.core.util.DefaultPostLiker
import com.hgm.socialnetworktwitch.core.util.PostLiker
import com.hgm.socialnetworktwitch.feature_post.domain.repository.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

      @Provides
      @Singleton
      fun provideOKHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
            return OkHttpClient.Builder()
                  .addInterceptor {
                        val token = sharedPreferences.getString(Constants.KEY_JWT_TOKEN, "")
                        val newRequest = it.request().newBuilder()
                              .addHeader("Authorization", "Bearer $token")
                              .build()
                        it.proceed(newRequest)
                  }
                  .addInterceptor(
                        HttpLoggingInterceptor().apply {
                              level = HttpLoggingInterceptor.Level.BODY
                        }
                  )
                  .build()
      }

      @Provides
      @Singleton
      fun provideSharedPreference(app: Application): SharedPreferences {
            return app.getSharedPreferences(
                  Constants.SHARED_PREF_NAME,
                  MODE_PRIVATE
            )
      }

      @Provides
      @Singleton
      fun provideGson(): Gson {
            return Gson()
      }

      @Provides
      @Singleton
      fun provideGetOwnUserIdUseCase(sharedPreferences: SharedPreferences): GetOwnUserIdUseCase {
            return GetOwnUserIdUseCase(sharedPreferences)
      }

      @Provides
      @Singleton
      fun provideUpdateFollowUseCase(repository: ProfileRepository): UpdateFollowUseCase {
            return UpdateFollowUseCase(repository)
      }

      @Provides
      @Singleton
      fun providePostLiker(): PostLiker {
            return DefaultPostLiker()
      }

}