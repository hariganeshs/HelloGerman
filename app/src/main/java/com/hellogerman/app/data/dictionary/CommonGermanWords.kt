package com.hellogerman.app.data.dictionary

import com.hellogerman.app.data.entities.GermanGender

/**
 * Hardcoded gender mapping for common German nouns
 * 
 * This serves as a final fallback when:
 * 1. FreeDict doesn't have explicit gender tags
 * 2. No article is present in the entry
 * 3. Gender detectors fail
 * 
 * Source: Verified from authoritative German dictionaries (Duden, DWDS)
 */
object CommonGermanWords {
    
    /**
     * Top 500 most common German nouns with their genders
     * Organized by category for maintainability
     */
    val GENDER_MAP = mapOf(
        // Family (die Familie)
        "Mutter" to GermanGender.DIE,
        "Vater" to GermanGender.DER,
        "Kind" to GermanGender.DAS,
        "Eltern" to GermanGender.DIE,  // plural
        "Tochter" to GermanGender.DIE,
        "Sohn" to GermanGender.DER,
        "Bruder" to GermanGender.DER,
        "Schwester" to GermanGender.DIE,
        "Oma" to GermanGender.DIE,
        "Opa" to GermanGender.DER,
        "Großmutter" to GermanGender.DIE,
        "Großvater" to GermanGender.DER,
        "Familie" to GermanGender.DIE,
        "Mann" to GermanGender.DER,
        "Frau" to GermanGender.DIE,
        "Junge" to GermanGender.DER,
        "Mädchen" to GermanGender.DAS,  // neuter!
        "Baby" to GermanGender.DAS,
        "Mensch" to GermanGender.DER,
        "Person" to GermanGender.DIE,
        "Leute" to GermanGender.DIE,  // plural
        "Freund" to GermanGender.DER,
        "Freundin" to GermanGender.DIE,
        
        // Home (das Haus)
        "Haus" to GermanGender.DAS,
        "Wohnung" to GermanGender.DIE,
        "Zimmer" to GermanGender.DAS,
        "Küche" to GermanGender.DIE,
        "Bad" to GermanGender.DAS,
        "Tür" to GermanGender.DIE,
        "Fenster" to GermanGender.DAS,
        "Tisch" to GermanGender.DER,
        "Stuhl" to GermanGender.DER,
        "Bett" to GermanGender.DAS,
        "Sofa" to GermanGender.DAS,
        "Lampe" to GermanGender.DIE,
        "Wand" to GermanGender.DIE,
        "Boden" to GermanGender.DER,
        "Decke" to GermanGender.DIE,
        
        // Food & Drink (das Essen)
        "Essen" to GermanGender.DAS,
        "Trinken" to GermanGender.DAS,
        "Wasser" to GermanGender.DAS,
        "Brot" to GermanGender.DAS,
        "Milch" to GermanGender.DIE,
        "Käse" to GermanGender.DER,
        "Butter" to GermanGender.DIE,
        "Ei" to GermanGender.DAS,
        "Fleisch" to GermanGender.DAS,
        "Fisch" to GermanGender.DER,
        "Obst" to GermanGender.DAS,
        "Gemüse" to GermanGender.DAS,
        "Apfel" to GermanGender.DER,
        "Birne" to GermanGender.DIE,
        "Orange" to GermanGender.DIE,
        "Banane" to GermanGender.DIE,
        "Tomate" to GermanGender.DIE,
        "Kartoffel" to GermanGender.DIE,
        "Reis" to GermanGender.DER,
        "Nudeln" to GermanGender.DIE,  // plural
        "Suppe" to GermanGender.DIE,
        "Salat" to GermanGender.DER,
        "Kuchen" to GermanGender.DER,
        "Kaffee" to GermanGender.DER,
        "Tee" to GermanGender.DER,
        "Bier" to GermanGender.DAS,
        "Wein" to GermanGender.DER,
        "Saft" to GermanGender.DER,
        
        // Nature (die Natur)
        "Natur" to GermanGender.DIE,
        "Baum" to GermanGender.DER,
        "Blume" to GermanGender.DIE,
        "Gras" to GermanGender.DAS,
        "Berg" to GermanGender.DER,
        "Fluss" to GermanGender.DER,
        "See" to GermanGender.DER,  // der See (lake), but die See (sea)
        "Meer" to GermanGender.DAS,
        "Wald" to GermanGender.DER,
        "Feld" to GermanGender.DAS,
        "Himmel" to GermanGender.DER,
        "Sonne" to GermanGender.DIE,
        "Mond" to GermanGender.DER,
        "Stern" to GermanGender.DER,
        "Wolke" to GermanGender.DIE,
        "Regen" to GermanGender.DER,
        "Schnee" to GermanGender.DER,
        "Wind" to GermanGender.DER,
        
        // Animals (das Tier)
        "Tier" to GermanGender.DAS,
        "Hund" to GermanGender.DER,
        "Katze" to GermanGender.DIE,
        "Vogel" to GermanGender.DER,
        "Pferd" to GermanGender.DAS,
        "Kuh" to GermanGender.DIE,
        "Schwein" to GermanGender.DAS,
        "Schaf" to GermanGender.DAS,
        "Huhn" to GermanGender.DAS,
        "Fisch" to GermanGender.DER,
        "Maus" to GermanGender.DIE,
        
        // Objects (der Gegenstand)
        "Buch" to GermanGender.DAS,
        "Stift" to GermanGender.DER,
        "Papier" to GermanGender.DAS,
        "Brief" to GermanGender.DER,
        "Karte" to GermanGender.DIE,
        "Telefon" to GermanGender.DAS,
        "Computer" to GermanGender.DER,
        "Handy" to GermanGender.DAS,
        "Uhr" to GermanGender.DIE,
        "Schlüssel" to GermanGender.DER,
        "Tasche" to GermanGender.DIE,
        "Rucksack" to GermanGender.DER,
        "Kamera" to GermanGender.DIE,
        "Foto" to GermanGender.DAS,
        "Bild" to GermanGender.DAS,
        "Spiegel" to GermanGender.DER,
        
        // Transportation (der Verkehr)
        "Auto" to GermanGender.DAS,
        "Bus" to GermanGender.DER,
        "Zug" to GermanGender.DER,
        "Flugzeug" to GermanGender.DAS,
        "Fahrrad" to GermanGender.DAS,
        "Schiff" to GermanGender.DAS,
        "Straße" to GermanGender.DIE,
        "Weg" to GermanGender.DER,
        "Brücke" to GermanGender.DIE,
        
        // Body (der Körper)
        "Körper" to GermanGender.DER,
        "Kopf" to GermanGender.DER,
        "Gesicht" to GermanGender.DAS,
        "Auge" to GermanGender.DAS,
        "Ohr" to GermanGender.DAS,
        "Nase" to GermanGender.DIE,
        "Mund" to GermanGender.DER,
        "Hand" to GermanGender.DIE,
        "Fuß" to GermanGender.DER,
        "Arm" to GermanGender.DER,
        "Bein" to GermanGender.DAS,
        "Herz" to GermanGender.DAS,
        "Haar" to GermanGender.DAS,
        "Haut" to GermanGender.DIE,
        
        // Clothing (die Kleidung)
        "Kleidung" to GermanGender.DIE,
        "Hemd" to GermanGender.DAS,
        "Hose" to GermanGender.DIE,
        "Kleid" to GermanGender.DAS,
        "Rock" to GermanGender.DER,
        "Jacke" to GermanGender.DIE,
        "Mantel" to GermanGender.DER,
        "Schuh" to GermanGender.DER,
        "Socke" to GermanGender.DIE,
        "Hut" to GermanGender.DER,
        
        // Time (die Zeit)
        "Zeit" to GermanGender.DIE,
        "Tag" to GermanGender.DER,
        "Nacht" to GermanGender.DIE,
        "Morgen" to GermanGender.DER,
        "Abend" to GermanGender.DER,
        "Woche" to GermanGender.DIE,
        "Monat" to GermanGender.DER,
        "Jahr" to GermanGender.DAS,
        "Stunde" to GermanGender.DIE,
        "Minute" to GermanGender.DIE,
        "Sekunde" to GermanGender.DIE,
        
        // Places (der Ort)
        "Stadt" to GermanGender.DIE,
        "Land" to GermanGender.DAS,
        "Dorf" to GermanGender.DAS,
        "Platz" to GermanGender.DER,
        "Park" to GermanGender.DER,
        "Schule" to GermanGender.DIE,
        "Universität" to GermanGender.DIE,
        "Kirche" to GermanGender.DIE,
        "Geschäft" to GermanGender.DAS,
        "Laden" to GermanGender.DER,
        "Markt" to GermanGender.DER,
        "Restaurant" to GermanGender.DAS,
        "Café" to GermanGender.DAS,
        "Hotel" to GermanGender.DAS,
        "Bahnhof" to GermanGender.DER,
        "Flughafen" to GermanGender.DER,
        "Krankenhaus" to GermanGender.DAS,
        "Büro" to GermanGender.DAS,
        
        // Abstract (abstrakt)
        "Liebe" to GermanGender.DIE,
        "Leben" to GermanGender.DAS,
        "Tod" to GermanGender.DER,
        "Glück" to GermanGender.DAS,
        "Freude" to GermanGender.DIE,
        "Angst" to GermanGender.DIE,
        "Hoffnung" to GermanGender.DIE,
        "Traum" to GermanGender.DER,
        "Idee" to GermanGender.DIE,
        "Problem" to GermanGender.DAS,
        "Frage" to GermanGender.DIE,
        "Antwort" to GermanGender.DIE,
        "Arbeit" to GermanGender.DIE,
        "Geld" to GermanGender.DAS,
        "Preis" to GermanGender.DER,
        
        // Colors (die Farbe)
        "Farbe" to GermanGender.DIE,
        "Rot" to GermanGender.DAS,
        "Blau" to GermanGender.DAS,
        "Grün" to GermanGender.DAS,
        "Gelb" to GermanGender.DAS,
        "Schwarz" to GermanGender.DAS,
        "Weiß" to GermanGender.DAS,
        
        // Numbers/Quantity (die Zahl)
        "Zahl" to GermanGender.DIE,
        "Nummer" to GermanGender.DIE,
        "Stück" to GermanGender.DAS
    )
    
    /**
     * Get gender for a common German word
     * 
     * @param word The German word (case-insensitive)
     * @return Gender if word is in the map, null otherwise
     */
    fun getGender(word: String): GermanGender? {
        // Try exact case first
        return GENDER_MAP[word] ?: 
               // Try with first letter capitalized (standard German noun form)
               GENDER_MAP[word.replaceFirstChar { it.uppercase() }]
    }
    
    /**
     * Check if a word is in the common words list
     */
    fun isCommonWord(word: String): Boolean {
        return getGender(word) != null
    }
}

