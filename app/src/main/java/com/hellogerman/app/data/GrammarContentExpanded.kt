package com.hellogerman.app.data

import com.hellogerman.app.data.entities.Lesson
import com.google.gson.Gson

/**
 * Expanded Grammar Content Generator with comprehensive lessons for A1-C2
 * Fixes placeholder examples and adds detailed content for all levels
 */
object GrammarContentExpanded {
    
    private val gson = Gson()
    
    fun generateExpandedGrammarLessons(level: String): List<Lesson> {
        val lessons = mutableListOf<Lesson>()
        var order = 1
        
        fun create(title: String, description: String, content: GrammarContent) {
            lessons.add(
                Lesson(
                    title = title,
                    description = description,
                    level = level,
                    skill = "grammar",
                    content = gson.toJson(content),
                    orderIndex = order++,
                    isCompleted = false,
                    score = 0,
                    timeSpent = 0
                )
            )
        }
        
        // Generate comprehensive content for each level
        when (level) {
            "A1" -> generateA1Content(::create)
            "A2" -> generateA2Content(::create)
            "B1" -> generateB1Content(::create)
            "B2" -> generateB2Content(::create)
            "C1" -> generateC1Content(::create)
            "C2" -> generateC2Content(::create)
        }
        
        return lessons
    }
    
    private fun generateA1Content(create: (String, String, GrammarContent) -> Unit) {
        
        // Definite Articles
        create(
            "Bestimmte Artikel",
            "Der, die, das - definite articles with nouns",
            GrammarContent(
                topicKey = "a1_definite_articles",
                explanations = listOf(
                    "Der bestimmte Artikel zeigt das Genus (Geschlecht) des Nomens: der (maskulin), die (feminin), das (neutral)",
                    "Maskulin: der Mann, der Tisch, der Baum",
                    "Feminin: die Frau, die Lampe, die Blume", 
                    "Neutral: das Kind, das Haus, das Auto",
                    "Im Plural ist der Artikel immer 'die': die Männer, die Frauen, die Kinder"
                ),
                explanationsEn = listOf(
                    "The definite article shows the gender of the noun: der (masculine), die (feminine), das (neuter)",
                    "Masculine: der Mann (the man), der Tisch (the table), der Baum (the tree)",
                    "Feminine: die Frau (the woman), die Lampe (the lamp), die Blume (the flower)",
                    "Neuter: das Kind (the child), das Haus (the house), das Auto (the car)",
                    "In plural, the article is always 'die': die Männer, die Frauen, die Kinder"
                ),
                examples = listOf(
                    "Der Vater liest ein Buch. (The father reads a book.)",
                    "Die Mutter kocht das Essen. (The mother cooks the food.)",
                    "Das Baby schläft im Bett. (The baby sleeps in bed.)",
                    "Die Kinder spielen im Park. (The children play in the park.)",
                    "Der Hund ist sehr freundlich. (The dog is very friendly.)"
                ),
                quiz = listOf(
                    GrammarQuestion("___ Mann ist groß.", listOf("Der", "Die", "Das"), "Der", 10, "Choose the correct definite article for 'Mann' (masculine)."),
                    GrammarQuestion("___ Frau arbeitet hier.", listOf("Der", "Die", "Das"), "Die", 10, "Choose the correct definite article for 'Frau' (feminine)."),
                    GrammarQuestion("___ Haus ist neu.", listOf("Der", "Die", "Das"), "Das", 10, "Choose the correct definite article for 'Haus' (neuter)."),
                    GrammarQuestion("___ Kinder sind glücklich.", listOf("Der", "Die", "Das"), "Die", 10, "Plural nouns always take 'die'."),
                    GrammarQuestion("___ Auto fährt schnell.", listOf("Der", "Die", "Das"), "Das", 10, "Choose the correct definite article for 'Auto' (neuter).")
                )
            )
        )
        
        // Indefinite Articles  
        create(
            "Unbestimmte Artikel", 
            "Ein, eine, ein - indefinite articles",
            GrammarContent(
                topicKey = "a1_indefinite_articles",
                explanations = listOf(
                    "Der unbestimmte Artikel wird verwendet für unbekannte oder nicht spezifische Dinge",
                    "Maskulin: ein Mann, ein Tisch, ein Apfel",
                    "Feminin: eine Frau, eine Lampe, eine Orange",
                    "Neutral: ein Kind, ein Haus, ein Brot",
                    "Im Plural gibt es keinen unbestimmten Artikel (Nullartikel): Männer, Frauen, Kinder"
                ),
                explanationsEn = listOf(
                    "The indefinite article is used for unknown or non-specific things",
                    "Masculine: ein Mann (a man), ein Tisch (a table), ein Apfel (an apple)",
                    "Feminine: eine Frau (a woman), eine Lampe (a lamp), eine Orange (an orange)",
                    "Neuter: ein Kind (a child), ein Haus (a house), ein Brot (a bread)",
                    "In plural there is no indefinite article: Männer (men), Frauen (women), Kinder (children)"
                ),
                examples = listOf(
                    "Ich kaufe ein Auto. (I buy a car.)",
                    "Sie hat eine Katze. (She has a cat.)", 
                    "Wir brauchen ein Bett. (We need a bed.)",
                    "Er trinkt einen Kaffee. (He drinks a coffee.)",
                    "Kinder spielen gerne. (Children like to play.)"
                ),
                quiz = listOf(
                    GrammarQuestion("Ich kaufe ___ Apfel.", listOf("ein", "eine", "der"), "ein", 10, "Masculine 'Apfel' takes 'ein'."),
                    GrammarQuestion("Sie hat ___ Katze.", listOf("ein", "eine", "das"), "eine", 10, "Feminine 'Katze' takes 'eine'."),
                    GrammarQuestion("Wir brauchen ___ Bett.", listOf("ein", "eine", "der"), "ein", 10, "Neuter 'Bett' takes 'ein'."),
                    GrammarQuestion("Er trinkt ___ Milch.", listOf("ein", "eine", "die"), "eine", 10, "Feminine 'Milch' takes 'eine'."),
                    GrammarQuestion("Das ist ___ gutes Buch.", listOf("ein", "eine", "einen"), "ein", 10, "Neuter 'Buch' takes 'ein'.")
                )
            )
        )
        
        // Noun Gender Rules
        create(
            "Nomen Geschlecht",
            "Gender rules and patterns for German nouns",
            GrammarContent(
                topicKey = "a1_noun_gender",
                explanations = listOf(
                    "Maskulin sind oft: männliche Personen, Tage/Monate/Jahreszeiten, Wörter auf -er",
                    "Feminin sind oft: weibliche Personen, Wörter auf -e, -ung, -heit, -keit, -ion",
                    "Neutral sind oft: Diminutive (-chen, -lein), Infinitive als Nomen, junge Tiere",
                    "Viele Nomen müssen einfach gelernt werden - es gibt Ausnahmen!",
                    "Tipp: Lernen Sie immer Nomen mit Artikel: der Tisch, die Lampe, das Buch"
                ),
                explanationsEn = listOf(
                    "Masculine are often: male persons, days/months/seasons, words ending in -er",
                    "Feminine are often: female persons, words ending in -e, -ung, -heit, -keit, -ion",
                    "Neuter are often: diminutives (-chen, -lein), infinitives as nouns, young animals",
                    "Many nouns must simply be learned - there are exceptions!",
                    "Tip: Always learn nouns with their article: der Tisch, die Lampe, das Buch"
                ),
                examples = listOf(
                    "der Lehrer (teacher, m.) - masculine person",
                    "die Zeitung (newspaper) - ends in -ung, feminine",
                    "das Mädchen (girl) - ends in -chen, neuter diminutive",
                    "die Freiheit (freedom) - ends in -heit, feminine",
                    "das Kätzchen (kitten) - young animal + diminutive"
                ),
                quiz = listOf(
                    GrammarQuestion("___ Zeitung (newspaper)", listOf("der", "die", "das"), "die", 10, "Words ending in -ung are usually feminine."),
                    GrammarQuestion("___ Mädchen (girl)", listOf("der", "die", "das"), "das", 10, "Diminutives ending in -chen are neuter."),
                    GrammarQuestion("___ Lehrer (teacher, male)", listOf("der", "die", "das"), "der", 10, "Male persons are usually masculine."),
                    GrammarQuestion("___ Freiheit (freedom)", listOf("der", "die", "das"), "die", 10, "Words ending in -heit are feminine."),
                    GrammarQuestion("___ Nation (nation)", listOf("der", "die", "das"), "die", 10, "Words ending in -ion are feminine.")
                )
            )
        )
        
        // Plurals
        create(
            "Pluralbildung",
            "How to form plural nouns in German",
            GrammarContent(
                topicKey = "a1_plurals",
                explanations = listOf(
                    "Es gibt verschiedene Pluralendungen: -e, -er, -en/-n, -s, oder keine Endung",
                    "Viele maskuline/neutrale Nomen: +e (der Tag → die Tage, das Jahr → die Jahre)",
                    "Viele neutrale Einsilber: +er (das Kind → die Kinder, das Haus → die Häuser)",
                    "Viele feminine Nomen: +en oder +n (die Frau → die Frauen, die Lampe → die Lampen)",
                    "Fremdwörter oft: +s (das Auto → die Autos, das Foto → die Fotos)"
                ),
                explanationsEn = listOf(
                    "There are different plural endings: -e, -er, -en/-n, -s, or no ending",
                    "Many masculine/neuter nouns: +e (der Tag → die Tage, das Jahr → die Jahre)",
                    "Many neuter monosyllables: +er (das Kind → die Kinder, das Haus → die Häuser)",
                    "Many feminine nouns: +en or +n (die Frau → die Frauen, die Lampe → die Lampen)",
                    "Foreign words often: +s (das Auto → die Autos, das Foto → die Fotos)"
                ),
                examples = listOf(
                    "das Kind → die Kinder (child → children)",
                    "die Frau → die Frauen (woman → women)",
                    "der Tag → die Tage (day → days)",
                    "das Auto → die Autos (car → cars)",
                    "die Lampe → die Lampen (lamp → lamps)"
                ),
                quiz = listOf(
                    GrammarQuestion("Plural von 'Kind':", listOf("Kinder", "Kinds", "Kinden"), "Kinder", 10, "Kind → Kinder (common pattern for neuter monosyllables)"),
                    GrammarQuestion("Plural von 'Frau':", listOf("Fraue", "Frauen", "Fraus"), "Frauen", 10, "Frau → Frauen (feminine +en pattern)"),
                    GrammarQuestion("Plural von 'Auto':", listOf("Autos", "Autoe", "Auten"), "Autos", 10, "Auto → Autos (foreign word +s pattern)"),
                    GrammarQuestion("Plural von 'Tag':", listOf("Tage", "Tager", "Tagen"), "Tage", 10, "Tag → Tage (masculine +e pattern)"),
                    GrammarQuestion("Plural von 'Lampe':", listOf("Lampes", "Lampen", "Lamper"), "Lampen", 10, "Lampe → Lampen (feminine +n pattern)")
                )
            )
        )
        
        // Present Tense Verbs
        create(
            "Präsens",
            "Present tense of regular and irregular verbs",
            GrammarContent(
                topicKey = "a1_present_tense",
                explanations = listOf(
                    "Regelmäßige Verben: Stamm + Endung (ich spiele, du spielst, er spielt)",
                    "Endungen: ich -e, du -st, er/sie/es -t, wir -en, ihr -t, sie/Sie -en",
                    "Wichtige unregelmäßige Verben: sein (bin, bist, ist), haben (habe, hast, hat)",
                    "Vokalwechsel bei einigen Verben: fahren → du fährst, er fährt",
                    "Trennbare Verben: aufstehen → ich stehe auf, du stehst auf"
                ),
                explanationsEn = listOf(
                    "Regular verbs: stem + ending (ich spiele, du spielst, er spielt)",
                    "Endings: ich -e, du -st, er/sie/es -t, wir -en, ihr -t, sie/Sie -en",
                    "Important irregular verbs: sein (am, are, is), haben (have, has)",
                    "Vowel change in some verbs: fahren → du fährst, er fährt",
                    "Separable verbs: aufstehen → ich stehe auf, du stehst auf"
                ),
                examples = listOf(
                    "Ich spiele Tennis. (I play tennis.)",
                    "Du bist sehr nett. (You are very nice.)",
                    "Er hat einen Hund. (He has a dog.)",
                    "Wir fahren nach Berlin. (We drive to Berlin.)",
                    "Sie steht um 7 Uhr auf. (She gets up at 7 o'clock.)"
                ),
                quiz = listOf(
                    GrammarQuestion("Ich ___ Tennis.", listOf("spiele", "spielst", "spielt"), "spiele", 10, "First person singular: ich spiele"),
                    GrammarQuestion("Du ___ sehr nett.", listOf("bin", "bist", "ist"), "bist", 10, "Second person singular of 'sein': du bist"),
                    GrammarQuestion("Er ___ einen Hund.", listOf("habe", "hast", "hat"), "hat", 10, "Third person singular of 'haben': er hat"),
                    GrammarQuestion("Wir ___ nach Berlin.", listOf("fahre", "fährst", "fahren"), "fahren", 10, "First person plural: wir fahren"),
                    GrammarQuestion("Sie ___ um 7 Uhr ___.", listOf("steht...auf", "stehe...auf", "stehst...auf"), "steht...auf", 10, "Separable verb: sie steht auf")
                )
            )
        )
    }
    
    private fun generateA2Content(create: (String, String, GrammarContent) -> Unit) {
        
        // Accusative Case
        create(
            "Akkusativ",
            "The accusative case - direct objects",
            GrammarContent(
                topicKey = "a2_accusative",
                explanations = listOf(
                    "Der Akkusativ ist für das direkte Objekt (wen? was?)",
                    "Maskulin: den (der Mann → den Mann), einen (ein Mann → einen Mann)",
                    "Feminin und Neutral bleiben gleich: die/eine Frau, das/ein Kind",
                    "Artikel im Akkusativ: den, die, das / einen, eine, ein",
                    "Nach bestimmten Präpositionen: durch, für, gegen, ohne, um"
                ),
                explanationsEn = listOf(
                    "The accusative is for the direct object (whom? what?)",
                    "Masculine: den (der Mann → den Mann), einen (ein Mann → einen Mann)",
                    "Feminine and neuter stay the same: die/eine Frau, das/ein Kind",
                    "Articles in accusative: den, die, das / einen, eine, ein",
                    "After certain prepositions: durch, für, gegen, ohne, um"
                ),
                examples = listOf(
                    "Ich sehe den Mann. (I see the man.) - direct object",
                    "Sie kauft eine Zeitung. (She buys a newspaper.)",
                    "Wir essen das Brot. (We eat the bread.)",
                    "Er geht durch den Park. (He walks through the park.)",
                    "Das Geschenk ist für die Mutter. (The gift is for the mother.)"
                ),
                quiz = listOf(
                    GrammarQuestion("Ich sehe ___ Mann.", listOf("der", "den", "dem"), "den", 10, "Masculine direct object takes 'den'."),
                    GrammarQuestion("Sie kauft ___ Zeitung.", listOf("der", "die", "den"), "die", 10, "Feminine accusative stays 'die'."),
                    GrammarQuestion("Wir essen ___ Brot.", listOf("der", "die", "das"), "das", 10, "Neuter accusative stays 'das'."),
                    GrammarQuestion("Das ist für ___ Kinder.", listOf("der", "die", "den"), "die", 10, "Plural accusative is always 'die'."),
                    GrammarQuestion("Er hat ___ Hund.", listOf("ein", "einen", "einem"), "einen", 10, "Masculine indefinite accusative is 'einen'.")
                )
            )
        )
        
        // Dative Case
        create(
            "Dativ",
            "The dative case - indirect objects",
            GrammarContent(
                topicKey = "a2_dative",
                explanations = listOf(
                    "Der Dativ ist für das indirekte Objekt (wem?)",
                    "Maskulin: dem (der Mann → dem Mann), einem (ein Mann → einem Mann)",
                    "Feminin: der (die Frau → der Frau), einer (eine Frau → einer Frau)",
                    "Neutral: dem (das Kind → dem Kind), einem (ein Kind → einem Kind)",
                    "Nach bestimmten Präpositionen: aus, bei, mit, nach, seit, von, zu"
                ),
                explanationsEn = listOf(
                    "The dative is for the indirect object (to whom?)",
                    "Masculine: dem (der Mann → dem Mann), einem (ein Mann → einem Mann)",
                    "Feminine: der (die Frau → der Frau), einer (eine Frau → einer Frau)",
                    "Neuter: dem (das Kind → dem Kind), einem (ein Kind → einem Kind)",
                    "After certain prepositions: aus, bei, mit, nach, seit, von, zu"
                ),
                examples = listOf(
                    "Ich gebe dem Mann das Buch. (I give the man the book.)",
                    "Sie hilft der Frau. (She helps the woman.)",
                    "Wir spielen mit dem Kind. (We play with the child.)",
                    "Er kommt aus der Stadt. (He comes from the city.)",
                    "Nach dem Essen gehen wir. (After the meal we go.)"
                ),
                quiz = listOf(
                    GrammarQuestion("Ich gebe ___ Mann das Buch.", listOf("der", "den", "dem"), "dem", 10, "Masculine dative indirect object is 'dem'."),
                    GrammarQuestion("Sie hilft ___ Frau.", listOf("die", "der", "den"), "der", 10, "Feminine dative is 'der'."),
                    GrammarQuestion("Wir spielen mit ___ Kind.", listOf("das", "den", "dem"), "dem", 10, "Neuter dative is 'dem'."),
                    GrammarQuestion("Er kommt von ___ Schule.", listOf("die", "der", "den"), "der", 10, "Feminine dative after 'von' is 'der'."),
                    GrammarQuestion("Nach ___ Film gehen wir.", listOf("der", "den", "dem"), "dem", 10, "Masculine dative after 'nach' is 'dem'.")
                )
            )
        )
        
        // Modal Verbs
        create(
            "Modalverben",
            "Modal verbs: können, müssen, wollen, dürfen, sollen, mögen",
            GrammarContent(
                topicKey = "a2_modal_verbs",
                explanations = listOf(
                    "Modalverben drücken eine Modalität aus (Fähigkeit, Notwendigkeit, Wunsch)",
                    "können (can/to be able to): ich kann, du kannst, er kann",
                    "müssen (must/have to): ich muss, du musst, er muss",
                    "wollen (want to): ich will, du willst, er will",
                    "Das Vollverb steht im Infinitiv am Satzende: Ich kann schwimmen."
                ),
                explanationsEn = listOf(
                    "Modal verbs express modality (ability, necessity, wish)",
                    "können (can/to be able to): ich kann, du kannst, er kann",
                    "müssen (must/have to): ich muss, du musst, er muss", 
                    "wollen (want to): ich will, du willst, er will",
                    "The main verb stands in infinitive at the end: Ich kann schwimmen."
                ),
                examples = listOf(
                    "Ich kann Deutsch sprechen. (I can speak German.)",
                    "Du musst die Hausaufgaben machen. (You must do homework.)",
                    "Er will ins Kino gehen. (He wants to go to the cinema.)",
                    "Wir dürfen hier nicht rauchen. (We may not smoke here.)",
                    "Sie soll um 8 Uhr kommen. (She should come at 8 o'clock.)"
                ),
                quiz = listOf(
                    GrammarQuestion("Ich ___ Deutsch sprechen.", listOf("kann", "muss", "will"), "kann", 10, "'können' expresses ability."),
                    GrammarQuestion("Du ___ die Hausaufgaben machen.", listOf("kannst", "musst", "willst"), "musst", 10, "'müssen' expresses necessity."),
                    GrammarQuestion("Er ___ ins Kino gehen.", listOf("kann", "muss", "will"), "will", 10, "'wollen' expresses desire."),
                    GrammarQuestion("Wir ___ hier nicht rauchen.", listOf("können", "müssen", "dürfen"), "dürfen", 10, "'dürfen' (negative) expresses prohibition."),
                    GrammarQuestion("Sie ___ um 8 Uhr kommen.", listOf("kann", "muss", "soll"), "soll", 10, "'sollen' expresses obligation/suggestion.")
                )
            )
        )
        
        // Perfect Tense
        create(
            "Perfekt",
            "Perfect tense with haben and sein",
            GrammarContent(
                topicKey = "a2_perfect",
                explanations = listOf(
                    "Das Perfekt wird mit haben oder sein + Partizip II gebildet",
                    "Die meisten Verben mit haben: Ich habe gespielt, gegessen, gelernt",
                    "Bewegungsverben und sein/werden mit sein: Ich bin gegangen, gefahren",
                    "Partizip II: ge-...-t (regelmäßig), ge-...-en (unregelmäßig)",
                    "Trennbare Verben: aufgestanden, eingekauft"
                ),
                explanationsEn = listOf(
                    "Perfect tense is formed with haben or sein + past participle",
                    "Most verbs with haben: Ich habe gespielt, gegessen, gelernt",
                    "Motion verbs and sein/werden with sein: Ich bin gegangen, gefahren",
                    "Past participle: ge-...-t (regular), ge-...-en (irregular)",
                    "Separable verbs: aufgestanden, eingekauft"
                ),
                examples = listOf(
                    "Ich habe gestern gespielt. (I played yesterday.)",
                    "Sie ist nach Berlin gefahren. (She drove to Berlin.)",
                    "Wir haben Pizza gegessen. (We ate pizza.)",
                    "Er ist um 7 Uhr aufgestanden. (He got up at 7 o'clock.)",
                    "Habt ihr die Hausaufgaben gemacht? (Did you do the homework?)"
                ),
                quiz = listOf(
                    GrammarQuestion("Ich ___ gestern gespielt.", listOf("bin", "habe", "war"), "habe", 10, "Regular activities use 'haben'."),
                    GrammarQuestion("Sie ___ nach Berlin gefahren.", listOf("hat", "ist", "war"), "ist", 10, "Motion verbs use 'sein'."),
                    GrammarQuestion("Wir ___ Pizza ___.", listOf("haben...gegessen", "sind...gegessen", "haben...geessen"), "haben...gegessen", 10, "Eating uses 'haben' + gegessen."),
                    GrammarQuestion("Er ___ um 7 Uhr ___.", listOf("hat...aufgestanden", "ist...aufgestanden", "war...aufgestanden"), "ist...aufgestanden", 10, "Movement verb 'aufstehen' uses 'sein'."),
                    GrammarQuestion("___ ihr die Hausaufgaben ___?", listOf("Seid...gemacht", "Habt...gemacht", "Wart...gemacht"), "Habt...gemacht", 10, "Regular activity 'machen' uses 'haben'.")
                )
            )
        )
    }
    
    private fun generateB1Content(create: (String, String, GrammarContent) -> Unit) {
        
        // Relative Clauses
        create(
            "Relativsätze",
            "Relative clauses with der, die, das",
            GrammarContent(
                topicKey = "b1_relative_clauses",
                explanations = listOf(
                    "Relativsätze geben zusätzliche Informationen über ein Nomen",
                    "Sie beginnen mit Relativpronomen: der, die, das (je nach Genus und Kasus)",
                    "Der Kasus hängt von der Funktion im Relativsatz ab (Subjekt, Objekt)",
                    "Das Verb steht am Ende des Relativsatzes",
                    "Beispiel: Der Mann, der dort steht, ist mein Vater."
                ),
                explanationsEn = listOf(
                    "Relative clauses give additional information about a noun",
                    "They start with relative pronouns: der, die, das (depending on gender and case)",
                    "The case depends on the function in the relative clause (subject, object)",
                    "The verb stands at the end of the relative clause",
                    "Example: Der Mann, der dort steht, ist mein Vater."
                ),
                examples = listOf(
                    "Der Mann, der dort steht, ist mein Vater. (The man who stands there is my father.)",
                    "Die Frau, die ich kenne, ist Lehrerin. (The woman whom I know is a teacher.)",
                    "Das Buch, das auf dem Tisch liegt, ist interessant. (The book that lies on the table is interesting.)",
                    "Die Leute, mit denen wir sprechen, sind nett. (The people with whom we speak are nice.)",
                    "Das Haus, in dem wir wohnen, ist alt. (The house in which we live is old.)"
                ),
                quiz = listOf(
                    GrammarQuestion("Der Mann, ___ dort steht, ist mein Vater.", listOf("der", "den", "dem"), "der", 10, "Subject of relative clause (nominative)."),
                    GrammarQuestion("Die Frau, ___ ich kenne, ist Lehrerin.", listOf("der", "die", "den"), "die", 10, "Direct object of relative clause (accusative)."),
                    GrammarQuestion("Das Buch, ___ auf dem Tisch liegt, ist interessant.", listOf("der", "die", "das"), "das", 10, "Neuter subject (nominative)."),
                    GrammarQuestion("Die Leute, mit ___ wir sprechen, sind nett.", listOf("die", "denen", "deren"), "denen", 10, "Dative plural after preposition."),
                    GrammarQuestion("Das ist das Auto, ___ er gekauft hat.", listOf("der", "die", "das"), "das", 10, "Neuter direct object (accusative).")
                )
            )
        )
        
        // Adjective Declension
        create(
            "Adjektivdeklination",
            "Strong and weak adjective declension",
            GrammarContent(
                topicKey = "b1_adjective_declension",
                explanations = listOf(
                    "Adjektive vor Nomen werden dekliniert (bekommen Endungen)",
                    "Schwache Deklination nach bestimmtem Artikel: der große Mann, die schöne Frau",
                    "Starke Deklination ohne Artikel: großer Mann, schöne Frau, kleines Kind",
                    "Gemischte Deklination nach unbestimmtem Artikel: ein großer Mann, eine schöne Frau",
                    "Die Endung hängt von Genus, Numerus und Kasus ab"
                ),
                explanationsEn = listOf(
                    "Adjectives before nouns are declined (get endings)",
                    "Weak declension after definite article: der große Mann, die schöne Frau",
                    "Strong declension without article: großer Mann, schöne Frau, kleines Kind",
                    "Mixed declension after indefinite article: ein großer Mann, eine schöne Frau",
                    "The ending depends on gender, number and case"
                ),
                examples = listOf(
                    "der große Mann (the big man) - weak declension",
                    "ein großer Mann (a big man) - mixed declension", 
                    "großer Mann (big man) - strong declension",
                    "die schönen Blumen (the beautiful flowers)",
                    "mit dem kleinen Kind (with the small child)"
                ),
                quiz = listOf(
                    GrammarQuestion("Der ___ Mann ist hier.", listOf("groß", "große", "großer"), "große", 10, "Weak declension after 'der' (masculine nominative)."),
                    GrammarQuestion("Ein ___ Mann ist hier.", listOf("groß", "große", "großer"), "großer", 10, "Mixed declension after 'ein' (masculine nominative)."),
                    GrammarQuestion("Ich sehe den ___ Mann.", listOf("groß", "große", "großen"), "großen", 10, "Weak declension after 'den' (masculine accusative)."),
                    GrammarQuestion("___ Männer sind hier.", listOf("Groß", "Große", "Großer"), "Große", 10, "Strong declension without article (plural nominative)."),
                    GrammarQuestion("Die ___ Frau arbeitet.", listOf("schön", "schöne", "schöner"), "schöne", 10, "Weak declension after 'die' (feminine nominative).")
                )
            )
        )
        
        // Subjunctive II
        create(
            "Konjunktiv II",
            "Subjunctive II for hypothetical situations",
            GrammarContent(
                topicKey = "b1_subjunctive_ii",
                explanations = listOf(
                    "Der Konjunktiv II drückt Irreales, Wünsche und höfliche Bitten aus",
                    "Bildung: Präteritumstamm + Konjunktivendung (ich käme, du kämest)",
                    "Häufig mit würde + Infinitiv: Ich würde gehen, du würdest kommen",
                    "Höfliche Bitte: Könnten Sie mir helfen? Hätten Sie Zeit?",
                    "Irreale Bedingung: Wenn ich reich wäre, würde ich reisen."
                ),
                explanationsEn = listOf(
                    "Subjunctive II expresses unreal situations, wishes and polite requests",
                    "Formation: preterite stem + subjunctive ending (ich käme, du kämest)",
                    "Often with würde + infinitive: Ich würde gehen, du würdest kommen",
                    "Polite request: Könnten Sie mir helfen? Hätten Sie Zeit?",
                    "Unreal condition: Wenn ich reich wäre, würde ich reisen."
                ),
                examples = listOf(
                    "Wenn ich Zeit hätte, würde ich kommen. (If I had time, I would come.)",
                    "Könnten Sie mir bitte helfen? (Could you please help me?)",
                    "Ich wäre gerne reich. (I would like to be rich.)",
                    "Was würdest du machen? (What would you do?)",
                    "Wenn das Wetter schön wäre! (If only the weather were nice!)"
                ),
                quiz = listOf(
                    GrammarQuestion("Wenn ich Zeit ___, würde ich kommen.", listOf("habe", "hatte", "hätte"), "hätte", 10, "Subjunctive II of 'haben' is 'hätte'."),
                    GrammarQuestion("___ Sie mir bitte helfen?", listOf("Können", "Konnten", "Könnten"), "Könnten", 10, "Polite request with subjunctive II."),
                    GrammarQuestion("Ich ___ gerne reich.", listOf("bin", "war", "wäre"), "wäre", 10, "Subjunctive II of 'sein' is 'wäre'."),
                    GrammarQuestion("Was ___ du machen?", listOf("wirst", "würdest", "wolltest"), "würdest", 10, "Subjunctive II with 'würde' + infinitive."),
                    GrammarQuestion("Wenn ich reich ___, würde ich reisen.", listOf("bin", "war", "wäre"), "wäre", 10, "Unreal condition with subjunctive II.")
                )
            )
        )
    }
    
    private fun generateB2Content(create: (String, String, GrammarContent) -> Unit) {
        
        // Passive Voice
        create(
            "Passiv",
            "Passive voice - process and state passive",
            GrammarContent(
                topicKey = "b2_passive_voice",
                explanations = listOf(
                    "Das Passiv betont die Handlung, nicht den Handelnden",
                    "Vorgangspassiv: werden + Partizip II (Das Haus wird gebaut)",
                    "Zustandspassiv: sein + Partizip II (Das Haus ist gebaut)",
                    "Präsens: wird gemacht, Präteritum: wurde gemacht, Perfekt: ist gemacht worden",
                    "Der Handelnde kann mit 'von' (Person) oder 'durch' (Mittel) genannt werden"
                ),
                explanationsEn = listOf(
                    "The passive emphasizes the action, not the agent",
                    "Process passive: werden + past participle (Das Haus wird gebaut)",
                    "State passive: sein + past participle (Das Haus ist gebaut)",
                    "Present: wird gemacht, past: wurde gemacht, perfect: ist gemacht worden",
                    "The agent can be mentioned with 'von' (person) or 'durch' (means)"
                ),
                examples = listOf(
                    "Das Haus wird gebaut. (The house is being built.)",
                    "Der Brief wurde geschrieben. (The letter was written.)",
                    "Das Problem ist gelöst. (The problem is solved.)",
                    "Das Buch wird von einem berühmten Autor geschrieben. (The book is written by a famous author.)",
                    "Die Nachricht wurde durch Radio verbreitet. (The news was spread by radio.)"
                ),
                quiz = listOf(
                    GrammarQuestion("Das Haus ___ gebaut.", listOf("ist", "wird", "war"), "wird", 10, "Process passive in present tense."),
                    GrammarQuestion("Der Brief ___ geschrieben.", listOf("wird", "wurde", "ist"), "wurde", 10, "Process passive in past tense."),
                    GrammarQuestion("Das Problem ___ gelöst.", listOf("wird", "wurde", "ist"), "ist", 10, "State passive shows completed state."),
                    GrammarQuestion("Das Buch wird ___ einem Autor geschrieben.", listOf("von", "durch", "mit"), "von", 10, "'von' introduces personal agent."),
                    GrammarQuestion("Die Tür ___ geöffnet worden.", listOf("hat", "ist", "wird"), "ist", 10, "Perfect passive with 'ist...worden'.")
                )
            )
        )
        
        // Indirect Speech
        create(
            "Indirekte Rede",
            "Indirect speech with Subjunctive I",
            GrammarContent(
                topicKey = "b2_indirect_speech",
                explanations = listOf(
                    "Die indirekte Rede gibt fremde Äußerungen wieder",
                    "Konjunktiv I: er sei, er habe, er komme, er werde",
                    "Bei Formengleichheit mit Indikativ → Konjunktiv II: sie hätten, sie kämen",
                    "Einleitung oft mit: Er sagt, dass... / Er behauptet, dass...",
                    "In Zeitungen und formellen Texten sehr häufig"
                ),
                explanationsEn = listOf(
                    "Indirect speech reports what others have said",
                    "Subjunctive I: er sei, er habe, er komme, er werde",
                    "When forms are identical with indicative → Subjunctive II: sie hätten, sie kämen",
                    "Often introduced with: Er sagt, dass... / Er behauptet, dass...",
                    "Very common in newspapers and formal texts"
                ),
                examples = listOf(
                    "Er sagt, er sei müde. (He says he is tired.)",
                    "Sie behauptet, sie habe es nicht gesehen. (She claims she didn't see it.)",
                    "Der Minister erklärte, die Situation werde sich verbessern. (The minister explained the situation would improve.)",
                    "Die Zeitung berichtet, es seien viele Menschen gekommen. (The newspaper reports many people came.)",
                    "Er meinte, sie hätten recht. (He thought they were right.)"
                ),
                quiz = listOf(
                    GrammarQuestion("Er sagt, er ___ müde.", listOf("ist", "sei", "wäre"), "sei", 10, "Subjunctive I of 'sein': er sei."),
                    GrammarQuestion("Sie behauptet, sie ___ es nicht gesehen.", listOf("hat", "habe", "hätte"), "habe", 10, "Subjunctive I of 'haben': sie habe."),
                    GrammarQuestion("Er meinte, sie ___ recht.", listOf("haben", "hätten", "haben würden"), "hätten", 10, "Subjunctive II because of identical forms."),
                    GrammarQuestion("Der Minister erklärte, die Situation ___ sich verbessern.", listOf("wird", "werde", "würde"), "werde", 10, "Subjunctive I of 'werden': werde."),
                    GrammarQuestion("Sie sagte, sie ___ morgen.", listOf("kommt", "komme", "käme"), "komme", 10, "Subjunctive I of 'kommen': komme.")
                )
            )
        )
    }
    
    private fun generateC1Content(create: (String, String, GrammarContent) -> Unit) {
        
        // Extended Participle Constructions
        create(
            "Erweiterte Partizipialkonstruktionen",
            "Extended participial phrases for advanced expression",
            GrammarContent(
                topicKey = "c1_extended_participles",
                explanations = listOf(
                    "Partizipialkonstruktionen ersetzen Relativsätze und machen Texte kompakter",
                    "Partizip I (Aktiv): der kommende Zug = der Zug, der kommt",
                    "Partizip II (Passiv): das gelesene Buch = das Buch, das gelesen wurde",
                    "Erweiterte Konstruktionen: der von allen bewunderte Künstler",
                    "Häufig in formellen und wissenschaftlichen Texten"
                ),
                explanationsEn = listOf(
                    "Participial constructions replace relative clauses and make texts more compact",
                    "Present participle (active): der kommende Zug = der Zug, der kommt",
                    "Past participle (passive): das gelesene Buch = das Buch, das gelesen wurde",
                    "Extended constructions: der von allen bewunderte Künstler",
                    "Common in formal and academic texts"
                ),
                examples = listOf(
                    "der kommende Winter (the coming winter)",
                    "das geschriebene Wort (the written word)",
                    "der von allen bewunderte Künstler (the artist admired by all)",
                    "die vor kurzem veröffentlichte Studie (the recently published study)",
                    "das seit Jahren diskutierte Problem (the problem discussed for years)"
                ),
                quiz = listOf(
                    GrammarQuestion("Der ___ Zug hat Verspätung.", listOf("kommende", "gekommene", "ankommende"), "kommende", 10, "Present participle for ongoing action."),
                    GrammarQuestion("Das ___ Buch liegt auf dem Tisch.", listOf("lesende", "gelesene", "zu lesende"), "gelesene", 10, "Past participle for completed action."),
                    GrammarQuestion("Der von allen ___ Künstler...", listOf("bewundernde", "bewunderte", "zu bewundernde"), "bewunderte", 10, "Past participle in extended construction."),
                    GrammarQuestion("Die vor kurzem ___ Studie...", listOf("veröffentlichende", "veröffentlichte", "zu veröffentlichende"), "veröffentlichte", 10, "Past participle with time adverbial."),
                    GrammarQuestion("Das seit Jahren ___ Problem...", listOf("diskutierende", "diskutierte", "zu diskutierende"), "diskutierte", 10, "Past participle with duration adverbial.")
                )
            )
        )
        
        // Complex Sentence Structures
        create(
            "Komplexe Satzstrukturen",
            "Multiple subordinate clauses and sophisticated syntax",
            GrammarContent(
                topicKey = "c1_complex_sentences",
                explanations = listOf(
                    "Komplexe Sätze enthalten mehrere Nebensätze verschiedener Arten",
                    "Nebensätze können geschachtelt werden: Ich glaube, dass er sagt, dass...",
                    "Verschiedene Funktionen: Subjekt-, Objekt-, Adverbialsätze",
                    "Koordination und Subordination können kombiniert werden",
                    "Wichtig für gehobene schriftliche Kommunikation"
                ),
                explanationsEn = listOf(
                    "Complex sentences contain multiple subordinate clauses of different types",
                    "Subordinate clauses can be nested: Ich glaube, dass er sagt, dass...",
                    "Different functions: subject, object, adverbial clauses",
                    "Coordination and subordination can be combined",
                    "Important for sophisticated written communication"
                ),
                examples = listOf(
                    "Obwohl er wusste, dass es schwierig war, begann er das Projekt.",
                    "Nachdem sie erklärt hatte, warum sie nicht gekommen war, entschuldigte sie sich.",
                    "Es ist wichtig, dass wir verstehen, was hier passiert ist.",
                    "Der Grund, weshalb er ging, obwohl wir ihn gebeten hatten zu bleiben, bleibt unklar.",
                    "Damit du weißt, wovon ich spreche, erkläre ich dir, was geschehen ist."
                ),
                quiz = listOf(
                    GrammarQuestion("Obwohl er wusste, dass es schwierig war, ___ er das Projekt.", listOf("begann", "beginnt", "begonnen hat"), "begann", 10, "Main clause after subordinate clause."),
                    GrammarQuestion("Nachdem sie erklärt hatte, warum sie nicht gekommen war, ___ sie sich.", listOf("entschuldigt", "entschuldigte", "hat entschuldigt"), "entschuldigte", 10, "Past tense in main clause after pluperfect."),
                    GrammarQuestion("Es ist wichtig, dass wir ___, was hier passiert ist.", listOf("verstehen", "verstanden", "verständen"), "verstehen", 10, "Present tense in subordinate clause."),
                    GrammarQuestion("Der Grund, ___ er ging, bleibt unklar.", listOf("warum", "weshalb", "dass"), "weshalb", 10, "Relative adverb for reason."),
                    GrammarQuestion("Damit du weißt, wovon ich spreche, ___ ich dir, was geschehen ist.", listOf("erkläre", "erklärte", "erklärt habe"), "erkläre", 10, "Present tense in main clause.")
                )
            )
        )
    }
    
    private fun generateC2Content(create: (String, String, GrammarContent) -> Unit) {
        
        // Literary and Archaic Forms
        create(
            "Literarische und archaische Formen",
            "Literary devices and historical language forms",
            GrammarContent(
                topicKey = "c2_literary_forms",
                explanations = listOf(
                    "Gehobene und veraltete Sprachformen in Literatur und formellen Texten",
                    "Genetivus subjektivus/objektivus: die Furcht des Kindes (Kind fürchtet vs. jemand fürchtet Kind)",
                    "Alte Konjunktivformen: er komme für 'er würde kommen'",
                    "Substantivierte Infinitive: das Sein, das Werden, das Nichts",
                    "Archaische Wortstellung: Des Lebens Freude (statt: die Freude des Lebens)"
                ),
                explanationsEn = listOf(
                    "Elevated and archaic language forms in literature and formal texts",
                    "Subjective/objective genitive: die Furcht des Kindes (child fears vs. someone fears child)",
                    "Old subjunctive forms: er komme for 'er würde kommen'",
                    "Substantivized infinitives: das Sein, das Werden, das Nichts",
                    "Archaic word order: Des Lebens Freude (instead of: die Freude des Lebens)"
                ),
                examples = listOf(
                    "Die Liebe der Mutter (mother's love - subjective genitive)",
                    "Die Eroberung der Stadt (conquest of the city - objective genitive)",
                    "Man nehme (one should take - old subjunctive)",
                    "Das Sein und das Nichts (being and nothingness)",
                    "Des Menschen Wille ist sein Himmelreich (man's will is his kingdom)"
                ),
                quiz = listOf(
                    GrammarQuestion("'Die Furcht des Soldaten' ist ein ___ Genitiv, wenn der Soldat fürchtet.", listOf("subjektiver", "objektiver", "possessiver"), "subjektiver", 15, "The soldier is the subject of fearing."),
                    GrammarQuestion("'Die Eroberung der Stadt' zeigt einen ___ Genitiv.", listOf("subjektiven", "objektiven", "possessiven"), "objektiven", 15, "The city is the object being conquered."),
                    GrammarQuestion("'Man nehme drei Eier' ist ein ___ Konjunktiv.", listOf("alter", "neuer", "irrealer"), "alter", 15, "Old form instead of 'man soll nehmen'."),
                    GrammarQuestion("'Das Sein' ist ein ___ Infinitiv.", listOf("substantivierter", "erweiterter", "modaler"), "substantivierter", 15, "Infinitive used as noun."),
                    GrammarQuestion("'Des Lebens Freude' zeigt ___ Wortstellung.", listOf("moderne", "archaische", "umgangssprachliche"), "archaische", 15, "Old-fashioned genitive before noun.")
                )
            )
        )
        
        // Sophisticated Style
        create(
            "Gehobener Stil",
            "Sophisticated expression and register variation",
            GrammarContent(
                topicKey = "c2_sophisticated_style",
                explanations = listOf(
                    "Stilistische Mittel für gehobene und differenzierte Ausdrucksweise",
                    "Nominalisierung: das Versprechen geben (statt: versprechen)",
                    "Funktionsverbgefüge: zur Diskussion stellen (statt: diskutieren)",
                    "Passivumschreibungen: es lässt sich sagen (statt: man kann sagen)",
                    "Euphemismen und verhüllende Ausdrücke für sensible Themen"
                ),
                explanationsEn = listOf(
                    "Stylistic devices for elevated and differentiated expression",
                    "Nominalization: das Versprechen geben (instead of: versprechen)",
                    "Functional verb phrases: zur Diskussion stellen (instead of: diskutieren)",
                    "Passive paraphrases: es lässt sich sagen (instead of: man kann sagen)",
                    "Euphemisms and veiled expressions for sensitive topics"
                ),
                examples = listOf(
                    "zur Diskussion stellen (to put up for discussion)",
                    "in Betracht ziehen (to take into consideration)",
                    "Es lässt sich nicht leugnen, dass... (It cannot be denied that...)",
                    "das Ableben (demise - euphemism for death)",
                    "eine gewisse Unzulänglichkeit (a certain inadequacy)"
                ),
                quiz = listOf(
                    GrammarQuestion("'Zur Diskussion stellen' ist ein ___.", listOf("Funktionsverbgefüge", "Modalverb", "Hilfsverb"), "Funktionsverbgefüge", 15, "Functional verb phrase replacing simple verb."),
                    GrammarQuestion("'Es lässt sich sagen' ist eine ___.", listOf("Passivumschreibung", "Modalverbkonstruktion", "Reflexivkonstruktion"), "Passivumschreibung", 15, "Passive paraphrase with 'lassen'."),
                    GrammarQuestion("'Das Ableben' ist ein ___ für 'sterben'.", listOf("Euphemismus", "Synonym", "Fremdwort"), "Euphemismus", 15, "Veiled, polite expression for death."),
                    GrammarQuestion("'In Betracht ziehen' bedeutet ___.", listOf("erwägen", "betrachten", "anschauen"), "erwägen", 15, "Formal expression for 'consider'."),
                    GrammarQuestion("'Eine gewisse Unzulänglichkeit' ist ___ Ausdrucksweise.", listOf("gehobene", "umgangssprachliche", "technische"), "gehobene", 15, "Sophisticated, formal expression.")
                )
            )
        )
    }
}
