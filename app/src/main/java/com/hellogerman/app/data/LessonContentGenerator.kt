package com.hellogerman.app.data

import android.content.Context
import com.google.gson.Gson
import com.hellogerman.app.data.entities.*
import com.hellogerman.app.data.repository.OfflineCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LessonContentGenerator {

    private val gson = Gson()
    private var offlineCacheManager: OfflineCacheManager? = null

    /**
     * Initialize with offline cache manager for better performance
     */
    fun initializeCacheManager(context: Context) {
        offlineCacheManager = OfflineCacheManager(context)
    }
    
    fun generateAllLessons(): List<Lesson> {
        val lessons = mutableListOf<Lesson>()

        // Generate lessons for each level and skill
        val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val skills = listOf("lesen", "hoeren", "schreiben", "sprechen", "grammar")

        levels.forEach { level ->
            skills.forEach { skill ->
                val skillLessons = generateLessonsForSkillAndLevel(skill, level)
                // Add visual elements to lessons
                val lessonsWithVisuals = skillLessons.map { lesson ->
                    lesson.copy(
                        illustrationResId = getIllustrationForLesson(lesson),
                        characterResId = getCharacterForLesson(lesson),
                        animationType = getAnimationForLesson(lesson)
                    )
                }
                lessons.addAll(lessonsWithVisuals)
            }
        }

        // Cache the generated lessons asynchronously for better performance
        // Note: Caching will be done when lessons are first accessed

        return lessons
    }

    /**
     * Generate lessons with caching support
     */
    suspend fun generateAllLessonsWithCache(): List<Lesson> = withContext(Dispatchers.IO) {
        // Try to load from cache first
        val cachedLessons = loadCachedLessons()
        if (cachedLessons.isNotEmpty()) {
            return@withContext cachedLessons
        }

        // Generate new lessons if cache is empty
        val lessons = mutableListOf<Lesson>()

        // Generate lessons for each level and skill
        val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val skills = listOf("lesen", "hoeren", "schreiben", "sprechen", "grammar")

        levels.forEach { level ->
            skills.forEach { skill ->
                val skillLessons = generateLessonsForSkillAndLevel(skill, level)
                // Add visual elements to lessons
                val lessonsWithVisuals = skillLessons.map { lesson ->
                    lesson.copy(
                        illustrationResId = getIllustrationForLesson(lesson),
                        characterResId = getCharacterForLesson(lesson),
                        animationType = getAnimationForLesson(lesson)
                    )
                }
                lessons.addAll(lessonsWithVisuals)
            }
        }

        // Cache the generated lessons
        cacheGeneratedLessons(lessons)

        return@withContext lessons
    }

    /**
     * Cache generated lessons for offline use
     */
    private suspend fun cacheGeneratedLessons(lessons: List<Lesson>) {
        offlineCacheManager?.let { cacheManager ->
            try {
                val lessonsJson = gson.toJson(lessons)
                val success = cacheManager.compressAndCacheLessonData(-1, lessonsJson) // Use -1 for all lessons
                if (success) {
                    android.util.Log.d("LessonGenerator", "Successfully cached ${lessons.size} lessons")
                }
            } catch (e: Exception) {
                android.util.Log.e("LessonGenerator", "Failed to cache lessons", e)
            }
        }
    }

    /**
     * Load cached lessons
     */
    private suspend fun loadCachedLessons(): List<Lesson> {
        return offlineCacheManager?.let { cacheManager ->
            try {
                val cachedJson = cacheManager.getCachedLessonData(-1) // Use -1 for all lessons
                cachedJson?.let { json ->
                    val lessonType = object : com.google.gson.reflect.TypeToken<List<Lesson>>() {}.type
                    gson.fromJson(json, lessonType) ?: emptyList()
                } ?: emptyList()
            } catch (e: Exception) {
                android.util.Log.e("LessonGenerator", "Failed to load cached lessons", e)
                emptyList()
            }
        } ?: emptyList()
    }
    
    private fun generateLessonsForSkillAndLevel(skill: String, level: String): List<Lesson> {
        return when (skill) {
            "lesen" -> generateLesenLessons(level)
            "hoeren" -> generateHoerenLessons(level)
            "schreiben" -> generateSchreibenLessons(level)
            "sprechen" -> generateSprechenLessons(level)
            "grammar" -> GrammarContentExpanded.generateExpandedGrammarLessons(level)
            else -> emptyList()
        }
    }
    
    private fun generateLesenLessons(level: String): List<Lesson> {
        val lessons = mutableListOf<Lesson>()
        
        when (level) {
            "A1" -> {
                lessons.add(createLesenLesson(
                    title = "Meine Familie",
                    description = "Learn about family members",
                    level = level,
                    orderIndex = 1,
                    text = "Hallo! Ich heiße Anna. Das ist meine Familie. Mein Vater heißt Thomas. Meine Mutter heißt Maria. Ich habe einen Bruder. Er heißt Peter. Peter ist 15 Jahre alt. Ich bin 12 Jahre alt. Wir wohnen in Berlin.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wie heißt die Hauptperson?",
                            options = listOf("Anna", "Maria", "Peter", "Thomas"),
                            correctAnswer = "Anna",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is the main person's name?",
                            optionsEnglish = listOf("Anna", "Maria", "Peter", "Thomas")
                        ),
                        Question(
                            id = 2,
                            question = "Wie alt ist Peter?",
                            options = listOf("12", "15", "20", "25"),
                            correctAnswer = "15",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How old is Peter?",
                            optionsEnglish = listOf("12", "15", "20", "25")
                        ),
                        Question(
                            id = 3,
                            question = "Wo wohnt die Familie?",
                            options = null,
                            correctAnswer = "Berlin",
                            correctAnswers = null,
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "Where does the family live?"
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Familie", "family", "Das ist meine Familie."),
                        VocabularyItem("Bruder", "brother", "Ich habe einen Bruder."),
                        VocabularyItem("wohnen", "to live", "Wir wohnen in Berlin.")
                    )
                ))
                
                lessons.add(createLesenLesson(
                    title = "Im Supermarkt",
                    description = "Shopping vocabulary and phrases",
                    level = level,
                    orderIndex = 2,
                    text = "Ich gehe in den Supermarkt. Ich kaufe Brot, Milch und Äpfel. Das Brot kostet 2 Euro. Die Milch kostet 1 Euro. Die Äpfel kosten 3 Euro. Ich bezahle mit Karte.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was kauft die Person?",
                            options = listOf("Brot, Milch, Äpfel", "Fleisch, Fisch", "Gemüse", "Süßigkeiten"),
                            correctAnswer = "Brot, Milch, Äpfel",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the person buy?",
                            optionsEnglish = listOf("Bread, milk, apples", "Meat, fish", "Vegetables", "Sweets")
                        ),
                        Question(
                            id = 2,
                            question = "Wie bezahlt die Person?",
                            options = listOf("Mit Bargeld", "Mit Karte", "Mit Scheck", "Online"),
                            correctAnswer = "Mit Karte",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How does the person pay?",
                            optionsEnglish = listOf("With cash", "With card", "With check", "Online")
                        ),
                        Question(
                            id = 3,
                            question = "Wie viel kosten die Äpfel?",
                            options = null,
                            correctAnswer = "3 Euro",
                            correctAnswers = null,
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How much do the apples cost?"
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Supermarkt", "supermarket", "Ich gehe in den Supermarkt."),
                        VocabularyItem("kaufen", "to buy", "Ich kaufe Brot."),
                        VocabularyItem("bezahlen", "to pay", "Ich bezahle mit Karte.")
                    )
                ))
                
                lessons.add(createLesenLesson(
                    title = "Mein Haustier",
                    description = "Learn about pets and animals",
                    level = level,
                    orderIndex = 3,
                    text = "Ich habe einen Hund. Er heißt Bello und ist 3 Jahre alt. Bello ist braun und sehr freundlich. Er mag es, im Park zu spielen. Jeden Tag gehe ich mit ihm spazieren. Bello kann gut apportieren und ist sehr klug. Meine Nachbarn mögen ihn auch.",
                    questions = listOf(
                        Question(id = 1, question = "Was für ein Haustier hat die Person?", options = listOf("Katze", "Hund", "Hamster", "Vogel"), correctAnswer = "Hund", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What kind of pet does the person have?", optionsEnglish = listOf("Cat", "Dog", "Hamster", "Bird")),
                        Question(id = 2, question = "Wie alt ist das Haustier?", options = listOf("2 Jahre", "3 Jahre", "4 Jahre", "5 Jahre"), correctAnswer = "3 Jahre", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How old is the pet?", optionsEnglish = listOf("2 years", "3 years", "4 years", "5 years")),
                        Question(id = 3, question = "Was kann das Haustier gut?", options = null, correctAnswer = "apportieren", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What can the pet do well?")
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Haustier", "pet", "Ich habe ein Haustier."),
                        VocabularyItem("Hund", "dog", "Das ist mein Hund."),
                        VocabularyItem("spazieren", "to walk", "Ich gehe spazieren.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "Meine Schule",
                    description = "School and education vocabulary",
                    level = level,
                    orderIndex = 4,
                    text = "Meine Schule ist groß und schön. Es gibt 20 Klassenzimmer und eine Bibliothek. Die Lehrer sind nett und helfen uns. Ich habe viele Freunde in der Schule. Wir lernen Deutsch, Mathematik und Englisch. In der Pause spielen wir Fußball. Die Schule beginnt um 8 Uhr und endet um 13 Uhr.",
                    questions = listOf(
                        Question(id = 1, question = "Wie viele Klassenzimmer gibt es?", options = listOf("15", "20", "25", "30"), correctAnswer = "20", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How many classrooms are there?", optionsEnglish = listOf("15", "20", "25", "30")),
                        Question(id = 2, question = "Wann beginnt die Schule?", options = listOf("7 Uhr", "8 Uhr", "9 Uhr", "10 Uhr"), correctAnswer = "8 Uhr", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "When does school start?", optionsEnglish = listOf("7 o'clock", "8 o'clock", "9 o'clock", "10 o'clock")),
                        Question(id = 3, question = "Was spielen sie in der Pause?", options = null, correctAnswer = "Fußball", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What do they play during break?")
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Schule", "school", "Das ist meine Schule."),
                        VocabularyItem("Klassenzimmer", "classroom", "Das Klassenzimmer ist groß."),
                        VocabularyItem("Pause", "break", "In der Pause spielen wir.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "Meine Lieblingsfarbe",
                    description = "Colors and preferences",
                    level = level,
                    orderIndex = 5,
                    text = "Meine Lieblingsfarbe ist Blau. Ich mag alle Blautöne: hellblau, dunkelblau und türkis. Meine Kleidung ist oft blau. Mein Zimmer ist auch blau gestrichen. Blau erinnert mich an das Meer und den Himmel. Meine Freunde sagen, dass Blau zu mir passt. Ich mag auch Grün, aber Blau ist meine Lieblingsfarbe.",
                    questions = listOf(
                        Question(id = 1, question = "Was ist die Lieblingsfarbe?", options = listOf("Rot", "Blau", "Grün", "Gelb"), correctAnswer = "Blau", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What is the favorite color?", optionsEnglish = listOf("Red", "Blue", "Green", "Yellow")),
                        Question(id = 2, question = "Woran erinnert Blau die Person?", options = listOf("An Blumen", "An das Meer und den Himmel", "An Essen", "An Musik"), correctAnswer = "An das Meer und den Himmel", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does blue remind the person of?", optionsEnglish = listOf("Flowers", "The sea and sky", "Food", "Music")),
                        Question(id = 3, question = "Welche Farbe mag die Person auch?", options = null, correctAnswer = "Grün", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What other color does the person like?")
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Farbe", "color", "Das ist eine schöne Farbe."),
                        VocabularyItem("blau", "blue", "Blau ist meine Lieblingsfarbe."),
                        VocabularyItem("Lieblingsfarbe", "favorite color", "Was ist deine Lieblingsfarbe?")
                    )
                ))

                // Goethe-Zertifikat A1 Lesen - Additional Lessons (6-20)
                lessons.add(createLesenLesson(
                    title = "Treffen mit Freunden",
                    description = "Meeting friends and making plans - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 6,
                    text = "Hallo Anna! Wie geht es dir? Gut, danke. Was machst du am Wochenende? Ich gehe mit meinen Freunden ins Kino. Wir sehen uns einen Film an. Welchen Film magst du? Ich mag Komödien. Und du? Ich mag Actionfilme. Wann treffen wir uns? Um 19 Uhr vor dem Kino. Okay, bis dann!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo treffen sich die Freunde?",
                            options = listOf("Im Café", "Im Kino", "Im Park", "Zu Hause"),
                            correctAnswer = "Im Kino",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where are the friends meeting?",
                            optionsEnglish = listOf("At the café", "At the cinema", "In the park", "At home")
                        ),
                        Question(
                            id = 2,
                            question = "Welche Filmart mag Anna?",
                            options = listOf("Komödien", "Actionfilme", "Horrorfilme", "Liebesfilme"),
                            correctAnswer = "Komödien",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What type of movie does Anna like?",
                            optionsEnglish = listOf("Comedies", "Action movies", "Horror movies", "Romance movies")
                        ),
                        Question(
                            id = 3,
                            question = "Um wie viel Uhr treffen sie sich?",
                            options = null,
                            correctAnswer = "19 Uhr",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "At what time are they meeting?"
                        ),
                        Question(
                            id = 4,
                            question = "Was machen sie zuerst?",
                            options = listOf("Essen gehen", "Sich treffen", "Den Film sehen", "Nach Hause gehen"),
                            correctAnswer = "Sich treffen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What do they do first?",
                            optionsEnglish = listOf("Go eat", "Meet", "Watch the movie", "Go home")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("treffen", "to meet", "Wir treffen uns um 19 Uhr."),
                        VocabularyItem("Kino", "cinema", "Wir gehen ins Kino."),
                        VocabularyItem("Film", "movie", "Welchen Film siehst du?"),
                        VocabularyItem("Komödie", "comedy", "Eine Komödie ist lustig.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Im Restaurant bestellen",
                    description = "Ordering food at a restaurant - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 7,
                    text = "Guten Abend! Ein Tisch für zwei Personen, bitte. Natürlich, hier entlang. Hier ist die Speisekarte. Danke. Was möchten Sie trinken? Ich nehme ein Wasser. Und Sie? Ein Bier, bitte. Als Vorspeise nehme ich die Suppe. Und als Hauptgang? Das Schnitzel mit Pommes. Das klingt gut. Ich nehme den Salat und die Pizza.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Für wie viele Personen ist der Tisch?",
                            options = listOf("1", "2", "3", "4"),
                            correctAnswer = "2",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "For how many people is the table?",
                            optionsEnglish = listOf("1", "2", "3", "4")
                        ),
                        Question(
                            id = 2,
                            question = "Was bestellt die erste Person zu trinken?",
                            options = listOf("Wasser", "Bier", "Wein", "Saft"),
                            correctAnswer = "Wasser",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the first person order to drink?",
                            optionsEnglish = listOf("Water", "Beer", "Wine", "Juice")
                        ),
                        Question(
                            id = 3,
                            question = "Was nimmt die zweite Person als Hauptgang?",
                            options = null,
                            correctAnswer = "Pizza",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What does the second person take as main course?"
                        ),
                        Question(
                            id = 4,
                            question = "Was ist eine Vorspeise?",
                            options = listOf("Hauptgang", "Nachspeise", "Erster Gang", "Getränk"),
                            correctAnswer = "Erster Gang",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is a starter?",
                            optionsEnglish = listOf("Main course", "Dessert", "First course", "Drink")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("bestellen", "to order", "Ich bestelle das Essen."),
                        VocabularyItem("Speisekarte", "menu", "Hier ist die Speisekarte."),
                        VocabularyItem("Vorspeise", "starter", "Die Suppe ist die Vorspeise."),
                        VocabularyItem("Hauptgang", "main course", "Was nimmst du als Hauptgang?")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Arbeiten im Büro",
                    description = "Working in an office - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 8,
                    text = "Herr Müller arbeitet in einem Büro. Sein Schreibtisch ist groß und ordentlich. Er hat einen Computer, einen Telefon und viele Papiere. Morgens kommt er um 8 Uhr. Er trinkt zuerst einen Kaffee. Dann liest er seine E-Mails. Nachmittags hat er Meetings. Er spricht mit Kollegen über Projekte. Abends geht er um 17 Uhr nach Hause. Er arbeitet hart, aber er mag seinen Job.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo arbeitet Herr Müller?",
                            options = listOf("Zu Hause", "Im Büro", "Im Geschäft", "In der Schule"),
                            correctAnswer = "Im Büro",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where does Mr. Müller work?",
                            optionsEnglish = listOf("At home", "In the office", "In the shop", "At school")
                        ),
                        Question(
                            id = 2,
                            question = "Wann kommt Herr Müller morgens?",
                            options = listOf("7 Uhr", "8 Uhr", "9 Uhr", "10 Uhr"),
                            correctAnswer = "8 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When does Mr. Müller come in the morning?",
                            optionsEnglish = listOf("7 o'clock", "8 o'clock", "9 o'clock", "10 o'clock")
                        ),
                        Question(
                            id = 3,
                            question = "Was macht Herr Müller nachmittags?",
                            options = null,
                            correctAnswer = "Meetings",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What does Mr. Müller do in the afternoon?"
                        ),
                        Question(
                            id = 4,
                            question = "Wie findet Herr Müller seine Arbeit?",
                            options = listOf("Schwierig", "Langweilig", "Gut", "Stressig"),
                            correctAnswer = "Gut",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How does Mr. Müller find his work?",
                            optionsEnglish = listOf("Difficult", "Boring", "Good", "Stressful")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("arbeiten", "to work", "Ich arbeite im Büro."),
                        VocabularyItem("Schreibtisch", "desk", "Mein Schreibtisch ist groß."),
                        VocabularyItem("Computer", "computer", "Ich arbeite am Computer."),
                        VocabularyItem("Meeting", "meeting", "Wir haben ein Meeting.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Einkaufen gehen",
                    description = "Going shopping - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 9,
                    text = "Lisa geht einkaufen. Sie braucht Milch, Brot und Obst. Zuerst geht sie in den Supermarkt. Dort kauft sie die Milch für 1,50 Euro. Dann geht sie zur Bäckerei und kauft zwei Brötchen für 1 Euro. Im Obstladen kauft sie Äpfel und Bananen. Alles zusammen kostet 8 Euro. Sie bezahlt mit ihrer Karte. Zu Hause packt sie alles in den Kühlschrank.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was braucht Lisa zuerst?",
                            options = listOf("Milch", "Brot", "Obst", "Fleisch"),
                            correctAnswer = "Milch",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does Lisa need first?",
                            optionsEnglish = listOf("Milk", "Bread", "Fruit", "Meat")
                        ),
                        Question(
                            id = 2,
                            question = "Wo kauft sie das Brot?",
                            options = listOf("Supermarkt", "Bäckerei", "Obstladen", "Metzgerei"),
                            correctAnswer = "Bäckerei",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where does she buy the bread?",
                            optionsEnglish = listOf("Supermarket", "Bakery", "Fruit shop", "Butcher")
                        ),
                        Question(
                            id = 3,
                            question = "Wie viel kosten die Brötchen?",
                            options = null,
                            correctAnswer = "1 Euro",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How much do the rolls cost?"
                        ),
                        Question(
                            id = 4,
                            question = "Wie bezahlt Lisa?",
                            options = listOf("Bar", "Karte", "Scheck", "Online"),
                            correctAnswer = "Karte",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How does Lisa pay?",
                            optionsEnglish = listOf("Cash", "Card", "Check", "Online")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("einkaufen", "to shop", "Ich gehe einkaufen."),
                        VocabularyItem("Supermarkt", "supermarket", "Im Supermarkt gibt es alles."),
                        VocabularyItem("Bäckerei", "bakery", "Die Bäckerei hat frisches Brot."),
                        VocabularyItem("bezahlen", "to pay", "Ich bezahle mit Karte.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Am Bahnhof",
                    description = "At the train station - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 10,
                    text = "Entschuldigung, wo ist der Bahnhof? Gehen Sie geradeaus und dann links. Danke. Wann fährt der nächste Zug nach Berlin? In 15 Minuten von Gleis 3. Wie viel kostet die Fahrkarte? 25 Euro einfach. Hin und zurück? Nein, nur einfach. Hier ist Ihr Ticket. Der Zug hat Verspätung. Wann kommt er? In 10 Minuten. Danke für die Information.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo findet das Gespräch statt?",
                            options = listOf("Am Flughafen", "Am Bahnhof", "Am Busbahnhof", "Am Hafen"),
                            correctAnswer = "Am Bahnhof",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where does the conversation take place?",
                            optionsEnglish = listOf("At the airport", "At the train station", "At the bus station", "At the harbor")
                        ),
                        Question(
                            id = 2,
                            question = "Von welchem Gleis fährt der Zug?",
                            options = listOf("Gleis 1", "Gleis 2", "Gleis 3", "Gleis 4"),
                            correctAnswer = "Gleis 3",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "From which platform does the train depart?",
                            optionsEnglish = listOf("Platform 1", "Platform 2", "Platform 3", "Platform 4")
                        ),
                        Question(
                            id = 3,
                            question = "Wie viel kostet die Fahrkarte?",
                            options = null,
                            correctAnswer = "25 Euro",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How much does the ticket cost?"
                        ),
                        Question(
                            id = 4,
                            question = "Wann kommt der Zug?",
                            options = listOf("In 5 Minuten", "In 10 Minuten", "In 15 Minuten", "Jetzt"),
                            correctAnswer = "In 10 Minuten",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When does the train arrive?",
                            optionsEnglish = listOf("In 5 minutes", "In 10 minutes", "In 15 minutes", "Now")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Bahnhof", "train station", "Der Bahnhof ist groß."),
                        VocabularyItem("Gleis", "platform", "Der Zug fährt von Gleis 3."),
                        VocabularyItem("Fahrkarte", "ticket", "Die Fahrkarte kostet 25 Euro."),
                        VocabularyItem("Verspätung", "delay", "Der Zug hat Verspätung.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Bei der Arbeit",
                    description = "At work conversations - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 11,
                    text = "Guten Morgen, Frau Schmidt. Guten Morgen, Herr Bauer. Wie war Ihr Wochenende? Sehr schön, danke. Ich war im Park mit meiner Familie. Und Sie? Ich habe zu Hause gearbeitet. Haben Sie die Berichte fertig? Ja, sie sind auf Ihrem Schreibtisch. Danke. Können Sie heute länger bleiben? Wir haben ein wichtiges Meeting. Natürlich, kein Problem.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo war Herr Bauer am Wochenende?",
                            options = listOf("Im Büro", "Im Park", "Zu Hause", "Im Kino"),
                            correctAnswer = "Im Park",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where was Mr. Bauer on the weekend?",
                            optionsEnglish = listOf("At the office", "In the park", "At home", "At the cinema")
                        ),
                        Question(
                            id = 2,
                            question = "Wo sind die Berichte?",
                            options = listOf("Auf dem Boden", "Auf dem Schreibtisch", "Im Schrank", "In der Tasche"),
                            correctAnswer = "Auf dem Schreibtisch",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where are the reports?",
                            optionsEnglish = listOf("On the floor", "On the desk", "In the cabinet", "In the bag")
                        ),
                        Question(
                            id = 3,
                            question = "Was hat Frau Schmidt zu Hause gemacht?",
                            options = null,
                            correctAnswer = "gearbeitet",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What did Ms. Schmidt do at home?"
                        ),
                        Question(
                            id = 4,
                            question = "Was haben sie heute?",
                            options = listOf("Ein Meeting", "Eine Party", "Ein Picknick", "Ein Konzert"),
                            correctAnswer = "Ein Meeting",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What do they have today?",
                            optionsEnglish = listOf("A meeting", "A party", "A picnic", "A concert")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Wochenende", "weekend", "Wie war Ihr Wochenende?"),
                        VocabularyItem("Bericht", "report", "Der Bericht ist fertig."),
                        VocabularyItem("arbeiten", "to work", "Ich arbeite zu Hause."),
                        VocabularyItem("Meeting", "meeting", "Wir haben ein Meeting.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Im Hotel",
                    description = "Hotel check-in conversation - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 12,
                    text = "Guten Tag, haben Sie ein Zimmer frei? Für wie viele Nächte? Für drei Nächte. Ja, wir haben ein Doppelzimmer. Wie viel kostet es? 80 Euro pro Nacht. Frühstück inklusive? Ja, Frühstück ist im Preis. Hier ist mein Ausweis. Danke. Ihre Zimmernummer ist 205. Das Frühstück gibt es von 7 bis 10 Uhr. Danke schön. Gute Reise!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wie viele Nächte bleibt der Gast?",
                            options = listOf("1", "2", "3", "4"),
                            correctAnswer = "3",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How many nights is the guest staying?",
                            optionsEnglish = listOf("1", "2", "3", "4")
                        ),
                        Question(
                            id = 2,
                            question = "Was für ein Zimmer bekommt der Gast?",
                            options = listOf("Einzelzimmer", "Doppelzimmer", "Suite", "Appartement"),
                            correctAnswer = "Doppelzimmer",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What kind of room does the guest get?",
                            optionsEnglish = listOf("Single room", "Double room", "Suite", "Apartment")
                        ),
                        Question(
                            id = 3,
                            question = "Wie viel kostet das Zimmer pro Nacht?",
                            options = null,
                            correctAnswer = "80 Euro",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How much does the room cost per night?"
                        ),
                        Question(
                            id = 4,
                            question = "Wann gibt es Frühstück?",
                            options = listOf("6-9 Uhr", "7-10 Uhr", "8-11 Uhr", "9-12 Uhr"),
                            correctAnswer = "7-10 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When is breakfast served?",
                            optionsEnglish = listOf("6-9 o'clock", "7-10 o'clock", "8-11 o'clock", "9-12 o'clock")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Hotel", "hotel", "Ich wohne im Hotel."),
                        VocabularyItem("Zimmer", "room", "Das Zimmer ist schön."),
                        VocabularyItem("Ausweis", "ID", "Hier ist mein Ausweis."),
                        VocabularyItem("Frühstück", "breakfast", "Das Frühstück ist gut.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Beim Arzt",
                    description = "Doctor visit conversation - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 13,
                    text = "Guten Tag, was fehlt Ihnen? Ich habe Halsschmerzen und Husten. Seit wann? Seit drei Tagen. Haben Sie Fieber? Ja, ein bisschen. Öffnen Sie den Mund. Sagen Sie 'Aaa'. Ihre Mandeln sind rot. Sie haben eine Erkältung. Ich gebe Ihnen Medikamente. Nehmen Sie diese Tabletten dreimal täglich. Trinken Sie viel Tee. Kommen Sie in einer Woche wieder.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was hat der Patient?",
                            options = listOf("Halsschmerzen", "Bauchschmerzen", "Kopfschmerzen", "Rückenschmerzen"),
                            correctAnswer = "Halsschmerzen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the patient have?",
                            optionsEnglish = listOf("Sore throat", "Stomach ache", "Headache", "Back pain")
                        ),
                        Question(
                            id = 2,
                            question = "Wie hoch ist das Fieber?",
                            options = listOf("37,5 Grad", "38,0 Grad", "38,5 Grad", "39,0 Grad"),
                            correctAnswer = "38,5 Grad",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How high is the fever?",
                            optionsEnglish = listOf("37.5 degrees", "38.0 degrees", "38.5 degrees", "39.0 degrees")
                        ),
                        Question(
                            id = 3,
                            question = "Wie oft soll der Patient die Tabletten nehmen?",
                            options = null,
                            correctAnswer = "dreimal täglich",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How often should the patient take the tablets?"
                        ),
                        Question(
                            id = 4,
                            question = "Wann soll der Patient wiederkommen?",
                            options = listOf("Morgen", "In einer Woche", "In zwei Wochen", "Nächsten Monat"),
                            correctAnswer = "In einer Woche",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When should the patient come back?",
                            optionsEnglish = listOf("Tomorrow", "In a week", "In two weeks", "Next month")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Halsschmerzen", "sore throat", "Ich habe Halsschmerzen."),
                        VocabularyItem("Fieber", "fever", "Ich habe 38 Grad Fieber."),
                        VocabularyItem("erkältet", "having a cold", "Ich bin erkältet."),
                        VocabularyItem("Medikamente", "medicine", "Nehmen Sie diese Medikamente.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Im Urlaub",
                    description = "Holiday conversations - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 14,
                    text = "Wohin fahren Sie in Urlaub? Nach Spanien, ans Meer. Wie lange bleiben Sie? Zwei Wochen. Fliegen Sie? Nein, wir fahren mit dem Auto. Das dauert länger, aber es ist billiger. Was machen Sie dort? Wir gehen an den Strand, schwimmen und essen Tapas. Klingt schön! Haben Sie schon gebucht? Ja, das Hotel und den Flug. Viel Spaß im Urlaub!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wohin fährt die Person in Urlaub?",
                            options = listOf("Nach Italien", "Nach Spanien", "Nach Frankreich", "Nach Portugal"),
                            correctAnswer = "Nach Spanien",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where is the person going on vacation?",
                            optionsEnglish = listOf("To Italy", "To Spain", "To France", "To Portugal")
                        ),
                        Question(
                            id = 2,
                            question = "Wie reist die Person?",
                            options = listOf("Mit dem Flugzeug", "Mit dem Auto", "Mit dem Zug", "Mit dem Bus"),
                            correctAnswer = "Mit dem Auto",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How is the person traveling?",
                            optionsEnglish = listOf("By plane", "By car", "By train", "By bus")
                        ),
                        Question(
                            id = 3,
                            question = "Wie lange bleibt die Person?",
                            options = null,
                            correctAnswer = "zwei Wochen",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How long is the person staying?"
                        ),
                        Question(
                            id = 4,
                            question = "Was macht die Person im Urlaub?",
                            options = listOf("Wandern", "Schwimmen", "Ski fahren", "Stadt besichtigen"),
                            correctAnswer = "Schwimmen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the person do on vacation?",
                            optionsEnglish = listOf("Hiking", "Swimming", "Skiing", "Sightseeing")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Urlaub", "vacation", "Ich fahre in Urlaub."),
                        VocabularyItem("Meer", "sea", "Ans Meer fahren."),
                        VocabularyItem("Strand", "beach", "Am Strand liegen."),
                        VocabularyItem("schwimmen", "to swim", "Ich gehe schwimmen.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Im Fitnessstudio",
                    description = "Gym conversation - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 15,
                    text = "Hallo, ich möchte Mitglied werden. Guten Tag! Füllen Sie bitte dieses Formular aus. Name, Adresse, Telefon. Haben Sie einen Ausweis dabei? Ja, hier ist mein Personalausweis. Wie oft kommen Sie? Dreimal pro Woche. Was für Kurse gibt es? Aerobic, Yoga und Krafttraining. Ich interessiere mich für Yoga. Der Kurs ist montags und mittwochs. Die Mitgliedschaft kostet 30 Euro im Monat.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was muss die Person ausfüllen?",
                            options = listOf("Einen Vertrag", "Ein Formular", "Eine Liste", "Ein Antrag"),
                            correctAnswer = "Ein Formular",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the person have to fill out?",
                            optionsEnglish = listOf("A contract", "A form", "A list", "An application")
                        ),
                        Question(
                            id = 2,
                            question = "Wie oft kommt die Person?",
                            options = listOf("Einmal pro Woche", "Zweimal pro Woche", "Dreimal pro Woche", "Täglich"),
                            correctAnswer = "Dreimal pro Woche",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How often does the person come?",
                            optionsEnglish = listOf("Once a week", "Twice a week", "Three times a week", "Daily")
                        ),
                        Question(
                            id = 3,
                            question = "Wann ist der Yoga-Kurs?",
                            options = null,
                            correctAnswer = "montags und mittwochs",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "When is the yoga class?"
                        ),
                        Question(
                            id = 4,
                            question = "Wie viel kostet die Mitgliedschaft?",
                            options = listOf("20 Euro", "25 Euro", "30 Euro", "35 Euro"),
                            correctAnswer = "30 Euro",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How much does the membership cost?",
                            optionsEnglish = listOf("20 Euro", "25 Euro", "30 Euro", "35 Euro")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Fitnessstudio", "gym", "Ich gehe ins Fitnessstudio."),
                        VocabularyItem("Mitglied", "member", "Ich werde Mitglied."),
                        VocabularyItem("Kurs", "course", "Der Yoga-Kurs ist gut."),
                        VocabularyItem("trainieren", "to train", "Ich trainiere dreimal pro Woche.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Beim Friseur",
                    description = "Hair salon conversation - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 16,
                    text = "Guten Tag, was möchten Sie? Ich möchte die Haare schneiden lassen. Wie kurz? Bis zu den Schultern, bitte. Waschen wir zuerst die Haare. Ja, gerne. Möchten Sie eine Spülung? Nein, danke. Jetzt föhne ich Ihre Haare. Wie gefällt es Ihnen? Sehr gut, danke. Das macht 25 Euro. Hier ist 30 Euro. Danke, 5 Euro zurück. Auf Wiedersehen!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was möchte die Kundin?",
                            options = listOf("Haare waschen", "Haare schneiden", "Haare färben", "Haare stylen"),
                            correctAnswer = "Haare schneiden",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the customer want?",
                            optionsEnglish = listOf("Hair wash", "Hair cut", "Hair color", "Hair styling")
                        ),
                        Question(
                            id = 2,
                            question = "Wie kurz sollen die Haare sein?",
                            options = listOf("Sehr kurz", "Bis zum Hals", "Bis zu den Schultern", "Lang"),
                            correctAnswer = "Bis zu den Schultern",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How short should the hair be?",
                            optionsEnglish = listOf("Very short", "To the neck", "To the shoulders", "Long")
                        ),
                        Question(
                            id = 3,
                            question = "Wie viel kostet der Haarschnitt?",
                            options = null,
                            correctAnswer = "25 Euro",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How much does the haircut cost?"
                        ),
                        Question(
                            id = 4,
                            question = "Was gibt die Kundin?",
                            options = listOf("20 Euro", "25 Euro", "30 Euro", "35 Euro"),
                            correctAnswer = "30 Euro",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the customer give?",
                            optionsEnglish = listOf("20 Euro", "25 Euro", "30 Euro", "35 Euro")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Friseur", "hairdresser", "Ich gehe zum Friseur."),
                        VocabularyItem("Haare schneiden", "to cut hair", "Die Haare schneiden lassen."),
                        VocabularyItem("föhnen", "to blow dry", "Die Haare föhnen."),
                        VocabularyItem("Spülung", "rinse", "Eine Spülung machen.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Im Park",
                    description = "Park activities - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 17,
                    text = "Es ist schön draußen. Lass uns in den Park gehen. Gute Idee! Was machen wir dort? Wir können spazieren gehen oder picknicken. Ich habe einen Ball dabei. Wir können Fußball spielen. Toll! Dort sind Bänke und Bäume. Die Blumen sind schön. Hörst du die Vögel? Ja, sie singen schön. Es gibt auch einen Spielplatz für Kinder. Sie haben Spaß.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo gehen die Personen hin?",
                            options = listOf("Ins Kino", "In den Park", "Ins Café", "Nach Hause"),
                            correctAnswer = "In den Park",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where are the people going?",
                            optionsEnglish = listOf("To the cinema", "To the park", "To the café", "Home")
                        ),
                        Question(
                            id = 2,
                            question = "Was haben sie dabei?",
                            options = listOf("Einen Ball", "Ein Buch", "Musik", "Essen"),
                            correctAnswer = "Einen Ball",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What do they have with them?",
                            optionsEnglish = listOf("A ball", "A book", "Music", "Food")
                        ),
                        Question(
                            id = 3,
                            question = "Was können sie im Park machen?",
                            options = null,
                            correctAnswer = "Fußball spielen",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What can they do in the park?"
                        ),
                        Question(
                            id = 4,
                            question = "Was gibt es für Kinder?",
                            options = listOf("Einen Zoo", "Einen Spielplatz", "Ein Schwimmbad", "Ein Museum"),
                            correctAnswer = "Einen Spielplatz",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is there for children?",
                            optionsEnglish = listOf("A zoo", "A playground", "A swimming pool", "A museum")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Park", "park", "Wir gehen in den Park."),
                        VocabularyItem("spazieren", "to stroll", "Spazieren gehen."),
                        VocabularyItem("Ball", "ball", "Wir spielen mit dem Ball."),
                        VocabularyItem("Spielplatz", "playground", "Der Spielplatz ist für Kinder.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Beim Zahnarzt",
                    description = "Dentist visit - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 18,
                    text = "Guten Tag, was fehlt Ihnen? Ich habe Zahnschmerzen. Seit wann? Seit gestern Abend. Öffnen Sie bitte den Mund. Welcher Zahn tut weh? Der hier rechts oben. Sie haben ein Loch. Ich muss bohren und füllen. Tut das weh? Nein, Sie bekommen eine Spritze. Danach putzen Sie die Zähne zweimal täglich. In zwei Wochen kommen Sie zur Kontrolle. Danke, auf Wiedersehen!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was hat der Patient?",
                            options = listOf("Halsschmerzen", "Zahnschmerzen", "Kopfschmerzen", "Bauchschmerzen"),
                            correctAnswer = "Zahnschmerzen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the patient have?",
                            optionsEnglish = listOf("Sore throat", "Toothache", "Headache", "Stomach ache")
                        ),
                        Question(
                            id = 2,
                            question = "Seit wann hat er Schmerzen?",
                            options = listOf("Seit heute Morgen", "Seit gestern Abend", "Seit zwei Tagen", "Seit einer Woche"),
                            correctAnswer = "Seit gestern Abend",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Since when does he have pain?",
                            optionsEnglish = listOf("Since this morning", "Since yesterday evening", "For two days", "For a week")
                        ),
                        Question(
                            id = 3,
                            question = "Was muss der Zahnarzt machen?",
                            options = null,
                            correctAnswer = "bohren und füllen",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What does the dentist have to do?"
                        ),
                        Question(
                            id = 4,
                            question = "Wann kommt der Patient wieder?",
                            options = listOf("Morgen", "In einer Woche", "In zwei Wochen", "Nächsten Monat"),
                            correctAnswer = "In zwei Wochen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When does the patient come back?",
                            optionsEnglish = listOf("Tomorrow", "In a week", "In two weeks", "Next month")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Zahnarzt", "dentist", "Ich gehe zum Zahnarzt."),
                        VocabularyItem("Zahnschmerzen", "toothache", "Ich habe Zahnschmerzen."),
                        VocabularyItem("bohren", "to drill", "Der Zahnarzt bohrt."),
                        VocabularyItem("füllen", "to fill", "Den Zahn füllen.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Im Schwimmbad",
                    description = "Swimming pool activities - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 19,
                    text = "Das Schwimmbad ist heute voll. Lass uns eine Bahn schwimmen. Gute Idee! Das Wasser ist warm. Ich schwimme 20 Bahnen. Danach gehen wir in die Sauna. Die Sauna ist sehr heiß. Wir bleiben nur 10 Minuten. Schau, dort ist ein Sprungturm. Traust du dich zu springen? Nein, ich habe Angst. Ich bleibe im Becken. Die Kinder spielen im Planschbecken. Sie haben Spaß.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo sind die Personen?",
                            options = listOf("Im Meer", "Im Schwimmbad", "Im See", "Im Fluss"),
                            correctAnswer = "Im Schwimmbad",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where are the people?",
                            optionsEnglish = listOf("In the sea", "In the swimming pool", "In the lake", "In the river")
                        ),
                        Question(
                            id = 2,
                            question = "Wie viele Bahnen schwimmt die Person?",
                            options = listOf("10", "15", "20", "25"),
                            correctAnswer = "20",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How many laps does the person swim?",
                            optionsEnglish = listOf("10", "15", "20", "25")
                        ),
                        Question(
                            id = 3,
                            question = "Wohin gehen sie nach dem Schwimmen?",
                            options = null,
                            correctAnswer = "Sauna",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "Where do they go after swimming?"
                        ),
                        Question(
                            id = 4,
                            question = "Was traut sich die Person nicht?",
                            options = listOf("Schwimmen", "Springen", "Tauchen", "Spielen"),
                            correctAnswer = "Springen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What doesn't the person dare to do?",
                            optionsEnglish = listOf("Swimming", "Jumping", "Diving", "Playing")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Schwimmbad", "swimming pool", "Das Schwimmbad ist groß."),
                        VocabularyItem("schwimmen", "to swim", "Ich schwimme 20 Bahnen."),
                        VocabularyItem("Sauna", "sauna", "Die Sauna ist heiß."),
                        VocabularyItem("Sprungturm", "diving tower", "Vom Sprungturm springen.")
                    ),
                    source = "Goethe"
                ))

                lessons.add(createLesenLesson(
                    title = "Telefonieren",
                    description = "Phone conversations - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 20,
                    text = "Hallo, hier ist Anna. Wer ist da? Hallo Anna, hier ist Thomas. Wie geht es dir? Danke, gut. Und dir? Auch gut. Was machst du heute Abend? Ich gehe ins Kino mit Freunden. Hast du Lust mitzukommen? Ja, gerne! Wann und wo treffen wir uns? Um 19 Uhr vor dem Kino. Okay, bis dann! Tschüs! Auf Wiederhören!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wer ruft an?",
                            options = listOf("Anna", "Thomas", "Maria", "Peter"),
                            correctAnswer = "Thomas",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Who is calling?",
                            optionsEnglish = listOf("Anna", "Thomas", "Maria", "Peter")
                        ),
                        Question(
                            id = 2,
                            question = "Was macht Anna heute Abend?",
                            options = listOf("Zu Hause bleiben", "Ins Kino gehen", "Essen gehen", "Sport machen"),
                            correctAnswer = "Ins Kino gehen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is Anna doing tonight?",
                            optionsEnglish = listOf("Stay at home", "Go to the cinema", "Go out to eat", "Do sports")
                        ),
                        Question(
                            id = 3,
                            question = "Um wie viel Uhr treffen sie sich?",
                            options = null,
                            correctAnswer = "19 Uhr",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "At what time are they meeting?"
                        ),
                        Question(
                            id = 4,
                            question = "Wo treffen sie sich?",
                            options = listOf("Vor dem Kino", "Im Kino", "Im Café", "Zu Hause"),
                            correctAnswer = "Vor dem Kino",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where are they meeting?",
                            optionsEnglish = listOf("In front of the cinema", "In the cinema", "At the café", "At home")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("telefonieren", "to phone", "Ich telefoniere mit Freunden."),
                        VocabularyItem("anrufen", "to call", "Wer ruft an?"),
                        VocabularyItem("Lust haben", "to feel like", "Hast du Lust?"),
                        VocabularyItem("treffen", "to meet", "Wir treffen uns um 19 Uhr.")
                    ),
                    source = "Goethe"
                ))

                // TELC Deutsch A1 Lesen - Lessons (21-23)
                lessons.add(createLesenLesson(
                    title = "Anzeigen verstehen",
                    description = "Understanding advertisements - TELC Lesen Teil 1",
                    level = level,
                    orderIndex = 21,
                    text = "GESUCHT: Babysitter für 2 Kinder (3 und 5 Jahre). Zeit: Nachmittags, 14-18 Uhr. Bezahlung: 8 Euro/Stunde. Erfahrung nötig. Tel: 030-1234567\n\nZIMMER ZU VERMIETEN: Zentral gelegen, 25 qm, möbliert. Miete: 350 Euro + Nebenkosten. Ab sofort. Kontakt: wohnung@berlin.de\n\nKURSANGEBOT: Deutsch lernen für Anfänger. 2x pro Woche, 10 Wochen. Kosten: 150 Euro. Beginn: 15. März. Info: sprachschule@deutsch-lernen.de",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was sucht die Anzeige?",
                            options = listOf("Einen Lehrer", "Einen Babysitter", "Eine Wohnung", "Einen Kurs"),
                            correctAnswer = "Einen Babysitter",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is the advertisement looking for?",
                            optionsEnglish = listOf("A teacher", "A babysitter", "An apartment", "A course")
                        ),
                        Question(
                            id = 2,
                            question = "Wie viel kostet das Zimmer im Monat?",
                            options = listOf("250 Euro", "350 Euro", "450 Euro", "550 Euro"),
                            correctAnswer = "350 Euro",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How much does the room cost per month?",
                            optionsEnglish = listOf("250 Euro", "350 Euro", "450 Euro", "550 Euro")
                        ),
                        Question(
                            id = 3,
                            question = "Wann beginnt der Sprachkurs?",
                            options = null,
                            correctAnswer = "15. März",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "When does the language course start?"
                        ),
                        Question(
                            id = 4,
                            question = "Wie oft findet der Deutschkurs statt?",
                            options = listOf("1x pro Woche", "2x pro Woche", "3x pro Woche", "Täglich"),
                            correctAnswer = "2x pro Woche",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How often does the German course take place?",
                            optionsEnglish = listOf("Once a week", "Twice a week", "Three times a week", "Daily")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Anzeige", "advertisement", "Die Anzeige steht in der Zeitung."),
                        VocabularyItem("vermieten", "to rent out", "Das Zimmer wird vermietet."),
                        VocabularyItem("möbliert", "furnished", "Die Wohnung ist möbliert."),
                        VocabularyItem("Nebenkosten", "additional costs", "Nebenkosten sind extra.")
                    ),
                    source = "TELC"
                ))

                lessons.add(createLesenLesson(
                    title = "Formulare ausfüllen",
                    description = "Filling out forms - TELC Lesen Teil 2",
                    level = level,
                    orderIndex = 22,
                    text = "ANMELDUNG BEI DER BIBLIOTHEK\n\nName: ________________________\nVorname: _____________________\nAdresse: ______________________\nTelefon: ______________________\nE-Mail: _______________________\n\nAusweisnummer: ________________\nGeburtsdatum: _________________\nBeruf: ________________________\n\nUnterschrift: __________________\nDatum: _______________________\n\nMitgliedsbeitrag: 20 Euro/Jahr\nÖffnungszeiten: Mo-Fr 9-19 Uhr, Sa 9-14 Uhr",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was muss man für die Anmeldung bezahlen?",
                            options = listOf("10 Euro", "20 Euro", "30 Euro", "50 Euro"),
                            correctAnswer = "20 Euro",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What do you have to pay for registration?",
                            optionsEnglish = listOf("10 Euro", "20 Euro", "30 Euro", "50 Euro")
                        ),
                        Question(
                            id = 2,
                            question = "Wann ist die Bibliothek samstags geöffnet?",
                            options = listOf("9-14 Uhr", "9-17 Uhr", "10-15 Uhr", "10-16 Uhr"),
                            correctAnswer = "9-14 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When is the library open on Saturdays?",
                            optionsEnglish = listOf("9-14 o'clock", "9-17 o'clock", "10-15 o'clock", "10-16 o'clock")
                        ),
                        Question(
                            id = 3,
                            question = "Was braucht man für die Anmeldung?",
                            options = null,
                            correctAnswer = "Ausweisnummer",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What do you need for registration?"
                        ),
                        Question(
                            id = 4,
                            question = "Wann ist die Bibliothek montags geöffnet?",
                            options = listOf("8-18 Uhr", "9-19 Uhr", "10-20 Uhr", "11-21 Uhr"),
                            correctAnswer = "9-19 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When is the library open on Mondays?",
                            optionsEnglish = listOf("8-18 o'clock", "9-19 o'clock", "10-20 o'clock", "11-21 o'clock")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Anmeldung", "registration", "Die Anmeldung ist einfach."),
                        VocabularyItem("Bibliothek", "library", "Die Bibliothek hat viele Bücher."),
                        VocabularyItem("Mitgliedsbeitrag", "membership fee", "Der Mitgliedsbeitrag ist 20 Euro."),
                        VocabularyItem("Öffnungszeiten", "opening hours", "Die Öffnungszeiten sind von 9-19 Uhr.")
                    ),
                    source = "TELC"
                ))

                lessons.add(createLesenLesson(
                    title = "Kurze Texte verstehen",
                    description = "Understanding short texts - TELC Lesen Teil 3",
                    level = level,
                    orderIndex = 23,
                    text = "Liebe Anna,\n\nvielen Dank für deine Einladung zur Party! Ich komme gerne. Kann ich etwas mitbringen? Vielleicht einen Salat oder Kuchen?\n\nBeste Grüße\nMaria\n\n---\n\nHallo Herr Müller,\n\nich bin morgen krank und kann nicht zur Arbeit kommen. Ich schicke Ihnen eine Krankmeldung vom Arzt.\n\nMit freundlichen Grüßen\nFrau Schmidt\n\n---\n\nSehr geehrte Damen und Herren,\n\nich interessiere mich für Ihr Zimmerangebot. Kann ich es morgen ansehen? Bitte rufen Sie mich an: 0171-9876543.\n\nMit freundlichen Grüßen\nMax Wagner",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was möchte Maria mitbringen?",
                            options = listOf("Nichts", "Einen Salat oder Kuchen", "Getränke", "Musik"),
                            correctAnswer = "Einen Salat oder Kuchen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does Maria want to bring?",
                            optionsEnglish = listOf("Nothing", "A salad or cake", "Drinks", "Music")
                        ),
                        Question(
                            id = 2,
                            question = "Warum kann Frau Schmidt nicht arbeiten?",
                            options = listOf("Sie ist im Urlaub", "Sie ist krank", "Sie hat einen Termin", "Sie arbeitet zu Hause"),
                            correctAnswer = "Sie ist krank",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Why can't Ms. Schmidt work?",
                            optionsEnglish = listOf("She is on vacation", "She is sick", "She has an appointment", "She works from home")
                        ),
                        Question(
                            id = 3,
                            question = "Was möchte Max ansehen?",
                            options = null,
                            correctAnswer = "Zimmerangebot",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What does Max want to see?"
                        ),
                        Question(
                            id = 4,
                            question = "Wann möchte Max das Zimmer ansehen?",
                            options = listOf("Heute", "Morgen", "Übermorgen", "Nächste Woche"),
                            correctAnswer = "Morgen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When does Max want to see the room?",
                            optionsEnglish = listOf("Today", "Tomorrow", "The day after tomorrow", "Next week")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Einladung", "invitation", "Die Einladung kam per E-Mail."),
                        VocabularyItem("mitbringen", "to bring along", "Ich bringe einen Kuchen mit."),
                        VocabularyItem("Krankmeldung", "sick note", "Die Krankmeldung ist vom Arzt."),
                        VocabularyItem("interessieren", "to interest", "Das Zimmer interessiert mich.")
                    ),
                    source = "TELC"
                ))

                // ÖSD Zertifikat A1 Lesen - Lessons (33-35)
                lessons.add(createLesenLesson(
                    title = "Öffentliche Verkehrsmittel",
                    description = "Public transportation - ÖSD Lesen Teil 1",
                    level = level,
                    orderIndex = 33,
                    text = "INFORMATION: Ab Montag, 15. März, ändern sich die Fahrpläne der Straßenbahnlinien 1, 2 und 3. Die Straßenbahn 1 fährt ab 6:00 Uhr alle 10 Minuten. Die Linie 2 verkehrt von 5:30 bis 23:00 Uhr alle 15 Minuten. Die Straßenbahn 3 hat abends ab 20:00 Uhr nur noch alle 30 Minuten eine Fahrt. Bitte informieren Sie sich an den Haltestellen über die genauen Abfahrtszeiten.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Ab wann gelten die neuen Fahrpläne?",
                            options = listOf("Ab Montag, 15. März", "Ab Dienstag, 16. März", "Ab Mittwoch, 17. März", "Ab Donnerstag, 18. März"),
                            correctAnswer = "Ab Montag, 15. März",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "From when do the new timetables apply?",
                            optionsEnglish = listOf("From Monday, March 15th", "From Tuesday, March 16th", "From Wednesday, March 17th", "From Thursday, March 18th")
                        ),
                        Question(
                            id = 2,
                            question = "Wie oft fährt die Straßenbahn 1 ab 6:00 Uhr?",
                            options = listOf("Alle 5 Minuten", "Alle 10 Minuten", "Alle 15 Minuten", "Alle 20 Minuten"),
                            correctAnswer = "Alle 10 Minuten",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How often does tram 1 run from 6:00 AM?",
                            optionsEnglish = listOf("Every 5 minutes", "Every 10 minutes", "Every 15 minutes", "Every 20 minutes")
                        ),
                        Question(
                            id = 3,
                            question = "Wie oft fährt die Straßenbahn 3 abends ab 20:00 Uhr?",
                            options = null,
                            correctAnswer = "alle 30 Minuten",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How often does tram 3 run in the evening from 8:00 PM?"
                        ),
                        Question(
                            id = 4,
                            question = "Wo kann man sich über die genauen Abfahrtszeiten informieren?",
                            options = listOf("Am Bahnhof", "An den Haltestellen", "Im Internet", "Beim Fahrer"),
                            correctAnswer = "An den Haltestellen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where can you get information about the exact departure times?",
                            optionsEnglish = listOf("At the train station", "At the stops", "On the internet", "From the driver")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("ändern", "to change", "Die Fahrpläne ändern sich."),
                        VocabularyItem("Fahrplan", "timetable", "Der Fahrplan ist neu."),
                        VocabularyItem("Straßenbahn", "tram", "Die Straßenbahn fährt pünktlich."),
                        VocabularyItem("Abfahrtszeit", "departure time", "Die Abfahrtszeit ist wichtig.")
                    ),
                    source = "ÖSD"
                ))

                lessons.add(createLesenLesson(
                    title = "Wetterbericht verstehen",
                    description = "Understanding weather reports - ÖSD Lesen Teil 2",
                    level = level,
                    orderIndex = 34,
                    text = "WETTER FÜR WIEN: Morgen wird es sonnig und warm. Die Temperaturen steigen auf 22-25 Grad Celsius. Es gibt nur wenige Wolken und keinen Regen. In der Nacht kühlt es auf 12 Grad ab. Am Donnerstag erwartet uns wechselhaftes Wetter mit Schauern. Die Höchsttemperaturen liegen bei 18 Grad. Freitag bringt wieder Sonnenschein und Temperaturen bis zu 24 Grad.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wie wird das Wetter morgen in Wien?",
                            options = listOf("Regnerisch", "Sonnig und warm", "Bewölkt", "Stürmisch"),
                            correctAnswer = "Sonnig und warm",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What will the weather be like tomorrow in Vienna?",
                            optionsEnglish = listOf("Rainy", "Sunny and warm", "Cloudy", "Stormy")
                        ),
                        Question(
                            id = 2,
                            question = "Wie hoch sind die Temperaturen morgen?",
                            options = listOf("15-18 Grad", "18-21 Grad", "22-25 Grad", "25-28 Grad"),
                            correctAnswer = "22-25 Grad",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What are the temperatures tomorrow?",
                            optionsEnglish = listOf("15-18 degrees", "18-21 degrees", "22-25 degrees", "25-28 degrees")
                        ),
                        Question(
                            id = 3,
                            question = "Wie wird das Wetter am Donnerstag?",
                            options = null,
                            correctAnswer = "wechselhaft mit Schauern",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What will the weather be like on Thursday?"
                        ),
                        Question(
                            id = 4,
                            question = "Wie hoch sind die Temperaturen am Freitag?",
                            options = listOf("Bis zu 20 Grad", "Bis zu 22 Grad", "Bis zu 24 Grad", "Bis zu 26 Grad"),
                            correctAnswer = "Bis zu 24 Grad",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What are the temperatures on Friday?",
                            optionsEnglish = listOf("Up to 20 degrees", "Up to 22 degrees", "Up to 24 degrees", "Up to 26 degrees")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Wetterbericht", "weather report", "Der Wetterbericht ist gut."),
                        VocabularyItem("Temperatur", "temperature", "Die Temperatur steigt."),
                        VocabularyItem("Sonnenschein", "sunshine", "Es gibt viel Sonnenschein."),
                        VocabularyItem("wechselhaft", "changeable", "Das Wetter ist wechselhaft.")
                    ),
                    source = "ÖSD"
                ))

                lessons.add(createLesenLesson(
                    title = "E-Mail verstehen",
                    description = "Understanding emails - ÖSD Lesen Teil 3",
                    level = level,
                    orderIndex = 35,
                    text = "Von: office@company.at\nAn: anna.schmidt@email.com\nBetreff: Bewerbungsgespräch\n\nLiebe Frau Schmidt,\n\nvielen Dank für Ihre Bewerbung. Wir freuen uns, Ihnen mitteilen zu können, dass Sie zum Vorstellungsgespräch eingeladen sind.\n\nTermin: Donnerstag, 20. April, 14:00 Uhr\nOrt: Bürogebäude Wien Mitte, 3. Stock, Raum 301\nAdresse: Mariahilfer Straße 123, 1060 Wien\n\nBitte bringen Sie folgende Unterlagen mit:\n- Lebenslauf\n- Zeugnisse\n- Personalausweis\n\nBei Fragen erreichen Sie uns unter 01-234567.\n\nMit freundlichen Grüßen\nMax Müller\nPersonalabteilung\nCompany GmbH",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Warum schreibt die Firma eine E-Mail?",
                            options = listOf("Zur Kündigung", "Zur Einladung zum Vorstellungsgespräch", "Zur Gehaltsverhandlung", "Zur Urlaubsplanung"),
                            correctAnswer = "Zur Einladung zum Vorstellungsgespräch",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Why is the company writing an email?",
                            optionsEnglish = listOf("For termination", "For invitation to job interview", "For salary negotiation", "For vacation planning")
                        ),
                        Question(
                            id = 2,
                            question = "Wann findet das Vorstellungsgespräch statt?",
                            options = listOf("Donnerstag, 20. April, 14:00 Uhr", "Freitag, 21. April, 15:00 Uhr", "Montag, 24. April, 10:00 Uhr", "Dienstag, 25. April, 16:00 Uhr"),
                            correctAnswer = "Donnerstag, 20. April, 14:00 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When does the job interview take place?",
                            optionsEnglish = listOf("Thursday, April 20th, 2:00 PM", "Friday, April 21st, 3:00 PM", "Monday, April 24th, 10:00 AM", "Tuesday, April 25th, 4:00 PM")
                        ),
                        Question(
                            id = 3,
                            question = "Wo findet das Gespräch statt?",
                            options = null,
                            correctAnswer = "Bürogebäude Wien Mitte, 3. Stock, Raum 301",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "Where does the interview take place?"
                        ),
                        Question(
                            id = 4,
                            question = "Was soll man zum Gespräch mitbringen?",
                            options = listOf("Lebenslauf, Zeugnisse, Personalausweis", "Nur Lebenslauf", "Nur Zeugnisse", "Nur Personalausweis"),
                            correctAnswer = "Lebenslauf, Zeugnisse, Personalausweis",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What should you bring to the interview?",
                            optionsEnglish = listOf("Resume, certificates, ID card", "Only resume", "Only certificates", "Only ID card")
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Bewerbung", "application", "Die Bewerbung ist eingegangen."),
                        VocabularyItem("Vorstellungsgespräch", "job interview", "Das Vorstellungsgespräch ist wichtig."),
                        VocabularyItem("Lebenslauf", "resume/CV", "Der Lebenslauf muss aktuell sein."),
                        VocabularyItem("Zeugnis", "certificate", "Die Zeugnisse sind wichtig.")
                    ),
                    source = "ÖSD"
                ))
            }

            "A2" -> {
                lessons.add(createLesenLesson(
                    title = "Eine Reise nach München",
                    description = "Travel and transportation",
                    level = level,
                    orderIndex = 1,
                    text = "Letzten Sommer bin ich nach München gefahren. Ich habe den Zug genommen. Die Fahrt hat drei Stunden gedauert. In München habe ich das Oktoberfest besucht. Es war sehr interessant. Ich habe viele neue Freunde kennengelernt. Das Wetter war schön und ich habe viel Spaß gehabt.",
                    questions = listOf(
                        Question(id = 1, question = "Womit ist die Person gefahren?", options = listOf("Auto", "Zug", "Flugzeug", "Bus"), correctAnswer = "Zug", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wie lange hat die Fahrt gedauert?", options = listOf("2 Stunden", "3 Stunden", "4 Stunden", "5 Stunden"), correctAnswer = "3 Stunden", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was hat die Person in München besucht?", options = null, correctAnswer = "Oktoberfest", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Reise", "journey", "Ich mache eine Reise."),
                        VocabularyItem("Zug", "train", "Ich nehme den Zug."),
                        VocabularyItem("kennenlernen", "to meet", "Ich lerne neue Leute kennen.")
                    )
                ))
                
                lessons.add(createLesenLesson(
                    title = "Meine Hobbys",
                    description = "Hobbies and interests",
                    level = level,
                    orderIndex = 2,
                    text = "Ich habe viele Hobbys. Am liebsten spiele ich Fußball. Ich bin in einem Verein und wir trainieren zweimal pro Woche. Außerdem lese ich gerne Bücher, besonders Krimis. Manchmal gehe ich auch ins Kino oder treffe mich mit Freunden. Am Wochenende koche ich gerne und experimentiere mit neuen Rezepten.",
                    questions = listOf(
                        Question(id = 1, question = "Was ist das Lieblingshobby?", options = listOf("Lesen", "Fußball", "Kochen", "Kino"), correctAnswer = "Fußball", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wie oft trainiert die Person?", options = listOf("Einmal pro Woche", "Zweimal pro Woche", "Dreimal pro Woche", "Täglich"), correctAnswer = "Zweimal pro Woche", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was liest die Person gerne?", options = null, correctAnswer = "Krimis", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Hobby", "hobby", "Ich habe viele Hobbys."),
                        VocabularyItem("Verein", "club", "Ich bin in einem Verein."),
                        VocabularyItem("experimentieren", "to experiment", "Ich experimentiere mit Rezepten.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "Gesundheit und Ernährung",
                    description = "Health and nutrition",
                    level = level,
                    orderIndex = 3,
                    text = "Gesunde Ernährung ist sehr wichtig für unseren Körper. Ich esse viel Obst und Gemüse. Jeden Morgen trinke ich ein Glas Wasser und esse Müsli mit Joghurt. Zum Mittagessen gibt es oft Salat oder Suppe. Ich versuche, weniger Fleisch zu essen und mehr Fisch. Sport ist auch wichtig - ich gehe dreimal pro Woche ins Fitnessstudio.",
                    questions = listOf(
                        Question(id = 1, question = "Was ist wichtig für den Körper?", options = listOf("Süßigkeiten", "Gesunde Ernährung", "Fast Food", "Kaffee"), correctAnswer = "Gesunde Ernährung", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was trinkt die Person morgens?", options = listOf("Kaffee", "Tee", "Wasser", "Saft"), correctAnswer = "Wasser", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Wie oft geht die Person ins Fitnessstudio?", options = null, correctAnswer = "dreimal pro Woche", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Ernährung", "nutrition", "Gesunde Ernährung ist wichtig."),
                        VocabularyItem("Müsli", "muesli", "Ich esse Müsli zum Frühstück."),
                        VocabularyItem("Fitnessstudio", "gym", "Ich gehe ins Fitnessstudio.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "Arbeit und Beruf",
                    description = "Work and professions",
                    level = level,
                    orderIndex = 4,
                    text = "Ich arbeite als Lehrerin an einer Grundschule. Meine Arbeit macht mir viel Spaß. Ich unterrichte Mathematik und Deutsch. Die Kinder sind zwischen 6 und 10 Jahre alt. Jeden Tag bereite ich den Unterricht vor und korrigiere Hausaufgaben. Manchmal habe ich auch Elternabende oder Konferenzen. Die Ferien sind ein großer Vorteil meines Berufs.",
                    questions = listOf(
                        Question(id = 1, question = "Was ist der Beruf der Person?", options = listOf("Ärztin", "Lehrerin", "Ingenieurin", "Köchin"), correctAnswer = "Lehrerin", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Welche Fächer unterrichtet sie?", options = listOf("Englisch und Sport", "Mathematik und Deutsch", "Kunst und Musik", "Geschichte und Biologie"), correctAnswer = "Mathematik und Deutsch", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was ist ein Vorteil des Berufs?", options = null, correctAnswer = "Ferien", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Lehrerin", "teacher", "Ich bin Lehrerin."),
                        VocabularyItem("unterrichten", "to teach", "Ich unterrichte Mathematik."),
                        VocabularyItem("Elternabend", "parent evening", "Heute ist Elternabend.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "Kultur und Traditionen",
                    description = "Culture and traditions",
                    level = level,
                    orderIndex = 5,
                    text = "Deutschland hat viele interessante Traditionen. An Weihnachten feiern wir mit der Familie und essen traditionelle Gerichte wie Braten und Lebkuchen. Im Oktober ist das Oktoberfest in München sehr beliebt. Karneval wird vor allem im Rheinland gefeiert. Ostern ist ein wichtiges christliches Fest. Viele Deutsche gehen auch gerne ins Theater oder ins Museum.",
                    questions = listOf(
                        Question(id = 1, question = "Wann ist das Oktoberfest?", options = listOf("September", "Oktober", "November", "Dezember"), correctAnswer = "Oktober", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wo wird Karneval gefeiert?", options = listOf("Nur in Berlin", "Nur in München", "Im Rheinland", "Überall"), correctAnswer = "Im Rheinland", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was essen die Deutschen zu Weihnachten?", options = null, correctAnswer = "Braten und Lebkuchen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Tradition", "tradition", "Das ist eine deutsche Tradition."),
                        VocabularyItem("Weihnachten", "Christmas", "Weihnachten ist ein wichtiges Fest."),
                        VocabularyItem("Karneval", "carnival", "Karneval wird im Februar gefeiert.")
                    )
                ))

                // Goethe A2 - Email reading (from Modellsatz)
                lessons.add(createLesenLesson(
                    title = "Eine E-Mail lesen",
                    description = "Reading and understanding emails (Goethe-Zertifikat A2)",
                    level = level,
                    orderIndex = 6,
                    text = """Von: anna.mueller@email.de
An: info@reiseservice.de
Betreff: Hotelreservierung in Berlin

Sehr geehrte Damen und Herren,

ich möchte ein Zimmer für zwei Nächte vom 15. bis 17. Mai in Berlin reservieren. Ich suche ein Doppelzimmer mit Frühstück. Die Zimmer sollten ruhig liegen und einen Internetzugang haben.

Bitte senden Sie mir die Preise und Verfügbarkeit. Ich benötige auch Informationen über die Anreise mit öffentlichen Verkehrsmitteln vom Flughafen Tegel.

Mit freundlichen Grüßen
Anna Müller""",
                    questions = listOf(
                        Question(id = 1, question = "Was möchte die Kundin reservieren?", options = listOf("Ein Einzelzimmer", "Ein Doppelzimmer", "Eine Suite", "Zwei Zimmer"), correctAnswer = "Ein Doppelzimmer", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the customer want to book?"),
                        Question(id = 2, question = "Wie lange bleibt sie?", options = listOf("Eine Nacht", "Zwei Nächte", "Drei Nächte", "Eine Woche"), correctAnswer = "Zwei Nächte", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How long is she staying?"),
                        Question(id = 3, question = "Was braucht sie für Informationen?", options = null, correctAnswer = "Anreise mit öffentlichen Verkehrsmitteln", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What information does she need?")
                    ),
                    vocabulary = listOf(
                        VocabularyItem("reservieren", "to reserve", "Ich möchte ein Zimmer reservieren."),
                        VocabularyItem("Doppelzimmer", "double room", "Ein Doppelzimmer für zwei Personen."),
                        VocabularyItem("Anreise", "arrival", "Die Anreise mit dem Zug.")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Reading notices
                lessons.add(createLesenLesson(
                    title = "Notizen und Anzeigen lesen",
                    description = "Reading notices and advertisements (TELC Deutsch A2)",
                    level = level,
                    orderIndex = 7,
                    text = """VERMISST: Schwarze Katze, ca. 3 Jahre alt, trägt rotes Halsband.
Antworten an: Maria Schmidt, Tel. 030-1234567

ZU VERKAUFEN: Fahrrad, Herrenmodell, gut erhalten, Preis 150 Euro.
Interesse? Ruf an: 0176-9876543

GESUCHT: Babysitter für Mittwoch- und Freitagabende, 18-22 Uhr.
Kinder 4 und 6 Jahre. Bezahlung 12 Euro/Stunde.
Bewerbungen an: familie.meier@email.de""",
                    questions = listOf(
                        Question(id = 1, question = "Was ist die Telefonnummer für die vermisste Katze?", options = listOf("030-1234567", "0176-9876543", "030-9876543", "0176-1234567"), correctAnswer = "030-1234567", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wie viel kostet das Fahrrad?", options = listOf("100 Euro", "120 Euro", "150 Euro", "200 Euro"), correctAnswer = "150 Euro", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Wann braucht die Familie einen Babysitter?", options = null, correctAnswer = "Mittwoch- und Freitagabende", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("vermisst", "missing", "Die Katze ist vermisst."),
                        VocabularyItem("Halsband", "collar", "Die Katze trägt ein Halsband."),
                        VocabularyItem("Babysitter", "babysitter", "Wir suchen einen Babysitter.")
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Reading dialogs
                lessons.add(createLesenLesson(
                    title = "Dialoge verstehen",
                    description = "Understanding conversations about health (ÖSD Zertifikat A2)",
                    level = level,
                    orderIndex = 8,
                    text = """Arzt: Guten Tag, Frau Müller. Was führt Sie zu mir?
Patientin: Guten Tag, Herr Doktor. Ich habe seit drei Tagen Halsschmerzen und Husten. Außerdem habe ich Fieber.
Arzt: Haben Sie auch Kopfschmerzen oder Gliederschmerzen?
Patientin: Ja, ich habe starke Kopfschmerzen und meine Glieder tun weh.
Arzt: Ich verschreibe Ihnen ein Antibiotikum und einen Hustenstiller. Trinken Sie viel Tee und ruhen Sie sich aus. In drei Tagen kommen Sie zur Kontrolle.
Patientin: Vielen Dank, Herr Doktor. Auf Wiedersehen.
Arzt: Auf Wiedersehen, Frau Müller.""",
                    questions = listOf(
                        Question(id = 1, question = "Warum geht die Patientin zum Arzt?", options = listOf("Kopfschmerzen", "Halsschmerzen und Husten", "Rückenschmerzen", "Magenschmerzen"), correctAnswer = "Halsschmerzen und Husten", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was verschreibt der Arzt?", options = listOf("Nur Tee", "Antibiotikum und Hustenstiller", "Nur Schmerzmittel", "Eine Spritze"), correctAnswer = "Antibiotikum und Hustenstiller", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Wann soll die Patientin wiederkommen?", options = null, correctAnswer = "In drei Tagen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Halsschmerzen", "sore throat", "Ich habe Halsschmerzen."),
                        VocabularyItem("Husten", "cough", "Ich habe Husten."),
                        VocabularyItem("verschrei ben", "to prescribe", "Der Arzt verschreibt Medikamente.")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Connected texts about travel
                lessons.add(createLesenLesson(
                    title = "Reiseberichte lesen",
                    description = "Reading connected texts about travel experiences",
                    level = level,
                    orderIndex = 9,
                    text = """Meine Reise nach Österreich

Letztes Jahr habe ich eine Woche in Wien verbracht. Die Stadt hat mir sehr gut gefallen. Ich habe viele Sehenswürdigkeiten besucht: den Stephansdom, das Schloss Schönbrunn und das Belvedere. Besonders beeindruckend fand ich die Musikgeschichte der Stadt.

Die Unterkunft war sehr komfortabel. Das Hotel lag zentral, aber trotzdem ruhig. Das Frühstück war ausgezeichnet und die Bedienung freundlich. Nur das Wetter war nicht ideal - es hat fast jeden Tag geregnet.

Ich habe auch eine Wanderung in den Wienerwald gemacht. Die Natur war wunderschön, obwohl der Weg etwas anstrengend war. In einem Gasthaus habe ich typische Wiener Küche probiert: Schnitzel und Sachertorte.

Ich würde Wien jederzeit wieder besuchen und kann die Stadt wärmstens empfehlen.""",
                    questions = listOf(
                        Question(id = 1, question = "Wo hat die Person eine Woche verbracht?", options = listOf("Berlin", "Wien", "München", "Salzburg"), correctAnswer = "Wien", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was war nicht ideal?", options = listOf("Die Unterkunft", "Das Frühstück", "Das Wetter", "Die Sehenswürdigkeiten"), correctAnswer = "Das Wetter", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was hat die Person im Wienerwald gemacht?", options = null, correctAnswer = "eine Wanderung", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Sehenswürdigkeit", "sightseeing attraction", "Der Stephansdom ist eine Sehenswürdigkeit."),
                        VocabularyItem("beeindruckend", "impressive", "Das Schloss ist beeindruckend."),
                        VocabularyItem("empfehlen", "to recommend", "Ich kann das Restaurant empfehlen.")
                    ),
                    source = "Goethe"
                ))

                // Shopping conversations (TELC inspired)
                lessons.add(createLesenLesson(
                    title = "Einkaufen gehen",
                    description = "Reading shopping conversations",
                    level = level,
                    orderIndex = 10,
                    text = """Verkäuferin: Guten Tag! Kann ich Ihnen helfen?
Kundin: Ja, ich suche eine Jacke für den Winter. Sie soll warm sein und nicht zu teuer.
Verkäuferin: Welche Größe haben Sie?
Kundin: Größe 38.
Verkäuferin: Hier haben wir eine schöne Winterjacke. Sie ist aus Wolle, hat eine Kapuze und kostet 89 Euro.
Kundin: Die gefällt mir. Haben Sie sie auch in Schwarz?
Verkäuferin: Ja, in Schwarz und Blau. Die schwarze ist gerade im Angebot für 75 Euro.
Kundin: Perfekt! Ich nehme die schwarze Jacke.
Verkäuferin: Möchten Sie auch Handschuhe dazu? Wir haben passende Handschuhe für 15 Euro.
Kundin: Nein danke, nur die Jacke.
Verkäuferin: Gut, dann gehen wir zur Kasse.""",
                    questions = listOf(
                        Question(id = 1, question = "Was sucht die Kundin?", options = listOf("Eine Hose", "Eine Jacke", "Ein Kleid", "Schuhe"), correctAnswer = "Eine Jacke", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wie viel kostet die Jacke im Angebot?", options = listOf("89 Euro", "75 Euro", "65 Euro", "95 Euro"), correctAnswer = "75 Euro", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was kauft die Kundin noch?", options = null, correctAnswer = "Nichts, nur die Jacke", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Jacke", "jacket", "Ich brauche eine warme Jacke."),
                        VocabularyItem("Kapuze", "hood", "Die Jacke hat eine Kapuze."),
                        VocabularyItem("Angebot", "special offer", "Die Jacke ist im Angebot.")
                    ),
                    source = "TELC"
                ))

                // Health and routines (ÖSD inspired)
                lessons.add(createLesenLesson(
                    title = "Gesundheitsdialoge",
                    description = "Reading health-related conversations",
                    level = level,
                    orderIndex = 11,
                    text = """Apotheker: Hallo, was kann ich für Sie tun?
Kunde: Ich brauche etwas gegen Kopfschmerzen. Haben Sie Aspirin?
Apotheker: Ja, wir haben verschiedene Schmerzmittel. Nehmen Sie regelmäßig Medikamente?
Kunde: Nein, aber ich habe oft Migräne. Mein Arzt hat mir Tabletten verschrieben.
Apotheker: Hier ist das Medikament. Nehmen Sie es mit Wasser ein. Wenn die Schmerzen anhalten, gehen Sie zum Arzt.
Kunde: Danke für den Rat. Wie viel kostet es?
Apotheker: 8,50 Euro. Zahlen Sie an der Kasse.
Kunde: Vielen Dank und auf Wiedersehen.
Apotheker: Auf Wiedersehen! Gute Besserung.""",
                    questions = listOf(
                        Question(id = 1, question = "Warum geht der Kunde in die Apotheke?", options = listOf("Für Vitamine", "Für Kopfschmerzen", "Für Erkältung", "Für Allergie"), correctAnswer = "Für Kopfschmerzen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was hat der Arzt verschrieben?", options = listOf("Aspirin", "Vitamine", "Tabletten", "Tropfen"), correctAnswer = "Tabletten", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Wie viel kostet das Medikament?", options = null, correctAnswer = "8,50 Euro", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Kopfschmerzen", "headache", "Ich habe Kopfschmerzen."),
                        VocabularyItem("Migräne", "migraine", "Sie hat Migräne."),
                        VocabularyItem("verschrei ben", "to prescribe", "Der Arzt verschreibt Medikamente.")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Reading instructions
                lessons.add(createLesenLesson(
                    title = "Anleitungen verstehen",
                    description = "Reading and understanding instructions",
                    level = level,
                    orderIndex = 12,
                    text = """Anleitung für den Gebrauch des neuen Kaffeeautomaten

1. Stecken Sie den Stecker in eine Steckdose.
2. Drücken Sie den Power-Knopf, um das Gerät einzuschalten.
3. Warten Sie 2 Minuten, bis das Gerät bereit ist.
4. Füllen Sie Wasser in den Tank (max. 1,5 Liter).
5. Geben Sie Kaffeepulver in den Filter (2-3 Esslöffel pro Tasse).
6. Schließen Sie den Deckel und wählen Sie die gewünschte Programm.
7. Drücken Sie den Start-Knopf.
8. Warten Sie, bis der Kaffee fertig ist (ca. 3-4 Minuten).
9. Entfernen Sie die Kanne vorsichtig.
10. Genießen Sie Ihren Kaffee!

Wichtig: Reinigen Sie das Gerät nach jedem Gebrauch. Verwenden Sie nur kaltes Wasser.""",
                    questions = listOf(
                        Question(id = 1, question = "Wie viel Wasser kann man maximal einfüllen?", options = listOf("1 Liter", "1,5 Liter", "2 Liter", "0,5 Liter"), correctAnswer = "1,5 Liter", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wie viel Kaffeepulver braucht man pro Tasse?", options = listOf("1 Esslöffel", "2-3 Esslöffel", "4 Esslöffel", "Eine Handvoll"), correctAnswer = "2-3 Esslöffel", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was soll man nach jedem Gebrauch machen?", options = null, correctAnswer = "das Gerät reinigen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Anleitung", "instruction", "Lesen Sie die Anleitung."),
                        VocabularyItem("Steckdose", "socket", "Stecken Sie den Stecker in die Steckdose."),
                        VocabularyItem("Kaffeepulver", "coffee powder", "Geben Sie Kaffeepulver in den Filter.")
                    ),
                    source = "Goethe"
                ))

                // Travel notices (TELC inspired)
                lessons.add(createLesenLesson(
                    title = "Reiseinformationen",
                    description = "Reading travel information and notices",
                    level = level,
                    orderIndex = 13,
                    text = """INFORMATION FÜR REISENDE

Abfahrt der Züge von Gleis 8:
- Hamburg 14:25 (verspätet 10 Minuten)
- Berlin 14:45 (pünktlich)
- München 15:10 (ausgefallen)

Wichtige Mitteilung: Aufgrund von Bauarbeiten ist die S-Bahn-Linie S1 zwischen den Stationen Hauptbahnhof und Ostbahnhof unterbrochen. Benutzen Sie bitte die Buslinie 123 als Ersatz.

Flugauskunft:
LH 456 nach Frankfurt: Boarding 15:30, Gate A7
LH 789 nach London: Boarding 16:00, Gate B3
LH 234 nach Paris: Boarding 16:45, Gate A9

Bitte beachten Sie: Rauchen ist in allen Bereichen des Flughafens verboten.""",
                    questions = listOf(
                        Question(id = 1, question = "Welcher Zug ist ausgefallen?", options = listOf("Hamburg", "Berlin", "München", "Alle Züge"), correctAnswer = "München", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Warum ist die S-Bahn unterbrochen?", options = listOf("Wetter", "Bauarbeiten", "Streik", "Unfall"), correctAnswer = "Bauarbeiten", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Wann boardet der Flug nach Paris?", options = null, correctAnswer = "16:45", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("verspätet", "delayed", "Der Zug ist verspätet."),
                        VocabularyItem("ausgefallen", "cancelled", "Der Flug ist ausgefallen."),
                        VocabularyItem("Ersatz", "replacement", "Benutzen Sie den Ersatzbus.")
                    ),
                    source = "TELC"
                ))

                // Health advice (ÖSD inspired)
                lessons.add(createLesenLesson(
                    title = "Gesundheitstipps",
                    description = "Reading health advice and recommendations",
                    level = level,
                    orderIndex = 14,
                    text = """Gesund leben - Tipps für jeden Tag

1. Bewegung: Gehen Sie jeden Tag mindestens 30 Minuten spazieren. Sport hält fit und macht glücklich.

2. Ernährung: Essen Sie viel Obst und Gemüse. Vermeiden Sie zu viel Zucker und Fett. Trinken Sie täglich 2 Liter Wasser.

3. Schlaf: Schlafen Sie 7-8 Stunden pro Nacht. Ein fester Schlaf-Wach-Rhythmus ist wichtig für die Gesundheit.

4. Stress vermeiden: Machen Sie Pausen bei der Arbeit. Hören Sie Musik oder treffen Sie Freunde.

5. Arztbesuche: Gehen Sie regelmäßig zur Vorsorge. Früherkennung kann Leben retten.

6. Hygiene: Waschen Sie sich regelmäßig die Hände. Das verhindert Krankheiten.

Denken Sie daran: Vorbeugen ist besser als Heilen!""",
                    questions = listOf(
                        Question(id = 1, question = "Wie viel sollte man täglich spazieren gehen?", options = listOf("10 Minuten", "20 Minuten", "30 Minuten", "60 Minuten"), correctAnswer = "30 Minuten", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wie viel Wasser sollte man täglich trinken?", options = listOf("1 Liter", "2 Liter", "3 Liter", "0,5 Liter"), correctAnswer = "2 Liter", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was ist besser als Heilen?", options = null, correctAnswer = "Vorbeugen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Bewegung", "exercise", "Regelmäßige Bewegung ist wichtig."),
                        VocabularyItem("Ernährung", "nutrition", "Gesunde Ernährung ist essentiell."),
                        VocabularyItem("Vorbeugen", "to prevent", "Vorbeugen ist besser als heilen.")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Reading emails about plans
                lessons.add(createLesenLesson(
                    title = "E-Mails über Pläne",
                    description = "Reading emails about future plans",
                    level = level,
                    orderIndex = 15,
                    text = """Von: max.berger@email.de
An: lisa.koch@email.de
Betreff: Wochenendpläne

Liebe Lisa,

wie geht es Dir? Ich hoffe, Du bist gesund. Nächstes Wochenende möchte ich Dich besuchen kommen. Ich könnte Freitagabend ankommen und bis Sonntag bleiben.

Ich würde gerne mit Dir ins Museum gehen und danach essen. Kennst Du ein gutes Restaurant in der Nähe? Vielleicht können wir auch einen Spaziergang machen.

Sag mir bitte Bescheid, ob das für Dich passt. Ich freue mich auf Deine Antwort.

Viele Grüße
Max""",
                    questions = listOf(
                        Question(id = 1, question = "Wann möchte Max zu Besuch kommen?", options = listOf("Dieses Wochenende", "Nächstes Wochenende", "In zwei Wochen", "Am Montag"), correctAnswer = "Nächstes Wochenende", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wie lange möchte er bleiben?", options = listOf("Einen Tag", "Zwei Tage", "Drei Tage", "Eine Woche"), correctAnswer = "Zwei Tage", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was möchte er mit Lisa machen?", options = null, correctAnswer = "ins Museum gehen und essen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Wochenendpläne", "weekend plans", "Ich habe Wochenendpläne."),
                        VocabularyItem("besuchen", "to visit", "Ich möchte Dich besuchen."),
                        VocabularyItem("Spaziergang", "walk", "Wir machen einen Spaziergang.")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Reading invitations and responses
                lessons.add(createLesenLesson(
                    title = "Einladungen lesen",
                    description = "Reading invitations and responses",
                    level = level,
                    orderIndex = 16,
                    text = """EINLADUNG

Liebe Freunde,

wir möchten euch zu unserer Gartenparty einladen!
Datum: Samstag, 20. Juni
Uhrzeit: 15:00 - 20:00 Uhr
Ort: Gartenstraße 15, 80331 München

Wir freuen uns auf Grillen, Spiele und gute Gespräche. Bringt bitte etwas zu trinken mit. Bei schlechtem Wetter findet die Party im Haus statt.

Bitte gebt uns bis zum 15. Juni Bescheid, ob ihr kommt.

Mit freundlichen Grüßen
Anna und Thomas

---

Von: maria@email.de
An: anna@email.de
Betreff: AW: Einladung Gartenparty

Liebe Anna, lieber Thomas,

vielen Dank für die Einladung! Wir kommen sehr gerne. Ich bringe einen Salat mit. Soll ich noch etwas anderes mitbringen?

Liebe Grüße
Maria""",
                    questions = listOf(
                        Question(id = 1, question = "Wann findet die Party statt?", options = listOf("20. Mai", "20. Juni", "20. Juli", "20. August"), correctAnswer = "20. Juni", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was sollen die Gäste mitbringen?", options = listOf("Essen", "Getränke", "Geschenke", "Musik"), correctAnswer = "Getränke", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was bringt Maria mit?", options = null, correctAnswer = "einen Salat", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Einladung", "invitation", "Ich habe eine Einladung bekommen."),
                        VocabularyItem("Gartenparty", "garden party", "Die Gartenparty ist am Samstag."),
                        VocabularyItem("Bescheid geben", "to let know", "Gib mir Bescheid.")
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Reading about past events
                lessons.add(createLesenLesson(
                    title = "Erlebnisse erzählen",
                    description = "Reading stories about past experiences",
                    level = level,
                    orderIndex = 17,
                    text = """Mein Urlaub in den Bergen

Vor zwei Jahren habe ich eine Wanderreise in den bayerischen Alpen gemacht. Ich war mit drei Freunden unterwegs. Wir hatten schönes Wetter und konnten jeden Tag wandern.

Am ersten Tag sind wir zum Königssee gefahren. Der See war wunderschön und das Wasser war sehr klar. Wir haben eine Bootsfahrt gemacht und die Berge bestaunt.

Am nächsten Tag haben wir eine lange Wanderung zum Gipfel gemacht. Es war anstrengend, aber die Aussicht war fantastisch. Auf dem Weg haben wir viele Blumen und Tiere gesehen.

Abends haben wir in einer Berghütte übernachtet. Das Essen war typisch bayerisch: Knödel, Schweinebraten und Bier. Die Wirtin hat uns Geschichten aus der Region erzählt.

Es war eine unvergessliche Reise. Ich würde gerne wieder in die Alpen fahren.""",
                    questions = listOf(
                        Question(id = 1, question = "Wann hat die Person die Reise gemacht?", options = listOf("Letztes Jahr", "Vor zwei Jahren", "Vor einem Jahr", "Dieses Jahr"), correctAnswer = "Vor zwei Jahren", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wo haben sie übernachtet?", options = listOf("Im Hotel", "In einer Berghütte", "Im Zelt", "Bei Freunden"), correctAnswer = "In einer Berghütte", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was war fantastisch?", options = null, correctAnswer = "die Aussicht", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Wanderreise", "hiking trip", "Ich mache eine Wanderreise."),
                        VocabularyItem("Bootsfahrt", "boat trip", "Wir machen eine Bootsfahrt."),
                        VocabularyItem("unvergesslich", "unforgettable", "Die Reise war unvergesslich.")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Reading notices about events
                lessons.add(createLesenLesson(
                    title = "Veranstaltungsankündigungen",
                    description = "Reading event announcements",
                    level = level,
                    orderIndex = 18,
                    text = """STADTFEST BERLIN 2024

Das traditionelle Stadtfest findet dieses Jahr vom 15. bis 17. August statt. Veranstaltungsort ist der Alexanderplatz und die umliegenden Straßen.

Programm:
Freitag, 15.08.: Eröffnung mit Feuerwerk um 20:00 Uhr
Samstag, 16.08.: Live-Musik verschiedener Bands, Kinderprogramm, Essensstände
Sonntag, 17.08.: Familiennachmittag mit Spielen und Vorführungen

Eintritt frei! Für Verpflegung ist gesorgt. Bei Regen findet das Fest in der Kongresshalle statt.

Besuchen Sie unsere Website für detaillierte Informationen: www.stadtfest-berlin.de""",
                    questions = listOf(
                        Question(id = 1, question = "Wie lange dauert das Stadtfest?", options = listOf("Einen Tag", "Zwei Tage", "Drei Tage", "Eine Woche"), correctAnswer = "Drei Tage", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wo findet das Fest statt?", options = listOf("Brandenburger Tor", "Alexanderplatz", "Potsdamer Platz", "Museumsinsel"), correctAnswer = "Alexanderplatz", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was gibt es am Sonntag?", options = null, correctAnswer = "Familiennachmittag mit Spielen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Veranstaltung", "event", "Die Veranstaltung ist interessant."),
                        VocabularyItem("Feuerwerk", "fireworks", "Das Feuerwerk ist schön."),
                        VocabularyItem("Vor führung", "performance", "Die Vorführung beginnt um 15 Uhr.")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Reading job advertisements
                lessons.add(createLesenLesson(
                    title = "Stellenanzeigen lesen",
                    description = "Reading job advertisements",
                    level = level,
                    orderIndex = 19,
                    text = """GESUCHT: Verkäuferin/Verkäufer für Bekleidungsgeschäft

Wir suchen ab sofort eine/n motivierte/n Verkäufer/in für unser Bekleidungsgeschäft in der Innenstadt.

Anforderungen:
- Freundliches Auftreten
- Erfahrung im Verkauf erwünscht, aber nicht zwingend
- Flexibilität bezüglich Arbeitszeiten
- Gute Deutschkenntnisse

Wir bieten:
- Festanstellung
- Übertarifliche Bezahlung
- Mitarbeiterrabatte
- Angenehmes Arbeitsumfeld

Bewerbungen mit Lebenslauf an: personal@modehaus.de""",
                    questions = listOf(
                        Question(id = 1, question = "Für welches Geschäft suchen sie jemanden?", options = listOf("Restaurant", "Bekleidungsgeschäft", "Supermarkt", "Buchladen"), correctAnswer = "Bekleidungsgeschäft", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was ist erwünscht aber nicht zwingend?", options = listOf("Freundliches Auftreten", "Erfahrung im Verkauf", "Gute Deutschkenntnisse", "Flexibilität"), correctAnswer = "Erfahrung im Verkauf", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was bieten sie?", options = null, correctAnswer = "Festanstellung und übertarifliche Bezahlung", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Verkäufer", "salesperson", "Der Verkäufer ist freundlich."),
                        VocabularyItem("Anforderungen", "requirements", "Die Anforderungen sind hoch."),
                        VocabularyItem("Bewerbung", "application", "Schicken Sie Ihre Bewerbung.")
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Reading about daily routines
                lessons.add(createLesenLesson(
                    title = "Alltägliche Routinen",
                    description = "Reading about daily routines and habits",
                    level = level,
                    orderIndex = 20,
                    text = """Ein Tag im Leben von Frau Schmidt

Frau Schmidt steht jeden Morgen um 6:30 Uhr auf. Zuerst duscht sie und zieht sich an. Dann bereitet sie das Frühstück für die Familie vor. Um 7:15 Uhr weckt sie ihre Kinder und sie frühstücken zusammen.

Von 8:00 bis 16:00 Uhr arbeitet sie als Krankenschwester im Krankenhaus. Ihre Schicht ist anstrengend, aber sie hilft gerne den Patienten. In der Mittagspause isst sie in der Kantine.

Nach der Arbeit kauft sie auf dem Heimweg ein. Zu Hause kocht sie das Abendessen und hilft den Kindern bei den Hausaufgaben. Abends sieht sie manchmal fern oder liest ein Buch.

Um 22:00 Uhr geht sie ins Bett. Sie braucht ihren Schlaf, denn der nächste Tag beginnt früh.""",
                    questions = listOf(
                        Question(id = 1, question = "Wann steht Frau Schmidt auf?", options = listOf("6:00 Uhr", "6:30 Uhr", "7:00 Uhr", "7:30 Uhr"), correctAnswer = "6:30 Uhr", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was ist ihr Beruf?", options = listOf("Lehrerin", "Krankenschwester", "Verkäuferin", "Köchin"), correctAnswer = "Krankenschwester", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was macht sie nach der Arbeit?", options = null, correctAnswer = "einkaufen und Abendessen kochen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("aufstehen", "to get up", "Ich stehe um 7 Uhr auf."),
                        VocabularyItem("Krankenschwester", "nurse", "Sie arbeitet als Krankenschwester."),
                        VocabularyItem("Schicht", "shift", "Die Schicht dauert 8 Stunden.")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Reading product descriptions
                lessons.add(createLesenLesson(
                    title = "Produktbeschreibungen",
                    description = "Reading product descriptions and reviews",
                    level = level,
                    orderIndex = 21,
                    text = """NEU: Smart Home Lautsprecher S-2000

Der S-2000 ist unser neuestes Modell für das smarte Zuhause. Mit diesem kabellosen Lautsprecher können Sie Musik in hoher Qualität hören.

Technische Daten:
- Bluetooth 5.0 für stabile Verbindung
- Akkulaufzeit bis zu 12 Stunden
- Wassergeschützt (IPX7)
- Sprachsteuerung möglich
- Preis: 149 Euro

Kundenbewertung: "Der Lautsprecher hat einen tollen Klang und ist sehr einfach zu bedienen. Die Akkulaufzeit ist ausgezeichnet. Ich kann ihn überall im Haus verwenden." - Maria K., Berlin

Bestellen Sie jetzt online oder besuchen Sie unser Geschäft!""",
                    questions = listOf(
                        Question(id = 1, question = "Wie lange hält der Akku?", options = listOf("6 Stunden", "8 Stunden", "10 Stunden", "12 Stunden"), correctAnswer = "12 Stunden", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was ist besonders erwähnt?", options = listOf("WLAN", "Bluetooth 5.0", "USB-Anschluss", "Radio"), correctAnswer = "Bluetooth 5.0", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Wie viel kostet der Lautsprecher?", options = null, correctAnswer = "149 Euro", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("kabellos", "wireless", "Der Lautsprecher ist kabellos."),
                        VocabularyItem("Akkulaufzeit", "battery life", "Die Akkulaufzeit ist 12 Stunden."),
                        VocabularyItem("Sprachsteuerung", "voice control", "Die Sprachsteuerung funktioniert gut.")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Reading weather reports
                lessons.add(createLesenLesson(
                    title = "Wetterberichte lesen",
                    description = "Reading weather reports and forecasts",
                    level = level,
                    orderIndex = 22,
                    text = """WETTERVORHERSAGE FÜR DEUTSCHLAND

Heute: Meist bewölkt mit Regenschauern, besonders im Norden und Westen. Temperaturen zwischen 12°C und 18°C. Wind aus West bis Nordwest, 3-5 Bft.

Morgen: Wechselhaft mit Sonne und Wolken. Im Süden sonnig, im Norden bewölkt. Temperaturen 14-22°C. Nachts kühler, Tiefstwert 8°C.

Übermorgen: Sonnig und warm. Temperaturen bis 25°C. Ideal für Outdoor-Aktivitäten.

Wochenende: Stabil mit viel Sonnenschein. Perfekt für Ausflüge und Grillen.

Unwetterwarnung: Am Donnerstag Sturmwarnung für die Küstenregionen. Wind bis 8 Bft möglich.""",
                    questions = listOf(
                        Question(id = 1, question = "Wie ist das Wetter heute?", options = listOf("Sonnig", "Meist bewölkt mit Regenschauern", "Sturm", "Schnee"), correctAnswer = "Meist bewölkt mit Regenschauern", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wann wird es sonnig und warm?", options = listOf("Heute", "Morgen", "Übermorgen", "Wochenende"), correctAnswer = "Übermorgen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Wo gibt es eine Sturmwarnung?", options = null, correctAnswer = "Küstenregionen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Regenschauer", "rain showers", "Es gibt Regenschauer."),
                        VocabularyItem("wechselhaft", "changeable", "Das Wetter ist wechselhaft."),
                        VocabularyItem("Unwetterwarnung", "severe weather warning", "Es gibt eine Unwetterwarnung.")
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Reading personal descriptions
                lessons.add(createLesenLesson(
                    title = "Personenbeschreibungen",
                    description = "Reading personal descriptions and characteristics",
                    level = level,
                    orderIndex = 23,
                    text = """Meine beste Freundin Anna

Anna ist 28 Jahre alt und wohnt in Hamburg. Sie ist Lehrerin an einer Grundschule und liebt ihren Beruf. Anna ist sehr geduldig und hat viel Verständnis für die Kinder.

Sie ist etwa 1,70 m groß und hat lange, braune Haare. Ihre Augen sind blau und sie trägt meist eine Brille. Anna kleidet sich praktisch und bequem, meist Jeans und Pullover.

In ihrer Freizeit geht sie gerne joggen oder macht Yoga. Sie interessiert sich für Umweltschutz und engagiert sich in einer Umweltorganisation. Anna kocht sehr gerne und probiert immer neue Rezepte aus.

Anna ist hilfsbereit und zuverlässig. Sie ist eine treue Freundin und immer da, wenn man sie braucht. Ich kenne sie seit der Universität und schätze ihre Ehrlichkeit und ihren Humor.""",
                    questions = listOf(
                        Question(id = 1, question = "Was ist Annas Beruf?", options = listOf("Ärztin", "Lehrerin", "Ingenieurin", "Verkäuferin"), correctAnswer = "Lehrerin", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wie sind ihre Haare?", options = listOf("Kurz und blond", "Lange und braune", "Schwarz und lockig", "Rot und kurz"), correctAnswer = "Lange und braune", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Wofür interessiert sie sich?", options = null, correctAnswer = "Umweltschutz", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("geduldig", "patient", "Die Lehrerin ist geduldig."),
                        VocabularyItem("Verständnis", "understanding", "Sie hat viel Verständnis."),
                        VocabularyItem("hilfsbereit", "helpful", "Anna ist hilfsbereit.")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Reading menu descriptions
                lessons.add(createLesenLesson(
                    title = "Speisekarten lesen",
                    description = "Reading restaurant menus and food descriptions",
                    level = level,
                    orderIndex = 24,
                    text = """RESTAURANT ZUR POST - TAGESKARTE

VORSPEISEN:
- Gulaschsuppe (traditionelle ungarische Suppe mit Paprika und Rindfleisch) - 4,50 €
- Caesar Salad (frischer Salat mit Hähnchenbrust, Parmesan und Knoblauch-Dressing) - 6,80 €

HAUPTGERICHTE:
- Wiener Schnitzel (paniertes Kalbfleisch mit Petersilienkartoffeln und Preiselbeeren) - 12,90 €
- Lachsforelle (frischer Lachs mit Zitronenbutter, Reis und Gemüse) - 14,50 €
- Vegetarisches Curry (gemischtes Gemüse in Kokosmilch mit Reis) - 11,20 €

NACHSPEISEN:
- Apfelstrudel (hausgemachter Strudel mit Vanillesauce) - 4,90 €
- Schokoladenmousse (luftige Schokoladencreme mit Beeren) - 5,20 €

Alle Preise inklusive Bedienung. Wir bitten um Reservierung unter Tel. 089-123456.""",
                    questions = listOf(
                        Question(id = 1, question = "Was kostet die Gulaschsuppe?", options = listOf("4,50 €", "6,80 €", "12,90 €", "14,50 €"), correctAnswer = "4,50 €", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was ist im Caesar Salad?", options = listOf("Fisch", "Hähnchenbrust", "Rindfleisch", "Schinken"), correctAnswer = "Hähnchenbrust", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was gibt es zum Wiener Schnitzel?", options = null, correctAnswer = "Petersilienkartoffeln und Preiselbeeren", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Vorspeise", "starter/appetizer", "Die Suppe ist eine Vorspeise."),
                        VocabularyItem("Hauptgericht", "main course", "Das Schnitzel ist ein Hauptgericht."),
                        VocabularyItem("hausgemacht", "homemade", "Der Kuchen ist hausgemacht.")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Reading appointment confirmations
                lessons.add(createLesenLesson(
                    title = "Terminbestätigungen",
                    description = "Reading appointment confirmations and schedules",
                    level = level,
                    orderIndex = 25,
                    text = """TERMINBESTÄTIGUNG

Zahnarztpraxis Dr. Müller
Maximilianstraße 12, 80539 München

Sehr geehrter Herr Schmidt,

wir bestätigen Ihren Termin für die Zahnkontrolle:
Datum: Donnerstag, 25. April 2024
Uhrzeit: 14:30 Uhr
Behandlung: Routinekontrolle und Zahnreinigung

Bitte bringen Sie folgende Unterlagen mit:
- Krankenkassenkarte
- Personalausweis
- Vorherige Röntgenbilder (falls vorhanden)

Falls Sie den Termin nicht wahrnehmen können, bitten wir um Absage bis spätestens 24 Stunden vorher.

Mit freundlichen Grüßen
Zahnarztpraxis Dr. Müller
Tel.: 089-9876543""",
                    questions = listOf(
                        Question(id = 1, question = "Wann ist der Termin?", options = listOf("25. April", "25. Mai", "25. Juni", "25. Juli"), correctAnswer = "25. April", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was soll man mitbringen?", options = listOf("Geld", "Krankenkassenkarte", "Fotoapparat", "Buch"), correctAnswer = "Krankenkassenkarte", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Bis wann sollte man absagen?", options = null, correctAnswer = "24 Stunden vorher", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Terminbestätigung", "appointment confirmation", "Die Terminbestätigung ist da."),
                        VocabularyItem("Zahnkontrolle", "dental check-up", "Die Zahnkontrolle ist wichtig."),
                        VocabularyItem("wahrnehmen", "to attend", "Können Sie den Termin wahrnehmen?")
                    ),
                    source = "TELC"
                ))
            }
            
            "B1" -> {
                lessons.add(createLesenLesson(
                    title = "B1 Goethe Prüfung - Leseverstehen Teil 1",
                    description = "Goethe-Zertifikat B1 Reading Comprehension - Authentic exam style",
                    level = level,
                    orderIndex = 1,
                    text = """Lesen Sie den folgenden Text und lösen Sie die Aufgaben.

Umweltfreundliches Reisen in Deutschland

Immer mehr Deutsche entscheiden sich für umweltfreundliche Reiseformen. Besonders beliebt ist das Bahnfahren, da die Deutsche Bahn ein dichtes Streckennetz anbietet. Viele Städte haben ein gut ausgebautes öffentliches Verkehrsnetz, das es ermöglicht, ohne Auto von A nach B zu kommen.

Eine Studie der Umweltbehörde zeigt, dass 65% der Deutschen in den letzten Jahren bewusster auf ihre CO2-Bilanz achten. Sie wählen Bahnreisen statt Flugreisen für Kurzstrecken und nutzen Elektrofahrräder für den Weg zur Arbeit. Auch Carsharing-Angebote werden immer beliebter, besonders in Großstädten wie Berlin, München und Hamburg.

Besonders Familien mit Kindern profitieren von diesen Entwicklungen. Viele Freizeitparks und Museen bieten vergünstigte Eintrittspreise für Bahnfahrer an. Außerdem gibt es zahlreiche Apps, die dabei helfen, die umweltfreundlichste Route zu planen.

Kritiker bemerken jedoch, dass diese Entwicklungen noch nicht ausreichen. Sie fordern mehr Investitionen in die Infrastruktur und bessere Anreize für umweltbewusstes Verhalten.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was ist besonders beliebt für umweltfreundliches Reisen?",
                            options = listOf("Flugreisen", "Autofahren", "Bahnfahren", "Schiffreisen"),
                            correctAnswer = "Bahnfahren",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Wie viele Deutsche achten bewusster auf ihre CO2-Bilanz?",
                            options = listOf("25%", "45%", "65%", "85%"),
                            correctAnswer = "65%",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Was wird immer beliebter in Großstädten?",
                            options = listOf("Flugreisen", "Carsharing-Angebote", "Autofahren", "Schiffreisen"),
                            correctAnswer = "Carsharing-Angebote",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was bieten viele Freizeitparks an?",
                            options = listOf("höhere Preise", "vergünstigte Eintrittspreise für Bahnfahrer", "nur Online-Tickets", "keine Familienermäßigungen"),
                            correctAnswer = "vergünstigte Eintrittspreise für Bahnfahrer",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Was fordern die Kritiker?",
                            options = listOf("weniger Investitionen", "schlechteren Service", "mehr Investitionen in die Infrastruktur", "höhere Preise"),
                            correctAnswer = "mehr Investitionen in die Infrastruktur",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("umweltfreundlich", "environmentally friendly", "Umweltfreundliches Reisen schützt die Natur."),
                        VocabularyItem("Streckennetz", "route network", "Die Bahn hat ein dichtes Streckennetz."),
                        VocabularyItem("CO2-Bilanz", "CO2 balance", "Eine gute CO2-Bilanz ist wichtig."),
                        VocabularyItem("Carsharing", "car sharing", "Carsharing ist günstig und umweltfreundlich."),
                        VocabularyItem("Infrastruktur", "infrastructure", "Die Infrastruktur muss verbessert werden.")
                    )
                ))
                
                lessons.add(createLesenLesson(
                    title = "B1 Goethe Prüfung - Leseverstehen Teil 2",
                    description = "Goethe-Zertifikat B1 Reading Comprehension - Text with gaps",
                    level = level,
                    orderIndex = 2,
                    text = """Lesen Sie den folgenden Text und lösen Sie die Aufgaben.

Arbeiten in der Zukunft

Die Arbeitswelt verändert sich durch die Digitalisierung rapide. Immer mehr Menschen arbeiten von zu Hause aus, was neue Möglichkeiten, aber auch Herausforderungen mit sich bringt. Homeoffice bietet Flexibilität, kann aber auch zu Isolation führen.

Unternehmen setzen verstärkt auf Cloud-Computing und digitale Zusammenarbeit. Videokonferenzen sind zum Alltag geworden. Künstliche Intelligenz übernimmt Routineaufgaben, während sich die Mitarbeiter auf kreative und strategische Arbeit konzentrieren können.

Besonders junge Arbeitnehmer profitieren von diesen Entwicklungen. Sie erwarten flexible Arbeitszeiten und die Möglichkeit, von überall zu arbeiten. Allerdings müssen sie auch lernen, sich selbst zu organisieren und Grenzen zwischen Arbeit und Freizeit zu setzen.

Kritiker warnen vor einer zunehmenden Arbeitsverdichtung durch ständige Erreichbarkeit. Sie fordern klare Regeln zum Schutz der Arbeitnehmer.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was bietet Homeoffice?",
                            options = listOf("nur Isolation", "nur Flexibilität", "Flexibilität und kann zu Isolation führen", "nur Stress"),
                            correctAnswer = "Flexibilität und kann zu Isolation führen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Was übernimmt Künstliche Intelligenz?",
                            options = listOf("kreative Arbeit", "strategische Arbeit", "Routineaufgaben", "Führungsaufgaben"),
                            correctAnswer = "Routineaufgaben",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Was erwarten junge Arbeitnehmer?",
                            options = listOf("feste Arbeitszeiten", "flexible Arbeitszeiten", "keine Homeoffice-Möglichkeit", "längere Arbeitszeiten"),
                            correctAnswer = "flexible Arbeitszeiten",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was müssen Arbeitnehmer lernen?",
                            options = listOf("sich selbst zu organisieren und Grenzen zu setzen", "ständig erreichbar zu sein", "nur zu Hause zu arbeiten", "keine Pausen zu machen"),
                            correctAnswer = "sich selbst zu organisieren und Grenzen zu setzen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Was warnen die Kritiker?",
                            options = listOf("vor zu wenig Arbeit", "vor zunehmender Arbeitsverdichtung", "vor zu viel Freizeit", "vor zu wenig Technologie"),
                            correctAnswer = "vor zunehmender Arbeitsverdichtung",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Digitalisierung", "digitalization", "Die Digitalisierung verändert die Arbeitswelt."),
                        VocabularyItem("Homeoffice", "home office", "Viele arbeiten im Homeoffice."),
                        VocabularyItem("Cloud-Computing", "cloud computing", "Cloud-Computing ermöglicht flexible Arbeit."),
                        VocabularyItem("Routineaufgaben", "routine tasks", "KI übernimmt Routineaufgaben."),
                        VocabularyItem("Arbeitsverdichtung", "work intensification", "Arbeitsverdichtung kann stressen.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "B1 Goethe Prüfung - Hörverstehen Teil 1",
                    description = "Goethe-Zertifikat B1 Listening Comprehension - Multiple choice",
                    level = level,
                    orderIndex = 3,
                    text = """Hören Sie sich den folgenden Dialog an und lösen Sie die Aufgaben.

Dialog: Ein Beratungsgespräch bei der Arbeitsagentur

Beraterin: Guten Tag, Herr Müller. Was kann ich für Sie tun?

Herr Müller: Ich suche eine neue Stelle. Ich bin gelernter Koch und habe 5 Jahre Erfahrung.

Beraterin: Das klingt gut. Haben Sie schon Bewerbungen geschrieben?

Herr Müller: Ja, aber ich bekomme keine Antworten. Vielleicht liegt es an meinem Lebenslauf.

Beraterin: Lassen Sie mich Ihren Lebenslauf sehen. Ah, ich sehe das Problem. Sie haben keine Weiterbildung erwähnt.

Herr Müller: Was für eine Weiterbildung meinen Sie?

Beraterin: Vielleicht einen Kurs in Hygienevorschriften oder eine Fortbildung zum Küchenchef.

Herr Müller: Das ist eine gute Idee. Wo kann ich solche Kurse machen?

Beraterin: Die Volkshochschule bietet viele Kurse an. Auch die Industrie- und Handelskammer hat Programme.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was ist Herr Müller von Beruf?",
                            options = listOf("Bäcker", "Koch", "Kellner", "Manager"),
                            correctAnswer = "Koch",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Wie viele Jahre Berufserfahrung hat er?",
                            options = listOf("2 Jahre", "3 Jahre", "5 Jahre", "7 Jahre"),
                            correctAnswer = "5 Jahre",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Was ist das Problem mit seinem Lebenslauf?",
                            options = listOf("Er hat keine Ausbildung", "Er hat keine Weiterbildung erwähnt", "Er hat keine Berufserfahrung", "Er hat keine Sprachkenntnisse"),
                            correctAnswer = "Er hat keine Weiterbildung erwähnt",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was schlägt die Beraterin vor?",
                            options = listOf("einen Kurs in Buchhaltung", "einen Kurs in Hygienevorschriften", "einen Kurs in Fremdsprachen", "einen Kurs in Sport"),
                            correctAnswer = "einen Kurs in Hygienevorschriften",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Wo kann er Kurse machen?",
                            options = listOf("nur an der Universität", "an der Volkshochschule und IHK", "nur im Internet", "nur privat"),
                            correctAnswer = "an der Volkshochschule und IHK",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Bewerbung", "application", "Ich schreibe eine Bewerbung."),
                        VocabularyItem("Lebenslauf", "CV/resume", "Der Lebenslauf ist wichtig."),
                        VocabularyItem("Weiterbildung", "further education", "Weiterbildung ist wichtig für die Karriere."),
                        VocabularyItem("Hygienevorschriften", "hygiene regulations", "In der Küche sind Hygienevorschriften wichtig."),
                        VocabularyItem("Volkshochschule", "adult education center", "Die Volkshochschule bietet viele Kurse.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "B1 Goethe Prüfung - Schreiben Teil 1",
                    description = "Goethe-Zertifikat B1 Writing - Formal letter",
                    level = level,
                    orderIndex = 4,
                    text = """Schreiben Sie eine E-Mail an Ihren Vermieter. Sie haben ein Problem mit Ihrer Wohnung.

Situation: Sie wohnen seit 3 Monaten in einer Mietwohnung. Das Badezimmer ist feucht und es gibt Schimmel. Sie möchten, dass der Vermieter das Problem behebt.

Schreiben Sie eine höfliche E-Mail mit folgenden Punkten:
- Beschreiben Sie das Problem
- Erklären Sie, seit wann es besteht
- Bitten Sie um eine schnelle Lösung
- Geben Sie Ihre Kontaktdaten an

Betreff: Problem mit Badezimmer in Wohnung [Ihre Adresse]

Sehr geehrter Herr/Frau [Name des Vermieters],

ich schreibe Ihnen, weil ich ein Problem mit meiner Wohnung habe. Ich wohne seit drei Monaten in der [Ihre Straße und Hausnummer] und bin mit der Wohnung im Allgemeinen zufrieden. Allerdings gibt es ein ernstes Problem im Badezimmer.

Das Badezimmer ist sehr feucht und es hat sich Schimmel gebildet, besonders an den Wänden und in den Ecken. Dieses Problem besteht schon seit etwa zwei Monaten. Ich habe schon versucht, das Badezimmer regelmäßig zu lüften und die Feuchtigkeit zu reduzieren, aber das Problem wird nicht besser.

Ich bitte Sie höflich, dieses Problem so schnell wie möglich zu beheben. Vielleicht muss die Feuchtigkeitssperre repariert oder erneuert werden. Ich bin unter der Telefonnummer [Ihre Telefonnummer] und per E-Mail unter [Ihre E-Mail] zu erreichen.

Ich freue mich auf Ihre baldige Antwort und hoffe, dass wir das Problem gemeinsam lösen können.

Mit freundlichen Grüßen
[Ihr Name]""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was ist das Hauptproblem in der Wohnung?",
                            options = listOf("Das Wohnzimmer ist zu klein", "Das Badezimmer ist feucht und hat Schimmel", "Die Küche ist alt", "Das Schlafzimmer ist dunkel"),
                            correctAnswer = "Das Badezimmer ist feucht und hat Schimmel",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Seit wann wohnt die Person in der Wohnung?",
                            options = listOf("seit 1 Monat", "seit 3 Monaten", "seit 6 Monaten", "seit 1 Jahr"),
                            correctAnswer = "seit 3 Monaten",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Seit wann besteht das Problem?",
                            options = listOf("seit 1 Woche", "seit 2 Wochen", "seit 2 Monaten", "seit 3 Monaten"),
                            correctAnswer = "seit 2 Monaten",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was bittet die Person vom Vermieter?",
                            options = listOf("eine neue Wohnung", "eine Mieterhöhung", "eine schnelle Lösung des Problems", "einen Umzug"),
                            correctAnswer = "eine schnelle Lösung des Problems",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Wie endet die E-Mail?",
                            options = listOf("Viele Grüße", "Hochachtungsvoll", "Mit freundlichen Grüßen", "Bis bald"),
                            correctAnswer = "Mit freundlichen Grüßen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Vermieter", "landlord", "Der Vermieter ist für die Wohnung verantwortlich."),
                        VocabularyItem("Schimmel", "mold", "Schimmel ist schädlich für die Gesundheit."),
                        VocabularyItem("Feuchtigkeit", "humidity/moisture", "Hohe Feuchtigkeit kann Probleme verursachen."),
                        VocabularyItem("beheben", "to fix/resolve", "Das Problem muss behoben werden."),
                        VocabularyItem("höflich", "polite", "Man sollte höflich schreiben.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "B1 Goethe Prüfung - Sprechen Teil 1",
                    description = "Goethe-Zertifikat B1 Speaking - Role play",
                    level = level,
                    orderIndex = 5,
                    text = """Bereiten Sie sich auf ein Rollenspiel vor.

Situation: Sie sind in einem Reisebüro und möchten eine Reise nach Berlin buchen.

Sie sprechen mit einem Reiseberater und müssen folgende Punkte besprechen:
- Reiseziel und Zeitraum
- Art der Unterkunft
- Transportmittel
- Besondere Wünsche
- Budget

Mögliche Dialogstruktur:

Reiseberater: Guten Tag! Wie kann ich Ihnen helfen?

Sie: Guten Tag! Ich möchte eine Reise nach Berlin buchen.

Reiseberater: Wann möchten Sie reisen und wie lange?

Sie: Ich möchte im nächsten Monat für eine Woche fahren.

Reiseberater: Was für eine Unterkunft suchen Sie?

Sie: Ein Hotel im Zentrum, nicht zu teuer.

Reiseberater: Wie möchten Sie anreisen?

Sie: Mit dem Zug, bitte.

Reiseberater: Haben Sie besondere Wünsche?

Sie: Ja, ich brauche ein ruhiges Zimmer und eine gute Anbindung an öffentliche Verkehrsmittel.

Reiseberater: Was ist Ihr Budget?

Sie: Etwa 800 Euro für alles zusammen.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo findet das Gespräch statt?",
                            options = listOf("im Hotel", "im Reisebüro", "am Bahnhof", "am Flughafen"),
                            correctAnswer = "im Reisebüro",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Was ist das Reiseziel?",
                            options = listOf("München", "Berlin", "Hamburg", "Köln"),
                            correctAnswer = "Berlin",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Wie lange möchte die Person bleiben?",
                            options = listOf("3 Tage", "eine Woche", "2 Wochen", "einen Monat"),
                            correctAnswer = "eine Woche",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Wie möchte die Person anreisen?",
                            options = listOf("mit dem Auto", "mit dem Flugzeug", "mit dem Zug", "mit dem Bus"),
                            correctAnswer = "mit dem Zug",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Was ist ungefähr das Budget?",
                            options = listOf("400 Euro", "800 Euro", "1200 Euro", "2000 Euro"),
                            correctAnswer = "800 Euro",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Reisebüro", "travel agency", "Im Reisebüro kann man Reisen buchen."),
                        VocabularyItem("Unterkunft", "accommodation", "Welche Unterkunft suchen Sie?"),
                        VocabularyItem("Transportmittel", "means of transport", "Das Transportmittel ist wichtig."),
                        VocabularyItem("Budget", "budget", "Mein Budget ist begrenzt."),
                        VocabularyItem("Anbindung", "connection/access", "Gute Anbindung an öffentliche Verkehrsmittel.")
                    )
                ))
            }
            
            "B2" -> {
                lessons.add(createLesenLesson(
                    title = "B2 Goethe Prüfung - Leseverstehen Teil 1",
                    description = "Goethe-Zertifikat B2 Reading Comprehension - Academic text analysis",
                    level = level,
                    orderIndex = 1,
                    text = """Lesen Sie den folgenden akademischen Text und lösen Sie die Aufgaben.

Die Auswirkungen der Globalisierung auf die Arbeitsmärkte

Die Globalisierung hat in den letzten Jahrzehnten zu einer fundamentalen Transformation der Arbeitsmärkte geführt. Während Befürworter argumentieren, dass die Öffnung der Märkte zu höherem Wirtschaftswachstum und mehr Arbeitsplätzen führt, kritisieren Gegner die negativen sozialen Konsequenzen.

Empirische Studien zeigen, dass die Globalisierung zu einer Polarisierung der Arbeitsmärkte beiträgt. Hochqualifizierte Arbeitskräfte profitieren von der internationalen Arbeitsteilung und höheren Löhnen, während gering qualifizierte Arbeitnehmer häufig mit Lohnsenkungen und Arbeitsplatzverlust konfrontiert sind. Besonders betroffen sind Branchen wie die Textilindustrie und die Fertigung, die einer starken Konkurrenz aus Niedriglohnländern ausgesetzt sind.

Ein weiterer Aspekt ist die Zunahme prekärer Beschäftigungsverhältnisse. Leiharbeit, befristete Verträge und Teilzeitbeschäftigung sind in vielen OECD-Ländern deutlich angestiegen. Dies führt zu einer Erosion der sozialen Sicherheit und einer zunehmenden Unsicherheit für Arbeitnehmer.

Allerdings gibt es auch positive Entwicklungen. Die Globalisierung fördert den Wissensaustausch und die Innovation. Unternehmen investieren mehr in Forschung und Entwicklung, was wiederum neue, hochwertige Arbeitsplätze schafft. Zudem ermöglicht die internationale Mobilität vielen Menschen den Zugang zu besseren Bildungs- und Karrierechancen.

Die Herausforderung besteht darin, die Vorteile der Globalisierung zu nutzen, ohne die sozialen Kosten zu ignorieren. Politische Maßnahmen wie Weiterbildungsprogramme, soziale Absicherungen und progressive Besteuerung können dazu beitragen, die negativen Auswirkungen abzumildern.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Welche These vertreten die Befürworter der Globalisierung?",
                            options = listOf("Sie führt zu Wirtschaftswachstum und mehr Arbeitsplätzen", "Sie zerstört alle Arbeitsplätze", "Sie hat keine Auswirkungen", "Sie führt nur zu Problemen"),
                            correctAnswer = "Sie führt zu Wirtschaftswachstum und mehr Arbeitsplätzen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Was passiert mit gering qualifizierten Arbeitnehmern?",
                            options = listOf("Sie bekommen höhere Löhne", "Sie haben Lohnsenkungen und Arbeitsplatzverlust", "Sie sind nicht betroffen", "Sie bekommen mehr Urlaub"),
                            correctAnswer = "Sie haben Lohnsenkungen und Arbeitsplatzverlust",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Was sind Beispiele für prekäre Beschäftigung?",
                            options = listOf("Festanstellung und hohe Löhne", "Leiharbeit und befristete Verträge", "Nur Vollzeitstellen", "Nur Selbstständigkeit"),
                            correctAnswer = "Leiharbeit und befristete Verträge",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Welche positiven Entwicklungen nennt der Text?",
                            options = listOf("Wissensaustausch und Innovation", "nur Arbeitsplatzverlust", "nur Lohnsenkungen", "nur Unsicherheit"),
                            correctAnswer = "Wissensaustausch und Innovation",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Was schlägt der Autor als politische Maßnahmen vor?",
                            options = listOf("Weiterbildungsprogramme und soziale Absicherungen", "Schließung aller Grenzen", "Abschaffung aller Steuern", "Verbot von Innovation"),
                            correctAnswer = "Weiterbildungsprogramme und soziale Absicherungen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Globalisierung", "globalization", "Die Globalisierung verändert die Wirtschaft."),
                        VocabularyItem("Polarisierung", "polarization", "Die Polarisierung der Arbeitsmärkte nimmt zu."),
                        VocabularyItem("prekäre Beschäftigung", "precarious employment", "Prekäre Beschäftigung ist unsicher."),
                        VocabularyItem("Wissensaustausch", "knowledge exchange", "Wissensaustausch fördert Innovation."),
                        VocabularyItem("soziale Absicherungen", "social security measures", "Soziale Absicherungen sind wichtig.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "B2 Goethe Prüfung - Leseverstehen Teil 1",
                    description = "Goethe-Zertifikat B2 Reading Comprehension - Academic text analysis",
                    level = level,
                    orderIndex = 1,
                    text = """Lesen Sie den folgenden akademischen Text und lösen Sie die Aufgaben.

Die Auswirkungen der Globalisierung auf die Arbeitsmärkte

Die Globalisierung hat in den letzten Jahrzehnten zu einer fundamentalen Transformation der Arbeitsmärkte geführt. Während Befürworter argumentieren, dass die Öffnung der Märkte zu höherem Wirtschaftswachstum und mehr Arbeitsplätzen führt, kritisieren Gegner die negativen sozialen Konsequenzen.

Empirische Studien zeigen, dass die Globalisierung zu einer Polarisierung der Arbeitsmärkte beiträgt. Hochqualifizierte Arbeitskräfte profitieren von der internationalen Arbeitsteilung und höheren Löhnen, während gering qualifizierte Arbeitnehmer häufig mit Lohnsenkungen und Arbeitsplatzverlust konfrontiert sind. Besonders betroffen sind Branchen wie die Textilindustrie und die Fertigung, die einer starken Konkurrenz aus Niedriglohnländern ausgesetzt sind.

Ein weiterer Aspekt ist die Zunahme prekärer Beschäftigungsverhältnisse. Leiharbeit, befristete Verträge und Teilzeitbeschäftigung sind in vielen OECD-Ländern deutlich angestiegen. Dies führt zu einer Erosion der sozialen Sicherheit und einer zunehmenden Unsicherheit für Arbeitnehmer.

Allerdings gibt es auch positive Entwicklungen. Die Globalisierung fördert den Wissensaustausch und die Innovation. Unternehmen investieren mehr in Forschung und Entwicklung, was wiederum neue, hochwertige Arbeitsplätze schafft. Zudem ermöglicht die internationale Mobilität vielen Menschen den Zugang zu besseren Bildungs- und Karrierechancen.

Die Herausforderung besteht darin, die Vorteile der Globalisierung zu nutzen, ohne die sozialen Kosten zu ignorieren. Politische Maßnahmen wie Weiterbildungsprogramme, soziale Absicherungen und progressive Besteuerung können dazu beitragen, die negativen Auswirkungen abzumildern.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Welche These vertreten die Befürworter der Globalisierung?",
                            options = listOf("Sie führt zu Wirtschaftswachstum und mehr Arbeitsplätzen", "Sie zerstört alle Arbeitsplätze", "Sie hat keine Auswirkungen", "Sie führt nur zu Problemen"),
                            correctAnswer = "Sie führt zu Wirtschaftswachstum und mehr Arbeitsplätzen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Was passiert mit gering qualifizierten Arbeitnehmern?",
                            options = listOf("Sie bekommen höhere Löhne", "Sie haben Lohnsenkungen und Arbeitsplatzverlust", "Sie sind nicht betroffen", "Sie bekommen mehr Urlaub"),
                            correctAnswer = "Sie haben Lohnsenkungen und Arbeitsplatzverlust",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Was sind Beispiele für prekäre Beschäftigung?",
                            options = listOf("Festanstellung und hohe Löhne", "Leiharbeit und befristete Verträge", "Nur Vollzeitstellen", "Nur Selbstständigkeit"),
                            correctAnswer = "Leiharbeit und befristete Verträge",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Welche positiven Entwicklungen nennt der Text?",
                            options = listOf("Wissensaustausch und Innovation", "nur Arbeitsplatzverlust", "nur Lohnsenkungen", "nur Unsicherheit"),
                            correctAnswer = "Wissensaustausch und Innovation",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Was schlägt der Autor als politische Maßnahmen vor?",
                            options = listOf("Weiterbildungsprogramme und soziale Absicherungen", "Schließung aller Grenzen", "Abschaffung aller Steuern", "Verbot von Innovation"),
                            correctAnswer = "Weiterbildungsprogramme und soziale Absicherungen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Globalisierung", "globalization", "Die Globalisierung verändert die Wirtschaft."),
                        VocabularyItem("Polarisierung", "polarization", "Die Polarisierung der Arbeitsmärkte nimmt zu."),
                        VocabularyItem("prekäre Beschäftigung", "precarious employment", "Prekäre Beschäftigung ist unsicher."),
                        VocabularyItem("Wissensaustausch", "knowledge exchange", "Wissensaustausch fördert Innovation."),
                        VocabularyItem("soziale Absicherungen", "social security measures", "Soziale Absicherungen sind wichtig.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "Wissenschaft und Forschung",
                    description = "Science and research",
                    level = level,
                    orderIndex = 2,
                    text = "Deutschland ist ein führendes Land in der Wissenschaft und Forschung. Deutsche Universitäten und Forschungsinstitute genießen weltweit hohes Ansehen. Besonders in den Bereichen Medizin, Ingenieurwesen und Naturwissenschaften werden wichtige Entdeckungen gemacht. Die Zusammenarbeit zwischen Universitäten und Unternehmen ist sehr eng. Viele internationale Studenten kommen nach Deutschland, um zu studieren und zu forschen.",
                    questions = listOf(
                        Question(id = 1, question = "Was ist Deutschland in der Wissenschaft?", options = listOf("Ein führendes Land", "Ein kleines Land", "Ein unbedeutendes Land", "Ein armes Land"), correctAnswer = "Ein führendes Land", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "In welchen Bereichen werden Entdeckungen gemacht?", options = listOf("Nur in Kunst", "Nur in Literatur", "Medizin, Ingenieurwesen, Naturwissenschaften", "Nur in Geschichte"), correctAnswer = "Medizin, Ingenieurwesen, Naturwissenschaften", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Warum kommen internationale Studenten nach Deutschland?", options = null, correctAnswer = "um zu studieren und zu forschen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Forschung", "research", "Deutschland ist führend in der Forschung."),
                        VocabularyItem("Universität", "university", "Ich studiere an der Universität."),
                        VocabularyItem("Entdeckung", "discovery", "Wichtige Entdeckungen werden gemacht.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "Kunst und Kultur",
                    description = "Art and culture",
                    level = level,
                    orderIndex = 3,
                    text = "Deutschland hat eine reiche kulturelle Tradition. Berühmte Komponisten wie Beethoven, Bach und Mozart haben hier gelebt und gearbeitet. Die deutsche Literatur ist weltweit bekannt, mit Autoren wie Goethe, Schiller und Kafka. Deutsche Museen beherbergen wertvolle Kunstsammlungen. Das Theater und die Oper haben eine lange Tradition. Moderne deutsche Künstler sind international erfolgreich.",
                    questions = listOf(
                        Question(id = 1, question = "Was haben Beethoven, Bach und Mozart gemeinsam?", options = listOf("Sie waren Maler", "Sie waren Komponisten", "Sie waren Schriftsteller", "Sie waren Architekten"), correctAnswer = "Sie waren Komponisten", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Welche deutschen Autoren sind bekannt?", options = listOf("Nur moderne Autoren", "Goethe, Schiller, Kafka", "Nur englische Autoren", "Nur französische Autoren"), correctAnswer = "Goethe, Schiller, Kafka", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was haben deutsche Museen?", options = null, correctAnswer = "wertvolle Kunstsammlungen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Komponist", "composer", "Beethoven war ein Komponist."),
                        VocabularyItem("Literatur", "literature", "Die deutsche Literatur ist bekannt."),
                        VocabularyItem("Kunstsammlung", "art collection", "Das Museum hat eine Kunstsammlung.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "Geschichte und Erinnerung",
                    description = "History and memory",
                    level = level,
                    orderIndex = 4,
                    text = "Die deutsche Geschichte ist komplex und oft schwierig. Das 20. Jahrhundert war geprägt von zwei Weltkriegen und der Teilung des Landes. Nach dem Fall der Mauer 1989 wurde Deutschland wiedervereinigt. Heute ist Deutschland ein demokratisches Land, das sich seiner historischen Verantwortung stellt. Gedenkstätten und Museen erinnern an die dunklen Kapitel der Geschichte. Die Erinnerungskultur ist wichtig für die Zukunft.",
                    questions = listOf(
                        Question(id = 1, question = "Wann wurde Deutschland wiedervereinigt?", options = listOf("1985", "1989", "1990", "1995"), correctAnswer = "1989", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was war das 20. Jahrhundert geprägt von?", options = listOf("Nur Frieden", "Zwei Weltkriegen und Teilung", "Nur Wirtschaftskrisen", "Nur technologischen Fortschritten"), correctAnswer = "Zwei Weltkriegen und Teilung", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was ist wichtig für die Zukunft?", options = null, correctAnswer = "Erinnerungskultur", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Geschichte", "history", "Die deutsche Geschichte ist komplex."),
                        VocabularyItem("Wiedervereinigung", "reunification", "Die Wiedervereinigung war 1989."),
                        VocabularyItem("Gedenkstätte", "memorial", "Gedenkstätten erinnern an die Geschichte.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "Sprache und Identität",
                    description = "Language and identity",
                    level = level,
                    orderIndex = 5,
                    text = "Die deutsche Sprache ist eine wichtige europäische Sprache mit über 100 Millionen Sprechern. Sie ist die Amtssprache in Deutschland, Österreich und der Schweiz. Deutsch hat viele Dialekte und regionale Varianten. Die Sprache ist ein wichtiger Teil der deutschen Identität und Kultur. Viele deutsche Wörter sind in andere Sprachen übernommen worden. Das Erlernen der deutschen Sprache öffnet viele Türen.",
                    questions = listOf(
                        Question(id = 1, question = "Wie viele Menschen sprechen Deutsch?", options = listOf("50 Millionen", "100 Millionen", "150 Millionen", "200 Millionen"), correctAnswer = "100 Millionen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "In welchen Ländern ist Deutsch Amtssprache?", options = listOf("Nur Deutschland", "Deutschland, Österreich, Schweiz", "Nur Österreich", "Nur Schweiz"), correctAnswer = "Deutschland, Österreich, Schweiz", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was ist ein wichtiger Teil der deutschen Identität?", options = null, correctAnswer = "die Sprache", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Amtssprache", "official language", "Deutsch ist Amtssprache in Deutschland."),
                        VocabularyItem("Dialekt", "dialect", "Deutsch hat viele Dialekte."),
                        VocabularyItem("Identität", "identity", "Die Sprache ist Teil der Identität.")
                    )
                ))
            }


            
            "C1" -> {
                lessons.add(createLesenLesson(
                    title = "C1 Goethe Prüfung - Leseverstehen Teil 1",
                    description = "Goethe-Zertifikat C1 Reading Comprehension - Philosophical discourse",
                    level = level,
                    orderIndex = 1,
                    text = """Lesen Sie den folgenden philosophischen Text und lösen Sie die Aufgaben.

Die Krise der Demokratie im digitalen Zeitalter

Die liberale Demokratie, einst als Garant individueller Freiheit und politischer Partizipation gefeiert, steht heute vor einer tiefgreifenden Legitimationskrise. Während die Institutionen der repräsentativen Demokratie zunehmend an Vertrauen verlieren, eröffnen digitale Technologien neue Möglichkeiten der politischen Teilhabe, bergen jedoch auch erhebliche Risiken für die demokratische Kultur.

Das Paradoxon der digitalen Demokratie manifestiert sich in der Spannung zwischen Inklusion und Fragmentierung. Einerseits ermöglichen soziale Netzwerke eine beispiellose politische Mobilisierung und den Austausch von Informationen jenseits traditioneller Medienkanäle. Andererseits fördern algorithmische Filterblasen und Echokammern die Polarisierung der Gesellschaft, indem sie individuelle Weltanschauungen verstärken und den Diskurs mit Andersdenkenden erschweren.

Kritiker der deliberativen Demokratie argumentieren, dass die Komplexität moderner Gesellschaften eine rationale, diskursive Entscheidungsfindung unmöglich macht. Stattdessen dominieren emotionale Appelle und vereinfachende Narrative die politische Debatte. Die Fragmentierung der Öffentlichkeit in eine Vielzahl von Teilöffentlichkeiten untergräbt die Möglichkeit eines gemeinsamen gesellschaftlichen Diskurses.

Dennoch eröffnen digitale Technologien auch innovative Formen der Partizipation. Blockchain-basierte Abstimmungssysteme könnten die Integrität von Wahlen stärken, während Crowdsourcing-Plattformen eine breitere Beteiligung an politischen Entscheidungsprozessen ermöglichen. Die Herausforderung besteht darin, diese Innovationen so zu gestalten, dass sie die Prinzipien der Gleichheit und Transparenz stärken, anstatt sie zu untergraben.

Die Zukunft der Demokratie wird davon abhängen, ob es gelingt, die Potentiale der Digitalisierung für eine Erneuerung demokratischer Institutionen zu nutzen, ohne die fundamentalen Werte der Aufklärung zu kompromittieren.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Welche zwei Aspekte bilden das Paradoxon der digitalen Demokratie?",
                            options = listOf("Inklusion und Fragmentierung", "Freiheit und Kontrolle", "Teilhabe und Isolation", "Innovation und Tradition"),
                            correctAnswer = "Inklusion und Fragmentierung",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Welche zwei negativen Auswirkungen werden genannt?",
                            options = listOf("Polarisierung und erschwerter Diskurs", "mehr Vertrauen und bessere Entscheidungen", "weniger Mobilisierung und Informationsaustausch", "stärkere Institutionen und gemeinsamer Diskurs"),
                            correctAnswer = "Polarisierung und erschwerter Diskurs",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Welche zwei innovativen Formen der Partizipation werden erwähnt?",
                            options = listOf("Blockchain-basierte Abstimmungssysteme und Crowdsourcing-Plattformen", "traditionelle Medien und repräsentative Demokratie", "emotionale Appelle und vereinfachende Narrative", "Filterblasen und Echokammern"),
                            correctAnswer = "Blockchain-basierte Abstimmungssysteme und Crowdsourcing-Plattformen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Verbinden Sie die Begriffe mit ihren Definitionen:",
                            options = null,
                            correctAnswer = "",
                            correctAnswers = null,
                            type = QuestionType.TEXT_MATCHING,
                            matchingItems = mapOf(
                                "Legitimationskrise" to "Vertrauensverlust in Institutionen",
                                "Filterblasen" to "Algorithmische Verstärkung von Meinungen",
                                "deliberative Demokratie" to "Diskursive Entscheidungsfindung",
                                "Crowdsourcing" to "Breite Beteiligung an Entscheidungen"
                            )
                        ),
                        Question(
                            id = 5,
                            question = "Die liberale Demokratie steht vor einer tiefgreifenden Krise.",
                            options = null,
                            correctAnswer = "Richtig",
                            correctAnswers = null,
                            type = QuestionType.TRUE_FALSE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Legitimationskrise", "legitimation crisis", "Die Demokratie steht vor einer Legitimationskrise."),
                        VocabularyItem("Filterblasen", "filter bubbles", "Filterblasen verstärken die Polarisierung."),
                        VocabularyItem("deliberativ", "deliberative", "Die deliberative Demokratie ist in der Krise."),
                        VocabularyItem("Echokammern", "echo chambers", "Echokammern verstärken eigene Meinungen."),
                        VocabularyItem("Crowdsourcing", "crowdsourcing", "Crowdsourcing ermöglicht breitere Beteiligung.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "C1 Goethe Prüfung - Leseverstehen Teil 2",
                    description = "Goethe-Zertifikat C1 Reading Comprehension - Literary analysis",
                    level = level,
                    orderIndex = 2,
                    text = """Lesen Sie den folgenden literaturwissenschaftlichen Text und lösen Sie die Aufgaben.

Die narrative Komplexität in der modernen deutschsprachigen Literatur

Die deutschsprachige Literatur der Gegenwart zeichnet sich durch eine zunehmende narrative Komplexität aus, die traditionelle Erzählkonventionen herausfordert und neue Formen der Leserpartizipation erfordert. Diese Entwicklung manifestiert sich in einer Vielzahl von experimentellen Erzählstrategien, die von der Postmoderne inspiriert sind und die Grenzen zwischen Fiktion und Realität bewusst verwischen.

Ein zentrales Merkmal dieser Entwicklung ist die Metafiktion, bei der der Text selbstreflexiv auf seine eigene Konstruiertheit verweist. Autoren wie Daniel Kehlmann oder Felicitas Hoppe bedienen sich dieser Technik, um die Arbitrarität literarischer Konstruktionen zu thematisieren und den Leser zur aktiven Auseinandersetzung mit dem Text zu provozieren.

Parallel dazu beobachten wir eine Renaissance des multiperspektivischen Erzählens, das unterschiedliche Sichtweisen auf ein Ereignis nebeneinanderstellt und absolute Wahrheit als Konstrukt entlarvt. Diese Technik findet sich prominent in den Werken von Katharina Hacker oder Terézia Mora, wo die Fragmentierung der Perspektiven die Komplexität moderner Identitätskonstruktionen widerspiegelt.

Besonders innovativ sind die Versuche, digitale Erzählformen in die Literatur zu integrieren. Hypertextuelle Strukturen und interaktive Elemente erweitern den literarischen Raum und fordern traditionelle Vorstellungen von Linearität und Kohärenz heraus. Diese Entwicklung wirft fundamentale Fragen nach der Natur des Lesens und der Autorenschaft auf.

Kritiker dieser Entwicklung argumentieren, dass die zunehmende Komplexität den Zugang zur Literatur erschwert und elitäre Tendenzen verstärkt. Befürworter hingegen sehen darin eine notwendige Adaption an die Komplexität der modernen Welt, die neue Möglichkeiten der ästhetischen Erfahrung eröffnet.

Die Zukunft der deutschsprachigen Literatur wird davon abhängen, ob es gelingt, innovative Erzählformen mit einer breiten Lesbarkeit zu verbinden.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was zeichnet die deutschsprachige Literatur der Gegenwart aus?",
                            options = listOf("zunehmende narrative Komplexität", "Rückkehr zu traditionellen Formen", "Vereinfachung der Erzählungen", "Ablehnung von Experimenten"),
                            correctAnswer = "zunehmende narrative Komplexität",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Was ist ein zentrales Merkmal der Metafiktion?",
                            options = listOf("der Text verweist selbstreflexiv auf seine Konstruiertheit", "der Text ist rein deskriptiv", "der Text vermeidet jegliche Reflexion", "der Text ist rein autobiografisch"),
                            correctAnswer = "der Text verweist selbstreflexiv auf seine Konstruiertheit",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Was entlarvt das multiperspektivische Erzählen?",
                            options = listOf("absolute Wahrheit als Konstrukt", "die Notwendigkeit einer einzigen Perspektive", "die Überlegenheit des Autors", "die Bedeutungslosigkeit von Identität"),
                            correctAnswer = "absolute Wahrheit als Konstrukt",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was erweitern hypertextuelle Strukturen?",
                            options = listOf("den literarischen Raum", "nur die Seitenzahl", "nur die Schriftgröße", "nur die Rechtschreibung"),
                            correctAnswer = "den literarischen Raum",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Was sehen Kritiker als Problem?",
                            options = listOf("die zunehmende Komplexität erschwert den Zugang zur Literatur", "die Literatur wird zu einfach", "die Autoren werden zu traditionell", "die Leser werden zu aktiv"),
                            correctAnswer = "die zunehmende Komplexität erschwert den Zugang zur Literatur",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("narrative Komplexität", "narrative complexity", "Die narrative Komplexität fordert den Leser heraus."),
                        VocabularyItem("Metafiktion", "metafiction", "Metafiktion verweist auf die eigene Konstruiertheit."),
                        VocabularyItem("multiperspektivisch", "multiperspectival", "Multiperspektivisches Erzählen stellt verschiedene Sichtweisen nebeneinander."),
                        VocabularyItem("hypertextuell", "hypertextual", "Hypertextuelle Strukturen erweitern den literarischen Raum."),
                        VocabularyItem("selbstreflexiv", "self-reflexive", "Der Text ist selbstreflexiv.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "C1 Goethe Prüfung - Hörverstehen Teil 1",
                    description = "Goethe-Zertifikat C1 Listening Comprehension - Academic lecture",
                    level = level,
                    orderIndex = 3,
                    text = """Hören Sie sich den folgenden Vortrag an und lösen Sie die Aufgaben.

Vortrag: "Die Psychologie der Entscheidungsfindung in der modernen Gesellschaft"

Sehr geehrte Damen und Herren,

die Entscheidungsfindung in unserer komplexen modernen Gesellschaft stellt Psychologen vor neue Herausforderungen. Während traditionelle Modelle rationale Entscheidungen als Ergebnis logischer Abwägungen betrachteten, zeigen neuere Forschungen, dass Emotionen und Intuition eine wesentlich größere Rolle spielen als bisher angenommen.

Das sogenannte "Dual-Process-Modell" unterscheidet zwischen schnellen, intuitiven Entscheidungen (System 1) und langsamen, analytischen Prozessen (System 2). System 1 arbeitet automatisch und emotionsgesteuert, während System 2 bewusste kognitive Anstrengungen erfordert. In vielen Alltagssituationen dominiert System 1 unsere Entscheidungen, obwohl wir glauben, rational zu handeln.

Besonders problematisch wird dies bei komplexen Entscheidungen unter Unsicherheit. Die Prospect-Theorie von Kahneman und Tversky zeigt, dass Menschen Verluste stärker gewichten als Gewinne gleicher Größe und dabei systematische Verzerrungen zeigen. Diese Erkenntnisse haben weitreichende Implikationen für Wirtschaft, Politik und persönliche Lebensplanung.

Im Kontext der Digitalisierung verstärken sich diese Phänomene noch. Algorithmen beeinflussen unsere Entscheidungen, ohne dass wir uns dessen bewusst sind. Von Empfehlungssystemen bis zu Suchalgorithmen - unsere scheinbar freien Entscheidungen sind oft das Ergebnis subtiler Manipulationen.

Die Herausforderung besteht darin, Menschen für diese psychologischen Mechanismen zu sensibilisieren, ohne sie zu überfordern. Bildung und Training können helfen, das Gleichgewicht zwischen Intuition und rationaler Analyse zu verbessern.

Vielen Dank für Ihre Aufmerksamkeit.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was zeigen neuere Forschungen über Entscheidungen?",
                            options = listOf("Emotionen und Intuition spielen eine größere Rolle", "nur rationale Abwägungen sind wichtig", "Intuition ist völlig irrelevant", "nur Logik entscheidet"),
                            correctAnswer = "Emotionen und Intuition spielen eine größere Rolle",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Was unterscheidet das Dual-Process-Modell?",
                            options = listOf("schnelle, intuitive Entscheidungen und langsame, analytische Prozesse", "nur schnelle Entscheidungen", "nur langsame Entscheidungen", "emotionale und logische Prozesse"),
                            correctAnswer = "schnelle, intuitive Entscheidungen und langsame, analytische Prozesse",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Was zeigt die Prospect-Theorie?",
                            options = listOf("Menschen gewichten Verluste stärker als Gewinne", "Gewinne sind wichtiger als Verluste", "alle Entscheidungen sind rational", "Intuition ist immer richtig"),
                            correctAnswer = "Menschen gewichten Verluste stärker als Gewinne",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was verstärkt die Digitalisierung?",
                            options = listOf("psychologische Mechanismen der Entscheidungsfindung", "nur rationale Entscheidungen", "nur intuitive Entscheidungen", "die Bedeutung von Emotionen"),
                            correctAnswer = "psychologische Mechanismen der Entscheidungsfindung",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Was ist die Herausforderung laut dem Vortrag?",
                            options = listOf("Menschen für psychologische Mechanismen zu sensibilisieren", "alle Intuition zu unterdrücken", "nur auf Algorithmen zu vertrauen", "Entscheidungen zu automatisieren"),
                            correctAnswer = "Menschen für psychologische Mechanismen zu sensibilisieren",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Entscheidungsfindung", "decision making", "Die Entscheidungsfindung ist komplex."),
                        VocabularyItem("Dual-Process-Modell", "dual-process model", "Das Modell unterscheidet zwei Systeme."),
                        VocabularyItem("Prospect-Theorie", "prospect theory", "Die Theorie erklärt Verlustaversion."),
                        VocabularyItem("Verzerrungen", "biases/cognitive distortions", "Systematische Verzerrungen beeinflussen Entscheidungen."),
                        VocabularyItem("sensibilisieren", "to sensitize", "Menschen müssen sensibilisiert werden.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "C1 Goethe Prüfung - Schreiben Teil 1",
                    description = "Goethe-Zertifikat C1 Writing - Formal essay",
                    level = level,
                    orderIndex = 4,
                    text = """Schreiben Sie einen Essay zum folgenden Thema:

"Thesenpapier: Die Auswirkungen der Digitalisierung auf die Gesellschaft"

Schreiben Sie einen zusammenhängenden Text (ca. 300 Wörter) zum Thema "Die Auswirkungen der Digitalisierung auf die Gesellschaft". Gehen Sie auf folgende Aspekte ein:

1. Positive Auswirkungen der Digitalisierung
2. Negative Auswirkungen und Risiken
3. Gesellschaftliche Herausforderungen
4. Mögliche Lösungsansätze

Strukturieren Sie Ihren Text klar und verwenden Sie angemessene formale Sprache.

Beispielstruktur:

Einleitung:
- Definition und Bedeutung der Digitalisierung
- These des Essays

Hauptteil:
- Positive Aspekte (Kommunikation, Bildung, Wirtschaft)
- Negative Aspekte (Privatsphäre, soziale Isolation, Arbeitsmarkt)
- Gesellschaftliche Konsequenzen (Ungleichheit, Demokratie, Kultur)

Schluss:
- Zusammenfassung der wichtigsten Punkte
- Ausblick auf zukünftige Entwicklungen
- Eigene Bewertung

Verwenden Sie komplexe Satzstrukturen und akademische Vokabeln:
- fundamentale Transformation
- paradoxe Entwicklung
- weitreichende Implikationen
- kritische Auseinandersetzung
- nachhaltige Lösungsansätze""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was ist das Thema des Essays?",
                            options = listOf("Auswirkungen der Digitalisierung auf die Gesellschaft", "Vorteile der Digitalisierung", "Gefahren der Digitalisierung", "Zukunft der Technologie"),
                            correctAnswer = "Auswirkungen der Digitalisierung auf die Gesellschaft",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Welche Aspekte soll der Essay behandeln?",
                            options = listOf("nur positive Auswirkungen", "Positive und negative Auswirkungen, Herausforderungen und Lösungen", "nur technische Aspekte", "nur wirtschaftliche Aspekte"),
                            correctAnswer = "Positive und negative Auswirkungen, Herausforderungen und Lösungen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Wie sollte der Text strukturiert sein?",
                            options = listOf("Einleitung, Hauptteil, Schluss", "nur Einleitung", "nur Schluss", "ohne Struktur"),
                            correctAnswer = "Einleitung, Hauptteil, Schluss",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Welche Sprache sollte verwendet werden?",
                            options = listOf("angemessene formale Sprache", "Umgangssprache", "nur Fachbegriffe", "einfache Sätze"),
                            correctAnswer = "angemessene formale Sprache",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Wie lang sollte der Text sein?",
                            options = listOf("ca. 300 Wörter", "100 Wörter", "500 Wörter", "keine Begrenzung"),
                            correctAnswer = "ca. 300 Wörter",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Digitalisierung", "digitization", "Die Digitalisierung verändert die Gesellschaft."),
                        VocabularyItem("fundamentale Transformation", "fundamental transformation", "Wir erleben eine fundamentale Transformation."),
                        VocabularyItem("weitreichende Implikationen", "far-reaching implications", "Das hat weitreichende Implikationen."),
                        VocabularyItem("kritische Auseinandersetzung", "critical examination", "Eine kritische Auseinandersetzung ist notwendig."),
                        VocabularyItem("nachhaltige Lösungsansätze", "sustainable solutions", "Wir brauchen nachhaltige Lösungsansätze.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "C1 Goethe Prüfung - Sprechen Teil 1",
                    description = "Goethe-Zertifikat C1 Speaking - Academic presentation",
                    level = level,
                    orderIndex = 5,
                    text = """Bereiten Sie eine Präsentation vor und führen Sie ein Gespräch.

Thema: "Sprache und Identität in der globalisierten Welt"

Bereiten Sie eine kurze Präsentation (ca. 5 Minuten) vor und führen Sie anschließend ein Gespräch. Gehen Sie auf folgende Aspekte ein:

1. Die Rolle der Sprache bei der Identitätsbildung
2. Auswirkungen der Globalisierung auf Sprachenvielfalt
3. Mehrsprachigkeit als Chance oder Herausforderung
4. Sprachliche Anpassungen in der digitalen Kommunikation

Struktur Ihrer Präsentation:
- Einleitung: Definition zentraler Begriffe
- Hauptteil: Analyse der verschiedenen Aspekte
- Beispiele aus verschiedenen Kulturen
- Schluss: Eigene Position und Ausblick

Mögliche Gesprächsfragen:
- Wie beeinflusst die Sprache unsere kulturelle Identität?
- Sollten Minderheitensprachen geschützt werden?
- Welche Rolle spielen Sprachen in der internationalen Kommunikation?
- Wie verändert sich Sprache durch neue Medien?

Verwenden Sie komplexe Argumentationsstrukturen:
- Einerseits... andererseits...
- Zunächst... darüber hinaus...
- Kritiker argumentieren... Befürworter hingegen...
- Empirische Studien zeigen...
- Die Konsequenz daraus ist...

Präsentationsbeispiel:
"Sehr geehrte Damen und Herren,

heute möchte ich mit Ihnen über das Thema 'Sprache und Identität in der globalisierten Welt' sprechen. Dieser komplexe Zusammenhang hat weitreichende Implikationen für unsere Gesellschaft.

Zunächst einmal ist festzustellen, dass Sprache nicht nur ein Kommunikationsmittel ist, sondern auch ein wesentlicher Bestandteil unserer Identität. Sie prägt unser Denken, unsere kulturellen Werte und unsere Zugehörigkeit zu bestimmten Gruppen."...

Nach der Präsentation: Diskussion und Beantwortung von Fragen.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was ist das Thema der Präsentation?",
                            options = listOf("Sprache und Identität in der globalisierten Welt", "Sprache und Technologie", "Identität ohne Sprache", "Globalisierung ohne Sprache"),
                            correctAnswer = "Sprache und Identität in der globalisierten Welt",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Welche Aspekte soll die Präsentation behandeln?",
                            options = listOf("Rolle der Sprache, Globalisierungsauswirkungen, Mehrsprachigkeit, digitale Anpassungen", "nur technische Aspekte", "nur historische Aspekte", "nur grammatikalische Aspekte"),
                            correctAnswer = "Rolle der Sprache, Globalisierungsauswirkungen, Mehrsprachigkeit, digitale Anpassungen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Wie sollte die Präsentation strukturiert sein?",
                            options = listOf("Einleitung, Hauptteil mit Beispielen, Schluss", "nur Einleitung", "nur Beispiele", "ohne Struktur"),
                            correctAnswer = "Einleitung, Hauptteil mit Beispielen, Schluss",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was soll nach der Präsentation stattfinden?",
                            options = listOf("Diskussion und Beantwortung von Fragen", "nur Pause", "nur Notizen machen", "Ende der Veranstaltung"),
                            correctAnswer = "Diskussion und Beantwortung von Fragen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Wie lang sollte die Präsentation sein?",
                            options = listOf("ca. 5 Minuten", "1 Minute", "10 Minuten", "30 Minuten"),
                            correctAnswer = "ca. 5 Minuten",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Sprachenvielfalt", "linguistic diversity", "Sprachenvielfalt ist bedroht."),
                        VocabularyItem("Mehrsprachigkeit", "multilingualism", "Mehrsprachigkeit ist eine Chance."),
                        VocabularyItem("Identitätsbildung", "identity formation", "Sprache spielt bei der Identitätsbildung eine Rolle."),
                        VocabularyItem("Minderheitensprachen", "minority languages", "Minderheitensprachen müssen geschützt werden."),
                        VocabularyItem("empirische Studien", "empirical studies", "Empirische Studien belegen diesen Zusammenhang.")
                    )
                ))
            }
            
            "C2" -> {
                lessons.add(createLesenLesson(
                    title = "C2 Goethe Prüfung - Leseverstehen Teil 1",
                    description = "Goethe-Zertifikat C2 Reading Comprehension - Hermeneutic analysis",
                    level = level,
                    orderIndex = 1,
                    text = """Lesen Sie den folgenden literaturphilosophischen Text und lösen Sie die Aufgaben.

Hermeneutik und literarische Interpretation: Die Unabschließbarkeit des Verstehens

Die hermeneutische Wende in der Literaturwissenschaft markiert einen Paradigmenwechsel von der werkimmanenten Interpretation zur rezeptionsästhetischen Perspektive. Hans-Georg Gadamers "Wahrheit und Methode" (1960) revolutionierte das Verständnis literarischer Hermeneutik, indem er das Vorurteil nicht als Erkenntnishindernis, sondern als konstitutive Bedingung des Verstehens begreift.

Die Konsequenz dieser Einsicht ist fundamental: Literarische Texte besitzen keine objektive Bedeutung, die es zu entdecken gilt, sondern konstituieren sich erst im dialogischen Prozess zwischen Text und Leser. Wolfgang Isers Konzept der "Leerstelle" beschreibt jene textuellen Lücken, die der Leser durch seine Imagination füllen muss, wodurch jeder Leseakt zu einer kreativen Neuschöpfung des Werkes wird.

Diese perspektivistische Wende hat weitreichende Implikationen für die Literaturtheorie. Der Tod des Autors, wie Roland Barthes ihn proklamiert hat, bedeutet nicht das Ende der Interpretation, sondern ihre Demokratisierung. Jeder Leser wird zum potentiellen Interpreten, wobei die Pluralität der Deutungen nicht als Defizit, sondern als Reichtum der Literatur erscheint.

Kritiker dieser Position argumentieren, dass die Auflösung des Autoritätsanspruchs in interpretatorischen Relativismus münde und die Möglichkeit rationaler Kritik untergrabe. Doch die hermeneutische Tradition betont die Notwendigkeit methodischer Disziplin auch im Zeitalter der Deutungsoffenheit.

Die Zukunft der Literaturwissenschaft liegt in der produktiven Spannung zwischen der Unabschließbarkeit des Verstehens und der Notwendigkeit methodischer Reflexion.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was revolutionierte Hans-Georg Gadamers Werk?",
                            options = listOf("das Verständnis literarischer Hermeneutik", "nur die Grammatik", "nur die Rechtschreibung", "nur die Typografie"),
                            correctAnswer = "das Verständnis literarischer Hermeneutik",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Was beschreibt Wolfgang Isers Konzept der 'Leerstelle'?",
                            options = listOf("textuelle Lücken, die der Leser füllen muss", "vollständige Texte ohne Lücken", "nur die Seitenzahlen", "nur die Schriftgröße"),
                            correctAnswer = "textuelle Lücken, die der Leser füllen muss",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Was bedeutet der 'Tod des Autors' nach Roland Barthes?",
                            options = listOf("Demokratisierung der Interpretation", "Ende aller Literatur", "nur Zensur", "nur Druckverbot"),
                            correctAnswer = "Demokratisierung der Interpretation",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was kritisieren die Gegner der hermeneutischen Wende?",
                            options = listOf("interpretatorischen Relativismus", "zu viel Methodik", "zu wenig Leser", "zu viele Autoren"),
                            correctAnswer = "interpretatorischen Relativismus",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Wo liegt die Zukunft der Literaturwissenschaft?",
                            options = listOf("in der Spannung zwischen Unabschließbarkeit und methodischer Reflexion", "nur in der Vergangenheit", "nur in der Technik", "nur in der Mathematik"),
                            correctAnswer = "in der Spannung zwischen Unabschließbarkeit und methodischer Reflexion",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("hermeneutische Wende", "hermeneutic turn", "Die hermeneutische Wende revolutionierte die Literaturwissenschaft."),
                        VocabularyItem("rezeptionsästhetisch", "reception-aesthetic", "Die rezeptionsästhetische Perspektive ist wichtig."),
                        VocabularyItem("werkimmanent", "immanent to the work", "Werkimmanente Interpretation ist traditionell."),
                        VocabularyItem("interpretatorischer Relativismus", "interpretive relativism", "Relativismus untergräbt rationale Kritik."),
                        VocabularyItem("Deutungsoffenheit", "openness to interpretation", "Deutungsoffenheit ist ein Merkmal moderner Literatur.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "C2 Goethe Prüfung - Leseverstehen Teil 2",
                    description = "Goethe-Zertifikat C2 Reading Comprehension - Epistemological discourse",
                    level = level,
                    orderIndex = 2,
                    text = """Lesen Sie den folgenden erkenntnistheoretischen Text und lösen Sie die Aufgaben.

Die Krise der Repräsentation: Konstruktivismus und die Grenzen objektiver Erkenntnis

Die konstruktivistische Wende in der Erkenntnistheorie markiert eine fundamentale Infragestellung des Korrespondenzmodells der Wahrheit. Während der klassische Realismus davon ausgeht, dass wahre Aussagen die Wirklichkeit abbilden wie sie ist, betont der Konstruktivismus die aktive Rolle des Erkenntnissubjekts bei der Konstitution von Wissen.

Paul Feyerabends "Anything goes" (1975) radikalisiert diese Position, indem er die Idee universaler wissenschaftlicher Methoden als ideologisches Konstrukt entlarvt. Die Wissenschaftsgeschichte zeige vielmehr, dass theoretische Fortschritte häufig durch die Verletzung methodischer Regeln erreicht würden, nicht durch deren Befolgung.

Diese Einsicht hat weitreichende Konsequenzen für das Verständnis wissenschaftlicher Objektivität. Thomas Kuhns Paradigmenkonzept beschreibt wissenschaftliche Entwicklung als Abfolge inkommensurabler Theorien, die nicht durch rationale Argumente allein zu vermitteln sind. Der Wechsel zwischen Paradigmen erfolgt nicht durch logische Deduktion, sondern durch wissenschaftliche Revolutionen, die soziale und psychologische Faktoren einschließen.

Die feministische Epistemologie erweitert diese Kritik um die Dimension der Geschlechterverhältnisse. Evelyn Fox Kellers Analyse der Wissenschaftskultur zeigt, wie androzentrische Metaphern und Werte die Konstruktion wissenschaftlicher Objekte beeinflussen. Die vermeintlich objektive Beobachtung erweist sich als perspektivische Konstruktion.

Kritiker des Konstruktivismus warnen vor einem Subjektivismus, der die Möglichkeit rationaler Kritik untergrabe und in Beliebigkeit münde. Doch die konstruktivistische Tradition betont die Notwendigkeit reflexiver Methodologie, die die Bedingtheit allen Wissens anerkennt, ohne die Möglichkeit rationaler Diskussion aufzugeben.

Die Zukunft der Erkenntnistheorie liegt in der Vermittlung zwischen der Einsicht in die Perspektivität allen Wissens und der Suche nach transsubjektiven Kriterien rationaler Validierung.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was hinterfragt die konstruktivistische Wende?",
                            options = listOf("das Korrespondenzmodell der Wahrheit", "nur die Mathematik", "nur die Kunst", "nur die Musik"),
                            correctAnswer = "das Korrespondenzmodell der Wahrheit",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Was entlarvt Paul Feyerabend?",
                            options = listOf("universale wissenschaftliche Methoden als ideologisches Konstrukt", "nur die Physik", "nur die Biologie", "nur die Chemie"),
                            correctAnswer = "universale wissenschaftliche Methoden als ideologisches Konstrukt",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Was beschreibt Thomas Kuhns Paradigmenkonzept?",
                            options = listOf("wissenschaftliche Entwicklung als Abfolge inkommensurabler Theorien", "nur lineare Entwicklung", "nur zyklische Entwicklung", "nur zufällige Entwicklung"),
                            correctAnswer = "wissenschaftliche Entwicklung als Abfolge inkommensurabler Theorien",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was zeigt Evelyn Fox Kellers Analyse?",
                            options = listOf("wie androzentrische Metaphern die Wissenschaft beeinflussen", "nur biologische Unterschiede", "nur psychologische Unterschiede", "nur kulturelle Unterschiede"),
                            correctAnswer = "wie androzentrische Metaphern die Wissenschaft beeinflussen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Was warnen Kritiker des Konstruktivismus?",
                            options = listOf("vor einem Subjektivismus, der rationale Kritik untergräbt", "vor zu viel Objektivität", "vor zu wenig Subjektivität", "vor zu wenig Methodik"),
                            correctAnswer = "vor einem Subjektivismus, der rationale Kritik untergräbt",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Erkenntnistheorie", "epistemology", "Erkenntnistheorie untersucht die Grundlagen des Wissens."),
                        VocabularyItem("Konstruktivismus", "constructivism", "Konstruktivismus betont die aktive Rolle des Subjekts."),
                        VocabularyItem("Korrespondenzmodell", "correspondence model", "Das Korrespondenzmodell der Wahrheit ist klassisch."),
                        VocabularyItem("inkommensurabel", "incommensurable", "Theorien können inkommensurabel sein."),
                        VocabularyItem("androzentrisch", "androcentric", "Androzentrische Metaphern prägen die Wissenschaft.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "C2 Goethe Prüfung - Hörverstehen Teil 1",
                    description = "Goethe-Zertifikat C2 Listening Comprehension - Philosophical debate",
                    level = level,
                    orderIndex = 3,
                    text = """Hören Sie sich die folgende philosophische Diskussion an und lösen Sie die Aufgaben.

Philosophisches Streitgespräch: Die Zukunft der menschlichen Rationalität

Moderator: Sehr geehrte Damen und Herren, heute diskutieren wir über die Zukunft der menschlichen Rationalität im Zeitalter der Künstlichen Intelligenz. Professor Müller, Sie sind Skeptiker. Was befürchten Sie?

Prof. Müller: Die Gefahr besteht darin, dass wir unsere kognitiven Fähigkeiten an Maschinen delegieren und dadurch unsere eigenen rationalen Kapazitäten verkümmern lassen. Wenn Algorithmen komplexe Entscheidungen treffen, verlieren wir die Übung im kritischen Denken. Die Konsequenz wäre eine Entmündigung des Menschen durch die Technik.

Moderator: Professor Schmidt, Sie sehen das optimistischer?

Prof. Schmidt: Ganz im Gegenteil. Die KI erweitert unsere Rationalität, anstatt sie zu ersetzen. Wir können nun Probleme lösen, die zuvor jenseits unserer kognitiven Grenzen lagen. Die Mensch-Maschine-Symbiose schafft eine neue Form der Intelligenz, die beide Komponenten transzendiert.

Prof. Müller: Aber wer kontrolliert die KI? Wenn Maschinen lernen, könnten sie Ziele entwickeln, die unseren widersprechen. Die Autonomie der KI könnte zur Heteronomie des Menschen führen.

Prof. Schmidt: Das ist das cartesianische Missverständnis der KI. Maschinen haben kein Bewusstsein, keine Intentionalität. Sie sind Werkzeuge, die unsere Rationalität verstärken. Die Frage ist nicht Kontrolle, sondern optimale Integration.

Moderator: Frau Dr. Weber, was sagen Sie als Ethikerin zu diesem Konflikt?

Dr. Weber: Beide Positionen haben Berechtigung. Die Herausforderung liegt in der Verantwortung. Wir müssen KI so gestalten, dass sie menschliche Werte unterstützt, nicht untergräbt. Die Zukunft der Rationalität hängt von unserer Fähigkeit ab, Technologie ethisch zu gestalten.

Prof. Müller: Aber die Technik entwickelt sich schneller als unsere ethischen Reflexionen!

Dr. Weber: Deshalb brauchen wir dringend neue Formen der deliberativen Demokratie, die technische Entwicklungen mit gesellschaftlicher Diskussion verbindet.

Moderator: Vielen Dank für diese anregende Diskussion.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was befürchtet Professor Müller?",
                            options = listOf("Verkümmerung der menschlichen Rationalität durch Delegation an Maschinen", "zu wenig Technologie", "zu viel menschliche Kontrolle", "zu wenig Algorithmen"),
                            correctAnswer = "Verkümmerung der menschlichen Rationalität durch Delegation an Maschinen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Was sieht Professor Schmidt als positiv?",
                            options = listOf("Erweiterung der menschlichen Rationalität durch KI", "Ersatz menschlicher Intelligenz", "Verkümmerung menschlicher Fähigkeiten", "Autonomie der Maschinen"),
                            correctAnswer = "Erweiterung der menschlichen Rationalität durch KI",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Was kritisiert Professor Müller an der KI?",
                            options = listOf("mögliche Entwicklung eigener Ziele durch Maschinen", "zu wenig Lernfähigkeit", "zu viel menschliche Kontrolle", "zu geringe Geschwindigkeit"),
                            correctAnswer = "mögliche Entwicklung eigener Ziele durch Maschinen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was betont Frau Dr. Weber?",
                            options = listOf("Notwendigkeit ethischer Gestaltung der Technologie", "Vollständige Ablehnung der KI", "Absolute Kontrolle der Maschinen", "Nur technische Entwicklung"),
                            correctAnswer = "Notwendigkeit ethischer Gestaltung der Technologie",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Was fordert Frau Dr. Weber?",
                            options = listOf("neue Formen deliberativer Demokratie", "Verbot aller KI-Entwicklung", "nur technische Standards", "ausschließlich philosophische Diskussionen"),
                            correctAnswer = "neue Formen deliberativer Demokratie",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Entmündigung", "disempowerment", "Entmündigung des Menschen durch Technik."),
                        VocabularyItem("Heteronomie", "heteronomy", "Heteronomie bedeutet Fremdbestimmung."),
                        VocabularyItem("cartesianisch", "Cartesian", "Das cartesianische Missverständnis der KI."),
                        VocabularyItem("deliberativ", "deliberative", "Deliberative Demokratie ist notwendig."),
                        VocabularyItem("Intentionalität", "intentionality", "Maschinen haben keine Intentionalität.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "C2 Goethe Prüfung - Schreiben Teil 1",
                    description = "Goethe-Zertifikat C2 Writing - Critical essay",
                    level = level,
                    orderIndex = 4,
                    text = """Schreiben Sie einen kritischen Essay zum folgenden Thema:

"Kritische Auseinandersetzung: Die Krise der Aufklärung im digitalen Zeitalter"

Schreiben Sie einen kritischen Essay (ca. 400 Wörter) zur Krise der Aufklärung im digitalen Zeitalter. Analysieren Sie die folgenden Aspekte:

1. Die Grundprinzipien der Aufklärung (Vernunft, Autonomie, Fortschritt)
2. Die Herausforderungen der Digitalisierung für diese Prinzipien
3. Die Rolle der sozialen Medien bei der Meinungsbildung
4. Möglichkeiten einer "digitalen Aufklärung"

Strukturieren Sie Ihren Essay klar:
- Einleitung mit These
- Hauptteil mit Argumentation und Beispielen
- Schluss mit Bewertung und Ausblick

Verwenden Sie komplexe argumentative Strukturen:
- Obwohl... muss dennoch festgehalten werden, dass...
- Kritiker argumentieren zu Recht, dass... Allerdings übersieht diese Position...
- Empirische Befunde zeigen, dass... Dennoch bleibt zu fragen...
- Die Ambivalenz besteht darin, dass...

Beispielargumentation:
"Die Aufklärung, als historisches Projekt der Vernunftautonomie, steht heute vor ihrer größten Herausforderung: der Digitalisierung aller Lebensbereiche. Während die sozialen Medien einerseits als Plattformen der deliberativen Öffentlichkeit gefeiert werden, zeigen algorithmische Filterblasen andererseits Tendenzen zur Fragmentierung der rationalen Diskussion.

Kritiker der digitalen Öffentlichkeit argumentieren zu Recht, dass emotionale Appelle und vereinfachende Narrative die rationale Auseinandersetzung verdrängen. Empirische Befunde zeigen jedoch, dass digitale Vernetzung auch neue Formen der Solidarität und des Wissenszugangs ermöglicht hat.

Die Ambivalenz besteht darin, dass die Digitalisierung sowohl die Mittel der Aufklärung demokratisiert als auch deren Grundlagen untergräbt. Die Frage ist, ob wir eine 'digitale Aufklärung' entwickeln können, die die Potentiale der Vernetzung nutzt, ohne deren Risiken zu ignorieren."...

Verwenden Sie akademisches Vokabular:
- Vernunftautonomie
- deliberative Öffentlichkeit
- algorithmische Filterblasen
- Fragmentierung
- Ambivalenz
- Vernetzung""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was ist das Thema des Essays?",
                            options = listOf("Krise der Aufklärung im digitalen Zeitalter", "Vorteile der Digitalisierung", "Geschichte der Aufklärung", "Technische Entwicklung"),
                            correctAnswer = "Krise der Aufklärung im digitalen Zeitalter",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Welche Aspekte soll der Essay analysieren?",
                            options = listOf("Grundprinzipien, Herausforderungen, Rolle sozialer Medien, Möglichkeiten digitaler Aufklärung", "nur technische Aspekte", "nur historische Aspekte", "nur philosophische Aspekte"),
                            correctAnswer = "Grundprinzipien, Herausforderungen, Rolle sozialer Medien, Möglichkeiten digitaler Aufklärung",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Welche Struktur soll der Essay haben?",
                            options = listOf("Einleitung mit These, Hauptteil mit Argumentation, Schluss mit Bewertung", "nur Einleitung", "nur Argumente", "keine Struktur"),
                            correctAnswer = "Einleitung mit These, Hauptteil mit Argumentation, Schluss mit Bewertung",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Welche komplexen Strukturen sollen verwendet werden?",
                            options = listOf("Obwohl... muss dennoch..., Kritiker argumentieren..., Empirische Befunde...", "nur einfache Sätze", "nur Aufzählungen", "nur Fragen"),
                            correctAnswer = "Obwohl... muss dennoch..., Kritiker argumentieren..., Empirische Befunde...",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Wie lang sollte der Essay sein?",
                            options = listOf("ca. 400 Wörter", "100 Wörter", "200 Wörter", "keine Begrenzung"),
                            correctAnswer = "ca. 400 Wörter",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Vernunftautonomie", "autonomy of reason", "Vernunftautonomie ist ein Grundprinzip der Aufklärung."),
                        VocabularyItem("deliberative Öffentlichkeit", "deliberative public sphere", "Deliberative Öffentlichkeit ermöglicht rationale Diskussion."),
                        VocabularyItem("algorithmische Filterblasen", "algorithmic filter bubbles", "Filterblasen fragmentieren die Öffentlichkeit."),
                        VocabularyItem("Ambivalenz", "ambivalence", "Die Ambivalenz der Digitalisierung ist offensichtlich."),
                        VocabularyItem("Vernetzung", "networking/connectivity", "Vernetzung ermöglicht neue Kommunikationsformen.")
                    )
                ))

                lessons.add(createLesenLesson(
                    title = "C2 Goethe Prüfung - Sprechen Teil 1",
                    description = "Goethe-Zertifikat C2 Speaking - Academic presentation with discussion",
                    level = level,
                    orderIndex = 5,
                    text = """Bereiten Sie eine Präsentation vor und nehmen Sie an einer Diskussion teil.

Thema: "Die Zukunft der Menschheit: Transhumanismus und die Grenzen der Evolution"

Bereiten Sie eine 7-minütige Präsentation vor und führen Sie anschließend eine 10-minütige Diskussion. Gehen Sie auf folgende Aspekte ein:

1. Die Grundannahmen des Transhumanismus
2. Technologische Möglichkeiten (Genetik, KI, Nanotechnologie)
3. Ethische und philosophische Implikationen
4. Gesellschaftliche und politische Konsequenzen
5. Alternativen zum Transhumanismus

Struktur Ihrer Präsentation:
- Einleitung: Definition und historische Entwicklung
- Hauptteil: Analyse der verschiedenen Dimensionen
- Schluss: Eigene Position und kritische Bewertung

Mögliche Diskussionsfragen:
- Soll der Mensch die Evolution kontrollieren?
- Wo liegen die Grenzen der technischen Machbarkeit?
- Welche Risiken überwiegen die Vorteile?
- Wie können wir ethische Rahmenbedingungen schaffen?
- Welche Alternativen gibt es zur technischen Verbesserung?

Verwenden Sie höchst komplexe Argumentationsstrukturen:
- Die Dichotomie zwischen Optimismus und Skepsis manifestiert sich darin, dass...
- Obgleich die technischen Möglichkeiten faszinierend sind, muss dennoch kritisch hinterfragt werden...
- Die Paradoxie besteht darin, dass der Transhumanismus einerseits die menschliche Autonomie stärken will, andererseits aber...
- Empirische Evidenz zeigt zwar..., doch die philosophische Reflexion legt nahe, dass...
- Die Kontingenz menschlicher Existenz wird durch transhumanistische Visionen nicht aufgehoben, sondern vielmehr...

Präsentationsbeispiel:
"Sehr geehrte Damen und Herren,

der Transhumanismus als philosophische und technologische Bewegung stellt eine radikale Infragestellung traditioneller Vorstellungen vom Menschen dar. Die Dichotomie zwischen Optimismus und Skepsis manifestiert sich darin, dass Befürworter in der technischen Verbesserung des Menschen den nächsten Schritt der Evolution sehen, während Kritiker vor einer Entfremdung von der menschlichen Natur warnen.

Die transhumanistische Vision umfasst die Überwindung biologischer Limitationen durch Genmanipulation, die Erweiterung kognitiver Fähigkeiten durch Hirn-Computer-Schnittstellen und die potentielle Unsterblichkeit durch Nanotechnologie. Diese Entwicklungen werfen fundamentale Fragen nach Identität, Gleichheit und dem Wesen des Menschseins auf.

Obgleich die technischen Möglichkeiten faszinierend sind, muss dennoch kritisch hinterfragt werden, ob die Kontingenz menschlicher Existenz durch transhumanistische Visionen nicht aufgehoben, sondern vielmehr in eine neue Form der Determination überführt wird.

Die philosophische Reflexion legt nahe, dass der Transhumanismus nicht nur eine technologische, sondern auch eine anthropologische Revolution darstellt, deren Konsequenzen wir heute noch nicht abschätzen können."...

Diskussionsphase: Argumente austauschen, Fragen stellen und beantworten, Positionen verteidigen.""",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was ist das Thema der Präsentation?",
                            options = listOf("Zukunft der Menschheit: Transhumanismus und Grenzen der Evolution", "Geschichte der Technologie", "Philosophie der Biologie", "Ethische Grundlagen"),
                            correctAnswer = "Zukunft der Menschheit: Transhumanismus und Grenzen der Evolution",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 2,
                            question = "Welche Aspekte soll die Präsentation behandeln?",
                            options = listOf("Grundannahmen, Möglichkeiten, Implikationen, Konsequenzen, Alternativen", "nur technische Aspekte", "nur historische Aspekte", "nur philosophische Aspekte"),
                            correctAnswer = "Grundannahmen, Möglichkeiten, Implikationen, Konsequenzen, Alternativen",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 3,
                            question = "Wie lange sollte die Präsentation sein?",
                            options = listOf("7 Minuten", "3 Minuten", "10 Minuten", "15 Minuten"),
                            correctAnswer = "7 Minuten",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 4,
                            question = "Was soll nach der Präsentation stattfinden?",
                            options = listOf("10-minütige Diskussion", "nur Fragen stellen", "nur Notizen machen", "Ende der Veranstaltung"),
                            correctAnswer = "10-minütige Diskussion",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        ),
                        Question(
                            id = 5,
                            question = "Welche komplexen Strukturen sollen verwendet werden?",
                            options = listOf("Dichotomie zwischen..., Obgleich..., Paradoxie besteht darin..., Empirische Evidenz zeigt..., Kontingenz...", "nur einfache Sätze", "nur Fragen", "nur Aufzählungen"),
                            correctAnswer = "Dichotomie zwischen..., Obgleich..., Paradoxie besteht darin..., Empirische Evidenz zeigt..., Kontingenz...",
                            correctAnswers = null,
                            type = QuestionType.MULTIPLE_CHOICE
                        )
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Transhumanismus", "transhumanism", "Transhumanismus will den Menschen verbessern."),
                        VocabularyItem("Dichotomie", "dichotomy", "Die Dichotomie zwischen Optimismus und Skepsis."),
                        VocabularyItem("Kontingenz", "contingency", "Kontingenz menschlicher Existenz."),
                        VocabularyItem("Genmanipulation", "genetic manipulation", "Genmanipulation ist technisch möglich."),
                        VocabularyItem("anthropologische Revolution", "anthropological revolution", "Eine anthropologische Revolution steht bevor.")
                    )
                ))
            }
        }
        
        return lessons
    }

    private fun generateGrammarLessons(level: String): List<Lesson> {
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
                    orderIndex = order++
                )
            )
        }

        // Core topics per level
        val topics = when (level) {
            "A1" -> listOf(
                "articles" to ("Artikel: Bestimmt/Unbestimmt" to "der/die/das; ein/eine/ein"),
                "nouns_gender" to ("Nomen: Genus" to "Gender rules and patterns"),
                "plural" to ("Pluralbildung" to "Common plural endings"),
                "sentence_order" to ("Wortstellung" to "Verb second position"),
                "pronouns" to ("Pronomen" to "Personal pronouns nominative")
            )
            "A2" -> listOf(
                "cases" to ("Kasus: Akkusativ/Dativ" to "Articles in Accusative/Dative"),
                "prepositions" to ("Präpositionen" to "Two-way prepositions"),
                "adjectives_basic" to ("Adjektivdeklination (Basis)" to "After indefinite/definite"),
                "modal_verbs" to ("Modalverben" to "können, müssen, dürfen"),
                "past_perfekt" to ("Perfekt" to "haben/sein + Partizip II")
            )
            "B1" -> listOf(
                "relative_clauses" to ("Relativsätze" to "der/die/das; den/dem/deren"),
                "adjectives" to ("Adjektivdeklination (stark/schwach)" to "Tables and usage"),
                "konjunktiv2" to ("Konjunktiv II" to "Irrealis in Gegenwart"),
                "future" to ("Futur I" to "werden + Infinitiv"),
                "word_order_sub" to ("Nebensätze" to "Verbend")
            )
            "B2" -> listOf(
                "passive" to ("Passiv" to "Vorgang/Zustand"),
                "genitiv" to ("Genitiv" to "Attribute und Präpositionen"),
                "indirect_speech" to ("Indirekte Rede" to "Konjunktiv I"),
                "partizipien" to ("Partizip I/II als Adjektiv" to "Attributive usage"),
                "connectors" to ("Konnektoren" to "obwohl, trotzdem, daher")
            )
            "C1" -> listOf(
                "subjunctive_full" to ("Konjunktiv I/II" to "Indirekte Rede und Irrealis"),
                "complex_clauses" to ("Komplexe Satzgefüge" to "Eingebettete Nebensätze"),
                "nominalization" to ("Nominalisierung" to "Stil und Verdichtung"),
                "word_formation" to ("Wortbildung" to "Prä-/Suffixe, Komposita"),
                "register" to ("Register und Stil" to "Formell/Informell")
            )
            else -> listOf(
                "rhetoric" to ("Rhetorische Mittel" to "Metapher, Ironie, Hyperbel"),
                "cohesion" to ("Kohäsion/Kohärenz" to "Thema-Rhema, Verknüpfung"),
                "advanced_nominal" to ("Nominalstil" to "Verdichtung und Präzision"),
                "ellipsis" to ("Ellipse" to "Auslassungen gezielt nutzen"),
                "style" to ("Stilmittel" to "Variatio, Parallelismus")
            )
        }

        // Create at least 10 core lessons (one per topic, plus variants)
        topics.forEach { (key, pair) ->
            val (title, desc) = pair
            val englishHint = if (level in listOf("A1","A2")) when (key) {
                "articles" -> "Definite (der/die/das) and indefinite (ein/eine) articles."
                "nouns_gender" -> "Gender rules (feminine -ung, neuter -chen/-lein)."
                "plural" -> "Plural endings patterns in German."
                "sentence_order" -> "Verb in second position in main clauses."
                "pronouns" -> "Personal pronouns in nominative."
                "cases" -> "Accusative/Dative: prepositions and articles."
                "prepositions" -> "Two-way prepositions (Wechselpräpositionen)."
                "adjectives_basic" -> "Basic adjective endings after articles."
                "modal_verbs" -> "Modal verbs: können, müssen, dürfen."
                "past_perfekt" -> "Present perfect: haben/sein + Participle II."
                else -> "Core grammar concept explained in English."
            } else null

            val quizList: List<GrammarQuestion> = when (level) {
                "A1" -> when (key) {
                    "articles" -> listOf(
                        GrammarQuestion("__ Mann ist groß.", listOf("Der","Die","Das"), "Der", 5, questionEn = "Choose the correct definite article for 'Mann'."),
                        GrammarQuestion("Ich habe __ Apfel.", listOf("ein","eine","der"), "ein", 5, questionEn = "Indefinite article for masculine 'Apfel'."),
                        GrammarQuestion("__ Haus ist neu.", listOf("Der","Die","Das"), "Das", 5, questionEn = "Neuter 'Haus' takes 'das'."),
                        GrammarQuestion("Sie hat __ Katze.", listOf("ein","eine","das"), "eine", 5, questionEn = "Feminine 'Katze' takes 'eine'.")
                    )
                    "nouns_gender" -> listOf(
                        GrammarQuestion("__ Zeitung", listOf("der","die","das"), "die", 5, questionEn = "-ung nouns are usually feminine."),
                        GrammarQuestion("__ Mädchen", listOf("der","die","das"), "das", 5, questionEn = "-chen diminutives are neuter."),
                        GrammarQuestion("__ Junge", listOf("der","die","das"), "der", 5, questionEn = "Male person is usually masculine."),
                        GrammarQuestion("__ Blume", listOf("der","die","das"), "die", 5)
                    )
                    "plural" -> listOf(
                        GrammarQuestion("Plural von 'Kind' ist __", listOf("Kinder","Kinds","Kinden"), "Kinder", 5, questionEn = "Common plural pattern."),
                        GrammarQuestion("Plural von 'Auto' ist __", listOf("Autos","Autoe","Auten"), "Autos", 5),
                        GrammarQuestion("Plural von 'Frau' ist __", listOf("Fraue","Frauen","Fraus"), "Frauen", 5)
                    )
                    "sentence_order" -> listOf(
                        GrammarQuestion("(ich/ins Kino/gehe)", listOf("Ich gehe ins Kino","Ins Kino gehe ich","Gehe ich ins Kino"), "Ich gehe ins Kino", 5, questionEn = "Main clause: verb in 2nd position."),
                        GrammarQuestion("(heute/lerne/ich)", listOf("Heute ich lerne","Ich lerne heute","Lerne ich heute"), "Ich lerne heute", 5)
                    )
                    "pronouns" -> listOf(
                        GrammarQuestion("__ bin Anna.", listOf("Ich","Du","Er"), "Ich", 5),
                        GrammarQuestion("__ bist Max.", listOf("Ich","Du","Sie"), "Du", 5),
                        GrammarQuestion("__ ist müde.", listOf("Er","Ich","Du"), "Er", 5)
                    )
                    else -> listOf(GrammarQuestion("Wähle den korrekten Eintrag.", listOf("A","B","C"), "A", 5))
                }
                "A2" -> when (key) {
                    "cases" -> listOf(
                        GrammarQuestion("Ich gehe __ Park.", listOf("in den","im","ins"), "in den", 10, questionEn = "Akkusativ after movement"),
                        GrammarQuestion("Ich bin __ Park.", listOf("im","in den","ins"), "im", 10, questionEn = "Dativ for location"),
                        GrammarQuestion("Ich fahre mit __ Bus.", listOf("dem","den","der"), "dem", 10)
                    )
                    "prepositions" -> listOf(
                        GrammarQuestion("Das Bild hängt __ Wand.", listOf("an der","an die","auf die"), "an der", 10, questionEn = "Dative with location"),
                        GrammarQuestion("Ich hänge das Bild __ Wand.", listOf("an die","an der","auf der"), "an die", 10, questionEn = "Accusative with movement")
                    )
                    "adjectives_basic" -> listOf(
                        GrammarQuestion("Das ist ein __ Haus.", listOf("großes","große","großer"), "großes", 10),
                        GrammarQuestion("Ich sehe den __ Mann.", listOf("großen","großem","großer"), "großen", 10)
                    )
                    "modal_verbs" -> listOf(
                        GrammarQuestion("Ich __ Deutsch sprechen.", listOf("kann","muss","darf"), "kann", 10),
                        GrammarQuestion("Du __ heute lernen.", listOf("musst","kannst","darfst"), "musst", 10)
                    )
                    "past_perfekt" -> listOf(
                        GrammarQuestion("Ich __ nach Berlin gefahren.", listOf("bin","habe","war"), "bin", 10, questionEn = "'sein' with movement"),
                        GrammarQuestion("Ich __ Pizza gegessen.", listOf("habe","bin","war"), "habe", 10)
                    )
                    else -> listOf(GrammarQuestion("Wähle die richtige Antwort.", listOf("A","B","C"), "A", 10))
                }
                else -> listOf(
                    GrammarQuestion("Wähle richtig", listOf("A","B","C"), "A", 5),
                    GrammarQuestion("Vervollständige den Satz", listOf("A","B","C"), "B", 5)
                )
            }

            create(title, desc, GrammarContent(
                topicKey = "${level.lowercase()}_${key}",
                explanations = listOf(desc),
                explanationsEn = if (englishHint != null) listOf(englishHint) else emptyList(),
                examples = listOf("Beispiel 1", "Beispiel 2"),
                miniGames = listOf(
                    GrammarMiniGame.FillBlank("___ Haus ist groß.", "Das"),
                    GrammarMiniGame.Match(listOf("obwohl" to "Konjunktion", "trotzdem" to "Adverb"))
                ),
                quiz = quizList
            ))
        }

        // Procedurally add practice sets to reach 20+ lessons per level
        val remaining = 20 - lessons.size
        if (remaining > 0) {
            for (i in 1..remaining) {
                val practiceQuiz = generatePracticeQuiz(level, i)
                create(
                    title = "${when(level){"A1"->"Artikel";"A2"->"Kasus";"B1"->"Relativsätze";"B2"->"Passiv";"C1"->"Konjunktiv";else->"Nominalstil"}} Praxis ${i}",
                    description = "Übungen und Beispiele ${i}",
                    content = GrammarContent(
                        topicKey = "${level.lowercase()}_practice_${i}",
                        explanations = listOf("Übungsreihe $i für $level"),
                        examples = listOf("Beispielsatz $i a", "Beispielsatz $i b"),
                        miniGames = listOf(
                            GrammarMiniGame.SentenceBuilder(
                                words = listOf("ich","gehe","in","den","Park"),
                                correctOrder = listOf("ich","gehe","in","den","Park")
                            )
                        ),
                        quiz = practiceQuiz
                    )
                )
            }
        }

        return lessons
    }

    /**
     * Generate a varied practice quiz so that each practice lesson has its own set of questions rather than the
     * generic fallback. We create simple sentence–gap or article questions that differ for every iteration.
     */
    private fun generatePracticeQuiz(level: String, index: Int): List<GrammarQuestion> {
        val baseNouns = listOf("Hund" to "der", "Katze" to "die", "Kind" to "das", "Auto" to "das", "Lehrer" to "der")
        val (noun, article) = baseNouns[(index - 1) % baseNouns.size]

        val articleOptions = listOf("Der", "Die", "Das")
        val question1 = GrammarQuestion("__ $noun ist hier.", articleOptions, article.replaceFirstChar { it.uppercase() }, 5)

        // sentence order variation
        val verbs = listOf("gehe", "lerne", "spiele", "esse", "trinke")
        val verb = verbs[index % verbs.size]
        val question2 = GrammarQuestion("(ich/$verb/gerne)", listOf("Ich $verb gerne", "$verb ich gerne", "Gerne ich $verb"), "Ich $verb gerne", 5)

        // plural question variation (A1/A2 only)
        val plurals = listOf("Kind" to "Kinder", "Buch" to "Bücher", "Haus" to "Häuser")
        val (singular, plural) = plurals[index % plurals.size]
        val question3 = GrammarQuestion("Plural von '$singular' ist __", listOf(plural, "${plural}n", "${plural}e"), plural, 5)

        // modal verb question
        val question4 = GrammarQuestion("Ich __ Deutsch sprechen.", listOf("kann","muss","darf"), "kann", 5)

        return listOf(question1, question2, question3, question4)
    }

    data class GrammarContent(
        val topicKey: String,
        val explanations: List<String>,
        val explanationsEn: List<String> = emptyList(),
        val examples: List<String>,
        val miniGames: List<GrammarMiniGame> = emptyList(),
        val quiz: List<GrammarQuestion> = emptyList()
    )

    data class GrammarQuestion(
        val question: String,
        val options: List<String>,
        val correct: String,
        val points: Int,
        val questionEn: String? = null
    )

    sealed class GrammarMiniGame {
        data class DragDrop(val buckets: List<String>, val items: List<Pair<String, String>>) : GrammarMiniGame()
        data class Match(val pairs: List<Pair<String, String>>) : GrammarMiniGame()
        data class FillBlank(val text: String, val answer: String) : GrammarMiniGame()
        data class SentenceBuilder(val words: List<String>, val correctOrder: List<String>) : GrammarMiniGame()
    }
    
    private fun generateHoerenLessons(level: String): List<Lesson> {
        val lessons = mutableListOf<Lesson>()
        
        when (level) {
            "A1" -> {
                lessons.add(createHoerenLesson(
                    title = "Begrüßungen",
                    description = "Basic greetings and introductions",
                    level = level,
                    orderIndex = 1,
                    script = "Max: Hallo! Wie heißt du?\nLisa: Ich heiße Lisa. Und du?\nMax: Ich heiße Max. Wie geht es dir?\nLisa: Mir geht es gut, danke. Und dir?\nMax: Mir geht es auch gut.",
                    questions = listOf(
                        Question(id = 1, question = "Wie heißt die Frau?", options = listOf("Lisa", "Max", "Anna", "Peter"), correctAnswer = "Lisa", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What is the woman's name?", optionsEnglish = listOf("Lisa", "Max", "Anna", "Peter")),
                        Question(id = 2, question = "Wie geht es Lisa?", options = listOf("Schlecht", "Gut", "Okay", "Weiß nicht"), correctAnswer = "Gut", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How is Lisa?", optionsEnglish = listOf("Bad", "Good", "Okay", "Don't know")),
                        Question(id = 3, question = "Wie heißt der Mann?", options = null, correctAnswer = "Max", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What is the man's name?")
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Zahlen und Alter",
                    description = "Numbers and age",
                    level = level,
                    orderIndex = 2,
                    script = "Thomas: Wie alt bist du?\nAnna: Ich bin 25 Jahre alt. Und du?\nThomas: Ich bin 30 Jahre alt. Woher kommst du?\nAnna: Ich komme aus Deutschland. Und du?\nThomas: Ich komme aus Österreich.",
                    questions = listOf(
                        Question(id = 1, question = "Wie alt ist die erste Person?", options = listOf("20", "25", "30", "35"), correctAnswer = "25", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How old is the first person?", optionsEnglish = listOf("20", "25", "30", "35")),
                        Question(id = 2, question = "Wie alt ist die zweite Person?", options = listOf("25", "30", "35", "40"), correctAnswer = "30", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How old is the second person?", optionsEnglish = listOf("25", "30", "35", "40")),
                        Question(id = 3, question = "Woher kommt die erste Person?", options = null, correctAnswer = "Deutschland", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "Where does the first person come from?")
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Im Café",
                    description = "At the café",
                    level = level,
                    orderIndex = 3,
                    script = "Kellner: Guten Tag! Was möchten Sie?\nGast: Ich hätte gerne einen Kaffee, bitte.\nKellner: Mit Milch und Zucker?\nGast: Nein, nur schwarz, bitte.\nKellner: Das macht 3 Euro 50.\nGast: Hier sind 5 Euro.\nKellner: Danke, hier ist Ihr Wechselgeld.",
                    questions = listOf(
                        Question(id = 1, question = "Was bestellt der Gast?", options = listOf("Tee", "Kaffee", "Saft", "Wasser"), correctAnswer = "Kaffee", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the guest order?", optionsEnglish = listOf("Tea", "Coffee", "Juice", "Water")),
                        Question(id = 2, question = "Wie viel kostet der Kaffee?", options = listOf("2 Euro", "3 Euro 50", "4 Euro", "5 Euro"), correctAnswer = "3 Euro 50", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How much does the coffee cost?", optionsEnglish = listOf("2 Euro", "3 Euro 50", "4 Euro", "5 Euro")),
                        Question(id = 3, question = "Möchte der Gast Milch und Zucker?", options = listOf("Ja", "Nein"), correctAnswer = "Nein", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Does the guest want milk and sugar?", optionsEnglish = listOf("Yes", "No"))
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Im Krankenhaus",
                    description = "Hospital and health conversation",
                    level = level,
                    orderIndex = 4,
                    script = "Arzt: Guten Tag! Was fehlt Ihnen?\nPatient: Ich habe starke Kopfschmerzen seit gestern.\nArzt: Haben Sie auch Fieber?\nPatient: Ja, ein bisschen. Ich fühle mich sehr müde.\nArzt: Das klingt nach einer Erkältung. Ich verschreibe Ihnen Medikamente.\nPatient: Danke, Herr Doktor. Wann kann ich wieder arbeiten?\nArzt: Bleiben Sie zu Hause und ruhen Sie sich aus.",
                    questions = listOf(
                        Question(id = 1, question = "Was hat der Patient?", options = listOf("Bauchschmerzen", "Kopfschmerzen", "Rückenschmerzen", "Zahnschmerzen"), correctAnswer = "Kopfschmerzen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the patient have?", optionsEnglish = listOf("Stomach ache", "Headache", "Back pain", "Toothache")),
                        Question(id = 2, question = "Seit wann hat er die Schmerzen?", options = listOf("Heute", "Gestern", "Vor einer Woche", "Vor einem Monat"), correctAnswer = "Gestern", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Since when does he have the pain?", optionsEnglish = listOf("Today", "Yesterday", "A week ago", "A month ago")),
                        Question(id = 3, question = "Was verschreibt der Arzt?", options = null, correctAnswer = "Medikamente", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What does the doctor prescribe?")
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Im Bus",
                    description = "Public transportation conversation",
                    level = level,
                    orderIndex = 5,
                    script = "Fahrer: Guten Tag! Wohin möchten Sie?\nPassagier: Ich möchte zum Bahnhof, bitte.\nFahrer: Das kostet 2 Euro 50. Haben Sie das passende Geld?\nPassagier: Nein, ich habe nur einen 5-Euro-Schein.\nFahrer: Kein Problem, hier ist Ihr Wechselgeld.\nPassagier: Danke schön! Wo muss ich aussteigen?\nFahrer: Bei der nächsten Haltestelle.",
                    questions = listOf(
                        Question(id = 1, question = "Wohin möchte der Passagier?", options = listOf("Zum Bahnhof", "Zur Schule", "Zum Supermarkt", "Zur Bank"), correctAnswer = "Zum Bahnhof", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Where does the passenger want to go?", optionsEnglish = listOf("To the train station", "To school", "To the supermarket", "To the bank")),
                        Question(id = 2, question = "Wie viel kostet die Fahrt?", options = listOf("2 Euro", "2 Euro 50", "3 Euro", "5 Euro"), correctAnswer = "2 Euro 50", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How much does the ride cost?", optionsEnglish = listOf("2 Euro", "2 Euro 50", "3 Euro", "5 Euro")),
                        Question(id = 3, question = "Was hat der Passagier?", options = null, correctAnswer = "5-Euro-Schein", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What does the passenger have?")
                    )
                ))

                // Goethe-Zertifikat A1 Hören - Additional Lessons (6-20)
                lessons.add(createHoerenLesson(
                    title = "Familie und Verwandte",
                    description = "Talking about family and relatives - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 6,
                    script = "Anna: Hallo Maria! Wie geht es deiner Familie?\nMaria: Danke, gut. Mein Bruder hat Geburtstag nächste Woche.\nAnna: Wie alt wird er?\nMaria: Er wird 25 Jahre alt. Meine Eltern machen eine Party.\nAnna: Das klingt schön. Kommt deine Oma auch?\nMaria: Ja, natürlich. Sie bringt ihren berühmten Kuchen mit.\nAnna: Toll! Ich komme gerne. Wann beginnt die Party?\nMaria: Um 15 Uhr am Samstag.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wessen Geburtstag feiert die Familie?",
                            options = listOf("Der Bruder", "Die Schwester", "Der Vater", "Die Mutter"),
                            correctAnswer = "Der Bruder",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Whose birthday is the family celebrating?",
                            optionsEnglish = listOf("The brother", "The sister", "The father", "The mother")
                        ),
                        Question(
                            id = 2,
                            question = "Wie alt wird der Bruder?",
                            options = listOf("20", "25", "30", "35"),
                            correctAnswer = "25",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How old will the brother be?",
                            optionsEnglish = listOf("20", "25", "30", "35")
                        ),
                        Question(
                            id = 3,
                            question = "Was bringt die Oma mit?",
                            options = null,
                            correctAnswer = "Kuchen",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What does grandma bring?"
                        ),
                        Question(
                            id = 4,
                            question = "Um wie viel Uhr beginnt die Party?",
                            options = listOf("Um 14 Uhr", "Um 15 Uhr", "Um 16 Uhr", "Um 17 Uhr"),
                            correctAnswer = "Um 15 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "At what time does the party start?",
                            optionsEnglish = listOf("At 2 PM", "At 3 PM", "At 4 PM", "At 5 PM")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Hobby und Freizeit",
                    description = "Talking about hobbies and free time - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 7,
                    script = "Thomas: Was machst du in deiner Freizeit?\nLisa: Ich lese gerne Bücher und höre Musik.\nThomas: Welche Musik hörst du?\nLisa: Ich mag Pop und Rock. Und du?\nThomas: Ich spiele Fußball jeden Samstag.\nLisa: Spielst du in einem Verein?\nThomas: Ja, seit zwei Jahren. Wir trainieren zweimal pro Woche.\nLisa: Das klingt toll. Vielleicht komme ich mal zum Zuschauen.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was macht Lisa in ihrer Freizeit?",
                            options = listOf("Bücher lesen und Musik hören", "Fußball spielen", "Kochen", "Zeichnen"),
                            correctAnswer = "Bücher lesen und Musik hören",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does Lisa do in her free time?",
                            optionsEnglish = listOf("Reading books and listening to music", "Playing soccer", "Cooking", "Drawing")
                        ),
                        Question(
                            id = 2,
                            question = "Welche Musik mag Lisa?",
                            options = listOf("Klassik", "Pop und Rock", "Jazz", "Hip-Hop"),
                            correctAnswer = "Pop und Rock",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What music does Lisa like?",
                            optionsEnglish = listOf("Classical", "Pop and rock", "Jazz", "Hip-Hop")
                        ),
                        Question(
                            id = 3,
                            question = "Wie oft spielt Thomas Fußball?",
                            options = null,
                            correctAnswer = "jeden Samstag",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How often does Thomas play soccer?"
                        ),
                        Question(
                            id = 4,
                            question = "Seit wann spielt Thomas im Verein?",
                            options = listOf("Seit einem Jahr", "Seit zwei Jahren", "Seit drei Jahren", "Seit vier Jahren"),
                            correctAnswer = "Seit zwei Jahren",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Since when has Thomas been playing in the club?",
                            optionsEnglish = listOf("For one year", "For two years", "For three years", "For four years")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Einkaufen im Supermarkt",
                    description = "Shopping at the supermarket - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 8,
                    script = "Verkäuferin: Guten Tag! Was suchen Sie?\nKunde: Ich brauche Milch und Brot.\nVerkäuferin: Die Milch ist im Kühlregal. Welche Größe?\nKunde: Eine Flasche mit 1 Liter, bitte.\nVerkäuferin: Hier ist sie. Kostet 1,20 Euro.\nKunde: Danke. Wo ist das Brot?\nVerkäuferin: Bei den Backwaren, Regal 3.\nKunde: Welches Brot empfehlen Sie?\nVerkäuferin: Das Vollkornbrot ist sehr gut.\nKunde: Gut, das nehme ich. Wie viel kostet es?\nVerkäuferin: 2,50 Euro.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was braucht der Kunde zuerst?",
                            options = listOf("Milch und Brot", "Käse und Wurst", "Obst und Gemüse", "Schokolade und Kekse"),
                            correctAnswer = "Milch und Brot",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the customer need first?",
                            optionsEnglish = listOf("Milk and bread", "Cheese and sausage", "Fruit and vegetables", "Chocolate and cookies")
                        ),
                        Question(
                            id = 2,
                            question = "Wo steht die Milch?",
                            options = listOf("Im Kühlregal", "Bei den Backwaren", "Im Regal 3", "Bei den Süßigkeiten"),
                            correctAnswer = "Im Kühlregal",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where is the milk?",
                            optionsEnglish = listOf("In the refrigerated section", "At the bakery", "In shelf 3", "At the sweets")
                        ),
                        Question(
                            id = 3,
                            question = "Wie viel kostet die Milch?",
                            options = null,
                            correctAnswer = "1,20 Euro",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How much does the milk cost?"
                        ),
                        Question(
                            id = 4,
                            question = "Welches Brot empfiehlt die Verkäuferin?",
                            options = listOf("Weißbrot", "Vollkornbrot", "Roggenbrot", "Mischbrot"),
                            correctAnswer = "Vollkornbrot",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What bread does the saleswoman recommend?",
                            optionsEnglish = listOf("White bread", "Whole grain bread", "Rye bread", "Mixed bread")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Im Café sitzen",
                    description = "Sitting at a café - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 9,
                    script = "Kellner: Guten Abend! Was möchten Sie trinken?\nGast: Ich hätte gerne einen Kaffee, bitte.\nKellner: Mit Milch und Zucker?\nGast: Nein, schwarz bitte. Und ein Stück Kuchen.\nKellner: Welchen Kuchen möchten Sie?\nGast: Den Schokoladenkuchen, bitte.\nKellner: Sehr gerne. Das macht zusammen 6,50 Euro.\nGast: Hier sind 10 Euro.\nKellner: Danke, 3,50 Euro zurück. Guten Appetit!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was bestellt der Gast zu trinken?",
                            options = listOf("Tee", "Kaffee", "Saft", "Wasser"),
                            correctAnswer = "Kaffee",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the guest order to drink?",
                            optionsEnglish = listOf("Tea", "Coffee", "Juice", "Water")
                        ),
                        Question(
                            id = 2,
                            question = "Wie trinkt der Gast den Kaffee?",
                            options = listOf("Mit Milch und Zucker", "Schwarz", "Mit Sahne", "Mit Zitrone"),
                            correctAnswer = "Schwarz",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How does the guest drink the coffee?",
                            optionsEnglish = listOf("With milk and sugar", "Black", "With cream", "With lemon")
                        ),
                        Question(
                            id = 3,
                            question = "Welchen Kuchen möchte der Gast?",
                            options = null,
                            correctAnswer = "Schokoladenkuchen",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What cake does the guest want?"
                        ),
                        Question(
                            id = 4,
                            question = "Wie viel kostet alles zusammen?",
                            options = listOf("5,50 Euro", "6,50 Euro", "7,50 Euro", "8,50 Euro"),
                            correctAnswer = "6,50 Euro",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How much does everything cost together?",
                            optionsEnglish = listOf("5.50 Euro", "6.50 Euro", "7.50 Euro", "8.50 Euro")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Termin beim Arzt",
                    description = "Doctor's appointment - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 10,
                    script = "Arzt: Guten Tag, was fehlt Ihnen?\nPatient: Ich habe Bauchschmerzen seit gestern.\nArzt: Haben Sie auch Übelkeit?\nPatient: Ja, ein bisschen. Und ich habe Durchfall.\nArzt: Öffnen Sie bitte den Mund. Ihre Mandeln sind rot.\nPatient: Ist es schlimm?\nArzt: Nein, nur eine Magenverstimmung. Trinken Sie viel Tee.\nPatient: Wie lange dauert das?\nArzt: Zwei oder drei Tage. Kommen Sie wieder, wenn es schlimmer wird.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo hat der Patient Schmerzen?",
                            options = listOf("Im Bauch", "Im Kopf", "Im Rücken", "In den Beinen"),
                            correctAnswer = "Im Bauch",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where does the patient have pain?",
                            optionsEnglish = listOf("In the stomach", "In the head", "In the back", "In the legs")
                        ),
                        Question(
                            id = 2,
                            question = "Seit wann hat er Schmerzen?",
                            options = listOf("Seit heute", "Seit gestern", "Seit zwei Tagen", "Seit einer Woche"),
                            correctAnswer = "Seit gestern",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Since when does he have pain?",
                            optionsEnglish = listOf("Since today", "Since yesterday", "For two days", "For a week")
                        ),
                        Question(
                            id = 3,
                            question = "Was hat der Patient auch?",
                            options = null,
                            correctAnswer = "Übelkeit",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What else does the patient have?"
                        ),
                        Question(
                            id = 4,
                            question = "Was hat der Patient?",
                            options = listOf("Erkältung", "Magenverstimmung", "Grippe", "Allergie"),
                            correctAnswer = "Magenverstimmung",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the patient have?",
                            optionsEnglish = listOf("Cold", "Stomach upset", "Flu", "Allergy")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Wetter und Jahreszeiten",
                    description = "Talking about weather and seasons - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 11,
                    script = "Anna: Was für Wetter haben wir heute?\nMaria: Es ist sonnig und warm, etwa 25 Grad.\nAnna: Das ist schön! Was machst du bei diesem Wetter?\nMaria: Ich gehe an den See und mache Picknick.\nAnna: Klingt gut. Was ist dein Lieblingswetter?\nMaria: Ich mag den Sommer am liebsten. Und du?\nAnna: Ich liebe den Frühling, wenn alles blüht.\nMaria: Im Winter fahre ich Ski.\nAnna: Das mache ich auch gerne. Wann fährst du wieder?",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wie ist das Wetter heute?",
                            options = listOf("Regnerisch", "Sonnig und warm", "Bewölkt", "Kalt"),
                            correctAnswer = "Sonnig und warm",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is the weather like today?",
                            optionsEnglish = listOf("Rainy", "Sunny and warm", "Cloudy", "Cold")
                        ),
                        Question(
                            id = 2,
                            question = "Wie viel Grad sind es ungefähr?",
                            options = listOf("15 Grad", "20 Grad", "25 Grad", "30 Grad"),
                            correctAnswer = "25 Grad",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "About how many degrees is it?",
                            optionsEnglish = listOf("15 degrees", "20 degrees", "25 degrees", "30 degrees")
                        ),
                        Question(
                            id = 3,
                            question = "Was macht Maria bei diesem Wetter?",
                            options = null,
                            correctAnswer = "Picknick",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What does Maria do in this weather?"
                        ),
                        Question(
                            id = 4,
                            question = "Was macht Maria im Winter?",
                            options = listOf("Schwimmen", "Ski fahren", "Rad fahren", "Wandern"),
                            correctAnswer = "Ski fahren",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does Maria do in winter?",
                            optionsEnglish = listOf("Swimming", "Skiing", "Cycling", "Hiking")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Im Büro arbeiten",
                    description = "Working in the office - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 12,
                    script = "Chef: Guten Morgen, Frau Müller!\nMüller: Guten Morgen, Herr Schmidt!\nChef: Haben Sie die Berichte fertig?\nMüller: Ja, sie liegen auf Ihrem Schreibtisch.\nChef: Danke. Können Sie heute länger bleiben?\nMüller: Ja, natürlich. Bis wann?\nChef: Bis 18 Uhr. Wir haben ein Meeting.\nMüller: Kein Problem. Soll ich etwas vorbereiten?\nChef: Ja, bitte machen Sie eine Präsentation.\nMüller: Gut, ich mache das sofort.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo liegen die Berichte?",
                            options = listOf("Auf dem Boden", "Auf dem Schreibtisch", "Im Schrank", "In der Tasche"),
                            correctAnswer = "Auf dem Schreibtisch",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where are the reports?",
                            optionsEnglish = listOf("On the floor", "On the desk", "In the cabinet", "In the bag")
                        ),
                        Question(
                            id = 2,
                            question = "Bis wann soll Frau Müller bleiben?",
                            options = listOf("17 Uhr", "18 Uhr", "19 Uhr", "20 Uhr"),
                            correctAnswer = "18 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Until when should Ms. Müller stay?",
                            optionsEnglish = listOf("5 PM", "6 PM", "7 PM", "8 PM")
                        ),
                        Question(
                            id = 3,
                            question = "Was soll Frau Müller machen?",
                            options = null,
                            correctAnswer = "Präsentation",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What should Ms. Müller do?"
                        ),
                        Question(
                            id = 4,
                            question = "Wann findet das Meeting statt?",
                            options = listOf("Heute", "Morgen", "Übermorgen", "Nächste Woche"),
                            correctAnswer = "Heute",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When does the meeting take place?",
                            optionsEnglish = listOf("Today", "Tomorrow", "The day after tomorrow", "Next week")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Freunde treffen",
                    description = "Meeting friends - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 13,
                    script = "Max: Hallo Anna! Wie geht es dir?\nAnna: Hallo Max! Gut, danke. Und dir?\nMax: Auch gut. Was machst du heute Abend?\nAnna: Ich gehe ins Kino mit meiner Schwester.\nMax: Welchen Film seht ihr?\nAnna: Einen Komödie. Hast du Lust mitzukommen?\nMax: Ja, gerne! Wann und wo?\nAnna: Um 20 Uhr im City-Kino.\nMax: Perfekt! Treffen wir uns davor?\nAnna: Ja, um 19:30 Uhr am Eingang.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Mit wem geht Anna ins Kino?",
                            options = listOf("Mit ihrem Bruder", "Mit ihrer Schwester", "Mit ihrem Freund", "Mit ihrer Mutter"),
                            correctAnswer = "Mit ihrer Schwester",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Who is Anna going to the cinema with?",
                            optionsEnglish = listOf("With her brother", "With her sister", "With her boyfriend", "With her mother")
                        ),
                        Question(
                            id = 2,
                            question = "Welchen Film sehen sie?",
                            options = listOf("Eine Komödie", "Einen Actionfilm", "Ein Drama", "Einen Horrorfilm"),
                            correctAnswer = "Eine Komödie",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What movie are they seeing?",
                            optionsEnglish = listOf("A comedy", "An action movie", "A drama", "A horror movie")
                        ),
                        Question(
                            id = 3,
                            question = "Um wie viel Uhr ist das Kino?",
                            options = null,
                            correctAnswer = "20 Uhr",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "At what time is the cinema?"
                        ),
                        Question(
                            id = 4,
                            question = "Wo treffen sie sich?",
                            options = listOf("Im Kino", "Am Eingang", "Im Café", "Zu Hause"),
                            correctAnswer = "Am Eingang",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where do they meet?",
                            optionsEnglish = listOf("In the cinema", "At the entrance", "At the café", "At home")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Beim Bäcker",
                    description = "At the bakery - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 14,
                    script = "Kunde: Guten Tag! Ich hätte gerne Brot.\nBäcker: Welches Brot möchten Sie?\nKunde: Das Weißbrot, bitte.\nBäcker: Groß oder klein?\nKunde: Groß, bitte. Und zwei Brötchen.\nBäcker: Gerne. Haben Sie auch Kuchen?\nBäcker: Ja, wir haben verschiedene Sorten.\nKunde: Zeigen Sie mir den Erdbeerkuchen.\nBäcker: Sehr gerne. Das macht zusammen 8,50 Euro.\nKunde: Hier sind 10 Euro.\nBäcker: Danke, 1,50 Euro zurück. Einen schönen Tag!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was möchte der Kunde zuerst?",
                            options = listOf("Kuchen", "Brot", "Brötchen", "Kekse"),
                            correctAnswer = "Brot",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the customer want first?",
                            optionsEnglish = listOf("Cake", "Bread", "Rolls", "Cookies")
                        ),
                        Question(
                            id = 2,
                            question = "Wie groß soll das Brot sein?",
                            options = listOf("Klein", "Groß", "Mittel", "Extra groß"),
                            correctAnswer = "Groß",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How big should the bread be?",
                            optionsEnglish = listOf("Small", "Large", "Medium", "Extra large")
                        ),
                        Question(
                            id = 3,
                            question = "Wie viele Brötchen möchte der Kunde?",
                            options = null,
                            correctAnswer = "zwei",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How many rolls does the customer want?"
                        ),
                        Question(
                            id = 4,
                            question = "Welchen Kuchen möchte der Kunde sehen?",
                            options = listOf("Schokoladenkuchen", "Erdbeerkuchen", "Apfelkuchen", "Zitronenkuchen"),
                            correctAnswer = "Erdbeerkuchen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What cake does the customer want to see?",
                            optionsEnglish = listOf("Chocolate cake", "Strawberry cake", "Apple cake", "Lemon cake")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Sport treiben",
                    description = "Doing sports - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 15,
                    script = "Trainer: Hallo zusammen! Heute machen wir Gymnastik.\nSportler: Was machen wir zuerst?\nTrainer: Zuerst dehnen wir die Muskeln.\nSportler: Wie lange?\nTrainer: Fünf Minuten. Dann machen wir Übungen.\nSportler: Welche Übungen?\nTrainer: Kniebeugen und Liegestütze.\nSportler: Wie viele?\nTrainer: 15 Kniebeugen und 10 Liegestütze.\nSportler: Okay, fangen wir an!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was machen sie heute?",
                            options = listOf("Fußball", "Gymnastik", "Schwimmen", "Laufen"),
                            correctAnswer = "Gymnastik",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What are they doing today?",
                            optionsEnglish = listOf("Soccer", "Gymnastics", "Swimming", "Running")
                        ),
                        Question(
                            id = 2,
                            question = "Was machen sie zuerst?",
                            options = listOf("Übungen", "Dehnen", "Laufen", "Springen"),
                            correctAnswer = "Dehnen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What do they do first?",
                            optionsEnglish = listOf("Exercises", "Stretching", "Running", "Jumping")
                        ),
                        Question(
                            id = 3,
                            question = "Wie lange dehnen sie?",
                            options = null,
                            correctAnswer = "fünf Minuten",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How long do they stretch?"
                        ),
                        Question(
                            id = 4,
                            question = "Wie viele Kniebeugen machen sie?",
                            options = listOf("10", "15", "20", "25"),
                            correctAnswer = "15",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How many squats do they do?",
                            optionsEnglish = listOf("10", "15", "20", "25")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Im Park spazieren",
                    description = "Walking in the park - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 16,
                    script = "Maria: Schau, wie schön der Park ist!\nAnna: Ja, die Blumen blühen prächtig.\nMaria: Hörst du die Vögel singen?\nAnna: Ja, und die Luft ist so frisch.\nMaria: Lass uns auf der Bank sitzen.\nAnna: Gute Idee. Was machen die Leute?\nMaria: Manche lesen Bücher, andere joggen.\nAnna: Und dort spielen Kinder.\nMaria: Ja, sie haben Spaß. Das ist erholsam.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo sind die Frauen?",
                            options = listOf("Im Garten", "Im Park", "Im Wald", "Am See"),
                            correctAnswer = "Im Park",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where are the women?",
                            optionsEnglish = listOf("In the garden", "In the park", "In the forest", "At the lake")
                        ),
                        Question(
                            id = 2,
                            question = "Was blüht im Park?",
                            options = listOf("Bäume", "Blumen", "Gras", "Sträucher"),
                            correctAnswer = "Blumen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is blooming in the park?",
                            optionsEnglish = listOf("Trees", "Flowers", "Grass", "Bushes")
                        ),
                        Question(
                            id = 3,
                            question = "Was machen manche Leute?",
                            options = null,
                            correctAnswer = "lesen Bücher",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What are some people doing?"
                        ),
                        Question(
                            id = 4,
                            question = "Was spielen im Park?",
                            options = listOf("Hunde", "Kinder", "Vögel", "Erwachsene"),
                            correctAnswer = "Kinder",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Who is playing in the park?",
                            optionsEnglish = listOf("Dogs", "Children", "Birds", "Adults")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Kleidung kaufen",
                    description = "Buying clothes - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 17,
                    script = "Verkäufer: Kann ich Ihnen helfen?\nKunde: Ja, ich suche eine Jacke.\nVerkäufer: Welche Größe haben Sie?\nKunde: Größe M, bitte.\nVerkäufer: Hier ist eine schöne Lederjacke.\nKunde: Wie viel kostet sie?\nVerkäufer: 120 Euro. Gefällt sie Ihnen?\nKunde: Ja, aber haben Sie auch Jeans?\nVerkäufer: Ja, in verschiedenen Größen.\nKunde: Zeigen Sie mir Größe 32.\nVerkäufer: Hier, die kosten 80 Euro.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was sucht der Kunde?",
                            options = listOf("Eine Hose", "Eine Jacke", "Ein Hemd", "Ein Kleid"),
                            correctAnswer = "Eine Jacke",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is the customer looking for?",
                            optionsEnglish = listOf("Pants", "A jacket", "A shirt", "A dress")
                        ),
                        Question(
                            id = 2,
                            question = "Welche Größe braucht der Kunde?",
                            options = listOf("S", "M", "L", "XL"),
                            correctAnswer = "M",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What size does the customer need?",
                            optionsEnglish = listOf("S", "M", "L", "XL")
                        ),
                        Question(
                            id = 3,
                            question = "Wie viel kostet die Jacke?",
                            options = null,
                            correctAnswer = "120 Euro",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How much does the jacket cost?"
                        ),
                        Question(
                            id = 4,
                            question = "Welche Jeansgröße möchte der Kunde?",
                            options = listOf("30", "32", "34", "36"),
                            correctAnswer = "32",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What jeans size does the customer want?",
                            optionsEnglish = listOf("30", "32", "34", "36")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Essen bestellen",
                    description = "Ordering food - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 18,
                    script = "Kellner: Guten Abend! Haben Sie reserviert?\nGast: Nein, aber wir brauchen einen Tisch für zwei.\nKellner: Hier ist ein freier Tisch. Die Karte, bitte.\nGast: Danke. Was empfehlen Sie?\nKellner: Die Pasta ist sehr gut heute.\nGast: Gut, ich nehme die Spaghetti.\nKellner: Und als Getränk?\nGast: Ein Glas Rotwein, bitte.\nKellner: Sehr gerne. Und für Sie?\nGast: Ich nehme den Salat und Wasser.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Für wie viele Personen brauchen sie einen Tisch?",
                            options = listOf("1", "2", "3", "4"),
                            correctAnswer = "2",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "For how many people do they need a table?",
                            optionsEnglish = listOf("1", "2", "3", "4")
                        ),
                        Question(
                            id = 2,
                            question = "Was empfiehlt der Kellner?",
                            options = listOf("Pizza", "Pasta", "Steak", "Fisch"),
                            correctAnswer = "Pasta",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the waiter recommend?",
                            optionsEnglish = listOf("Pizza", "Pasta", "Steak", "Fish")
                        ),
                        Question(
                            id = 3,
                            question = "Was bestellt die erste Person?",
                            options = null,
                            correctAnswer = "Spaghetti",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What does the first person order?"
                        ),
                        Question(
                            id = 4,
                            question = "Was trinkt die zweite Person?",
                            options = listOf("Rotwein", "Weißwein", "Bier", "Wasser"),
                            correctAnswer = "Wasser",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the second person drink?",
                            optionsEnglish = listOf("Red wine", "White wine", "Beer", "Water")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Uhrzeit und Termine",
                    description = "Time and appointments - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 19,
                    script = "Sekretärin: Guten Tag! Wann haben Sie einen Termin?\nPatient: Morgen um 10 Uhr.\nSekretärin: Der Arzt kommt um 9:30 Uhr.\nPatient: Kann ich früher kommen?\nSekretärin: Ja, um 9 Uhr ist noch frei.\nPatient: Gut, dann komme ich um 9 Uhr.\nSekretärin: Wie lange dauert die Untersuchung?\nPatient: Etwa 30 Minuten.\nSekretärin: Dann sind Sie um 9:30 Uhr fertig.\nPatient: Danke für die Information!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wann hatte der Patient einen Termin?",
                            options = listOf("Heute um 10 Uhr", "Morgen um 10 Uhr", "Übermorgen um 10 Uhr", "Nächste Woche"),
                            correctAnswer = "Morgen um 10 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When did the patient have an appointment?",
                            optionsEnglish = listOf("Today at 10 AM", "Tomorrow at 10 AM", "The day after tomorrow at 10 AM", "Next week")
                        ),
                        Question(
                            id = 2,
                            question = "Um wie viel Uhr kommt der Arzt?",
                            options = listOf("8:30 Uhr", "9:00 Uhr", "9:30 Uhr", "10:00 Uhr"),
                            correctAnswer = "9:30 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "At what time does the doctor arrive?",
                            optionsEnglish = listOf("8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM")
                        ),
                        Question(
                            id = 3,
                            question = "Wie lange dauert die Untersuchung?",
                            options = null,
                            correctAnswer = "30 Minuten",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How long does the examination take?"
                        ),
                        Question(
                            id = 4,
                            question = "Um wie viel Uhr ist der Patient fertig?",
                            options = listOf("9:00 Uhr", "9:30 Uhr", "10:00 Uhr", "10:30 Uhr"),
                            correctAnswer = "9:30 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "At what time is the patient finished?",
                            optionsEnglish = listOf("9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM")
                        )
                    ),
                    source = "Goethe"
                ))

                lessons.add(createHoerenLesson(
                    title = "Reisen und Urlaub",
                    description = "Travel and vacation - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 20,
                    script = "Reiseberater: Wohin möchten Sie reisen?\nKunde: Nach Italien, ans Meer.\nReiseberater: Für wie viele Personen?\nKunde: Für zwei Erwachsene und ein Kind.\nReiseberater: Wie lange bleiben Sie?\nKunde: Zwei Wochen im Juli.\nReiseberater: Wir haben ein schönes Hotel mit Pool.\nKunde: Wie viel kostet das?\nReiseberater: 1200 Euro für alle zusammen.\nKunde: Das ist günstig. Wann fliegen wir?\nReiseberater: Abflug ist am 15. Juli morgens.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wohin möchte der Kunde reisen?",
                            options = listOf("Nach Spanien", "Nach Italien", "Nach Frankreich", "Nach Griechenland"),
                            correctAnswer = "Nach Italien",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where does the customer want to travel?",
                            optionsEnglish = listOf("To Spain", "To Italy", "To France", "To Greece")
                        ),
                        Question(
                            id = 2,
                            question = "Für wie viele Personen ist die Reise?",
                            options = listOf("2 Personen", "3 Personen", "4 Personen", "5 Personen"),
                            correctAnswer = "3 Personen",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "For how many people is the trip?",
                            optionsEnglish = listOf("2 people", "3 people", "4 people", "5 people")
                        ),
                        Question(
                            id = 3,
                            question = "Wie lange bleiben sie?",
                            options = null,
                            correctAnswer = "zwei Wochen",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How long are they staying?"
                        ),
                        Question(
                            id = 4,
                            question = "Wann ist der Abflug?",
                            options = listOf("15. Juni", "15. Juli", "15. August", "15. September"),
                            correctAnswer = "15. Juli",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When is the departure?",
                            optionsEnglish = listOf("June 15th", "July 15th", "August 15th", "September 15th")
                        )
                    ),
                    source = "Goethe"
                ))

                // TELC Deutsch A1 Hören - Lessons (24-26)
                lessons.add(createHoerenLesson(
                    title = "Öffentliche Ansagen",
                    description = "Public announcements - TELC Hören Teil 1",
                    level = level,
                    orderIndex = 24,
                    script = "Ansage: Achtung! Achtung! Der Zug von Berlin nach München hat 10 Minuten Verspätung. Die Passagiere werden gebeten, am Gleis zu warten. Wir entschuldigen uns für die Unannehmlichkeiten.\n\n---\n\nAnsage: Liebe Fahrgäste, der Bus Linie 42 fährt heute ab 18 Uhr alle 20 Minuten vom Hauptbahnhof ab. Die Fahrt dauert etwa 25 Minuten bis zum Stadtzentrum.\n\n---\n\nAnsage: Aufmerksamkeit bitte! Der Supermarkt schließt heute um 20 Uhr. Bitte erledigen Sie Ihre Einkäufe rechtzeitig. Vielen Dank für Ihren Besuch!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wie viel Verspätung hat der Zug?",
                            options = listOf("5 Minuten", "10 Minuten", "15 Minuten", "20 Minuten"),
                            correctAnswer = "10 Minuten",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How much delay does the train have?",
                            optionsEnglish = listOf("5 minutes", "10 minutes", "15 minutes", "20 minutes")
                        ),
                        Question(
                            id = 2,
                            question = "Wie oft fährt der Bus abends?",
                            options = listOf("Alle 10 Minuten", "Alle 15 Minuten", "Alle 20 Minuten", "Alle 30 Minuten"),
                            correctAnswer = "Alle 20 Minuten",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How often does the bus run in the evening?",
                            optionsEnglish = listOf("Every 10 minutes", "Every 15 minutes", "Every 20 minutes", "Every 30 minutes")
                        ),
                        Question(
                            id = 3,
                            question = "Um wie viel Uhr schließt der Supermarkt?",
                            options = null,
                            correctAnswer = "20 Uhr",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "At what time does the supermarket close?"
                        ),
                        Question(
                            id = 4,
                            question = "Was sollen die Fahrgäste tun?",
                            options = listOf("Nach Hause gehen", "Am Gleis warten", "Den Zug verlassen", "Essen kaufen"),
                            correctAnswer = "Am Gleis warten",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What should the passengers do?",
                            optionsEnglish = listOf("Go home", "Wait at the platform", "Leave the train", "Buy food")
                        )
                    ),
                    source = "TELC"
                ))

                lessons.add(createHoerenLesson(
                    title = "Alltagsgespräche",
                    description = "Everyday conversations - TELC Hören Teil 2",
                    level = level,
                    orderIndex = 25,
                    script = "Verkäufer: Guten Tag! Kann ich Ihnen helfen?\nKunde: Ja, ich suche eine Jacke für den Winter.\nVerkäufer: Welche Größe brauchen Sie?\nKunde: Größe 48, bitte.\nVerkäufer: Hier ist eine warme Winterjacke. Sie kostet 89 Euro.\nKunde: Das ist ein bisschen teuer. Haben Sie eine günstigere?\nVerkäufer: Ja, diese hier kostet 65 Euro.\nKunde: Die nehme ich. Kann ich mit Karte bezahlen?\nVerkäufer: Natürlich, hier entlang zur Kasse.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was sucht der Kunde?",
                            options = listOf("Eine Hose", "Eine Jacke", "Ein Hemd", "Einen Mantel"),
                            correctAnswer = "Eine Jacke",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is the customer looking for?",
                            optionsEnglish = listOf("Pants", "A jacket", "A shirt", "A coat")
                        ),
                        Question(
                            id = 2,
                            question = "Welche Größe braucht der Kunde?",
                            options = listOf("46", "47", "48", "49"),
                            correctAnswer = "48",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What size does the customer need?",
                            optionsEnglish = listOf("46", "47", "48", "49")
                        ),
                        Question(
                            id = 3,
                            question = "Wie viel kostet die günstigere Jacke?",
                            options = null,
                            correctAnswer = "65 Euro",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How much does the cheaper jacket cost?"
                        ),
                        Question(
                            id = 4,
                            question = "Wie möchte der Kunde bezahlen?",
                            options = listOf("Bar", "Mit Karte", "Mit Scheck", "Online"),
                            correctAnswer = "Mit Karte",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How does the customer want to pay?",
                            optionsEnglish = listOf("Cash", "With card", "With check", "Online")
                        )
                    ),
                    source = "TELC"
                ))

                lessons.add(createHoerenLesson(
                    title = "Telefonate und Nachrichten",
                    description = "Phone calls and messages - TELC Hören Teil 3",
                    level = level,
                    orderIndex = 26,
                    script = "Hallo Anna, hier ist Thomas. Ich kann morgen nicht zum Treffen kommen, weil ich krank bin. Ich habe Fieber und muss im Bett bleiben. Können wir das Treffen auf nächste Woche verschieben? Ruf mich bitte zurück. Danke!\n\n---\n\nLiebe Eltern, die Schule ist heute früher aus. Bitte holen Sie Ihre Kinder bis 13 Uhr ab. Vielen Dank für Ihre Aufmerksamkeit.\n\n---\n\nGuten Tag, Sie haben die Nummer von Frau Müller gewählt. Ich bin gerade nicht da. Hinterlassen Sie bitte eine Nachricht nach dem Signalton. Piep!",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Warum kann Thomas nicht kommen?",
                            options = listOf("Er hat viel Arbeit", "Er ist krank", "Er hat einen Termin", "Er ist verreist"),
                            correctAnswer = "Er ist krank",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Why can't Thomas come?",
                            optionsEnglish = listOf("He has a lot of work", "He is sick", "He has an appointment", "He is traveling")
                        ),
                        Question(
                            id = 2,
                            question = "Bis wann müssen die Kinder abgeholt werden?",
                            options = listOf("12 Uhr", "13 Uhr", "14 Uhr", "15 Uhr"),
                            correctAnswer = "13 Uhr",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Until what time must the children be picked up?",
                            optionsEnglish = listOf("12 o'clock", "1 o'clock", "2 o'clock", "3 o'clock")
                        ),
                        Question(
                            id = 3,
                            question = "Was soll man hinterlassen?",
                            options = null,
                            correctAnswer = "eine Nachricht",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "What should one leave?"
                        ),
                        Question(
                            id = 4,
                            question = "Wann ist die Schule aus?",
                            options = listOf("Heute", "Morgen", "Übermorgen", "Nächste Woche"),
                            correctAnswer = "Heute",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "When does school end?",
                            optionsEnglish = listOf("Today", "Tomorrow", "The day after tomorrow", "Next week")
                        )
                    ),
                    source = "TELC"
                ))

                // ÖSD Zertifikat A1 Hören - Lessons (36-38)
                lessons.add(createHoerenLesson(
                    title = "Wegbeschreibungen folgen",
                    description = "Following directions - ÖSD Hören Teil 1",
                    level = level,
                    orderIndex = 36,
                    script = "Passant: Entschuldigung, wie komme ich zur Oper?\nEinheimischer: Geradeaus gehen, dann rechts in die Kärntner Straße. Nach 200 Metern links in die Operngasse. Die Oper ist auf der rechten Seite.\n\n---\n\nPassant: Wo ist das Rathaus?\nEinheimischer: Vom Stephansdom aus immer geradeaus die Stephansplatz entlang. Nach dem Café Central links in die Wipplinger Straße. Das Rathaus ist dann rechts.\n\n---\n\nPassant: Wie komme ich zum Naschmarkt?\nEinheimischer: Nehmen Sie die U-Bahn U1 bis Karlsplatz. Dann mit der U4 bis Kettenbrückengasse. Der Naschmarkt ist direkt bei der Station.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wie kommt man zur Oper?",
                            options = listOf("Geradeaus, rechts, links", "Links, rechts, geradeaus", "Rechts, links, geradeaus", "Geradeaus, links, rechts"),
                            correctAnswer = "Geradeaus, rechts, links",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How do you get to the Opera?",
                            optionsEnglish = listOf("Straight, right, left", "Left, right, straight", "Right, left, straight", "Straight, left, right")
                        ),
                        Question(
                            id = 2,
                            question = "Wo ist das Rathaus?",
                            options = listOf("Links von der Wipplinger Straße", "Rechts von der Wipplinger Straße", "Geradeaus vom Stephansplatz", "Neben dem Café Central"),
                            correctAnswer = "Rechts von der Wipplinger Straße",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where is the town hall?",
                            optionsEnglish = listOf("Left of Wipplinger Street", "Right of Wipplinger Street", "Straight from Stephansplatz", "Next to Café Central")
                        ),
                        Question(
                            id = 3,
                            question = "Wie kommt man zum Naschmarkt?",
                            options = null,
                            correctAnswer = "U1 bis Karlsplatz, dann U4 bis Kettenbrückengasse",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How do you get to the Naschmarkt?"
                        ),
                        Question(
                            id = 4,
                            question = "Was ist direkt bei der Station Kettenbrückengasse?",
                            options = listOf("Das Rathaus", "Der Naschmarkt", "Die Oper", "Der Stephansdom"),
                            correctAnswer = "Der Naschmarkt",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is directly at Kettenbrückengasse station?",
                            optionsEnglish = listOf("The town hall", "The Naschmarkt", "The Opera", "St. Stephen's Cathedral")
                        )
                    ),
                    source = "ÖSD"
                ))

                lessons.add(createHoerenLesson(
                    title = "Alltägliche Unterhaltungen",
                    description = "Everyday conversations - ÖSD Hören Teil 2",
                    level = level,
                    orderIndex = 37,
                    script = "Kellner: Guten Abend! Was darf es sein?\nGast: Ich hätte gerne das Wiener Schnitzel mit Kartoffelsalat.\nKellner: Möchten Sie etwas zu trinken?\nGast: Ein Viertel Rotwein, bitte.\n\n---\n\nVerkäuferin: Hallo! Kann ich Ihnen helfen?\nKunde: Ja, ich suche ein Sommerkleid Größe 38.\nVerkäuferin: Wir haben dieses hier in Blau oder Rot.\nKunde: Das blaue gefällt mir. Wie viel kostet es?\nVerkäuferin: 49 Euro.\n\n---\n\nFriseur: Was wünschen Sie?\nKunde: Ich möchte die Haare schneiden lassen.\nFriseur: Wie kurz soll es werden?\nKunde: Etwa 5 cm kürzer, bitte.\nFriseur: Und waschen?\nKunde: Ja, bitte.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Was bestellt der Gast zu essen?",
                            options = listOf("Wiener Schnitzel mit Kartoffeln", "Wiener Schnitzel mit Kartoffelsalat", "Schnitzel mit Salat", "Kartoffeln mit Schnitzel"),
                            correctAnswer = "Wiener Schnitzel mit Kartoffelsalat",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the guest order to eat?",
                            optionsEnglish = listOf("Wiener Schnitzel with potatoes", "Wiener Schnitzel with potato salad", "Schnitzel with salad", "Potatoes with schnitzel")
                        ),
                        Question(
                            id = 2,
                            question = "Was sucht der Kunde im Geschäft?",
                            options = listOf("Ein Wintermantel", "Ein Sommerkleid", "Eine Hose", "Ein Hemd"),
                            correctAnswer = "Ein Sommerkleid",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What is the customer looking for in the shop?",
                            optionsEnglish = listOf("A winter coat", "A summer dress", "Pants", "A shirt")
                        ),
                        Question(
                            id = 3,
                            question = "Wie viel kostet das Kleid?",
                            options = null,
                            correctAnswer = "49 Euro",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "How much does the dress cost?"
                        ),
                        Question(
                            id = 4,
                            question = "Was möchte der Kunde beim Friseur?",
                            options = listOf("Die Haare waschen", "Die Haare schneiden", "Die Haare färben", "Die Haare stylen"),
                            correctAnswer = "Die Haare schneiden",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What does the customer want at the hairdresser?",
                            optionsEnglish = listOf("Hair washing", "Hair cutting", "Hair coloring", "Hair styling")
                        )
                    ),
                    source = "ÖSD"
                ))

                lessons.add(createHoerenLesson(
                    title = "Radio- und TV-Nachrichten",
                    description = "Radio and TV news - ÖSD Hören Teil 3",
                    level = level,
                    orderIndex = 38,
                    script = "Nachrichtensprecher: In Wien hat heute der Opernball stattgefunden. Über 5000 Gäste waren dabei. Das Wetter war perfekt für das Event.\n\n---\n\nNachrichtensprecher: Die Wiener Philharmoniker haben gestern ein Konzert im Musikverein gegeben. Das Programm umfasste Werke von Mozart und Beethoven.\n\n---\n\nNachrichtensprecher: Morgen findet in Wien die Buchmesse statt. Über 300 Aussteller präsentieren ihre Neuerscheinungen. Der Eintritt ist frei.",
                    questions = listOf(
                        Question(
                            id = 1,
                            question = "Wo hat der Opernball stattgefunden?",
                            options = listOf("In Salzburg", "In Wien", "In Graz", "In Innsbruck"),
                            correctAnswer = "In Wien",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "Where did the Opera Ball take place?",
                            optionsEnglish = listOf("In Salzburg", "In Vienna", "In Graz", "In Innsbruck")
                        ),
                        Question(
                            id = 2,
                            question = "Was haben die Wiener Philharmoniker gespielt?",
                            options = listOf("Werke von Mozart", "Werke von Beethoven", "Werke von Mozart und Beethoven", "Werke von Bach"),
                            correctAnswer = "Werke von Mozart und Beethoven",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "What did the Vienna Philharmonic play?",
                            optionsEnglish = listOf("Works by Mozart", "Works by Beethoven", "Works by Mozart and Beethoven", "Works by Bach")
                        ),
                        Question(
                            id = 3,
                            question = "Wo findet die Buchmesse statt?",
                            options = null,
                            correctAnswer = "in Wien",
                            type = QuestionType.FILL_BLANK,
                            questionEnglish = "Where does the book fair take place?"
                        ),
                        Question(
                            id = 4,
                            question = "Wie viele Aussteller sind bei der Buchmesse?",
                            options = listOf("Über 100", "Über 200", "Über 300", "Über 400"),
                            correctAnswer = "Über 300",
                            type = QuestionType.MULTIPLE_CHOICE,
                            questionEnglish = "How many exhibitors are at the book fair?",
                            optionsEnglish = listOf("Over 100", "Over 200", "Over 300", "Over 400")
                        )
                    ),
                    source = "ÖSD"
                ))
            }

            "A2" -> {
                lessons.add(createHoerenLesson(
                    title = "Im Restaurant",
                    description = "Ordering food and drinks",
                    level = level,
                    orderIndex = 1,
                    script = "Kellner: Guten Tag! Haben Sie einen Tisch reserviert?\nGast: Nein, haben wir nicht. Haben Sie einen freien Tisch für zwei Personen?\nKellner: Ja, hier ist ein Tisch. Hier ist die Speisekarte.\nGast: Danke. Ich hätte gerne eine Pizza Margherita und ein Bier.\nKellner: Und für Sie?\nGast: Ich nehme einen Salat und ein Glas Wasser.",
                    questions = listOf(
                        Question(id = 1, question = "Wie viele Personen sind es?", options = listOf("1", "2", "3", "4"), correctAnswer = "2", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How many people are there?", optionsEnglish = listOf("1", "2", "3", "4")),
                        Question(id = 2, question = "Was bestellt die erste Person?", options = listOf("Salat und Wasser", "Pizza und Bier", "Pasta", "Steak"), correctAnswer = "Pizza und Bier", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the first person order?", optionsEnglish = listOf("Salad and water", "Pizza and beer", "Pasta", "Steak")),
                        Question(id = 3, question = "Haben sie einen Tisch reserviert?", options = listOf("Ja", "Nein"), correctAnswer = "Nein", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Did they reserve a table?", optionsEnglish = listOf("Yes", "No"))
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Einkaufen",
                    description = "Shopping conversation",
                    level = level,
                    orderIndex = 2,
                    script = "Verkäufer: Kann ich Ihnen helfen?\nKunde: Ja, ich suche ein Geschenk für meine Mutter.\nVerkäufer: Wie alt ist Ihre Mutter?\nKunde: Sie wird 60 Jahre alt.\nVerkäufer: Hier haben wir schöne Schmuckstücke. Was denken Sie?\nKunde: Das ist eine gute Idee. Wie viel kostet diese Kette?\nVerkäufer: 45 Euro. Soll ich sie einpacken?",
                    questions = listOf(
                        Question(id = 1, question = "Wen sucht der Kunde ein Geschenk für?", options = listOf("Seine Schwester", "Seine Mutter", "Seine Freundin", "Seine Tochter"), correctAnswer = "Seine Mutter", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Who is the customer looking for a gift for?", optionsEnglish = listOf("His sister", "His mother", "His girlfriend", "His daughter")),
                        Question(id = 2, question = "Wie alt wird die Mutter?", options = listOf("50", "55", "60", "65"), correctAnswer = "60", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How old will the mother be?", optionsEnglish = listOf("50", "55", "60", "65")),
                        Question(id = 3, question = "Wie viel kostet die Kette?", options = null, correctAnswer = "45 Euro", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "How much does the necklace cost?")
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Reiseplanung",
                    description = "Travel planning",
                    level = level,
                    orderIndex = 3,
                    script = "Claudia: Wohin fährst du in den Urlaub?\nStefan: Ich fahre nach Italien, nach Rom.\nClaudia: Das ist toll! Wie lange bleibst du?\nStefan: Zwei Wochen. Ich fliege am 15. Juli und komme am 29. Juli zurück.\nClaudia: Wo übernachtest du?\nStefan: In einem Hotel in der Nähe des Vatikans. Es ist nicht teuer, aber sehr schön.",
                    questions = listOf(
                        Question(id = 1, question = "Wohin fährt die Person?", options = listOf("Frankreich", "Italien", "Spanien", "Griechenland"), correctAnswer = "Italien", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Where is the person going?", optionsEnglish = listOf("France", "Italy", "Spain", "Greece")),
                        Question(id = 2, question = "Wie lange bleibt sie?", options = listOf("Eine Woche", "Zwei Wochen", "Drei Wochen", "Einen Monat"), correctAnswer = "Zwei Wochen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How long is she staying?", optionsEnglish = listOf("One week", "Two weeks", "Three weeks", "One month")),
                        Question(id = 3, question = "Wo übernachtet sie?", options = null, correctAnswer = "Hotel", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "Where is she staying?")
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Arztbesuch",
                    description = "Doctor visit",
                    level = level,
                    orderIndex = 4,
                    script = "Arzt: Guten Tag! Was fehlt Ihnen?\nPatient: Ich habe starke Kopfschmerzen und Fieber.\nArzt: Seit wann haben Sie diese Symptome?\nPatient: Seit gestern Abend. Ich kann kaum schlafen.\nArzt: Haben Sie auch Halsschmerzen?\nPatient: Ja, ein bisschen. Und ich fühle mich sehr müde.\nArzt: Das klingt nach einer Grippe. Ich verschreibe Ihnen Medikamente.",
                    questions = listOf(
                        Question(id = 1, question = "Was hat der Patient?", options = listOf("Bauchschmerzen", "Kopfschmerzen und Fieber", "Rückenschmerzen", "Zahnschmerzen"), correctAnswer = "Kopfschmerzen und Fieber", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the patient have?", optionsEnglish = listOf("Stomach ache", "Headache and fever", "Back pain", "Toothache")),
                        Question(id = 2, question = "Seit wann hat er die Symptome?", options = listOf("Heute", "Gestern Abend", "Vor einer Woche", "Vor einem Monat"), correctAnswer = "Gestern Abend", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Since when does he have the symptoms?", optionsEnglish = listOf("Today", "Yesterday evening", "A week ago", "A month ago")),
                        Question(id = 3, question = "Was verschreibt der Arzt?", options = null, correctAnswer = "Medikamente", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What does the doctor prescribe?")
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Hobbys und Interessen",
                    description = "Hobbies and interests",
                    level = level,
                    orderIndex = 5,
                    script = "Nina: Was machst du in deiner Freizeit?\nFelix: Ich spiele gerne Fußball und gehe ins Fitnessstudio. Und du?\nNina: Ich lese gerne Bücher und schaue Filme. Welche Art von Büchern liest du?\nFelix: Am liebsten Krimis und Science-Fiction. Und welche Filme magst du?\nNina: Ich mag Actionfilme und Komödien. Gehen wir zusammen ins Kino?\nFelix: Ja, gerne! Nächste Woche läuft ein neuer Actionfilm.",
                    questions = listOf(
                        Question(id = 1, question = "Was macht die erste Person in der Freizeit?", options = listOf("Nur lesen", "Fußball und Fitnessstudio", "Nur Filme schauen", "Nur ins Kino gehen"), correctAnswer = "Fußball und Fitnessstudio", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the first person do in their free time?", optionsEnglish = listOf("Only reading", "Football and gym", "Only watching movies", "Only going to cinema")),
                        Question(id = 2, question = "Welche Bücher liest die zweite Person?", options = listOf("Romane", "Krimis und Science-Fiction", "Biografien", "Kochbücher"), correctAnswer = "Krimis und Science-Fiction", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What kind of books does the second person read?", optionsEnglish = listOf("Novels", "Crime and science fiction", "Biographies", "Cookbooks")),
                        Question(id = 3, question = "Was für ein Film läuft nächste Woche?", options = null, correctAnswer = "Actionfilm", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What kind of movie is showing next week?")
                    )
                ))

                // Goethe A2 - Extended listening content
                lessons.add(createHoerenLesson(
                    title = "Wettergespräch",
                    description = "Talking about weather and plans (Goethe-Zertifikat A2)",
                    level = level,
                    orderIndex = 6,
                    script = "Anna: Hallo Thomas! Was für ein schönes Wetter heute!\nThomas: Ja, endlich scheint die Sonne. Was machst du heute?\nAnna: Ich gehe mit Freunden an den See. Wir wollen schwimmen und picknicken.\nThomas: Das klingt super! Wie ist die Wassertemperatur?\nAnna: Das Wasser ist 22 Grad warm. Perfekt zum Schwimmen!\nThomas: Ich würde gerne mitkommen, aber ich muss arbeiten.\nAnna: Schade! Vielleicht nächstes Wochenende?\nThomas: Ja, gerne! Das Wetter soll schön bleiben.",
                    questions = listOf(
                        Question(id = 1, question = "Wie ist das Wetter?", options = listOf("Regnerisch", "Bewölkt", "Sonnig", "Windig"), correctAnswer = "Sonnig", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How is the weather?", optionsEnglish = listOf("Rainy", "Cloudy", "Sunny", "Windy")),
                        Question(id = 2, question = "Was wollen Anna und ihre Freunde machen?", options = listOf("Einkaufen", "Schwimmen und picknicken", "Ins Kino gehen", "Sport treiben"), correctAnswer = "Schwimmen und picknicken", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What do Anna and her friends want to do?", optionsEnglish = listOf("Shopping", "Swimming and picnic", "Go to cinema", "Do sports")),
                        Question(id = 3, question = "Wie warm ist das Wasser?", options = null, correctAnswer = "22 Grad", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "How warm is the water?")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Shopping and prices
                lessons.add(createHoerenLesson(
                    title = "Im Bekleidungsgeschäft",
                    description = "Shopping for clothes (TELC Deutsch A2)",
                    level = level,
                    orderIndex = 7,
                    script = "Verkäuferin: Guten Tag! Kann ich Ihnen helfen?\nKundin: Ja, ich suche eine Winterjacke. Haben Sie Jacken in Größe 38?\nVerkäuferin: Ja, wir haben verschiedene Modelle. Diese hier ist sehr warm und hat eine Kapuze.\nKundin: Wie viel kostet sie?\nVerkäuferin: Normalerweise 89 Euro, aber heute im Angebot für 75 Euro.\nKundin: Die gefällt mir. Haben Sie sie auch in Blau?\nVerkäuferin: Moment, ich schaue nach... Ja, hier ist sie in Blau.\nKundin: Perfekt! Kann ich sie anprobieren?\nVerkäuferin: Natürlich, die Umkleidekabinen sind dort drüben.",
                    questions = listOf(
                        Question(id = 1, question = "Was sucht die Kundin?", options = listOf("Eine Hose", "Eine Winterjacke", "Ein Kleid", "Schuhe"), correctAnswer = "Eine Winterjacke", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What is the customer looking for?", optionsEnglish = listOf("Pants", "Winter jacket", "Dress", "Shoes")),
                        Question(id = 2, question = "Wie viel kostet die Jacke im Angebot?", options = listOf("89 Euro", "75 Euro", "65 Euro", "95 Euro"), correctAnswer = "75 Euro", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How much does the jacket cost on offer?", optionsEnglish = listOf("89 Euro", "75 Euro", "65 Euro", "95 Euro")),
                        Question(id = 3, question = "Was hat die Jacke?", options = null, correctAnswer = "Kapuze", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What does the jacket have?")
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Health and doctor's appointment
                lessons.add(createHoerenLesson(
                    title = "Beim Arzt",
                    description = "Doctor's appointment conversation (ÖSD Zertifikat A2)",
                    level = level,
                    orderIndex = 8,
                    script = "Arzt: Guten Tag, Frau Müller. Was führt Sie zu mir?\nPatientin: Guten Tag, Herr Doktor. Ich habe seit zwei Tagen starke Bauchschmerzen.\nArzt: Haben Sie auch Übelkeit oder Erbrechen?\nPatientin: Ja, mir ist übel, aber ich habe nicht erbrochen. Außerdem habe ich Durchfall.\nArzt: Seit wann haben Sie diese Symptome?\nPatientin: Seit vorgestern Abend. Ich habe auch Fieber, 38,5 Grad.\nArzt: Das klingt nach einer Magen-Darm-Grippe. Ich verschreibe Ihnen Medikamente gegen die Übelkeit.\nPatientin: Soll ich mich krankschreiben lassen?\nArzt: Ja, bleiben Sie zu Hause und trinken Sie viel Tee. In drei Tagen kommen Sie zur Kontrolle.",
                    questions = listOf(
                        Question(id = 1, question = "Was hat die Patientin?", options = listOf("Kopfschmerzen", "Bauchschmerzen", "Rückenschmerzen", "Halsschmerzen"), correctAnswer = "Bauchschmerzen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the patient have?", optionsEnglish = listOf("Headache", "Stomach pain", "Back pain", "Sore throat")),
                        Question(id = 2, question = "Wie hoch ist das Fieber?", options = listOf("37,5 Grad", "38,5 Grad", "39,5 Grad", "36,5 Grad"), correctAnswer = "38,5 Grad", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How high is the fever?", optionsEnglish = listOf("37.5 degrees", "38.5 degrees", "39.5 degrees", "36.5 degrees")),
                        Question(id = 3, question = "Was soll die Patientin trinken?", options = null, correctAnswer = "Tee", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What should the patient drink?")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Travel planning
                lessons.add(createHoerenLesson(
                    title = "Urlaubsplanung",
                    description = "Planning a vacation",
                    level = level,
                    orderIndex = 9,
                    script = "Lisa: Hallo Markus! Hast du schon Urlaubspläne?\nMarkus: Ja, wir fahren nächstes Jahr nach Spanien. Und ihr?\nLisa: Wir wollen nach Italien, nach Rom und Florenz.\nMarkus: Wie lange bleibt ihr?\nLisa: Drei Wochen im August. Wir fliegen hin und mieten ein Auto.\nMarkus: Das klingt toll! Wo übernachtet ihr?\nLisa: In kleinen Hotels und einer Ferienwohnung. Es soll nicht zu teuer sein.\nMarkus: Passt auf die Hitze auf! Im August ist es sehr heiß in Italien.\nLisa: Danke für den Tipp! Wir nehmen Sonnencreme mit.",
                    questions = listOf(
                        Question(id = 1, question = "Wohin fahren Lisa und ihre Familie?", options = listOf("Spanien", "Italien", "Frankreich", "Griechenland"), correctAnswer = "Italien", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Where are Lisa and her family going?", optionsEnglish = listOf("Spain", "Italy", "France", "Greece")),
                        Question(id = 2, question = "Wie lange bleiben sie?", options = listOf("Zwei Wochen", "Drei Wochen", "Vier Wochen", "Einen Monat"), correctAnswer = "Drei Wochen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How long are they staying?", optionsEnglish = listOf("Two weeks", "Three weeks", "Four weeks", "One month")),
                        Question(id = 3, question = "Wann fahren sie?", options = null, correctAnswer = "August", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "When are they going?")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - At the train station
                lessons.add(createHoerenLesson(
                    title = "Am Bahnhof",
                    description = "At the train station",
                    level = level,
                    orderIndex = 10,
                    script = "Reisender: Entschuldigung, wann fährt der nächste Zug nach Berlin?\nSchalter: Der nächste Zug fährt in 15 Minuten von Gleis 8.\nReisender: Ist das ein direkter Zug?\nSchalter: Nein, Sie müssen in Hannover umsteigen.\nReisender: Wie lange dauert die Fahrt insgesamt?\nSchalter: Etwa 4 Stunden. Sie kommen um 16:30 Uhr an.\nReisender: Haben Sie auch Fahrkartenautomaten?\nSchalter: Ja, dort drüben. Oder ich kann Ihnen hier eine Fahrkarte verkaufen.\nReisender: Dann bitte eine Fahrkarte einfach für Erwachsene.",
                    questions = listOf(
                        Question(id = 1, question = "Wann fährt der nächste Zug?", options = listOf("In 5 Minuten", "In 15 Minuten", "In 30 Minuten", "In einer Stunde"), correctAnswer = "In 15 Minuten", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "When does the next train leave?", optionsEnglish = listOf("In 5 minutes", "In 15 minutes", "In 30 minutes", "In an hour")),
                        Question(id = 2, question = "Wo muss der Reisende umsteigen?", options = listOf("Berlin", "Hannover", "München", "Hamburg"), correctAnswer = "Hannover", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Where does the traveler have to change trains?", optionsEnglish = listOf("Berlin", "Hannover", "Munich", "Hamburg")),
                        Question(id = 3, question = "Wie lange dauert die Fahrt?", options = null, correctAnswer = "4 Stunden", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "How long does the journey take?")
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Daily routines
                lessons.add(createHoerenLesson(
                    title = "Alltagsgespräche",
                    description = "Daily conversations about routines",
                    level = level,
                    orderIndex = 11,
                    script = "Maria: Guten Morgen, Frau Schneider! Wie geht es Ihnen?\nFrau Schneider: Guten Morgen! Danke, gut. Und Ihnen?\nMaria: Auch gut. Arbeiten Sie heute?\nFrau Schneider: Nein, heute habe ich frei. Ich gehe zum Friseur.\nMaria: Das ist schön. Wann haben Sie Termin?\nFrau Schneider: Um 10 Uhr. Danach kaufe ich ein und koche Mittagessen.\nMaria: Was kochen Sie heute?\nFrau Schneider: Einen Eintopf mit Gemüse und Fleisch. Mein Mann kommt um 13 Uhr nach Hause.\nMaria: Das klingt lecker! Einen schönen Tag noch!",
                    questions = listOf(
                        Question(id = 1, question = "Was macht Frau Schneider heute?", options = listOf("Sie arbeitet", "Sie geht zum Friseur", "Sie geht einkaufen", "Sie kocht"), correctAnswer = "Sie geht zum Friseur", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What is Mrs. Schneider doing today?", optionsEnglish = listOf("She works", "She goes to the hairdresser", "She goes shopping", "She cooks")),
                        Question(id = 2, question = "Um wie viel Uhr hat sie Termin?", options = listOf("9 Uhr", "10 Uhr", "11 Uhr", "12 Uhr"), correctAnswer = "10 Uhr", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "At what time does she have an appointment?", optionsEnglish = listOf("9 AM", "10 AM", "11 AM", "12 PM")),
                        Question(id = 3, question = "Was kocht sie?", options = null, correctAnswer = "Eintopf mit Gemüse und Fleisch", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What is she cooking?")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Making appointments
                lessons.add(createHoerenLesson(
                    title = "Terminvereinbarung",
                    description = "Making appointments",
                    level = level,
                    orderIndex = 12,
                    script = "Sekretärin: Guten Tag! Zahnarztpraxis Dr. Meier. Wie kann ich Ihnen helfen?\nPatient: Guten Tag! Ich möchte einen Termin für eine Zahnkontrolle.\nSekretärin: Wann würde es Ihnen passen?\nPatient: Am besten nächste Woche, vielleicht Dienstag oder Mittwoch.\nSekretärin: Dienstag um 14 Uhr oder Mittwoch um 10 Uhr?\nPatient: Mittwoch um 10 Uhr passt mir gut.\nSekretärin: Gut, dann trage ich Sie für Mittwoch, den 15. April um 10 Uhr ein.\nPatient: Vielen Dank! Soll ich etwas mitbringen?\nSekretärin: Bringen Sie bitte Ihre Versicherungskarte und frühere Röntgenbilder mit.",
                    questions = listOf(
                        Question(id = 1, question = "Wofür braucht der Patient einen Termin?", options = listOf("Für eine Operation", "Für eine Zahnkontrolle", "Für einen Check-up", "Für eine Behandlung"), correctAnswer = "Für eine Zahnkontrolle", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the patient need an appointment for?", optionsEnglish = listOf("For an operation", "For a dental check-up", "For a check-up", "For treatment")),
                        Question(id = 2, question = "Wann ist der Termin?", options = listOf("Dienstag um 14 Uhr", "Mittwoch um 10 Uhr", "Donnerstag um 15 Uhr", "Freitag um 11 Uhr"), correctAnswer = "Mittwoch um 10 Uhr", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "When is the appointment?", optionsEnglish = listOf("Tuesday at 2 PM", "Wednesday at 10 AM", "Thursday at 3 PM", "Friday at 11 AM")),
                        Question(id = 3, question = "Was soll der Patient mitbringen?", options = null, correctAnswer = "Versicherungskarte und Röntgenbilder", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What should the patient bring?")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Hotel booking
                lessons.add(createHoerenLesson(
                    title = "Hotelreservierung",
                    description = "Hotel reservation conversation",
                    level = level,
                    orderIndex = 13,
                    script = "Rezeptionist: Guten Abend! Herzlich willkommen im Hotel Berlin.\nGast: Guten Abend! Ich habe eine Reservierung auf den Namen Müller.\nRezeptionist: Moment, ich schaue nach... Ja, hier ist sie. Ein Doppelzimmer für drei Nächte.\nGast: Stimmt. Können Sie mir sagen, wo das Zimmer ist?\nRezeptionist: Ihr Zimmer ist im dritten Stock, Nummer 312. Hier ist Ihre Schlüsselkarte.\nGast: Gibt es WLAN im Zimmer?\nRezeptionist: Ja, natürlich. Das Passwort steht auf der Karte neben dem Schreibtisch.\nGast: Wann wird das Frühstück serviert?\nRezeptionist: Von 7 bis 10 Uhr im Frühstücksraum.",
                    questions = listOf(
                        Question(id = 1, question = "Wie viele Nächte bleibt der Gast?", options = listOf("Eine Nacht", "Zwei Nächte", "Drei Nächte", "Vier Nächte"), correctAnswer = "Drei Nächte", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How many nights is the guest staying?", optionsEnglish = listOf("One night", "Two nights", "Three nights", "Four nights")),
                        Question(id = 2, question = "Wo ist das Zimmer?", options = listOf("Im ersten Stock", "Im zweiten Stock", "Im dritten Stock", "Im vierten Stock"), correctAnswer = "Im dritten Stock", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Where is the room?", optionsEnglish = listOf("On the first floor", "On the second floor", "On the third floor", "On the fourth floor")),
                        Question(id = 3, question = "Wann gibt es Frühstück?", options = null, correctAnswer = "Von 7 bis 10 Uhr", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "When is breakfast served?")
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - At the pharmacy
                lessons.add(createHoerenLesson(
                    title = "In der Apotheke",
                    description = "At the pharmacy",
                    level = level,
                    orderIndex = 14,
                    script = "Apotheker: Guten Tag! Was kann ich für Sie tun?\nKunde: Hallo! Ich brauche etwas gegen Schnupfen und Husten.\nApotheker: Haben Sie auch Fieber oder Halsschmerzen?\nKunde: Nein, nur Schnupfen und Husten. Ich habe eine Erkältung.\nApotheker: Hier habe ich ein Kombinationspräparat. Es hilft gegen beide Symptome.\nKunde: Wie oft soll ich es nehmen?\nApotheker: Dreimal täglich nach dem Essen. Nehmen Sie es mit Wasser ein.\nKunde: Gibt es Nebenwirkungen?\nApotheker: Es kann müde machen. Fahren Sie kein Auto.\nKunde: Verstanden. Wie viel kostet es?\nApotheker: 12,50 Euro. Zahlen Sie an der Kasse.",
                    questions = listOf(
                        Question(id = 1, question = "Warum geht der Kunde in die Apotheke?", options = listOf("Für Schmerzmittel", "Für Schnupfen und Husten", "Für Vitamine", "Für Allergie"), correctAnswer = "Für Schnupfen und Husten", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Why does the customer go to the pharmacy?", optionsEnglish = listOf("For pain medication", "For runny nose and cough", "For vitamins", "For allergy")),
                        Question(id = 2, question = "Wie oft soll er das Medikament nehmen?", options = listOf("Einmal täglich", "Zweimal täglich", "Dreimal täglich", "Vier mal täglich"), correctAnswer = "Dreimal täglich", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How often should he take the medication?", optionsEnglish = listOf("Once daily", "Twice daily", "Three times daily", "Four times daily")),
                        Question(id = 3, question = "Was kann Nebenwirkung sein?", options = null, correctAnswer = "müde machen", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What can be a side effect?")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Job interview
                lessons.add(createHoerenLesson(
                    title = "Vorstellungsgespräch",
                    description = "Job interview conversation",
                    level = level,
                    orderIndex = 15,
                    script = "Interviewer: Guten Tag! Erzählen Sie uns etwas über Ihre Berufserfahrung.\nBewerber: Guten Tag! Ich habe drei Jahre als Verkäuferin gearbeitet.\nInterviewer: Warum möchten Sie bei uns arbeiten?\nBewerber: Ihr Unternehmen ist sehr bekannt und ich möchte in einem Team arbeiten.\nInterviewer: Was sind Ihre Stärken?\nBewerber: Ich bin kommunikativ und kann gut mit Kunden umgehen.\nInterviewer: Haben Sie auch Schwächen?\nBewerber: Manchmal bin ich zu perfektionistisch. Aber das hilft mir, gute Arbeit zu leisten.\nInterviewer: Vielen Dank für das Gespräch. Wir melden uns bei Ihnen.",
                    questions = listOf(
                        Question(id = 1, question = "Wie lange hat die Bewerberin gearbeitet?", options = listOf("Ein Jahr", "Zwei Jahre", "Drei Jahre", "Vier Jahre"), correctAnswer = "Drei Jahre", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How long has the applicant worked?", optionsEnglish = listOf("One year", "Two years", "Three years", "Four years")),
                        Question(id = 2, question = "Warum möchte sie bei dem Unternehmen arbeiten?", options = listOf("Wegen des Gehalts", "Wegen des Teams", "Wegen der Lage", "Wegen der Größe"), correctAnswer = "Wegen des Teams", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Why does she want to work for the company?", optionsEnglish = listOf("Because of the salary", "Because of the team", "Because of the location", "Because of the size")),
                        Question(id = 3, question = "Was ist ihre Stärke?", options = null, correctAnswer = "kommunikativ", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What is her strength?")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - At the bank
                lessons.add(createHoerenLesson(
                    title = "Bei der Bank",
                    description = "At the bank",
                    level = level,
                    orderIndex = 16,
                    script = "Bankangestellte: Guten Tag! Wie kann ich Ihnen helfen?\nKunde: Hallo! Ich möchte ein Konto eröffnen.\nBankangestellte: Welche Art von Konto wünschen Sie?\nKunde: Ein Girokonto für den täglichen Gebrauch.\nBankangestellte: Gut. Haben Sie Ihren Personalausweis dabei?\nKunde: Ja, hier ist er. Brauche ich noch etwas?\nBankangestellte: Eine Adressbestätigung, zum Beispiel eine Rechnung.\nKunde: Hier habe ich eine Stromrechnung.\nBankangestellte: Perfekt. Das Formular müssen Sie ausfüllen.\nKunde: Wie lange dauert es, bis das Konto aktiv ist?\nBankangestellte: Normalerweise zwei bis drei Werktage.",
                    questions = listOf(
                        Question(id = 1, question = "Was möchte der Kunde?", options = listOf("Ein Sparkonto", "Ein Girokonto", "Ein Kreditkonto", "Ein Depotkonto"), correctAnswer = "Ein Girokonto", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the customer want?", optionsEnglish = listOf("A savings account", "A checking account", "A credit account", "An investment account")),
                        Question(id = 2, question = "Was braucht er für die Kontoeröffnung?", options = listOf("Nur Personalausweis", "Personalausweis und Adressbestätigung", "Nur eine Rechnung", "Nichts"), correctAnswer = "Personalausweis und Adressbestätigung", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does he need to open the account?", optionsEnglish = listOf("Just ID", "ID and address confirmation", "Just a bill", "Nothing")),
                        Question(id = 3, question = "Wie lange dauert es bis das Konto aktiv ist?", options = null, correctAnswer = "zwei bis drei Werktage", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "How long does it take until the account is active?")
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Restaurant conversation
                lessons.add(createHoerenLesson(
                    title = "Im Restaurant bestellen",
                    description = "Ordering in a restaurant",
                    level = level,
                    orderIndex = 17,
                    script = "Kellner: Guten Abend! Haben Sie schon gewählt?\nGast: Ja, ich nehme die Bratwurst mit Sauerkraut.\nKellner: Möchten Sie dazu Pommes oder Kartoffelsalat?\nGast: Kartoffelsalat, bitte. Und ein Bier.\nKellner: Gerne. Und als Dessert?\nGast: Was empfehlen Sie?\nKellner: Unser Apfelkuchen ist sehr beliebt. Oder die Schokoladenmousse.\nGast: Dann den Apfelkuchen. Ist er hausgemacht?\nKellner: Ja, natürlich. Alles wird frisch zubereitet.\nGast: Perfekt! Bringen Sie bitte auch die Speisekarte für meinen Freund?",
                    questions = listOf(
                        Question(id = 1, question = "Was bestellt der Gast als Hauptgericht?", options = listOf("Schnitzel", "Bratwurst mit Sauerkraut", "Steak", "Fisch"), correctAnswer = "Bratwurst mit Sauerkraut", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the guest order as main course?", optionsEnglish = listOf("Schnitzel", "Sausage with sauerkraut", "Steak", "Fish")),
                        Question(id = 2, question = "Was empfiehlt der Kellner als Dessert?", options = listOf("Tiramisu", "Apfelkuchen", "Eis", "Käsekuchen"), correctAnswer = "Apfelkuchen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the waiter recommend as dessert?", optionsEnglish = listOf("Tiramisu", "Apple cake", "Ice cream", "Cheesecake")),
                        Question(id = 3, question = "Was trinkt der Gast?", options = null, correctAnswer = "Bier", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What is the guest drinking?")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Post office
                lessons.add(createHoerenLesson(
                    title = "Auf der Post",
                    description = "At the post office",
                    level = level,
                    orderIndex = 18,
                    script = "Kunde: Guten Tag! Ich möchte ein Paket aufgeben.\nPostangestellte: Guten Tag! Wohin soll das Paket?\nKunde: Nach Österreich, nach Wien.\nPostangestellte: Wie schwer ist das Paket?\nKunde: Etwa 2 Kilogramm.\nPostangestellte: Dann kostet der Versand 15 Euro.\nKunde: Geht das auch als Express?\nPostangestellte: Ja, dann kommt es in zwei Tagen an. Das kostet 25 Euro.\nKunde: Gut, dann nehme ich Express. Hier ist das Paket.\nPostangestellte: Füllen Sie bitte dieses Formular aus.\nKunde: Natürlich. Wann kann ich es abholen?\nPostangestellte: Sie bekommen eine Abholnummer. Kommen Sie morgen wieder.",
                    questions = listOf(
                        Question(id = 1, question = "Wohin geht das Paket?", options = listOf("Nach Deutschland", "Nach Österreich", "Nach Frankreich", "Nach Italien"), correctAnswer = "Nach Österreich", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Where is the package going?", optionsEnglish = listOf("To Germany", "To Austria", "To France", "To Italy")),
                        Question(id = 2, question = "Wie schwer ist das Paket?", options = listOf("1 Kilogramm", "2 Kilogramm", "3 Kilogramm", "4 Kilogramm"), correctAnswer = "2 Kilogramm", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How heavy is the package?", optionsEnglish = listOf("1 kilogram", "2 kilograms", "3 kilograms", "4 kilograms")),
                        Question(id = 3, question = "Wie schnell kommt das Express-Paket an?", options = null, correctAnswer = "in zwei Tagen", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "How fast does the express package arrive?")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Cinema tickets
                lessons.add(createHoerenLesson(
                    title = "Kinokarten kaufen",
                    description = "Buying cinema tickets",
                    level = level,
                    orderIndex = 19,
                    script = "Kartenverkäufer: Guten Abend! Was möchten Sie?\nBesucher: Zwei Karten für den Film 'Der verlorene Sohn'.\nKartenverkäufer: Wann möchten Sie gehen?\nBesucher: Heute Abend um 20 Uhr.\nKartenverkäufer: In welchem Saal läuft der Film?\nBesucher: Im Saal 3. Sind noch Karten frei?\nKartenverkäufer: Ja, Reihe 8, Plätze 12 und 13.\nBesucher: Wie viel kosten die Karten?\nKartenverkäufer: Normal 12 Euro pro Karte, aber heute Studentenrabatt.\nBesucher: Wir sind Studenten. Dann 10 Euro pro Karte?\nKartenverkäufer: Ja, genau. Macht zusammen 20 Euro.\nBesucher: Hier sind 25 Euro.\nKartenverkäufer: Hier ist Ihr Wechselgeld und die Karten.",
                    questions = listOf(
                        Question(id = 1, question = "Wann geht der Film?", options = listOf("Um 18 Uhr", "Um 20 Uhr", "Um 22 Uhr", "Um 16 Uhr"), correctAnswer = "Um 20 Uhr", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "When does the movie start?", optionsEnglish = listOf("At 6 PM", "At 8 PM", "At 10 PM", "At 4 PM")),
                        Question(id = 2, question = "Wo läuft der Film?", options = listOf("Saal 1", "Saal 2", "Saal 3", "Saal 4"), correctAnswer = "Saal 3", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Where does the movie play?", optionsEnglish = listOf("Hall 1", "Hall 2", "Hall 3", "Hall 4")),
                        Question(id = 3, question = "Wie viel kosten die Karten mit Rabatt?", options = null, correctAnswer = "10 Euro pro Karte", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "How much do the tickets cost with discount?")
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Library conversation
                lessons.add(createHoerenLesson(
                    title = "In der Bibliothek",
                    description = "At the library",
                    level = level,
                    orderIndex = 20,
                    script = "Bibliothekar: Hallo! Wie kann ich Ihnen helfen?\nLeser: Ich suche ein Buch über deutsche Geschichte.\nBibliothekar: Welche Epoche interessiert Sie?\nLeser: Die Zeit nach dem Zweiten Weltkrieg.\nBibliothekar: Hier haben wir ein gutes Buch: 'Deutschland nach 1945'.\nLeser: Kann ich es ausleihen?\nBibliothekar: Ja, aber Sie brauchen einen Bibliotheksausweis.\nLeser: Den habe ich schon. Wie lange kann ich es behalten?\nBibliothekar: Vier Wochen. Wenn Sie es verlängern wollen, rufen Sie an.\nLeser: Gibt es auch Bücher auf Englisch?\nBibliothekar: Ja, in der englischen Abteilung. Soll ich Ihnen den Weg zeigen?\nLeser: Ja, gerne. Vielen Dank!",
                    questions = listOf(
                        Question(id = 1, question = "Was sucht der Leser?", options = listOf("Ein Kochbuch", "Ein Buch über deutsche Geschichte", "Einen Roman", "Ein Sachbuch über Tiere"), correctAnswer = "Ein Buch über deutsche Geschichte", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What is the reader looking for?", optionsEnglish = listOf("A cookbook", "A book about German history", "A novel", "A book about animals")),
                        Question(id = 2, question = "Wie lange kann er das Buch behalten?", options = listOf("Zwei Wochen", "Drei Wochen", "Vier Wochen", "Fünf Wochen"), correctAnswer = "Vier Wochen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How long can he keep the book?", optionsEnglish = listOf("Two weeks", "Three weeks", "Four weeks", "Five weeks")),
                        Question(id = 3, question = "Was braucht er für die Ausleihe?", options = null, correctAnswer = "Bibliotheksausweis", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What does he need to borrow the book?")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Hairdresser
                lessons.add(createHoerenLesson(
                    title = "Beim Friseur",
                    description = "At the hairdresser",
                    level = level,
                    orderIndex = 21,
                    script = "Friseurin: Hallo! Was wünschen Sie?\nKunde: Ich möchte meine Haare schneiden lassen.\nFriseurin: Wie kurz sollen die Haare werden?\nKunde: Etwa 5 Zentimeter kürzer als jetzt.\nFriseurin: Gut. Möchten Sie auch eine neue Farbe?\nKunde: Nein, nur schneiden. Aber waschen Sie die Haare?\nFriseurin: Ja, natürlich. Waschen, schneiden und föhnen.\nKunde: Wie viel Zeit brauchen Sie?\nFriseurin: Etwa 45 Minuten. Haben Sie einen Termin?\nKunde: Nein, aber ich kann warten.\nFriseurin: Gut, setzen Sie sich bitte in den Wartebereich.\nKunde: Vielen Dank!",
                    questions = listOf(
                        Question(id = 1, question = "Was möchte der Kunde?", options = listOf("Haare waschen", "Haare schneiden", "Haare färben", "Haare kämmen"), correctAnswer = "Haare schneiden", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What does the customer want?", optionsEnglish = listOf("Hair washing", "Hair cutting", "Hair coloring", "Hair combing")),
                        Question(id = 2, question = "Wie viel kürzer sollen die Haare werden?", options = listOf("3 Zentimeter", "5 Zentimeter", "7 Zentimeter", "10 Zentimeter"), correctAnswer = "5 Zentimeter", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How much shorter should the hair be?", optionsEnglish = listOf("3 centimeters", "5 centimeters", "7 centimeters", "10 centimeters")),
                        Question(id = 3, question = "Wie lange dauert es ungefähr?", options = null, correctAnswer = "45 Minuten", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "How long does it take approximately?")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Supermarket
                lessons.add(createHoerenLesson(
                    title = "Im Supermarkt",
                    description = "At the supermarket",
                    level = level,
                    orderIndex = 22,
                    script = "Kassiererin: Hallo! Haben Sie alles gefunden?\nKunde: Ja, danke. Nur die Milch fehlt noch.\nKassiererin: Welche Milch möchten Sie?\nKunde: Die fettarme Milch, bitte. Ein Liter.\nKassiererin: Hier ist sie. Sonst noch etwas?\nKunde: Nein, das war's. Wie viel kostet das alles?\nKassiererin: Das macht 24,50 Euro.\nKunde: Hier sind 25 Euro.\nKassiererin: Danke, 50 Cent zurück. Einen schönen Tag!\nKunde: Danke, Ihnen auch! Auf Wiedersehen.\nKassiererin: Auf Wiedersehen!",
                    questions = listOf(
                        Question(id = 1, question = "Welche Milch möchte der Kunde?", options = listOf("Vollmilch", "Fettarme Milch", "Sojamilch", "Mandel milch"), correctAnswer = "Fettarme Milch", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What kind of milk does the customer want?", optionsEnglish = listOf("Whole milk", "Low-fat milk", "Soy milk", "Almond milk")),
                        Question(id = 2, question = "Wie viel Liter Milch möchte er?", options = listOf("0,5 Liter", "1 Liter", "1,5 Liter", "2 Liter"), correctAnswer = "1 Liter", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How many liters of milk does he want?", optionsEnglish = listOf("0.5 liters", "1 liter", "1.5 liters", "2 liters")),
                        Question(id = 3, question = "Wie viel kostet der Einkauf?", options = null, correctAnswer = "24,50 Euro", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "How much does the shopping cost?")
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Travel agency
                lessons.add(createHoerenLesson(
                    title = "Im Reisebüro",
                    description = "At the travel agency",
                    level = level,
                    orderIndex = 23,
                    script = "Reiseberater: Guten Tag! Wohin möchten Sie reisen?\nKunde: Nach Prag, in die Tschechische Republik.\nReiseberater: Wann möchten Sie fahren?\nKunde: Im Juni, für eine Woche.\nReiseberater: Wir haben ein schönes Hotel im Stadtzentrum.\nKunde: Wie viel kostet das?\nReiseberater: Für 7 Nächte mit Frühstück: 450 Euro pro Person.\nKunde: Ist der Flug im Preis inbegriffen?\nReiseberater: Ja, Hin- und Rückflug von Frankfurt.\nKunde: Gibt es auch andere Hotels?\nReiseberater: Ja, ein günstigeres Hotel für 380 Euro.\nKunde: Dann nehme ich das günstigere. Wann ist der nächste Flug?\nReiseberater: Morgen um 9:15 Uhr. Soll ich für Sie buchen?",
                    questions = listOf(
                        Question(id = 1, question = "Wohin möchte der Kunde reisen?", options = listOf("Nach Berlin", "Nach Prag", "Nach Wien", "Nach Budapest"), correctAnswer = "Nach Prag", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Where does the customer want to travel?", optionsEnglish = listOf("To Berlin", "To Prague", "To Vienna", "To Budapest")),
                        Question(id = 2, question = "Wie lange möchte er bleiben?", options = listOf("3 Tage", "Eine Woche", "Zwei Wochen", "Einen Monat"), correctAnswer = "Eine Woche", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How long does he want to stay?", optionsEnglish = listOf("3 days", "One week", "Two weeks", "One month")),
                        Question(id = 3, question = "Wie viel kostet das günstigere Hotel?", options = null, correctAnswer = "380 Euro", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "How much does the cheaper hotel cost?")
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Police station
                lessons.add(createHoerenLesson(
                    title = "Auf dem Polizeirevier",
                    description = "At the police station",
                    level = level,
                    orderIndex = 24,
                    script = "Polizist: Guten Tag! Was ist passiert?\nBürger: Guten Tag! Man hat mir die Tasche gestohlen.\nPolizist: Wann und wo ist das passiert?\nBürger: Vor einer Stunde am Hauptbahnhof.\nPolizist: Können Sie die Tasche beschreiben?\nBürger: Es ist eine schwarze Ledertasche mit einem Reißverschluss.\nPolizist: Waren wichtige Dinge darin?\nBürger: Ja, mein Portemonnaie mit Ausweis und Kreditkarte.\nPolizist: Wir brauchen Ihre Personalien. Füllen Sie bitte dieses Formular aus.\nBürger: Natürlich. Bekomme ich eine Anzeige?\nPolizist: Ja, wir stellen eine Anzeige aus. Kommen Sie in zwei Tagen wieder.\nBürger: Vielen Dank für Ihre Hilfe.",
                    questions = listOf(
                        Question(id = 1, question = "Was ist gestohlen worden?", options = listOf("Ein Auto", "Eine Tasche", "Ein Fahrrad", "Ein Handy"), correctAnswer = "Eine Tasche", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "What was stolen?", optionsEnglish = listOf("A car", "A bag", "A bicycle", "A mobile phone")),
                        Question(id = 2, question = "Wo ist es passiert?", options = listOf("Am Flughafen", "Am Hauptbahnhof", "Im Supermarkt", "Zu Hause"), correctAnswer = "Am Hauptbahnhof", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "Where did it happen?", optionsEnglish = listOf("At the airport", "At the main station", "At the supermarket", "At home")),
                        Question(id = 3, question = "Was war in der Tasche?", options = null, correctAnswer = "Portemonnaie mit Ausweis und Kreditkarte", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "What was in the bag?")
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Museum visit
                lessons.add(createHoerenLesson(
                    title = "Im Museum",
                    description = "At the museum",
                    level = level,
                    orderIndex = 25,
                    script = "Museumswächter: Hallo! Willkommen im Museum. Haben Sie Eintrittskarten?\nBesucher: Nein, noch nicht. Wie viel kostet der Eintritt?\nMuseumswächter: Erwachsene 12 Euro, Kinder unter 12 Jahren frei.\nBesucher: Wir sind zwei Erwachsene und ein Kind.\nMuseumswächter: Dann macht das 24 Euro. Möchten Sie eine Führung?\nBesucher: Ja, wann beginnt die nächste Führung?\nMuseumswächter: In 15 Minuten. Sie dauert etwa eine Stunde.\nBesucher: Perfekt! Gibt es auch einen Museumsshop?\nMuseumswächter: Ja, im Erdgeschoss. Dort finden Sie Bücher und Souvenirs.\nBesucher: Vielen Dank! Wo sind die Toiletten?\nMuseumswächter: Gleich rechts neben dem Eingang.",
                    questions = listOf(
                        Question(id = 1, question = "Wie viel kostet der Eintritt für Erwachsene?", options = listOf("8 Euro", "10 Euro", "12 Euro", "15 Euro"), correctAnswer = "12 Euro", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How much is the entrance fee for adults?", optionsEnglish = listOf("8 Euro", "10 Euro", "12 Euro", "15 Euro")),
                        Question(id = 2, question = "Wie lange dauert die Führung?", options = listOf("30 Minuten", "45 Minuten", "Eine Stunde", "1,5 Stunden"), correctAnswer = "Eine Stunde", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE, questionEnglish = "How long does the tour take?", optionsEnglish = listOf("30 minutes", "45 minutes", "One hour", "1.5 hours")),
                        Question(id = 3, question = "Wo ist der Museumsshop?", options = null, correctAnswer = "im Erdgeschoss", correctAnswers = null, type = QuestionType.FILL_BLANK, questionEnglish = "Where is the museum shop?")
                    ),
                    source = "TELC"
                ))
            }
            
            "B1" -> {
                lessons.add(createHoerenLesson(
                    title = "Jobinterview",
                    description = "Job interview conversation",
                    level = level,
                    orderIndex = 1,
                    script = "Interviewer: Guten Tag! Vielen Dank, dass Sie gekommen sind. Erzählen Sie mir etwas über sich.\nBewerber: Guten Tag! Ich heiße Maria Schmidt und bin 28 Jahre alt. Ich habe Betriebswirtschaft studiert und arbeite seit drei Jahren in der Marketingabteilung einer Firma.\nInterviewer: Warum möchten Sie zu uns wechseln?\nBewerber: Ich suche neue Herausforderungen und möchte in einem internationalen Unternehmen arbeiten. Ihre Firma ist sehr innovativ und das gefällt mir.\nInterviewer: Was sind Ihre Stärken?\nBewerber: Ich bin sehr organisiert, kann gut im Team arbeiten und spreche fließend Englisch und Französisch.",
                    questions = listOf(
                        Question(id = 1, question = "Was hat Maria studiert?", options = listOf("Medizin", "Betriebswirtschaft", "Jura", "Informatik"), correctAnswer = "Betriebswirtschaft", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wie lange arbeitet sie schon?", options = listOf("1 Jahr", "2 Jahre", "3 Jahre", "4 Jahre"), correctAnswer = "3 Jahre", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Welche Sprachen spricht sie?", options = null, correctAnswer = "Englisch und Französisch", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Umweltdiskussion",
                    description = "Environmental discussion",
                    level = level,
                    orderIndex = 2,
                    script = "Laura: Was denkst du über den Klimawandel?\nMarkus: Ich finde es sehr besorgniserregend. Wir müssen sofort handeln.\nLaura: Was können wir als Einzelpersonen tun?\nMarkus: Wir können weniger Auto fahren, mehr öffentliche Verkehrsmittel nutzen und weniger Fleisch essen. Auch das Recycling ist wichtig.\nLaura: Und was sollte die Politik tun?\nMarkus: Die Politik sollte erneuerbare Energien fördern und strengere Umweltgesetze einführen. Außerdem sollten sie Unternehmen dazu verpflichten, umweltfreundlicher zu werden.",
                    questions = listOf(
                        Question(id = 1, question = "Was kann man als Einzelperson tun?", options = listOf("Nichts", "Weniger Auto fahren", "Nur protestieren", "Nur wählen"), correctAnswer = "Weniger Auto fahren", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was sollte die Politik fördern?", options = listOf("Kohle", "Erdöl", "Erneuerbare Energien", "Atomkraft"), correctAnswer = "Erneuerbare Energien", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was ist wichtig für die Umwelt?", options = null, correctAnswer = "Recycling", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Technologie und Zukunft",
                    description = "Technology and future discussion",
                    level = level,
                    orderIndex = 3,
                    script = "Sandra: Wie siehst du die Zukunft der Arbeit?\nAndreas: Ich denke, dass viele Jobs durch Automatisierung verschwinden werden, aber auch neue entstehen.\nSandra: Was bedeutet das für die Bildung?\nAndreas: Wir müssen uns lebenslang weiterbilden und digitale Kompetenzen entwickeln. Die Schulen sollten mehr auf Kreativität und Problemlösung setzen.\nSandra: Und was ist mit der künstlichen Intelligenz?\nAndreas: KI wird unser Leben verändern, aber wir müssen sie verantwortungsvoll einsetzen. Sie kann uns bei der Arbeit helfen, aber sie sollte uns nicht ersetzen.",
                    questions = listOf(
                        Question(id = 1, question = "Was wird durch Automatisierung passieren?", options = listOf("Alle Jobs verschwinden", "Viele Jobs verschwinden, neue entstehen", "Nichts ändert sich", "Nur neue Jobs entstehen"), correctAnswer = "Viele Jobs verschwinden, neue entstehen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was ist wichtig für die Bildung?", options = listOf("Nur Mathematik", "Lebenslanges Lernen", "Nur Sprachen", "Nur Sport"), correctAnswer = "Lebenslanges Lernen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was sollte KI nicht tun?", options = null, correctAnswer = "uns ersetzen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Gesundheit und Lifestyle",
                    description = "Health and lifestyle discussion",
                    level = level,
                    orderIndex = 4,
                    script = "Katrin: Wie wichtig ist gesunde Ernährung für dich?\nDaniel: Sehr wichtig! Ich achte darauf, was ich esse und koche meistens selbst.\nKatrin: Was ist dein Geheimnis für ein gesundes Leben?\nDaniel: Ausgewogene Ernährung, regelmäßiger Sport und genug Schlaf. Ich gehe dreimal pro Woche ins Fitnessstudio und versuche, acht Stunden zu schlafen.\nKatrin: Und wie gehst du mit Stress um?\nDaniel: Ich mache Yoga und Meditation. Das hilft mir, entspannt zu bleiben. Außerdem versuche ich, Arbeit und Privatleben in Balance zu halten.",
                    questions = listOf(
                        Question(id = 1, question = "Was ist wichtig für ein gesundes Leben?", options = listOf("Nur Sport", "Nur Ernährung", "Ausgewogene Ernährung, Sport, Schlaf", "Nur Schlaf"), correctAnswer = "Ausgewogene Ernährung, Sport, Schlaf", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wie oft geht sie ins Fitnessstudio?", options = listOf("Einmal pro Woche", "Zweimal pro Woche", "Dreimal pro Woche", "Täglich"), correctAnswer = "Dreimal pro Woche", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was macht sie gegen Stress?", options = null, correctAnswer = "Yoga und Meditation", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Kultur und Gesellschaft",
                    description = "Culture and society discussion",
                    level = level,
                    orderIndex = 5,
                    script = "Monika: Wie wichtig ist Kultur in unserer Gesellschaft?\nChristian: Sehr wichtig! Kultur verbindet Menschen und hilft uns, andere Perspektiven zu verstehen.\nMonika: Was ist deine Lieblingskultur?\nChristian: Ich interessiere mich sehr für die deutsche Kultur, besonders für die Literatur und Musik. Goethe und Beethoven sind meine Favoriten.\nMonika: Und wie siehst du die Rolle der Kunst?\nChristian: Kunst kann uns zum Nachdenken bringen und die Gesellschaft verändern. Sie ist ein Spiegel unserer Zeit und zeigt uns neue Möglichkeiten.",
                    questions = listOf(
                        Question(id = 1, question = "Was verbindet Kultur?", options = listOf("Nur Geld", "Menschen", "Nur Politik", "Nur Wirtschaft"), correctAnswer = "Menschen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Wer sind ihre Favoriten?", options = listOf("Mozart und Schiller", "Goethe und Beethoven", "Bach und Kafka", "Wagner und Mann"), correctAnswer = "Goethe und Beethoven", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was kann Kunst tun?", options = null, correctAnswer = "uns zum Nachdenken bringen", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
            }
            
            "B2" -> {
                lessons.add(createHoerenLesson(
                    title = "Wissenschaft und Forschung",
                    description = "Science and research discussion",
                    level = level,
                    orderIndex = 1,
                    script = "Dr. Weber: Was hältst du von der aktuellen Forschung zu erneuerbaren Energien?\nProf. Müller: Ich finde die Entwicklungen sehr vielversprechend. Die Solartechnologie wird immer effizienter und günstiger.\nDr. Weber: Und was ist mit der Kernfusion?\nProf. Müller: Das ist ein interessanter Ansatz, aber noch sehr experimentell. Es könnte eine Lösung für die Zukunft sein, aber wir brauchen jetzt Lösungen.\nDr. Weber: Welche Rolle spielt Deutschland in der Forschung?\nProf. Müller: Deutschland ist ein führendes Land in der Wissenschaft, besonders in den Bereichen Ingenieurwesen und Medizin. Die Zusammenarbeit zwischen Universitäten und Industrie ist sehr gut.",
                    questions = listOf(
                        Question(id = 1, question = "Was wird immer effizienter?", options = listOf("Kernkraft", "Solartechnologie", "Windkraft", "Wasserkraft"), correctAnswer = "Solartechnologie", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was ist noch experimentell?", options = listOf("Solarenergie", "Windenergie", "Kernfusion", "Wasserkraft"), correctAnswer = "Kernfusion", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "In welchen Bereichen ist Deutschland führend?", options = null, correctAnswer = "Ingenieurwesen und Medizin", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Globalisierung und Wirtschaft",
                    description = "Globalization and economy discussion",
                    level = level,
                    orderIndex = 2,
                    script = "Dr. Schmidt: Wie beeinflusst die Globalisierung die deutsche Wirtschaft?\nProf. Wagner: Die Globalisierung hat sowohl positive als auch negative Auswirkungen. Einerseits profitieren wir vom internationalen Handel, andererseits gibt es mehr Konkurrenz.\nDr. Schmidt: Was bedeutet das für die Arbeitsplätze?\nProf. Wagner: Einige Jobs gehen verloren, aber es entstehen auch neue Möglichkeiten. Die Digitalisierung schafft neue Berufe, die vor 20 Jahren noch nicht existierten.\nDr. Schmidt: Und wie sieht es mit der Nachhaltigkeit aus?\nProf. Wagner: Das ist ein wichtiger Punkt. Wir müssen wirtschaftliches Wachstum mit Umweltschutz in Einklang bringen. Nachhaltige Geschäftsmodelle werden immer wichtiger.",
                    questions = listOf(
                        Question(id = 1, question = "Was hat die Globalisierung?", options = listOf("Nur positive Auswirkungen", "Nur negative Auswirkungen", "Positive und negative Auswirkungen", "Keine Auswirkungen"), correctAnswer = "Positive und negative Auswirkungen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was schafft die Digitalisierung?", options = listOf("Nur Probleme", "Neue Berufe", "Nur Arbeitslosigkeit", "Nur Konkurrenz"), correctAnswer = "Neue Berufe", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was wird immer wichtiger?", options = null, correctAnswer = "nachhaltige Geschäftsmodelle", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Medien und Kommunikation",
                    description = "Media and communication discussion",
                    level = level,
                    orderIndex = 3,
                    script = "Dr. Fischer: Wie hat sich die Medienlandschaft in den letzten Jahren verändert?\nProf. Hoffmann: Dramatisch! Soziale Medien dominieren jetzt die Kommunikation, und traditionelle Medien kämpfen um ihre Existenz.\nDr. Fischer: Was bedeutet das für die Qualität der Informationen?\nProf. Hoffmann: Das ist ein großes Problem. Fake News verbreiten sich schneller als echte Nachrichten. Wir müssen lernen, Informationen kritisch zu bewerten.\nDr. Fischer: Und was ist die Rolle der Journalisten?\nProf. Hoffmann: Journalisten sind wichtiger denn je. Sie müssen Fakten überprüfen und objektiv berichten. Qualitätsjournalismus ist unverzichtbar für eine funktionierende Demokratie.",
                    questions = listOf(
                        Question(id = 1, question = "Was dominieren jetzt die Kommunikation?", options = listOf("Zeitungen", "Soziale Medien", "Radio", "Fernsehen"), correctAnswer = "Soziale Medien", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was verbreitet sich schneller?", options = listOf("Echte Nachrichten", "Fake News", "Wissenschaftliche Artikel", "Bücher"), correctAnswer = "Fake News", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was ist unverzichtbar für die Demokratie?", options = null, correctAnswer = "Qualitätsjournalismus", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Bildung und Lernen",
                    description = "Education and learning discussion",
                    level = level,
                    orderIndex = 4,
                    script = "Dr. Bauer: Wie sollte sich das Bildungssystem an die digitale Welt anpassen?\nProf. Klein: Wir brauchen eine grundlegende Reform. Digitale Kompetenzen müssen genauso wichtig sein wie Lesen und Schreiben.\nDr. Bauer: Was ist mit der Rolle der Lehrer?\nProf. Klein: Lehrer werden zu Lernbegleitern. Sie müssen Schülern helfen, selbstständig zu lernen und digitale Werkzeuge zu nutzen.\nDr. Bauer: Und wie sieht es mit der Chancengleichheit aus?\nProf. Klein: Das ist ein kritisches Thema. Nicht alle Schüler haben Zugang zu digitalen Geräten. Wir müssen sicherstellen, dass niemand benachteiligt wird.",
                    questions = listOf(
                        Question(id = 1, question = "Was brauchen wir im Bildungssystem?", options = listOf("Nur mehr Bücher", "Eine grundlegende Reform", "Nur mehr Tests", "Nur mehr Hausaufgaben"), correctAnswer = "Eine grundlegende Reform", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was werden Lehrer zu?", options = listOf("Nur Wissensvermittler", "Lernbegleiter", "Nur Aufseher", "Nur Bewerter"), correctAnswer = "Lernbegleiter", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was ist ein kritisches Thema?", options = null, correctAnswer = "Chancengleichheit", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Kunst und Kultur",
                    description = "Art and culture discussion",
                    level = level,
                    orderIndex = 5,
                    script = "Dr. Meyer: Welche Rolle spielt Kunst in der modernen Gesellschaft?\nProf. Schulz: Kunst ist ein Spiegel der Gesellschaft und kann soziale Probleme aufzeigen. Sie regt zum Nachdenken an und kann Menschen verbinden.\nDr. Meyer: Wie hat sich die Kunst durch die Digitalisierung verändert?\nProf. Schulz: Digitale Kunst ist ein neues Medium, aber traditionelle Kunst bleibt wichtig. Die Kombination aus analog und digital eröffnet neue Möglichkeiten.\nDr. Meyer: Und was ist mit der Finanzierung der Kultur?\nProf. Schulz: Das ist ein schwieriges Thema. Kultur braucht Unterstützung, aber auch neue Finanzierungsmodelle. Crowdfunding und private Sponsoren werden immer wichtiger.",
                    questions = listOf(
                        Question(id = 1, question = "Was kann Kunst aufzeigen?", options = listOf("Nur Schönheit", "Soziale Probleme", "Nur Geschichte", "Nur Technologie"), correctAnswer = "Soziale Probleme", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was ist ein neues Medium?", options = listOf("Malerei", "Digitale Kunst", "Skulptur", "Fotografie"), correctAnswer = "Digitale Kunst", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was wird immer wichtiger?", options = null, correctAnswer = "Crowdfunding und private Sponsoren", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
            }
            
            "C1" -> {
                lessons.add(createHoerenLesson(
                    title = "Philosophie und Ethik",
                    description = "Philosophy and ethics discussion",
                    level = level,
                    orderIndex = 1,
                    script = "Prof. Dr. Schmidt: Was bedeutet für dich ein gutes Leben?\nDr. Weber: Das ist eine komplexe philosophische Frage. Ich denke, es geht um die Balance zwischen individueller Freiheit und sozialer Verantwortung.\nProf. Dr. Schmidt: Wie siehst du die Rolle der Ethik in der modernen Gesellschaft?\nDr. Weber: Ethik ist wichtiger denn je. In einer globalisierten Welt müssen wir universelle Werte finden, die verschiedene Kulturen verbinden.\nProf. Dr. Schmidt: Und was ist mit der Verantwortung gegenüber zukünftigen Generationen?\nDr. Weber: Das ist eine zentrale Frage der Nachhaltigkeit. Wir müssen heute Entscheidungen treffen, die auch morgen noch sinnvoll sind. Das erfordert eine langfristige Perspektive.",
                    questions = listOf(
                        Question(id = 1, question = "Was ist wichtig für ein gutes Leben?", options = listOf("Nur Geld", "Nur Erfolg", "Balance zwischen Freiheit und Verantwortung", "Nur Spaß"), correctAnswer = "Balance zwischen Freiheit und Verantwortung", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was müssen wir in einer globalisierten Welt finden?", options = listOf("Nur lokale Werte", "Universelle Werte", "Nur religiöse Werte", "Nur wirtschaftliche Werte"), correctAnswer = "Universelle Werte", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was erfordert Nachhaltigkeit?", options = null, correctAnswer = "langfristige Perspektive", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Literatur und Gesellschaft",
                    description = "Literature and society discussion",
                    level = level,
                    orderIndex = 2,
                    script = "Prof. Dr. Müller: Wie spiegelt Literatur die Gesellschaft wider?\nDr. Fischer: Literatur ist ein Spiegel der Zeit. Sie zeigt uns die Probleme und Hoffnungen einer Gesellschaft und kann soziale Veränderungen vorwegnehmen.\nProf. Dr. Müller: Welche Rolle spielt die deutsche Literatur heute?\nDr. Fischer: Die deutsche Literatur ist sehr vielfältig und international. Moderne Autoren beschäftigen sich mit Migration, Identität und globalen Herausforderungen.\nProf. Dr. Müller: Und was ist mit der Zukunft des Lesens?\nDr. Fischer: Das Lesen wird sich verändern, aber nicht verschwinden. Digitale Medien eröffnen neue Möglichkeiten, aber das Buch bleibt ein wichtiges Medium für tiefes Verstehen.",
                    questions = listOf(
                        Question(id = 1, question = "Was ist Literatur?", options = listOf("Nur Unterhaltung", "Ein Spiegel der Zeit", "Nur Geschichte", "Nur Fantasie"), correctAnswer = "Ein Spiegel der Zeit", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Womit beschäftigen sich moderne Autoren?", options = listOf("Nur Liebe", "Migration, Identität, globale Herausforderungen", "Nur Politik", "Nur Wirtschaft"), correctAnswer = "Migration, Identität, globale Herausforderungen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was bleibt ein wichtiges Medium?", options = null, correctAnswer = "das Buch", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Psychologie und Verhalten",
                    description = "Psychology and behavior discussion",
                    level = level,
                    orderIndex = 3,
                    script = "Prof. Dr. Wagner: Wie beeinflusst die digitale Welt unser Verhalten?\nDr. Hoffmann: Die digitale Welt verändert unsere Aufmerksamkeit und Kommunikation grundlegend. Wir sind ständig abgelenkt und haben weniger Geduld.\nProf. Dr. Wagner: Was bedeutet das für unsere Beziehungen?\nDr. Hoffmann: Das ist ambivalent. Einerseits können wir leichter in Kontakt bleiben, andererseits fehlt oft die Tiefe. Echte Beziehungen brauchen persönlichen Kontakt.\nProf. Dr. Wagner: Und wie können wir das ausbalancieren?\nDr. Hoffmann: Wir müssen bewusste Entscheidungen treffen. Digitale Medien sind Werkzeuge, aber wir sollten sie nicht unser Leben dominieren lassen. Achtsamkeit ist wichtig.",
                    questions = listOf(
                        Question(id = 1, question = "Was verändert die digitale Welt?", options = listOf("Nur unsere Kleidung", "Unsere Aufmerksamkeit und Kommunikation", "Nur unsere Arbeit", "Nur unsere Freizeit"), correctAnswer = "Unsere Aufmerksamkeit und Kommunikation", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was fehlt oft in digitalen Beziehungen?", options = listOf("Geschwindigkeit", "Tiefe", "Anonymität", "Flexibilität"), correctAnswer = "Tiefe", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was ist wichtig für die Balance?", options = null, correctAnswer = "Achtsamkeit", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Soziologie und Gesellschaft",
                    description = "Sociology and society discussion",
                    level = level,
                    orderIndex = 4,
                    script = "Prof. Dr. Bauer: Wie verändert sich die Gesellschaft durch die Digitalisierung?\nDr. Klein: Die Digitalisierung führt zu einer Beschleunigung der Gesellschaft und verändert soziale Strukturen. Neue Formen der Gemeinschaft entstehen.\nProf. Dr. Bauer: Was bedeutet das für die soziale Ungleichheit?\nDr. Klein: Das ist ein kritisches Thema. Die digitale Kluft kann soziale Ungleichheit verstärken, aber digitale Bildung kann auch Chancen eröffnen.\nProf. Dr. Bauer: Und wie sieht es mit der Demokratie aus?\nDr. Klein: Die Digitalisierung bietet neue Möglichkeiten für politische Partizipation, aber auch Risiken wie Manipulation und Überwachung. Wir müssen die Balance finden.",
                    questions = listOf(
                        Question(id = 1, question = "Was führt zu einer Beschleunigung?", options = listOf("Nur Technologie", "Die Digitalisierung", "Nur Wirtschaft", "Nur Politik"), correctAnswer = "Die Digitalisierung", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was kann die digitale Kluft verstärken?", options = listOf("Gleichheit", "Soziale Ungleichheit", "Demokratie", "Bildung"), correctAnswer = "Soziale Ungleichheit", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was bietet die Digitalisierung für die Demokratie?", options = null, correctAnswer = "neue Möglichkeiten für politische Partizipation", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Linguistik und Sprache",
                    description = "Linguistics and language discussion",
                    level = level,
                    orderIndex = 5,
                    script = "Prof. Dr. Meyer: Wie verändert sich die deutsche Sprache durch die Digitalisierung?\nDr. Schulz: Die Sprache wird dynamischer und internationaler. Anglizismen nehmen zu, aber es entstehen auch neue deutsche Begriffe.\nProf. Dr. Meyer: Was bedeutet das für die Sprachpflege?\nDr. Schulz: Wir müssen die Balance zwischen sprachlicher Innovation und Bewahrung der Tradition finden. Die deutsche Sprache ist reich und vielfältig.\nProf. Dr. Meyer: Und wie sieht es mit der Mehrsprachigkeit aus?\nDr. Schulz: Mehrsprachigkeit wird immer wichtiger. In einer globalisierten Welt ist es ein Vorteil, mehrere Sprachen zu sprechen. Es eröffnet neue Perspektiven.",
                    questions = listOf(
                        Question(id = 1, question = "Was nimmt in der Sprache zu?", options = listOf("Nur deutsche Wörter", "Anglizismen", "Nur alte Begriffe", "Nur Fachsprache"), correctAnswer = "Anglizismen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was müssen wir zwischen Innovation und Tradition finden?", options = listOf("Nur Innovation", "Nur Tradition", "Balance", "Nur Kompromiss"), correctAnswer = "Balance", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was wird immer wichtiger?", options = null, correctAnswer = "Mehrsprachigkeit", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
            }
            
            "C2" -> {
                lessons.add(createHoerenLesson(
                    title = "Metaphysik und Ontologie",
                    description = "Metaphysics and ontology discussion",
                    level = level,
                    orderIndex = 1,
                    script = "Prof. Dr. Schmidt: Was ist die grundlegende Struktur der Wirklichkeit?\nProf. Dr. Weber: Das ist eine zentrale Frage der Metaphysik. Wir müssen zwischen physischer und mentaler Realität unterscheiden.\nProf. Dr. Schmidt: Wie verstehen wir das Verhältnis zwischen Geist und Materie?\nProf. Dr. Weber: Das ist das klassische Leib-Seele-Problem. Verschiedene philosophische Positionen bieten unterschiedliche Lösungen an.\nProf. Dr. Schmidt: Und was ist mit der Frage nach der Existenz Gottes?\nProf. Dr. Weber: Das ist ein metaphysisches Problem, das verschiedene Antworten zulässt. Es geht um die grundlegende Struktur der Wirklichkeit.",
                    questions = listOf(
                        Question(id = 1, question = "Was ist eine zentrale Frage der Metaphysik?", options = listOf("Nur Politik", "Die grundlegende Struktur der Wirklichkeit", "Nur Wissenschaft", "Nur Kunst"), correctAnswer = "Die grundlegende Struktur der Wirklichkeit", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was ist das klassische Problem?", options = listOf("Nur Wirtschaft", "Das Leib-Seele-Problem", "Nur Gesellschaft", "Nur Technologie"), correctAnswer = "Das Leib-Seele-Problem", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Worum geht es bei der Gottesfrage?", options = null, correctAnswer = "grundlegende Struktur der Wirklichkeit", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Ästhetik und Kunstphilosophie",
                    description = "Aesthetics and philosophy of art discussion",
                    level = level,
                    orderIndex = 2,
                    script = "Prof. Dr. Müller: Was ist das Wesen der Kunst?\nProf. Dr. Fischer: Das ist eine komplexe ästhetische Frage. Kunst kann verschiedene Funktionen haben: Ausdruck, Darstellung, Kommunikation.\nProf. Dr. Müller: Wie verstehen wir Schönheit in der modernen Kunst?\nProf. Dr. Fischer: Das Konzept der Schönheit hat sich gewandelt. Moderne Kunst hinterfragt traditionelle Schönheitsideale und eröffnet neue Perspektiven.\nProf. Dr. Müller: Und was ist die Rolle der Kunst in der Gesellschaft?\nProf. Dr. Fischer: Kunst kann die Gesellschaft spiegeln, kritisieren und verändern. Sie ist ein wichtiger Teil der kulturellen Identität.",
                    questions = listOf(
                        Question(id = 1, question = "Was kann Kunst haben?", options = listOf("Nur eine Funktion", "Verschiedene Funktionen", "Keine Funktion", "Nur Unterhaltung"), correctAnswer = "Verschiedene Funktionen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was hinterfragt moderne Kunst?", options = listOf("Nur Politik", "Traditionelle Schönheitsideale", "Nur Wirtschaft", "Nur Technologie"), correctAnswer = "Traditionelle Schönheitsideale", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was kann Kunst in der Gesellschaft tun?", options = null, correctAnswer = "spiegeln, kritisieren und verändern", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Wissenschaftstheorie",
                    description = "Philosophy of science discussion",
                    level = level,
                    orderIndex = 3,
                    script = "Prof. Dr. Wagner: Was ist wissenschaftliche Erkenntnis?\nProf. Dr. Hoffmann: Das ist eine zentrale Frage der Wissenschaftstheorie. Wissenschaftliche Erkenntnis basiert auf empirischen Methoden und theoretischen Modellen.\nProf. Dr. Wagner: Wie verstehen wir die Objektivität der Wissenschaft?\nProf. Dr. Hoffmann: Objektivität ist ein Ideal, aber Wissenschaft ist auch von sozialen und kulturellen Faktoren beeinflusst. Wir müssen das reflektieren.\nProf. Dr. Wagner: Und was ist die Rolle der Werte in der Forschung?\nProf. Dr. Hoffmann: Werte spielen eine wichtige Rolle in der Wissenschaft. Sie beeinflussen die Wahl der Forschungsfragen und die Interpretation der Ergebnisse.",
                    questions = listOf(
                        Question(id = 1, question = "Worauf basiert wissenschaftliche Erkenntnis?", options = listOf("Nur auf Glauben", "Empirische Methoden und theoretische Modelle", "Nur auf Intuition", "Nur auf Tradition"), correctAnswer = "Empirische Methoden und theoretische Modelle", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was ist ein Ideal der Wissenschaft?", options = listOf("Subjektivität", "Objektivität", "Parteilichkeit", "Willkür"), correctAnswer = "Objektivität", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was beeinflusst die Wahl der Forschungsfragen?", options = null, correctAnswer = "Werte", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Ethik der Technologie",
                    description = "Ethics of technology discussion",
                    level = level,
                    orderIndex = 4,
                    script = "Prof. Dr. Bauer: Welche ethischen Herausforderungen stellt die künstliche Intelligenz?\nProf. Dr. Klein: KI wirft grundlegende Fragen auf: Verantwortung, Autonomie, Gerechtigkeit. Wer ist verantwortlich für die Entscheidungen einer KI?\nProf. Dr. Bauer: Wie verstehen wir die Beziehung zwischen Mensch und Maschine?\nProf. Dr. Klein: Das ist eine philosophische Frage. Wir müssen die Grenzen zwischen menschlicher und maschineller Intelligenz reflektieren.\nProf. Dr. Bauer: Und was ist mit der Zukunft der Arbeit?\nProf. Dr. Klein: Die Automatisierung verändert die Arbeitswelt grundlegend. Wir müssen neue Formen der Arbeit und des Zusammenlebens entwickeln.",
                    questions = listOf(
                        Question(id = 1, question = "Was wirft KI auf?", options = listOf("Nur technische Fragen", "Grundlegende ethische Fragen", "Nur wirtschaftliche Fragen", "Nur politische Fragen"), correctAnswer = "Grundlegende ethische Fragen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was müssen wir reflektieren?", options = listOf("Nur Technologie", "Grenzen zwischen menschlicher und maschineller Intelligenz", "Nur Wirtschaft", "Nur Politik"), correctAnswer = "Grenzen zwischen menschlicher und maschineller Intelligenz", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was verändert die Automatisierung?", options = null, correctAnswer = "die Arbeitswelt grundlegend", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
                
                lessons.add(createHoerenLesson(
                    title = "Philosophie der Moderne",
                    description = "Modern philosophy discussion",
                    level = level,
                    orderIndex = 5,
                    script = "Prof. Dr. Meyer: Was sind die Herausforderungen der Moderne?\nProf. Dr. Schulz: Die Moderne ist geprägt von Individualisierung, Rationalisierung und Globalisierung. Diese Entwicklungen schaffen neue Freiheiten, aber auch neue Probleme.\nProf. Dr. Meyer: Wie verstehen wir Identität in der modernen Welt?\nProf. Dr. Schulz: Identität ist heute komplexer und fluider. Wir müssen verschiedene Identitäten integrieren und neue Formen der Gemeinschaft finden.\nProf. Dr. Meyer: Und was ist die Rolle der Philosophie heute?\nProf. Dr. Schulz: Philosophie ist wichtiger denn je. Sie hilft uns, die komplexen Herausforderungen der Moderne zu verstehen und zu bewältigen.",
                    questions = listOf(
                        Question(id = 1, question = "Wovon ist die Moderne geprägt?", options = listOf("Nur Tradition", "Individualisierung, Rationalisierung, Globalisierung", "Nur Religion", "Nur Technologie"), correctAnswer = "Individualisierung, Rationalisierung, Globalisierung", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was ist Identität heute?", options = listOf("Einfach", "Komplexer und fluider", "Unveränderlich", "Nur biologisch"), correctAnswer = "Komplexer und fluider", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was hilft uns die Philosophie zu verstehen?", options = null, correctAnswer = "die komplexen Herausforderungen der Moderne", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    )
                ))
            }
        }
        
        return lessons
    }
    
    private fun generateSchreibenLessons(level: String): List<Lesson> {
        val lessons = mutableListOf<Lesson>()
        
        when (level) {
            "A1" -> {
                lessons.add(createSchreibenLesson(
                    title = "Brief an einen Freund",
                    description = "Write a simple letter to a friend",
                    level = level,
                    orderIndex = 1,
                    prompt = "Schreibe einen kurzen Brief an deinen Freund/deine Freundin. Erzähle über deinen Tag und was du gemacht hast. (50-80 Wörter)",
                    minWords = 50,
                    maxWords = 80,
                    tips = listOf(
                        "Beginne mit einer Begrüßung",
                        "Erzähle über deine Aktivitäten",
                        "Beende mit einer Verabschiedung"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Mein Haustier",
                    description = "Write about your pet",
                    level = level,
                    orderIndex = 2,
                    prompt = "Beschreibe dein Haustier oder ein Tier, das du magst. Wie sieht es aus? Was kann es? Was macht es gerne? (60-90 Wörter)",
                    minWords = 60,
                    maxWords = 90,
                    tips = listOf(
                        "Beschreibe das Aussehen des Tieres",
                        "Erzähle, was es kann und mag",
                        "Erkläre, warum du es magst"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Meine Schule",
                    description = "Write about your school",
                    level = level,
                    orderIndex = 3,
                    prompt = "Beschreibe deine Schule. Wie sieht sie aus? Was lernst du dort? Wer sind deine Lehrer? (50-80 Wörter)",
                    minWords = 50,
                    maxWords = 80,
                    tips = listOf(
                        "Beschreibe das Gebäude und die Räume",
                        "Nenne deine Lieblingsfächer",
                        "Erzähle über deine Lehrer und Freunde"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Meine Lieblingsfarbe",
                    description = "Write about your favorite color",
                    level = level,
                    orderIndex = 4,
                    prompt = "Schreibe über deine Lieblingsfarbe. Warum magst du sie? Wo siehst du sie? Was verbindest du mit dieser Farbe? (70-100 Wörter)",
                    minWords = 70,
                    maxWords = 100,
                    tips = listOf(
                        "Beginne mit dem Aufstehen",
                        "Beschreibe die verschiedenen Tageszeiten",
                        "Beende mit dem Schlafengehen"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Meine Wohnung",
                    description = "Write about your home",
                    level = level,
                    orderIndex = 5,
                    prompt = "Beschreibe deine Wohnung oder dein Haus. Wie viele Zimmer gibt es? Was ist in jedem Zimmer? (60-90 Wörter)",
                    minWords = 60,
                    maxWords = 90,
                    tips = listOf(
                        "Beginne mit der Art der Wohnung",
                        "Zähle die Zimmer auf",
                        "Beschreibe die wichtigsten Möbel"
                    )
                ))

                // Goethe-Zertifikat A1 Schreiben - Additional Lessons (6-20)
                lessons.add(createSchreibenLesson(
                    title = "Mein Tagesablauf",
                    description = "Write about your daily routine - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 6,
                    prompt = "Beschreibe deinen normalen Tagesablauf. Wann stehst du auf? Was machst du morgens, mittags und abends? Was sind deine Lieblingsaktivitäten? (70-100 Wörter)",
                    minWords = 70,
                    maxWords = 100,
                    tips = listOf(
                        "Beginne mit dem Aufstehen",
                        "Beschreibe Mahlzeiten und Arbeit",
                        "Erwähne Hobbys und Freizeit",
                        "Beende mit dem Schlafengehen"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Eine Einladung",
                    description = "Write an invitation - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 7,
                    prompt = "Du möchtest Freunde zu einer Party einladen. Schreibe eine Einladung. Wann ist die Party? Wo findet sie statt? Was gibt es zu essen und zu trinken? Was sollen die Gäste mitbringen? (60-90 Wörter)",
                    minWords = 60,
                    maxWords = 90,
                    tips = listOf(
                        "Schreibe eine nette Einleitung",
                        "Gib alle wichtigen Informationen",
                        "Erkläre, was du planst",
                        "Bitte um Bestätigung"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Mein Beruf",
                    description = "Write about your profession - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 8,
                    prompt = "Beschreibe deinen Beruf oder den Beruf, den du gerne haben möchtest. Was machst du jeden Tag? Warum gefällt dir der Beruf? Was sind die Vor- und Nachteile? (80-110 Wörter)",
                    minWords = 80,
                    maxWords = 110,
                    tips = listOf(
                        "Beginne mit der Berufsbezeichnung",
                        "Beschreibe tägliche Aufgaben",
                        "Erkläre, warum du ihn magst",
                        "Nenne Vor- und Nachteile"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Gesunde Ernährung",
                    description = "Write about healthy eating - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 9,
                    prompt = "Schreibe über gesunde Ernährung. Was isst du jeden Tag? Warum ist gesunde Ernährung wichtig? Welche Lebensmittel magst du besonders? Was trinkst du? (70-100 Wörter)",
                    minWords = 70,
                    maxWords = 100,
                    tips = listOf(
                        "Erkläre die Bedeutung der Gesundheit",
                        "Liste wichtige Lebensmittel auf",
                        "Beschreibe deine Essgewohnheiten",
                        "Gib Tipps für andere"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Meine Freizeit",
                    description = "Write about your free time - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 10,
                    prompt = "Beschreibe, was du in deiner Freizeit machst. Welche Hobbys hast du? Treibst du Sport? Gehst du aus? Was machst du am Wochenende? (60-90 Wörter)",
                    minWords = 60,
                    maxWords = 90,
                    tips = listOf(
                        "Beginne mit deinen Hobbys",
                        "Beschreibe sportliche Aktivitäten",
                        "Erzähle von sozialen Aktivitäten",
                        "Erkläre, warum du das magst"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Eine Entschuldigung",
                    description = "Write an apology - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 11,
                    prompt = "Du hast einen Termin vergessen oder bist zu spät gekommen. Schreibe eine Entschuldigung. Erkläre, was passiert ist. Sage, dass es dir leid tut. Biete eine Lösung an. (50-80 Wörter)",
                    minWords = 50,
                    maxWords = 80,
                    tips = listOf(
                        "Beginne mit einer Entschuldigung",
                        "Erkläre die Situation kurz",
                        "Zeige Verständnis",
                        "Biete Wiedergutmachung an"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Mein Haustier",
                    description = "Write about your pet - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 12,
                    prompt = "Beschreibe dein Haustier oder ein Tier, das du gerne hättest. Wie sieht es aus? Wie alt ist es? Was frisst es? Was macht es gerne? Wie kümmertst du dich darum? (70-100 Wörter)",
                    minWords = 70,
                    maxWords = 100,
                    tips = listOf(
                        "Beschreibe das Aussehen",
                        "Erzähle vom Charakter",
                        "Beschreibe die Pflege",
                        "Erkläre deine Beziehung zum Tier"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Ein Ausflug",
                    description = "Write about an excursion - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 13,
                    prompt = "Erzähle von einem schönen Ausflug, den du gemacht hast. Wohin bist du gefahren? Mit wem warst du zusammen? Was hast du gesehen und gemacht? Wie war das Wetter? (80-110 Wörter)",
                    minWords = 80,
                    maxWords = 110,
                    tips = listOf(
                        "Beginne mit dem Reiseziel",
                        "Beschreibe die Begleitung",
                        "Erzähle von den Aktivitäten",
                        "Beschreibe deine Eindrücke"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Meine Familie",
                    description = "Write about your family - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 14,
                    prompt = "Beschreibe deine Familie. Wie viele Personen seid ihr? Wie heißen deine Familienmitglieder? Was machen sie beruflich? Was sind ihre Hobbys? Wie verbringt ihr Zeit zusammen? (70-100 Wörter)",
                    minWords = 70,
                    maxWords = 100,
                    tips = listOf(
                        "Stelle jedes Familienmitglied vor",
                        "Beschreibe ihre Berufe",
                        "Erzähle von gemeinsamen Aktivitäten",
                        "Beschreibe die Beziehungen"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Sport und Gesundheit",
                    description = "Write about sports and health - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 15,
                    prompt = "Schreibe über Sport und Gesundheit. Treibst du Sport? Welchen Sport machst du? Wie oft trainierst du? Warum ist Sport wichtig? Was sind die Vorteile? (60-90 Wörter)",
                    minWords = 60,
                    maxWords = 90,
                    tips = listOf(
                        "Erkläre die Bedeutung von Sport",
                        "Beschreibe deine sportlichen Aktivitäten",
                        "Nenne gesundheitliche Vorteile",
                        "Gib Tipps für andere"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Eine Verabredung",
                    description = "Write about making plans - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 16,
                    prompt = "Du möchtest dich mit einem Freund/einer Freundin treffen. Schreibe eine Nachricht. Wann möchtest du dich treffen? Wo? Was möchtest du machen? Frage nach seiner/ihrer Meinung. (50-70 Wörter)",
                    minWords = 50,
                    maxWords = 70,
                    tips = listOf(
                        "Sei freundlich und direkt",
                        "Gib konkrete Vorschläge",
                        "Frage nach der Meinung des anderen",
                        "Sei offen für Alternativen"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Meine Stadt",
                    description = "Write about your city - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 17,
                    prompt = "Beschreibe deine Stadt oder dein Dorf. Wo liegt es? Wie viele Einwohner hat es? Was gibt es dort? Welche Sehenswürdigkeiten gibt es? Warum gefällt es dir dort? (80-110 Wörter)",
                    minWords = 80,
                    maxWords = 110,
                    tips = listOf(
                        "Beginne mit der Lage",
                        "Beschreibe die Größe und Einwohner",
                        "Erwähne wichtige Orte",
                        "Erkläre, warum du es magst"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Einkaufen",
                    description = "Write about shopping - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 18,
                    prompt = "Erzähle vom Einkaufen. Was brauchst du normalerweise? In welchen Geschäften kaufst du ein? Was sind deine Lieblingsläden? Wie bezahlst du normalerweise? (60-90 Wörter)",
                    minWords = 60,
                    maxWords = 90,
                    tips = listOf(
                        "Liste wichtige Dinge auf",
                        "Beschreibe verschiedene Geschäfte",
                        "Erkläre deine Vorlieben",
                        "Beschreibe Zahlungsmethoden"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Ein Fest",
                    description = "Write about a celebration - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 19,
                    prompt = "Beschreibe ein Fest, das du gefeiert hast oder feiern möchtest. Was für ein Fest ist es? Wann findet es statt? Wer kommt? Was esst und trinkt ihr? Was macht ihr? (70-100 Wörter)",
                    minWords = 70,
                    maxWords = 100,
                    tips = listOf(
                        "Nenne die Art des Festes",
                        "Beschreibe die Vorbereitungen",
                        "Erzähle von den Gästen",
                        "Beschreibe das Essen und die Aktivitäten"
                    ),
                    source = "Goethe"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Die Jahreszeiten",
                    description = "Write about the seasons - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 20,
                    prompt = "Beschreibe die vier Jahreszeiten. Was gefällt dir an jeder Jahreszeit? Was machst du im Frühling, Sommer, Herbst und Winter? Welches Wetter magst du am liebsten? (80-110 Wörter)",
                    minWords = 80,
                    maxWords = 110,
                    tips = listOf(
                        "Beschreibe jede Jahreszeit",
                        "Erkläre deine Vorlieben",
                        "Erzähle von saisonalen Aktivitäten",
                        "Vergleiche die Jahreszeiten"
                    ),
                    source = "Goethe"
                ))

                // TELC Deutsch A1 Schreiben - Lessons (27-29)
                lessons.add(createSchreibenLesson(
                    title = "Formulare ausfüllen",
                    description = "Filling out forms - TELC Schreiben Teil 1",
                    level = level,
                    orderIndex = 27,
                    prompt = "Sie müssen sich bei einer Bibliothek anmelden. Füllen Sie das Formular aus. Schreiben Sie Ihren Namen, Ihre Adresse, Ihre Telefonnummer und Ihre E-Mail-Adresse. (30-50 Wörter)",
                    minWords = 30,
                    maxWords = 50,
                    tips = listOf(
                        "Schreiben Sie alle geforderten Informationen",
                        "Benutzen Sie vollständige Sätze",
                        "Überprüfen Sie Ihre Rechtschreibung",
                        "Schreiben Sie leserlich"
                    ),
                    source = "TELC"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Kurze Mitteilung",
                    description = "Short message - TELC Schreiben Teil 2",
                    level = level,
                    orderIndex = 28,
                    prompt = "Sie können morgen nicht zum Unterricht kommen. Schreiben Sie eine kurze Mitteilung an Ihren Lehrer. Erklären Sie warum und wann Sie wiederkommen. (40-60 Wörter)",
                    minWords = 40,
                    maxWords = 60,
                    tips = listOf(
                        "Erklären Sie den Grund kurz",
                        "Sagen Sie, wann Sie wiederkommen",
                        "Seien Sie höflich",
                        "Schreiben Sie formell"
                    ),
                    source = "TELC"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Persönliche Nachricht",
                    description = "Personal message - TELC Schreiben Teil 3",
                    level = level,
                    orderIndex = 29,
                    prompt = "Sie haben einen Brief von einem Freund bekommen und möchten antworten. Schreiben Sie über Ihr Leben, Ihre Hobbys und laden Sie ihn zu sich ein. (60-80 Wörter)",
                    minWords = 60,
                    maxWords = 80,
                    tips = listOf(
                        "Begrüßen Sie freundlich",
                        "Erzählen Sie von sich",
                        "Stellen Sie Fragen",
                        "Verabschieden Sie sich nett"
                    ),
                    source = "TELC"
                ))

                // ÖSD Zertifikat A1 Schreiben - Lessons (39-41)
                lessons.add(createSchreibenLesson(
                    title = "Amtliche Korrespondenz",
                    description = "Official correspondence - ÖSD Schreiben Teil 1",
                    level = level,
                    orderIndex = 39,
                    prompt = "Sie müssen sich bei einem Amt anmelden. Schreiben Sie einen Brief mit Ihren persönlichen Daten, dem Grund für die Anmeldung und Ihren Kontaktdaten. Verwenden Sie eine formelle Sprache. (50-70 Wörter)",
                    minWords = 50,
                    maxWords = 70,
                    tips = listOf(
                        "Verwenden Sie formelle Anrede",
                        "Geben Sie alle geforderten Informationen an",
                        "Schreiben Sie höflich und korrekt",
                        "Vergessen Sie nicht Datum und Unterschrift"
                    ),
                    source = "ÖSD"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Geschäftliche Mitteilung",
                    description = "Business communication - ÖSD Schreiben Teil 2",
                    level = level,
                    orderIndex = 40,
                    prompt = "Sie arbeiten in einem Geschäft und müssen einen Kunden informieren, dass ein bestelltes Produkt nicht verfügbar ist. Bieten Sie Alternativen an und entschuldigen Sie sich. (60-80 Wörter)",
                    minWords = 60,
                    maxWords = 80,
                    tips = listOf(
                        "Seien Sie höflich und professionell",
                        "Erklären Sie das Problem",
                        "Bieten Sie Lösungen an",
                        "Bedanken Sie sich für das Verständnis"
                    ),
                    source = "ÖSD"
                ))

                lessons.add(createSchreibenLesson(
                    title = "Persönliche Einladung",
                    description = "Personal invitation - ÖSD Schreiben Teil 3",
                    level = level,
                    orderIndex = 41,
                    prompt = "Sie möchten Freunde zu einem Fest einladen. Schreiben Sie eine Einladung mit Datum, Ort, Zeit und was mitgebracht werden soll. Machen Sie die Einladung persönlich und herzlich. (70-90 Wörter)",
                    minWords = 70,
                    maxWords = 90,
                    tips = listOf(
                        "Schreiben Sie herzlich und einladend",
                        "Geben Sie alle wichtigen Informationen",
                        "Erklären Sie, warum es schön wird",
                        "Bitten Sie um Rückmeldung"
                    ),
                    source = "ÖSD"
                ))
            }

            "A2" -> {
                lessons.add(createSchreibenLesson(
                    title = "E-Mail an eine Kollegin",
                    description = "Write a professional email",
                    level = level,
                    orderIndex = 1,
                    prompt = "Du bist krank und kannst nicht zur Arbeit kommen. Schreibe eine E-Mail an deine Kollegin und erkläre die Situation. Bitte sie, wichtige Nachrichten weiterzuleiten. (80-120 Wörter)",
                    minWords = 80,
                    maxWords = 120,
                    tips = listOf(
                        "Formelle Anrede verwenden",
                        "Grund für Abwesenheit erklären",
                        "Höflich um Hilfe bitten"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Reisebericht",
                    description = "Write a travel report",
                    level = level,
                    orderIndex = 2,
                    prompt = "Schreibe einen Reisebericht über deine letzte Reise. Wohin bist du gefahren? Was hast du gemacht? Was war besonders schön? (100-150 Wörter)",
                    minWords = 100,
                    maxWords = 150,
                    tips = listOf(
                        "Beginne mit dem Reiseziel",
                        "Beschreibe die Aktivitäten",
                        "Erzähle von besonderen Erlebnissen"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Filmkritik",
                    description = "Write a movie review",
                    level = level,
                    orderIndex = 3,
                    prompt = "Schreibe eine kurze Filmkritik über einen Film, den du kürzlich gesehen hast. Worum geht es? Was hat dir gefallen? Was nicht? (90-130 Wörter)",
                    minWords = 90,
                    maxWords = 130,
                    tips = listOf(
                        "Nenne den Filmtitel",
                        "Beschreibe kurz die Handlung",
                        "Gib deine Meinung ab"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Beschwerdebrief",
                    description = "Write a complaint letter",
                    level = level,
                    orderIndex = 4,
                    prompt = "Du hast ein defektes Produkt gekauft. Schreibe einen Beschwerdebrief an den Kundenservice. Beschreibe das Problem und verlange eine Lösung. (100-140 Wörter)",
                    minWords = 100,
                    maxWords = 140,
                    tips = listOf(
                        "Höflich aber bestimmt bleiben",
                        "Das Problem genau beschreiben",
                        "Eine konkrete Lösung fordern"
                    )
                ))

                // Goethe A2 - Extended writing content
                lessons.add(createSchreibenLesson(
                    title = "Eine E-Mail schreiben",
                    description = "Write an email about travel plans (Goethe-Zertifikat A2)",
                    level = level,
                    orderIndex = 5,
                    prompt = "Du möchtest deinen Freund zu einem Wochenendausflug einladen. Schreibe eine E-Mail mit folgenden Informationen: Datum und Ort des Ausflugs, was ihr machen werdet, was mitzubringen ist, und bitte um Bestätigung. (80-120 Wörter)",
                    minWords = 80,
                    maxWords = 120,
                    tips = listOf(
                        "Verwende eine angemessene Anrede",
                        "Gib alle wichtigen Details an",
                        "Sei freundlich und einladend",
                        "Schließe mit einer Verabschiedung"
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Writing notes and messages
                lessons.add(createSchreibenLesson(
                    title = "Eine Nachricht hinterlassen",
                    description = "Leave a message for someone (TELC Deutsch A2)",
                    level = level,
                    orderIndex = 6,
                    prompt = "Du kommst zu spät zu einem Treffen und musst eine Nachricht für deine Freunde hinterlassen. Erkläre warum du zu spät kommst, wann du ungefähr ankommst, und entschuldige dich. (60-90 Wörter)",
                    minWords = 60,
                    maxWords = 90,
                    tips = listOf(
                        "Sei höflich und entschuldigend",
                        "Gib eine kurze Erklärung",
                        "Sage wann du ankommst",
                        "Biete an, später zu erklären"
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Writing descriptions
                lessons.add(createSchreibenLesson(
                    title = "Eine Person beschreiben",
                    description = "Describe a person (ÖSD Zertifikat A2)",
                    level = level,
                    orderIndex = 7,
                    prompt = "Beschreibe einen Familienangehörigen oder einen guten Freund. Wie sieht er/sie aus? Welche Persönlichkeit hat er/sie? Was macht er/sie gerne? Warum magst du ihn/sie? (100-140 Wörter)",
                    minWords = 100,
                    maxWords = 140,
                    tips = listOf(
                        "Beginne mit dem Aussehen",
                        "Beschreibe die Persönlichkeit",
                        "Erzähle von Hobbys und Interessen",
                        "Erkläre die Beziehung"
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Writing about past events
                lessons.add(createSchreibenLesson(
                    title = "Ein Erlebnis erzählen",
                    description = "Write about a past experience",
                    level = level,
                    orderIndex = 8,
                    prompt = "Erzähle von einem besonderen Erlebnis aus deinem Leben. Was ist passiert? Wann und wo war es? Wie hast du dich gefühlt? Was hast du gelernt? (120-160 Wörter)",
                    minWords = 120,
                    maxWords = 160,
                    tips = listOf(
                        "Setze eine chronologische Reihenfolge",
                        "Beschreibe deine Gefühle",
                        "Erkläre die Bedeutung",
                        "Schließe mit einer Erkenntnis"
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Writing forms and applications
                lessons.add(createSchreibenLesson(
                    title = "Ein Formular ausfüllen",
                    description = "Fill out an application form",
                    level = level,
                    orderIndex = 9,
                    prompt = "Du bewirbst dich um einen Ferienjob in einem Café. Fülle das Bewerbungsformular aus. Gib deine persönlichen Daten, deine Verfügbarkeit und warum du den Job möchtest an. (80-110 Wörter)",
                    minWords = 80,
                    maxWords = 110,
                    tips = listOf(
                        "Schreibe vollständige Angaben",
                        "Sei ehrlich und präzise",
                        "Erkläre deine Motivation",
                        "Überprüfe auf Rechtschreibung"
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Writing about health
                lessons.add(createSchreibenLesson(
                    title = "Gesundheitsberatung",
                    description = "Write about health advice",
                    level = level,
                    orderIndex = 10,
                    prompt = "Du gibst einem Freund Gesundheitsratschläge. Erkläre warum gesunde Ernährung wichtig ist, welche Lebensmittel er essen sollte, und welche Sportarten du empfiehlst. (100-130 Wörter)",
                    minWords = 100,
                    maxWords = 130,
                    tips = listOf(
                        "Erkläre die Vorteile",
                        "Gib konkrete Beispiele",
                        "Sei ermutigend",
                        "Biete Hilfe an"
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Writing emails to authorities
                lessons.add(createSchreibenLesson(
                    title = "Eine offizielle E-Mail",
                    description = "Write an official email",
                    level = level,
                    orderIndex = 11,
                    prompt = "Du möchtest deinen Führerschein verlängern lassen. Schreibe eine E-Mail an das Straßenverkehrsamt. Gib deine Personalien an, erkläre was du brauchst, und frage nach den nächsten Schritten. (90-120 Wörter)",
                    minWords = 90,
                    maxWords = 120,
                    tips = listOf(
                        "Verwende formelle Sprache",
                        "Gib alle notwendigen Informationen",
                        "Sei höflich und klar",
                        "Schließe mit einer Bitte um Rückmeldung"
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Writing invitations
                lessons.add(createSchreibenLesson(
                    title = "Eine Einladung schreiben",
                    description = "Write an invitation",
                    level = level,
                    orderIndex = 12,
                    prompt = "Du organisierst eine Geburtstagsfeier für einen Freund. Schreibe eine Einladung mit Datum, Uhrzeit, Ort, was mitzubringen ist, und warum es besonders wird. (80-110 Wörter)",
                    minWords = 80,
                    maxWords = 110,
                    tips = listOf(
                        "Sei herzlich und einladend",
                        "Gib alle wichtigen Details",
                        "Erkläre was erwartet wird",
                        "Bitte um Rückmeldung"
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Writing about daily routines
                lessons.add(createSchreibenLesson(
                    title = "Mein Tagesablauf",
                    description = "Describe your daily routine",
                    level = level,
                    orderIndex = 13,
                    prompt = "Beschreibe deinen typischen Tagesablauf von morgens bis abends. Erkläre wann du aufstehst, was du frühstückst, wie du zur Arbeit/Schule kommst, was du dort machst, und wie dein Abend aussieht. (120-150 Wörter)",
                    minWords = 120,
                    maxWords = 150,
                    tips = listOf(
                        "Folge einer zeitlichen Reihenfolge",
                        "Einschließe Mahlzeiten und Transport",
                        "Beschreibe Arbeit/Schule",
                        "Erwähne Freizeitaktivitäten"
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Writing about future plans
                lessons.add(createSchreibenLesson(
                    title = "Zukunftspläne",
                    description = "Write about future plans",
                    level = level,
                    orderIndex = 14,
                    prompt = "Schreibe über deine Pläne für die nächsten Jahre. Was möchtest du beruflich erreichen? Wohin möchtest du reisen? Welche Hobbys möchtest du ausüben? Wie stellst du dir dein Leben in 5 Jahren vor? (130-170 Wörter)",
                    minWords = 130,
                    maxWords = 170,
                    tips = listOf(
                        "Teile in verschiedene Lebensbereiche",
                        "Sei realistisch aber optimistisch",
                        "Erkläre warum diese Pläne wichtig sind",
                        "Beschreibe konkrete Schritte"
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Writing complaints
                lessons.add(createSchreibenLesson(
                    title = "Reklamation schreiben",
                    description = "Write a complaint",
                    level = level,
                    orderIndex = 15,
                    prompt = "Du hast in einem Restaurant schlechten Service erlebt. Schreibe eine Beschwerde-E-Mail an den Restaurantbesitzer. Beschreibe was passiert ist, wie du dich gefühlt hast, und was du als Lösung erwartest. (100-130 Wörter)",
                    minWords = 100,
                    maxWords = 130,
                    tips = listOf(
                        "Bleibe sachlich aber bestimmt",
                        "Beschreibe das Problem detailliert",
                        "Erkläre die Auswirkungen",
                        "Fordere eine angemessene Lösung"
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Writing about hobbies
                lessons.add(createSchreibenLesson(
                    title = "Meine Hobbys",
                    description = "Write about your hobbies",
                    level = level,
                    orderIndex = 16,
                    prompt = "Beschreibe deine Hobbys und warum du sie magst. Wie hast du sie entdeckt? Was brauchst du dafür? Wie viel Zeit verbringst du damit? Was hast du schon erreicht? (110-140 Wörter)",
                    minWords = 110,
                    maxWords = 140,
                    tips = listOf(
                        "Nenne mehrere Hobbys",
                        "Erkläre die Entstehung",
                        "Beschreibe Ausrüstung und Zeitaufwand",
                        "Erzähle von Erfolgen oder Fortschritten"
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Writing thank-you notes
                lessons.add(createSchreibenLesson(
                    title = "Danksagung schreiben",
                    description = "Write a thank-you note",
                    level = level,
                    orderIndex = 17,
                    prompt = "Du hast eine Einladung zum Essen erhalten und möchtest dich bedanken. Schreibe eine Dankes-E-Mail. Erinnere an den schönen Abend, bedanke dich für das Essen und die Gastfreundschaft, und schlage vor, sich bald wiederzusehen. (70-100 Wörter)",
                    minWords = 70,
                    maxWords = 100,
                    tips = listOf(
                        "Sei aufrichtig und herzlich",
                        "Erinnere an positive Momente",
                        "Bedanke dich konkret",
                        "Schlage weiteren Kontakt vor"
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Writing about work
                lessons.add(createSchreibenLesson(
                    title = "Arbeitsalltag beschreiben",
                    description = "Describe your work routine",
                    level = level,
                    orderIndex = 18,
                    prompt = "Beschreibe deinen Arbeitsalltag. Wann beginnt dein Arbeitstag? Was sind deine Aufgaben? Mit wem arbeitest du zusammen? Was gefällt dir an deiner Arbeit? Was ist manchmal schwierig? (120-150 Wörter)",
                    minWords = 120,
                    maxWords = 150,
                    tips = listOf(
                        "Beschreibe den Zeitplan",
                        "Liste wichtige Aufgaben auf",
                        "Erwähne Kollegen und Vorgesetzte",
                        "Teile Vor- und Nachteile"
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Writing about travel experiences
                lessons.add(createSchreibenLesson(
                    title = "Reiseerfahrungen",
                    description = "Write about travel experiences",
                    level = level,
                    orderIndex = 19,
                    prompt = "Erzähle von deiner schönsten Reise. Wohin bist du gefahren? Mit wem warst du unterwegs? Was habt ihr gemacht? Welche Sehenswürdigkeiten hast du besucht? Was war das Highlight? (130-160 Wörter)",
                    minWords = 130,
                    maxWords = 160,
                    tips = listOf(
                        "Beginne mit dem Reiseziel",
                        "Beschreibe die Reisegruppe",
                        "Erzähle von Aktivitäten",
                        "Hebe besondere Momente hervor"
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Writing requests
                lessons.add(createSchreibenLesson(
                    title = "Eine Anfrage stellen",
                    description = "Write a request",
                    level = level,
                    orderIndex = 20,
                    prompt = "Du möchtest Informationen über einen Deutschkurs bekommen. Schreibe eine E-Mail an das Sprachinstitut. Frage nach Kurszeiten, Preisen, Dauer, und ob es Anfängerkurse gibt. Gib auch deine Kontaktdaten an. (90-120 Wörter)",
                    minWords = 90,
                    maxWords = 120,
                    tips = listOf(
                        "Sei höflich und klar",
                        "Stelle konkrete Fragen",
                        "Gib ausreichend Informationen",
                        "Bitte um Rückmeldung"
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Writing about family
                lessons.add(createSchreibenLesson(
                    title = "Meine Familie vorstellen",
                    description = "Introduce your family",
                    level = level,
                    orderIndex = 21,
                    prompt = "Stelle deine Familie vor. Wie viele Personen seid ihr? Beschreibe jeden Familienangehörigen (Alter, Beruf, Charakter). Erzähle wie ihr zusammenlebt und was ihr gemeinsam macht. (120-150 Wörter)",
                    minWords = 120,
                    maxWords = 150,
                    tips = listOf(
                        "Beginne mit der Familiengröße",
                        "Beschreibe jedes Mitglied",
                        "Erzähle von Beziehungen",
                        "Beschreibe gemeinsame Aktivitäten"
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Writing opinions
                lessons.add(createSchreibenLesson(
                    title = "Eine Meinung äußern",
                    description = "Express an opinion",
                    level = level,
                    orderIndex = 22,
                    prompt = "Schreibe einen Leserbrief an eine Zeitung über Umweltschutz. Erkläre warum Umweltschutz wichtig ist, gib Beispiele für Umweltprobleme, und schlage Lösungen vor. (130-160 Wörter)",
                    minWords = 130,
                    maxWords = 160,
                    tips = listOf(
                        "Stelle das Thema vor",
                        "Gib Argumente und Beispiele",
                        "Sei überzeugend",
                        "Schlage praktische Lösungen vor"
                    ),
                    source = "ÖSD"
                ))

                // Goethe A2 - Writing apologies
                lessons.add(createSchreibenLesson(
                    title = "Eine Entschuldigung schreiben",
                    description = "Write an apology",
                    level = level,
                    orderIndex = 23,
                    prompt = "Du hast einen Termin vergessen und kommst zu spät zu einem Treffen. Schreibe eine Entschuldigungs-E-Mail an deinen Freund. Erkläre was passiert ist, entschuldige dich aufrichtig, und schlage einen neuen Termin vor. (80-110 Wörter)",
                    minWords = 80,
                    maxWords = 110,
                    tips = listOf(
                        "Sei ehrlich und aufrichtig",
                        "Erkläre die Situation",
                        "Übernimm die Verantwortung",
                        "Biete Wiedergutmachung an"
                    ),
                    source = "Goethe"
                ))

                // TELC A2 - Writing descriptions of places
                lessons.add(createSchreibenLesson(
                    title = "Einen Ort beschreiben",
                    description = "Describe a place",
                    level = level,
                    orderIndex = 24,
                    prompt = "Beschreibe deinen Lieblingsort in der Stadt oder Natur. Wo liegt er? Wie sieht er aus? Was kannst du dort machen? Warum gefällt er dir besonders? Was gibt es in der Nähe? (110-140 Wörter)",
                    minWords = 110,
                    maxWords = 140,
                    tips = listOf(
                        "Beschreibe Lage und Aussehen",
                        "Erzähle von Aktivitäten",
                        "Erkläre persönliche Bedeutung",
                        "Erwähne die Umgebung"
                    ),
                    source = "TELC"
                ))

                // ÖSD A2 - Writing about technology
                lessons.add(createSchreibenLesson(
                    title = "Technik im Alltag",
                    description = "Write about technology in daily life",
                    level = level,
                    orderIndex = 25,
                    prompt = "Beschreibe wie Technik deinen Alltag beeinflusst. Welche technischen Geräte benutzt du täglich? Wie hilft dir dein Smartphone? Was machst du am Computer? Welche Vor- und Nachteile siehst du? (120-150 Wörter)",
                    minWords = 120,
                    maxWords = 150,
                    tips = listOf(
                        "Liste wichtige Geräte auf",
                        "Beschreibe deren Nutzung",
                        "Erkläre Vorteile",
                        "Erwähne auch Nachteile"
                    ),
                    source = "ÖSD"
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Hobby-Beschreibung",
                    description = "Write about your hobby",
                    level = level,
                    orderIndex = 5,
                    prompt = "Beschreibe dein Lieblingshobby. Was machst du? Wie oft? Warum macht es dir Spaß? Wie hast du damit angefangen? (100-150 Wörter)",
                    minWords = 100,
                    maxWords = 150,
                    tips = listOf(
                        "Erkläre, was das Hobby ist",
                        "Beschreibe deine Aktivitäten",
                        "Erzähle von deinen Erfahrungen"
                    )
                ))
            }
            
            "B1" -> {
                lessons.add(createSchreibenLesson(
                    title = "Argumentativer Aufsatz",
                    description = "Write an argumentative essay",
                    level = level,
                    orderIndex = 1,
                    prompt = "Schreibe einen argumentativen Aufsatz zum Thema 'Sollte das Internet in Schulen verboten werden?' Nimm Stellung und begründe deine Meinung. (200-300 Wörter)",
                    minWords = 200,
                    maxWords = 300,
                    tips = listOf(
                        "Klare These formulieren",
                        "Pro- und Contra-Argumente abwägen",
                        "Mit einer Schlussfolgerung enden"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Berufsbiografie",
                    description = "Write a professional biography",
                    level = level,
                    orderIndex = 2,
                    prompt = "Schreibe eine kurze Berufsbiografie für deinen Lebenslauf. Beschreibe deine Ausbildung, Erfahrungen und Ziele. (150-250 Wörter)",
                    minWords = 150,
                    maxWords = 250,
                    tips = listOf(
                        "Chronologisch vorgehen",
                        "Wichtige Stationen hervorheben",
                        "Zukunftspläne erwähnen"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Umweltbericht",
                    description = "Write an environmental report",
                    level = level,
                    orderIndex = 3,
                    prompt = "Schreibe einen Bericht über Umweltschutz in deiner Stadt. Welche Probleme gibt es? Was wird dagegen getan? Was könntest du tun? (200-300 Wörter)",
                    minWords = 200,
                    maxWords = 300,
                    tips = listOf(
                        "Aktuelle Situation beschreiben",
                        "Maßnahmen analysieren",
                        "Persönliche Vorschläge machen"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Kulturvergleich",
                    description = "Write a cultural comparison",
                    level = level,
                    orderIndex = 4,
                    prompt = "Vergleiche die deutsche Kultur mit einer anderen Kultur, die du kennst. Was sind die Unterschiede in Essen, Feiern, Kommunikation? (200-300 Wörter)",
                    minWords = 200,
                    maxWords = 300,
                    tips = listOf(
                        "Spezifische Aspekte vergleichen",
                        "Beispiele geben",
                        "Respektvoll bleiben"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Technologie-Kommentar",
                    description = "Write a technology commentary",
                    level = level,
                    orderIndex = 5,
                    prompt = "Schreibe einen Kommentar über den Einfluss von Smartphones auf unser Leben. Welche Vor- und Nachteile siehst du? (200-300 Wörter)",
                    minWords = 200,
                    maxWords = 300,
                    tips = listOf(
                        "Aktuelle Entwicklungen beschreiben",
                        "Auswirkungen analysieren",
                        "Persönliche Erfahrungen einbauen"
                    )
                ))
            }
            
            "B2" -> {
                lessons.add(createSchreibenLesson(
                    title = "Wissenschaftlicher Aufsatz",
                    description = "Write a scientific essay",
                    level = level,
                    orderIndex = 1,
                    prompt = "Schreibe einen wissenschaftlichen Aufsatz über 'Die Auswirkungen der Globalisierung auf lokale Kulturen'. Verwende eine klare Struktur mit Einleitung, Hauptteil und Schluss. (400-600 Wörter)",
                    minWords = 400,
                    maxWords = 600,
                    tips = listOf(
                        "Thesen aufstellen und belegen",
                        "Quellen und Beispiele verwenden",
                        "Objektiv und sachlich schreiben"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Literaturanalyse",
                    description = "Write a literary analysis",
                    level = level,
                    orderIndex = 2,
                    prompt = "Analysiere ein literarisches Werk deiner Wahl. Beschreibe die Handlung, Charaktere, Themen und stilistische Mittel. (300-500 Wörter)",
                    minWords = 300,
                    maxWords = 500,
                    tips = listOf(
                        "Werk kurz vorstellen",
                        "Hauptelemente analysieren",
                        "Persönliche Interpretation geben"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Politischer Kommentar",
                    description = "Write a political commentary",
                    level = level,
                    orderIndex = 3,
                    prompt = "Schreibe einen politischen Kommentar zu einem aktuellen Thema. Nimm eine klare Position ein und begründe sie mit Argumenten. (300-500 Wörter)",
                    minWords = 300,
                    maxWords = 500,
                    tips = listOf(
                        "Aktuelle Ereignisse einordnen",
                        "Mehrere Perspektiven berücksichtigen",
                        "Konstruktive Lösungsvorschläge machen"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Wirtschaftsanalyse",
                    description = "Write an economic analysis",
                    level = level,
                    orderIndex = 4,
                    prompt = "Analysiere eine aktuelle wirtschaftliche Entwicklung. Erkläre die Ursachen, Auswirkungen und mögliche Lösungen. (400-600 Wörter)",
                    minWords = 400,
                    maxWords = 600,
                    tips = listOf(
                        "Fakten und Daten verwenden",
                        "Ursache-Wirkung-Zusammenhänge erklären",
                        "Zukunftsprognosen wagen"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Philosophischer Essay",
                    description = "Write a philosophical essay",
                    level = level,
                    orderIndex = 5,
                    prompt = "Schreibe einen philosophischen Essay über 'Freiheit und Verantwortung in der modernen Gesellschaft'. Diskutiere verschiedene philosophische Positionen. (400-600 Wörter)",
                    minWords = 400,
                    maxWords = 600,
                    tips = listOf(
                        "Philosophische Konzepte definieren",
                        "Verschiedene Ansichten diskutieren",
                        "Eigene Position entwickeln"
                    )
                ))
            }
            
            "C1" -> {
                lessons.add(createSchreibenLesson(
                    title = "Akademische Abhandlung",
                    description = "Write an academic treatise",
                    level = level,
                    orderIndex = 1,
                    prompt = "Verfasse eine akademische Abhandlung über 'Die Rolle der künstlichen Intelligenz in der Bildung'. Verwende wissenschaftliche Methoden und zitierfähige Quellen. (800-1200 Wörter)",
                    minWords = 800,
                    maxWords = 1200,
                    tips = listOf(
                        "Umfassende Literaturrecherche",
                        "Methodische Herangehensweise",
                        "Kritische Diskussion der Ergebnisse"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Literaturkritik",
                    description = "Write a literary criticism",
                    level = level,
                    orderIndex = 2,
                    prompt = "Schreibe eine detaillierte Literaturkritik zu einem bedeutenden Werk der deutschen Literatur. Analysiere Stil, Struktur, Themen und historischen Kontext. (600-900 Wörter)",
                    minWords = 600,
                    maxWords = 900,
                    tips = listOf(
                        "Tiefgehende Textanalyse",
                        "Historischen Kontext einbeziehen",
                        "Literaturtheoretische Ansätze verwenden"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Soziologische Studie",
                    description = "Write a sociological study",
                    level = level,
                    orderIndex = 3,
                    prompt = "Verfasse eine soziologische Studie über 'Soziale Medien und gesellschaftlicher Wandel'. Analysiere empirische Daten und theoretische Ansätze. (800-1200 Wörter)",
                    minWords = 800,
                    maxWords = 1200,
                    tips = listOf(
                        "Empirische Daten analysieren",
                        "Soziologische Theorien anwenden",
                        "Gesellschaftliche Implikationen diskutieren"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Kulturwissenschaftlicher Aufsatz",
                    description = "Write a cultural studies essay",
                    level = level,
                    orderIndex = 4,
                    prompt = "Schreibe einen kulturwissenschaftlichen Aufsatz über 'Interkulturalität in der deutschen Gesellschaft'. Untersuche kulturelle Hybridität und Identitätskonstruktionen. (700-1000 Wörter)",
                    minWords = 700,
                    maxWords = 1000,
                    tips = listOf(
                        "Kulturtheoretische Konzepte verwenden",
                        "Fallbeispiele analysieren",
                        "Identitätsfragen diskutieren"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Linguistische Analyse",
                    description = "Write a linguistic analysis",
                    level = level,
                    orderIndex = 5,
                    prompt = "Führe eine linguistische Analyse der deutschen Sprache im digitalen Zeitalter durch. Untersuche Sprachwandel, Anglizismen und neue Kommunikationsformen. (600-900 Wörter)",
                    minWords = 600,
                    maxWords = 900,
                    tips = listOf(
                        "Linguistische Methoden anwenden",
                        "Sprachbeispiele analysieren",
                        "Sprachwandelphänomene erklären"
                    )
                ))
            }
            
            "C2" -> {
                lessons.add(createSchreibenLesson(
                    title = "Wissenschaftliche Monografie",
                    description = "Write a scientific monograph",
                    level = level,
                    orderIndex = 1,
                    prompt = "Verfasse eine wissenschaftliche Monografie über 'Metaphysik und Ethik in der digitalen Ära'. Entwickle eine eigenständige philosophische Position. (1500-2500 Wörter)",
                    minWords = 1500,
                    maxWords = 2500,
                    tips = listOf(
                        "Umfassende philosophische Analyse",
                        "Eigene Theorie entwickeln",
                        "Interdisziplinäre Perspektiven einbeziehen"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Literaturwissenschaftliche Abhandlung",
                    description = "Write a literary studies treatise",
                    level = level,
                    orderIndex = 2,
                    prompt = "Schreibe eine literaturwissenschaftliche Abhandlung über 'Postmoderne Erzählstrategien in der deutschen Gegenwartsliteratur'. Analysiere innovative narrative Techniken. (1200-1800 Wörter)",
                    minWords = 1200,
                    maxWords = 1800,
                    tips = listOf(
                        "Literaturtheoretische Ansätze anwenden",
                        "Textanalysen durchführen",
                        "Innovative Interpretationsansätze entwickeln"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Kulturphilosophischer Essay",
                    description = "Write a cultural philosophical essay",
                    level = level,
                    orderIndex = 3,
                    prompt = "Verfasse einen kulturphilosophischen Essay über 'Ästhetik und Politik in der zeitgenössischen Kunst'. Untersuche die Beziehung zwischen Kunst und Gesellschaft. (1000-1500 Wörter)",
                    minWords = 1000,
                    maxWords = 1500,
                    tips = listOf(
                        "Philosophische Ästhetik anwenden",
                        "Kunsttheoretische Positionen diskutieren",
                        "Gesellschaftskritische Perspektiven entwickeln"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Wissenschaftstheoretische Abhandlung",
                    description = "Write a philosophy of science treatise",
                    level = level,
                    orderIndex = 4,
                    prompt = "Schreibe eine wissenschaftstheoretische Abhandlung über 'Objektivität und Werte in der modernen Wissenschaft'. Analysiere epistemologische Grundlagen. (1200-1800 Wörter)",
                    minWords = 1200,
                    maxWords = 1800,
                    tips = listOf(
                        "Wissenschaftstheoretische Konzepte anwenden",
                        "Epistemologische Fragen diskutieren",
                        "Methodologische Reflexion durchführen"
                    )
                ))
                
                lessons.add(createSchreibenLesson(
                    title = "Interdisziplinäre Studie",
                    description = "Write an interdisciplinary study",
                    level = level,
                    orderIndex = 5,
                    prompt = "Verfasse eine interdisziplinäre Studie über 'Sprache, Identität und Macht in der globalisierten Welt'. Verbinde linguistische, soziologische und politische Perspektiven. (1500-2500 Wörter)",
                    minWords = 1500,
                    maxWords = 2500,
                    tips = listOf(
                        "Mehrere Disziplinen integrieren",
                        "Komplexe Zusammenhänge analysieren",
                        "Innovative Forschungsansätze entwickeln"
                    )
                ))
            }
        }
        
        return lessons
    }
    
    private fun generateSprechenLessons(level: String): List<Lesson> {
        val lessons = mutableListOf<Lesson>()
        
        when (level) {
            "A1" -> {
                lessons.add(createSprechenLesson(
                    title = "Über dich sprechen",
                    description = "Introduce yourself and talk about your interests",
                    level = level,
                    orderIndex = 1,
                    prompt = "Stelle dich vor und erzähle über deine Hobbys und Interessen. Sprich 1-2 Minuten.",
                    modelResponse = "Hallo! Ich heiße [Name]. Ich bin [Alter] Jahre alt und komme aus [Land]. Ich arbeite als [Beruf]. In meiner Freizeit spiele ich gerne Fußball und lese Bücher. Ich mag auch Musik hören und reisen.",
                    keywords = listOf("heißen", "alt", "kommen aus", "arbeiten", "Hobby", "mögen")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Mein Haustier",
                    description = "Talk about your pet",
                    level = level,
                    orderIndex = 2,
                    prompt = "Erzähle über dein Haustier oder ein Tier, das du magst. Wie sieht es aus? Was kann es? Was macht es gerne? Sprich 1-2 Minuten.",
                    modelResponse = "Ich habe ein [Tier] namens [Name]. Es ist [Alter] Jahre alt und [Farbe]. [Name] kann [Fähigkeiten] und mag es, [Aktivitäten] zu machen. Ich spiele gerne mit [Name] und gehe mit ihm/ihr [Aktivität]. [Name] ist sehr [Adjektiv] und macht mich glücklich.",
                    keywords = listOf("Haustier", "Tier", "Name", "alt", "können", "mögen", "spielen")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Meine Schule",
                    description = "Talk about your school",
                    level = level,
                    orderIndex = 3,
                    prompt = "Erzähle über deine Schule. Wie sieht sie aus? Was lernst du dort? Wer sind deine Lehrer? Sprich 1-2 Minuten.",
                    modelResponse = "Meine Schule ist [Adjektiv] und [Adjektiv]. Es gibt [Anzahl] Klassenzimmer und eine [Einrichtung]. Die Lehrer sind [Adjektiv] und helfen uns. Ich lerne [Fächer] und mein Lieblingsfach ist [Fach]. In der Pause spielen wir [Spiel] und treffen uns mit Freunden. Die Schule beginnt um [Uhrzeit] und endet um [Uhrzeit].",
                    keywords = listOf("Schule", "Klassenzimmer", "Lehrer", "lernen", "Fach", "Pause", "Freunde")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Meine Lieblingsfarbe",
                    description = "Talk about your favorite color",
                    level = level,
                    orderIndex = 4,
                    prompt = "Erzähle über deine Lieblingsfarbe. Warum magst du sie? Wo siehst du sie? Was verbindest du mit dieser Farbe? Sprich 1-2 Minuten.",
                    modelResponse = "Meine Lieblingsfarbe ist [Farbe]. Ich mag sie, weil sie [Grund] ist. [Farbe] erinnert mich an [Assoziation]. Ich sehe diese Farbe oft in [Ort/Objekt]. Meine Kleidung ist oft [Farbe] und mein Zimmer ist auch [Farbe] gestrichen. [Farbe] macht mich [Gefühl] und ich verbinde sie mit [Erinnerung/Person].",
                    keywords = listOf("Lieblingsfarbe", "Farbe", "mögen", "erinnern", "sehen", "verbinden")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Das Wetter",
                    description = "Talk about the weather",
                    level = level,
                    orderIndex = 5,
                    prompt = "Beschreibe das Wetter heute und erzähle, was du bei diesem Wetter machst. Sprich 1-2 Minuten.",
                    modelResponse = "Heute ist das Wetter [Adjektiv]. Es ist [Temperatur] Grad Celsius. Der Himmel ist [Beschreibung]. Ich trage [Kleidung], weil es [Grund] ist. Bei diesem Wetter gehe ich gerne [Aktivität]. Gestern war das Wetter [Vergleich]. Morgen wird es wahrscheinlich [Vorhersage].",
                    keywords = listOf("Wetter", "Temperatur", "Himmel", "tragen", "gehen", "werden")
                ))

                // Goethe-Zertifikat A1 Sprechen - Additional Lessons (6-20)
                lessons.add(createSprechenLesson(
                    title = "Im Restaurant",
                    description = "Ordering food at a restaurant - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 6,
                    prompt = "Du bist im Restaurant. Bestelle Essen und Getränke. Erzähle dem Kellner, was du möchtest. Sprich 1-2 Minuten.",
                    modelResponse = "Guten Abend! Ich hätte gerne die [Gericht]. Als Vorspeise nehme ich die [Vorspeise]. Dazu bitte ein [Getränk]. Für meinen Freund/Freundin: [Gericht] und [Getränk]. Können Sie uns auch Brot bringen? Wir feiern heute [Anlass]. Danke für die Empfehlung!",
                    keywords = listOf("bestellen", "Gericht", "Vorspeise", "Getränk", "Brot", "feiern")
                ))

                lessons.add(createSprechenLesson(
                    title = "Einkaufen im Supermarkt",
                    description = "Shopping at the supermarket - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 7,
                    prompt = "Du bist im Supermarkt. Erzähle einer Verkäuferin, was du suchst. Frage nach Preisen und Qualität. Sprich 1-2 Minuten.",
                    modelResponse = "Entschuldigung, wo finde ich [Produkt]? Ist das [Produkt] frisch? Wie viel kostet [Produkt]? Haben Sie auch [Alternative]? Das sieht gut aus. Kann ich [Menge] nehmen? Danke für die Hilfe. Ich brauche noch [anderes Produkt].",
                    keywords = listOf("suchen", "finden", "frisch", "kosten", "nehmen", "brauchen")
                ))

                lessons.add(createSprechenLesson(
                    title = "Beim Arzt",
                    description = "Doctor visit conversation - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 8,
                    prompt = "Du bist beim Arzt. Erzähle dem Arzt von deinen Beschwerden. Beschreibe, was wehtut. Sprich 1-2 Minuten.",
                    modelResponse = "Guten Tag, Herr/Frau Doktor. Mir geht es nicht gut. Ich habe [Beschwerde] seit [Zeit]. Es tut [hier] weh. Ich habe auch [weitere Symptome]. Habe ich [Verdachtsdiagnose]? Was soll ich machen? Brauche ich [Medikamente]? Wie lange dauert das?",
                    keywords = listOf("weh", "tun", "beschwerden", "symptome", "machen", "brauchen")
                ))

                lessons.add(createSprechenLesson(
                    title = "Reisen und Verkehr",
                    description = "Travel and transportation - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 9,
                    prompt = "Erzähle von einer Reise, die du gemacht hast. Wie bist du gereist? Wohin bist du gefahren? Was hast du gesehen? Sprich 1-2 Minuten.",
                    modelResponse = "Letzte Woche bin ich nach [Ort] gefahren. Ich habe den [Verkehrsmittel] genommen. Die Fahrt hat [Dauer] gedauert. In [Ort] habe ich [Sehenswürdigkeit] besucht. Das Wetter war [Beschreibung]. Ich habe viele Fotos gemacht. Es war sehr schön!",
                    keywords = listOf("reisen", "gefahren", "genommen", "gedauert", "besucht", "gesehen")
                ))

                lessons.add(createSprechenLesson(
                    title = "Freizeit und Hobbys",
                    description = "Free time and hobbies - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 10,
                    prompt = "Erzähle über deine Hobbys. Was machst du gerne in deiner Freizeit? Treibst du Sport? Gehst du aus? Sprich 1-2 Minuten.",
                    modelResponse = "In meiner Freizeit [Hobby]. Ich [Hobby] sehr gerne und [Häufigkeit]. Manchmal gehe ich mit Freunden [Aktivität]. Am Wochenende [Wochenendaktivität]. Das macht mir viel Spaß. Meine Freunde finden das auch toll.",
                    keywords = listOf("Freizeit", "Hobby", "Sport", "gehen", "Wochenende", "Spaß")
                ))

                lessons.add(createSprechenLesson(
                    title = "Familie und Freunde",
                    description = "Family and friends - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 11,
                    prompt = "Erzähle über deine Familie und Freunde. Wie viele Geschwister hast du? Was machen deine Eltern? Hast du viele Freunde? Sprich 1-2 Minuten.",
                    modelResponse = "Meine Familie ist [Größe]. Ich habe [Anzahl] Geschwister. Mein Bruder/Schwester ist [Alter] Jahre alt und [macht]. Meine Eltern arbeiten als [Berufe]. Ich habe viele Freunde. Wir treffen uns oft und [Aktivitäten]. Meine beste Freundin heißt [Name].",
                    keywords = listOf("Familie", "Geschwister", "Eltern", "arbeiten", "Freunde", "treffen")
                ))

                lessons.add(createSprechenLesson(
                    title = "Im Büro arbeiten",
                    description = "Working in the office - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 12,
                    prompt = "Beschreibe deinen Arbeitstag. Wann beginnt deine Arbeit? Was machst du im Büro? Arbeitet dein Kollege mit dir? Sprich 1-2 Minuten.",
                    modelResponse = "Mein Arbeitstag beginnt um [Uhrzeit]. Ich komme mit dem [Verkehrsmittel] zur Arbeit. Im Büro arbeite ich am Computer. Ich schreibe E-Mails und mache Präsentationen. Mein Kollege hilft mir oft. Mittags esse ich in der Kantine. Nach der Arbeit gehe ich nach Hause.",
                    keywords = listOf("Arbeitstag", "beginnt", "Büro", "arbeiten", "schreiben", "helfen")
                ))

                lessons.add(createSprechenLesson(
                    title = "Essen und Kochen",
                    description = "Food and cooking - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 13,
                    prompt = "Erzähle über Essen und Kochen. Was isst du gerne? Kochst du selbst? Was ist dein Lieblingsessen? Sprich 1-2 Minuten.",
                    modelResponse = "Ich esse sehr gerne [Lieblingsessen]. Mein Lieblingsgericht ist [Gericht] mit [Beilage]. Ich koche selbst [Häufigkeit]. Das Kochen macht mir Spaß. Meine Familie mag mein [besonderes Gericht]. Manchmal gehen wir ins Restaurant und probieren neue Gerichte.",
                    keywords = listOf("essen", "gerne", "kochen", "Lieblingsgericht", "Spaß", "probieren")
                ))

                lessons.add(createSprechenLesson(
                    title = "Wohnen und Zuhause",
                    description = "Living and home - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 14,
                    prompt = "Beschreibe deine Wohnung oder dein Haus. Wie viele Zimmer hat es? Was gibt es in jedem Zimmer? Sprich 1-2 Minuten.",
                    modelResponse = "Ich wohne in [Art der Wohnung]. Sie hat [Anzahl] Zimmer. Im Wohnzimmer steht [Möbel]. In der Küche koche ich gerne. Mein Schlafzimmer ist [Beschreibung]. Das Bad hat [Ausstattung]. Ich mag meine Wohnung sehr, weil [Grund].",
                    keywords = listOf("wohnen", "Wohnung", "Zimmer", "Wohnzimmer", "Küche", "Schlafzimmer")
                ))

                lessons.add(createSprechenLesson(
                    title = "Sport treiben",
                    description = "Doing sports - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 15,
                    prompt = "Erzähle über Sport. Welchen Sport treibst du? Wie oft machst du Sport? Wo trainierst du? Sprich 1-2 Minuten.",
                    modelResponse = "Ich treibe gerne [Sportart]. Ich mache das [Häufigkeit] im [Ort]. Mein Training dauert [Dauer]. Ich gehe mit [Begleitung] zum Sport. Danach fühle ich mich [Gefühl]. Sport ist wichtig für die Gesundheit und macht Spaß.",
                    keywords = listOf("treiben", "Sport", "trainieren", "dauert", "fühlen", "wichtig")
                ))

                lessons.add(createSprechenLesson(
                    title = "Einkaufen gehen",
                    description = "Going shopping - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 16,
                    prompt = "Du gehst einkaufen. Erzähle, was du brauchst. In welchen Geschäften gehst du? Wie bezahlst du? Sprich 1-2 Minuten.",
                    modelResponse = "Heute gehe ich einkaufen. Ich brauche [Produkte]. Zuerst gehe ich in den Supermarkt für Lebensmittel. Dann in die [Geschäft] für [Produkt]. Die Preise sind [Bewertung]. Ich bezahle mit [Zahlungsmittel]. Manchmal kaufe ich auch online.",
                    keywords = listOf("einkaufen", "brauchen", "gehen", "Supermarkt", "bezahlen", "kaufen")
                ))

                lessons.add(createSprechenLesson(
                    title = "Termine und Zeit",
                    description = "Appointments and time - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 17,
                    prompt = "Erzähle von deinen Terminen. Hast du heute Termine? Wann musst du wo sein? Wie organisierst du deine Zeit? Sprich 1-2 Minuten.",
                    modelResponse = "Heute habe ich mehrere Termine. Um [Uhrzeit] muss ich zum [Termin]. Dann gehe ich zu [nächster Termin]. Meine Termine organisiere ich mit [Hilfsmittel]. Manchmal bin ich zu spät, aber meistens pünktlich. Zeitmanagement ist wichtig für mich.",
                    keywords = listOf("Termine", "muss", "gehen", "organisiere", "pünktlich", "wichtig")
                ))

                lessons.add(createSprechenLesson(
                    title = "Gesundheit und Krankheit",
                    description = "Health and illness - Goethe Teil 1 style",
                    level = level,
                    orderIndex = 18,
                    prompt = "Erzähle über Gesundheit. Wie bleibst du gesund? Was machst du, wenn du krank bist? Gehst du zum Arzt? Sprich 1-2 Minuten.",
                    modelResponse = "Für meine Gesundheit [Aktivitäten]. Ich esse gesund und treibe Sport. Wenn ich krank bin, bleibe ich im Bett und trinke Tee. Manchmal gehe ich zum Arzt. Mein Hausarzt heißt [Name]. Er hilft mir immer. Gesundheit ist das Wichtigste.",
                    keywords = listOf("Gesundheit", "bleiben", "essen", "treiben", "krank", "gehen")
                ))

                lessons.add(createSprechenLesson(
                    title = "Reisen planen",
                    description = "Planning a trip - Goethe Teil 2 style",
                    level = level,
                    orderIndex = 19,
                    prompt = "Du planst eine Reise. Wohin möchtest du fahren? Wie reist du? Was möchtest du sehen? Sprich 1-2 Minuten.",
                    modelResponse = "Ich plane eine Reise nach [Land]. Ich fliege mit dem Flugzeug. Die Reise dauert [Dauer]. Ich möchte [Sehenswürdigkeiten] sehen und [Aktivitäten] machen. Mein Hotel ist [Beschreibung]. Ich freue mich schon sehr auf die Reise!",
                    keywords = listOf("planen", "Reise", "fliegen", "dauert", "möchte", "sehen")
                ))

                lessons.add(createSprechenLesson(
                    title = "Alltag und Routine",
                    description = "Daily life and routine - Goethe Teil 3 style",
                    level = level,
                    orderIndex = 20,
                    prompt = "Beschreibe deinen Alltag. Wann stehst du auf? Was machst du morgens? Wie ist dein Arbeitstag? Was machst du abends? Sprich 1-2 Minuten.",
                    modelResponse = "Mein Alltag beginnt um [Uhrzeit]. Morgens [Aktivitäten]. Dann gehe ich zur Arbeit/Schule. Mittags esse ich [Essen]. Nachmittags [Aktivitäten]. Abends [Aktivitäten]. Mein Alltag ist [Bewertung], aber ich mag ihn.",
                    keywords = listOf("Alltag", "beginnt", "morgens", "gehe", "mittags", "abends")
                ))

                // TELC Deutsch A1 Sprechen - Lessons (30-32)
                lessons.add(createSprechenLesson(
                    title = "Sich vorstellen",
                    description = "Introducing yourself - TELC Sprechen Teil 1",
                    level = level,
                    orderIndex = 30,
                    prompt = "Stellen Sie sich vor. Sagen Sie, wie Sie heißen, woher Sie kommen, was Sie machen und was Ihre Hobbys sind. Sprich 1-2 Minuten.",
                    modelResponse = "Hallo! Mein Name ist [Name]. Ich komme aus [Land/Stadt]. Ich bin [Alter] Jahre alt. Ich arbeite als [Beruf] oder ich studiere [Fach]. In meiner Freizeit [Hobbys]. Ich spreche [Sprachen].",
                    keywords = listOf("heißen", "kommen", "arbeiten", "studieren", "Hobbys", "sprechen")
                ))

                lessons.add(createSprechenLesson(
                    title = "Termine vereinbaren",
                    description = "Making appointments - TELC Sprechen Teil 2",
                    level = level,
                    orderIndex = 31,
                    prompt = "Sie möchten einen Termin beim Arzt/Zahnarzt vereinbaren. Rufen Sie an und erklären Sie Ihr Problem. Vereinbaren Sie einen Termin. Sprich 1-2 Minuten.",
                    modelResponse = "Guten Tag, hier ist [Name]. Ich hätte gerne einen Termin beim [Arzt/Zahnarzt]. Ich habe [Problem]. Wann haben Sie einen Termin frei? Morgen um [Uhrzeit] wäre gut. Vielen Dank für Ihre Hilfe.",
                    keywords = listOf("Termin", "vereinbaren", "Problem", "wann", "frei", "hilfe")
                ))

                lessons.add(createSprechenLesson(
                    title = "Einkaufen und Preise",
                    description = "Shopping and prices - TELC Sprechen Teil 3",
                    level = level,
                    orderIndex = 32,
                    prompt = "Sie sind im Geschäft und möchten etwas kaufen. Fragen Sie nach Preisen, Größen und Qualität. Vergleichen Sie verschiedene Produkte. Sprich 1-2 Minuten.",
                    modelResponse = "Entschuldigung, wie viel kostet [Produkt]? Haben Sie das in [Größe]? Was ist der Unterschied zwischen [Produkt A] und [Produkt B]? Das ist sehr schön, aber ein bisschen teuer. Haben Sie eine günstigere Variante?",
                    keywords = listOf("kosten", "Größe", "Unterschied", "teuer", "günstig", "Variante")
                ))

                // ÖSD Zertifikat A1 Sprechen - Lessons (42-44)
                lessons.add(createSprechenLesson(
                    title = "Amtliche Angelegenheiten",
                    description = "Official matters - ÖSD Sprechen Teil 1",
                    level = level,
                    orderIndex = 42,
                    prompt = "Sie müssen etwas beim Amt erledigen. Erklären Sie Ihr Anliegen, zeigen Sie Ihre Unterlagen und beantworten Sie Fragen des Beamten. Sprich 1-2 Minuten.",
                    modelResponse = "Guten Tag! Ich möchte [Anliegen, z.B. mich anmelden]. Hier sind meine Unterlagen: Personalausweis, Meldezettel, [weitere Dokumente]. Ich bin [Alter] Jahre alt und komme aus [Land]. Ich brauche [Dokument/Zulassung/etc.] für [Zweck].",
                    keywords = listOf("Amt", "Anliegen", "Unterlagen", "anmelden", "Dokumente", "brauchen")
                ))

                lessons.add(createSprechenLesson(
                    title = "Berufliche Kommunikation",
                    description = "Professional communication - ÖSD Sprechen Teil 2",
                    level = level,
                    orderIndex = 43,
                    prompt = "Sie sind in einem Geschäft oder Büro und müssen ein berufliches Gespräch führen. Stellen Sie sich vor, erklären Sie Ihr Anliegen und finden Sie eine Lösung. Sprich 1-2 Minuten.",
                    modelResponse = "Guten Tag! Mein Name ist [Name]. Ich arbeite bei [Firma] und benötige [Information/Produkt/Dienstleistung]. Können Sie mir helfen? Ich brauche [Details]. Wann wäre das möglich? Vielen Dank für Ihre Unterstützung.",
                    keywords = listOf("arbeiten", "benötigen", "helfen", "brauchen", "möglich", "Unterstützung")
                ))

                lessons.add(createSprechenLesson(
                    title = "Soziale Interaktionen",
                    description = "Social interactions - ÖSD Sprechen Teil 3",
                    level = level,
                    orderIndex = 44,
                    prompt = "Sie nehmen an einer sozialen Veranstaltung teil. Lernen Sie jemanden kennen, stellen Sie Fragen und erzählen Sie von sich. Sprich 1-2 Minuten.",
                    modelResponse = "Hallo! Schön Sie kennenzulernen. Ich heiße [Name]. Woher kommen Sie? Was machen Sie beruflich? Ich komme aus [Ort] und arbeite als [Beruf]. In meiner Freizeit [Hobbys]. Erzählen Sie mir von sich!",
                    keywords = listOf("kennenlernen", "heißen", "kommen", "arbeiten", "Freizeit", "erzählen")
                ))
            }

            "A2" -> {
                lessons.add(createSprechenLesson(
                    title = "Über deine Stadt",
                    description = "Describe your city or town",
                    level = level,
                    orderIndex = 1,
                    prompt = "Beschreibe deine Stadt oder dein Dorf. Erzähle über Sehenswürdigkeiten, Restaurants und Aktivitäten. Sprich 2-3 Minuten.",
                    modelResponse = "Ich wohne in [Stadt]. Das ist eine [große/kleine] Stadt mit vielen interessanten Sehenswürdigkeiten. Es gibt ein historisches Zentrum mit einem Marktplatz und einer Kirche. In der Stadt gibt es viele gute Restaurants und Cafés. Die Menschen sind sehr freundlich. Es gibt auch viele Parks und Grünflächen.",
                    keywords = listOf("wohnen", "Stadt", "Sehenswürdigkeiten", "Restaurant", "Park", "freundlich")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Reiseerlebnisse",
                    description = "Talk about travel experiences",
                    level = level,
                    orderIndex = 2,
                    prompt = "Erzähle über eine interessante Reise, die du gemacht hast. Wohin bist du gefahren? Was hast du erlebt? Sprich 2-3 Minuten.",
                    modelResponse = "Letzten Sommer bin ich nach [Land] gefahren. Ich war in [Stadt] und habe viele interessante Orte besucht. Das Wetter war [Beschreibung] und die Menschen waren sehr [Adjektiv]. Ich habe [Aktivitäten] gemacht und viel über die Kultur gelernt. Das Essen war [Beschreibung] und ich habe neue Freunde kennengelernt.",
                    keywords = listOf("reisen", "besuchen", "Wetter", "Kultur", "Essen", "kennenlernen")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Hobbys und Freizeit",
                    description = "Talk about hobbies and free time",
                    level = level,
                    orderIndex = 3,
                    prompt = "Erzähle über deine Hobbys und was du in deiner Freizeit machst. Wie oft machst du diese Aktivitäten? Sprich 2-3 Minuten.",
                    modelResponse = "Ich habe viele verschiedene Hobbys. Am liebsten [Hobby 1] und [Hobby 2]. Ich [Hobby 1] [Häufigkeit] und das macht mir viel Spaß. Außerdem [Hobby 2] ich gerne und treffe mich mit Freunden. Am Wochenende gehe ich oft [Aktivität] und manchmal [Aktivität]. Diese Hobbys helfen mir, [Grund].",
                    keywords = listOf("Hobby", "Freizeit", "Spaß", "treffen", "helfen", "machen")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Arbeit und Beruf",
                    description = "Talk about work and profession",
                    level = level,
                    orderIndex = 4,
                    prompt = "Erzähle über deine Arbeit oder deinen Beruf. Was machst du? Was gefällt dir daran? Sprich 2-3 Minuten.",
                    modelResponse = "Ich arbeite als [Beruf] bei [Firma]. Meine Arbeit ist [Beschreibung] und ich bin dafür verantwortlich, [Aufgaben]. Ich arbeite [Stunden] pro Woche und mein Arbeitsplatz ist [Ort]. Was mir an meiner Arbeit gefällt, ist [Grund]. Manchmal ist es [Schwierigkeit], aber meistens macht es Spaß.",
                    keywords = listOf("arbeiten", "Beruf", "verantwortlich", "Arbeitsplatz", "gefällt", "Spaß")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Gesundheit und Sport",
                    description = "Talk about health and sports",
                    level = level,
                    orderIndex = 5,
                    prompt = "Erzähle über deine Gesundheit und Sportaktivitäten. Was machst du, um gesund zu bleiben? Sprich 2-3 Minuten.",
                    modelResponse = "Gesundheit ist mir sehr wichtig. Ich achte auf [Aspekt] und mache regelmäßig Sport. Ich gehe [Häufigkeit] ins Fitnessstudio und mache [Sportarten]. Außerdem esse ich [Ernährung] und versuche, genug zu schlafen. Sport hilft mir, [Vorteil] und ich fühle mich [Gefühl].",
                    keywords = listOf("Gesundheit", "Sport", "Fitnessstudio", "essen", "schlafen", "fühlen")
                ))

                // Goethe A2 - Extended speaking content
                lessons.add(createSprechenLesson(
                    title = "Einen Freund beschreiben",
                    description = "Describe a friend (Goethe-Zertifikat A2)",
                    level = level,
                    orderIndex = 6,
                    prompt = "Beschreibe einen deiner Freunde oder eine Freundin. Wie sieht er/sie aus? Welche Persönlichkeit hat er/sie? Was macht ihr zusammen? Sprich 2-3 Minuten.",
                    modelResponse = "Mein bester Freund heißt [Name]. Er ist [Alter] Jahre alt und wohnt in [Stadt]. Er hat [Haarfarbe] Haare und [Augenfarbe] Augen. Er ist [Größe] groß und trägt gerne [Kleidung]. [Name] ist sehr [Persönlichkeitsmerkmal] und [Persönlichkeitsmerkmal]. Wir kennen uns seit [Zeit] Jahren und machen oft zusammen Sport. Am Wochenende gehen wir [Aktivität] oder treffen uns mit anderen Freunden.",
                    keywords = listOf("Freund", "beschreiben", "Persönlichkeit", "zusammen", "Sport", "treffen"),
                    source = "Goethe"
                ))

                // TELC A2 - Speaking about plans
                lessons.add(createSprechenLesson(
                    title = "Wochenendpläne",
                    description = "Talk about weekend plans (TELC Deutsch A2)",
                    level = level,
                    orderIndex = 7,
                    prompt = "Erzähle über deine Pläne für das Wochenende. Was wirst du machen? Mit wem? Wo? Wann beginnt es? Sprich 2-3 Minuten.",
                    modelResponse = "Dieses Wochenende habe ich viele Pläne. Am Samstagmorgen gehe ich einkaufen und dann treffe ich mich mit [Freund]. Wir wollen ins Kino gehen und einen Film sehen. Der Film beginnt um [Uhrzeit]. Am Abend essen wir zusammen in einem Restaurant. Am Sonntag schlafe ich länger aus und dann gehe ich joggen im Park. Vielleicht besuche ich auch meine Familie.",
                    keywords = listOf("Wochenende", "Pläne", "treffen", "Kino", "essen", "besuchen"),
                    source = "TELC"
                ))

                // ÖSD A2 - Speaking about past experiences
                lessons.add(createSprechenLesson(
                    title = "Ein besonderes Erlebnis",
                    description = "Talk about a special experience (ÖSD Zertifikat A2)",
                    level = level,
                    orderIndex = 8,
                    prompt = "Erzähle von einem besonderen Erlebnis in deinem Leben. Was ist passiert? Wann war das? Wie hast du dich gefühlt? Sprich 2-3 Minuten.",
                    modelResponse = "Vor zwei Jahren hatte ich ein sehr besonderes Erlebnis. Ich bin nach [Land] gereist und habe [Aktivität] gemacht. Es war im [Jahreszeit] und das Wetter war [Beschreibung]. Ich war mit [Person] zusammen und wir haben [Details]. Es war sehr aufregend und ich habe mich [Gefühl] gefühlt. Seitdem erzähle ich oft von diesem Erlebnis.",
                    keywords = listOf("Erlebnis", "passiert", "gefühlt", "gereist", "aufregend", "erzählen"),
                    source = "ÖSD"
                ))

                // Goethe A2 - Speaking about daily routines
                lessons.add(createSprechenLesson(
                    title = "Mein Tagesablauf",
                    description = "Describe your daily routine",
                    level = level,
                    orderIndex = 9,
                    prompt = "Beschreibe deinen typischen Tagesablauf. Wann stehst du auf? Was machst du morgens? Wie kommst du zur Arbeit/Schule? Sprich 2-3 Minuten.",
                    modelResponse = "Mein Tag beginnt normalerweise um [Uhrzeit]. Ich stehe auf, dusche und frühstücke. Dann ziehe ich mich an und gehe zur Arbeit. Ich arbeite von [Uhrzeit] bis [Uhrzeit] als [Beruf]. In der Mittagspause esse ich [Essen] und treffe manchmal Kollegen. Nach der Arbeit kaufe ich ein und koche Abendessen. Abends sehe ich fern oder lese. Um [Uhrzeit] gehe ich ins Bett.",
                    keywords = listOf("Tagesablauf", "aufstehen", "arbeiten", "Mittagspause", "Abendessen", "schlafen"),
                    source = "Goethe"
                ))

                // TELC A2 - Speaking about shopping
                lessons.add(createSprechenLesson(
                    title = "Einkaufen",
                    description = "Talk about shopping",
                    level = level,
                    orderIndex = 10,
                    prompt = "Erzähle über deine Einkaufsgewohnheiten. Wo kaufst du ein? Was kaufst du normalerweise? Wie oft gehst du einkaufen? Sprich 2-3 Minuten.",
                    modelResponse = "Ich gehe gerne einkaufen, aber nicht zu oft. Normalerweise kaufe ich im Supermarkt in der Nähe ein. Ich kaufe Obst, Gemüse, Brot und Milchprodukte. Zweimal pro Woche gehe ich in den Supermarkt. Manchmal kaufe ich auch Kleidung in einem Geschäft in der Stadtmitte. Ich achte auf die Preise und nehme oft Sonderangebote wahr. Einkaufen macht mir Spaß, besonders wenn ich Zeit habe.",
                    keywords = listOf("Einkaufen", "Supermarkt", "kaufen", "Preise", "Sonderangebote", "Spaß"),
                    source = "TELC"
                ))

                // ÖSD A2 - Speaking about food and cooking
                lessons.add(createSprechenLesson(
                    title = "Essen und Kochen",
                    description = "Talk about food and cooking",
                    level = level,
                    orderIndex = 11,
                    prompt = "Erzähle über deine Essgewohnheiten und Kochen. Was isst du gerne? Kochst du selbst? Welche Gerichte kannst du kochen? Sprich 2-3 Minuten.",
                    modelResponse = "Essen ist mir sehr wichtig und ich koche gerne selbst. Zum Frühstück esse ich immer Müsli mit Obst und Joghurt. Mittags esse ich meistens in der Kantine bei der Arbeit. Abends koche ich verschiedene Gerichte. Ich kann gut Spaghetti kochen, Salat machen und manchmal backe ich Kuchen. Meine Lieblingsgerichte sind Pizza und asiatisches Essen. Ich achte darauf, viel Gemüse zu essen.",
                    keywords = listOf("Essen", "kochen", "Frühstück", "Gerichte", "Lieblingsgerichte", "Gemüse"),
                    source = "ÖSD"
                ))

                // Goethe A2 - Speaking about technology
                lessons.add(createSprechenLesson(
                    title = "Technik im Alltag",
                    description = "Talk about technology in daily life",
                    level = level,
                    orderIndex = 12,
                    prompt = "Erzähle über die Technik in deinem Leben. Welche technischen Geräte benutzt du? Wie hilft dir das Smartphone? Sprich 2-3 Minuten.",
                    modelResponse = "Technik spielt eine große Rolle in meinem Alltag. Ich benutze jeden Tag mein Smartphone für viele Dinge. Damit telefoniere ich, schreibe Nachrichten und gehe ins Internet. Ich habe auch einen Computer zu Hause für die Arbeit und zum Lernen. Im Auto höre ich Musik über Bluetooth. Technik macht vieles einfacher, aber manchmal vermisse ich auch die alte Zeit ohne so viele Geräte.",
                    keywords = listOf("Technik", "Smartphone", "Computer", "Bluetooth", "einfacher", "Geräte"),
                    source = "Goethe"
                ))

                // TELC A2 - Speaking about travel
                lessons.add(createSprechenLesson(
                    title = "Reisen",
                    description = "Talk about traveling",
                    level = level,
                    orderIndex = 13,
                    prompt = "Erzähle über deine Reiseerfahrungen. Wohin bist du schon gereist? Wie reist du am liebsten? Was planst du als nächstes? Sprich 2-3 Minuten.",
                    modelResponse = "Ich liebe es zu reisen und habe schon viele Länder besucht. Letztes Jahr war ich in Italien und Spanien. Am liebsten reise ich mit dem Zug oder Auto, weil ich unterwegs die Landschaft sehen kann. Flugzeug nehme ich nur für weite Strecken. Als nächstes möchte ich nach Skandinavien fahren. Ich plane immer meine Reisen im Voraus und freue mich auf neue Abenteuer.",
                    keywords = listOf("reisen", "besucht", "liebsten", "planen", "Abenteuer", "Landschaft"),
                    source = "TELC"
                ))

                // ÖSD A2 - Speaking about education
                lessons.add(createSprechenLesson(
                    title = "Lernen und Bildung",
                    description = "Talk about learning and education",
                    level = level,
                    orderIndex = 14,
                    prompt = "Erzähle über deine Bildung und Lernen. Was hast du gelernt? Lernst du noch etwas Neues? Welche Sprachen sprichst du? Sprich 2-3 Minuten.",
                    modelResponse = "Ich habe [Abschluss] und arbeite seit [Zeit] Jahren. Neben der Arbeit lerne ich Deutsch, weil ich in Deutschland lebe. Zweimal pro Woche habe ich Unterricht. Ich spreche schon ganz gut Deutsch und Englisch. Außerdem interessiere ich mich für Kochen und nehme Kochkurse. Lernen macht mir Spaß und ich lerne gerne neue Dinge. Nächstes Jahr möchte ich einen Computerkurs machen.",
                    keywords = listOf("Bildung", "lernen", "Sprachen", "Unterricht", "interessieren", "Kurs"),
                    source = "ÖSD"
                ))

                // Goethe A2 - Speaking about family
                lessons.add(createSprechenLesson(
                    title = "Meine Familie",
                    description = "Talk about your family",
                    level = level,
                    orderIndex = 15,
                    prompt = "Erzähle über deine Familie. Wie viele Personen seid ihr? Was machen deine Familienmitglieder? Sprich 2-3 Minuten.",
                    modelResponse = "Meine Familie besteht aus [Anzahl] Personen. Meine Eltern wohnen in [Stadt] und mein Vater arbeitet als [Beruf]. Meine Mutter ist [Beruf] und hat viel zu tun. Ich habe [Anzahl] Geschwister. Meine Schwester ist [Alter] und studiert [Fach]. Mein Bruder ist [Alter] und macht eine Ausbildung. Wir treffen uns oft am Wochenende und feiern Familienfeste zusammen.",
                    keywords = listOf("Familie", "bestehen", "arbeiten", "studieren", "treffen", "feiern"),
                    source = "Goethe"
                ))

                // TELC A2 - Speaking about free time
                lessons.add(createSprechenLesson(
                    title = "Freizeitaktivitäten",
                    description = "Talk about leisure activities",
                    level = level,
                    orderIndex = 16,
                    prompt = "Erzähle über deine Freizeitaktivitäten. Was machst du in deiner freien Zeit? Gehst du aus? Bleibst du zu Hause? Sprich 2-3 Minuten.",
                    modelResponse = "In meiner Freizeit mache ich viele verschiedene Dinge. Am Wochenende gehe ich oft ins Fitnessstudio und treibe Sport. Manchmal gehe ich mit Freunden aus, ins Kino oder in ein Café. Zu Hause koche ich gerne und sehe fern. Ich lese auch Bücher, besonders Krimis. Im Sommer mache ich gerne Wanderungen oder fahre Rad. Freizeit ist wichtig für mich, um zu entspannen und neue Energie zu tanken.",
                    keywords = listOf("Freizeit", "Sport", "ausgehen", "kochen", "lesen", "entspannen"),
                    source = "TELC"
                ))

                // ÖSD A2 - Speaking about environment
                lessons.add(createSprechenLesson(
                    title = "Umwelt und Natur",
                    description = "Talk about environment and nature",
                    level = level,
                    orderIndex = 17,
                    prompt = "Erzähle über Umwelt und Natur. Was machst du für die Umwelt? Besuchst du gerne die Natur? Sprich 2-3 Minuten.",
                    modelResponse = "Umwelt ist ein wichtiges Thema für mich. Ich versuche, umweltbewusst zu leben. Ich trenne den Müll und kaufe Bio-Produkte. Mit dem Fahrrad fahre ich zur Arbeit, statt mit dem Auto. In der Natur mache ich gerne Wanderungen im Wald oder am See. Die Natur ist schön und ich entspanne dort. Manchmal mache ich auch Picknick mit Freunden. Ich finde, wir müssen die Umwelt besser schützen.",
                    keywords = listOf("Umwelt", "Natur", "umweltbewusst", "trennen", "Wanderungen", "schützen"),
                    source = "ÖSD"
                ))

                // Goethe A2 - Speaking about work
                lessons.add(createSprechenLesson(
                    title = "Mein Arbeitsplatz",
                    description = "Talk about your workplace",
                    level = level,
                    orderIndex = 18,
                    prompt = "Beschreibe deinen Arbeitsplatz. Wo arbeitest du? Wie sieht dein Büro aus? Was machst du den ganzen Tag? Sprich 2-3 Minuten.",
                    modelResponse = "Ich arbeite in einem Büro in der Stadtmitte. Mein Arbeitsplatz ist in einem modernen Gebäude mit großen Fenstern. Ich sitze an einem Schreibtisch mit Computer und Telefon. Den ganzen Tag bearbeite ich E-Mails, führe Telefonate und habe Meetings mit Kollegen. Manchmal muss ich auch Berichte schreiben. Die Atmosphäre ist gut und die Kollegen sind nett. Ich arbeite von 9 bis 17 Uhr mit einer Stunde Mittagspause.",
                    keywords = listOf("Arbeitsplatz", "arbeiten", "Büro", "bearbeiten", "führen", "schreiben"),
                    source = "Goethe"
                ))

                // TELC A2 - Speaking about media
                lessons.add(createSprechenLesson(
                    title = "Medien und Unterhaltung",
                    description = "Talk about media and entertainment",
                    level = level,
                    orderIndex = 19,
                    prompt = "Erzähle über deine Mediennutzung. Welche Fernsehsendungen siehst du? Hörst du Radio? Liest du Zeitung? Sprich 2-3 Minuten.",
                    modelResponse = "Ich informiere mich jeden Tag über die Nachrichten. Morgens höre ich Radio im Auto auf dem Weg zur Arbeit. Abends sehe ich manchmal fern, besonders die Tagesschau und Dokumentationen. Am Wochenende lese ich die Zeitung und Zeitschriften. Ich höre auch Podcasts über verschiedene Themen. Im Internet lese ich Nachrichten und sehe Videos. Medien sind wichtig, aber ich achte darauf, nicht zu viel Zeit damit zu verbringen.",
                    keywords = listOf("Medien", "fernsehen", "hören", "lesen", "informieren", "achten"),
                    source = "TELC"
                ))

                // ÖSD A2 - Speaking about future plans
                lessons.add(createSprechenLesson(
                    title = "Zukunftspläne",
                    description = "Talk about future plans",
                    level = level,
                    orderIndex = 20,
                    prompt = "Erzähle über deine Pläne für die Zukunft. Was möchtest du in den nächsten Jahren erreichen? Sprich 2-3 Minuten.",
                    modelResponse = "Für die Zukunft habe ich viele Pläne. Beruflich möchte ich mich weiterentwickeln und mehr Verantwortung übernehmen. Vielleicht mache ich einen Kurs oder Weiterbildung. Privat möchte ich mehr reisen und neue Länder kennenlernen. Ich spare Geld für eine Reise nach Asien. Auch meine Wohnung möchte ich renovieren. Gesundheit ist mir wichtig, deshalb mache ich mehr Sport. Die Zukunft sieht positiv aus.",
                    keywords = listOf("Zukunft", "Pläne", "beruflich", "reisen", "sparen", "Gesundheit"),
                    source = "ÖSD"
                ))

                // Goethe A2 - Speaking about culture
                lessons.add(createSprechenLesson(
                    title = "Kultur und Traditionen",
                    description = "Talk about culture and traditions",
                    level = level,
                    orderIndex = 21,
                    prompt = "Erzähle über Kultur und Traditionen in deinem Land oder in Deutschland. Welche Feste feiert ihr? Sprich 2-3 Minuten.",
                    modelResponse = "In Deutschland gibt es viele schöne Traditionen. Weihnachten ist das wichtigste Fest. Wir schmücken den Baum und feiern mit der Familie. Ostern feiern wir auch, mit bemalten Eiern und Hasen. Karneval ist lustig, besonders im Rheinland. Ich mag auch den Frühlingsanfang mit den ersten Blumen. Jede Region hat ihre eigenen Traditionen. Das macht Deutschland so interessant und vielfältig.",
                    keywords = listOf("Kultur", "Traditionen", "feiern", "schmücken", "bemalten", "vielfältig"),
                    source = "Goethe"
                ))

                // TELC A2 - Speaking about shopping preferences
                lessons.add(createSprechenLesson(
                    title = "Einkaufsgewohnheiten",
                    description = "Talk about shopping habits",
                    level = level,
                    orderIndex = 22,
                    prompt = "Erzähle über deine Einkaufsgewohnheiten. Wo kaufst du am liebsten ein? Was sind deine Lieblingsläden? Sprich 2-3 Minuten.",
                    modelResponse = "Ich gehe gerne in verschiedene Geschäfte einkaufen. Für Lebensmittel gehe ich in den Supermarkt, aber auch auf den Markt. Dort kaufe ich frisches Obst und Gemüse. Für Kleidung mag ich Geschäfte in der Stadtmitte. Meine Lieblingsläden sind [Laden] und [Laden]. Sie haben gute Qualität und freundliches Personal. Manchmal kaufe ich auch online, besonders Bücher und Elektronik. Ich achte auf Preise und Qualität.",
                    keywords = listOf("Einkaufen", "Supermarkt", "Markt", "Kleidung", "Qualität", "Preise"),
                    source = "TELC"
                ))

                // ÖSD A2 - Speaking about accommodation
                lessons.add(createSprechenLesson(
                    title = "Wohnen",
                    description = "Talk about housing",
                    level = level,
                    orderIndex = 23,
                    prompt = "Beschreibe, wo und wie du wohnst. Erzähle über deine Wohnung oder dein Haus. Sprich 2-3 Minuten.",
                    modelResponse = "Ich wohne in einer schönen Wohnung in der Stadt. Die Wohnung hat drei Zimmer, Küche, Bad und Balkon. Sie ist hell und modern eingerichtet. In der Küche koche ich gerne und im Wohnzimmer sehe ich fern. Mein Schlafzimmer ist ruhig und gemütlich. Die Nachbarn sind freundlich und das Viertel ist sicher. Die Miete ist angemessen und ich fühle mich wohl hier. Manchmal denke ich über einen Umzug nach, aber im Moment bin ich zufrieden.",
                    keywords = listOf("wohnen", "Wohnung", "Zimmer", "eingerichtet", "gemütlich", "zufrieden"),
                    source = "ÖSD"
                ))

                // Goethe A2 - Speaking about music and arts
                lessons.add(createSprechenLesson(
                    title = "Musik und Kunst",
                    description = "Talk about music and arts",
                    level = level,
                    orderIndex = 24,
                    prompt = "Erzähle über deine Interessen an Musik und Kunst. Welche Musik hörst du? Gehst du ins Museum oder Theater? Sprich 2-3 Minuten.",
                    modelResponse = "Musik ist ein wichtiger Teil meines Lebens. Ich höre gerne Pop und Rock, besonders deutsche Bands. Manchmal gehe ich zu Konzerten. Kunst interessiert mich auch sehr. Ich besuche Museen und sehe mir Ausstellungen an. Vor kurzem war ich in einer Kunstausstellung mit modernen Bildern. Theater mag ich weniger, aber ich sehe gerne Filme. Musik und Kunst machen mein Leben bunter und inspirieren mich.",
                    keywords = listOf("Musik", "Kunst", "hören", "besuchen", "Theater", "inspirieren"),
                    source = "Goethe"
                ))

                // TELC A2 - Speaking about celebrations
                lessons.add(createSprechenLesson(
                    title = "Feste und Feiern",
                    description = "Talk about celebrations",
                    level = level,
                    orderIndex = 25,
                    prompt = "Erzähle über Feste und Feiern in deinem Leben. Welche Feste feierst du? Wie feierst du Geburtstage? Sprich 2-3 Minuten.",
                    modelResponse = "Ich feiere gerne Feste mit Familie und Freunden. Geburtstage sind besonders wichtig. Ich backe Kuchen und lade Freunde ein. Zu Weihnachten schmücken wir den Baum und essen traditionelle Gerichte. Ostern machen wir Eiersuchen im Garten. Silvester feiere ich mit Feuerwerk und Sekt. Manchmal organisiere ich Grillfeste im Sommer. Feste bringen Menschen zusammen und machen Spaß. Ich freue mich immer auf die nächste Feier.",
                    keywords = listOf("Feste", "feiern", "Geburtstage", "Weihnachten", "organisiere", "Spaß"),
                    source = "TELC"
                ))
            }
            
            "B1" -> {
                lessons.add(createSprechenLesson(
                    title = "Umwelt und Nachhaltigkeit",
                    description = "Talk about environment and sustainability",
                    level = level,
                    orderIndex = 1,
                    prompt = "Diskutiere über Umweltschutz und Nachhaltigkeit. Was tust du für die Umwelt? Was sollte die Gesellschaft tun? Sprich 3-4 Minuten.",
                    modelResponse = "Umweltschutz ist ein sehr wichtiges Thema. Ich versuche, umweltfreundlich zu leben, indem ich [Maßnahmen]. Die Gesellschaft sollte [Maßnahmen] und die Politik sollte [Maßnahmen]. Der Klimawandel ist eine große Herausforderung, aber wir können etwas dagegen tun. Jeder kann seinen Beitrag leisten.",
                    keywords = listOf("Umweltschutz", "Nachhaltigkeit", "Klimawandel", "Gesellschaft", "Politik", "Beitrag")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Technologie und Zukunft",
                    description = "Talk about technology and future",
                    level = level,
                    orderIndex = 2,
                    prompt = "Diskutiere über den Einfluss von Technologie auf unser Leben und die Zukunft. Welche Entwicklungen siehst du? Sprich 3-4 Minuten.",
                    modelResponse = "Technologie verändert unser Leben grundlegend. Künstliche Intelligenz wird immer wichtiger und [Auswirkungen]. Die Digitalisierung hat [Vor- und Nachteile]. In der Zukunft werden wir [Entwicklungen] sehen. Wir müssen lernen, mit diesen Veränderungen umzugehen und [Herausforderungen] bewältigen.",
                    keywords = listOf("Technologie", "Künstliche Intelligenz", "Digitalisierung", "Zukunft", "Veränderungen", "Herausforderungen")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Bildung und Lernen",
                    description = "Talk about education and learning",
                    level = level,
                    orderIndex = 3,
                    prompt = "Diskutiere über das Bildungssystem und lebenslanges Lernen. Was ist wichtig für die Zukunft? Sprich 3-4 Minuten.",
                    modelResponse = "Bildung ist der Schlüssel für die Zukunft. Das Bildungssystem sollte [Verbesserungen] und lebenslanges Lernen wird immer wichtiger. Digitale Kompetenzen sind [Bedeutung] und Schulen müssen sich anpassen. Lehrer werden zu [Rolle] und Schüler müssen [Fähigkeiten] entwickeln.",
                    keywords = listOf("Bildung", "Bildungssystem", "lebenslanges Lernen", "digitale Kompetenzen", "Lehrer", "Schüler")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Kultur und Gesellschaft",
                    description = "Talk about culture and society",
                    level = level,
                    orderIndex = 4,
                    prompt = "Diskutiere über Kultur und gesellschaftliche Entwicklungen. Welche Rolle spielt Kultur? Sprich 3-4 Minuten.",
                    modelResponse = "Kultur ist ein wichtiger Teil unserer Gesellschaft. Sie verbindet Menschen und hilft uns, [Funktionen]. Die deutsche Kultur ist [Beschreibung] und wir sollten sie [Maßnahmen]. Gesellschaftliche Entwicklungen wie [Entwicklung] beeinflussen unsere Kultur. Wir müssen [Herausforderung] bewältigen.",
                    keywords = listOf("Kultur", "Gesellschaft", "verbinden", "deutsche Kultur", "Entwicklungen", "Herausforderungen")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Arbeitswelt und Karriere",
                    description = "Talk about work world and career",
                    level = level,
                    orderIndex = 5,
                    prompt = "Diskutiere über die moderne Arbeitswelt und Karriereentwicklung. Welche Trends siehst du? Sprich 3-4 Minuten.",
                    modelResponse = "Die Arbeitswelt verändert sich schnell. Homeoffice wird [Entwicklung] und flexible Arbeitszeiten sind [Bedeutung]. Karriereentwicklung bedeutet heute [Definition] und wir müssen [Fähigkeiten] entwickeln. Die Zukunft der Arbeit wird [Vorhersage] und wir müssen uns darauf vorbereiten.",
                    keywords = listOf("Arbeitswelt", "Homeoffice", "Karriereentwicklung", "Fähigkeiten", "Zukunft", "vorbereiten")
                ))
            }
            
            "B2" -> {
                lessons.add(createSprechenLesson(
                    title = "Wissenschaft und Forschung",
                    description = "Talk about science and research",
                    level = level,
                    orderIndex = 1,
                    prompt = "Diskutiere über aktuelle wissenschaftliche Entwicklungen und deren Auswirkungen auf die Gesellschaft. Sprich 4-5 Minuten.",
                    modelResponse = "Wissenschaft und Forschung sind entscheidend für den Fortschritt. Aktuelle Entwicklungen in [Bereich] haben [Auswirkungen]. Deutschland ist ein führendes Land in der Forschung, besonders in den Bereichen Ingenieurwesen und Medizin. Die Zusammenarbeit zwischen Universitäten und Industrie ist [Bedeutung]. Wir müssen [Herausforderungen] bewältigen.",
                    keywords = listOf("Wissenschaft", "Forschung", "Entwicklungen", "Deutschland", "Zusammenarbeit", "Herausforderungen")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Globalisierung und Wirtschaft",
                    description = "Talk about globalization and economy",
                    level = level,
                    orderIndex = 2,
                    prompt = "Analysiere die Auswirkungen der Globalisierung auf Wirtschaft und Gesellschaft. Welche Chancen und Risiken siehst du? Sprich 4-5 Minuten.",
                    modelResponse = "Globalisierung hat die Weltwirtschaft grundlegend verändert. Internationale Handelsbeziehungen sind [Entwicklung] und Unternehmen müssen [Anpassungen]. Die Chancen liegen in [Vorteile], aber es gibt auch Risiken wie [Nachteile]. Nachhaltige Geschäftsmodelle werden [Bedeutung] und wir müssen [Maßnahmen] ergreifen.",
                    keywords = listOf("Globalisierung", "Weltwirtschaft", "Handelsbeziehungen", "Chancen", "Risiken", "nachhaltig")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Medien und Kommunikation",
                    description = "Talk about media and communication",                    level = level,
                    orderIndex = 3,
                    prompt = "Diskutiere über die Entwicklung der Medienlandschaft und deren Einfluss auf die Demokratie. Sprich 4-5 Minuten.",
                    modelResponse = "Die Medienlandschaft hat sich dramatisch verändert. Soziale Medien dominieren [Bereich] und traditionelle Medien kämpfen um [Herausforderung]. Fake News sind ein [Problem] und wir müssen [Maßnahmen] entwickeln. Qualitätsjournalismus ist [Bedeutung] für eine funktionierende Demokratie. Wir brauchen [Lösungen].",
                    keywords = listOf("Medienlandschaft", "soziale Medien", "Fake News", "Journalismus", "Demokratie", "Lösungen")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Kunst und Kultur",
                    description = "Talk about art and culture",
                    level = level,
                    orderIndex = 4,
                    prompt = "Analysiere die Rolle der Kunst in der modernen Gesellschaft und deren Entwicklung durch die Digitalisierung. Sprich 4-5 Minuten.",
                    modelResponse = "Kunst spielt eine wichtige Rolle in der Gesellschaft. Sie kann [Funktionen] und ist ein Spiegel unserer Zeit. Die Digitalisierung hat [Auswirkungen] auf die Kunst. Digitale Kunst ist ein [Entwicklung], aber traditionelle Kunst bleibt [Bedeutung]. Die Finanzierung der Kultur ist [Herausforderung].",
                    keywords = listOf("Kunst", "Gesellschaft", "Digitalisierung", "digitale Kunst", "Finanzierung", "Kultur")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Philosophie und Ethik",
                    description = "Talk about philosophy and ethics",
                    level = level,
                    orderIndex = 5,
                    prompt = "Diskutiere über ethische Fragen in der modernen Gesellschaft und die Rolle der Philosophie. Sprich 4-5 Minuten.",
                    modelResponse = "Philosophie und Ethik sind wichtiger denn je. In einer globalisierten Welt müssen wir [Herausforderungen] bewältigen. Ethische Fragen wie [Beispiele] erfordern [Ansätze]. Die Philosophie hilft uns, [Funktionen] und [Perspektiven] zu entwickeln. Wir brauchen [Lösungen] für die komplexen Herausforderungen unserer Zeit.",
                    keywords = listOf("Philosophie", "Ethik", "globalisiert", "Herausforderungen", "Perspektiven", "Lösungen")
                ))
            }
            
            "C1" -> {
                lessons.add(createSprechenLesson(
                    title = "Literatur und Gesellschaft",
                    description = "Talk about literature and society",
                    level = level,
                    orderIndex = 1,
                    prompt = "Analysiere die Beziehung zwischen Literatur und gesellschaftlichen Entwicklungen. Welche Rolle spielt Literatur heute? Sprich 5-6 Minuten.",
                    modelResponse = "Literatur ist ein Spiegel der Gesellschaft und kann soziale Entwicklungen [Funktionen]. Moderne deutsche Literatur beschäftigt sich mit [Themen] und reflektiert [Entwicklungen]. Die Literatur hat [Bedeutung] für das Verständnis unserer Zeit. Digitale Medien verändern [Aspekte], aber das Buch bleibt [Bedeutung].",
                    keywords = listOf("Literatur", "Gesellschaft", "Entwicklungen", "deutsche Literatur", "digitale Medien", "Buch")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Psychologie und Verhalten",
                    description = "Talk about psychology and behavior",
                    level = level,
                    orderIndex = 2,
                    prompt = "Diskutiere über den Einfluss der digitalen Welt auf menschliches Verhalten und psychische Gesundheit. Sprich 5-6 Minuten.",
                    modelResponse = "Die digitale Welt verändert unser Verhalten grundlegend. Unsere Aufmerksamkeit wird [Auswirkungen] und unsere Kommunikation [Entwicklung]. Das hat [Folgen] für unsere psychische Gesundheit. Wir müssen [Strategien] entwickeln, um [Balance] zu finden. Achtsamkeit und [Maßnahmen] sind wichtig.",
                    keywords = listOf("digitale Welt", "Verhalten", "Aufmerksamkeit", "Kommunikation", "psychische Gesundheit", "Achtsamkeit")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Soziologie und Gesellschaft",
                    description = "Talk about sociology and society",
                    level = level,
                    orderIndex = 3,
                    prompt = "Analysiere gesellschaftliche Strukturen und soziale Prozesse in der modernen Welt. Welche Trends siehst du? Sprich 5-6 Minuten.",
                    modelResponse = "Die Soziologie hilft uns, gesellschaftliche Strukturen zu verstehen. Moderne Gesellschaften sind geprägt von [Entwicklungen] und [Herausforderungen]. Soziale Ungleichheit [Entwicklung] und neue Formen der Gemeinschaft entstehen. Die Digitalisierung [Auswirkungen] und wir müssen [Lösungen] finden.",
                    keywords = listOf("Soziologie", "Gesellschaft", "Strukturen", "soziale Ungleichheit", "Digitalisierung", "Lösungen")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Linguistik und Sprache",
                    description = "Talk about linguistics and language",
                    level = level,
                    orderIndex = 4,
                    prompt = "Diskutiere über Sprachwandel und die Entwicklung der deutschen Sprache im digitalen Zeitalter. Sprich 5-6 Minuten.",
                    modelResponse = "Die deutsche Sprache verändert sich durch die Digitalisierung. Anglizismen [Entwicklung] und neue Kommunikationsformen entstehen. Die Sprachpflege muss [Herausforderungen] bewältigen und die Balance zwischen [Aspekte] finden. Mehrsprachigkeit wird [Bedeutung] und eröffnet [Möglichkeiten].",
                    keywords = listOf("Sprachwandel", "deutsche Sprache", "Digitalisierung", "Anglizismen", "Sprachpflege", "Mehrsprachigkeit")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Interkulturalität",
                    description = "Talk about interculturality",
                    level = level,
                    orderIndex = 5,
                    prompt = "Analysiere interkulturelle Beziehungen und kulturelle Hybridität in der deutschen Gesellschaft. Sprich 5-6 Minuten.",
                    modelResponse = "Interkulturalität ist ein wichtiges Thema in der deutschen Gesellschaft. Kulturelle Hybridität [Entwicklung] und neue Identitätskonstruktionen entstehen. Migration [Auswirkungen] und wir müssen [Herausforderungen] bewältigen. Interkulturelle Kompetenz ist [Bedeutung] und erfordert [Fähigkeiten].",
                    keywords = listOf("Interkulturalität", "kulturelle Hybridität", "Identität", "Migration", "interkulturelle Kompetenz", "Fähigkeiten")
                ))
            }
            
            "C2" -> {
                lessons.add(createSprechenLesson(
                    title = "Metaphysik und Ontologie",
                    description = "Talk about metaphysics and ontology",
                    level = level,
                    orderIndex = 1,
                    prompt = "Diskutiere über grundlegende metaphysische Fragen und ontologische Probleme in der modernen Philosophie. Sprich 6-7 Minuten.",
                    modelResponse = "Metaphysik fragt nach den grundlegenden Strukturen der Wirklichkeit. Das Leib-Seele-Problem ist ein [Herausforderung] und verschiedene philosophische Positionen bieten [Lösungen]. Die Frage nach der Existenz Gottes ist ein [Problem] und erfordert [Ansätze]. Ontologie untersucht [Aspekte] und hat [Bedeutung].",
                    keywords = listOf("Metaphysik", "Wirklichkeit", "Leib-Seele-Problem", "Gottesfrage", "Ontologie", "Strukturen")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Ästhetik und Kunstphilosophie",
                    description = "Talk about aesthetics and philosophy of art",
                    level = level,
                    orderIndex = 2,
                    prompt = "Analysiere ästhetische Konzepte und die Philosophie der Kunst in der modernen Gesellschaft. Sprich 6-7 Minuten.",
                    modelResponse = "Ästhetik untersucht das Wesen der Kunst und [Funktionen]. Das Konzept der Schönheit hat sich [Entwicklung] und moderne Kunst hinterfragt [Aspekte]. Die Rolle der Kunst in der Gesellschaft ist [Bedeutung] und Kunst kann [Wirkungen] haben. Kunstphilosophie fragt nach [Fragen].",
                    keywords = listOf("Ästhetik", "Kunst", "Schönheit", "moderne Kunst", "Gesellschaft", "Kunstphilosophie")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Wissenschaftstheorie",
                    description = "Talk about philosophy of science",
                    level = level,
                    orderIndex = 3,
                    prompt = "Diskutiere über wissenschaftliche Erkenntnis, Objektivität und die Rolle der Werte in der Forschung. Sprich 6-7 Minuten.",
                    modelResponse = "Wissenschaftliche Erkenntnis basiert auf [Grundlagen] und erfordert [Methoden]. Objektivität ist ein [Ideal], aber Wissenschaft ist auch von [Faktoren] beeinflusst. Werte spielen eine [Rolle] in der Forschung und beeinflussen [Aspekte]. Die Wissenschaftstheorie hilft uns, [Verständnis] zu entwickeln.",
                    keywords = listOf("wissenschaftliche Erkenntnis", "Objektivität", "Methoden", "Werte", "Forschung", "Wissenschaftstheorie")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Ethik der Technologie",
                    description = "Talk about ethics of technology",
                    level = level,
                    orderIndex = 4,
                    prompt = "Analysiere ethische Herausforderungen der künstlichen Intelligenz und der Beziehung zwischen Mensch und Maschine. Sprich 6-7 Minuten.",
                    modelResponse = "Künstliche Intelligenz wirft grundlegende ethische Fragen auf. Verantwortung, Autonomie und Gerechtigkeit sind [Herausforderungen]. Die Beziehung zwischen Mensch und Maschine erfordert [Reflexion] und wir müssen [Grenzen] definieren. Die Zukunft der Arbeit wird [Entwicklung] und erfordert [Anpassungen].",
                    keywords = listOf("künstliche Intelligenz", "Ethik", "Verantwortung", "Mensch-Maschine", "Zukunft", "Arbeit")
                ))
                
                lessons.add(createSprechenLesson(
                    title = "Philosophie der Moderne",
                    description = "Talk about modern philosophy",
                    level = level,
                    orderIndex = 5,
                    prompt = "Diskutiere über die Herausforderungen der Moderne und die Rolle der Philosophie in der Gegenwart. Sprich 6-7 Minuten.",
                    modelResponse = "Die Moderne ist geprägt von Individualisierung, Rationalisierung und Globalisierung. Diese Entwicklungen schaffen [Freiheiten] und [Probleme]. Identität ist heute [Entwicklung] und erfordert [Anpassungen]. Die Philosophie hilft uns, [Herausforderungen] zu verstehen und [Lösungen] zu entwickeln.",
                    keywords = listOf("Moderne", "Individualisierung", "Globalisierung", "Identität", "Philosophie", "Herausforderungen")
                ))
            }
        }
        
        return lessons
    }
    
    private fun createLesenLesson(
        title: String,
        description: String,
        level: String,
        orderIndex: Int,
        text: String,
        questions: List<Question>,
        vocabulary: List<VocabularyItem>,
        source: String = "Goethe"
    ): Lesson {
        return Lesson(
            title = title,
            description = description,
            level = level,
            skill = "lesen",
            content = gson.toJson(LesenContent(text, questions, vocabulary)),
            source = source,
            orderIndex = orderIndex
        )
    }
    
    private fun createHoerenLesson(
        title: String,
        description: String,
        level: String,
        orderIndex: Int,
        script: String,
        questions: List<Question>,
        source: String = "Goethe"
    ): Lesson {
        return Lesson(
            title = title,
            description = description,
            level = level,
            skill = "hoeren",
            content = gson.toJson(HoerenContent(script, questions = questions)),
            source = source,
            orderIndex = orderIndex
        )
    }
    
    private fun createSchreibenLesson(
        title: String,
        description: String,
        level: String,
        orderIndex: Int,
        prompt: String,
        minWords: Int,
        maxWords: Int,
        tips: List<String>,
        source: String = "Goethe"
    ): Lesson {
        return Lesson(
            title = title,
            description = description,
            level = level,
            skill = "schreiben",
            content = gson.toJson(SchreibenContent(prompt, minWords, maxWords, tips = tips)),
            source = source,
            orderIndex = orderIndex
        )
    }
    
    private fun createSprechenLesson(
        title: String,
        description: String,
        level: String,
        orderIndex: Int,
        prompt: String,
        modelResponse: String,
        keywords: List<String>,
        source: String = "Goethe"
    ): Lesson {
        return Lesson(
            title = title,
            description = description,
            level = level,
            skill = "sprechen",
            content = gson.toJson(SprechenContent(prompt, modelResponse, keywords = keywords)),
            source = source,
            orderIndex = orderIndex
        )
    }

    /**
     * Get appropriate illustration for a lesson based on its content and skill
     */
    private fun getIllustrationForLesson(lesson: Lesson): String? {
        val title = lesson.title.lowercase()
        val skill = lesson.skill.lowercase()
        val level = lesson.level.lowercase()

        return when {
            // Lesen illustrations
            skill == "lesen" -> {
                when {
                    title.contains("haus") || title.contains("wohnung") -> "ic_house_illustration"
                    title.contains("essen") || title.contains("restaurant") -> "ic_pretzel"
                    title.contains("arbeit") || title.contains("beruf") -> "ic_person_character"
                    else -> null // Default for lesen - no illustration
                }
            }
            // Hören illustrations
            skill == "hoeren" -> {
                when {
                    title.contains("arbeit") -> "ic_person_character"
                    else -> "ic_owl_character" // Default for hören
                }
            }
            // Schreiben illustrations
            skill == "schreiben" -> {
                when {
                    title.contains("email") || title.contains("brief") -> "ic_house_illustration"
                    title.contains("tagebuch") || title.contains("geschichte") -> "ic_person_character"
                    else -> null // Default for schreiben - no illustration
                }
            }
            // Sprechen illustrations
            skill == "sprechen" -> {
                when {
                    title.contains("essen") -> "ic_pretzel"
                    else -> "ic_person_character" // Default for sprechen
                }
            }
            // Grammar illustrations - colorful and game-like
            skill == "grammar" -> {
                when {
                    title.contains("artikel") || title.contains("geschlecht") -> {
                        // Rotate through gender illustrations
                        val genderIndex = lesson.id % 3
                        when (genderIndex) {
                            0 -> "ic_grammar_masculine"
                            1 -> "ic_grammar_feminine"
                            else -> "ic_grammar_neuter"
                        }
                    }
                    title.contains("verb") || title.contains("zeit") -> "ic_person_character"
                    title.contains("präteritum") || title.contains("perfekt") -> "ic_owl_character"
                    else -> "ic_pretzel" // Default for grammar
                }
            }
            else -> null
        }
    }

    /**
     * Get appropriate character for a lesson
     */
    private fun getCharacterForLesson(lesson: Lesson): String? {
        val skill = lesson.skill.lowercase()
        val level = lesson.level.lowercase()

        // Rotate through different characters for variety
        val lessonId = lesson.id
        val characterIndex = (lessonId % 2)

        return when (skill) {
            "lesen" -> if (characterIndex == 0) "ic_owl_character" else "ic_person_character"
            "hoeren" -> "ic_owl_character" // Owl for listening feedback
            "schreiben" -> "ic_person_character" // Person for writing feedback
            "sprechen" -> "ic_owl_character" // Owl for speaking feedback
            "grammar" -> "ic_owl_character" // Owl for grammar explanations
            else -> null
        }
    }

    /**
     * Get appropriate animation type for a lesson
     */
    private fun getAnimationForLesson(lesson: Lesson): AnimationType {
        val skill = lesson.skill.lowercase()
        val level = lesson.level.lowercase()

        return when (skill) {
            "lesen" -> AnimationType.FADE_IN
            "hoeren" -> AnimationType.CHARACTER_HAPPY
            "schreiben" -> AnimationType.PROGRESS_FILL
            "sprechen" -> AnimationType.CONFETTI
            "grammar" -> AnimationType.BOUNCE
            else -> AnimationType.NONE
        }
    }
}

