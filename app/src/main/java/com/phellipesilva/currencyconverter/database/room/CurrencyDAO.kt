package com.phellipesilva.currencyconverter.database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates

@Dao
interface CurrencyDAO {

    @Insert(onConflict = REPLACE)
    fun saveCurrencyRates(currencyRates: CurrencyRates)

    @Query("SELECT * FROM currencyRates")
    fun getCurrencyRates(): LiveData<CurrencyRates>

    @Query("UPDATE currencyRates SET baseCurrencyValue = :currencyValue")
    fun updateBaseCurrencyValue(currencyValue: Double)
}
