package com.phellipesilva.currencyconverter.database.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates
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
        currencyDAO.saveCurrencyRates(expectedCurrencyRate)

        currencyDAO.getCurrencyRates().observeForever {
            assertThat(it).isEqualTo(expectedCurrencyRate)
        }
    }

    @Test
    fun shouldReplaceStoreCurrencyRateWhenSavedObjetsHaveTheSameID() {
        val currencyRate = CurrencyRates(1, Currency("EUR", 100.0), mapOf())
        val expectedCurrencyRate = CurrencyRates(1, Currency("BRL", 150.0), mapOf("Key" to 1.1))

        currencyDAO.saveCurrencyRates(currencyRate)
        currencyDAO.saveCurrencyRates(expectedCurrencyRate)

        currencyDAO.getCurrencyRates().observeForever {
            assertThat(it).isEqualTo(expectedCurrencyRate)
        }
    }

    @Test
    fun shouldUpdateCurrencyBaseRateValueWhenObjectIsStored() {
        val currencyRate = CurrencyRates(1, Currency("EUR", 100.0), mapOf("Key" to 1.1))

        currencyDAO.saveCurrencyRates(currencyRate)
        currencyDAO.updateBaseCurrencyValue(200.0)

        currencyDAO.getCurrencyRates().observeForever {
            assertThat(it.base).isEqualTo(Currency("EUR", 200.0))
            assertThat(it.rates).isEqualTo(mapOf("Key" to 1.1))
        }
    }

    @Test
    fun shouldDoNothingWhenTryingToUpdateBaseRateValueOnEmptyDatabase() {
        currencyDAO.updateBaseCurrencyValue(200.0)

        currencyDAO.getCurrencyRates().observeForever {
            assertThat(it).isNull()
        }
    }
}