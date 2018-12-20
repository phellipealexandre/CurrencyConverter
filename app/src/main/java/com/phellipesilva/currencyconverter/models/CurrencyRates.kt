package com.phellipesilva.currencyconverter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyRates(
    @PrimaryKey val id: Int = 1,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)