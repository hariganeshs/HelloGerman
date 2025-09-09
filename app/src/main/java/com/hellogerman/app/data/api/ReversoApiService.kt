package com.hellogerman.app.data.api

import com.hellogerman.app.data.models.ReversoExample
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Reverso Context API service interface for bilingual examples
 * Free API - no authentication required
 */
interface ReversoApiService {

    @GET("bst-query-context/{fromLang}-{toLang}")
    suspend fun getExamples(
        @Path("fromLang") fromLang: String,
        @Path("toLang") toLang: String,
        @Query("query") query: String,
        @Query("limit") limit: Int = 10
    ): Response<List<ReversoExample>>

    companion object {
        const val BASE_URL = "https://context.reverso.net/"
        const val RATE_LIMIT = 30 // 30 requests per minute per IP
    }
}
