package com.hellogerman.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hellogerman.app.data.entities.UserProgress
import com.hellogerman.app.data.repository.HelloGermanRepository
import com.hellogerman.app.gamification.AchievementManager
import kotlinx.coroutines.flow.firstOrNull

/**
 * Daily Maintenance Worker
 * Handles streak calculation, achievement checking, and daily challenge generation
 */
class DailyGrammarChallengeWorker(
	appContext: Context,
	workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

	private val repository = HelloGermanRepository(appContext)

	override suspend fun doWork(): Result {
		try {
			// Get current user progress
			val userProgress = repository.getUserProgress().firstOrNull()

			userProgress?.let { progress ->
				val currentTime = System.currentTimeMillis()
				val lastStudyTime = progress.lastStudyDate
				val daysSinceLastStudy = calculateDaysDifference(lastStudyTime, currentTime)

				// Update streak based on study activity
				val updatedProgress = when {
					daysSinceLastStudy == 0 -> {
						// Studied today, maintain streak
						progress
					}
					daysSinceLastStudy == 1 -> {
						// Consecutive day: increment streak and update longest if needed
						val newStreak = progress.currentStreak + 1
						progress.copy(
							currentStreak = newStreak,
							longestStreak = maxOf(progress.longestStreak, newStreak)
						)
					}
					else -> {
						// Streak broken, reset to 0 and preserve longest
						progress.copy(
							currentStreak = 0,
							longestStreak = maxOf(progress.longestStreak, progress.currentStreak)
						)
					}
				}

				// Check for new achievements
				val newAchievements = AchievementManager.checkAchievements(updatedProgress, 0)

				// Update progress with any achievement rewards
				var finalProgress = updatedProgress
				newAchievements.forEach { achievement ->
					finalProgress = finalProgress.copy(
						totalXP = finalProgress.totalXP + achievement.rewardXP,
						coins = finalProgress.coins + achievement.rewardCoins
					)
				}

				// Save updated progress
				repository.updateUserProgress(finalProgress)
			}

			return Result.success()
		} catch (e: Exception) {
			return Result.failure()
		}
	}

	private fun calculateDaysDifference(time1: Long, time2: Long): Int {
		val diffInMillis = time2 - time1
		return (diffInMillis / (24 * 60 * 60 * 1000)).toInt()
	}
}

/**
 * Achievement Check Worker
 * Periodically checks for new achievements based on user progress
 */
class AchievementCheckWorker(
	appContext: Context,
	workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

	private val repository = HelloGermanRepository(appContext)

	override suspend fun doWork(): Result {
		try {
			val userProgress = repository.getUserProgress().firstOrNull()

			userProgress?.let { progress ->
				// Check for new achievements
				val newAchievements = AchievementManager.checkAchievements(progress, 0)

				// Award achievement rewards
				if (newAchievements.isNotEmpty()) {
					var updatedProgress = progress
					newAchievements.forEach { achievement ->
						updatedProgress = updatedProgress.copy(
							totalXP = updatedProgress.totalXP + achievement.rewardXP,
							coins = updatedProgress.coins + achievement.rewardCoins
						)
					}
					repository.updateUserProgress(updatedProgress)
				}
			}

			return Result.success()
		} catch (e: Exception) {
			return Result.failure()
		}
	}
}