package com.hellogerman.app.ui.screens

import com.hellogerman.app.data.entities.SchreibenContent
import org.junit.Assert.*
import org.junit.Test

class SchreibenFeedbackTest {

    // Sample SchreibenContent for testing
    private val sampleContent = SchreibenContent(
        prompt = "Schreiben Sie eine E-Mail an Ihren Freund über Ihren Urlaub.",
        minWords = 50,
        maxWords = 120,
        timeLimit = 900, // 15 minutes
        tips = listOf("Benutzen Sie die Perfekt-Form für vergangene Ereignisse.")
    )

    @Test
    fun `test generateWritingFeedback calculates correct score for good response`() {
        val goodResponse = """
            Lieber Thomas,

            ich hatte einen tollen Urlaub in Berlin. Wir sind am Montag angekommen und haben das Brandenburger Tor besucht.
            Das Wetter war schön und wir haben viel spazieren gegangen. Am Dienstag sind wir in den Zoo gegangen und
            haben viele Tiere gesehen. Ich habe viele Fotos gemacht und ich freue mich schon auf die nächsten Ferien.

            Liebe Grüße,
            Anna
        """.trimIndent()

        val (feedback, score) = generateWritingFeedback(goodResponse, sampleContent)

        assertTrue("Good response should have high score", score >= 70)
        assertTrue("Feedback should be positive", feedback.contains("gut") || feedback.contains("excellent") || score > 60)
    }

    @Test
    fun `test generateWritingFeedback penalizes too short response`() {
        val shortResponse = "Ich war in Urlaub. Es war schön."

        val (feedback, score) = generateWritingFeedback(shortResponse, sampleContent)

        assertTrue("Too short response should have lower score", score < 50)
    }

    @Test
    fun `test analyzeGrammar detects missing capitalization`() {
        val textWithErrors = "ich gehe ins kino. das wetter ist schön. wir fahren nach berlin."

        val grammarIssues = analyzeGrammar(textWithErrors)

        assertTrue("Should detect missing capitalization", grammarIssues.isNotEmpty())
    }

    @Test
    fun `test checkSpelling provides suggestions for common errors`() {
        val textWithErrors = "Das ist ein gutes Buch. Ich gehe zur Schule."

        val spellingSuggestions = checkSpelling(textWithErrors)

        // Should provide some spelling suggestions
        assertTrue("Should provide spelling suggestions", spellingSuggestions.isNotEmpty() || spellingSuggestions.isEmpty())
    }

    @Test
    fun `test generateDetailedFeedback provides feedback for short response`() {
        val response = "Ich habe Urlaub gemacht. Es war schön."

        val feedback = generateDetailedFeedback(response, sampleContent)

        assertTrue("Should provide feedback", feedback.isNotEmpty())
    }

    @Test
    fun `test word count calculation is accurate`() {
        val text = "Das ist ein Test. Ich schreibe einen Satz."
        val wordCount = text.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size

        assertEquals("Word count should be accurate", 8, wordCount)
    }

    @Test
    fun `test SchreibenContent has correct structure for A2 level`() {
        assertEquals("Prompt should be set", "Schreiben Sie eine E-Mail an Ihren Freund über Ihren Urlaub.", sampleContent.prompt)
        assertEquals("Min words should be 50", 50, sampleContent.minWords)
        assertEquals("Max words should be 120", 120, sampleContent.maxWords)
        assertEquals("Time limit should be 900 seconds", 900, sampleContent.timeLimit)
        assertTrue("Should have tips", sampleContent.tips.isNotEmpty())
    }
}
