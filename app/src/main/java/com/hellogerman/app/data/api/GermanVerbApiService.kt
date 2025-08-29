package com.hellogerman.app.data.api

import com.hellogerman.app.data.models.VerbConjugationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * German Verb Conjugation API service interface
 * Free API for German verb conjugations - no authentication required
 */
interface GermanVerbApiService {
    
    @GET("api/verbs/{verb}")
    suspend fun getVerbConjugation(
        @Path("verb") verb: String
    ): Response<VerbConjugationResponse>
    
    // Alternative endpoint for direct verb lookup
    @GET("conjugation/{verb}")
    suspend fun getVerbConjugationAlt(
        @Path("verb") verb: String
    ): Response<VerbConjugationResponse>
    
    companion object {
        const val BASE_URL = "https://german-verbs-api.onrender.com/"
        // Alternative services for verb conjugation
        const val FALLBACK_BASE_URL = "https://api.verbformen.com/"
        const val ALT_BASE_URL = "https://conjugator.reverso.net/conjugation-german-verb-"
    }
}
