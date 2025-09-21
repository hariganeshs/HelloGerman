package com.hellogerman.app.data.api

import com.hellogerman.app.data.models.PronunciationData
import com.hellogerman.app.data.models.WiktionaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Wiktionary API service for retrieving pronunciation and grammar information
 * Uses the Wiktionary API to get IPA pronunciation data and other linguistic information
 */
interface WiktionaryApiService {

    /**
     * Get pronunciation data for a word
     * @param word The word to get pronunciation for
     */
    suspend fun getPronunciation(word: String): PronunciationData?

    /**
     * Get word definition from Wiktionary
     * @param url The API URL to use
     * @param page The word to get definition for
     */
    @GET("w/api.php")
    suspend fun getWordDefinition(@Query("page") page: String): WiktionaryResponse

    companion object {
        const val GERMAN_BASE_URL = "https://de.wiktionary.org/"
        const val ENGLISH_BASE_URL = "https://en.wiktionary.org/"

        fun getBaseUrlForLanguage(language: String): String {
            return when (language.lowercase()) {
                "de", "german" -> GERMAN_BASE_URL
                "en", "english" -> ENGLISH_BASE_URL
                else -> ENGLISH_BASE_URL
            }
        }

        fun createApiUrl(baseUrl: String): String {
            return "$baseUrl/w/api.php"
        }
    }

    /**
     * Get page content from Wiktionary for a specific word
     * @param action The API action (parse for getting wikitext content)
     * @param format Response format (json)
     * @param prop Properties to retrieve (wikitext for parsing pronunciation)
     * @param page The word to look up
     * @param disableeditsection Disable edit sections in the response
     */
    @GET("w/api.php")
    suspend fun getPageContent(
        @Query("action") action: String = "parse",
        @Query("format") format: String = "json",
        @Query("prop") prop: String = "wikitext",
        @Query("page") page: String,
        @Query("disableeditsection") disableeditsection: Boolean = true
    ): WiktionaryResponse

    /**
     * Search for pages on Wiktionary
     * @param action The API action (query for searching)
     * @param format Response format (json)
     * @param list Search list (search for finding pages)
     * @param srsearch Search query
     * @param srlimit Maximum number of results
     * @param srnamespace Namespace to search in (0 for main namespace)
     */
    @GET("w/api.php")
    suspend fun searchPages(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "search",
        @Query("srsearch") srsearch: String,
        @Query("srlimit") srlimit: Int = 10,
        @Query("srnamespace") srnamespace: Int = 0
    ): WiktionarySearchResponse
}

/**
 * Response model for Wiktionary search API
 */
data class WiktionarySearchResponse(
    val query: WiktionarySearchQuery
)

data class WiktionarySearchQuery(
    val search: List<WiktionarySearchResult>
)

data class WiktionarySearchResult(
    val title: String,
    val pageid: Int,
    val size: Int,
    val wordcount: Int,
    val snippet: String,
    val timestamp: String
)