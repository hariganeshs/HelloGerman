// Debug test to understand the mutter issue
suspend fun debugMutterSearch() {
    val context = getApplicationContext()
    
    // Initialize the English to German reader
    val enReader = FreedictReader.buildEnglishToGerman(context)
    enReader.initializeIfNeeded()
    
    println("=== DEBUG: Searching for 'mutter' ===")
    
    // Test 1: Direct lookup of English word "mutter"
    val directMutter = enReader.lookupExact("mutter")
    println("Direct lookup of 'mutter': ${directMutter?.headword}")
    println("Translations: ${directMutter?.translations}")
    
    // Test 2: Reverse lookup for German word "mutter"
    val reverseMutter = enReader.lookupByGermanWord("mutter")
    println("Reverse lookup for 'mutter': ${reverseMutter?.headword}")
    println("Translations: ${reverseMutter?.translations}")
    
    // Test 3: Reverse lookup for German word "Mutter" (capitalized)
    val reverseMutterCapital = enReader.lookupByGermanWord("Mutter")
    println("Reverse lookup for 'Mutter': ${reverseMutterCapital?.headword}")
    println("Translations: ${reverseMutterCapital?.translations}")
    
    // Test 4: Let's see what entries contain "mutter" in their translations
    println("\n=== Scanning all entries for 'mutter' in translations ===")
    var count = 0
    for ((head, idx) in enReader.getIndexForTesting()) {
        if (count++ > 1000) break // Limit scan for testing
        val entry = enReader.buildEntryFromIndexForTesting(head, idx)
        if (entry.translations.any { it.contains("mutter", ignoreCase = true) }) {
            println("Found: ${entry.headword} -> ${entry.translations}")
        }
    }
}