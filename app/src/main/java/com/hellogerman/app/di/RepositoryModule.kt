package com.hellogerman.app.di

import android.content.Context
import com.hellogerman.app.data.repository.DictionaryRepository
import com.hellogerman.app.data.service.VocabularyPackService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repository dependencies
 * Ensures proper injection of offline-first dictionary system
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideDictionaryRepository(
        @ApplicationContext context: Context
    ): DictionaryRepository {
        return DictionaryRepository(context)
    }
    
    @Provides
    @Singleton
    fun provideVocabularyPackService(
        @ApplicationContext context: Context
    ): VocabularyPackService {
        return VocabularyPackService(context)
    }
}
