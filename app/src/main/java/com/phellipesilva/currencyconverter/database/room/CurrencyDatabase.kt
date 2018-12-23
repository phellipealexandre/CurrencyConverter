package com.phellipesilva.currencyconverter.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates

@Database(entities = [CurrencyRates::class], version = 1)
@TypeConverters(MapTypeConverter::class)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun getCurrencyDAO(): CurrencyDAO
}