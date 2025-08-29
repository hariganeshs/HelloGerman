package com.hellogerman.app.data.service

import android.content.Context
import androidx.work.*
import androidx.lifecycle.LiveData
import com.hellogerman.app.data.database.ComprehensiveGermanData
import com.hellogerman.app.data.database.GermanDictionaryDatabase
import com.hellogerman.app.data.database.OfflineWordEntity
import com.hellogerman.app.data.database.OfflineExampleEntity
import com.hellogerman.app.data.database.DictionaryConverters
import com.hellogerman.app.data.models.Definition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for downloading and managing vocabulary packs
 * Keeps initial app size small while providing extensibility
 */
@Singleton
class VocabularyPackService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        const val WORK_NAME_DOWNLOAD_EXTENDED = "download_extended_vocab"
        const val WORK_NAME_DOWNLOAD_SPECIALIZED = "download_specialized_vocab"
    }
    
    /**
     * Download extended vocabulary pack (B1-B2 level)
     * Adds 2000+ additional words
     */
    fun downloadExtendedVocabulary(): LiveData<WorkInfo> {
        val downloadRequest = OneTimeWorkRequestBuilder<ExtendedVocabularyWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_DOWNLOAD_EXTENDED,
            ExistingWorkPolicy.KEEP,
            downloadRequest
        )
        
        return WorkManager.getInstance(context).getWorkInfoByIdLiveData(downloadRequest.id)
    }
    
    /**
     * Download specialized vocabulary packs
     * Business, Medical, Technical German
     */
    fun downloadSpecializedVocabulary(category: VocabularyCategory): LiveData<WorkInfo> {
        val data = Data.Builder()
            .putString("category", category.name)
            .build()
        
        val downloadRequest = OneTimeWorkRequestBuilder<SpecializedVocabularyWorker>()
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_DOWNLOAD_SPECIALIZED + "_" + category.name,
            ExistingWorkPolicy.KEEP,
            downloadRequest
        )
        
        return WorkManager.getInstance(context).getWorkInfoByIdLiveData(downloadRequest.id)
    }
    
    /**
     * Get available vocabulary packs
     */
    suspend fun getAvailableVocabularyPacks(): List<VocabularyPack> {
        return listOf(
            VocabularyPack(
                id = "extended_a2_b1",
                name = "Extended A2-B1 Vocabulary",
                description = "2000+ intermediate German words",
                level = "A2-B1",
                wordCount = 2000,
                sizeMB = 5.2f,
                isInstalled = false
            ),
            VocabularyPack(
                id = "business_german",
                name = "Business German",
                description = "Professional and business terminology",
                level = "B2-C1",
                wordCount = 800,
                sizeMB = 2.1f,
                isInstalled = false
            ),
            VocabularyPack(
                id = "medical_german",
                name = "Medical German",
                description = "Healthcare and medical vocabulary",
                level = "B2-C1",
                wordCount = 600,
                sizeMB = 1.8f,
                isInstalled = false
            ),
            VocabularyPack(
                id = "technical_german",
                name = "Technical German",
                description = "Engineering and technical terms",
                level = "B2-C1",
                wordCount = 700,
                sizeMB = 2.0f,
                isInstalled = false
            )
        )
    }
    
    /**
     * Check which packs are installed
     */
    suspend fun getInstalledPacks(): List<String> {
        // Implementation would check database for installed packs
        return emptyList()
    }
}

enum class VocabularyCategory {
    BUSINESS, MEDICAL, TECHNICAL, ACADEMIC, DAILY_LIFE
}

data class VocabularyPack(
    val id: String,
    val name: String,
    val description: String,
    val level: String,
    val wordCount: Int,
    val sizeMB: Float,
    val isInstalled: Boolean
)

/**
 * Worker for downloading extended vocabulary
 */
class ExtendedVocabularyWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // In a real implementation, this would download from a server
            // For now, we'll use the extended data from our ComprehensiveGermanData
            
            setProgress(Data.Builder().putInt("progress", 10).build())
            
            // Simulate download and processing
            withContext(Dispatchers.IO) {
                val extendedWords = ComprehensiveGermanData.getExtendedGermanWords()
                
                setProgress(Data.Builder().putInt("progress", 50).build())
                
                // Convert to database entities and insert
                // (Implementation would be similar to the main population)
                
                setProgress(Data.Builder().putInt("progress", 100).build())
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

/**
 * Worker for downloading specialized vocabulary
 */
class SpecializedVocabularyWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val category = inputData.getString("category") ?: return Result.failure()
            
            setProgress(Data.Builder().putInt("progress", 10).build())
            
            // Download specialized vocabulary based on category
            withContext(Dispatchers.IO) {
                when (VocabularyCategory.valueOf(category)) {
                    VocabularyCategory.BUSINESS -> downloadBusinessGerman()
                    VocabularyCategory.MEDICAL -> downloadMedicalGerman()
                    VocabularyCategory.TECHNICAL -> downloadTechnicalGerman()
                    VocabularyCategory.ACADEMIC -> downloadAcademicGerman()
                    VocabularyCategory.DAILY_LIFE -> downloadDailyLifeGerman()
                }
            }
            
            setProgress(Data.Builder().putInt("progress", 100).build())
            Result.success()
            
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private suspend fun downloadBusinessGerman() {
        // Implementation for business German vocabulary
        setProgress(Data.Builder().putInt("progress", 30).build())
        // Simulate processing
        kotlinx.coroutines.delay(1000)
        setProgress(Data.Builder().putInt("progress", 60).build())
        kotlinx.coroutines.delay(1000)
    }
    
    private suspend fun downloadMedicalGerman() {
        // Implementation for medical German vocabulary
        setProgress(Data.Builder().putInt("progress", 40).build())
        kotlinx.coroutines.delay(1000)
    }
    
    private suspend fun downloadTechnicalGerman() {
        // Implementation for technical German vocabulary
        setProgress(Data.Builder().putInt("progress", 35).build())
        kotlinx.coroutines.delay(1000)
    }
    
    private suspend fun downloadAcademicGerman() {
        // Implementation for academic German vocabulary
        setProgress(Data.Builder().putInt("progress", 45).build())
        kotlinx.coroutines.delay(1000)
    }
    
    private suspend fun downloadDailyLifeGerman() {
        // Implementation for daily life German vocabulary
        setProgress(Data.Builder().putInt("progress", 50).build())
        kotlinx.coroutines.delay(1000)
    }
}
