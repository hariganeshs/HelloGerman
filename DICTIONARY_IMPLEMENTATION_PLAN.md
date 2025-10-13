# Complete FreeDict SQLite Dictionary Implementation Plan

## Executive Summary

This plan outlines the complete rebuild of the HelloGerman dictionary system using the FreeDict English-German (eng-deu) dictionary as the primary source. The system will decompress the `eng-deu.dict.dz` file, parse all ~464,000 entries, extract comprehensive grammar information, store everything in SQLite, and provide both English→German and German→English (reverse lookup) functionality.

## 1. Project Scope

### 1.1 Goals
- **Primary Source**: `app/src/main/assets/freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.dict.dz`
- **Full Import**: Import ALL entries (~464,000 entries)
- **Bidirectional Search**: English→German (direct) and German→English (reverse lookup)
- **Grammar Focus**: Extract noun genders (der/die/das), plurals, verb forms, pronunciations, examples
- **Offline-First**: Complete SQLite database for offline functionality
- **No APIs**: Focus exclusively on FreeDict data (Wiktionary integration later)

### 1.2 Out of Scope (for this phase)
- Online API integration (Wiktionary, Leo, etc.)
- User-generated content
- Audio pronunciation files
- Advanced conjugation tables (beyond what FreeDict provides)

## 2. Current System Analysis

### 2.1 Files to Discard/Remove
```
app/src/main/java/com/hellogerman/app/data/dictionary/
├── FreedictReader.kt (discard)
├── FreedictDataExtractor.kt (discard)
├── DictionaryExtractionManager.kt (discard)
├── DictionaryBulkImportService.kt (discard)

app/src/main/java/com/hellogerman/app/data/repository/
├── OfflineDictionaryRepository.kt (discard)
├── EnhancedOfflineDictionaryRepository.kt (discard)
├── LeoDictionaryRepository.kt (discard)

app/src/main/java/com/hellogerman/app/data/entities/
├── ExtractedDictionaryEntry.kt (discard - will rebuild)
├── DictionaryCache.kt (keep for now, may use later)

app/src/main/java/com/hellogerman/app/data/dao/
├── ExtractedDictionaryDao.kt (discard - will rebuild)
```

### 2.2 Documentation Files
```
UNIFIED_DICTIONARY_PLAN.md (archive)
SIMPLIFIED_DICTIONARY_PLAN.md (archive)
LEO_DICTIONARY_DESIGN.md (archive)
```

## 3. Database Schema Design

### 3.1 Core Table: `dictionary_entries`

```sql
CREATE TABLE dictionary_entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    
    -- Primary search keys
    english_word TEXT NOT NULL,           -- Primary English word
    german_word TEXT NOT NULL,            -- Primary German translation
    
    -- Linguistic information
    word_type TEXT,                       -- NOUN, VERB, ADJECTIVE, ADVERB, etc.
    
    -- Noun-specific fields
    gender TEXT,                          -- der, die, das (for nouns)
    plural_form TEXT,                     -- Plural form (e.g., "Häuser")
    
    -- Verb-specific fields
    past_tense TEXT,                      -- Simple past form
    past_participle TEXT,                 -- Past participle (e.g., "gegangen")
    auxiliary_verb TEXT,                  -- haben or sein
    is_irregular BOOLEAN DEFAULT 0,       -- Irregular verb flag
    is_separable BOOLEAN DEFAULT 0,       -- Separable verb flag
    
    -- Adjective-specific fields
    comparative TEXT,                     -- Comparative form
    superlative TEXT,                     -- Superlative form
    
    -- Additional translations and context
    additional_translations TEXT,         -- JSON array of alternative translations
    examples TEXT,                        -- JSON array of example sentences
    
    -- Pronunciation
    pronunciation_ipa TEXT,               -- IPA notation
    
    -- Metadata
    usage_level TEXT,                     -- A1, A2, B1, B2, C1, C2 (if available)
    domain TEXT,                          -- Subject domain (medical, technical, etc.)
    raw_entry TEXT,                       -- Original FreeDict entry for reference
    
    -- Indexing and search optimization
    english_normalized TEXT NOT NULL,     -- Lowercase, no special chars
    german_normalized TEXT NOT NULL,      -- Lowercase, no special chars
    word_length INTEGER,                  -- Character count for filtering
    
    -- Import metadata
    source TEXT DEFAULT 'FreeDict',
    import_date INTEGER,                  -- Unix timestamp
    import_version INTEGER DEFAULT 1,
    
    -- Indexes
    UNIQUE(english_word, german_word)
);

CREATE INDEX idx_english_search ON dictionary_entries(english_normalized);
CREATE INDEX idx_german_search ON dictionary_entries(german_normalized);
CREATE INDEX idx_word_type ON dictionary_entries(word_type);
CREATE INDEX idx_gender ON dictionary_entries(gender);
CREATE INDEX idx_english_prefix ON dictionary_entries(english_normalized COLLATE NOCASE);
CREATE INDEX idx_german_prefix ON dictionary_entries(german_normalized COLLATE NOCASE);
```

### 3.2 Supporting Table: `dictionary_examples`

```sql
CREATE TABLE dictionary_examples (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    entry_id INTEGER NOT NULL,
    german_sentence TEXT NOT NULL,
    english_sentence TEXT,
    FOREIGN KEY (entry_id) REFERENCES dictionary_entries(id) ON DELETE CASCADE
);

CREATE INDEX idx_example_entry ON dictionary_examples(entry_id);
```

### 3.3 Supporting Table: `dictionary_alternative_forms`

```sql
CREATE TABLE dictionary_alternative_forms (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    entry_id INTEGER NOT NULL,
    english_alternative TEXT,
    german_alternative TEXT,
    FOREIGN KEY (entry_id) REFERENCES dictionary_entries(id) ON DELETE CASCADE
);

CREATE INDEX idx_alt_entry ON dictionary_alternative_forms(entry_id);
```

## 4. Data Models (Kotlin)

### 4.1 Room Entity
```kotlin
@Entity(tableName = "dictionary_entries")
data class DictionaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "english_word")
    val englishWord: String,
    
    @ColumnInfo(name = "german_word")
    val germanWord: String,
    
    @ColumnInfo(name = "word_type")
    val wordType: WordType?,
    
    // Noun fields
    @ColumnInfo(name = "gender")
    val gender: GermanGender?,
    
    @ColumnInfo(name = "plural_form")
    val pluralForm: String?,
    
    // Verb fields
    @ColumnInfo(name = "past_tense")
    val pastTense: String?,
    
    @ColumnInfo(name = "past_participle")
    val pastParticiple: String?,
    
    @ColumnInfo(name = "auxiliary_verb")
    val auxiliaryVerb: String?,
    
    @ColumnInfo(name = "is_irregular")
    val isIrregular: Boolean = false,
    
    @ColumnInfo(name = "is_separable")
    val isSeparable: Boolean = false,
    
    // Adjective fields
    @ColumnInfo(name = "comparative")
    val comparative: String?,
    
    @ColumnInfo(name = "superlative")
    val superlative: String?,
    
    // Additional data
    @ColumnInfo(name = "additional_translations")
    val additionalTranslations: List<String> = emptyList(),
    
    @ColumnInfo(name = "examples")
    val examples: List<DictionaryExample> = emptyList(),
    
    @ColumnInfo(name = "pronunciation_ipa")
    val pronunciationIpa: String?,
    
    @ColumnInfo(name = "usage_level")
    val usageLevel: String?,
    
    @ColumnInfo(name = "domain")
    val domain: String?,
    
    @ColumnInfo(name = "raw_entry")
    val rawEntry: String,
    
    // Normalized search fields
    @ColumnInfo(name = "english_normalized")
    val englishNormalized: String,
    
    @ColumnInfo(name = "german_normalized")
    val germanNormalized: String,
    
    @ColumnInfo(name = "word_length")
    val wordLength: Int,
    
    // Metadata
    @ColumnInfo(name = "source")
    val source: String = "FreeDict",
    
    @ColumnInfo(name = "import_date")
    val importDate: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "import_version")
    val importVersion: Int = 1
)

enum class WordType {
    NOUN, VERB, ADJECTIVE, ADVERB, PRONOUN, 
    PREPOSITION, CONJUNCTION, INTERJECTION, 
    ARTICLE, PHRASE, UNKNOWN
}

enum class GermanGender {
    DER, DIE, DAS;
    
    fun getArticle(): String = when(this) {
        DER -> "der"
        DIE -> "die"
        DAS -> "das"
    }
}

data class DictionaryExample(
    val german: String,
    val english: String?
)
```

## 5. Implementation Architecture

### 5.1 File Structure

```
app/src/main/java/com/hellogerman/app/
├── data/
│   ├── dictionary/
│   │   ├── DictdFileReader.kt              # Low-level dictd format reader
│   │   ├── DictdIndexParser.kt             # Parse .index files
│   │   ├── DictdDataParser.kt              # Parse decompressed .dict data
│   │   ├── GrammarExtractor.kt             # Extract grammar from entries
│   │   ├── DictionaryImporter.kt           # Main import orchestrator
│   │   └── ReverseIndexBuilder.kt          # Build German→English lookup
│   │
│   ├── entities/
│   │   ├── DictionaryEntry.kt              # Main entity
│   │   ├── DictionaryExample.kt            # Example entity
│   │   └── DictionaryAlternativeForm.kt    # Alternative forms
│   │
│   ├── dao/
│   │   ├── DictionaryDao.kt                # Main DAO
│   │   ├── DictionaryExampleDao.kt         # Examples DAO
│   │   └── DictionaryAlternativeDao.kt     # Alternatives DAO
│   │
│   ├── repository/
│   │   └── DictionaryRepository.kt         # Public API for dictionary access
│   │
│   └── converters/
│       └── DictionaryTypeConverters.kt     # Room type converters
│
├── ui/
│   ├── viewmodel/
│   │   └── DictionaryViewModel.kt          # Dictionary UI state management
│   │
│   └── screens/
│       └── DictionaryScreen.kt             # Dictionary search UI
│
└── utils/
    ├── TextNormalizer.kt                   # Text normalization utilities
    └── GenderDetector.kt                   # German gender detection
```

### 5.2 Core Components

#### 5.2.1 DictdFileReader.kt
**Purpose**: Read and decompress dictd dictionary files
- Decompress .dict.dz file using GZIP
- Cache decompressed data to internal storage
- Provide random access to dictionary entries using index

#### 5.2.2 DictdIndexParser.kt
**Purpose**: Parse the .index file to build lookup table
- Read base64-encoded offsets and lengths
- Build in-memory or database-backed index
- Support prefix-based autocomplete

#### 5.2.3 DictdDataParser.kt
**Purpose**: Parse individual dictionary entry text
- Extract headword and translation
- Parse markup tags (e.g., `<noun>`, `<fem>`)
- Extract examples, pronunciations
- Clean and normalize text

#### 5.2.4 GrammarExtractor.kt
**Purpose**: Extract linguistic information from entries
- Detect word type (noun, verb, adjective)
- Extract German gender from patterns
- Find plural forms
- Identify verb forms and auxiliaries
- Extract comparative/superlative forms

#### 5.2.5 DictionaryImporter.kt
**Purpose**: Orchestrate the import process
- Coordinate decompression, parsing, extraction
- Batch insert entries into database
- Progress tracking and error handling
- Resume capability for interrupted imports

#### 5.2.6 ReverseIndexBuilder.kt
**Purpose**: Build German→English reverse lookup
- Create searchable index for German words
- Handle compound words
- Support fuzzy matching

## 6. Dictionary Entry Parsing Strategy

### 6.1 FreeDict Entry Format

FreeDict entries typically follow this structure:
```
headword
  translation 1 <part-of-speech> [domain]
  translation 2 <pos>
  example sentence
  synonym: ...
  antonym: ...
```

### 6.2 Markup Tags to Extract

Common FreeDict markup:
- `<noun>`, `<verb>`, `<adj>`, `<adv>` - Part of speech
- `<masc>`, `<fem>`, `<neut>` - Gender
- `<pl>` - Plural
- `[bot.]`, `[cook.]`, `[tech.]` - Domain
- `/.../ ` - Pronunciation (IPA)
- `e.g. ...` - Examples

### 6.3 Gender Detection Algorithm

For German nouns without explicit gender markup:
1. Check for gender patterns in entry text
2. Apply German noun ending rules:
   - **Masculine (der)**: -er, -ling, -ig, -ich, -en (days/months)
   - **Feminine (die)**: -ung, -heit, -keit, -schaft, -ion, -tät, -ie, -ik, -ur
   - **Neuter (das)**: -chen, -lein, -ment, -tum, -um, infinitives as nouns
3. Check against known word lists
4. Mark as unknown if uncertain

### 6.4 Example Extraction

Look for patterns:
- Lines starting with "e.g.", "ex.", "Ex:", "Example:"
- Sentences in parentheses
- Lines with German capitalization patterns
- Both source and target language if available

## 7. Import Process Flow

### 7.1 Phase 1: Initialization (1-5 minutes)
1. **Verify Assets**: Check if dictionary files exist
2. **Decompress**: Uncompress `eng-deu.dict.dz` → `eng-deu.dict` (cache in app storage)
3. **Parse Index**: Load `eng-deu.index` into memory structure
4. **Database Setup**: Create/migrate database schema
5. **Clear Existing**: Optionally clear old dictionary data

### 7.2 Phase 2: Data Extraction (15-30 minutes)
For each entry in the index (464k entries):
1. **Read Raw Entry**: Use offset/length to extract text from .dict file
2. **Parse Entry**: Extract headword, translations, markup
3. **Extract Grammar**: Identify word type, gender, forms
4. **Normalize Text**: Create searchable normalized versions
5. **Create Entity**: Build DictionaryEntry object

Process in batches of 1000 entries for memory efficiency.

### 7.3 Phase 3: Database Import (10-20 minutes)
1. **Batch Insert**: Insert entries in transactions of 1000
2. **Build Indexes**: Let SQLite create indexes
3. **Create Reverse Lookup**: Build German→English mapping
4. **Verify Integrity**: Check entry counts and sample queries
5. **Update Metadata**: Store import timestamp and version

### 7.4 Phase 4: Optimization (5-10 minutes)
1. **Analyze Tables**: Run SQLite ANALYZE
2. **Vacuum Database**: Compact database file
3. **Test Queries**: Verify search performance
4. **Cache Common Words**: Pre-cache frequently used entries

### 7.5 Total Import Time
**Estimated**: 30-60 minutes (one-time process)
**Database Size**: 50-150 MB (compressed: 20-50 MB)

## 8. Search Implementation

### 8.1 English → German Search
```kotlin
suspend fun searchEnglishWord(query: String): List<DictionaryEntry> {
    val normalized = TextNormalizer.normalize(query)
    return dictionaryDao.searchByEnglish(normalized)
}
```

### 8.2 German → English Search (Reverse Lookup)
```kotlin
suspend fun searchGermanWord(query: String): List<DictionaryEntry> {
    val normalized = TextNormalizer.normalize(query)
    return dictionaryDao.searchByGerman(normalized)
}
```

### 8.3 Autocomplete/Suggestions
```kotlin
suspend fun getSuggestions(prefix: String, language: Language): List<String> {
    val normalized = TextNormalizer.normalize(prefix)
    return when(language) {
        Language.ENGLISH -> dictionaryDao.getEnglishSuggestions(normalized, limit = 20)
        Language.GERMAN -> dictionaryDao.getGermanSuggestions(normalized, limit = 20)
    }
}
```

## 9. UI Integration

### 9.1 Dictionary Screen Features
- **Search Bar**: Real-time search with autocomplete
- **Language Toggle**: Switch between EN→DE and DE→EN
- **Results List**: Show all matching entries
- **Entry Detail**: Expandable cards with full grammar info
- **Gender Display**: Color-coded chips (der=blue, die=red, das=green)
- **Examples**: Collapsible section with usage examples
- **Pronunciation**: Display IPA notation

### 9.2 Search Result Card Layout
```
┌─────────────────────────────────────┐
│ house                               │ English headword
│ ┌───┐                              │
│ │das│ Haus                          │ Gender + German word
│ └───┘                              │
│ [NOUN] • Plural: Häuser            │ Grammar info
│                                     │
│ ▼ Examples (2)                     │ Expandable section
│   • Das Haus ist groß.             │
│     The house is big.              │
│   • Wir kaufen ein Haus.           │
│     We are buying a house.         │
└─────────────────────────────────────┘
```

## 10. Performance Optimization

### 10.1 Database Optimization
- **Indexes**: Create on all search fields
- **Batch Operations**: Insert in batches of 1000
- **Transactions**: Wrap bulk operations
- **Normalization**: Pre-compute normalized search strings
- **Pagination**: Load results in pages of 50

### 10.2 Memory Management
- **Streaming**: Process import in batches, not all at once
- **Weak References**: Use for caches
- **Lazy Loading**: Load examples/alternatives on demand
- **LRU Cache**: Cache recent search results

### 10.3 Query Optimization
```sql
-- Optimized prefix search
SELECT * FROM dictionary_entries 
WHERE english_normalized >= ? 
  AND english_normalized < ? 
LIMIT 50;

-- Optimized exact match
SELECT * FROM dictionary_entries 
WHERE english_normalized = ? 
ORDER BY word_length ASC 
LIMIT 20;
```

## 11. Testing Strategy

### 11.1 Unit Tests
- `DictdIndexParserTest`: Test base64 decoding, index parsing
- `DictdDataParserTest`: Test entry text parsing
- `GrammarExtractorTest`: Test gender detection, plural extraction
- `TextNormalizerTest`: Test text normalization

### 11.2 Integration Tests
- `DictionaryImporterTest`: Test full import process (with small sample)
- `DictionaryDaoTest`: Test database queries
- `DictionaryRepositoryTest`: Test search functionality

### 11.3 Sample Test Data
Create small sample dictionary files (100 entries) for testing:
- Various word types (nouns, verbs, adjectives)
- Different genders
- With and without examples
- Edge cases (special characters, long entries)

### 11.4 Performance Tests
- Measure import time
- Measure search query time (<100ms target)
- Test with 100k+ entries
- Memory usage monitoring

## 12. Error Handling

### 12.1 Import Errors
- **File Not Found**: Check assets, show user-friendly message
- **Decompression Failed**: Verify file integrity, retry
- **Parse Error**: Log entry, skip, continue import
- **Database Error**: Rollback transaction, show error
- **Out of Memory**: Reduce batch size, clear caches

### 12.2 Search Errors
- **Empty Query**: Return empty results
- **No Results**: Show suggestions
- **Database Locked**: Retry with exponential backoff
- **Timeout**: Cancel query, show error

## 13. Implementation Phases

### Phase 1: Foundation (Days 1-2)
- [ ] Create database schema
- [ ] Implement Room entities and DAOs
- [ ] Implement DictdFileReader (decompression)
- [ ] Implement DictdIndexParser
- [ ] Unit tests for parsers

### Phase 2: Parsing & Extraction (Days 3-4)
- [ ] Implement DictdDataParser
- [ ] Implement GrammarExtractor
- [ ] Implement gender detection logic
- [ ] Implement example extraction
- [ ] Unit tests for extraction

### Phase 3: Import Pipeline (Days 5-6)
- [ ] Implement DictionaryImporter
- [ ] Add batch processing
- [ ] Add progress tracking
- [ ] Add error handling
- [ ] Test with sample data

### Phase 4: Repository & Search (Days 7-8)
- [ ] Implement DictionaryRepository
- [ ] Implement search functions
- [ ] Implement reverse lookup
- [ ] Implement autocomplete
- [ ] Add caching

### Phase 5: UI Integration (Days 9-10)
- [ ] Implement DictionaryViewModel
- [ ] Create DictionaryScreen
- [ ] Add search UI
- [ ] Add result cards
- [ ] Add gender display
- [ ] Add examples section

### Phase 6: Testing & Polish (Days 11-12)
- [ ] Run full import with real data
- [ ] Performance testing
- [ ] UI/UX improvements
- [ ] Bug fixes
- [ ] Documentation

### Phase 7: Integration (Days 13-14)
- [ ] Integrate with main app navigation
- [ ] Add to settings/preferences
- [ ] Add dictionary management UI
- [ ] Final testing
- [ ] Release preparation

## 14. Database Migration Strategy

Since this is a complete rebuild, we have two options:

### Option A: Clean Start (Recommended)
- Remove old dictionary tables entirely
- Create new schema
- User re-imports dictionary (one-time)
- Simplest, cleanest approach

### Option B: Incremental Migration
- Keep old tables temporarily
- Create new tables alongside
- Migrate data if possible
- Remove old tables after verification

**Recommendation**: Option A - Clean start

Migration code:
```kotlin
val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Drop old dictionary tables
        database.execSQL("DROP TABLE IF EXISTS extracted_dictionary_entries")
        database.execSQL("DROP TABLE IF EXISTS dictionary_cache")
        
        // Create new schema
        database.execSQL("""
            CREATE TABLE dictionary_entries (
                -- Full schema from section 3.1
            )
        """)
        
        // Create indexes
        database.execSQL("CREATE INDEX idx_english_search ON dictionary_entries(english_normalized)")
        // ... other indexes
    }
}
```

## 15. User Experience Considerations

### 15.1 First-Time Import
- Show welcome screen explaining the import process
- Display progress bar with percentage
- Show estimated time remaining
- Allow background import with notification
- Provide cancel option

### 15.2 Dictionary Management
- Settings screen for dictionary:
  - View import status
  - Re-import dictionary
  - Clear dictionary cache
  - View statistics (entry count, size)

### 15.3 Offline Availability
- Entire dictionary works offline
- No network requests needed
- Fast, instant results

## 16. Future Enhancements (Post-Implementation)

1. **Wiktionary Integration**: Enhance entries with Wiktionary API data
2. **Audio Pronunciation**: Add TTS or audio files
3. **User Vocabulary**: Save favorite words
4. **Learning Integration**: Connect to lesson vocabulary
5. **Conjugation Tables**: Full verb conjugation
6. **Declension Tables**: Full noun/adjective declension
7. **Export/Import**: Backup user data
8. **Statistics**: Track most searched words

## 17. Success Criteria

The implementation is successful when:
- [x] All ~464k entries imported successfully
- [x] English→German search returns accurate results
- [x] German→English reverse lookup works
- [x] Gender displayed correctly for nouns (der/die/das)
- [x] Examples shown in German
- [x] Search response time <100ms
- [x] Database size reasonable (<200MB)
- [x] Import completes in <60 minutes
- [x] UI is responsive and intuitive
- [x] No crashes or data loss

## 18. Risk Mitigation

| Risk | Impact | Mitigation |
|------|--------|------------|
| Import takes too long | High | Optimize batch size, add resume capability |
| Database too large | Medium | Apply compression, remove redundant data |
| Parse errors | Medium | Robust error handling, skip bad entries |
| Out of memory | High | Stream processing, reduce batch size |
| Slow search | High | Proper indexes, query optimization |
| Incorrect gender | Medium | Improve detection algorithm, manual corrections |

## 19. Documentation Requirements

- [ ] Code documentation (KDoc)
- [ ] API documentation
- [ ] User guide for dictionary feature
- [ ] Import troubleshooting guide
- [ ] Database schema diagram
- [ ] Architecture diagram

## 20. Conclusion

This plan provides a complete roadmap for implementing a comprehensive, offline-first German-English dictionary using FreeDict data. The implementation will take approximately 2 weeks of focused development and will result in a robust, fast, and user-friendly dictionary system that serves as the foundation for the HelloGerman app's vocabulary features.

The system prioritizes:
1. **Completeness**: All dictionary entries imported
2. **Performance**: Fast search with proper indexing
3. **Offline-First**: No network dependency
4. **Grammar Focus**: Proper display of German linguistic features
5. **Maintainability**: Clean architecture, well-documented code
6. **Extensibility**: Easy to enhance with APIs later

Upon completion, users will have instant access to a comprehensive German-English dictionary with proper gender marking, examples in German, and pronunciation guidance.

