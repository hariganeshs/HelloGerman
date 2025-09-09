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
        Achievement::class
    ],
    version = 11,
    exportSchema = false
)
abstract class HelloGermanDatabase : RoomDatabase() {
    
    abstract fun userProgressDao(): UserProgressDao
    abstract fun lessonDao(): LessonDao
    abstract fun userSubmissionDao(): UserSubmissionDao
    abstract fun grammarProgressDao(): GrammarProgressDao
    abstract fun achievementDao(): AchievementDao
    
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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)
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
    }
}
