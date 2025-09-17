## FreeDict Integration: Requirements and Implementation Plan

### Goals
- Make FreeDict dictd data the primary offline dictionary source for both directions:
  - `deu→eng` from `app/src/main/assets/freedict-deu-eng-1.9-fd1.dictd/deu-eng`
  - `eng→deu` from `app/src/main/assets/freedict-eng-deu-1.9-fd1.dictd/eng-deu`
- Remove prior offline dictionary database usage (`german_dictionary.db` and related Room entities for words/examples).
- Preserve and enhance the LEO-style experience already in the app (rich overview, grammar chips, examples, conjugations, synonyms, translations, attribution).
- Include grammar elements (articles, genders, POS) and merge/enrich with Wikidata/Wiktionary where FreeDict is sparse.
- Keep everything fully offline-first with optional online enrichment.

### Data Format Summary (dictd)
- Files: `.dict.dz` (dictzip-compressed dictionary data), `.index` (tab-separated headword, offset, length).
- The `.index` offset/length are base64-like encoded integers; decode using a 64-symbol alphabet (A–Z, a–z, 0–9, +, /) as big-endian base-64.
- Offsets/lengths refer to the uncompressed `.dict` stream. We will decompress the `.dict.dz` once on first use and cache the `.dict` in app storage for fast random access.
- Encoding: `00databaseutf8` flag exists in the index; assume UTF-8 payload.

### Architecture Changes
- New `FreedictReader`:
  - Decompress `.dict.dz` from assets → internal files dir on first run (idempotent).
  - Parse `.index` from assets. Provide:
    - `lookupExact(headword)` → returns raw entry text and a structured parse (translations, hints).
    - `suggest(prefix)` → quick suggestions using the index (optional initial subset; can evolve to full in-memory trie/binary search).
  - Lightweight grammar extraction from entry text:
    - Detect articles in German translations (`der|die|das`), infer gender.
    - Extract common POS markers (noun/verb/adj) when present.
- Refactor `OfflineDictionaryRepository`:
  - Replace Room-backed `GermanDictionaryDatabase` usage with `FreedictReader`.
  - Search flow:
    1) Try FreeDict (primary).
    2) Merge grammar from Wikidata Lexemes (gender, plural, declensions) when available.
    3) Merge definitions/pronunciation/etymology from Wiktionary; examples from Tatoeba; synonyms from OpenThesaurus; conjugations from German Verb API.
  - Provide reset to clear decompressed caches and reinitialize FreeDict.
- UI remains the same (`DictionaryScreen`, `DictionaryViewModel`), but “Reset Dictionary Database” will map to FreeDict cache reset.

### Parsing Heuristics (initial)
- For `en→de` translations, treat returned list as German candidates; if an item begins with an article (`der|die|das`), set gender and strip article from display variant.
- For `de→en`, the headword is German; return English translations from entry text.
- Split entry text into translations by `;`, `,`, or line breaks; trim and deduplicate. Keep 5–12 items.
- POS detection: patterns like `(n.)`, `(v.)`, `(adj.)`, or words `noun`, `verb`, `adjective` within the first segment.

### Removal/Deprecation
- Remove asset `app/src/main/assets/german_dictionary.db`.
- Stop using `GermanDictionaryDatabase` and its entities in `OfflineDictionaryRepository`.
- Keep existing `DictionaryCacheEntry` (merged result cache) and other app databases intact.

### Storage & Footprint
- Decompressed `.dict` files are stored under `context.filesDir/freedict/<pair>.dict`.
- Not tracked in git; add ignore patterns for any accidental `.dict` dumps.

### Git & Licensing
- Ensure attribution for FreeDict in the attribution footer.
- Add `.gitignore` entries for `*.dict`, `*.dict.unzipped`, and `*.dict.cache` anywhere in the repo.

### Step-by-step Execution Plan
1) Implement `FreedictReader` (decode index, decompress dictzip to `.dict`, lookup/suggest, basic parsing).
2) Refactor `OfflineDictionaryRepository` to use `FreedictReader` first; remove Room dictionary usage.
3) Update `DictionaryViewModel` reset to call FreeDict cache reset (no UI changes required).
4) Delete `german_dictionary.db` asset and update `.gitignore`.
5) Verify end-to-end: DE→EN and EN→DE queries; check grammar chips; ensure online enrichments still merge.
6) Optimize: index memory use, add prefix suggestions, and incremental parsing improvements.

### Test Checklist
- First-run decompress works; subsequent runs skip.
- Exact lookup of common words works in both directions.
- Gender appears for nouns when article is present in translations or via Wikidata.
- Examples populate from Tatoeba; conjugations appear for verbs.
- Reset clears caches and reinitializes.
