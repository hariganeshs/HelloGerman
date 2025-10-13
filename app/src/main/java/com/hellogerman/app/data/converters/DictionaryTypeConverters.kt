package com.hellogerman.app.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hellogerman.app.data.entities.DictionaryExample
import com.hellogerman.app.data.entities.GermanGender
import com.hellogerman.app.data.entities.WordType

/**
 * Room TypeConverters for complex dictionary data types
 * 
 * These converters allow Room to store complex types (lists, enums, custom objects)
 * in SQLite by converting them to/from primitive types (strings, integers).
 */
class DictionaryTypeConverters {
    
    private val gson = Gson()
    
    // WordType converters
    @TypeConverter
    fun fromWordType(value: WordType?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toWordType(value: String?): WordType? {
        return value?.let { 
            try {
                WordType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                WordType.UNKNOWN
            }
        }
    }
    
    // GermanGender converters
    @TypeConverter
    fun fromGermanGender(value: GermanGender?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toGermanGender(value: String?): GermanGender? {
        return value?.let {
            try {
                GermanGender.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
    
    // List<String> converters (for additional translations)
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // List<DictionaryExample> converters
    @TypeConverter
    fun fromExampleList(value: List<DictionaryExample>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toExampleList(value: String?): List<DictionaryExample>? {
        if (value.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<DictionaryExample>>() {}.type
        return try {
            gson.fromJson(value, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

