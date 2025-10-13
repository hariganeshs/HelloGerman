package com.hellogerman.app.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hellogerman.app.data.models.LeoDictionaryEntry
import com.hellogerman.app.data.models.WordType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test class for LeoDictionaryRepository
 */
@RunWith(AndroidJUnit4::class)
class LeoDictionaryRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: LeoDictionaryRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        repository = LeoDictionaryRepository(context)
    }

    @Test
    fun testSearchGermanWord() = runTest {
        // Test searching for a common German word
        val result = repository.searchGermanWord("Haus")

        assertNotNull("Search result should not be null", result)
        assertTrue("Should have results for 'Haus'", result.hasResults)
        assertTrue("Should have at least one entry", result.entries.isNotEmpty())

        val entry = result.entries.first()
        assertEquals("Word should be 'Haus'", "Haus", entry.germanWord)
        assertEquals("Should be a noun", WordType.NOUN, entry.wordType)
        assertNotNull("Should have gender information", entry.gender)
        assertNotNull("Should have article", entry.article)
    }

    @Test
    fun testSearchEnglishWord() = runTest {
        // Test searching for an English word
        val result = repository.searchEnglishWord("house")

        assertNotNull("Search result should not be null", result)
        assertTrue("Should have results for 'house'", result.hasResults)
        assertTrue("Should have at least one entry", result.entries.isNotEmpty())

        val entry = result.entries.first()
        assertTrue("Should have English translations", entry.englishTranslations.isNotEmpty())
    }

    @Test
    fun testGermanWordWithGender() = runTest {
        // Test that German nouns show correct gender
        val result = repository.searchGermanWord("Mutter")

        if (result.hasResults) {
            val entry = result.entries.first()
            assertEquals("Word should be 'Mutter'", "Mutter", entry.germanWord)
            assertEquals("Should be a noun", WordType.NOUN, entry.wordType)

            // Check that gender is properly detected
            if (entry.gender != null) {
                val article = entry.gender.getArticle()
                assertTrue("Article should be 'die' for Mutter", article == "die")
            }
        }
    }

    @Test
    fun testVerbConjugation() = runTest {
        // Test verb conjugation information
        val result = repository.searchGermanWord("gehen")

        if (result.hasResults) {
            val entry = result.entries.first()
            assertEquals("Word should be 'gehen'", "gehen", entry.germanWord)
            assertEquals("Should be a verb", WordType.VERB, entry.wordType)

            // Should have auxiliary information for verbs
            assertNotNull("Should have auxiliary information", entry.auxiliary)
        }
    }

    @Test
    fun testCacheFunctionality() = runTest {
        // Test that caching works
        val word = "Wasser"

        // First search
        val result1 = repository.searchGermanWord(word)

        // Second search should use cache
        val result2 = repository.searchGermanWord(word)

        assertEquals("Cached results should be identical", result1.originalWord, result2.originalWord)
        assertEquals("Cached results should have same entries", result1.entries.size, result2.entries.size)
    }

    @Test
    fun testNoResults() = runTest {
        // Test searching for a word that doesn't exist
        val result = repository.searchGermanWord("nonexistentword")

        assertNotNull("Search result should not be null", result)
        assertFalse("Should not have results for nonexistent word", result.hasResults)
    }
}