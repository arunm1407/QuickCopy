package com.example.ocr.di

import android.content.Context
import android.content.SharedPreferences
import com.example.ocr.base.SharedPreferenceName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferenceModule {

    @Provides
    @Singleton
    fun provideDefaultSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPreferenceName.DEFAULT_SHARED_PREFERENCE, Context.MODE_PRIVATE)
    }

}