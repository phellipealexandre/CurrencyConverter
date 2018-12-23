package com.phellipesilva.currencyconverter.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates
import com.phellipesilva.currencyconverter.service.RemoteCurrencyRates
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencyDAORoomTest {

    private lateinit var currencyDAO: CurrencyDAO
    private lateinit var database: CurrencyDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(getApplicationContext(), CurrencyDatabase::class.java).build()
        currencyDAO = database.getCurrencyDAO()
    }

    @Test
    fun shouldReturnNullCurrencyRateWhenThereIsNothingStoredInTheDatabase() {
        val currencyRateLiveData = currencyDAO.getCurrencyRates()

        currencyRateLiveData.observeForever {
            assertThat(it).isNull()
        }
    }

    @Test
    fun shouldReceiveTheSameValueStoredWhenDatabaseWasPreviouslyEmpty() {
        val expectedCurrencyRate = CurrencyRates(1, Currency("EUR", 100.0), mapOf())
        currencyDAO.save(expectedCurrencyRate)

        currencyDAO.getCurrencyRates().observeForever {
            assertThat(it).isEqualTo(expectedCurrencyRate)
        }
    }

    @Test
    fun shouldReplaceStoreCurrencyRateWhenSavedObjetsHaveTheSameID() {
        val currencyRate = CurrencyRates(1, Currency("EUR", 100.0), mapOf())
        val expectedCurrencyRate = CurrencyRates(1, Currency("BRL", 150.0), mapOf("Key" to 1.1))

        currencyDAO.save(currencyRate)
        currencyDAO.save(expectedCurrencyRate)

        currencyDAO.getCurrencyRates().observeForever {
            assertThat(it).isEqualTo(expectedCurrencyRate)
        }
    }
}