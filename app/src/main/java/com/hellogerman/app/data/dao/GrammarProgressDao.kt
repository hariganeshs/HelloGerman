package com.hellogerman.app.data.dao

import androidx.room.*
import com.hellogerman.app.data.entities.GrammarProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface GrammarProgressDao {

	@Query("SELECT * FROM grammar_progress ORDER BY level ASC, topicKey ASC")
	fun getAll(): Flow<List<GrammarProgress>>

	@Query("SELECT * FROM grammar_progress WHERE level = :level ORDER BY topicKey ASC")
	fun getByLevel(level: String): Flow<List<GrammarProgress>>

	@Query("SELECT * FROM grammar_progress WHERE topicKey = :topicKey LIMIT 1")
	suspend fun getByTopic(topicKey: String): GrammarProgress?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(progress: GrammarProgress): Long

	@Update
	suspend fun update(progress: GrammarProgress)

	@Delete
	suspend fun delete(progress: GrammarProgress)

	@Query("UPDATE grammar_progress SET points = points + :delta, lastCompleted = :timestamp WHERE topicKey = :topicKey")
	suspend fun addPoints(topicKey: String, delta: Int, timestamp: Long)

	@Query("UPDATE grammar_progress SET streak = :streak WHERE topicKey = :topicKey")
	suspend fun updateStreak(topicKey: String, streak: Int)

	@Query("UPDATE grammar_progress SET completedLessons = completedLessons + 1 WHERE topicKey = :topicKey")
	suspend fun incrementCompletedLessons(topicKey: String)

	@Query("SELECT COALESCE(SUM(points),0) FROM grammar_progress")
	fun totalPoints(): Flow<Int>

	@Query("UPDATE grammar_progress SET badgesJson = :badgesJson WHERE topicKey = :topicKey")
	suspend fun updateBadges(topicKey: String, badgesJson: String)
}


