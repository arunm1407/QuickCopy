package com.example.ocr.di

import com.example.ocr.eventBus.EventBus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideEventBus(): EventBus {
        return EventBus.getInstance()
    }
}