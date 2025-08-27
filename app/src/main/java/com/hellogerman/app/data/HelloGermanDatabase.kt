package com.hellogerman.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hellogerman.app.data.dao.LessonDao
import com.hellogerman.app.data.dao.UserProgressDao
import com.hellogerman.app.data.dao.UserSubmissionDao
import com.hellogerman.app.data.entities.Lesson
import com.hellogerman.app.data.entities.UserProgress
import com.hellogerman.app.data.entities.UserSubmission

@Database(
    entities = [
        UserProgress::class,
        Lesson::class,
        UserSubmission::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HelloGermanDatabase : RoomDatabase() {
    
    abstract fun userProgressDao(): UserProgressDao
    abstract fun lessonDao(): LessonDao
    abstract fun userSubmissionDao(): UserSubmissionDao
    
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
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
