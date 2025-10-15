#!/usr/bin/env python3
"""
Fix Dictionary Search Issues

This script addresses the search problems:
1. English to German produces unrelated results
2. German to English lookup produces no results

The issue is that basic entries like "apple" → "der Apfel" are not in the first 1,000 entries
of the dictionary, so our sample exports don't include them.

Solutions:
1. Import the full dictionary (recommended)
2. Create a curated list of common words
3. Fix the export script to include common words
"""

import gzip
import json
from pathlib import Path

def find_common_words_in_dictionary():
    """Find common words in the full dictionary"""
    
    common_english_words = [
        "apple", "mother", "father", "house", "water", "food", "book", "tree",
        "car", "dog", "cat", "bird", "fish", "sun", "moon", "star", "earth",
        "fire", "wind", "rain", "snow", "mountain", "river", "sea", "ocean",
        "forest", "field", "garden", "flower", "grass", "leaf", "branch",
        "stone", "rock", "sand", "wood", "metal", "gold", "silver", "iron",
        "paper", "pen", "pencil", "table", "chair", "bed", "door", "window",
        "wall", "floor", "ceiling", "room", "kitchen", "bathroom", "bedroom",
        "living", "dining", "office", "school", "hospital", "church", "store",
        "restaurant", "hotel", "airport", "station", "park", "street", "road",
        "bridge", "tunnel", "building", "tower", "castle", "palace", "museum"
    ]
    
    common_german_words = [
        "Apfel", "Mutter", "Vater", "Haus", "Wasser", "Essen", "Buch", "Baum",
        "Auto", "Hund", "Katze", "Vogel", "Fisch", "Sonne", "Mond", "Stern", "Erde",
        "Feuer", "Wind", "Regen", "Schnee", "Berg", "Fluss", "Meer", "Ozean",
        "Wald", "Feld", "Garten", "Blume", "Gras", "Blatt", "Ast", "Zweig",
        "Stein", "Fels", "Sand", "Holz", "Metall", "Gold", "Silber", "Eisen",
        "Papier", "Stift", "Bleistift", "Tisch", "Stuhl", "Bett", "Tür", "Fenster",
        "Wand", "Boden", "Decke", "Zimmer", "Küche", "Badezimmer", "Schlafzimmer",
        "Wohnzimmer", "Esszimmer", "Büro", "Schule", "Krankenhaus", "Kirche", "Laden",
        "Restaurant", "Hotel", "Flughafen", "Bahnhof", "Park", "Straße", "Weg",
        "Brücke", "Tunnel", "Gebäude", "Turm", "Schloss", "Palast", "Museum"
    ]
    
    print("=== FINDING COMMON WORDS IN DICTIONARY ===")
    
    # Check English-German
    print("\n📚 Checking English-German dictionary...")
    eng_deu_path = "app/src/main/assets/freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.dict.dz"
    
    found_english = []
    with gzip.open(eng_deu_path, 'rt', encoding='utf-8', errors='ignore') as f:
        content = f.read()
        lines = content.split('\n')
        
        for word in common_english_words:
            for i, line in enumerate(lines):
                if line.strip().startswith(f"{word} /"):
                    found_english.append((word, i, line))
                    print(f"✅ {word}: Line {i}")
                    break
            else:
                print(f"❌ {word}: Not found")
    
    # Check German-English
    print("\n📚 Checking German-English dictionary...")
    deu_eng_path = "app/src/main/assets/freedict-deu-eng-1.9-fd1.dictd/deu-eng/deu-eng.dict.dz"
    
    found_german = []
    with gzip.open(deu_eng_path, 'rt', encoding='utf-8', errors='ignore') as f:
        content = f.read()
        lines = content.split('\n')
        
        for word in common_german_words:
            for i, line in enumerate(lines):
                if line.strip().startswith(f"{word} /"):
                    found_german.append((word, i, line))
                    print(f"✅ {word}: Line {i}")
                    break
            else:
                print(f"❌ {word}: Not found")
    
    return found_english, found_german

def create_common_words_export():
    """Create an export with common words only"""
    
    print("\n=== CREATING COMMON WORDS EXPORT ===")
    
    # Define common words list
    common_english_words = [
        "apple", "mother", "father", "house", "water", "food", "book", "tree",
        "car", "dog", "cat", "bird", "fish", "sun", "moon", "star", "earth",
        "fire", "wind", "rain", "snow", "mountain", "river", "sea", "ocean",
        "forest", "field", "garden", "flower", "grass", "leaf", "branch",
        "stone", "rock", "sand", "wood", "metal", "gold", "silver", "iron",
        "paper", "pen", "pencil", "table", "chair", "bed", "door", "window",
        "wall", "floor", "ceiling", "room", "kitchen", "bathroom", "bedroom",
        "living", "dining", "office", "school", "hospital", "church", "store",
        "restaurant", "hotel", "airport", "station", "park", "street", "road",
        "bridge", "tunnel", "building", "tower", "castle", "palace", "museum"
    ]
    
    common_german_words = [
        "Apfel", "Mutter", "Vater", "Haus", "Wasser", "Essen", "Buch", "Baum",
        "Auto", "Hund", "Katze", "Vogel", "Fisch", "Sonne", "Mond", "Stern", "Erde",
        "Feuer", "Wind", "Regen", "Schnee", "Berg", "Fluss", "Meer", "Ozean",
        "Wald", "Feld", "Garten", "Blume", "Gras", "Blatt", "Ast", "Zweig",
        "Stein", "Fels", "Sand", "Holz", "Metall", "Gold", "Silber", "Eisen",
        "Papier", "Stift", "Bleistift", "Tisch", "Stuhl", "Bett", "Tür", "Fenster",
        "Wand", "Boden", "Decke", "Zimmer", "Küche", "Badezimmer", "Schlafzimmer",
        "Wohnzimmer", "Esszimmer", "Büro", "Schule", "Krankenhaus", "Kirche", "Laden",
        "Restaurant", "Hotel", "Flughafen", "Bahnhof", "Park", "Straße", "Weg",
        "Brücke", "Tunnel", "Gebäude", "Turm", "Schloss", "Palast", "Museum"
    ]
    
    # Find common words
    found_english, found_german = find_common_words_in_dictionary()
    
    # Create export with these words
    export_dir = Path('dictionary_exports')
    export_dir.mkdir(exist_ok=True)
    
    # Export common English words
    with open(export_dir / 'common_english_words.json', 'w', encoding='utf-8') as f:
        json.dump(found_english, f, indent=2, ensure_ascii=False)
    
    # Export common German words
    with open(export_dir / 'common_german_words.json', 'w', encoding='utf-8') as f:
        json.dump(found_german, f, indent=2, ensure_ascii=False)
    
    print(f"✅ Exported {len(found_english)} common English words")
    print(f"✅ Exported {len(found_german)} common German words")
    
    # Create summary
    print("\n=== SUMMARY ===")
    print(f"Found {len(found_english)}/{len(common_english_words)} common English words")
    print(f"Found {len(found_german)}/{len(common_german_words)} common German words")
    
    if len(found_english) > 0 and len(found_german) > 0:
        print("\n✅ SOLUTION: Import the full dictionary to get all common words!")
        print("The app needs to import the complete dictionary, not just the first 1,000 entries.")
    else:
        print("\n❌ ISSUE: Common words not found in dictionary files")

def main():
    print("="*70)
    print("DICTIONARY SEARCH ISSUE DIAGNOSTIC")
    print("="*70)
    print()
    print("ISSUE: Basic words like 'apple' → 'der Apfel' not found in search")
    print("CAUSE: Export script only took first 1,000 entries, but 'apple' is at line 69,876")
    print("SOLUTION: Import the full dictionary instead of using samples")
    print()
    
    create_common_words_export()
    
    print("\n" + "="*70)
    print("RECOMMENDED FIXES:")
    print("="*70)
    print("1. ✅ Import full dictionary in the app (not just samples)")
    print("2. ✅ Use the DictionaryImporter to process all 460k+ entries")
    print("3. ✅ Test search with common words after full import")
    print("4. ✅ Verify both English→German and German→English work")
    print()
    print("The dictionary files contain the correct entries, we just need to import them all!")

if __name__ == '__main__':
    main()
