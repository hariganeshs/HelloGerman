package com.hellogerman.app.data.repository

import android.content.Context
import androidx.room.Room
import com.hellogerman.app.data.database.*
import com.hellogerman.app.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first dictionary repository with comprehensive German coverage
 * Falls back to APIs only when words aren't found offline
 */
@Singleton
class OfflineDictionaryRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val onlineDictionaryRepository: DictionaryRepository // Fallback to existing online repo
) {
    
    private lateinit var database: GermanDictionaryDatabase
    private var isInitialized = false
    
    suspend fun initialize() {
        if (isInitialized) return
        
        withContext(Dispatchers.IO) {
            android.util.Log.d("OfflineDict", "Initializing offline dictionary database...")
            
            // Initialize Room database
            database = Room.databaseBuilder(
                context,
                GermanDictionaryDatabase::class.java,
                "german_dictionary.db"
            )
            .fallbackToDestructiveMigration()
            .build()
            
            // Populate database if empty
            val wordCount = database.dictionaryDao().getWordCount()
            android.util.Log.d("OfflineDict", "Current word count in database: $wordCount")
            
            if (wordCount == 0) {
                android.util.Log.d("OfflineDict", "Database is empty, populating...")
                populateDatabase()
                val newWordCount = database.dictionaryDao().getWordCount()
                android.util.Log.d("OfflineDict", "After population, word count: $newWordCount")
            }
            
            isInitialized = true
            android.util.Log.d("OfflineDict", "Offline dictionary initialization complete")
        }
    }
    
    /**
     * Main search function - offline first, API fallback
     */
    suspend fun searchWord(request: DictionarySearchRequest): Result<DictionarySearchResult> {
        return try {
            if (!isInitialized) initialize()
            
            val word = request.word.lowercase().trim()
            
            // Debug logging
            android.util.Log.d("OfflineDict", "Searching for word: $word")
            
            // 1. Try offline database first
            val offlineResult = searchOfflineDatabase(word)
            android.util.Log.d("OfflineDict", "Offline result hasResults: ${offlineResult.hasResults}, gender: ${offlineResult.gender}")
            
            if (offlineResult.hasResults) {
                android.util.Log.d("OfflineDict", "Returning offline result for: $word")
                return Result.success(offlineResult)
            }
            
            // 2. Try compound word analysis (German specialty)
            val compoundResult = analyzeCompoundWord(word)
            if (compoundResult.hasResults) {
                android.util.Log.d("OfflineDict", "Returning compound result for: $word")
                return Result.success(compoundResult)
            }
            
            // 3. Fallback to online APIs only if offline fails
            android.util.Log.d("OfflineDict", "Falling back to online APIs for: $word")
            return onlineDictionaryRepository.searchWord(request)
            
        } catch (e: Exception) {
            android.util.Log.e("OfflineDict", "Error searching for ${request.word}", e)
            // Even on error, try to return something useful
            val basicResult = createBasicResult(request.word)
            Result.success(basicResult)
        }
    }
    
    private suspend fun searchOfflineDatabase(word: String): DictionarySearchResult {
        val dao = database.dictionaryDao()
        
        // Get word data
        val wordEntity = dao.getWord(word)
        val examples = dao.getExamples(word)
        
        return if (wordEntity != null) {
            // Convert database entities to result
            val definitions = DictionaryConverters().toDefinitionsList(wordEntity.definitions)
            val examplesList = examples.map { 
                Example(it.germanSentence, it.englishTranslation) 
            }
            
            DictionarySearchResult(
                originalWord = word,
                fromLanguage = "de",
                toLanguage = "en",
                hasResults = true,
                definitions = definitions,
                examples = examplesList,
                wordType = wordEntity.wordType,
                gender = wordEntity.gender,
                pronunciation = wordEntity.pronunciation?.let { 
                    Pronunciation(it, "de")
                },
                difficulty = wordEntity.level
            )
        } else {
            // Empty result
            DictionarySearchResult(
                originalWord = word,
                fromLanguage = "de",
                toLanguage = "en",
                hasResults = false
            )
        }
    }
    
    /**
     * Analyze German compound words (Komposita)
     * e.g., "hausregeln" = "haus" + "regeln"
     */
    private suspend fun analyzeCompoundWord(word: String): DictionarySearchResult {
        if (word.length < 6) return createEmptyResult(word)
        
        val dao = database.dictionaryDao()
        
        // Try common compound patterns
        for (splitPoint in 3..word.length-3) {
            val firstPart = word.substring(0, splitPoint)
            val secondPart = word.substring(splitPoint)
            
            val firstWord = dao.getWord(firstPart)
            val secondWord = dao.getWord(secondPart)
            
            if (firstWord != null && secondWord != null) {
                // Found compound word components
                val combinedDefinition = "Compound word: ${firstWord.wordType} + ${secondWord.wordType}"
                
                return DictionarySearchResult(
                    originalWord = word,
                    fromLanguage = "de",
                    toLanguage = "en",
                    hasResults = true,
                    definitions = listOf(
                        Definition(combinedDefinition, "compound"),
                        Definition("First part: ${firstPart}", secondWord.wordType),
                        Definition("Second part: ${secondPart}", secondWord.wordType)
                    ),
                    examples = listOf(
                        Example("$word ist ein zusammengesetztes Wort.", "$word is a compound word.")
                    ),
                    wordType = "compound",
                    etymology = "Compound of $firstPart + $secondPart"
                )
            }
        }
        
        return createEmptyResult(word)
    }
    
    private fun createBasicResult(word: String): DictionarySearchResult {
        return DictionarySearchResult(
            originalWord = word,
            fromLanguage = "de",
            toLanguage = "en",
            hasResults = true,
            definitions = listOf(
                Definition("German word: $word", "unknown"),
                Definition("No offline definition available", "note")
            ),
            examples = listOf(
                Example("Suche nach '$word' online für mehr Informationen.", "Search for '$word' online for more information.")
            )
        )
    }
    
    private fun createEmptyResult(word: String): DictionarySearchResult {
        return DictionarySearchResult(
            originalWord = word,
            fromLanguage = "de",
            toLanguage = "en",
            hasResults = false
        )
    }
    
    /**
     * Populate database with comprehensive German data
     */
    private suspend fun populateDatabase() {
        val dao = database.dictionaryDao()
        val converter = DictionaryConverters()
        
        // Get essential German words
        val essentialWords = ComprehensiveGermanData.getEssentialGermanWords()
        
        // Convert to database entities
        val wordEntities = essentialWords.map { wordData ->
            val definitions = wordData.definition.split(";").map { def ->
                Definition(def.trim(), wordData.wordType, wordData.level)
            }
            
            OfflineWordEntity(
                word = wordData.word,
                definitions = converter.fromDefinitionsList(definitions),
                wordType = wordData.wordType,
                gender = wordData.gender,
                frequency = wordData.frequency,
                level = wordData.level,
                pronunciation = wordData.pronunciation,
                etymology = wordData.etymology
            )
        }
        
        // Create example entities
        val exampleEntities = essentialWords.flatMap { wordData ->
            wordData.germanExamples.zip(wordData.englishTranslations).map { (german, english) ->
                OfflineExampleEntity(
                    word = wordData.word,
                    germanSentence = german,
                    englishTranslation = english,
                    difficulty = wordData.level
                )
            }
        }
        
        // Insert into database
        dao.insertWords(wordEntities)
        dao.insertExamples(exampleEntities)
    }
    
    /**
     * Get word suggestions for autocomplete
     */
    suspend fun getWordSuggestions(prefix: String): List<String> {
        if (!isInitialized) return emptyList()
        
        return try {
            val suggestions = database.dictionaryDao().searchWords("$prefix%")
            suggestions.take(10).map { it.word }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get words by CEFR level
     */
    suspend fun getWordsByLevel(level: String): List<String> {
        if (!isInitialized) return emptyList()
        
        return try {
            val words = database.dictionaryDao().getWordsByLevel(level)
            words.map { it.word }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get database statistics
     */
    suspend fun getDatabaseStats(): DatabaseStats {
        if (!isInitialized) initialize()
        
        return try {
            val totalWords = database.dictionaryDao().getWordCount()
            DatabaseStats(
                totalWords = totalWords,
                offlineCapable = true,
                lastUpdated = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            DatabaseStats(0, false, 0)
        }
    }
}

data class DatabaseStats(
    val totalWords: Int,
    val offlineCapable: Boolean,
    val lastUpdated: Long
)
