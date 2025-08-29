package com.hellogerman.app.data.api

import com.hellogerman.app.data.models.WiktionaryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Wiktionary API service interface for definitions, examples, and etymology
 * Free API - no authentication required
 */
interface WiktionaryApiService {
    
    @GET("w/api.php")
    suspend fun getWordDefinition(
        @Query("action") action: String = "parse",
        @Query("format") format: String = "json",
        @Query("prop") prop: String = "wikitext",
        @Query("page") page: String,
        @Query("disableeditsection") disableEditSection: String = "true"
    ): Response<WiktionaryResponse>
    
    companion object {
        const val BASE_URL = "https://de.wiktionary.org/"
        const val FALLBACK_BASE_URL = "https://en.wiktionary.org/"
    }
}
