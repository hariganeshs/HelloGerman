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
        UserLevel::class,
        DailyChallenge::class,
        UserStats::class
    ],
    version = 4,
    exportSchema = false
)
abstract class HelloGermanDatabase : RoomDatabase() {
    
    abstract fun userProgressDao(): UserProgressDao
    abstract fun lessonDao(): LessonDao
    abstract fun userSubmissionDao(): UserSubmissionDao
    abstract fun grammarProgressDao(): GrammarProgressDao
    abstract fun achievementDao(): AchievementDao
    abstract fun userLevelDao(): UserLevelDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun userStatsDao(): UserStatsDao
    
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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
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
                
                // Create user_level table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `user_level` (
                        `id` INTEGER NOT NULL PRIMARY KEY,
                        `level` INTEGER NOT NULL,
                        `totalXP` INTEGER NOT NULL,
                        `currentLevelXP` INTEGER NOT NULL,
                        `nextLevelXP` INTEGER NOT NULL,
                        `title` TEXT NOT NULL,
                        `prestige` INTEGER NOT NULL
                    )
                """)
                
                // Create daily_challenges table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `daily_challenges` (
                        `date` TEXT NOT NULL PRIMARY KEY,
                        `challengeType` TEXT NOT NULL,
                        `targetValue` INTEGER NOT NULL,
                        `currentProgress` INTEGER NOT NULL,
                        `isCompleted` INTEGER NOT NULL,
                        `rewardXP` INTEGER NOT NULL,
                        `rewardCoins` INTEGER NOT NULL
                    )
                """)
                
                // Create user_stats table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `user_stats` (
                        `id` INTEGER NOT NULL PRIMARY KEY,
                        `totalLessonsCompleted` INTEGER NOT NULL,
                        `totalQuizzesCompleted` INTEGER NOT NULL,
                        `totalTimeSpent` INTEGER NOT NULL,
                        `averageAccuracy` REAL NOT NULL,
                        `currentStreak` INTEGER NOT NULL,
                        `longestStreak` INTEGER NOT NULL,
                        `totalCoins` INTEGER NOT NULL,
                        `totalPoints` INTEGER NOT NULL,
                        `perfectQuizzes` INTEGER NOT NULL,
                        `fastCompletions` INTEGER NOT NULL,
                        `lastActiveDate` INTEGER NOT NULL
                    )
                """)
            }
        }
    }
}
