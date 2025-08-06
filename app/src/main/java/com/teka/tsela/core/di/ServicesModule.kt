package com.teka.tsela.core.di

import android.content.Context
import com.teka.tsela.data_layer.DataStoreRepository
import com.teka.tsela.data_layer.api.GenericApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServicesModule {

    @Provides
    @Singleton
    fun provideGenericApiService(
        @ApplicationContext context: Context,
        dataStoreRepository: DataStoreRepository
    ): GenericApiService {
        return GenericApiService.getInstance(context, dataStoreRepository)
    }

}