package com.phellipesilva.currencyconverter.dependencyInjection

import android.content.Context

import androidx.room.Room
import com.phellipesilva.currencyconverter.database.CurrencyDAO
import com.phellipesilva.currencyconverter.database.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class DatabaseModule {

    @Provides
    @Reusable
    fun providesCurrencyRateDAO(context: Context): CurrencyDAO =
        Room.databaseBuilder(context, CurrencyDatabase::class.java, "Currency Database")
            .build()
            .getCurrencyDAO()
}