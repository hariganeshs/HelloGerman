package com.hellogerman.app.data.conjugation

import com.hellogerman.app.data.models.VerbConjugations
import com.hellogerman.app.data.models.Participle

/**
 * Fallback German verb conjugation system
 * Handles basic conjugation patterns for common German verbs
 */
class GermanVerbConjugator {
    
    companion object {
        // Common German verbs with their conjugations
        private val COMMON_VERBS = mapOf(
            // Regular -en verbs
            "lernen" to createRegularConjugation("lern"),
            "spielen" to createRegularConjugation("spiel"),
            "arbeiten" to createRegularConjugation("arbeit", addEt = true),
            "hören" to createRegularConjugation("hör"),
            "kaufen" to createRegularConjugation("kauf"),
            "leben" to createRegularConjugation("leb"),
            "lieben" to createRegularConjugation("lieb"),
            "machen" to createRegularConjugation("mach"),
            "sagen" to createRegularConjugation("sag"),
            "suchen" to createRegularConjugation("such"),
            "wohnen" to createRegularConjugation("wohn"),
            "zeigen" to createRegularConjugation("zeig"),
            
            // Strong verbs
            "sprechen" to VerbConjugations(
                present = mapOf(
                    "ich" to "spreche",
                    "du" to "sprichst", 
                    "er/sie/es" to "spricht",
                    "wir" to "sprechen",
                    "ihr" to "sprecht",
                    "sie/Sie" to "sprechen"
                ),
                past = mapOf(
                    "ich" to "sprach",
                    "du" to "sprachst",
                    "er/sie/es" to "sprach", 
                    "wir" to "sprachen",
                    "ihr" to "spracht",
                    "sie/Sie" to "sprachen"
                ),
                participle = Participle(
                    present = "sprechend",
                    past = "gesprochen"
                )
            ),
            
            "gehen" to VerbConjugations(
                present = mapOf(
                    "ich" to "gehe",
                    "du" to "gehst",
                    "er/sie/es" to "geht",
                    "wir" to "gehen", 
                    "ihr" to "geht",
                    "sie/Sie" to "gehen"
                ),
                past = mapOf(
                    "ich" to "ging",
                    "du" to "gingst",
                    "er/sie/es" to "ging",
                    "wir" to "gingen",
                    "ihr" to "gingt", 
                    "sie/Sie" to "gingen"
                ),
                participle = Participle(
                    present = "gehend",
                    past = "gegangen"
                )
            ),
            
            "kommen" to VerbConjugations(
                present = mapOf(
                    "ich" to "komme",
                    "du" to "kommst",
                    "er/sie/es" to "kommt",
                    "wir" to "kommen",
                    "ihr" to "kommt",
                    "sie/Sie" to "kommen"
                ),
                past = mapOf(
                    "ich" to "kam",
                    "du" to "kamst", 
                    "er/sie/es" to "kam",
                    "wir" to "kamen",
                    "ihr" to "kamt",
                    "sie/Sie" to "kamen"
                ),
                participle = Participle(
                    present = "kommend",
                    past = "gekommen"
                )
            ),
            
            "haben" to VerbConjugations(
                present = mapOf(
                    "ich" to "habe",
                    "du" to "hast",
                    "er/sie/es" to "hat", 
                    "wir" to "haben",
                    "ihr" to "habt",
                    "sie/Sie" to "haben"
                ),
                past = mapOf(
                    "ich" to "hatte",
                    "du" to "hattest",
                    "er/sie/es" to "hatte",
                    "wir" to "hatten",
                    "ihr" to "hattet",
                    "sie/Sie" to "hatten"
                ),
                participle = Participle(
                    present = "habend",
                    past = "gehabt"
                )
            ),
            
            "sein" to VerbConjugations(
                present = mapOf(
                    "ich" to "bin",
                    "du" to "bist",
                    "er/sie/es" to "ist",
                    "wir" to "sind",
                    "ihr" to "seid", 
                    "sie/Sie" to "sind"
                ),
                past = mapOf(
                    "ich" to "war",
                    "du" to "warst",
                    "er/sie/es" to "war",
                    "wir" to "waren",
                    "ihr" to "wart",
                    "sie/Sie" to "waren"
                ),
                participle = Participle(
                    present = "seiend",
                    past = "gewesen"
                )
            ),
            
            "werden" to VerbConjugations(
                present = mapOf(
                    "ich" to "werde",
                    "du" to "wirst",
                    "er/sie/es" to "wird",
                    "wir" to "werden",
                    "ihr" to "werdet",
                    "sie/Sie" to "werden"
                ),
                past = mapOf(
                    "ich" to "wurde",
                    "du" to "wurdest",
                    "er/sie/es" to "wurde",
                    "wir" to "wurden",
                    "ihr" to "wurdet",
                    "sie/Sie" to "wurden"
                ),
                participle = Participle(
                    present = "werdend", 
                    past = "geworden"
                )
            ),
            
            "erreichen" to VerbConjugations(
                present = mapOf(
                    "ich" to "erreiche",
                    "du" to "erreichst",
                    "er/sie/es" to "erreicht",
                    "wir" to "erreichen",
                    "ihr" to "erreicht",
                    "sie/Sie" to "erreichen"
                ),
                past = mapOf(
                    "ich" to "erreichte",
                    "du" to "erreichtest",
                    "er/sie/es" to "erreichte",
                    "wir" to "erreichten",
                    "ihr" to "erreichtet",
                    "sie/Sie" to "erreichten"
                ),
                participle = Participle(
                    present = "erreichend",
                    past = "erreicht"
                )
            ),
            
            "verstehen" to VerbConjugations(
                present = mapOf(
                    "ich" to "verstehe",
                    "du" to "verstehst",
                    "er/sie/es" to "versteht",
                    "wir" to "verstehen",
                    "ihr" to "versteht",
                    "sie/Sie" to "verstehen"
                ),
                past = mapOf(
                    "ich" to "verstand",
                    "du" to "verstandest",
                    "er/sie/es" to "verstand",
                    "wir" to "verstanden",
                    "ihr" to "verstandet",
                    "sie/Sie" to "verstanden"
                ),
                participle = Participle(
                    present = "verstehend",
                    past = "verstanden"
                )
            ),
            
            "geben" to VerbConjugations(
                present = mapOf(
                    "ich" to "gebe",
                    "du" to "gibst",
                    "er/sie/es" to "gibt",
                    "wir" to "geben",
                    "ihr" to "gebt",
                    "sie/Sie" to "geben"
                ),
                past = mapOf(
                    "ich" to "gab",
                    "du" to "gabst",
                    "er/sie/es" to "gab",
                    "wir" to "gaben",
                    "ihr" to "gabt",
                    "sie/Sie" to "gaben"
                ),
                participle = Participle(
                    present = "gebend",
                    past = "gegeben"
                )
            ),
            
            "sehen" to VerbConjugations(
                present = mapOf(
                    "ich" to "sehe",
                    "du" to "siehst",
                    "er/sie/es" to "sieht",
                    "wir" to "sehen",
                    "ihr" to "seht",
                    "sie/Sie" to "sehen"
                ),
                past = mapOf(
                    "ich" to "sah",
                    "du" to "sahst",
                    "er/sie/es" to "sah",
                    "wir" to "sahen",
                    "ihr" to "saht",
                    "sie/Sie" to "sahen"
                ),
                participle = Participle(
                    present = "sehend",
                    past = "gesehen"
                )
            ),
            
            "finden" to VerbConjugations(
                present = mapOf(
                    "ich" to "finde",
                    "du" to "findest",
                    "er/sie/es" to "findet",
                    "wir" to "finden",
                    "ihr" to "findet",
                    "sie/Sie" to "finden"
                ),
                past = mapOf(
                    "ich" to "fand",
                    "du" to "fandest",
                    "er/sie/es" to "fand",
                    "wir" to "fanden",
                    "ihr" to "fandet",
                    "sie/Sie" to "fanden"
                ),
                participle = Participle(
                    present = "findend",
                    past = "gefunden"
                )
            ),
            
            "wissen" to VerbConjugations(
                present = mapOf(
                    "ich" to "weiß",
                    "du" to "weißt",
                    "er/sie/es" to "weiß",
                    "wir" to "wissen",
                    "ihr" to "wisst",
                    "sie/Sie" to "wissen"
                ),
                past = mapOf(
                    "ich" to "wusste",
                    "du" to "wusstest",
                    "er/sie/es" to "wusste",
                    "wir" to "wussten",
                    "ihr" to "wusstet",
                    "sie/Sie" to "wussten"
                ),
                participle = Participle(
                    present = "wissend",
                    past = "gewusst"
                )
            ),
            
            "denken" to VerbConjugations(
                present = mapOf(
                    "ich" to "denke",
                    "du" to "denkst",
                    "er/sie/es" to "denkt",
                    "wir" to "denken",
                    "ihr" to "denkt",
                    "sie/Sie" to "denken"
                ),
                past = mapOf(
                    "ich" to "dachte",
                    "du" to "dachtest",
                    "er/sie/es" to "dachte",
                    "wir" to "dachten",
                    "ihr" to "dachtet",
                    "sie/Sie" to "dachten"
                ),
                participle = Participle(
                    present = "denkend",
                    past = "gedacht"
                )
            ),
            
            "können" to VerbConjugations(
                present = mapOf(
                    "ich" to "kann",
                    "du" to "kannst",
                    "er/sie/es" to "kann",
                    "wir" to "können",
                    "ihr" to "könnt",
                    "sie/Sie" to "können"
                ),
                past = mapOf(
                    "ich" to "konnte",
                    "du" to "konntest",
                    "er/sie/es" to "konnte",
                    "wir" to "konnten",
                    "ihr" to "konntet",
                    "sie/Sie" to "konnten"
                ),
                participle = Participle(
                    present = "könnend",
                    past = "gekonnt"
                )
            )
        )
        
        private fun createRegularConjugation(stem: String, addEt: Boolean = false): VerbConjugations {
            val etSuffix = if (addEt) "et" else "t"
            return VerbConjugations(
                present = mapOf(
                    "ich" to "${stem}e",
                    "du" to "${stem}st",
                    "er/sie/es" to "${stem}$etSuffix",
                    "wir" to "${stem}en", 
                    "ihr" to "${stem}t",
                    "sie/Sie" to "${stem}en"
                ),
                past = mapOf(
                    "ich" to "${stem}te",
                    "du" to "${stem}test",
                    "er/sie/es" to "${stem}te",
                    "wir" to "${stem}ten",
                    "ihr" to "${stem}tet",
                    "sie/Sie" to "${stem}ten"
                ),
                participle = Participle(
                    present = "${stem}end",
                    past = "ge${stem}t"
                )
            )
        }
        
        /**
         * Get conjugation for a German verb
         */
        fun getConjugation(verb: String): VerbConjugations? {
            // First check if we have the exact verb
            COMMON_VERBS[verb.lowercase()]?.let { return it }
            
            // Try to conjugate regular verbs
            if (verb.endsWith("en") && verb.length > 3) {
                val stem = verb.dropLast(2)
                return createRegularConjugation(stem, addEt = stem.endsWith("t") || stem.endsWith("d"))
            }
            
            return null
        }
        
        /**
         * Check if a word is likely a verb based on endings and known verbs
         */
        fun isLikelyVerb(word: String): Boolean {
            val lowerWord = word.lowercase()
            
            // Check if it's in our known verbs
            if (COMMON_VERBS.containsKey(lowerWord)) {
                return true
            }
            
            // Common verb endings in German
            val verbEndings = listOf(
                "en", // infinitive ending (most common)
                "ern", // verbs like wandern
                "eln", // verbs like klingeln
                "ieren" // foreign verbs like studieren
            )
            
            for (ending in verbEndings) {
                if (lowerWord.endsWith(ending) && lowerWord.length > ending.length + 1) {
                    return true
                }
            }
            
            // Avoid calling verb API for obvious non-verbs
            val nonVerbEndings = listOf(
                "ung", "heit", "keit", "schaft", "tum", // noun endings
                "ig", "lich", "isch", "los", "voll", "bar", // adjective endings
                "lich", "weit", "mal" // adverb endings
            )
            
            for (ending in nonVerbEndings) {
                if (lowerWord.endsWith(ending)) {
                    return false
                }
            }
            
            return false // Default to false for unknown words
        }
    }
}
