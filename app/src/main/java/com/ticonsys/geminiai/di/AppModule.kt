package com.ticonsys.geminiai.di

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.ticonsys.geminiai.BuildConfig
import com.ticonsys.geminiai.di.annotation.Pro
import com.ticonsys.geminiai.di.annotation.Vision
import com.ticonsys.geminiai.domain.utils.BitmapExtractor
import com.ticonsys.geminiai.domain.utils.SettingsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideSettingProvider(
        @ApplicationContext context: Context
    ): SettingsProvider = SettingsProvider(context)


    @Provides
    @Singleton
    fun provideBitmapExtractor(
        @ApplicationContext context: Context,
        settingsProvider: SettingsProvider,
    ): BitmapExtractor = BitmapExtractor(context, settingsProvider)

    @Pro
    @Provides
    @Singleton
    fun provideGeminiPro(
    ): GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro",
        apiKey = BuildConfig.API_KEY
    )

//    @Vision
//    @Provides
//    @Singleton
//    fun provideGeminiProVision(
//    ): GenerativeModel = GenerativeModel(
//        modelName = "gemini-pro-vision",
//        apiKey = BuildConfig.API_KEY
//    )

}