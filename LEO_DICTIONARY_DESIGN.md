# Leo-Dictionary System Design

## Overview
Redesign the dictionary system from scratch to create a comprehensive German-English dictionary similar to Leo-dictionary.org, focusing on:

1. **Noun gender display**: Show gender articles with German words (e.g., "der Mutter", "die Katze", "das Haus")
2. **German examples and use cases**: Provide contextual examples in German
3. **Complete grammar elements**: Include singular/plural forms, conjugations, declensions, etc.
4. **Free APIs only**: Use only Wiktionary and FreeDict databases

## Current System Analysis

### Existing Components
- **DictionaryRepository.kt**: Main repository with multiple APIs (English Dict, Wiktionary, Tatoeba, Reverso, OpenThesaurus, Wikidata, German Verb API)
- **GermanDictionary.kt**: Hardcoded English-German dictionary with basic gender info
- **FreeDictParser.kt & FreedictReader.kt**: FreeDict file parsers
- **WiktionaryApiService.kt**: Wiktionary API interface
- **Multiple repositories**: SimplifiedDictionaryRepository, UnifiedDictionaryRepository, OfflineDictionaryRepository

### Issues with Current System
- Too many APIs (not all free)
- Complex integration with inconsistent data
- Gender information not prominently displayed
- Limited grammar information
- Multiple overlapping repository classes

## New Leo-Dictionary Design

### Core Principles
1. **Simplicity**: Use only 2 data sources (FreeDict + Wiktionary)
2. **Completeness**: Extract all available grammar and usage information
3. **German-first**: Prioritize German language features (gender, cases, conjugations)
4. **Leo-style display**: Show gender articles with nouns, comprehensive grammar tables

### Data Sources

#### 1. FreeDict Database
- **Files**: deu-eng.dict.dz, deu-eng.index, eng-deu.dict.dz, eng-deu.index
- **Content**: German-English and English-German translations
- **Grammar info**: Gender markers (<masc>, <fem>, <neut>), plural forms, basic examples
- **Enhancement needed**: Better parsing of gender, examples, and grammar patterns

#### 2. Wiktionary API
- **API**: German Wiktionary (de.wiktionary.org)
- **Content**: Detailed grammar information, conjugations, declensions, IPA pronunciation
- **Usage**: Supplement FreeDict with comprehensive grammar data

### New Data Models

#### LeoDictionaryEntry
```kotlin
data class LeoDictionaryEntry(
    val germanWord: String,
    val englishTranslations: List<String>,
    val wordType: WordType, // NOUN, VERB, ADJECTIVE, etc.

    // Noun-specific (when wordType == NOUN)
    val gender: GermanGender?, // DER, DIE, DAS
    val article: String?, // "der", "die", "das"
    val plural: String?, // plural form
    val declension: NounDeclension?, // full declension table

    // Verb-specific (when wordType == VERB)
    val conjugation: VerbConjugation?, // full conjugation table
    val auxiliary: String?, // "haben" or "sein"
    val isIrregular: Boolean,

    // Adjective-specific (when wordType == ADJECTIVE)
    val comparative: String?,
    val superlative: String?,
    val declension: AdjectiveDeclension?,

    // Common fields
    val pronunciation: Pronunciation?,
    val examples: List<GermanExample>, // German sentences with usage
    val difficulty: String?, // A1, A2, B1, etc.
    val etymology: String?
)
```

#### GermanGender Enum
```kotlin
enum class GermanGender {
    DER, DIE, DAS;

    fun getArticle(): String = when(this) {
        DER -> "der"
        DIE -> "die"
        DAS -> "das"
    }
}
```

#### NounDeclension
```kotlin
data class NounDeclension(
    val nominative: CaseForms,
    val genitive: CaseForms,
    val dative: CaseForms,
    val accusative: CaseForms
)

data class CaseForms(
    val singular: String,
    val plural: String
)
```

#### VerbConjugation
```kotlin
data class VerbConjugation(
    val infinitive: String,
    val present: PersonForms,
    val past: PersonForms,
    val perfect: PersonForms,
    val future: PersonForms,
    val subjunctive: PersonForms?,
    val imperative: Map<String, String>, // informal/formal
    val participles: Participles
)

data class PersonForms(
    val ich: String,
    val du: String,
    val erSieEs: String,
    val wir: String,
    val ihr: String,
    val sieSie: String
)

data class Participles(
    val present: String,
    val past: String
)
```

#### GermanExample
```kotlin
data class GermanExample(
    val sentence: String, // German sentence
    val translation: String?, // English translation (optional)
    val context: String? // usage context
)
```

### New Repository Architecture

#### LeoDictionaryRepository
- **Single repository** replacing all existing dictionary repositories
- **Two data sources**: FreeDict + Wiktionary
- **Caching**: Room database for offline access
- **Methods**:
  - `searchGermanWord(word: String): LeoDictionaryEntry?`
  - `searchEnglishWord(word: String): List<LeoDictionaryEntry>`
  - `getWordWithGrammar(word: String): LeoDictionaryEntry?`

#### FreeDict Enhancements
- **Enhanced parsing**: Extract gender from <masc>/<fem>/<neut> tags
- **Plural extraction**: Parse plural forms from dictionary entries
- **Example extraction**: Find German example sentences in entries
- **Grammar hints**: Extract basic conjugation/declension info

#### Wiktionary Integration
- **Grammar extraction**: Parse wikitext for conjugation tables, declensions
- **Pronunciation**: Extract IPA from pronunciation sections
- **Examples**: Get additional German examples
- **Fallback**: Use when FreeDict lacks detailed grammar

### UI Display Features

#### Noun Display
```
der Hund (der Hunde)
  • der Hund, die Hunde
  • Examples:
    - Der Hund bellt laut.
    - Ich habe einen Hund.
```

#### Verb Display
```
sein (ist, war, ist gewesen) - irregular
  • Infinitive: sein
  • Present: ich bin, du bist, er ist...
  • Past: ich war, du warst, er war...
  • Perfect: ich bin gewesen, du bist gewesen...
```

#### Search Results
- Show gender article with German word
- Display word type and basic grammar
- Expandable sections for full conjugations/declensions
- German examples with optional English translations

### Implementation Plan

1. **Create new data models** in DictionaryModels.kt
2. **Build LeoDictionaryRepository** with FreeDict + Wiktionary integration
3. **Enhance FreeDictParser** for better grammar extraction
4. **Implement WiktionaryParser** for grammar table parsing
5. **Update database entities** for new data structure
6. **Remove old repositories** and unused APIs
7. **Update ViewModels** to use new repository
8. **Modify UI components** to display Leo-style information

### Migration Strategy

1. Keep existing database for backward compatibility
2. Add new tables for Leo dictionary data
3. Gradually migrate search functionality
4. Remove old code after testing new system

## Benefits

- **Simplified architecture**: One repository, two data sources
- **Comprehensive grammar**: Full declensions, conjugations, cases
- **Leo-style display**: Gender articles, grammar tables, examples
- **Free and open**: Only uses free APIs and databases
- **Better performance**: Less API calls, focused data extraction
- **Maintainable**: Single codebase instead of multiple repositories