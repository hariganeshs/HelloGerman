# Simplified Dictionary System: FreeDict + Wiktionary API

## Overview
Rebuild the dictionary system from scratch using only:
- **FreeDict eng-deu dictionary file** (`freedict-eng-deu-1.9-fd1.dictd`) as primary source
- **Wiktionary API** for comprehensive word data (pronunciation, examples, grammar)
- **No other databases** - complete removal of existing dictionary systems

## Core Requirements

### 1. German Translation Display
- **Correct format**: "die Mutter" (not "die mother")
- **Proper noun gender**: Always show German article (der/die/das) with German word
- **No English words in German translations**

### 2. Data Sources Priority
1. **FreeDict eng-deu** - Primary translations
2. **Wiktionary API** - Enhanced data (pronunciation, examples, grammar)
3. **No offline database** - Single file approach

### 3. Architecture Components

#### FreeDict Parser
- Parse `freedict-eng-deu-1.9-fd1.dictd` file format
- Extract English → German translations
- Handle noun gender extraction from German words
- Support for multiple translations per word

#### Wiktionary Integration
- German Wiktionary API for German words
- English Wiktionary API for English words (when needed)
- Extract: pronunciation, examples, synonyms, grammar
- Merge with FreeDict data

#### Translation Display Logic
- Always show German article + German word
- Extract gender from German word patterns
- Handle compound words and variations

## Implementation Steps

### Phase 1: Foundation
1. Remove all existing dictionary databases
2. Create FreeDict parser for eng-deu file
3. Implement basic translation lookup
4. Add German gender detection

### Phase 2: Enhanced Data
1. Integrate Wiktionary API
2. Add pronunciation data
3. Include example sentences
4. Add grammar information

### Phase 3: Polish
1. Optimize search performance
2. Add caching mechanism
3. Handle edge cases
4. Update UI components

## File Structure
```
app/src/main/java/com/hellogerman/app/data/dictionary/
├── FreeDictParser.kt          # Parse eng-deu dictionary file
├── FreeDictRepository.kt      # Main dictionary repository
├── WiktionaryIntegration.kt   # Wiktionary API integration
├── GermanGenderDetector.kt    # Extract gender from German words
└── DictionaryModels.kt        # Data models
```

## Key Features
- **Lightweight**: Single dictionary file + API
- **Accurate**: Proper German gender display
- **Comprehensive**: FreeDict + Wiktionary data
- **Fast**: Optimized parsing and caching
- **Reliable**: Fallback mechanisms

## Success Criteria
- ✅ German translations show correct gender (die Mutter)
- ✅ No English words in German translation display
- ✅ Fast search performance (< 1 second)
- ✅ Comprehensive word data from Wiktionary
- ✅ Clean, maintainable codebase