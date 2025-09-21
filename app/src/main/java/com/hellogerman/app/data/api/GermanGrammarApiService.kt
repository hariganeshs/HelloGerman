package com.hellogerman.app.data.api

import com.hellogerman.app.data.models.GrammarInfo
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API service for German grammar information including verb conjugations,
 * noun declensions, and adjective declensions
 */
interface GermanGrammarApiService {

    /**
     * Get verb conjugation information
     * @param verb The verb to get conjugation for
     */
    @GET("api/verbs/{verb}")
    suspend fun getVerbConjugation(@Path("verb") verb: String): VerbConjugationResponse

    /**
     * Get noun declension information
     * @param noun The noun to get declension for
     */
    @GET("api/nouns/{noun}")
    suspend fun getNounDeclension(@Path("noun") noun: String): NounDeclensionResponse

    /**
     * Get adjective declension information
     * @param adjective The adjective to get declension for
     */
    @GET("api/adjectives/{adjective}")
    suspend fun getAdjectiveDeclension(@Path("adjective") adjective: String): AdjectiveDeclensionResponse

    /**
     * Get comprehensive grammar information for a word
     * @param word The word to get grammar info for
     */
    @GET("api/grammar/{word}")
    suspend fun getGrammarInfo(@Path("word") word: String): GrammarInfo
}

/**
 * Response models for German grammar API
 */
data class VerbConjugationResponse(
    val verb: String,
    val conjugations: com.hellogerman.app.data.models.VerbConjugation
)

data class NounDeclensionResponse(
    val noun: String,
    val declension: com.hellogerman.app.data.models.NounDeclension
)

data class AdjectiveDeclensionResponse(
    val adjective: String,
    val declension: com.hellogerman.app.data.models.AdjectiveDeclension
)
