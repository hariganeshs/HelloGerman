package com.hellogerman.app.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.hellogerman.app.data.api.*
import com.hellogerman.app.data.api.LibreTranslateRequest
import com.hellogerman.app.data.api.WikidataLexemeEntity
import com.hellogerman.app.data.api.WikidataForm
import com.hellogerman.app.data.api.WikidataSense
import com.hellogerman.app.data.models.EnglishWordDefinition
import com.hellogerman.app.data.models.*
import com.hellogerman.app.data.parser.WiktionaryParser
import com.hellogerman.app.data.conjugation.GermanVerbConjugator
import com.hellogerman.app.data.dictionary.GermanDictionary
import com.hellogerman.app.data.HelloGermanDatabase
import com.hellogerman.app.data.entities.DictionaryCacheEntry
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
    companion object {
        // Bump this to invalidate in-memory cache when parsing/merge logic changes
        private const val CACHE_VERSION = 2
        private const val CACHE_TTL_HOURS = 72L // 72 hours TTL
    }
    
    private val cache = mutableMapOf<String, CachedDictionaryEntry>()
    private val wiktionaryParser = WiktionaryParser()
    private val database = HelloGermanDatabase.getDatabase(context)
    private val dictionaryCacheDao = database.dictionaryCacheDao()
    
    // API Services
    private val translationApiService: TranslationApiService by lazy { createTranslationApiService() }
    private val wiktionaryApiService: WiktionaryApiService by lazy { createWiktionaryApiService() }
    private val verbApiService: GermanVerbApiService by lazy { createVerbApiService() }
    private val thesaurusApiService: OpenThesaurusApiService by lazy { createThesaurusApiService() }
    private val reversoApiService: ReversoApiService by lazy { createReversoApiService() }
    private val englishDictionaryApiService: EnglishDictionaryApiService by lazy { createEnglishDictionaryApiService() }
    private val libreTranslateApiService: LibreTranslateApiService by lazy { createLibreTranslateApiService() }
    private val tatoebaApiService: TatoebaApiService by lazy { createTatoebaApiService() }
    private val wikidataLexemeService: WikidataLexemeService by lazy { createWikidataLexemeService() }
    
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

    private fun createTatoebaApiService(): TatoebaApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(TatoebaApiService.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(TatoebaApiService::class.java)
    }

    private fun createWikidataLexemeService(): WikidataLexemeService {
        val retrofit = Retrofit.Builder()
            .baseUrl(WikidataLexemeService.BASE_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(WikidataLexemeService::class.java)
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
                // Check database cache first
                val cachedEntry = dictionaryCacheDao.getCachedEntry(
                    word = request.word,
                    fromLanguage = request.fromLang,
                    toLanguage = request.toLang
                )
                
                // TEMPORARY: Clear cache for "apfel" to test fresh parsing
                if (request.word.lowercase() == "apfel") {
                    android.util.Log.d("DictionaryRepository", "TEMPORARY: Clearing cache for 'apfel' to test fresh parsing")
                    clearCacheForWord(request.word, request.fromLang, request.toLang)
                }
                
                if (cachedEntry != null && request.word.lowercase() != "apfel") {
                    android.util.Log.d("DictionaryRepository", "Using cached result for '${request.word}': gender='${cachedEntry.searchResult.gender}'")
                    return@withContext Result.success(cachedEntry.searchResult)
                }
                
                // Check in-memory cache as fallback
                val cacheKey = "$CACHE_VERSION:${request.word}_${request.fromLang}"
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
                    async { getTatoebaExamples(request) },
                    async { getReversoExamples(request) },
                    async { getWikidataLexemeData(request) },
                    if (isLikelyVerb) async { getVerbConjugations(request.word) } else async { null },
                    async { getSynonyms(request) },
                    async { getBasicTranslation(request) }
                )

                val results = deferredResults.awaitAll()
                val wiktionaryResult = results[0] as? DictionarySearchResult
                val englishWordResult = results[1] as? DictionarySearchResult
                val tatoebaExamples = results[2] as? List<Example> ?: emptyList()
                val reversoExamples = results[3] as? List<Example> ?: emptyList()
                val wikidataLexemeData = results[4] as? WikidataLexemeData ?: null
                val conjugations = if (isLikelyVerb) results[5] as? VerbConjugations else null
                val synonyms = results[6] as? List<String> ?: emptyList()
                val basicTranslation = results[7] as? List<String> ?: emptyList()
                


                // Combine all results with guaranteed offline fallback
                val offlineEntry = GermanDictionary.getWordEntry(request.word).also { entry ->
                    android.util.Log.d("DictionaryRepository", "Offline lookup for '${request.word}': found=${entry != null}, gender='${entry?.gender}', wordType='${entry?.wordType}'")
                }

                // Choose the best result based on language and availability
                // For English-to-German searches, prioritize German translations over English definitions
                val primaryResult = when (request.fromLang.lowercase()) {
                    "en", "english" -> {
                        // For English words, prioritize translation results over English definitions
                        android.util.Log.d("DictionaryRepository", "English-to-German search for '${request.word}': basicTranslation=${basicTranslation.size}, wiktionaryResult=${wiktionaryResult?.hasResults}, englishWordResult=${englishWordResult?.hasResults}")
                        
                        // Try multiple sources for German translations
                        val allGermanTranslations = mutableListOf<String>()
                        allGermanTranslations.addAll(basicTranslation)
                        
                        // Add translations from Wiktionary if available
                        wiktionaryResult?.translations?.let { allGermanTranslations.addAll(it) }
                        
                        // Add translations from examples that have German content (will be processed later)
                        // Note: allExamples is defined later, so we'll handle this in the final result combination
                        
                        // Remove duplicates and filter for German content
                        val germanTranslations = allGermanTranslations.distinct().filter { translation ->
                            translation.contains(Regex("[äöüßÄÖÜ]")) || 
                            translation.contains(Regex("\\b(der|die|das|ein|eine|einen|einem|einer)\\b", RegexOption.IGNORE_CASE))
                        }
                        
                        android.util.Log.d("DictionaryRepository", "Filtered German translations: $germanTranslations")
                        
                        if (germanTranslations.isNotEmpty()) {
                            // Create a result focused on German translations
                            createTranslationFocusedResult(request, germanTranslations, wiktionaryResult, englishWordResult)
                        } else {
                            // Try hardcoded fallback translations for common words
                            val fallbackTranslation = getFallbackGermanTranslation(request.word)
                            if (fallbackTranslation != null) {
                                android.util.Log.d("DictionaryRepository", "Using fallback German translation: $fallbackTranslation")
                                createTranslationFocusedResult(request, listOf(fallbackTranslation), wiktionaryResult, englishWordResult)
                            } else {
                                // Fallback to English definitions if no translations available
                                android.util.Log.d("DictionaryRepository", "No German translations found, falling back to English definitions")
                                wiktionaryResult ?: englishWordResult
                            }
                        }
                    }
                    else -> wiktionaryResult ?: englishWordResult
                }

                // Merge examples from all sources, prioritizing translated examples
                val allExamples = mutableListOf<Example>()

                // For English to German searches, only include examples that have translations
                val isEnglishToGerman = request.fromLang.lowercase() in listOf("en", "english") &&
                                       request.toLang.lowercase() in listOf("de", "german")

                if (isEnglishToGerman) {
                    // Only include examples with German translations for English-to-German searches
                    // Prioritize Tatoeba examples (licensed and reliable)
                    allExamples.addAll(tatoebaExamples.filter { it.translation != null && it.translation.contains(Regex("[äöüßÄÖÜ]")) })
                    allExamples.addAll(reversoExamples.filter { it.translation != null && it.translation.contains(Regex("[äöüßÄÖÜ]")) })
                    
                    // Only include Wiktionary examples if they have German translations
                    allExamples.addAll(primaryResult?.examples?.filter { it.translation != null && it.translation.contains(Regex("[äöüßÄÖÜ]")) } ?: emptyList())
                    allExamples.addAll(wiktionaryResult?.examples?.filter { it.translation != null && it.translation.contains(Regex("[äöüßÄÖÜ]")) } ?: emptyList())
                    
                    // Skip English-only examples from English Dictionary API
                    // allExamples.addAll(englishWordResult?.examples?.filter { it.translation != null } ?: emptyList())

                    // If we still don't have examples, fall back to offline German examples
                    if (allExamples.isEmpty()) {
                        allExamples.addAll(offlineEntry?.examples ?: emptyList())
                    }
                } else {
                    // For other language combinations, include all examples
                    // Prioritize Tatoeba examples (licensed and reliable)
                    allExamples.addAll(tatoebaExamples)
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
                    wordType = wikidataLexemeData?.lexicalCategory ?: primaryResult?.wordType ?: offlineEntry?.wordType,
                    // Prefer offline dictionary gender (verified correct) over Wikidata/parsed values
                    // Wikidata Q-codes need proper resolution, so trust offline data first
                    gender = offlineEntry?.gender ?: wikidataLexemeData?.gender?.let { wikidataGender ->
                        // Convert Wikidata gender format (masculine/feminine/neuter) to article format (der/die/das)
                        when (wikidataGender.lowercase()) {
                            "masculine" -> "der"
                            "feminine" -> "die"
                            "neuter" -> "das"
                            else -> wikidataGender
                        }
                    } ?: primaryResult?.gender.also { finalGender ->
                        android.util.Log.d("DictionaryRepository", "Gender assignment for '${request.word}': offline='${offlineEntry?.gender}', wikidata='${wikidataLexemeData?.gender}', primary='${primaryResult?.gender}', final='$finalGender'")
                    },
                    wikidataLexemeData = wikidataLexemeData
                )
                
                // Cache result in database
                val sources = mutableListOf<String>()
                if (wiktionaryResult != null) sources.add("Wiktionary")
                if (tatoebaExamples.isNotEmpty()) sources.add("Tatoeba")
                if (reversoExamples.isNotEmpty()) sources.add("Reverso")
                if (wikidataLexemeData != null) sources.add("Wikidata")
                if (conjugations != null) sources.add("German Verb API")
                if (synonyms.isNotEmpty()) sources.add("OpenThesaurus")
                if (basicTranslation.isNotEmpty()) sources.add("MyMemory/LibreTranslate")
                
                val cacheEntry = DictionaryCacheEntry(
                    word = request.word,
                    fromLanguage = request.fromLang,
                    toLanguage = request.toLang,
                    searchResult = combinedResult,
                    sources = sources,
                    expiresAt = System.currentTimeMillis() + (CACHE_TTL_HOURS * 60 * 60 * 1000),
                    cacheVersion = CACHE_VERSION
                )
                
                dictionaryCacheDao.insertCacheEntry(cacheEntry)
                
                // Also cache in memory for immediate access
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
     * Get fallback German translation for common English words
     * Used when translation APIs fail or return no results
     */
    private fun getFallbackGermanTranslation(englishWord: String): String? {
        val fallbackTranslations = mapOf(
            "mother" to "die Mutter",
            "father" to "der Vater",
            "brother" to "der Bruder",
            "sister" to "die Schwester",
            "son" to "der Sohn",
            "daughter" to "die Tochter",
            "family" to "die Familie",
            "house" to "das Haus",
            "car" to "das Auto",
            "book" to "das Buch",
            "water" to "das Wasser",
            "food" to "das Essen",
            "time" to "die Zeit",
            "day" to "der Tag",
            "night" to "die Nacht",
            "sun" to "die Sonne",
            "moon" to "der Mond",
            "tree" to "der Baum",
            "flower" to "die Blume",
            "cat" to "die Katze",
            "dog" to "der Hund",
            "bird" to "der Vogel",
            "fish" to "der Fisch",
            "bread" to "das Brot",
            "milk" to "die Milch",
            "coffee" to "der Kaffee",
            "tea" to "der Tee",
            "apple" to "der Apfel",
            "orange" to "die Orange",
            "banana" to "die Banane",
            "red" to "rot",
            "blue" to "blau",
            "green" to "grün",
            "yellow" to "gelb",
            "black" to "schwarz",
            "white" to "weiß",
            "table" to "der Tisch",
            "chair" to "der Stuhl",
            "door" to "die Tür",
            "window" to "das Fenster",
            "bed" to "das Bett",
            "phone" to "das Telefon",
            "computer" to "der Computer",
            "school" to "die Schule",
            "work" to "die Arbeit",
            "friend" to "der Freund",
            "family" to "die Familie",
            "love" to "die Liebe",
            "happy" to "glücklich",
            "sad" to "traurig",
            "big" to "groß",
            "small" to "klein",
            "good" to "gut",
            "bad" to "schlecht",
            "new" to "neu",
            "old" to "alt",
            "young" to "jung",
            "beautiful" to "schön",
            "ugly" to "hässlich",
            "fast" to "schnell",
            "slow" to "langsam",
            "hot" to "heiß",
            "cold" to "kalt",
            "warm" to "warm",
            "cool" to "kühl"
        )
        
        return fallbackTranslations[englishWord.lowercase()]
    }

    /**
     * Create a translation-focused result for English-to-German searches
     * Prioritizes German translations and German grammar information
     */
    private fun createTranslationFocusedResult(
        request: DictionarySearchRequest,
        translations: List<String>,
        wiktionaryResult: DictionarySearchResult?,
        englishWordResult: DictionarySearchResult?
    ): DictionarySearchResult {
        // Start with basic translation information
        val germanTranslations = translations.filter { translation ->
            // Filter out English words, keep German translations
            translation.matches(Regex(".*[äöüßÄÖÜ].*")) || // Contains German characters
            translation.matches(Regex(".*\\b(der|die|das|ein|eine|einen|einem|einer)\\b.*", RegexOption.IGNORE_CASE)) || // Contains German articles
            translation.length > 3 && !translation.matches(Regex("[a-zA-Z\\s]+")) // Not purely English
        }
        
        // Create German definitions from translations with proper German context
        val germanDefinitions = germanTranslations.mapIndexed { index, translation ->
            val cleanTranslation = translation.replace(Regex("\\b(der|die|das)\\b\\s*"), "").trim()
            Definition(
                meaning = if (cleanTranslation.isNotEmpty()) cleanTranslation else translation,
                partOfSpeech = if (translation.contains(Regex("\\b(der|die|das)\\b"))) "noun" else "word",
                level = if (index == 0) "primary" else "alternative"
            )
        }
        
        // Extract German grammar information from the first translation
        val firstTranslation = germanTranslations.firstOrNull()
        val germanGender = firstTranslation?.let { translation ->
            when {
                translation.contains(Regex("\\bder\\b", RegexOption.IGNORE_CASE)) -> "der"
                translation.contains(Regex("\\bdie\\b", RegexOption.IGNORE_CASE)) -> "die"
                translation.contains(Regex("\\bdas\\b", RegexOption.IGNORE_CASE)) -> "das"
                else -> null
            }
        }
        
        // Determine word type based on translation patterns
        val wordType = firstTranslation?.let { translation ->
            when {
                translation.contains(Regex("\\b(der|die|das)\\b", RegexOption.IGNORE_CASE)) -> "noun"
                translation.endsWith("en") && !translation.endsWith("gen") -> "verb"
                translation.endsWith("ig") || translation.endsWith("isch") -> "adjective"
                translation.endsWith("lich") -> "adjective"
                translation.endsWith("los") -> "adjective"
                translation.length <= 4 && !translation.contains(" ") -> "adjective" // Short words likely adjectives
                else -> "word"
            }
        }
        
        return DictionarySearchResult(
            originalWord = request.word,
            translations = germanTranslations,
            fromLanguage = request.fromLang,
            toLanguage = request.toLang,
            hasResults = germanTranslations.isNotEmpty(),
            definitions = germanDefinitions,
            examples = listOf(), // Will be filled by example merging logic
            wordType = wordType,
            gender = germanGender,
            pronunciation = null // Will be filled if available from other sources
        )
    }

    /**
     * Get comprehensive word data from Wiktionary with offline fallback
     */
    private suspend fun getWiktionaryData(request: DictionarySearchRequest): DictionarySearchResult? {
        return try {
            // For English-to-German searches, use German Wiktionary to get German translations
            val baseUrl = if (request.fromLang.lowercase() in listOf("en", "english") && 
                               request.toLang.lowercase() in listOf("de", "german")) {
                // Use German Wiktionary for English words to get German translations
                WiktionaryApiService.GERMAN_BASE_URL
            } else {
                // Use appropriate Wiktionary base URL for the source language
                WiktionaryApiService.getBaseUrlForLanguage(request.fromLang)
            }
            val apiUrl = WiktionaryApiService.createApiUrl(baseUrl)

            // Try Wiktionary API first
            try {
                val wiktionaryResponse = wiktionaryApiService.getWordDefinition(page = request.word)
                val wikitext = wiktionaryResponse.parse?.wikitext?.content
                if (wikitext != null) {
                    wiktionaryParser.parseWiktionaryContent(request.word, wikitext, request.fromLang.toString())
                } else {
                    // Use offline dictionary as fallback
                    createOfflineResult(request.word, request.fromLang, request.toLang)
                }
            } catch (e: Exception) {
                // Use offline dictionary for API errors
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
                response.body()?.conjugations as? VerbConjugations
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
     * Get Wikidata lexeme data for enhanced grammatical information
     */
    private suspend fun getWikidataLexemeData(request: DictionarySearchRequest): WikidataLexemeData? {
        return try {
            // Only search for German words
            if (request.fromLang.lowercase() !in listOf("de", "german")) {
                return null
            }

            val response = wikidataLexemeService.searchLexemes(
                search = request.word,
                language = "de",
                limit = 5
            )

            if (response.isSuccessful) {
                val searchResults = response.body()?.search
                if (!searchResults.isNullOrEmpty()) {
                    // Get the first lexeme's detailed data
                    val lexemeId = searchResults.first().id
                    val lexemeResponse = wikidataLexemeService.getLexemeData(lexemeId)
                    
                    if (lexemeResponse.isSuccessful) {
                        val lexemeEntity = lexemeResponse.body()?.entities?.values?.firstOrNull()
                        if (lexemeEntity != null) {
                            parseWikidataLexemeEntity(lexemeEntity)
                        } else null
                    } else null
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse Wikidata lexeme entity into our data model
     */
    private fun parseWikidataLexemeEntity(entity: WikidataLexemeEntity): WikidataLexemeData {
        val lexicalCategory = entity.lexicalCategory?.id?.let { categoryId ->
            when {
                categoryId.contains("noun") -> "noun"
                categoryId.contains("verb") -> "verb"
                categoryId.contains("adjective") -> "adjective"
                categoryId.contains("adverb") -> "adverb"
                else -> null
            }
        }

        val forms = entity.forms?.map { form ->
            com.hellogerman.app.data.models.WikidataForm(
                id = form.id,
                representation = form.representations?.values?.firstOrNull()?.value ?: "",
                grammaticalFeatures = form.grammaticalFeatures ?: emptyList()
            )
        } ?: emptyList()

        val senses = entity.senses?.map { sense ->
            com.hellogerman.app.data.models.WikidataSense(
                id = sense.id,
                gloss = sense.glosses?.values?.firstOrNull()?.value
            )
        } ?: emptyList()

        // Extract gender for nouns
        // Wikidata uses Q-codes for grammatical features, so we need to map them
        val gender = entity.grammaticalFeatures?.find { feature ->
            // Common Wikidata Q-codes for German gender
            when (feature) {
                "Q499327", // masculine
                "Q110786", // masculine (alternative)
                "Q131105"  // masculine (alternative)
                -> true
                "Q1775415", // feminine
                "Q145599"   // feminine (alternative)
                -> true
                "Q1775461", // neuter
                "Q146786"   // neuter (alternative)
                -> true
                else -> feature.contains("masculine") || feature.contains("feminine") || feature.contains("neuter")
            }
        }?.let { feature ->
            when {
                feature == "Q499327" || feature == "Q110786" || feature == "Q131105" || feature.contains("masculine") -> "masculine"
                feature == "Q1775415" || feature == "Q145599" || feature.contains("feminine") -> "feminine"
                feature == "Q1775461" || feature == "Q146786" || feature.contains("neuter") -> "neuter"
                else -> null
            }
        }

        // Extract plural form
        val plural = forms.find { form ->
            form.grammaticalFeatures.any { feature -> feature.contains("plural") }
        }?.representation

        // Build declension map
        val declensions = forms.associate { form ->
            val case = form.grammaticalFeatures.find { feature ->
                feature.contains("nominative") || feature.contains("genitive") || 
                feature.contains("dative") || feature.contains("accusative")
            } ?: "nominative"
            case to form.representation
        }

        return WikidataLexemeData(
            lexemeId = entity.id,
            lexicalCategory = lexicalCategory,
            language = entity.language,
            grammaticalFeatures = entity.grammaticalFeatures ?: emptyList(),
            forms = forms,
            senses = senses,
            gender = gender,
            plural = plural,
            declensions = declensions
        )
    }

    /**
     * Get contextual examples from Tatoeba API (licensed bilingual sentences)
     */
    private suspend fun getTatoebaExamples(request: DictionarySearchRequest): List<Example> {
        return try {
            // Map language codes to Tatoeba format
            val fromLang = when (request.fromLang.lowercase()) {
                "de", "german" -> "deu"
                "en", "english" -> "eng"
                else -> "deu" // Default to German
            }
            
            val toLang = when (request.toLang.lowercase()) {
                "de", "german" -> "deu"
                "en", "english" -> "eng"
                else -> "eng" // Default to English
            }

            val response = tatoebaApiService.searchSentences(
                query = request.word,
                from = fromLang,
                to = toLang,
                limit = 8
            )

            if (response.isSuccessful) {
                response.body()?.mapNotNull { sentence ->
                    // Only include sentences that have translations
                    val translation = sentence.translations?.firstOrNull()
                    if (translation != null) {
                        Example(
                            sentence = sentence.text,
                            translation = translation.text,
                            source = "Tatoeba"
                        )
                    } else null
                } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
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
    
    /**
     * Clear database cache
     */
    suspend fun clearDatabaseCache() {
        android.util.Log.d("DictionaryRepository", "Clearing database cache")
        dictionaryCacheDao.clearAllCache()
    }
    
    /**
     * Clear cache for specific word
     */
    suspend fun clearCacheForWord(word: String, fromLang: String, toLang: String) {
        android.util.Log.d("DictionaryRepository", "Clearing cache for word: '$word' ($fromLang -> $toLang)")
        // Clear database cache for this specific word
        dictionaryCacheDao.deleteCacheEntry(word, fromLang, toLang)
        // Also clear in-memory cache
        val cacheKey = "$CACHE_VERSION:${word}_${fromLang}"
        cache.remove(cacheKey)
    }
    
    /**
     * Delete expired cache entries
     */
    suspend fun cleanupExpiredCache() {
        dictionaryCacheDao.deleteExpiredEntries()
    }
    
    /**
     * Get cache statistics
     */
    suspend fun getCacheStats(): CacheStats {
        val totalEntries = dictionaryCacheDao.getCacheSize()
        val recentEntries = dictionaryCacheDao.getRecentEntries(10)
        return CacheStats(
            totalEntries = totalEntries,
            recentEntries = recentEntries.size,
            memoryCacheSize = cache.size
        )
    }
    
    /**
     * Search cached entries
     */
    suspend fun searchCachedEntries(query: String): List<DictionaryCacheEntry> {
        return dictionaryCacheDao.searchCacheEntries(query)
    }
}

/**
 * Cache statistics data class
 */
data class CacheStats(
    val totalEntries: Int,
    val recentEntries: Int,
    val memoryCacheSize: Int
)
