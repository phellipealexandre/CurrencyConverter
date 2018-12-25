package com.phellipesilva.currencyconverter.database.sharedprefs

import android.content.SharedPreferences
import com.phellipesilva.currencyconverter.database.entity.Currency
import dagger.Reusable
import javax.inject.Inject

@Reusable
class CurrencyPreferences @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val nameKey = "currencyName"
    private val valueKey = "currencyValue"

    fun getBaseCurrencyFromPreferences() = Currency(
        currencyName = sharedPreferences.getString(nameKey, "EUR") ?: "EUR",
        currencyValue = (sharedPreferences.getString(valueKey, "100.0") ?: "100.0").toDouble()
    )

    fun saveBaseCurrencyOnSharedPrefs(currency: Currency) = with(sharedPreferences.edit()) {
        putString(nameKey, currency.currencyName)
        putString(valueKey, currency.currencyValue.toString())
        apply()
    }
}