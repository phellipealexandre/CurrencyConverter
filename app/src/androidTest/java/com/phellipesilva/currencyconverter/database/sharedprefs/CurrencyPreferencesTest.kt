package com.phellipesilva.currencyconverter.database.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.phellipesilva.currencyconverter.database.entity.Currency
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencyPreferencesTest {

    private lateinit var currencyPreferences: CurrencyPreferences
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        sharedPreferences = getApplicationContext<Context>().getSharedPreferences("TestPrefs", Context.MODE_PRIVATE)
        currencyPreferences = CurrencyPreferences(sharedPreferences)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun shouldReturnEURCurrencyWith100valueWhenNothingIsStoredOnPreferences() {
        val currency = Currency("EUR", 100.0)

        assertThat(currencyPreferences.getBaseCurrencyFromPreferences()).isEqualTo(currency)
    }

    @Test
    fun shouldSaveCurrencyInSharedPreferencesSuccessfully() {
        val currency = Currency("EUR", 100.0)

        currencyPreferences.saveBaseCurrencyOnSharedPrefs(currency)

        assertThat(currencyPreferences.getBaseCurrencyFromPreferences()).isEqualTo(currency)
    }

    @Test
    fun shouldOverwriteCurrencyInSharedPreferencesSuccessfully() {
        val currency = Currency("EUR", 100.0)
        val newCurrency = Currency("BRL", 120.0)

        currencyPreferences.saveBaseCurrencyOnSharedPrefs(currency)
        currencyPreferences.saveBaseCurrencyOnSharedPrefs(newCurrency)

        assertThat(currencyPreferences.getBaseCurrencyFromPreferences()).isEqualTo(newCurrency)
    }
}
