package com.hellogerman.app.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hellogerman.app.data.models.DictionarySearchResult

/**
 * Type converter for DictionarySearchResult to store in Room database
 */
class DictionarySearchResultConverter {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromDictionarySearchResult(result: DictionarySearchResult): String {
        return gson.toJson(result)
    }
    
    @TypeConverter
    fun toDictionarySearchResult(json: String): DictionarySearchResult {
        return gson.fromJson(json, DictionarySearchResult::class.java)
    }
    
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }
    
    @TypeConverter
    fun toStringList(json: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, listType)
    }
}
