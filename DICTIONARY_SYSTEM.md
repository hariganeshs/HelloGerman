## HelloGerman Dictionary System (LEO-style) — Architecture, APIs, and Roadmap

This document explains the current dictionary functionality in the app, outlines gaps vs. a LEO-style experience, and proposes improvements. It also lists vetted free/open APIs and concrete HTTP requests you can use to fetch information about a word, with notes on reliability, licensing, and fallbacks.

### Scope and goals
- Provide a fast, offline-first German dictionary with rich entries: definitions, translations, examples, IPA/audio, gender, part of speech, conjugations, synonyms, and basic etymology.
- Aggregate data from free sources and cache results; present in a single LEO-like detail screen with actionable learning widgets (TTS, add-to-vocab, drill).

---

## 1) Current implementation overview

### Architecture (high-level flow)
- UI: `DictionaryScreen` (Jetpack Compose) shows a single scrollable page with subsections (overview, definitions, examples, conjugations, synonyms, translations, etymology).
- ViewModel: `DictionaryViewModel` orchestrates searches, manages language pair, TTS, history, and invokes the repository.
- Repositories:
  - `OfflineDictionaryRepository`: Room-backed offline database (`GermanDictionaryDatabase`) with 10k+ essential words, examples, gender, CEFR, IPA. Offline-first; falls back to online.
  - `DictionaryRepository`: Aggregates online sources in parallel, merges results, and provides structured `DictionarySearchResult` with definitions, examples, IPA/audio, synonyms, conjugations, etc.
- Parsing: `WiktionaryParser` extracts data from Wiktionary wikitext: definitions, examples, IPA/audio templates, gender, word type, etymology, synonyms.
- Models: `DictionaryModels.kt` contains unified result schema and API DTOs.
- TTS: `TTSHelper` plays German/English words and examples.

### Key classes and responsibilities
- `app/src/main/java/com/hellogerman/app/ui/screens/DictionaryScreen.kt`: LEO-style consolidated details UI.
- `app/src/main/java/com/hellogerman/app/ui/viewmodel/DictionaryViewModel.kt`: search pipeline, language swap, history, TTS integration.
- `app/src/main/java/com/hellogerman/app/data/repository/OfflineDictionaryRepository.kt`: offline-first lookup; compound-word analysis; DB bootstrap/reset.
- `app/src/main/java/com/hellogerman/app/data/database/GermanDictionaryDatabase.kt`: Room entities/DAO and compact converters.
- `app/src/main/java/com/hellogerman/app/data/repository/DictionaryRepository.kt`: parallel online fetch (Wiktionary, OpenThesaurus, Reverso, MyMemory, LibreTranslate, German Verb API) and result merging with offline fallback.
- `app/src/main/java/com/hellogerman/app/data/parser/WiktionaryParser.kt`: robust regex-based extraction from Wiktionary with fallbacks.

### Current online sources used
- Wiktionary (de/en) via MediaWiki action API (wikitext parsing)
- German Verb API (conjugations)
- OpenThesaurus (synonyms)
- Reverso Context (bilingual examples; unofficial endpoint)
- MyMemory (translations) and LibreTranslate (fallback translation)
- English Free Dictionary API (English-only fallback for EN words)

### Current offline sources
- `GermanDictionaryDatabase` (Room): essential words with definitions, examples, IPA, gender, CEFR, frequency.
- `GermanDictionary` (in-memory subset) used as additional fallback.

---

## 2) Gaps vs. a LEO-style experience

- Multi-source coverage is decent but can be extended and made more deterministic for grammar (noun declension tables; plural; gender disambiguation).
- Conjugations: present; could enrich with more moods/voices/auxiliaries and irregular forms.
- Declensions for nouns/adjectives/pronouns are not yet presented as tables.
- Examples: good if Reverso is available; need a sustainable, license-friendly source (e.g., Tatoeba) with bilingual pairs.
- Lemmatization/base forms for inflected input is basic; should improve for robust headword resolution (verbs, plural nouns, cases).
- Collocations/frequency: not surfaced; useful for learning priority and typical usage.
- Audio: IPA and Wikimedia audio extraction present; add robust audio discovery and fallback.
- Licensing and attribution: centralize source attributions and link-outs.
- UX: Add per-sense grouping, clickable gender/article chips, plural, separable prefix highlight, quick-add to spaced repetition.

---

## 3) Free/open APIs and concrete HTTP requests

Below are reliable, free-to-use endpoints with typical requests. Always check rate limits and terms of use.

### 3.1 Wiktionary (MediaWiki API) — definitions, IPA, examples, gender, POS, etymology
- Base: `https://de.wiktionary.org/w/api.php` (German) and `https://en.wiktionary.org/w/api.php` (English)
- Parse page wikitext (used in-app):
```bash
curl "https://de.wiktionary.org/w/api.php?action=parse&format=json&prop=wikitext&page=Haus&disableeditsection=true"
```
- Notes: Use `prop=wikitext` then parse with our `WiktionaryParser`. Audio templates like `{{Audio|...}}` map to Wikimedia Commons URLs.

### 3.2 OpenThesaurus — German synonyms/antonyms
- Base: `https://www.openthesaurus.de/`
- Search endpoint:
```bash
curl "https://www.openthesaurus.de/synonyme/search?q=Haus&format=application/json&similar=true"
```
- Notes: Rate limit ~60 RPM/IP. Attribution required; data under an open license (verify current terms on site).

### 3.3 Tatoeba — bilingual example sentences (recommended)
- Base: `https://tatoeba.org/eng/api_v0/search`
- Query German→English examples:
```bash
curl "https://tatoeba.org/eng/api_v0/search?query=Haus&from=deu&to=eng&orphans=no&unapproved=no&native=no&limit=10"
```
- Response includes sentence pairs with licensing; ideal for replacing/de-risking Reverso. Respect attribution.

### 3.4 Wikidata Lexemes — gender, part-of-speech, forms (recommended)
- Search lexemes by lemma:
```bash
curl "https://www.wikidata.org/w/api.php?action=wbsearchentities&format=json&type=lexeme&language=de&search=Haus"
```
- Fetch lexeme entity JSON (`L<ID>` from search result):
```bash
curl "https://www.wikidata.org/wiki/Special:EntityData/L1234.json"
```
- Notes: Lexemes include POS, grammatical gender (for nouns), forms (plural, cases), pronunciations (sometimes). Excellent for deterministic grammar tables.

### 3.5 MyMemory Translation — quick translations
- Base: `https://api.mymemory.translated.net/`
- Translate DE→EN:
```bash
curl "https://api.mymemory.translated.net/get?q=Haus&langpair=de|en"
```
- Notes: Free, crowd-sourced; quality varies. Use as one signal among others.

### 3.6 LibreTranslate — fallback machine translation
- Base: `https://libretranslate.com/`
- POST translate:
```bash
curl -X POST "https://libretranslate.com/translate" \
  -H "Content-Type: application/json" \
  -d '{"q":"Haus","source":"de","target":"en","format":"text"}'
```
- Notes: Public instances have strict rate limits; consider self-hosting for stability.

### 3.7 German Verb API — conjugation tables
- Base: `https://german-verbs-api.onrender.com/`
- Conjugations:
```bash
curl "https://german-verbs-api.onrender.com/api/verbs/sprechen"
```
- Notes: Community API; add retry/fallback to local conjugator.

### 3.8 Reverso Context (unofficial) — bilingual examples
- Base: `https://context.reverso.net/`
- Endpoint used by app:
```bash
curl "https://context.reverso.net/bst-query-context/de-en?query=Haus&limit=5"
```
- Notes: Not an official public API; terms may restrict automated use. Prefer Tatoeba for long-term stability.

### 3.9 Wikimedia Commons — audio files (from Wiktionary templates)
- Audio references appear in wikitext via templates like `{{Audio|De-Haus.ogg}}`. Build URLs as:
```
https://upload.wikimedia.org/wikipedia/commons/<path from template>
```
- Our parser already extracts audio file names when present and assembles URLs.

---

## 4) Proposed improvements (LEO-style roadmap)

### Data quality and coverage
- Add Tatoeba examples: Replace or augment Reverso with licensed bilingual sentence pairs.
- Add Wikidata Lexemes: Use to enrich gender, part of speech, plural and case forms, and alternate lemmas.
- Noun declensions and plural: Generate tables using Wikidata forms; fallback templates for common patterns.
- Verb conjugations: Expand with mood/voice; show auxiliaries, participles, and separable-prefix highlights.
- Collocations/frequency: Optional integration with Leipzig Wortschatz (requires API key) or compile an offline frequency list to annotate entries.
- Smarter lemmatization: On input, resolve base form via Wikidata lexemes + simple rules (strip common suffixes; normalize capitalization; umlaut handling) before querying sources.

### UX and pedagogy
- Per-sense grouping: Cluster definitions with sense labels and attach examples per sense.
- Grammar chips: Prominent `der/die/das`, plural, genitive, case governance (for prepositions), separable prefix indicator.
- Single-page LEO layout: Already present; add quick actions — copy, share, add to vocab, star/favorite.
- SRS integration: One-tap add to `VocabularyPackService` with default deck and CEFR tagging.
- Autocomplete and recent searches: Leverage offline DB suggestions (already supported), surface more aggressively.

### Performance and reliability
- Centralized caching: Persist merged `DictionarySearchResult` in Room for offline re-access, with source and timestamp metadata.
- Rate limiting and retries: Exponential backoff; vendor-specific ceilings (OpenThesaurus ~60 RPM, Tatoeba/QS limits, Wikimedia polite usage).
- Parallel fan-out with circuit breakers: Cancel slow sources when first high-quality result set is ready.

### Licensing and attribution
- Attribute sources prominently in the UI footer and within each section (Wiktionary, OpenThesaurus, Tatoeba, Wikidata, LibreTranslate/MyMemory, Commons).
- Respect content licenses (e.g., CC BY-SA for Wiktionary excerpts; Tatoeba per-sentence licensing); avoid storing raw licensed text beyond allowed terms.

---

## 5) Concrete integration plan (incremental)

1) Add Tatoeba examples
- New Retrofit service `TatoebaApiService` → `GET /eng/api_v0/search` with `from=deu&to=eng`.
- Merge into `DictionaryRepository.getReversoExamples()` replacement (`getTatoebaExamples`) and prefer examples with translations.

2) Add Wikidata Lexeme enrichment
- `WikidataLexemeService`:
  - `GET /w/api.php?action=wbsearchentities&type=lexeme&language=de&search={word}`
  - `GET /wiki/Special:EntityData/{lexemeId}.json`
- Extract: POS, gender (for nouns), plural, forms per case/number; surface in UI under “Grammar”.

3) Declension/Conjugation tables
- Build composables for noun/adjective/pronoun declension grids, and extend verb tables with additional moods/voices.

4) Caching layer for merged results
- Add `dictionary_entries` table storing `DictionarySearchResult` JSON, `sources`, and `fetchedAt`. TTL: 24–72h.

5) UX polish
- Chips for gender and plural; quick actions; sense-grouped definitions.
- Add “Add to vocab” on every section; wire to spaced repetition packs.

6) Optional: frequency/collocation badges
- If key available, fetch collocations; otherwise ship a small offline frequency list to annotate entries (A1–C1 labels already present in DB can guide).

---

## 6) Current HTTP requests used in-app (for reference)

These are already implemented and wired via Retrofit services:

- Wiktionary parse (wikitext):
```http
GET https://{de|en}.wiktionary.org/w/api.php?action=parse&format=json&prop=wikitext&page={WORD}&disableeditsection=true
```

- OpenThesaurus synonyms:
```http
GET https://www.openthesaurus.de/synonyme/search?q={WORD}&format=application/json&similar=true
```

- MyMemory translations:
```http
GET https://api.mymemory.translated.net/get?q={WORD}&langpair={FROM}|{TO}
```

- LibreTranslate (fallback):
```http
POST https://libretranslate.com/translate
Content-Type: application/json
{"q":"{WORD}","source":"{FROM}","target":"{TO}","format":"text"}
```

- German Verb API:
```http
GET https://german-verbs-api.onrender.com/api/verbs/{VERB}
```

- Reverso Context (unofficial):
```http
GET https://context.reverso.net/bst-query-context/{from}-{to}?query={WORD}&limit=5
```

---

## 7) Risks and mitigations

- Unofficial endpoints (e.g., Reverso) can break — replace with Tatoeba.
- Rate limits and downtime — add caching, retries, and multiple fallbacks per capability.
- Licensing — add attribution and avoid storing original long-form content where not permitted.

---

## 8) Quick test checklist (manual QA)
- Offline search for common A1–B1 words returns definitions, examples, gender, IPA.
- Online search for less-common words merges Wiktionary + synonyms + translations.
- Verb queries show present/past/future + participles; separable prefix verbs render clearly.
- Noun entries show article chip, plural, and (after lexeme integration) declension table.
- Examples section contains bilingual pairs with source attribution.
- TTS works for both DE and EN; audio button appears when Commons audio is available.

---

## 9) References
- Wiktionary MediaWiki API: `https://www.mediawiki.org/wiki/API:Main_page`
- OpenThesaurus API: `https://www.openthesaurus.de/`
- Tatoeba API v0: `https://tatoeba.org/eng/help/api`
- Wikidata API (entities & lexemes): `https://www.wikidata.org/wiki/Wikidata:Data_access`
- Wikidata Query Service (SPARQL): `https://query.wikidata.org/`
- LibreTranslate: `https://libretranslate.com/`
- MyMemory Translation: `https://mymemory.translated.net/doc/spec.php`
- Wikimedia Commons: `https://commons.wikimedia.org/`


