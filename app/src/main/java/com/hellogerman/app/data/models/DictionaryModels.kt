package com.hellogerman.app.data.models

import com.google.gson.annotations.SerializedName

/**
 * Data models for MyMemory Translation API responses
 */

data class MyMemoryTranslationResponse(
    @SerializedName("responseData")
    val responseData: ResponseData,
    
    @SerializedName("responseStatus")
    val responseStatus: Int,
    
    @SerializedName("responseDetails")
    val responseDetails: String?,
    
    @SerializedName("matches")
    val matches: List<Match>?
)

data class ResponseData(
    @SerializedName("translatedText")
    val translatedText: String,
    
    @SerializedName("match")
    val match: Float
)

data class Match(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("segment")
    val segment: String,
    
    @SerializedName("translation")
    val translation: String,
    
    @SerializedName("quality")
    val quality: String?,
    
    @SerializedName("reference")
    val reference: String?,
    
    @SerializedName("usage-count")
    val usageCount: Int?,
    
    @SerializedName("subject")
    val subject: String?,
    
    @SerializedName("created-by")
    val createdBy: String?,
    
    @SerializedName("last-updated-by")
    val lastUpdatedBy: String?,
    
    @SerializedName("create-date")
    val createDate: String?,
    
    @SerializedName("last-update-date")
    val lastUpdateDate: String?,
    
    @SerializedName("match")
    val match: Float
)

/**
 * Dictionary search result for UI
 */
data class DictionarySearchResult(
    val originalWord: String,
    val translations: List<String>,
    val fromLanguage: String,
    val toLanguage: String,
    val hasResults: Boolean
)

/**
 * Dictionary search request
 */
data class DictionarySearchRequest(
    val word: String,
    val fromLang: String = "de", // German
    val toLang: String = "en",   // English
    val format: String = "json"
)
