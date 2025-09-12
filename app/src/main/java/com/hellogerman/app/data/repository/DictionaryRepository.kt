package com.hellogerman.app.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.hellogerman.app.data.api.*
import com.hellogerman.app.data.api.LibreTranslateRequest
import com.hellogerman.app.data.models.EnglishWordDefinition
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
    private val englishDictionaryApiService: EnglishDictionaryApiService by lazy { createEnglishDictionaryApiService() }
    private val libreTranslateApiService: LibreTranslateApiService by lazy { createLibreTranslateApiService() }
    
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
            .baseUrl("https://en.wiktionary.org/") // Default base URL, will be overridden by @Url parameter
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

    private fun createEnglishDictionaryApiService(): EnglishDictionaryApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(EnglishDictionaryApiService.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(EnglishDictionaryApiService::class.java)
    }

    private fun createLibreTranslateApiService(): LibreTranslateApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(LibreTranslateApiService.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(LibreTranslateApiService::class.java)
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
                // Only call verb API if word is likely a verb and we're dealing with German
                val isLikelyVerb = (request.fromLang == "de" || request.fromLang == "german") &&
                                   (GermanVerbConjugator.isLikelyVerb(request.word) ||
                                    GermanDictionary.getWordEntry(request.word)?.wordType == "verb")

                // Enhanced parallel API calls with better error handling
                val deferredResults = listOf(
                    async { getWiktionaryData(request) },
                    async { getEnglishWordData(request) },
                    async { getReversoExamples(request) },
                    if (isLikelyVerb) async { getVerbConjugations(request.word) } else async { null },
                    async { getSynonyms(request) },
                    async { getBasicTranslation(request) }
                )

                val results = deferredResults.awaitAll()
                val wiktionaryResult = results[0] as? DictionarySearchResult
                val englishWordResult = results[1] as? DictionarySearchResult
                val reversoExamples = results[2] as? List<Example> ?: emptyList()
                val conjugations = if (isLikelyVerb) results[3] as? VerbConjugations else null
                val synonyms = results[4] as? List<String> ?: emptyList()
                val basicTranslation = results[5] as? List<String> ?: emptyList()
                


                // Combine all results with guaranteed offline fallback
                val offlineEntry = GermanDictionary.getWordEntry(request.word)

                // Choose the best result based on language and availability
                // For English searches, prefer Wiktionary (which has German sections) over English-only API
                val primaryResult = when (request.fromLang.lowercase()) {
                    "en", "english" -> {
                        // For English words, prefer Wiktionary which has German translations and examples
                        // Only use English API as fallback if Wiktionary fails
                        wiktionaryResult ?: englishWordResult
                    }
                    else -> wiktionaryResult ?: englishWordResult
                }

                // Merge examples from all sources, prioritizing translated examples
                val allExamples = mutableListOf<Example>()

                // For English to German searches, only include examples that have translations
                val isEnglishToGerman = request.fromLang.lowercase() in listOf("en", "english") &&
                                       request.toLang.lowercase() in listOf("de", "german")

                if (isEnglishToGerman) {
                    // Only include examples with translations for English-to-German searches
                    allExamples.addAll(reversoExamples.filter { it.translation != null })
                    allExamples.addAll(primaryResult?.examples?.filter { it.translation != null } ?: emptyList())
                    allExamples.addAll(wiktionaryResult?.examples?.filter { it.translation != null } ?: emptyList())
                    allExamples.addAll(englishWordResult?.examples?.filter { it.translation != null } ?: emptyList())

                    // If we still don't have examples, fall back to offline German examples
                    if (allExamples.isEmpty()) {
                        allExamples.addAll(offlineEntry?.examples ?: emptyList())
                    }
                } else {
                    // For other language combinations, include all examples
                    allExamples.addAll(reversoExamples)
                    allExamples.addAll(primaryResult?.examples ?: emptyList())
                    allExamples.addAll(wiktionaryResult?.examples ?: emptyList())
                    allExamples.addAll(englishWordResult?.examples ?: emptyList())
                    if (allExamples.isEmpty()) {
                        allExamples.addAll(offlineEntry?.examples ?: emptyList())
                    }
                }

                // Merge synonyms from all sources
                val allSynonyms = mutableListOf<String>()
                allSynonyms.addAll(primaryResult?.synonyms ?: emptyList())
                allSynonyms.addAll(synonyms)
                val mergedSynonyms = allSynonyms.distinct()

                val combinedResult = DictionarySearchResult(
                    originalWord = request.word,
                    translations = basicTranslation,
                    fromLanguage = request.fromLang,
                    toLanguage = request.toLang,
                    hasResults = primaryResult?.hasResults == true || basicTranslation.isNotEmpty() ||
                                reversoExamples.isNotEmpty() || offlineEntry != null,
                    definitions = (primaryResult?.definitions ?: emptyList()).ifEmpty {
                        offlineEntry?.definitions ?: emptyList()
                    },
                    examples = allExamples.distinctBy { it.sentence }, // Remove duplicates
                    synonyms = mergedSynonyms,
                    pronunciation = primaryResult?.pronunciation ?: wiktionaryResult?.pronunciation,
                    conjugations = conjugations ?: if (offlineEntry?.wordType == "verb") {
                        GermanVerbConjugator.getConjugation(request.word)
                    } else null,
                    etymology = primaryResult?.etymology ?: wiktionaryResult?.etymology,
                    wordType = primaryResult?.wordType ?: offlineEntry?.wordType,
                    gender = primaryResult?.gender ?: offlineEntry?.gender
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
    private suspend fun getWiktionaryData(request: DictionarySearchRequest): DictionarySearchResult? {
        return try {
            // Get the appropriate Wiktionary base URL for the source language
            val baseUrl = WiktionaryApiService.getBaseUrlForLanguage(request.fromLang)
            val apiUrl = WiktionaryApiService.createApiUrl(baseUrl)

            // Try Wiktionary API first
            val response = wiktionaryApiService.getWordDefinition(url = apiUrl, page = request.word)
            if (response.isSuccessful) {
                val wikitext = response.body()?.parse?.wikitext?.content
                if (wikitext != null) {
                    wiktionaryParser.parseWiktionaryContent(request.word, wikitext, request.fromLang)
                } else {
                    // Use offline dictionary as fallback
                    createOfflineResult(request.word, request.fromLang, request.toLang)
                }
            } else {
                // Use offline dictionary for 403/404 errors
                createOfflineResult(request.word, request.fromLang, request.toLang)
            }
        } catch (e: Exception) {
            // Use offline dictionary on any error
            createOfflineResult(request.word, request.fromLang, request.toLang)
        }
    }
    
    /**
     * Create result from offline dictionary
     */
    private fun createOfflineResult(word: String, fromLang: String, toLang: String): DictionarySearchResult? {
        val entry = GermanDictionary.getWordEntry(word)
        return if (entry != null) {
            DictionarySearchResult(
                originalWord = word,
                fromLanguage = fromLang,
                toLanguage = toLang,
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
     * Get synonyms from appropriate thesaurus service based on language
     */
    private suspend fun getSynonyms(request: DictionarySearchRequest): List<String> {
        return try {
            when (request.fromLang.lowercase()) {
                "de", "german" -> {
                    // Use OpenThesaurus for German words
                    val response = thesaurusApiService.getSynonyms(request.word)
                    if (response.isSuccessful) {
                        val synonyms = mutableListOf<String>()
                        response.body()?.synsets?.forEach { synset ->
                            synset.terms.forEach { term ->
                                if (term.term != request.word && !synonyms.contains(term.term)) {
                                    synonyms.add(term.term)
                                }
                            }
                        }
                        synonyms.take(10) // Limit to 10 synonyms
                    } else emptyList()
                }
                "en", "english" -> {
                    // Get synonyms from English Dictionary API
                    getEnglishSynonyms(request.word)
                }
                else -> emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get comprehensive English word definitions and related data
     */
    private suspend fun getEnglishWordData(request: DictionarySearchRequest): DictionarySearchResult? {
        return try {
            val response = englishDictionaryApiService.getWordDefinition(request.word)
            if (response.isSuccessful) {
                val definitions = response.body()
                if (!definitions.isNullOrEmpty()) {
                    convertEnglishApiResponseToDictionaryResult(definitions.first(), request)
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get English synonyms from the English Dictionary API
     */
    private suspend fun getEnglishSynonyms(word: String): List<String> {
        return try {
            val response = englishDictionaryApiService.getWordDefinition(word)
            if (response.isSuccessful) {
                val synonyms = mutableListOf<String>()
                response.body()?.forEach { definition ->
                    definition.meanings.forEach { meaning ->
                        meaning.synonyms?.let { synonyms.addAll(it) }
                        meaning.definitions.forEach { def ->
                            def.synonyms?.let { synonyms.addAll(it) }
                        }
                    }
                }
                synonyms.distinct().take(10)
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Convert English Dictionary API response to our DictionarySearchResult format
     * For English-to-German searches, we skip examples since they won't have German translations
     */
    private fun convertEnglishApiResponseToDictionaryResult(
        englishDefinition: EnglishWordDefinition,
        request: DictionarySearchRequest
    ): DictionarySearchResult {
        val definitions = mutableListOf<Definition>()
        val examples = mutableListOf<Example>()
        val synonyms = mutableListOf<String>()

        englishDefinition.meanings.forEach { meaning ->
            // Convert definitions
            meaning.definitions.forEach { def ->
                definitions.add(Definition(
                    meaning = def.definition,
                    partOfSpeech = meaning.partOfSpeech,
                    context = null,
                    level = null
                ))

                // Only add examples if we're searching within English (English to English)
                // For English to German searches, skip examples as they won't have translations
                if (request.toLang.lowercase() in listOf("en", "english")) {
                    def.example?.let { example ->
                        examples.add(Example(
                            sentence = example,
                            source = "Free Dictionary API"
                        ))
                    }
                }

                // Collect synonyms
                def.synonyms?.let { synonyms.addAll(it) }
            }

            // Collect synonyms from meaning level
            meaning.synonyms?.let { synonyms.addAll(it) }
        }

        // Get pronunciation
        val pronunciation = englishDefinition.phonetic?.let { phonetic ->
            Pronunciation(
                ipa = phonetic,
                audioUrl = englishDefinition.phonetics?.firstOrNull()?.audio,
                region = "English"
            )
        }

        return DictionarySearchResult(
            originalWord = englishDefinition.word,
            fromLanguage = request.fromLang,
            toLanguage = request.toLang,
            hasResults = definitions.isNotEmpty(),
            definitions = definitions.take(5),
            examples = examples.take(5),
            synonyms = synonyms.distinct().take(8),
            pronunciation = pronunciation,
            wordType = englishDefinition.meanings.firstOrNull()?.partOfSpeech
        )
    }
    
    /**
     * Get basic translation from multiple APIs (MyMemory + LibreTranslate as fallback)
     */
    private suspend fun getBasicTranslation(request: DictionarySearchRequest): List<String> {
        val translations = mutableListOf<String>()

        // Try MyMemory API first
        try {
            val langPair = TranslationApiService.createLanguagePair(request.fromLang, request.toLang)
            val response = translationApiService.getTranslation(
                query = request.word,
                langPair = langPair
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.responseStatus == 200) {
                    translations.addAll(extractTranslations(body))
                }
            }
        } catch (e: Exception) {
            // MyMemory failed, continue to LibreTranslate
        }

        // Try LibreTranslate as fallback if MyMemory didn't work or returned few results
        if (translations.size < 3) {
            try {
                val libreResponse = libreTranslateApiService.translate(
                    LibreTranslateRequest(
                        q = request.word,
                        source = request.fromLang,
                        target = request.toLang
                    )
                )

                if (libreResponse.isSuccessful) {
                    val translatedText = libreResponse.body()?.translatedText
                    if (!translatedText.isNullOrBlank() && !translations.contains(translatedText)) {
                        translations.add(translatedText)
                    }
                }
            } catch (e: Exception) {
                // Both translation services failed
            }
        }

        return translations.take(8)
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
