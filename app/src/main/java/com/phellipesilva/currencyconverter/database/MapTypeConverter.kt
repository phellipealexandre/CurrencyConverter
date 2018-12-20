package com.phellipesilva.currencyconverter.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapTypeConverter {

    @TypeConverter
    fun fromMapToString(map: Map<String, Double>): String {
        val type = object : TypeToken<Map<String, Double>>() {}.type
        return Gson().toJson(map, type)
    }

    @TypeConverter
    fun fromStringToMap(mapString: String): Map<String, Double> {
        val type = object : TypeToken<Map<String, Double>>() {}.type
        return Gson().fromJson(mapString, type)
    }
}