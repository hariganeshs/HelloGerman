package com.hellogerman.app.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Tatoeba API service for bilingual example sentences
 * Documentation: https://tatoeba.org/eng/help/api
 */
interface TatoebaApiService {
    companion object {
        const val BASE_URL = "https://tatoeba.org/"
    }

    /**
     * Search for bilingual sentence pairs
     * @param query The word to search for
     * @param from Source language code (e.g., "deu" for German)
     * @param to Target language code (e.g., "eng" for English)
     * @param orphans Whether to include orphaned sentences (no translation)
     * @param unapproved Whether to include unapproved sentences
     * @param native Whether to include native sentences only
     * @param limit Maximum number of results (default 10, max 100)
     */
    @GET("eng/api_v0/search")
    suspend fun searchSentences(
        @Query("query") query: String,
        @Query("from") from: String = "deu",
        @Query("to") to: String = "eng",
        @Query("orphans") orphans: String = "no",
        @Query("unapproved") unapproved: String = "no",
        @Query("native") native: String = "no",
        @Query("limit") limit: Int = 10
    ): Response<List<TatoebaSentence>>
}

/**
 * Tatoeba sentence response model
 */
data class TatoebaSentence(
    val id: Int,
    val text: String,
    val lang: String,
    val script: String? = null,
    val license: String? = null,
    val translations: List<TatoebaTranslation>? = null
)

/**
 * Tatoeba translation model
 */
data class TatoebaTranslation(
    val id: Int,
    val text: String,
    val lang: String,
    val script: String? = null,
    val license: String? = null
)
