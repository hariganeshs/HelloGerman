package com.hellogerman.app.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hellogerman.app.data.models.LeoDictionarySearchResult

/**
 * Type converter for LeoDictionarySearchResult to store in Room database
 */
class LeoDictionarySearchResultConverter {

    private val gson: Gson = GsonBuilder()
        .create()

    @TypeConverter
    fun fromLeoDictionarySearchResult(result: LeoDictionarySearchResult): String {
        return gson.toJson(result)
    }

    @TypeConverter
    fun toLeoDictionarySearchResult(json: String): LeoDictionarySearchResult {
        return gson.fromJson(json, LeoDictionarySearchResult::class.java)
    }
}