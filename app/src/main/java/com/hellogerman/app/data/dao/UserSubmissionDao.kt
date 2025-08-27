package com.hellogerman.app.data.dao

import androidx.room.*
import com.hellogerman.app.data.entities.UserSubmission
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSubmissionDao {
    
    @Query("SELECT * FROM user_submissions WHERE lessonId = :lessonId ORDER BY submittedAt DESC")
    fun getSubmissionsByLesson(lessonId: Int): Flow<List<UserSubmission>>
    
    @Query("SELECT * FROM user_submissions WHERE skill = :skill ORDER BY submittedAt DESC")
    fun getSubmissionsBySkill(skill: String): Flow<List<UserSubmission>>
    
    @Query("SELECT * FROM user_submissions WHERE id = :submissionId")
    suspend fun getSubmissionById(submissionId: Int): UserSubmission?
    
    @Insert
    suspend fun insertSubmission(submission: UserSubmission): Long
    
    @Update
    suspend fun updateSubmission(submission: UserSubmission)
    
    @Delete
    suspend fun deleteSubmission(submission: UserSubmission)
    
    @Query("SELECT AVG(score) FROM user_submissions WHERE skill = :skill")
    suspend fun getAverageScoreBySkill(skill: String): Double?
    
    @Query("SELECT COUNT(*) FROM user_submissions WHERE skill = :skill")
    suspend fun getSubmissionCountBySkill(skill: String): Int
}
