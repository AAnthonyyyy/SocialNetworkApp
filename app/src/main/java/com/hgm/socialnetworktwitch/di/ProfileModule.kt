package com.hgm.socialnetworktwitch.di

import android.content.SharedPreferences
import com.google.gson.Gson
import com.hgm.socialnetworktwitch.feature_post.data.remote.PostApi
import com.hgm.socialnetworktwitch.core.util.Constants.BASE_URL
import com.hgm.socialnetworktwitch.feature_profile.data.remote.ProfileApi
import com.hgm.socialnetworktwitch.core.data.repository.ProfileRepositoryImpl
import com.hgm.socialnetworktwitch.core.domain.repository.ProfileRepository
import com.hgm.socialnetworktwitch.feature_profile.domain.use_case.GetPostsForProfileUseCase
import com.hgm.socialnetworktwitch.feature_profile.domain.use_case.GetProfileUseCase
import com.hgm.socialnetworktwitch.feature_profile.domain.use_case.GetSkillsUseCase
import com.hgm.socialnetworktwitch.feature_profile.domain.use_case.ProfileUseCases
import com.hgm.socialnetworktwitch.feature_profile.domain.use_case.SearchUserUseCase
import com.hgm.socialnetworktwitch.feature_profile.domain.use_case.SetSkillSelectedUseCase
import com.hgm.socialnetworktwitch.core.domain.use_case.UpdateFollowUseCase
import com.hgm.socialnetworktwitch.feature_profile.domain.use_case.LogoutUseCase
import com.hgm.socialnetworktwitch.feature_profile.domain.use_case.UpdateProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

      @Provides
      @Singleton
      fun provideProfileApi(client: OkHttpClient): ProfileApi {
            return Retrofit.Builder().baseUrl(BASE_URL).client(client)
                  .addConverterFactory(GsonConverterFactory.create()).build()
                  .create(ProfileApi::class.java)
      }

      @Provides
      @Singleton
      fun provideProfileRepository(
            gson: Gson,
            postApi: PostApi,
            profileApi: ProfileApi,
            sharedPreferences: SharedPreferences
      ): ProfileRepository {
            return ProfileRepositoryImpl(gson, postApi, profileApi, sharedPreferences)
      }

      @Provides
      @Singleton
      fun provideProfileUseCases(repository: ProfileRepository): ProfileUseCases {
            return ProfileUseCases(
                  getProfileUseCase = GetProfileUseCase(repository),
                  getSkillsUseCase = GetSkillsUseCase(repository),
                  updateProfileUseCase = UpdateProfileUseCase(repository),
                  setSkillSelectedUseCase = SetSkillSelectedUseCase(),
                  getPostsForProfileUseCase = GetPostsForProfileUseCase(repository),
                  searchUserUseCase = SearchUserUseCase(repository),
                  updateFollowUseCase = UpdateFollowUseCase(repository),
                  logoutUseCase = LogoutUseCase(repository)
            )
      }
}