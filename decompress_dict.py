#!/usr/bin/env python3
"""
Dictionary Decompressor and JSON Converter
Decompresses FreeDict dictd format and converts to JSON for analysis
"""

import gzip
import json
import re
from pathlib import Path

def decompress_dict(dict_path):
    """Decompress a .dict.dz file"""
    print(f"Decompressing {dict_path}...")
    with gzip.open(dict_path, 'rt', encoding='utf-8', errors='ignore') as f:
        content = f.read()
    print(f"Decompressed {len(content)} characters")
    return content

def parse_entry(entry_text):
    """Parse a single dictionary entry into structured data"""
    lines = entry_text.strip().split('\n')
    if not lines:
        return None
    
    # First line contains headword and pronunciation
    first_line = lines[0]
    
    # Extract headword (before IPA)
    headword_match = re.match(r'^(.+?)\s*/([^/]+)/', first_line)
    if headword_match:
        headword = headword_match.group(1).strip()
        ipa = headword_match.group(2).strip()
    else:
        # No IPA present
        headword = first_line.strip()
        ipa = None
    
    # Extract translations with metadata
    translations = []
    examples = []
    synonyms = []
    notes = []
    domains = []
    
    for line in lines[1:]:
        line = line.strip()
        if not line:
            continue
        
        # Check for synonyms
        if line.startswith('Synonyms:') or line.startswith('Synonym:'):
            syn_text = line.split(':', 1)[1].strip()
            synonyms.extend(re.findall(r'\{([^}]+)\}', syn_text))
            continue
        
        # Check for cross-references
        if line.startswith('see:'):
            continue
        
        # Check for notes
        if 'Note:' in line:
            note_match = re.search(r'Note:\s*(.+)', line)
            if note_match:
                notes.append(note_match.group(1).strip())
            continue
        
        # Check for examples (quoted text)
        example_matches = re.findall(r'"([^"]+)"\s*-\s*([^"]+?)(?=\s*"|$)', line)
        for eng_example, ger_example in example_matches:
            examples.append({
                'english': eng_example.strip(),
                'german': ger_example.strip()
            })
        
        # Extract translations (lines with German text)
        # Skip if it's just metadata
        if not any(x in line for x in ['see:', 'Synonym', 'Note:']):
            # Extract gender markers
            gender = None
            if '<masc>' in line:
                gender = 'masculine'
            elif '<fem>' in line:
                gender = 'feminine'
            elif '<neut>' in line:
                gender = 'neuter'
            
            # Extract word type
            word_type = None
            if '<adj>' in line:
                word_type = 'adjective'
            elif '<pl>' in line:
                word_type = 'plural'
            elif '<verb>' in line or '<v>' in line:
                word_type = 'verb'
            elif gender:
                word_type = 'noun'
            
            # Extract domain labels
            domain_matches = re.findall(r'\[([^\]]+)\]', line)
            if domain_matches:
                domains.extend(domain_matches)
            
            # Clean translation text
            translation = re.sub(r'<[^>]+>', '', line).strip()
            translation = re.sub(r'\[([^\]]+)\]', '', translation).strip()
            translation = re.sub(r'Note:.*', '', translation).strip()
            
            if translation and not translation.startswith('"'):
                translations.append({
                    'text': translation,
                    'gender': gender,
                    'word_type': word_type
                })
    
    return {
        'headword': headword,
        'pronunciation_ipa': ipa,
        'translations': translations,
        'examples': examples,
        'synonyms': synonyms,
        'notes': notes,
        'domains': list(set(domains))
    }

def parse_dictionary(content, max_entries=100):
    """Parse dictionary content into structured entries"""
    # Split by headwords (lines that start with a word followed by IPA or just a word at line start)
    entries = []
    current_entry = []
    
    for line in content.split('\n'):
        # Check if this is a new headword (not indented and has IPA or is at start of line)
        if line and not line.startswith(' ') and not line.startswith('\t'):
            # Check if previous entry should be saved
            if current_entry:
                entry_text = '\n'.join(current_entry)
                parsed = parse_entry(entry_text)
                if parsed and parsed.get('translations'):
                    entries.append(parsed)
                    if len(entries) >= max_entries:
                        break
            current_entry = [line]
        else:
            current_entry.append(line)
    
    # Don't forget the last entry
    if current_entry and len(entries) < max_entries:
        entry_text = '\n'.join(current_entry)
        parsed = parse_entry(entry_text)
        if parsed and parsed.get('translations'):
            entries.append(parsed)
    
    return entries

def main():
    # Paths
    base_path = Path('app/src/main/assets')
    
    # English-German dictionary
    eng_deu_path = base_path / 'freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.dict.dz'
    
    # Decompress and parse
    if eng_deu_path.exists():
        content = decompress_dict(eng_deu_path)
        
        # Save first 10000 lines to text file for inspection
        lines = content.split('\n')[:10000]
        with open('dictionary_sample.txt', 'w', encoding='utf-8') as f:
            f.write('\n'.join(lines))
        print(f"Saved sample to dictionary_sample.txt")
        
        # Parse entries
        print("Parsing dictionary entries...")
        entries = parse_dictionary(content, max_entries=200)
        
        # Save to JSON
        output_file = 'dictionary_sample.json'
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(entries, f, indent=2, ensure_ascii=False)
        
        print(f"Saved {len(entries)} entries to {output_file}")
        
        # Print some statistics
        print("\n=== Dictionary Statistics (sample) ===")
        print(f"Total entries: {len(entries)}")
        
        with_gender = sum(1 for e in entries if any(t.get('gender') for t in e.get('translations', [])))
        print(f"Entries with gender: {with_gender}")
        
        with_examples = sum(1 for e in entries if e.get('examples'))
        print(f"Entries with examples: {with_examples}")
        
        with_ipa = sum(1 for e in entries if e.get('pronunciation_ipa'))
        print(f"Entries with IPA: {with_ipa}")
        
        # Show a few examples
        print("\n=== Sample Entries ===")
        for entry in entries[:5]:
            print(f"\n{entry['headword']} /{entry.get('pronunciation_ipa', 'N/A')}/")
            for trans in entry.get('translations', [])[:3]:
                gender_str = f" ({trans['gender']})" if trans.get('gender') else ""
                print(f"  - {trans['text']}{gender_str}")
    else:
        print(f"Error: Dictionary file not found at {eng_deu_path}")

if __name__ == '__main__':
    main()

