package com.hellogerman.app.data.api

import com.hellogerman.app.data.models.MyMemoryTranslationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * MyMemory Translation API service interface for dictionary functionality
 * Free translation API that doesn't require authentication
 */
interface TranslationApiService {
    
    @GET("get")
    suspend fun getTranslation(
        @Query("q") query: String,
        @Query("langpair") langPair: String
    ): Response<MyMemoryTranslationResponse>
    
    companion object {
        const val BASE_URL = "https://api.mymemory.translated.net/"
        
        // Create language pair string (e.g., "de|en" for German to English)
        fun createLanguagePair(fromLang: String, toLang: String): String {
            return "$fromLang|$toLang"
        }
    }
}
