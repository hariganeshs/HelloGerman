package com.hellogerman.app.data.api

import com.hellogerman.app.data.models.EnglishWordDefinition
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Free Dictionary API service for English word definitions
 * Uses https://api.dictionaryapi.dev/ - free API with no authentication required
 */
interface EnglishDictionaryApiService {

    @GET("api/v2/entries/en/{word}")
    suspend fun getWordDefinition(@Path("word") word: String): Response<List<EnglishWordDefinition>>

    companion object {
        const val BASE_URL = "https://api.dictionaryapi.dev/"
    }
}

