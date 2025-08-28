package com.hellogerman.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hellogerman.app.data.dao.GrammarProgressDao
import com.hellogerman.app.data.dao.LessonDao
import com.hellogerman.app.data.dao.UserProgressDao
import com.hellogerman.app.data.dao.UserSubmissionDao
import com.hellogerman.app.data.entities.Lesson
import com.hellogerman.app.data.entities.GrammarProgress
import com.hellogerman.app.data.entities.UserProgress
import com.hellogerman.app.data.entities.UserSubmission

@Database(
    entities = [
        UserProgress::class,
        Lesson::class,
        UserSubmission::class,
        GrammarProgress::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HelloGermanDatabase : RoomDatabase() {
    
    abstract fun userProgressDao(): UserProgressDao
    abstract fun lessonDao(): LessonDao
    abstract fun userSubmissionDao(): UserSubmissionDao
    abstract fun grammarProgressDao(): GrammarProgressDao
    
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
                .addMigrations(MIGRATION_1_2)
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
    }
}
