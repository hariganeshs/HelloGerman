package com.hellogerman.app.data.api

import com.hellogerman.app.data.models.OpenThesaurusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * OpenThesaurus API service interface for German synonyms and antonyms
 * Free API - no authentication required
 */
interface OpenThesaurusApiService {
    
    @GET("synonyme/search")
    suspend fun getSynonyms(
        @Query("q") word: String,
        @Query("format") format: String = "application/json",
        @Query("similar") similar: String = "true"
    ): Response<OpenThesaurusResponse>
    
    companion object {
        const val BASE_URL = "https://www.openthesaurus.de/"
        const val RATE_LIMIT = 60 // 60 requests per minute per IP
    }
}
