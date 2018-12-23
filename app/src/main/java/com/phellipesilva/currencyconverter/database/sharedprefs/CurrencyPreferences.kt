package com.phellipesilva.currencyconverter.database.sharedprefs

import android.content.SharedPreferences
import com.phellipesilva.currencyconverter.database.entity.Currency
import dagger.Reusable
import javax.inject.Inject

@Reusable
class CurrencyPreferences @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val nameKey = "currencyName"
    private val valueKey = "currencyValue"

    fun getBaseCurrencyFromPreferences(): Currency {
        val currencyName = sharedPreferences.getString(nameKey, "EUR") ?: "EUR"
        val currencyValue = sharedPreferences.getString(valueKey, "100.0") ?: "100.0"
        return Currency(currencyName, currencyValue.toDouble())
    }

    fun saveBaseCurrencyOnSharedPrefs(currency: Currency) {
        val editor = sharedPreferences.edit()
        editor.putString(nameKey, currency.currencyName)
        editor.putString(valueKey, currency.currencyValue.toString())
        editor.apply()
    }
}