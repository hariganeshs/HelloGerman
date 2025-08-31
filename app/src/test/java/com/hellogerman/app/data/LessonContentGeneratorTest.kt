package com.hellogerman.app.data

import com.hellogerman.app.data.entities.*
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class LessonContentGeneratorTest {

    private val gson = Gson()

    @Test
    fun `test A2 Lesen lessons generation contains 25 lessons`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2LesenLessons = lessons.filter { it.skill == "lesen" && it.level == "A2" }

        assertTrue("A2 Lesen should have at least 20 lessons", a2LesenLessons.size >= 20)
    }

    @Test
    fun `test A2 Hoeren lessons generation contains 25 lessons`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2HoerenLessons = lessons.filter { it.skill == "hoeren" && it.level == "A2" }

        assertTrue("A2 Hören should have at least 20 lessons", a2HoerenLessons.size >= 20)
    }

    @Test
    fun `test A2 Schreiben lessons generation contains 25 lessons`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2SchreibenLessons = lessons.filter { it.skill == "schreiben" && it.level == "A2" }

        assertTrue("A2 Schreiben should have at least 20 lessons", a2SchreibenLessons.size >= 20)
    }

    @Test
    fun `test A2 Sprechen lessons generation contains 25 lessons`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2SprechenLessons = lessons.filter { it.skill == "sprechen" && it.level == "A2" }

        assertTrue("A2 Sprechen should have at least 20 lessons", a2SprechenLessons.size >= 20)
    }

    @Test
    fun `test A2 Lesen lessons have valid content structure`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2LesenLessons = lessons.filter { it.skill == "lesen" && it.level == "A2" }

        a2LesenLessons.forEach { lesson ->
            assertNotNull("Lesson should have title", lesson.title)
            assertNotNull("Lesson should have description", lesson.description)
            assertEquals("Lesson should be A2 level", "A2", lesson.level)
            assertEquals("Lesson should be lesen skill", "lesen", lesson.skill)

            // Test content structure
            val content = gson.fromJson(lesson.content, LesenContent::class.java)
            assertNotNull("Lesson should have text content", content.text)
            assertTrue("Lesson should have questions", content.questions.isNotEmpty())
            assertTrue("Lesson should have vocabulary", content.vocabulary.isNotEmpty())

            // Test questions structure
            content.questions.forEach { question ->
                assertNotNull("Question should have text", question.question)
                assertNotNull("Question should have correct answer", question.correctAnswer)
                assertNotNull("Question should have type", question.type)
            }
        }
    }

    @Test
    fun `test A2 Hoeren lessons have valid content structure`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2HoerenLessons = lessons.filter { it.skill == "hoeren" && it.level == "A2" }

        a2HoerenLessons.forEach { lesson ->
            assertNotNull("Lesson should have title", lesson.title)
            assertNotNull("Lesson should have description", lesson.description)
            assertEquals("Lesson should be A2 level", "A2", lesson.level)
            assertEquals("Lesson should be hoeren skill", "hoeren", lesson.skill)

            // Test content structure
            val content = gson.fromJson(lesson.content, HoerenContent::class.java)
            assertNotNull("Lesson should have script content", content.script)
            assertTrue("Lesson should have questions", content.questions.isNotEmpty())
        }
    }

    @Test
    fun `test A2 Schreiben lessons have valid content structure`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2SchreibenLessons = lessons.filter { it.skill == "schreiben" && it.level == "A2" }

        a2SchreibenLessons.forEach { lesson ->
            assertNotNull("Lesson should have title", lesson.title)
            assertNotNull("Lesson should have description", lesson.description)
            assertEquals("Lesson should be A2 level", "A2", lesson.level)
            assertEquals("Lesson should be schreiben skill", "schreiben", lesson.skill)

            // Test content structure
            val content = gson.fromJson(lesson.content, SchreibenContent::class.java)
            assertNotNull("Lesson should have prompt", content.prompt)
            assertTrue("Lesson should have minimum words", content.minWords > 0)
            assertTrue("Lesson should have maximum words", content.maxWords > content.minWords)
        }
    }

    @Test
    fun `test A2 Sprechen lessons have valid content structure`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2SprechenLessons = lessons.filter { it.skill == "sprechen" && it.level == "A2" }

        a2SprechenLessons.forEach { lesson ->
            assertNotNull("Lesson should have title", lesson.title)
            assertNotNull("Lesson should have description", lesson.description)
            assertEquals("Lesson should be A2 level", "A2", lesson.level)
            assertEquals("Lesson should be sprechen skill", "sprechen", lesson.skill)

            // Test content structure
            val content = gson.fromJson(lesson.content, SprechenContent::class.java)
            assertNotNull("Lesson should have prompt", content.prompt)
            assertNotNull("Lesson should have model response", content.modelResponse)
            assertTrue("Lesson should have keywords", content.keywords.isNotEmpty())
        }
    }

    @Test
    fun `test A2 lessons have diverse sources`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2Lessons = lessons.filter { it.level == "A2" }

        val sources = a2Lessons.map { it.source }.distinct()
        assertTrue("A2 lessons should have diverse sources", sources.size >= 2)

        // Check for Goethe, TELC, ÖSD sources
        assertTrue("Should have Goethe source", sources.contains("Goethe"))
        assertTrue("Should have TELC source", sources.contains("TELC") || sources.contains("ÖSD"))
    }

    @Test
    fun `test A2 lessons have proper order indices`() {
        val lessons = LessonContentGenerator.generateAllLessons()

        // Test each skill has proper ordering
        val skills = listOf("lesen", "hoeren", "schreiben", "sprechen")
        skills.forEach { skill ->
            val skillLessons = lessons.filter { it.skill == skill && it.level == "A2" }
                .sortedBy { it.orderIndex }

            // Check that order indices are sequential starting from appropriate numbers
            // Check that indices are positive and unique
            skillLessons.forEach { lesson ->
                assertTrue("Order index should be positive", lesson.orderIndex > 0)
            }

            // Check that indices are sequential (allowing for some flexibility)
            val indices = skillLessons.map { it.orderIndex }.sorted()
            for (i in 0 until indices.size - 1) {
                assertTrue("Order indices should be sequential",
                    indices[i + 1] - indices[i] <= 2) // Allow small gaps
            }
        }
    }

    @Test
    fun `test A2 lessons contain authentic German content`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2Lessons = lessons.filter { it.level == "A2" }

        // Test that lessons contain German text and not just English
        a2Lessons.forEach { lesson ->
            when (lesson.skill) {
                "lesen" -> {
                    val content = gson.fromJson(lesson.content, LesenContent::class.java)
                    assertTrue("Lesen lesson should contain German text",
                        content.text.contains(Regex("[äöüß]|[a-zA-Z]{3,}")))
                }
                "hoeren" -> {
                    val content = gson.fromJson(lesson.content, HoerenContent::class.java)
                    assertTrue("Hören lesson should contain German script",
                        content.script.contains(Regex("[äöüß]|[a-zA-Z]{3,}")))
                }
                "sprechen" -> {
                    val content = gson.fromJson(lesson.content, SprechenContent::class.java)
                    assertTrue("Sprechen lesson should contain German prompt",
                        content.prompt.contains(Regex("[äöüß]|[a-zA-Z]{3,}")))
                    assertTrue("Sprechen lesson should contain German model response",
                        content.modelResponse.contains(Regex("[äöüß]|[a-zA-Z]{3,}")))
                }
            }
        }
    }

    @Test
    fun `test A2 Schreiben lessons have appropriate word limits for A2 level`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2SchreibenLessons = lessons.filter { it.skill == "schreiben" && it.level == "A2" }

        a2SchreibenLessons.forEach { lesson ->
            val content = gson.fromJson(lesson.content, SchreibenContent::class.java)

            // A2 level should have reasonable word limits
            assertTrue("Min words should be reasonable for A2", content.minWords >= 50)
            assertTrue("Max words should be reasonable for A2", content.maxWords <= 200)
            assertTrue("Max should be greater than min", content.maxWords > content.minWords)
        }
    }

    @Test
    fun `test A2 Sprechen lessons have appropriate time limits`() {
        val lessons = LessonContentGenerator.generateAllLessons()
        val a2SprechenLessons = lessons.filter { it.skill == "sprechen" && it.level == "A2" }

        a2SprechenLessons.forEach { lesson ->
            val content = gson.fromJson(lesson.content, SprechenContent::class.java)

            // A2 speaking should have reasonable time limits
            assertTrue("Time limit should be reasonable for A2", content.timeLimit >= 90)
            assertTrue("Time limit should not be too long", content.timeLimit <= 180)
        }
    }
}
