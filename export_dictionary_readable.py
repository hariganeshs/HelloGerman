#!/usr/bin/env python3
"""
Dictionary Database Exporter
Decompresses FreeDict files and exports to multiple viewable formats:
- JSON (structured data)
- CSV (spreadsheet compatible)
- TXT (human readable)
"""

import gzip
import json
import csv
import re
from pathlib import Path

def decompress_dict(dict_path):
    """Decompress a .dict.dz file"""
    print(f"Decompressing {dict_path}...")
    with gzip.open(dict_path, 'rt', encoding='utf-8', errors='ignore') as f:
        content = f.read()
    print(f"✓ Decompressed {len(content):,} characters")
    return content

def parse_dictionary_entry(lines, start_idx):
    """Parse a single dictionary entry starting from a line"""
    entry = {
        'headword': '',
        'pronunciation_ipa': '',
        'translations': [],
        'gender': [],
        'word_types': [],
        'examples': [],
        'synonyms': [],
        'notes': [],
        'domains': []
    }
    
    if start_idx >= len(lines):
        return None, start_idx
    
    # First line contains headword and IPA
    first_line = lines[start_idx].strip()
    if not first_line or first_line.startswith(' '):
        return None, start_idx
    
    # Extract headword and IPA
    ipa_match = re.match(r'^(.+?)\s*/([^/]+)/', first_line)
    if ipa_match:
        entry['headword'] = ipa_match.group(1).strip()
        entry['pronunciation_ipa'] = ipa_match.group(2).strip()
    else:
        entry['headword'] = first_line.strip()
    
    # Process following lines until next headword
    i = start_idx + 1
    while i < len(lines):
        line = lines[i]
        
        # Check if this is a new entry (starts without indentation and has IPA or is capitalized)
        if line and not line.startswith(' ') and not line.startswith('\t'):
            break
        
        line = line.strip()
        if not line:
            i += 1
            continue
        
        # Extract synonyms
        if 'Synonym' in line:
            syns = re.findall(r'\{([^}]+)\}', line)
            entry['synonyms'].extend(syns)
        
        # Extract notes
        elif 'Note:' in line:
            note = re.sub(r'.*Note:\s*', '', line).strip()
            if note:
                entry['notes'].append(note)
        
        # Extract examples (quoted)
        elif '"' in line:
            examples = re.findall(r'"([^"]+)"\s*-\s*([^"\n]+)', line)
            for eng, ger in examples:
                entry['examples'].append({
                    'english': eng.strip(),
                    'german': ger.strip()
                })
        
        # Extract translations
        elif not line.startswith('see:'):
            # Extract gender
            if '<masc>' in line:
                entry['gender'].append('masculine')
            elif '<fem>' in line:
                entry['gender'].append('feminine')
            elif '<neut>' in line:
                entry['gender'].append('neuter')
            
            # Extract word type
            if '<adj>' in line:
                entry['word_types'].append('adjective')
            elif '<verb>' in line or '<v>' in line:
                entry['word_types'].append('verb')
            elif '<pl>' in line:
                entry['word_types'].append('plural')
            
            # Extract domains
            domains = re.findall(r'\[([^\]]+)\]', line)
            entry['domains'].extend(domains)
            
            # Clean translation
            translation = re.sub(r'<[^>]+>', '', line)
            translation = re.sub(r'\[([^\]]+)\]', '', translation)
            translation = re.sub(r'Note:.*', '', translation).strip()
            
            if translation and len(translation) > 1 and not translation.startswith('Synonym'):
                entry['translations'].append(translation)
        
        i += 1
    
    # Clean up duplicates
    entry['gender'] = list(set(entry['gender']))
    entry['word_types'] = list(set(entry['word_types']))
    entry['domains'] = list(set(entry['domains']))
    entry['synonyms'] = list(set(entry['synonyms']))
    
    return entry if entry['headword'] else None, i

def parse_full_dictionary(content, max_entries=None):
    """Parse entire dictionary content"""
    lines = content.split('\n')
    entries = []
    i = 0
    
    while i < len(lines):
        if max_entries and len(entries) >= max_entries:
            break
        
        entry, next_i = parse_dictionary_entry(lines, i)
        if entry and entry['translations']:
            entries.append(entry)
        
        i = next_i if next_i > i else i + 1
    
    return entries

def export_to_json(entries, output_file):
    """Export to JSON format"""
    print(f"\nExporting to JSON: {output_file}")
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(entries, f, indent=2, ensure_ascii=False)
    print(f"✓ Exported {len(entries):,} entries")

def export_to_csv(entries, output_file):
    """Export to CSV format"""
    print(f"\nExporting to CSV: {output_file}")
    with open(output_file, 'w', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        
        # Header
        writer.writerow([
            'English Word',
            'IPA Pronunciation',
            'German Translation',
            'Gender',
            'Word Type',
            'Domains',
            'Examples',
            'Synonyms'
        ])
        
        # Data
        for entry in entries:
            writer.writerow([
                entry['headword'],
                entry['pronunciation_ipa'],
                '; '.join(entry['translations'][:3]),  # First 3 translations
                ', '.join(entry['gender']),
                ', '.join(entry['word_types']),
                ', '.join(entry['domains'][:3]),
                str(len(entry['examples'])),
                ', '.join(entry['synonyms'][:3])
            ])
    
    print(f"✓ Exported {len(entries):,} entries")

def export_to_readable_txt(entries, output_file):
    """Export to human-readable text format"""
    print(f"\nExporting to readable TXT: {output_file}")
    with open(output_file, 'w', encoding='utf-8') as f:
        for i, entry in enumerate(entries, 1):
            f.write(f"\n{'='*70}\n")
            f.write(f"Entry #{i}: {entry['headword']}\n")
            f.write(f"{'='*70}\n")
            
            if entry['pronunciation_ipa']:
                f.write(f"IPA: /{entry['pronunciation_ipa']}/\n")
            
            f.write(f"\n📝 German Translations:\n")
            for trans in entry['translations'][:5]:
                gender_str = f" ({', '.join(entry['gender'])})" if entry['gender'] else ""
                f.write(f"  • {trans}{gender_str}\n")
            
            if entry['word_types']:
                f.write(f"\n🔤 Word Type: {', '.join(entry['word_types'])}\n")
            
            if entry['domains']:
                f.write(f"🏷️  Domains: {', '.join(entry['domains'])}\n")
            
            if entry['examples']:
                f.write(f"\n💬 Examples ({len(entry['examples'])}):\n")
                for ex in entry['examples'][:3]:
                    f.write(f"  EN: \"{ex['english']}\"\n")
                    f.write(f"  DE: {ex['german']}\n")
            
            if entry['synonyms']:
                f.write(f"\n🔗 Synonyms: {', '.join(entry['synonyms'][:5])}\n")
            
            if entry['notes']:
                f.write(f"\n📌 Notes:\n")
                for note in entry['notes'][:2]:
                    f.write(f"  • {note}\n")
    
    print(f"✓ Exported {len(entries):,} entries")

def print_statistics(entries):
    """Print statistics about the dictionary"""
    print("\n" + "="*70)
    print("DICTIONARY STATISTICS")
    print("="*70)
    
    total = len(entries)
    with_ipa = sum(1 for e in entries if e['pronunciation_ipa'])
    with_gender = sum(1 for e in entries if e['gender'])
    with_examples = sum(1 for e in entries if e['examples'])
    with_synonyms = sum(1 for e in entries if e['synonyms'])
    
    # Gender breakdown
    masculine = sum(1 for e in entries if 'masculine' in e['gender'])
    feminine = sum(1 for e in entries if 'feminine' in e['gender'])
    neuter = sum(1 for e in entries if 'neuter' in e['gender'])
    
    # Word type breakdown
    nouns = sum(1 for e in entries if 'noun' in str(e['word_types']) or e['gender'])
    verbs = sum(1 for e in entries if 'verb' in str(e['word_types']))
    adjectives = sum(1 for e in entries if 'adjective' in str(e['word_types']))
    
    print(f"\nTotal Entries: {total:,}")
    print(f"\nCoverage:")
    print(f"  • With IPA Pronunciation: {with_ipa:,} ({with_ipa*100/total:.1f}%)")
    print(f"  • With Gender Info: {with_gender:,} ({with_gender*100/total:.1f}%)")
    print(f"  • With Examples: {with_examples:,} ({with_examples*100/total:.1f}%)")
    print(f"  • With Synonyms: {with_synonyms:,} ({with_synonyms*100/total:.1f}%)")
    
    print(f"\nGender Distribution:")
    print(f"  • Masculine (der): {masculine:,}")
    print(f"  • Feminine (die): {feminine:,}")
    print(f"  • Neuter (das): {neuter:,}")
    
    print(f"\nWord Types:")
    print(f"  • Nouns: {nouns:,}")
    print(f"  • Verbs: {verbs:,}")
    print(f"  • Adjectives: {adjectives:,}")
    
    # Sample entries
    print("\n" + "="*70)
    print("SAMPLE ENTRIES")
    print("="*70)
    
    for entry in entries[:5]:
        gender_str = f" ({entry['gender'][0]})" if entry['gender'] else ""
        trans_str = entry['translations'][0] if entry['translations'] else 'N/A'
        print(f"\n{entry['headword']} /{entry['pronunciation_ipa']}/")
        print(f"  → {trans_str}{gender_str}")

def main():
    print("="*70)
    print("GERMAN DICTIONARY DATABASE EXPORTER")
    print("="*70)
    print()
    
    # Paths
    base_path = Path('app/src/main/assets')
    eng_deu_path = base_path / 'freedict-eng-deu-1.9-fd1.dictd/eng-deu/eng-deu.dict.dz'
    deu_eng_path = base_path / 'freedict-deu-eng-1.9-fd1.dictd/deu-eng/deu-eng.dict.dz'
    
    # Export directories
    export_dir = Path('dictionary_exports')
    export_dir.mkdir(exist_ok=True)
    
    # Process English-German dictionary
    if eng_deu_path.exists():
        print("\n📚 Processing English-German Dictionary")
        print("-"*70)
        
        content = decompress_dict(eng_deu_path)
        
        # Parse entries (first 1000 for quick viewing)
        print("\nParsing entries...")
        entries = parse_full_dictionary(content, max_entries=1000)
        
        # Export to multiple formats
        export_to_json(entries, export_dir / 'eng-deu_sample_1000.json')
        export_to_csv(entries, export_dir / 'eng-deu_sample_1000.csv')
        export_to_readable_txt(entries, export_dir / 'eng-deu_sample_1000.txt')
        
        # Print statistics
        print_statistics(entries)
        
        # Also create a small sample (50 entries) for quick inspection
        print(f"\n📋 Creating small sample (50 entries)...")
        small_sample = entries[:50]
        export_to_json(small_sample, export_dir / 'eng-deu_sample_50.json')
        print(f"✓ Small sample created")
    
    # Process German-English dictionary
    if deu_eng_path.exists():
        print("\n\n📚 Processing German-English Dictionary")
        print("-"*70)
        
        content = decompress_dict(deu_eng_path)
        
        # Parse entries (first 1000)
        print("\nParsing entries...")
        entries = parse_full_dictionary(content, max_entries=1000)
        
        # Export to multiple formats
        export_to_json(entries, export_dir / 'deu-eng_sample_1000.json')
        export_to_csv(entries, export_dir / 'deu-eng_sample_1000.csv')
        export_to_readable_txt(entries, export_dir / 'deu-eng_sample_1000.txt')
        
        # Print statistics
        print_statistics(entries)
    
    print("\n" + "="*70)
    print("✅ EXPORT COMPLETE!")
    print("="*70)
    print(f"\nFiles created in '{export_dir}' directory:")
    print("  • eng-deu_sample_50.json (50 entries, quick view)")
    print("  • eng-deu_sample_1000.json (1000 entries, detailed)")
    print("  • eng-deu_sample_1000.csv (spreadsheet format)")
    print("  • eng-deu_sample_1000.txt (human readable)")
    print("  • deu-eng_sample_1000.json (reverse direction)")
    print("  • deu-eng_sample_1000.csv")
    print("  • deu-eng_sample_1000.txt")
    print()
    print("💡 Open these files in:")
    print("  • JSON: VS Code, any text editor")
    print("  • CSV: Excel, Google Sheets, LibreOffice")
    print("  • TXT: Any text editor (formatted for reading)")
    print()

if __name__ == '__main__':
    main()

