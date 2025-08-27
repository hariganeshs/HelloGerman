package com.hellogerman.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager

import com.hellogerman.app.data.DatabaseInitializer
import com.hellogerman.app.ads.AdMobManager

class HelloGermanApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize AdMob
        AdMobManager.initialize(this)
        
        // Initialize WorkManager
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build()
        )
        
        // Initialize database with sample data
        DatabaseInitializer.initializeDatabase(this)
    }
}
