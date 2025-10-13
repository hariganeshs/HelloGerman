import com.hellogerman.app.data.repository.OfflineDictionaryRepository
import com.hellogerman.app.data.model.DictionarySearchResult

fun main() {
    val repo = OfflineDictionaryRepository()
    
    println("Testing 'mutter' search fix...")
    
    // Test 1: Search for lowercase "mutter" (should find German "Mutter" - mother)
    println("\n1. Searching for 'mutter' (lowercase):")
    val result1 = repo.searchOfflineFreedict("mutter", "de", "en")
    println("   Original word: ${result1.originalWord}")
    println("   From language: ${result1.fromLanguage}")
    println("   To language: ${result1.toLanguage}")
    println("   Has results: ${result1.hasResults}")
    println("   Translations: ${result1.translations}")
    println("   Gender: ${result1.gender}")
    println("   Word type: ${result1.wordType}")
    
    // Test 2: Search for capitalized "Mutter" (should find German "Mutter" - mother)
    println("\n2. Searching for 'Mutter' (capitalized):")
    val result2 = repo.searchOfflineFreedict("Mutter", "de", "en")
    println("   Original word: ${result2.originalWord}")
    println("   From language: ${result2.fromLanguage}")
    println("   To language: ${result2.toLanguage}")
    println("   Has results: ${result2.hasResults}")
    println("   Translations: ${result2.translations}")
    println("   Gender: ${result2.gender}")
    println("   Word type: ${result2.wordType}")
    
    // Test 3: Search for English "mutter" (should find English verb "mutter" - to murmur)
    println("\n3. Searching for 'mutter' as English word:")
    val result3 = repo.searchOfflineFreedict("mutter", "en", "de")
    println("   Original word: ${result3.originalWord}")
    println("   From language: ${result3.fromLanguage}")
    println("   To language: ${result3.toLanguage}")
    println("   Has results: ${result3.hasResults}")
    println("   Translations: ${result3.translations}")
    println("   Gender: ${result3.gender}")
    println("   Word type: ${result3.wordType}")
    
    println("\nTest completed!")
}