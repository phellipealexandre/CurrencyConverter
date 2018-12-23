package com.phellipesilva.currencyconverter.database.entity

import androidx.room.ColumnInfo

data class Currency(
    @ColumnInfo(name = "CurrencyName") val currencyName: String,
    @ColumnInfo(name = "CurrencyValue") val currencyValue: Double
)