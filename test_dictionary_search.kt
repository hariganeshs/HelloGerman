#!/usr/bin/env kotlin

/**
 * Test script to debug dictionary search issues
 * Run this to check if dictionary data exists and search is working
 */

import java.io.File

fun main() {
    println("=== Dictionary Search Debug Test ===")
    println()
    
    // Check if dictionary files exist
    val engDeuPath = "app/src/main/assets/freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.dict.dz"
    val deuEngPath = "app/src/main/assets/freedict-deu-eng-1.9-fd1.dictd/deu-eng/deu-eng.dict.dz"
    
    println("📁 Checking dictionary files...")
    println("English-German: ${if (File(engDeuPath).exists()) "✅ EXISTS" else "❌ MISSING"}")
    println("German-English: ${if (File(deuEngPath).exists()) "✅ EXISTS" else "❌ MISSING"}")
    println()
    
    // Check exported data
    println("📊 Checking exported data...")
    val exportDir = File("dictionary_exports")
    if (exportDir.exists()) {
        val files = exportDir.listFiles()?.map { it.name } ?: emptyList()
        println("Found ${files.size} exported files:")
        files.forEach { println("  • $it") }
    } else {
        println("❌ No exported data found")
    }
    println()
    
    // Test specific words that should work
    println("🔍 Testing specific words...")
    testWord("apple", "der Apfel")
    testWord("mother", "die Mutter")
    testWord("house", "das Haus")
    testWord("Apfel", "apple")
    testWord("Mutter", "mother")
    testWord("Haus", "house")
    println()
    
    println("=== Debug Complete ===")
}

fun testWord(input: String, expectedOutput: String) {
    println("Testing: '$input' should return '$expectedOutput'")
    // This would need to be run in the actual app context
    // For now, just document the expected behavior
}
