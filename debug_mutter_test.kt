package com.hellogerman.app.data.dictionary

import android.content.Context

fun main() {
    println("=== DEBUG TEST FOR MUTTER ISSUE ===")
    
    // Create a mock context for testing
    val context = object : Context() {
        override fun getAssets() = object : android.content.res.AssetManager() {
            override fun open(fileName: String) = java.io.FileInputStream(
                "C:/Users/hyper/AndroidStudioProjects/HelloGerman/app/src/main/assets/$fileName"
            )
        }
    }
    
    val enReader = FreedictReader(context, "freedict-eng-deu-1.9-fd1.dictd/eng-deu", "eng-deu")
    
    println("\n1. Testing direct lookup for 'mutter':")
    val directResult = enReader.lookupExact("mutter")
    if (directResult != null) {
        println("Found entry: ${directResult.headword}")
        println("Translations: ${directResult.translations}")
        println("Gender: ${directResult.gender}")
    } else {
        println("No direct entry found")
    }
    
    println("\n2. Testing reverse lookup for 'mutter':")
    val reverseResult = enReader.lookupByGermanWord("mutter")
    if (reverseResult != null) {
        println("Found reverse entry: ${reverseResult.headword}")
        println("Translations: ${reverseResult.translations}")
        println("Gender: ${reverseResult.gender}")
        
        // Check if "mutter" appears in any translation
        val hasMutter = reverseResult.translations.any { 
            it.contains("mutter", ignoreCase = true) 
        }
        println("Contains 'mutter' in translations: $hasMutter")
    } else {
        println("No reverse entry found")
    }
    
    println("\n3. Testing reverse lookup for 'Mutter' (capitalized):")
    val capitalizedResult = enReader.lookupByGermanWord("Mutter")
    if (capitalizedResult != null) {
        println("Found reverse entry: ${capitalizedResult.headword}")
        println("Translations: ${capitalizedResult.translations}")
        println("Gender: ${capitalizedResult.gender}")
    } else {
        println("No reverse entry found")
    }
    
    println("\n4. Scanning all entries for 'mutter' in translations:")
    // This will be slow but we need to see what's happening
    val context2 = object : Context() {
        override fun getAssets() = object : android.content.res.AssetManager() {
            override fun open(fileName: String) = java.io.FileInputStream(
                "C:/Users/hyper/AndroidStudioProjects/HelloGerman/app/src/main/assets/$fileName"
            )
        }
    }
    
    val scanner = FreedictReader(context2, "freedict-eng-deu-1.9-fd1.dictd/eng-deu", "eng-deu")
    var count = 0
    var foundEntries = mutableListOf<String>()
    
    // Read a sample of entries to find ones containing "mutter"
    for (i in 0 until minOf(10000, scanner.size())) {
        try {
            val entry = scanner.getEntryAt(i)
            if (entry != null && entry.translations.any { 
                it.contains("mutter", ignoreCase = true) 
            }) {
                foundEntries.add("${entry.headword} -> ${entry.translations.filter { it.contains("mutter", ignoreCase = true) }}")
                count++
                if (count >= 5) break
            }
        } catch (e: Exception) {
            // Skip invalid entries
        }
    }
    
    println("Found $count entries containing 'mutter' in translations:")
    foundEntries.forEach { println("  $it") }
    
    println("\n=== END DEBUG TEST ===")
}

// Extension function to get entry by index (for debugging)
fun FreedictReader.getEntryAt(index: Int): FreedictReader.Entry? {
    if (!initialized) initializeIfNeeded()
    val keys = index.keys.toList()
    if (index >= keys.size) return null
    val key = keys[index]
    val idx = index[key] ?: return null
    return buildEntryFromIndex(key, idx)
}