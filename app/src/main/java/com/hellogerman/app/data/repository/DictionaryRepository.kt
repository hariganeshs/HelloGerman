package com.hellogerman.app.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.hellogerman.app.data.api.*
import com.hellogerman.app.data.models.*
import com.hellogerman.app.data.parser.WiktionaryParser
import com.hellogerman.app.data.conjugation.GermanVerbConjugator
import com.hellogerman.app.data.dictionary.GermanDictionary
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Enhanced dictionary repository using multiple free APIs
 * - Wiktionary for definitions, examples, etymology
 * - German Verb API for conjugations  
 * - OpenThesaurus for synonyms
 * - MyMemory for fallback translations
 */
class DictionaryRepository(private val context: Context) {
    
    private val cache = mutableMapOf<String, CachedDictionaryEntry>()
    private val wiktionaryParser = WiktionaryParser()
    
    // API Services
    private val translationApiService: TranslationApiService by lazy { createTranslationApiService() }
    private val wiktionaryApiService: WiktionaryApiService by lazy { createWiktionaryApiService() }
    private val verbApiService: GermanVerbApiService by lazy { createVerbApiService() }
    private val thesaurusApiService: OpenThesaurusApiService by lazy { createThesaurusApiService() }
    private val reversoApiService: ReversoApiService by lazy { createReversoApiService() }
    
    private fun createHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC // Reduced logging for production
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("User-Agent", "HelloGerman/1.0 (German Learning App)")
                    .header("Accept", "application/json")
                
                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    
    private fun createTranslationApiService(): TranslationApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(TranslationApiService.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(TranslationApiService::class.java)
    }
    
    private fun createWiktionaryApiService(): WiktionaryApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(WiktionaryApiService.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(WiktionaryApiService::class.java)
    }
    
    private fun createVerbApiService(): GermanVerbApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(GermanVerbApiService.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(GermanVerbApiService::class.java)
    }
    
    private fun createThesaurusApiService(): OpenThesaurusApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(OpenThesaurusApiService.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(OpenThesaurusApiService::class.java)
    }

    private fun createReversoApiService(): ReversoApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(ReversoApiService.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ReversoApiService::class.java)
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
     * Comprehensive dictionary search using multiple APIs
     */
    suspend fun searchWord(request: DictionarySearchRequest): Result<DictionarySearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Check cache first
                val cacheKey = "${request.word}_${request.fromLang}"
                cache[cacheKey]?.let { cached ->
                    if (System.currentTimeMillis() - cached.timestamp < 3600000) { // 1 hour cache
                        return@withContext Result.success(cached.result)
                    }
                }
                
                if (!isInternetAvailable()) {
                    return@withContext Result.failure(Exception("No internet connection"))
                }
                
                // Launch parallel API calls for comprehensive data
                // Only call verb API if word is likely a verb
                val isLikelyVerb = GermanVerbConjugator.isLikelyVerb(request.word) ||
                                   GermanDictionary.getWordEntry(request.word)?.wordType == "verb"

                // Enhanced parallel API calls with better error handling
                val deferredResults = listOf(
                    async { getWiktionaryData(request.word) },
                    async { getReversoExamples(request) },
                    if (isLikelyVerb) async { getVerbConjugations(request.word) } else async { null },
                    async { getSynonyms(request.word) },
                    async { getBasicTranslation(request) }
                )

                val results = deferredResults.awaitAll()
                val wiktionaryResult = results[0] as? DictionarySearchResult
                val reversoExamples = results[1] as? List<Example> ?: emptyList()
                val conjugations = if (isLikelyVerb) results[2] as? VerbConjugations else null
                val synonyms = results[3] as? List<String> ?: emptyList()
                val basicTranslation = results[4] as? List<String> ?: emptyList()
                

                
                // Combine all results with guaranteed offline fallback
                val offlineEntry = GermanDictionary.getWordEntry(request.word)

                // Merge examples from all sources
                val allExamples = mutableListOf<Example>()
                allExamples.addAll(wiktionaryResult?.examples ?: emptyList())
                allExamples.addAll(reversoExamples)
                if (allExamples.isEmpty()) {
                    allExamples.addAll(offlineEntry?.examples ?: emptyList())
                }

                val combinedResult = DictionarySearchResult(
                    originalWord = request.word,
                    translations = basicTranslation,
                    fromLanguage = request.fromLang,
                    toLanguage = request.toLang,
                    hasResults = wiktionaryResult?.hasResults == true || basicTranslation.isNotEmpty() ||
                                reversoExamples.isNotEmpty() || offlineEntry != null,
                    definitions = (wiktionaryResult?.definitions ?: emptyList()).ifEmpty {
                        offlineEntry?.definitions ?: emptyList()
                    },
                    examples = allExamples.distinctBy { it.sentence }, // Remove duplicates
                    synonyms = synonyms,
                    pronunciation = wiktionaryResult?.pronunciation,
                    conjugations = conjugations ?: if (offlineEntry?.wordType == "verb") {
                        GermanVerbConjugator.getConjugation(request.word)
                    } else null,
                    etymology = wiktionaryResult?.etymology,
                    wordType = wiktionaryResult?.wordType ?: offlineEntry?.wordType,
                    gender = wiktionaryResult?.gender ?: offlineEntry?.gender
                )
                
                // Cache result
                cache[cacheKey] = CachedDictionaryEntry(
                    word = request.word,
                    language = request.fromLang,
                    result = combinedResult
                )
                
                Result.success(combinedResult)
                
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get comprehensive word data from Wiktionary with offline fallback
     */
    private suspend fun getWiktionaryData(word: String): DictionarySearchResult? {
        return try {
            // Try Wiktionary API first
            val response = wiktionaryApiService.getWordDefinition(page = word)
            if (response.isSuccessful) {
                val wikitext = response.body()?.parse?.wikitext?.content
                if (wikitext != null) {
                    wiktionaryParser.parseWiktionaryContent(word, wikitext)
                } else {
                    // Use offline dictionary as fallback
                    createOfflineResult(word)
                }
            } else {
                // Use offline dictionary for 403/404 errors
                createOfflineResult(word)
            }
        } catch (e: Exception) {
            // Use offline dictionary on any error
            createOfflineResult(word)
        }
    }
    
    /**
     * Create result from offline dictionary
     */
    private fun createOfflineResult(word: String): DictionarySearchResult? {
        val entry = GermanDictionary.getWordEntry(word)
        return if (entry != null) {
            DictionarySearchResult(
                originalWord = word,
                fromLanguage = "de",
                toLanguage = "en", 
                hasResults = true,
                definitions = entry.definitions,
                examples = entry.examples,
                wordType = entry.wordType,
                gender = entry.gender
            )
        } else null
    }
    
    /**
     * Get verb conjugations from German Verb API with fallback
     */
    private suspend fun getVerbConjugations(word: String): VerbConjugations? {
        return try {
            // Try external API first
            val response = verbApiService.getVerbConjugation(word)
            if (response.isSuccessful) {
                response.body()?.conjugations
            } else {
                // Fallback to local conjugation system
                GermanVerbConjugator.getConjugation(word)
            }
        } catch (e: Exception) {
            // Use fallback system on any error
            GermanVerbConjugator.getConjugation(word)
        }
    }
    
    /**
     * Get synonyms from OpenThesaurus
     */
    private suspend fun getSynonyms(word: String): List<String> {
        return try {
            val response = thesaurusApiService.getSynonyms(word)
            if (response.isSuccessful) {
                val synonyms = mutableListOf<String>()
                response.body()?.synsets?.forEach { synset ->
                    synset.terms.forEach { term ->
                        if (term.term != word && !synonyms.contains(term.term)) {
                            synonyms.add(term.term)
                        }
                    }
                }
                synonyms.take(10) // Limit to 10 synonyms
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get basic translation from MyMemory API (fallback)
     */
    private suspend fun getBasicTranslation(request: DictionarySearchRequest): List<String> {
        return try {
            val langPair = TranslationApiService.createLanguagePair(request.fromLang, request.toLang)
            val response = translationApiService.getTranslation(
                query = request.word,
                langPair = langPair
            )
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.responseStatus == 200) {
                    extractTranslations(body)
                } else emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Extract translations from MyMemory API response
     */
    private fun extractTranslations(response: MyMemoryTranslationResponse): List<String> {
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
    
    /**
     * Get contextual examples from Reverso Context API
     */
    private suspend fun getReversoExamples(request: DictionarySearchRequest): List<Example> {
        return try {
            val response = reversoApiService.getExamples(
                fromLang = request.fromLang,
                toLang = request.toLang,
                query = request.word,
                limit = 5
            )

            if (response.isSuccessful) {
                response.body()?.map { reversoExample ->
                    Example(
                        sentence = reversoExample.sourceText,
                        translation = reversoExample.targetText,
                        source = "Reverso Context"
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Clear cache (for memory management)
     */
    fun clearCache() {
        cache.clear()
    }

    /**
     * Get cached entries count
     */
    fun getCacheSize(): Int = cache.size
}
