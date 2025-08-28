package com.hellogerman.app.data

import com.google.gson.Gson
import com.hellogerman.app.data.entities.*

object LessonContentGenerator {
    
    private val gson = Gson()
    
    fun generateAllLessons(): List<Lesson> {
        val lessons = mutableListOf<Lesson>()
        
        // Generate lessons for each level and skill
        val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val skills = listOf("lesen", "hoeren", "schreiben", "sprechen", "grammar")
        
        levels.forEach { level ->
            skills.forEach { skill ->
                lessons.addAll(generateLessonsForSkillAndLevel(skill, level))
            }
        }
        
        return lessons
    }
    
    private fun generateLessonsForSkillAndLevel(skill: String, level: String): List<Lesson> {
        return when (skill) {
            "lesen" -> generateLesenLessons(level)
            "hoeren" -> generateHoerenLessons(level)
            "schreiben" -> generateSchreibenLessons(level)
            "sprechen" -> generateSprechenLessons(level)
            "grammar" -> generateGrammarLessons(level)
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
            
            "B2" -> {
                lessons.add(createLesenLesson(
                    title = "Globalisierung und Wirtschaft",
                    description = "Globalization and economy",
                    level = level,
                    orderIndex = 1,
                    text = "Die Globalisierung hat die Weltwirtschaft grundlegend verändert. Internationale Handelsbeziehungen sind heute komplexer denn je. Unternehmen agieren global und müssen sich an verschiedene Märkte anpassen. Gleichzeitig entstehen neue Herausforderungen durch Protektionismus und Handelskriege. Die Zukunft der globalen Wirtschaft ist ungewiss, aber die Vernetzung wird weiter zunehmen.",
                    questions = listOf(
                        Question(id = 1, question = "Was hat die Globalisierung verändert?", options = listOf("Nur lokale Märkte", "Die Weltwirtschaft", "Nur Technologie", "Nur Politik"), correctAnswer = "Die Weltwirtschaft", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 2, question = "Was müssen Unternehmen heute tun?", options = listOf("Nur lokal arbeiten", "Sich an verschiedene Märkte anpassen", "Nur exportieren", "Nur importieren"), correctAnswer = "Sich an verschiedene Märkte anpassen", correctAnswers = null, type = QuestionType.MULTIPLE_CHOICE),
                        Question(id = 3, question = "Was wird weiter zunehmen?", options = null, correctAnswer = "Vernetzung", correctAnswers = null, type = QuestionType.FILL_BLANK)
                    ),
                    vocabulary = listOf(
                        VocabularyItem("Globalisierung", "globalization", "Die Globalisierung verändert alles."),
                        VocabularyItem("Handelsbeziehungen", "trade relations", "Internationale Handelsbeziehungen."),
                        VocabularyItem("Protektionismus", "protectionism", "Protektionismus ist ein Problem.")
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
        when (level) {
            "A1" -> {
                create(
                    title = "Artikel: Bestimmt/Unbestimmt",
                    description = "der/die/das und ein/eine/ein",
                    content = GrammarContent(
                        topicKey = "a1_articles",
                        explanations = listOf(
                            "Der bestimmte Artikel: der (m.), die (f.), das (n.)",
                            "Der unbestimmte Artikel: ein (m./n.), eine (f.)"
                        ),
                        examples = listOf(
                            "der Mann, die Frau, das Kind",
                            "ein Mann, eine Frau, ein Kind"
                        ),
                        miniGames = listOf(
                            GrammarMiniGame.Match(listOf("der" to "Mann", "die" to "Blume", "das" to "Haus")),
                            GrammarMiniGame.FillBlank("___ Apfel ist rot.", "Der")
                        ),
                        quiz = listOf(
                            GrammarQuestion("Der __ Tisch ist groß.", listOf("die","der","das"), "der", 5),
                            GrammarQuestion("Ich habe __ Auto.", listOf("ein","eine","der"), "ein", 5)
                        )
                    )
                )
                create(
                    title = "Nomen: Genus",
                    description = "Regeln und Ausnahmen",
                    content = GrammarContent(
                        topicKey = "a1_nouns_gender",
                        explanations = listOf(
                            "Substantive auf -ung sind meistens feminin",
                            "-chen, -lein sind Neutrum"
                        ),
                        examples = listOf("die Zeitung, das Mädchen"),
                        miniGames = listOf(
                            GrammarMiniGame.DragDrop(
                                buckets = listOf("der","die","das"),
                                items = listOf("Junge" to "der", "Zeitung" to "die", "Mädchen" to "das")
                            )
                        ),
                        quiz = listOf(
                            GrammarQuestion("__ Zeitung", listOf("der","die","das"), "die", 5)
                        )
                    )
                )
                // ... add many more A1 topics similarly
            }
            "A2" -> {
                create(
                    title = "Kasus: Akkusativ/Dativ",
                    description = "Artikel im Akkusativ und Dativ",
                    content = GrammarContent(
                        topicKey = "a2_cases",
                        explanations = listOf("Akkusativ nach 'für', Dativ nach 'mit'"),
                        examples = listOf("Ich kaufe den Apfel.", "Ich fahre mit dem Bus."),
                        miniGames = listOf(
                            GrammarMiniGame.SentenceBuilder(
                                words = listOf("mit", "dem", "Bus", "fahre", "ich"),
                                correctOrder = listOf("ich","fahre","mit","dem","Bus")
                            )
                        ),
                        quiz = listOf(
                            GrammarQuestion("Ich gehe __ Park.", listOf("in den","im","ins"), "in den", 10)
                        )
                    )
                )
            }
            "B1" -> {
                create(
                    title = "Relativsätze",
                    description = "der/die/das, den/dem/deren usw.",
                    content = GrammarContent(
                        topicKey = "b1_relative_clauses",
                        explanations = listOf("Relativpronomen richtet sich nach Genus/Kasus"),
                        examples = listOf("Der Mann, der dort steht, ist mein Nachbar."),
                        miniGames = listOf(
                            GrammarMiniGame.FillBlank("Der Mann, __ dort steht, ...", "der")
                        ),
                        quiz = listOf(
                            GrammarQuestion("Das Buch, __ ich lese, ist spannend.", listOf("das","den","dem"), "das", 15)
                        )
                    )
                )
            }
            "B2" -> {
                create(
                    title = "Passiv",
                    description = "Vorgangspassiv/Zustandspassiv",
                    content = GrammarContent(
                        topicKey = "b2_passive",
                        explanations = listOf("werden + Partizip II", "sein + Partizip II"),
                        examples = listOf("Der Brief wird geschrieben.", "Die Tür ist geschlossen."),
                        miniGames = listOf(
                            GrammarMiniGame.Match(listOf("werden" to "Vorgang", "sein" to "Zustand"))
                        ),
                        quiz = listOf(
                            GrammarQuestion("Der Kuchen __ gebacken.", listOf("wird","ist","hat"), "wird", 15)
                        )
                    )
                )
            }
            "C1" -> {
                create(
                    title = "Konjunktiv I/II",
                    description = "Indirekte Rede und Irrealis",
                    content = GrammarContent(
                        topicKey = "c1_subjunctive",
                        explanations = listOf("Konjunktiv I in indirekter Rede", "Konjunktiv II für Irreales"),
                        examples = listOf("Er sagte, er sei krank.", "Wenn ich Zeit hätte, käme ich."),
                        miniGames = listOf(
                            GrammarMiniGame.SentenceBuilder(
                                words = listOf("er", "sei", "krank"),
                                correctOrder = listOf("er","sei","krank")
                            )
                        ),
                        quiz = listOf(
                            GrammarQuestion("Er sagte, er __ kommen.", listOf("werde","würde","sei"), "würde", 20)
                        )
                    )
                )
            }
            "C2" -> {
                create(
                    title = "Nominalstil und Partizipialkonstruktionen",
                    description = "Gehobene Satzstrukturen",
                    content = GrammarContent(
                        topicKey = "c2_nominal_style",
                        explanations = listOf("Verdichtung durch Nominalisierung"),
                        examples = listOf("Die Durchführung der Analyse erfolgte gestern."),
                        miniGames = listOf(
                            GrammarMiniGame.Match(listOf("Nominalisierung" to "Substantiv", "Verb" to "Handlung"))
                        ),
                        quiz = listOf(
                            GrammarQuestion("Die __ des Projekts ist geplant.", listOf("Durchführung","durchführen","durchgeführt"), "Durchführung", 20)
                        )
                    )
                )
            }
        }
        return lessons
    }

    data class GrammarContent(
        val topicKey: String,
        val explanations: List<String>,
        val examples: List<String>,
        val miniGames: List<GrammarMiniGame> = emptyList(),
        val quiz: List<GrammarQuestion> = emptyList()
    )

    data class GrammarQuestion(
        val question: String,
        val options: List<String>,
        val correct: String,
        val points: Int
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
                    modelResponse = "Wissenschaft und Forschung sind entscheidend für den Fortschritt. Aktuelle Entwicklungen in [Bereich] haben [Auswirkungen]. Deutschland ist ein führendes Land in der Forschung, besonders in [Bereiche]. Die Zusammenarbeit zwischen Universitäten und Industrie ist [Bedeutung]. Wir müssen [Herausforderungen] bewältigen.",
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
                    description = "Talk about media and communication",
                    level = level,
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
        vocabulary: List<VocabularyItem>
    ): Lesson {
        val content = LesenContent(text, questions, vocabulary)
        return Lesson(
            title = title,
            description = description,
            level = level,
            skill = "lesen",
            content = gson.toJson(content),
            orderIndex = orderIndex
        )
    }
    
    private fun createHoerenLesson(
        title: String,
        description: String,
        level: String,
        orderIndex: Int,
        script: String,
        questions: List<Question>
    ): Lesson {
        val content = HoerenContent(script, null, questions)
        return Lesson(
            title = title,
            description = description,
            level = level,
            skill = "hoeren",
            content = gson.toJson(content),
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
        tips: List<String>
    ): Lesson {
        val content = SchreibenContent(prompt, minWords, maxWords, 900, tips)
        return Lesson(
            title = title,
            description = description,
            level = level,
            skill = "schreiben",
            content = gson.toJson(content),
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
        keywords: List<String>
    ): Lesson {
        val content = SprechenContent(prompt, modelResponse, 120, keywords)
        return Lesson(
            title = title,
            description = description,
            level = level,
            skill = "sprechen",
            content = gson.toJson(content),
            orderIndex = orderIndex
        )
    }
}
