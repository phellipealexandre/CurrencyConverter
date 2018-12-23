package com.phellipesilva.currencyconverter.dependencyInjection

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.phellipesilva.currencyconverter.database.room.CurrencyDAO
import com.phellipesilva.currencyconverter.database.room.CurrencyDatabase
import dagger.Module
import dagger.Provides

@Module
object DatabaseTestModule {

    @Provides
    @JvmStatic
    fun providesCurrencyRateDAO(context: Context): CurrencyDAO = Room.inMemoryDatabaseBuilder(context, CurrencyDatabase::class.java).build().getCurrencyDAO()

    @Provides
    @JvmStatic
    fun providesSharedPrefs(context: Context): SharedPreferences = context.getSharedPreferences("TestPrefs", Context.MODE_PRIVATE)
}