package com.hellogerman.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager

import com.hellogerman.app.data.DatabaseInitializer
import com.hellogerman.app.data.LessonContentGenerator
import com.hellogerman.app.ads.AdMobManager

class HelloGermanApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize AdMob
        AdMobManager.initialize(this)
        
        // Pre-load interstitial ad for better user experience
        AdMobManager.loadInterstitialAd(this)
        
        // Initialize WorkManager
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build()
        )
        
        // Initialize database with sample data

        // Initialize database with sample data
        DatabaseInitializer.initializeDatabase(this)

        // Initialize lesson content generator with cache manager
        LessonContentGenerator.initializeCacheManager(this)

        // Schedule daily grammar challenge worker
        val request = PeriodicWorkRequestBuilder<com.hellogerman.app.work.DailyGrammarChallengeWorker>(
            1, java.util.concurrent.TimeUnit.DAYS
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_grammar_challenge",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
