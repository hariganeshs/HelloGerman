package com.hellogerman.app.data.dictionary

import com.hellogerman.app.data.models.Definition
import com.hellogerman.app.data.models.Example

/**
 * Offline German dictionary with common words, definitions, and examples
 */
class GermanDictionary {
    
    companion object {
        
        private val COMMON_WORDS = mapOf(
            "haus" to WordEntry(
                definitions = listOf(
                    Definition("A building for human habitation", "noun", level = "A1"),
                    Definition("A dwelling place; residence", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Das Haus ist sehr groß.", "The house is very big."),
                    Example("Ich wohne in einem kleinen Haus.", "I live in a small house."),
                    Example("Mein Haus hat einen Garten.", "My house has a garden.")
                ),
                wordType = "noun",
                gender = "das"
            ),
            
            "sprechen" to WordEntry(
                definitions = listOf(
                    Definition("To communicate using words", "verb", level = "A1"),
                    Definition("To express thoughts through speech", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Ich spreche Deutsch.", "I speak German."),
                    Example("Kannst du langsamer sprechen?", "Can you speak slower?"),
                    Example("Wir sprechen über das Wetter.", "We are talking about the weather.")
                ),
                wordType = "verb"
            ),
            
            "schön" to WordEntry(
                definitions = listOf(
                    Definition("Attractive, pleasing to look at", "adjective", level = "A1"),
                    Definition("Pleasant, agreeable", "adjective", level = "A2")
                ),
                examples = listOf(
                    Example("Das ist ein schönes Bild.", "That is a beautiful picture."),
                    Example("Sie hat schöne Augen.", "She has beautiful eyes."),
                    Example("Heute ist schönes Wetter.", "Today is beautiful weather.")
                ),
                wordType = "adjective"
            ),
            
            "freund" to WordEntry(
                definitions = listOf(
                    Definition("A person you like and know well", "noun", level = "A1"),
                    Definition("A companion; someone close to you", "noun", level = "A2")
                ),
                examples = listOf(
                    Example("Er ist mein bester Freund.", "He is my best friend."),
                    Example("Ich treffe meine Freunde heute.", "I'm meeting my friends today."),
                    Example("Freunde sind wichtig.", "Friends are important.")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "lernen" to WordEntry(
                definitions = listOf(
                    Definition("To acquire knowledge or skills", "verb", level = "A1"),
                    Definition("To study or practice something", "verb", level = "A1")
                ),
                examples = listOf(
                    Example("Ich lerne Deutsch.", "I am learning German."),
                    Example("Sie lernt für die Prüfung.", "She is studying for the exam."),
                    Example("Kinder lernen schnell.", "Children learn quickly.")
                ),
                wordType = "verb"
            ),
            
            "wasser" to WordEntry(
                definitions = listOf(
                    Definition("A clear liquid essential for life", "noun", level = "A1"),
                    Definition("H2O in liquid form", "noun", level = "B1")
                ),
                examples = listOf(
                    Example("Ich trinke viel Wasser.", "I drink a lot of water."),
                    Example("Das Wasser ist kalt.", "The water is cold."),
                    Example("Pflanzen brauchen Wasser.", "Plants need water.")
                ),
                wordType = "noun",
                gender = "das"
            ),
            
            "arbeiten" to WordEntry(
                definitions = listOf(
                    Definition("To perform tasks for pay or purpose", "verb", level = "A1"),
                    Definition("To function or operate", "verb", level = "B1")
                ),
                examples = listOf(
                    Example("Ich arbeite in einem Büro.", "I work in an office."),
                    Example("Sie arbeitet sehr hart.", "She works very hard."),
                    Example("Der Computer arbeitet nicht.", "The computer is not working.")
                ),
                wordType = "verb"
            ),
            
            "gehen" to WordEntry(
                definitions = listOf(
                    Definition("To move by foot; to walk", "verb", level = "A1"),
                    Definition("To leave or depart", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Ich gehe zur Schule.", "I go to school."),
                    Example("Wir gehen spazieren.", "We are going for a walk."),
                    Example("Es geht mir gut.", "I am doing well.")
                ),
                wordType = "verb"
            ),
            
            "kommen" to WordEntry(
                definitions = listOf(
                    Definition("To move toward; to arrive", "verb", level = "A1"),
                    Definition("To originate from", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Ich komme aus Deutschland.", "I come from Germany."),
                    Example("Kommst du mit?", "Are you coming along?"),
                    Example("Der Zug kommt um 5 Uhr.", "The train comes at 5 o'clock.")
                ),
                wordType = "verb"
            ),
            
            "haben" to WordEntry(
                definitions = listOf(
                    Definition("To possess or own", "verb", level = "A1"),
                    Definition("Auxiliary verb for perfect tenses", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Ich habe ein Auto.", "I have a car."),
                    Example("Sie hat Hunger.", "She is hungry."),
                    Example("Wir haben gespielt.", "We have played.")
                ),
                wordType = "verb"
            ),
            
            "sein" to WordEntry(
                definitions = listOf(
                    Definition("To exist; to be", "verb", level = "A1"),
                    Definition("Auxiliary verb and copula", "verb", level = "A1")
                ),
                examples = listOf(
                    Example("Ich bin müde.", "I am tired."),
                    Example("Das ist mein Haus.", "That is my house."),
                    Example("Sie ist Lehrerin.", "She is a teacher.")
                ),
                wordType = "verb"
            ),
            
            "zeit" to WordEntry(
                definitions = listOf(
                    Definition("The measurement of duration", "noun", level = "A1"),
                    Definition("A specific moment or period", "noun", level = "A2")
                ),
                examples = listOf(
                    Example("Ich habe keine Zeit.", "I have no time."),
                    Example("Die Zeit vergeht schnell.", "Time passes quickly."),
                    Example("Es ist Zeit zu gehen.", "It's time to go.")
                ),
                wordType = "noun",
                gender = "die"
            ),
            
            "gut" to WordEntry(
                definitions = listOf(
                    Definition("Of high quality; positive", "adjective", level = "A1"),
                    Definition("Morally right; beneficial", "adjective", level = "A2")
                ),
                examples = listOf(
                    Example("Das Essen ist gut.", "The food is good."),
                    Example("Sie ist eine gute Freundin.", "She is a good friend."),
                    Example("Gut gemacht!", "Well done!")
                ),
                wordType = "adjective"
            ),
            
            "erreichen" to WordEntry(
                definitions = listOf(
                    Definition("To reach or arrive at a destination", "verb", level = "A2"),
                    Definition("To achieve or attain a goal", "verb", level = "B1"),
                    Definition("To contact or get in touch with someone", "verb", level = "B1")
                ),
                examples = listOf(
                    Example("Ich erreiche den Bahnhof um 8 Uhr.", "I reach the train station at 8 o'clock."),
                    Example("Sie hat ihr Ziel erreicht.", "She has achieved her goal."),
                    Example("Kannst du mich telefonisch erreichen?", "Can you reach me by phone?")
                ),
                wordType = "verb"
            ),
            
            "morgen" to WordEntry(
                definitions = listOf(
                    Definition("The day after today", "noun", level = "A1"),
                    Definition("The morning time of day", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Morgen fahre ich nach Berlin.", "Tomorrow I'm going to Berlin."),
                    Example("Jeden Morgen trinke ich Kaffee.", "Every morning I drink coffee."),
                    Example("Bis morgen!", "See you tomorrow!")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "verstehen" to WordEntry(
                definitions = listOf(
                    Definition("To comprehend or grasp the meaning", "verb", level = "A1"),
                    Definition("To have knowledge or awareness of", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Ich verstehe nicht.", "I don't understand."),
                    Example("Verstehst du Deutsch?", "Do you understand German?"),
                    Example("Sie versteht die Aufgabe.", "She understands the task.")
                ),
                wordType = "verb"
            ),
            
            "machen" to WordEntry(
                definitions = listOf(
                    Definition("To create, produce, or cause", "verb", level = "A1"),
                    Definition("To perform an action", "verb", level = "A1")
                ),
                examples = listOf(
                    Example("Ich mache Hausaufgaben.", "I'm doing homework."),
                    Example("Was machst du?", "What are you doing?"),
                    Example("Sie macht einen Kuchen.", "She is making a cake.")
                ),
                wordType = "verb"
            ),
            
            "geben" to WordEntry(
                definitions = listOf(
                    Definition("To hand over or provide", "verb", level = "A1"),
                    Definition("To offer or grant", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Kannst du mir das Buch geben?", "Can you give me the book?"),
                    Example("Ich gebe dir meine Telefonnummer.", "I'll give you my phone number."),
                    Example("Es gibt viele Möglichkeiten.", "There are many possibilities.")
                ),
                wordType = "verb"
            ),
            
            // Common adjectives
            "schön" to WordEntry(
                definitions = listOf(
                    Definition("Beautiful, attractive, pleasing to look at", "adjective", level = "A1"),
                    Definition("Pleasant, nice, agreeable", "adjective", level = "A1"),
                    Definition("Good, fine (weather, day)", "adjective", level = "A2")
                ),
                examples = listOf(
                    Example("Das ist ein schönes Bild.", "That is a beautiful picture."),
                    Example("Sie hat schöne Augen.", "She has beautiful eyes."),
                    Example("Heute ist schönes Wetter.", "Today is beautiful weather."),
                    Example("Schön, dich zu sehen!", "Nice to see you!"),
                    Example("Das war ein schöner Tag.", "That was a nice day.")
                ),
                wordType = "adjective"
            ),
            
            "groß" to WordEntry(
                definitions = listOf(
                    Definition("Large in size, big", "adjective", level = "A1"),
                    Definition("Tall in height", "adjective", level = "A1"),
                    Definition("Great, important", "adjective", level = "A2")
                ),
                examples = listOf(
                    Example("Das Haus ist sehr groß.", "The house is very big."),
                    Example("Er ist ein großer Mann.", "He is a tall man."),
                    Example("Das ist eine große Freude.", "That is a great joy.")
                ),
                wordType = "adjective"
            ),
            
            "klein" to WordEntry(
                definitions = listOf(
                    Definition("Small in size, little", "adjective", level = "A1"),
                    Definition("Short in height", "adjective", level = "A1"),
                    Definition("Young, minor", "adjective", level = "A2")
                ),
                examples = listOf(
                    Example("Ich habe ein kleines Auto.", "I have a small car."),
                    Example("Sie ist noch klein.", "She is still small/young."),
                    Example("Das ist ein kleines Problem.", "That's a small problem.")
                ),
                wordType = "adjective"
            ),
            
            "neu" to WordEntry(
                definitions = listOf(
                    Definition("Recently made or created, new", "adjective", level = "A1"),
                    Definition("Not used before, fresh", "adjective", level = "A1"),
                    Definition("Modern, latest", "adjective", level = "A2")
                ),
                examples = listOf(
                    Example("Ich habe ein neues Auto gekauft.", "I bought a new car."),
                    Example("Das ist mein neuer Freund.", "This is my new friend."),
                    Example("Hast du die neuen Nachrichten gehört?", "Have you heard the new news?")
                ),
                wordType = "adjective"
            ),
            
            "alt" to WordEntry(
                definitions = listOf(
                    Definition("Having lived for a long time, old", "adjective", level = "A1"),
                    Definition("From a past time, ancient", "adjective", level = "A1"),
                    Definition("Previous, former", "adjective", level = "A2")
                ),
                examples = listOf(
                    Example("Mein Großvater ist sehr alt.", "My grandfather is very old."),
                    Example("Das ist ein altes Buch.", "That is an old book."),
                    Example("Wo ist meine alte Jacke?", "Where is my old jacket?")
                ),
                wordType = "adjective"
            ),
            
            // Common particles and words
            "nein" to WordEntry(
                definitions = listOf(
                    Definition("No (negative response)", "particle", level = "A1"),
                    Definition("Expression of denial or refusal", "interjection", level = "A1")
                ),
                examples = listOf(
                    Example("Nein, das ist nicht richtig.", "No, that is not correct."),
                    Example("Kommst du mit? - Nein, danke.", "Are you coming along? - No, thanks."),
                    Example("Nein! Das will ich nicht!", "No! I don't want that!")
                ),
                wordType = "particle"
            ),
            
            "ja" to WordEntry(
                definitions = listOf(
                    Definition("Yes (affirmative response)", "particle", level = "A1"),
                    Definition("Expression of agreement or confirmation", "interjection", level = "A1")
                ),
                examples = listOf(
                    Example("Ja, das ist richtig.", "Yes, that is correct."),
                    Example("Kommst du mit? - Ja, gerne.", "Are you coming along? - Yes, gladly."),
                    Example("Ja, ich verstehe.", "Yes, I understand.")
                ),
                wordType = "particle"
            ),
            
            "bitte" to WordEntry(
                definitions = listOf(
                    Definition("Please (polite request)", "adverb", level = "A1"),
                    Definition("You're welcome (response to thanks)", "interjection", level = "A1"),
                    Definition("Excuse me, pardon", "interjection", level = "A2")
                ),
                examples = listOf(
                    Example("Kannst du mir bitte helfen?", "Can you please help me?"),
                    Example("Danke! - Bitte!", "Thanks! - You're welcome!"),
                    Example("Bitte schön!", "You're very welcome!")
                ),
                wordType = "adverb"
            ),
            
            "danke" to WordEntry(
                definitions = listOf(
                    Definition("Thank you, thanks", "interjection", level = "A1"),
                    Definition("Expression of gratitude", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Danke für deine Hilfe!", "Thanks for your help!"),
                    Example("Vielen Dank!", "Thank you very much!"),
                    Example("Danke schön!", "Thank you!")
                ),
                wordType = "interjection"
            ),
            
            // More common nouns
            "mann" to WordEntry(
                definitions = listOf(
                    Definition("An adult male human being", "noun", level = "A1"),
                    Definition("Husband", "noun", level = "A2"),
                    Definition("Person (gender-neutral usage)", "noun", level = "B1")
                ),
                examples = listOf(
                    Example("Der Mann ist sehr nett.", "The man is very nice."),
                    Example("Mein Mann arbeitet hier.", "My husband works here."),
                    Example("Ein alter Mann ging vorbei.", "An old man walked by.")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "frau" to WordEntry(
                definitions = listOf(
                    Definition("An adult female human being", "noun", level = "A1"),
                    Definition("Wife", "noun", level = "A2"),
                    Definition("Mrs., Ms. (title)", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Die Frau ist sehr freundlich.", "The woman is very friendly."),
                    Example("Das ist meine Frau.", "This is my wife."),
                    Example("Frau Müller ist unsere Lehrerin.", "Mrs. Müller is our teacher.")
                ),
                wordType = "noun",
                gender = "die"
            ),
            
            "kind" to WordEntry(
                definitions = listOf(
                    Definition("A young human being, child", "noun", level = "A1"),
                    Definition("Offspring, son or daughter", "noun", level = "A2")
                ),
                examples = listOf(
                    Example("Das Kind spielt im Garten.", "The child is playing in the garden."),
                    Example("Sie haben drei Kinder.", "They have three children."),
                    Example("Als Kind war ich sehr schüchtern.", "As a child I was very shy.")
                ),
                wordType = "noun",
                gender = "das"
            ),
            
            "auto" to WordEntry(
                definitions = listOf(
                    Definition("A motor vehicle, car", "noun", level = "A1"),
                    Definition("Automobile", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Mein Auto ist blau.", "My car is blue."),
                    Example("Ich fahre mit dem Auto zur Arbeit.", "I drive to work by car."),
                    Example("Das Auto ist kaputt.", "The car is broken.")
                ),
                wordType = "noun",
                gender = "das"
            ),
            
            "stadt" to WordEntry(
                definitions = listOf(
                    Definition("A large settlement, city", "noun", level = "A1"),
                    Definition("Urban area", "noun", level = "A2")
                ),
                examples = listOf(
                    Example("Berlin ist eine große Stadt.", "Berlin is a big city."),
                    Example("Ich wohne in der Stadt.", "I live in the city."),
                    Example("Die Stadt ist sehr schön.", "The city is very beautiful.")
                ),
                wordType = "noun",
                gender = "die"
            ),
            
            // More verbs
            "sehen" to WordEntry(
                definitions = listOf(
                    Definition("To perceive with the eyes, to see", "verb", level = "A1"),
                    Definition("To watch, to look at", "verb", level = "A1"),
                    Definition("To understand, to realize", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Ich sehe das Haus.", "I see the house."),
                    Example("Siehst du den Film?", "Are you watching the movie?"),
                    Example("Ich sehe das Problem.", "I see the problem.")
                ),
                wordType = "verb"
            ),
            
            "finden" to WordEntry(
                definitions = listOf(
                    Definition("To discover or locate something", "verb", level = "A1"),
                    Definition("To think or have an opinion", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Ich kann meine Schlüssel nicht finden.", "I can't find my keys."),
                    Example("Wie findest du das Buch?", "What do you think of the book?"),
                    Example("Ich finde das interessant.", "I find that interesting.")
                ),
                wordType = "verb"
            ),
            
            "wissen" to WordEntry(
                definitions = listOf(
                    Definition("To have knowledge or information about", "verb", level = "A1"),
                    Definition("To be aware of", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Ich weiß es nicht.", "I don't know."),
                    Example("Weißt du, wo er ist?", "Do you know where he is?"),
                    Example("Sie weiß viel über Geschichte.", "She knows a lot about history.")
                ),
                wordType = "verb"
            ),
            
            "denken" to WordEntry(
                definitions = listOf(
                    Definition("To use one's mind to consider or reason", "verb", level = "A1"),
                    Definition("To have an opinion or belief", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Ich denke, das ist richtig.", "I think that is correct."),
                    Example("Woran denkst du?", "What are you thinking about?"),
                    Example("Lass mich darüber denken.", "Let me think about it.")
                ),
                wordType = "verb"
            ),
            
            "können" to WordEntry(
                definitions = listOf(
                    Definition("To be able to, can", "verb", level = "A1"),
                    Definition("To have the skill or knowledge", "verb", level = "A1"),
                    Definition("To be allowed to, may", "verb", level = "A2")
                ),
                examples = listOf(
                    Example("Ich kann Deutsch sprechen.", "I can speak German."),
                    Example("Kannst du mir helfen?", "Can you help me?"),
                    Example("Du kannst jetzt gehen.", "You can go now.")
                ),
                wordType = "verb"
            ),
            
            // === COMPREHENSIVE NOUNS WITH GENDER ===
            
            // Family & People (die Familie)
            "vater" to WordEntry(
                definitions = listOf(
                    Definition("Father, dad", "noun", level = "A1"),
                    Definition("Male parent", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Mein Vater arbeitet im Büro.", "My father works in the office."),
                    Example("Der Vater spielt mit den Kindern.", "The father plays with the children."),
                    Example("Wo ist dein Vater?", "Where is your father?")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "mutter" to WordEntry(
                definitions = listOf(
                    Definition("Mother, mom", "noun", level = "A1"),
                    Definition("Female parent", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Meine Mutter kocht sehr gut.", "My mother cooks very well."),
                    Example("Die Mutter liest eine Geschichte vor.", "The mother reads a story aloud."),
                    Example("Ich rufe meine Mutter an.", "I'm calling my mother.")
                ),
                wordType = "noun",
                gender = "die"
            ),
            
            "sohn" to WordEntry(
                definitions = listOf(
                    Definition("Son", "noun", level = "A1"),
                    Definition("Male offspring", "noun", level = "A2")
                ),
                examples = listOf(
                    Example("Mein Sohn ist zehn Jahre alt.", "My son is ten years old."),
                    Example("Der Sohn hilft seinem Vater.", "The son helps his father."),
                    Example("Sie haben einen Sohn und eine Tochter.", "They have a son and a daughter.")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "tochter" to WordEntry(
                definitions = listOf(
                    Definition("Daughter", "noun", level = "A1"),
                    Definition("Female offspring", "noun", level = "A2")
                ),
                examples = listOf(
                    Example("Unsere Tochter studiert Medizin.", "Our daughter studies medicine."),
                    Example("Die Tochter tanzt sehr gern.", "The daughter loves to dance."),
                    Example("Meine Tochter ist sehr klug.", "My daughter is very smart.")
                ),
                wordType = "noun",
                gender = "die"
            ),
            
            "bruder" to WordEntry(
                definitions = listOf(
                    Definition("Brother", "noun", level = "A1"),
                    Definition("Male sibling", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Mein Bruder wohnt in Berlin.", "My brother lives in Berlin."),
                    Example("Der Bruder ist älter als ich.", "The brother is older than me."),
                    Example("Ich habe zwei Brüder.", "I have two brothers.")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "schwester" to WordEntry(
                definitions = listOf(
                    Definition("Sister", "noun", level = "A1"),
                    Definition("Female sibling", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Meine Schwester ist Lehrerin.", "My sister is a teacher."),
                    Example("Die Schwester hilft mir bei den Hausaufgaben.", "The sister helps me with homework."),
                    Example("Sie ist meine kleine Schwester.", "She is my little sister.")
                ),
                wordType = "noun",
                gender = "die"
            ),
            
            // Body parts (der Körper)
            "kopf" to WordEntry(
                definitions = listOf(
                    Definition("Head", "noun", level = "A1"),
                    Definition("The part of the body containing the brain", "noun", level = "A2")
                ),
                examples = listOf(
                    Example("Mein Kopf tut weh.", "My head hurts."),
                    Example("Er nickt mit dem Kopf.", "He nods his head."),
                    Example("Sie hat einen klugen Kopf.", "She has a smart head.")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "hand" to WordEntry(
                definitions = listOf(
                    Definition("Hand", "noun", level = "A1"),
                    Definition("The part of the body at the end of the arm", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich wasche meine Hände.", "I wash my hands."),
                    Example("Gib mir deine Hand.", "Give me your hand."),
                    Example("Sie schreibt mit der linken Hand.", "She writes with her left hand.")
                ),
                wordType = "noun",
                gender = "die"
            ),
            
            "auge" to WordEntry(
                definitions = listOf(
                    Definition("Eye", "noun", level = "A1"),
                    Definition("Organ of sight", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Sie hat blaue Augen.", "She has blue eyes."),
                    Example("Das Auge ist sehr wichtig.", "The eye is very important."),
                    Example("Ich kann es mit meinen eigenen Augen sehen.", "I can see it with my own eyes.")
                ),
                wordType = "noun",
                gender = "das"
            ),
            
            "mund" to WordEntry(
                definitions = listOf(
                    Definition("Mouth", "noun", level = "A1"),
                    Definition("Opening in the face used for eating and speaking", "noun", level = "A2")
                ),
                examples = listOf(
                    Example("Öffne deinen Mund.", "Open your mouth."),
                    Example("Er hat einen großen Mund.", "He has a big mouth."),
                    Example("Sie lächelt mit dem ganzen Mund.", "She smiles with her whole mouth.")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            // Household items (der Haushalt)
            "tisch" to WordEntry(
                definitions = listOf(
                    Definition("Table", "noun", level = "A1"),
                    Definition("Piece of furniture with a flat top", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Das Essen steht auf dem Tisch.", "The food is on the table."),
                    Example("Wir sitzen am Tisch.", "We sit at the table."),
                    Example("Der Tisch ist aus Holz.", "The table is made of wood.")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "stuhl" to WordEntry(
                definitions = listOf(
                    Definition("Chair", "noun", level = "A1"),
                    Definition("Seat for one person", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich sitze auf einem Stuhl.", "I sit on a chair."),
                    Example("Der Stuhl ist sehr bequem.", "The chair is very comfortable."),
                    Example("Stelle den Stuhl an den Tisch.", "Put the chair at the table.")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "bett" to WordEntry(
                definitions = listOf(
                    Definition("Bed", "noun", level = "A1"),
                    Definition("Piece of furniture for sleeping", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich gehe ins Bett.", "I go to bed."),
                    Example("Das Bett ist sehr weich.", "The bed is very soft."),
                    Example("Mach dein Bett!", "Make your bed!")
                ),
                wordType = "noun",
                gender = "das"
            ),
            
            "fenster" to WordEntry(
                definitions = listOf(
                    Definition("Window", "noun", level = "A1"),
                    Definition("Opening in a wall with glass", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Öffne das Fenster!", "Open the window!"),
                    Example("Ich schaue aus dem Fenster.", "I look out the window."),
                    Example("Das Fenster ist kaputt.", "The window is broken.")
                ),
                wordType = "noun",
                gender = "das"
            ),
            
            "tür" to WordEntry(
                definitions = listOf(
                    Definition("Door", "noun", level = "A1"),
                    Definition("Movable barrier for closing an entrance", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Schließe die Tür!", "Close the door!"),
                    Example("Die Tür ist offen.", "The door is open."),
                    Example("Klopf an die Tür.", "Knock on the door.")
                ),
                wordType = "noun",
                gender = "die"
            ),
            
            // Food & Drink (das Essen)
            "brot" to WordEntry(
                definitions = listOf(
                    Definition("Bread", "noun", level = "A1"),
                    Definition("Baked food made from flour and water", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich esse Brot zum Frühstück.", "I eat bread for breakfast."),
                    Example("Das Brot ist frisch.", "The bread is fresh."),
                    Example("Kaufst du Brot im Supermarkt?", "Do you buy bread at the supermarket?")
                ),
                wordType = "noun",
                gender = "das"
            ),
            
            "wasser" to WordEntry(
                definitions = listOf(
                    Definition("Water", "noun", level = "A1"),
                    Definition("Clear liquid essential for life", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich trinke viel Wasser.", "I drink a lot of water."),
                    Example("Das Wasser ist kalt.", "The water is cold."),
                    Example("Wasser ist sehr wichtig.", "Water is very important.")
                ),
                wordType = "noun",
                gender = "das"
            ),
            
            "milch" to WordEntry(
                definitions = listOf(
                    Definition("Milk", "noun", level = "A1"),
                    Definition("White liquid produced by mammals", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich trinke Milch mit Kaffee.", "I drink milk with coffee."),
                    Example("Die Milch ist sauer.", "The milk is sour."),
                    Example("Kaufe bitte Milch!", "Please buy milk!")
                ),
                wordType = "noun",
                gender = "die"
            ),
            
            "kaffee" to WordEntry(
                definitions = listOf(
                    Definition("Coffee", "noun", level = "A1"),
                    Definition("Dark beverage made from coffee beans", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich trinke jeden Morgen Kaffee.", "I drink coffee every morning."),
                    Example("Der Kaffee ist sehr stark.", "The coffee is very strong."),
                    Example("Möchtest du Kaffee oder Tee?", "Would you like coffee or tea?")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "apfel" to WordEntry(
                definitions = listOf(
                    Definition("Apple", "noun", level = "A1"),
                    Definition("Round fruit that grows on trees", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich esse einen Apfel.", "I eat an apple."),
                    Example("Der Apfel ist rot.", "The apple is red."),
                    Example("Äpfel sind gesund.", "Apples are healthy.")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            // Transportation (der Verkehr)
            "zug" to WordEntry(
                definitions = listOf(
                    Definition("Train", "noun", level = "A1"),
                    Definition("Railway vehicle", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich fahre mit dem Zug.", "I travel by train."),
                    Example("Der Zug ist pünktlich.", "The train is on time."),
                    Example("Wann kommt der nächste Zug?", "When does the next train come?")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "bus" to WordEntry(
                definitions = listOf(
                    Definition("Bus", "noun", level = "A1"),
                    Definition("Large motor vehicle for passengers", "noun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich nehme den Bus zur Arbeit.", "I take the bus to work."),
                    Example("Der Bus kommt in fünf Minuten.", "The bus comes in five minutes."),
                    Example("Im Bus ist es sehr voll.", "The bus is very crowded.")
                ),
                wordType = "noun",
                gender = "der"
            ),
            
            "fahrrad" to WordEntry(
                definitions = listOf(
                    Definition("Bicycle, bike", "noun", level = "A1"),
                    Definition("Two-wheeled vehicle powered by pedaling", "noun", level = "A2")
                ),
                examples = listOf(
                    Example("Ich fahre Fahrrad zur Schule.", "I ride a bicycle to school."),
                    Example("Mein Fahrrad ist kaputt.", "My bicycle is broken."),
                    Example("Das Fahrrad steht im Garten.", "The bicycle stands in the garden.")
                ),
                wordType = "noun",
                gender = "das"
            ),
            
            // === GERMAN PRONOUNS ===
            
            // Personal pronouns (Personalpronomen)
            "ich" to WordEntry(
                definitions = listOf(
                    Definition("I (first person singular pronoun)", "pronoun", level = "A1"),
                    Definition("The speaking person referring to themselves", "pronoun", level = "A1")
                ),
                examples = listOf(
                    Example("Ich bin müde.", "I am tired."),
                    Example("Ich komme aus Deutschland.", "I come from Germany."),
                    Example("Ich liebe dich.", "I love you.")
                ),
                wordType = "pronoun"
            ),
            
            "du" to WordEntry(
                definitions = listOf(
                    Definition("You (informal, singular)", "pronoun", level = "A1"),
                    Definition("Second person singular informal pronoun", "pronoun", level = "A1")
                ),
                examples = listOf(
                    Example("Du bist sehr nett.", "You are very nice."),
                    Example("Wo wohnst du?", "Where do you live?"),
                    Example("Kannst du mir helfen?", "Can you help me?")
                ),
                wordType = "pronoun"
            ),
            
            "er" to WordEntry(
                definitions = listOf(
                    Definition("He (masculine third person singular)", "pronoun", level = "A1"),
                    Definition("Referring to a male person or masculine noun", "pronoun", level = "A1")
                ),
                examples = listOf(
                    Example("Er ist mein Bruder.", "He is my brother."),
                    Example("Er arbeitet im Büro.", "He works in the office."),
                    Example("Wo ist er?", "Where is he?")
                ),
                wordType = "pronoun"
            ),
            
            "sie" to WordEntry(
                definitions = listOf(
                    Definition("She (feminine third person singular)", "pronoun", level = "A1"),
                    Definition("They (third person plural)", "pronoun", level = "A1"),
                    Definition("You (formal, Sie with capital S)", "pronoun", level = "A1")
                ),
                examples = listOf(
                    Example("Sie ist sehr klug.", "She is very smart."),
                    Example("Sie kommen heute.", "They are coming today."),
                    Example("Wie heißen Sie?", "What is your name? (formal)")
                ),
                wordType = "pronoun"
            ),
            
            "es" to WordEntry(
                definitions = listOf(
                    Definition("It (neuter third person singular)", "pronoun", level = "A1"),
                    Definition("Referring to neuter nouns or impersonal expressions", "pronoun", level = "A1")
                ),
                examples = listOf(
                    Example("Es ist kalt heute.", "It is cold today."),
                    Example("Das Auto? Es ist rot.", "The car? It is red."),
                    Example("Es gibt viele Möglichkeiten.", "There are many possibilities.")
                ),
                wordType = "pronoun"
            ),
            
            "wir" to WordEntry(
                definitions = listOf(
                    Definition("We (first person plural)", "pronoun", level = "A1"),
                    Definition("Including the speaker and others", "pronoun", level = "A1")
                ),
                examples = listOf(
                    Example("Wir gehen ins Kino.", "We are going to the cinema."),
                    Example("Wir sind eine Familie.", "We are a family."),
                    Example("Wir sprechen Deutsch.", "We speak German.")
                ),
                wordType = "pronoun"
            ),
            
            "ihr" to WordEntry(
                definitions = listOf(
                    Definition("You (informal plural)", "pronoun", level = "A1"),
                    Definition("Her/their (possessive)", "pronoun", level = "A1")
                ),
                examples = listOf(
                    Example("Ihr seid sehr nett.", "You (all) are very nice."),
                    Example("Wo wohnt ihr?", "Where do you (all) live?"),
                    Example("Das ist ihr Haus.", "That is her/their house.")
                ),
                wordType = "pronoun"
            ),
            
            // Possessive pronouns (Possessivpronomen)
            "mein" to WordEntry(
                definitions = listOf(
                    Definition("My, mine (possessive pronoun)", "pronoun", level = "A1"),
                    Definition("Belonging to me", "pronoun", level = "A1")
                ),
                examples = listOf(
                    Example("Das ist mein Buch.", "That is my book."),
                    Example("Meine Familie ist groß.", "My family is big."),
                    Example("Wo ist mein Schlüssel?", "Where is my key?")
                ),
                wordType = "pronoun"
            ),
            
            "dein" to WordEntry(
                definitions = listOf(
                    Definition("Your, yours (informal possessive)", "pronoun", level = "A1"),
                    Definition("Belonging to you (informal)", "pronoun", level = "A1")
                ),
                examples = listOf(
                    Example("Ist das dein Auto?", "Is that your car?"),
                    Example("Deine Schwester ist nett.", "Your sister is nice."),
                    Example("Wo ist dein Handy?", "Where is your phone?")
                ),
                wordType = "pronoun"
            ),
            
            "sein" to WordEntry(
                definitions = listOf(
                    Definition("His, its (possessive pronoun)", "pronoun", level = "A1"),
                    Definition("To be (infinitive verb)", "verb", level = "A1")
                ),
                examples = listOf(
                    Example("Das ist sein Fahrrad.", "That is his bicycle."),
                    Example("Seine Mutter ist Ärztin.", "His mother is a doctor."),
                    Example("Ich will glücklich sein.", "I want to be happy.")
                ),
                wordType = "pronoun"
            ),
            
            "unser" to WordEntry(
                definitions = listOf(
                    Definition("Our, ours (possessive pronoun)", "pronoun", level = "A1"),
                    Definition("Belonging to us", "pronoun", level = "A1")
                ),
                examples = listOf(
                    Example("Das ist unser Haus.", "That is our house."),
                    Example("Unsere Kinder spielen im Garten.", "Our children play in the garden."),
                    Example("Unser Lehrer ist sehr gut.", "Our teacher is very good.")
                ),
                wordType = "pronoun"
            ),
            
            // === GERMAN PREPOSITIONS ===
            
            "in" to WordEntry(
                definitions = listOf(
                    Definition("In, into (location/direction)", "preposition", level = "A1"),
                    Definition("Inside, within", "preposition", level = "A1")
                ),
                examples = listOf(
                    Example("Ich bin in der Schule.", "I am in the school."),
                    Example("Wir gehen in den Park.", "We go into the park."),
                    Example("In Deutschland spricht man Deutsch.", "In Germany people speak German.")
                ),
                wordType = "preposition"
            ),
            
            "auf" to WordEntry(
                definitions = listOf(
                    Definition("On, upon, onto", "preposition", level = "A1"),
                    Definition("At, to (certain places)", "preposition", level = "A2")
                ),
                examples = listOf(
                    Example("Das Buch liegt auf dem Tisch.", "The book lies on the table."),
                    Example("Ich gehe auf die Post.", "I go to the post office."),
                    Example("Auf Wiedersehen!", "Goodbye!")
                ),
                wordType = "preposition"
            ),
            
            "mit" to WordEntry(
                definitions = listOf(
                    Definition("With, by means of", "preposition", level = "A1"),
                    Definition("Together with, using", "preposition", level = "A1")
                ),
                examples = listOf(
                    Example("Ich gehe mit meinem Freund.", "I go with my friend."),
                    Example("Ich fahre mit dem Bus.", "I travel by bus."),
                    Example("Mit wem sprichst du?", "With whom are you speaking?")
                ),
                wordType = "preposition"
            ),
            
            "von" to WordEntry(
                definitions = listOf(
                    Definition("From, of, by", "preposition", level = "A1"),
                    Definition("Indicating origin or possession", "preposition", level = "A1")
                ),
                examples = listOf(
                    Example("Ich komme von der Arbeit.", "I come from work."),
                    Example("Das Buch von meinem Vater.", "The book of my father."),
                    Example("Von wem ist das?", "From whom is this?")
                ),
                wordType = "preposition"
            ),
            
            "zu" to WordEntry(
                definitions = listOf(
                    Definition("To, toward (direction)", "preposition", level = "A1"),
                    Definition("Too (adverb)", "adverb", level = "A1"),
                    Definition("At someone's place", "preposition", level = "A2")
                ),
                examples = listOf(
                    Example("Ich gehe zu meiner Oma.", "I go to my grandma's."),
                    Example("Das ist zu schwer.", "That is too difficult."),
                    Example("Kommst du zu mir?", "Are you coming to my place?")
                ),
                wordType = "preposition"
            ),
            
            "für" to WordEntry(
                definitions = listOf(
                    Definition("For, in favor of", "preposition", level = "A1"),
                    Definition("Intended for, on behalf of", "preposition", level = "A1")
                ),
                examples = listOf(
                    Example("Das Geschenk ist für dich.", "The gift is for you."),
                    Example("Ich arbeite für eine große Firma.", "I work for a big company."),
                    Example("Danke für deine Hilfe.", "Thanks for your help.")
                ),
                wordType = "preposition"
            ),
            
            "an" to WordEntry(
                definitions = listOf(
                    Definition("At, on, by (location)", "preposition", level = "A1"),
                    Definition("To, toward (direction)", "preposition", level = "A1")
                ),
                examples = listOf(
                    Example("Ich stehe an der Bushaltestelle.", "I stand at the bus stop."),
                    Example("Das Bild hängt an der Wand.", "The picture hangs on the wall."),
                    Example("Ich denke an dich.", "I think of you.")
                ),
                wordType = "preposition"
            ),
            
            "bei" to WordEntry(
                definitions = listOf(
                    Definition("At, by, near", "preposition", level = "A1"),
                    Definition("At someone's place/house", "preposition", level = "A1"),
                    Definition("During, while", "preposition", level = "A2")
                ),
                examples = listOf(
                    Example("Ich wohne bei meinen Eltern.", "I live at my parents' place."),
                    Example("Bei schlechtem Wetter bleibe ich zu Hause.", "In bad weather I stay home."),
                    Example("Das Restaurant ist bei der Bank.", "The restaurant is near the bank.")
                ),
                wordType = "preposition"
            ),
            
            "über" to WordEntry(
                definitions = listOf(
                    Definition("Over, above, across", "preposition", level = "A1"),
                    Definition("About, concerning", "preposition", level = "A1")
                ),
                examples = listOf(
                    Example("Das Flugzeug fliegt über die Stadt.", "The airplane flies over the city."),
                    Example("Wir sprechen über das Wetter.", "We talk about the weather."),
                    Example("Die Brücke geht über den Fluss.", "The bridge goes across the river.")
                ),
                wordType = "preposition"
            ),
            
            "unter" to WordEntry(
                definitions = listOf(
                    Definition("Under, beneath, below", "preposition", level = "A1"),
                    Definition("Among, between", "preposition", level = "A2")
                ),
                examples = listOf(
                    Example("Die Katze ist unter dem Tisch.", "The cat is under the table."),
                    Example("Unter Freunden spricht man offen.", "Among friends one speaks openly."),
                    Example("Das kostet unter 20 Euro.", "That costs under 20 euros.")
                ),
                wordType = "preposition"
            )
        )
        
        data class WordEntry(
            val definitions: List<Definition>,
            val examples: List<Example>,
            val wordType: String,
            val gender: String? = null
        )
        
        /**
         * Get dictionary entry for a word
         */
        fun getWordEntry(word: String): WordEntry? {
            return COMMON_WORDS[word.lowercase()]
        }
        
        /**
         * Check if we have offline data for a word
         */
        fun hasOfflineData(word: String): Boolean {
            return COMMON_WORDS.containsKey(word.lowercase())
        }
        
        /**
         * Get all available words in the offline dictionary
         */
        fun getAvailableWords(): List<String> {
            return COMMON_WORDS.keys.toList()
        }
    }
}
