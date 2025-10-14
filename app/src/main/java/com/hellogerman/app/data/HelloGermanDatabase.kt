package com.hellogerman.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hellogerman.app.data.dao.*
import com.hellogerman.app.data.entities.*

@Database(
    entities = [
        UserProgress::class,
        Lesson::class,
        UserSubmission::class,
        GrammarProgress::class,
        Achievement::class,
        UserVocabulary::class,
        DictionaryCache::class,
        DictionaryEntry::class,
        DictionaryVectorEntry::class
    ],
    version = 18,
    exportSchema = false
)
abstract class HelloGermanDatabase : RoomDatabase() {
    
    abstract fun userProgressDao(): UserProgressDao
    abstract fun lessonDao(): LessonDao
    abstract fun userSubmissionDao(): UserSubmissionDao
    abstract fun grammarProgressDao(): GrammarProgressDao
    abstract fun achievementDao(): AchievementDao
    abstract fun dictionaryCacheDao(): DictionaryCacheDao
    abstract fun userVocabularyDao(): UserVocabularyDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun dictionaryVectorDao(): DictionaryVectorDao
    
    companion object {
        @Volatile
        private var INSTANCE: HelloGermanDatabase? = null
        
        fun getDatabase(context: Context): HelloGermanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HelloGermanDatabase::class.java,
                    "hello_german_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `grammar_progress` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`topicKey` TEXT NOT NULL, " +
                            "`level` TEXT NOT NULL, " +
                            "`points` INTEGER NOT NULL, " +
                            "`badgesJson` TEXT NOT NULL, " +
                            "`streak` INTEGER NOT NULL, " +
                            "`lastCompleted` INTEGER NOT NULL, " +
                            "`completedLessons` INTEGER NOT NULL, " +
                            "`totalLessons` INTEGER NOT NULL)"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Delete old grammar lessons with placeholder content to force regeneration
                database.execSQL("DELETE FROM lessons WHERE skill = 'grammar'")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create achievements table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `achievements` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `icon` TEXT NOT NULL,
                        `points` INTEGER NOT NULL,
                        `category` TEXT NOT NULL,
                        `rarity` TEXT NOT NULL,
                        `unlockedAt` INTEGER NOT NULL,
                        `isUnlocked` INTEGER NOT NULL,
                        `progress` INTEGER NOT NULL,
                        `maxProgress` INTEGER NOT NULL
                    )
                """)
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new gamification fields to user_progress table
                database.execSQL("ALTER TABLE user_progress ADD COLUMN grammarScore INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE user_progress ADD COLUMN totalXP INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE user_progress ADD COLUMN coins INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE user_progress ADD COLUMN perfectLessons INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE user_progress ADD COLUMN dictionaryUsage INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE user_progress ADD COLUMN weeklyGoalProgress INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE user_progress ADD COLUMN monthlyGoalProgress INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add source column to lessons table for certificate tracking
                database.execSQL("ALTER TABLE lessons ADD COLUMN source TEXT NOT NULL DEFAULT 'Goethe'")
                // Add showEnglishExplanations column to user_progress table
                database.execSQL("ALTER TABLE user_progress ADD COLUMN showEnglishExplanations INTEGER NOT NULL DEFAULT 1")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add visual enhancement columns to lessons table
                database.execSQL("ALTER TABLE lessons ADD COLUMN illustrationResId TEXT")
                database.execSQL("ALTER TABLE lessons ADD COLUMN characterResId TEXT")
                database.execSQL("ALTER TABLE lessons ADD COLUMN animationType TEXT NOT NULL DEFAULT 'NONE'")
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Ensure database is ready for expanded content
                // Add any missing columns if needed
                try {
                    // Check if source column exists, add if not
                    database.execSQL("ALTER TABLE lessons ADD COLUMN source TEXT NOT NULL DEFAULT 'Goethe'")
                } catch (e: Exception) {
                    // Column might already exist, ignore
                }

                try {
                    // Check if showEnglishExplanations column exists, add if not
                    database.execSQL("ALTER TABLE user_progress ADD COLUMN showEnglishExplanations INTEGER NOT NULL DEFAULT 1")
                } catch (e: Exception) {
                    // Column might already exist, ignore
                }

                try {
                    // Check if illustration columns exist, add if not
                    database.execSQL("ALTER TABLE lessons ADD COLUMN illustrationResId TEXT")
                    database.execSQL("ALTER TABLE lessons ADD COLUMN characterResId TEXT")
                    database.execSQL("ALTER TABLE lessons ADD COLUMN animationType TEXT NOT NULL DEFAULT 'NONE'")
                } catch (e: Exception) {
                    // Columns might already exist, ignore
                }

                // Clear lessons to force repopulation with the latest expanded content
                database.execSQL("DELETE FROM lessons")
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add selectedTheme column to user_progress table for theme customization
                database.execSQL("ALTER TABLE user_progress ADD COLUMN selectedTheme TEXT NOT NULL DEFAULT 'default'")
            }
        }

        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add tutorialCompleted column to user_progress table for tutorial tracking
                database.execSQL("ALTER TABLE user_progress ADD COLUMN tutorialCompleted INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Clear lessons to force repopulation with expanded B1 content
                database.execSQL("DELETE FROM lessons")
            }
        }

        private val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Clear lessons to force repopulation with new B1 reading lessons
                database.execSQL("DELETE FROM lessons")
            }
        }

        private val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create dictionary cache table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `dictionary_cache` (
                        `word` TEXT NOT NULL PRIMARY KEY,
                        `fromLanguage` TEXT NOT NULL,
                        `toLanguage` TEXT NOT NULL,
                        `searchResult` TEXT NOT NULL,
                        `sources` TEXT NOT NULL,
                        `fetchedAt` INTEGER NOT NULL,
                        `expiresAt` INTEGER NOT NULL,
                        `cacheVersion` INTEGER NOT NULL
                    )
                """)
            }
        }

        private val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create user vocabulary table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `user_vocabulary` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `word` TEXT NOT NULL,
                        `translation` TEXT NOT NULL,
                        `gender` TEXT,
                        `level` TEXT,
                        `category` TEXT,
                        `notes` TEXT,
                        `addedAt` INTEGER NOT NULL,
                        `lastReviewed` INTEGER,
                        `reviewCount` INTEGER NOT NULL,
                        `masteryLevel` INTEGER NOT NULL,
                        `isFavorite` INTEGER NOT NULL,
                        `source` TEXT NOT NULL
                    )
                """)
            }
        }

        private val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create Leo dictionary cache table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `leo_dictionary_cache` (
                        `word` TEXT NOT NULL PRIMARY KEY,
                        `language` TEXT NOT NULL,
                        `searchResult` TEXT NOT NULL,
                        `sources` TEXT NOT NULL,
                        `fetchedAt` INTEGER NOT NULL,
                        `expiresAt` INTEGER NOT NULL,
                        `cacheVersion` INTEGER NOT NULL
                    )
                """)
            }
        }

        private val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Drop old dictionary tables if they exist
                database.execSQL("DROP TABLE IF EXISTS extracted_dictionary_entries")
                database.execSQL("DROP TABLE IF EXISTS leo_dictionary_cache")
                
                // Create new comprehensive dictionary_entries table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `dictionary_entries` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `english_word` TEXT NOT NULL,
                        `german_word` TEXT NOT NULL,
                        `word_type` TEXT,
                        `gender` TEXT,
                        `plural_form` TEXT,
                        `past_tense` TEXT,
                        `past_participle` TEXT,
                        `auxiliary_verb` TEXT,
                        `is_irregular` INTEGER NOT NULL,
                        `is_separable` INTEGER NOT NULL,
                        `comparative` TEXT,
                        `superlative` TEXT,
                        `additional_translations` TEXT NOT NULL,
                        `examples` TEXT NOT NULL,
                        `pronunciation_ipa` TEXT,
                        `usage_level` TEXT,
                        `domain` TEXT,
                        `raw_entry` TEXT NOT NULL,
                        `english_normalized` TEXT NOT NULL,
                        `german_normalized` TEXT NOT NULL,
                        `word_length` INTEGER NOT NULL,
                        `source` TEXT NOT NULL,
                        `import_date` INTEGER NOT NULL,
                        `import_version` INTEGER NOT NULL
                    )
                """)
                
                // Create indexes for efficient search
                database.execSQL("CREATE INDEX IF NOT EXISTS `idx_english_search` ON `dictionary_entries` (`english_normalized`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `idx_german_search` ON `dictionary_entries` (`german_normalized`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `idx_word_type` ON `dictionary_entries` (`word_type`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `idx_gender` ON `dictionary_entries` (`gender`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `idx_english_prefix` ON `dictionary_entries` (`english_normalized` COLLATE NOCASE)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `idx_german_prefix` ON `dictionary_entries` (`german_normalized` COLLATE NOCASE)")
            }
        }

        private val MIGRATION_17_18 = object : Migration(17, 18) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create dictionary vectors table for semantic search
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `dictionary_vectors` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `entry_id` INTEGER NOT NULL,
                        `combined_embedding` BLOB NOT NULL,
                        `german_embedding` BLOB NOT NULL,
                        `english_embedding` BLOB NOT NULL,
                        `has_examples` INTEGER NOT NULL,
                        `has_gender` INTEGER NOT NULL,
                        `word_type` TEXT,
                        `gender` TEXT,
                        `created_at` INTEGER NOT NULL,
                        FOREIGN KEY(`entry_id`) REFERENCES `dictionary_entries`(`id`) ON DELETE CASCADE
                    )
                """)
                
                // Create unique index on entry_id (Room's default naming: index_{table}_{column})
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_dictionary_vectors_entry_id` ON `dictionary_vectors` (`entry_id`)")
            }
        }
    }
}
