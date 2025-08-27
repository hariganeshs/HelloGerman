package com.hellogerman.app.data.dao

import androidx.room.*
import com.hellogerman.app.data.entities.Lesson
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    
    @Query("SELECT * FROM lessons WHERE skill = :skill AND level = :level ORDER BY orderIndex ASC")
    fun getLessonsBySkillAndLevel(skill: String, level: String): Flow<List<Lesson>>
    
    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    suspend fun getLessonById(lessonId: Int): Lesson?
    
    @Query("SELECT * FROM lessons WHERE skill = :skill ORDER BY level ASC, orderIndex ASC")
    fun getAllLessonsBySkill(skill: String): Flow<List<Lesson>>
    
    @Query("SELECT * FROM lessons")
    suspend fun getAllLessons(): List<Lesson>
    
    @Query("SELECT COUNT(*) FROM lessons WHERE skill = :skill AND level = :level AND isCompleted = 1")
    suspend fun getCompletedLessonsCount(skill: String, level: String): Int
    
    @Query("SELECT COUNT(*) FROM lessons WHERE skill = :skill AND level = :level")
    suspend fun getTotalLessonsCount(skill: String, level: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: Lesson)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<Lesson>)
    
    @Update
    suspend fun updateLesson(lesson: Lesson)
    
    @Query("UPDATE lessons SET isCompleted = :completed, score = :score, timeSpent = :timeSpent WHERE id = :lessonId")
    suspend fun updateLessonProgress(lessonId: Int, completed: Boolean, score: Int, timeSpent: Int)
    
    @Query("SELECT AVG(score) FROM lessons WHERE skill = :skill AND level = :level AND isCompleted = 1")
    suspend fun getAverageScore(skill: String, level: String): Double?
    
    @Query("DELETE FROM lessons")
    suspend fun deleteAllLessons()
}
