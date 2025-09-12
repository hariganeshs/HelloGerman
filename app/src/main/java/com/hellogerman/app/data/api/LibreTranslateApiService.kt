package com.hellogerman.app.data.api

import com.hellogerman.app.data.models.LibreTranslateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * LibreTranslate API service for free translations
 * Self-hosted or public instances available
 */
interface LibreTranslateApiService {

    @POST("translate")
    suspend fun translate(@Body request: LibreTranslateRequest): Response<LibreTranslateResponse>

    companion object {
        // Public LibreTranslate instance (may have rate limits)
        const val BASE_URL = "https://libretranslate.com/"

        // Alternative instances (commented out, can be used if main instance is down)
        // const val BASE_URL = "https://translate.astian.org/"
        // const val BASE_URL = "https://translate.argosopentech.com/"
    }
}

data class LibreTranslateRequest(
    val q: String,      // Text to translate
    val source: String, // Source language
    val target: String, // Target language
    val format: String = "text"
)
