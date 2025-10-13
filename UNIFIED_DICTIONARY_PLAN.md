# Unified Dictionary System Plan

## Overview
This document outlines the new simplified dictionary system that replaces all previous dictionary implementations. The system uses only the FreeDict eng-deu dictionary file as the primary source and Wiktionary API for enhanced data.

## Key Requirements
- **Primary Source**: `freedict-eng-deu-1.9-fd1.dictd` (kept as the only dictionary file)
- **Secondary Source**: Wiktionary API for pronunciation, examples, and additional word data
- **German Priority**: All German translations must display with proper noun gender (e.g., "die Mutter")
- **No English Articles**: Never display "die mother" or "der mother" - only German words with German articles

## Architecture

### Data Sources
1. **FreeDict Parser** (`FreeDictParser.kt`)
   - Parses `freedict-eng-deu-1.9-fd1.dictd` file
   - Extracts German translations with proper gender detection
   - Provides basic word-to-translation mapping

2. **Wiktionary API Integration** (`WiktionaryParser.kt`)
   - Fetches pronunciation data (IPA, audio URLs)
   - Retrieves example sentences
   - Provides additional grammar information
   - Enhanced word type and conjugation data

### Core Components

#### 1. SimplifiedDictionaryRepository.kt
- Main repository handling search logic
- Combines FreeDict and Wiktionary data
- Prioritizes German translations with correct gender
- Manages caching and data merging

#### 2. SimplifiedDictionaryViewModel.kt
- Manages UI state for dictionary searches
- Handles search debouncing
- Manages loading/error states
- Provides enhanced German word data fetching

#### 3. SimplifiedDictionaryScreen.kt
- Main UI for dictionary searches
- Displays German translations with gender chips
- Shows pronunciation and examples
- Clean, focused interface without clutter

### Data Flow
1. User enters search query
2. Language detection (German vs English)
3. FreeDict lookup for basic translations
4. Wiktionary API call for enhanced data
5. Data merging with German priority
6. UI display with proper gender formatting

### Key Features

#### German Gender Display
- **der** (masculine) - Blue chip
- **die** (feminine) - Red chip  
- **das** (neuter) - Purple chip
- Gender automatically detected from FreeDict data

#### Pronunciation Support
- IPA notation display
- Audio playback when available
- Enhanced data fetching for German words

#### Example Sentences
- Limited to 3 examples per word
- Shows both German and English
- Source attribution

## Implementation Status

### Completed ✅
- [x] FreeDict parser implementation
- [x] Wiktionary API integration
- [x] Simplified repository with German priority
- [x] ViewModel with search management
- [x] UI with proper gender display
- [x] Old dictionary plans removed

### Integration Steps
1. Replace existing dictionary screen in navigation
2. Update dependency injection to use new repository
3. Remove old dictionary-related database files
4. Update app configuration to use new system

## Usage Examples

### Search "mother"
**Result**: "die Mutter" (not "die mother")
- Shows gender chip: [die] Mutter
- Pronunciation: /ˈmʊtɐ/
- Examples: "Die Mutter kocht." (The mother cooks.)

### Search "Haus"
**Result**: "house"
- Shows German word "Haus" as source
- English translation: "house"
- Pronunciation: /haʊ̯s/

## Technical Notes

### Data Merging Strategy
1. FreeDict provides base translations
2. Wiktionary enhances with pronunciation/examples
3. German words always show German articles
4. English words show German translations with articles

### Performance Considerations
- Search debouncing (300ms)
- In-memory caching of FreeDict data
- Network timeout handling (5 seconds)
- Fallback to FreeDict only if Wiktionary fails

### Error Handling
- Graceful degradation to FreeDict-only data
- User-friendly error messages
- Retry mechanisms for network failures
- Offline mode using FreeDict data only

## Future Enhancements
- Favorites/bookmark system
- Search history
- Offline mode improvements
- Additional language pairs (if needed)
- User contribution system for missing words

## Migration Notes
This system completely replaces all previous dictionary implementations. The old `DictionaryRepository.kt` and related components should be removed once the new system is fully integrated and tested.