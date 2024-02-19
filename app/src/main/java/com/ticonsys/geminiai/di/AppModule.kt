package com.ticonsys.geminiai.di

import com.google.ai.client.generativeai.GenerativeModel
import com.ticonsys.geminiai.BuildConfig
import com.ticonsys.geminiai.di.annotation.Pro
import com.ticonsys.geminiai.di.annotation.Vision
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Pro
    @Provides
    @Singleton
    fun provideGeminiPro(
    ): GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.API_KEY
    )

    @Vision
    @Provides
    @Singleton
    fun provideGeminiProVision(
    ): GenerativeModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = BuildConfig.API_KEY
    )

}