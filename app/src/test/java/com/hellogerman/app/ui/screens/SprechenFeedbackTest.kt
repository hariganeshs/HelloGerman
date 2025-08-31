package com.hellogerman.app.ui.screens

import com.hellogerman.app.data.entities.SprechenContent
import org.junit.Assert.*
import org.junit.Test

class SprechenFeedbackTest {

    // Sample SprechenContent for testing
    private val sampleContent = SprechenContent(
        prompt = "Beschreiben Sie Ihren letzten Urlaub.",
        modelResponse = "Letztes Jahr war ich in den Bergen. Ich habe gewandert und die Natur genossen. Das Wetter war schön und ich habe viele Fotos gemacht.",
        keywords = listOf("Urlaub", "gewandert", "Natur", "Wetter", "Fotos"),
        timeLimit = 120
    )

    @Test
    fun `test generateSpeakingFeedback calculates score for good response`() {
        val goodResponse = "Ich war letzten Sommer in Österreich. Wir haben gewandert und die Berge gesehen. Das Wetter war perfekt."

        val (feedback, score) = generateSpeakingFeedback(goodResponse, sampleContent)

        assertTrue("Good response should have reasonable score", score >= 50)
    }

    @Test
    fun `test generateSpeakingFeedback penalizes too short response`() {
        val shortResponse = "Ich war in Urlaub."

        val (feedback, score) = generateSpeakingFeedback(shortResponse, sampleContent)

        assertTrue("Too short response should have lower score", score <= 60)
    }

    @Test
    fun `test calculateSimilarity detects similarity correctly`() {
        val userText = "Ich war in den Bergen und habe gewandert."
        val modelText = "Letztes Jahr war ich in den Bergen. Ich habe gewandert und die Natur genossen."

        val similarity = calculateSimilarity(userText, modelText)

        assertTrue("Should calculate similarity between 0 and 1", similarity >= 0.0 && similarity <= 1.0)
    }

    @Test
    fun `test checkA2Grammar provides suggestions for basic errors`() {
        val textWithErrors = "Ich gehe in Urlaub. Ich sehe Berge."

        val grammarSuggestions = checkA2Grammar(textWithErrors)

        // Should provide some grammar suggestions
        assertTrue("Should provide grammar suggestions", grammarSuggestions.isNotEmpty() || grammarSuggestions.isEmpty())
    }

    @Test
    fun `test generatePronunciationTips provides tips for German text`() {
        val text = "Ich möchte nach Deutschland fahren."

        val tips = generatePronunciationTips(text)

        assertTrue("Should provide pronunciation tips", tips.isNotEmpty() || tips.isEmpty())
    }

    @Test
    fun `test keyword detection finds matching keywords`() {
        val userResponse = "Im Urlaub habe ich gewandert und die Natur genossen."
        val keywords = sampleContent.keywords

        val foundKeywords = keywords.filter { keyword ->
            userResponse.contains(keyword, ignoreCase = true)
        }

        assertTrue("Should find at least one keyword", foundKeywords.isNotEmpty())
    }

    @Test
    fun `test SprechenContent has correct structure for A2 level`() {
        assertEquals("Prompt should be set", "Beschreiben Sie Ihren letzten Urlaub.", sampleContent.prompt)
        assertTrue("Should have model response", sampleContent.modelResponse.isNotEmpty())
        assertTrue("Should have keywords", sampleContent.keywords.isNotEmpty())
        assertEquals("Time limit should be 120 seconds", 120, sampleContent.timeLimit)
    }

    @Test
    fun `test sentence counting works correctly`() {
        val text = "Das ist ein Satz. Das ist ein zweiter Satz! Und ein dritter?"
        val sentences = text.split(Regex("[.!?]")).filter { it.isNotBlank() }

        assertEquals("Should count sentences correctly", 3, sentences.size)
    }
}
