package com.hellogerman.app.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.hellogerman.app.data.api.TranslationApiService
import com.hellogerman.app.data.models.DictionarySearchRequest
import com.hellogerman.app.data.models.DictionarySearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repository for dictionary functionality using MyMemory Translation API
 */
class DictionaryRepository(private val context: Context) {
    
    private val apiService: TranslationApiService by lazy {
        createApiService()
    }
    
    private fun createApiService(): TranslationApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(TranslationApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return retrofit.create(TranslationApiService::class.java)
    }
    
    /**
     * Check if device has internet connection
     */
    fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
    
    /**
     * Search for word translation using MyMemory Translation API
     */
    suspend fun searchWord(request: DictionarySearchRequest): Result<DictionarySearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                if (!isInternetAvailable()) {
                    return@withContext Result.failure(Exception("No internet connection"))
                }
                
                val langPair = TranslationApiService.createLanguagePair(request.fromLang, request.toLang)
                val response = apiService.getTranslation(
                    query = request.word,
                    langPair = langPair
                )
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.responseStatus == 200) {
                        val translations = extractTranslations(body)
                        val result = DictionarySearchResult(
                            originalWord = request.word,
                            translations = translations,
                            fromLanguage = request.fromLang,
                            toLanguage = request.toLang,
                            hasResults = translations.isNotEmpty()
                        )
                        Result.success(result)
                    } else {
                        val errorMsg = body?.responseDetails ?: "Translation failed"
                        Result.failure(Exception(errorMsg))
                    }
                } else {
                    Result.failure(Exception("API request failed: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Extract translations from MyMemory API response
     */
    private fun extractTranslations(response: com.hellogerman.app.data.models.MyMemoryTranslationResponse): List<String> {
        val translations = mutableListOf<String>()
        
        // Add primary translation
        val primaryTranslation = response.responseData.translatedText.trim()
        if (primaryTranslation.isNotBlank()) {
            translations.add(primaryTranslation)
        }
        
        // Add alternative translations from matches
        response.matches?.forEach { match ->
            val translation = match.translation.trim()
            if (translation.isNotBlank() && 
                !translations.contains(translation) && 
                translation.lowercase() != primaryTranslation.lowercase()) {
                translations.add(translation)
            }
        }
        
        return translations.take(8) // Limit to 8 translations
    }
}
