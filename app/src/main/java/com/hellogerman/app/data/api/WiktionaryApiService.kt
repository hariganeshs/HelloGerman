package com.hellogerman.app.data.api

import com.hellogerman.app.data.models.WiktionaryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Wiktionary API service interface for definitions, examples, and etymology
 * Free API - no authentication required
 * Supports both German and English Wiktionary dynamically
 */
interface WiktionaryApiService {

    @GET
    suspend fun getWordDefinition(
        @Url url: String,
        @Query("action") action: String = "parse",
        @Query("format") format: String = "json",
        @Query("prop") prop: String = "wikitext",
        @Query("page") page: String,
        @Query("disableeditsection") disableEditSection: String = "true"
    ): Response<WiktionaryResponse>

    companion object {
        const val GERMAN_BASE_URL = "https://de.wiktionary.org/"
        const val ENGLISH_BASE_URL = "https://en.wiktionary.org/"

        // Create the full API URL based on language
        fun createApiUrl(baseUrl: String): String {
            return "${baseUrl}w/api.php"
        }

        // Get the appropriate base URL for the language
        fun getBaseUrlForLanguage(language: String): String {
            return when (language.lowercase()) {
                "de", "german" -> GERMAN_BASE_URL
                "en", "english" -> ENGLISH_BASE_URL
                else -> ENGLISH_BASE_URL // Default to English for unknown languages
            }
        }
    }
}
