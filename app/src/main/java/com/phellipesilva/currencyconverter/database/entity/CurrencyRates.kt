package com.phellipesilva.currencyconverter.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyRates(
    @PrimaryKey val id: Int = 1,
    @Embedded val base: Currency,
    val rates: Map<String, Double>
)