package com.hgm.socialnetworktwitch.di

import com.hgm.socialnetworktwitch.core.util.Constants.BASE_URL
import com.hgm.socialnetworktwitch.feature_activity.data.remote.ActivityApi
import com.hgm.socialnetworktwitch.feature_activity.data.repository.ActivityRepositoryImpl
import com.hgm.socialnetworktwitch.feature_activity.domain.repository.ActivityRepository
import com.hgm.socialnetworktwitch.feature_activity.domain.use_case.GetActivitiesUseCase
import com.hgm.socialnetworktwitch.feature_auth.data.remote.AuthApi
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
object ActivityModule {

      @Provides
      @Singleton
      fun provideActivityApi(client: OkHttpClient): ActivityApi {
            return Retrofit.Builder()
                  .baseUrl(BASE_URL)
                  .client(client)
                  .addConverterFactory(GsonConverterFactory.create())
                  .build()
                  .create(ActivityApi::class.java)
      }

      @Provides
      @Singleton
      fun provideActivityRepository(
            api: ActivityApi,
      ): ActivityRepository {
            return ActivityRepositoryImpl(api)
      }

      @Provides
      @Singleton
      fun provideGetActivitiesUseCase(repository: ActivityRepository): GetActivitiesUseCase {
            return GetActivitiesUseCase(repository)
      }
}