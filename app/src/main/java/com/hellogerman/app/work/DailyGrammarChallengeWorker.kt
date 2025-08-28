package com.hellogerman.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hellogerman.app.data.repository.HelloGermanRepository

class DailyGrammarChallengeWorker(
	appContext: Context,
	workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

	private val repository = HelloGermanRepository(appContext)

	override suspend fun doWork(): Result {
		// In a full implementation, compute streaks and schedule a notification
		return Result.success()
	}
}


