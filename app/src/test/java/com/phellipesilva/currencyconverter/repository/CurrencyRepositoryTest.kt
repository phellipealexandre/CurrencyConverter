package com.phellipesilva.currencyconverter.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.phellipesilva.currencyconverter.database.CurrencyDAO
import com.phellipesilva.currencyconverter.models.CurrencyRates
import com.phellipesilva.currencyconverter.service.CurrencyRatesService
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CurrencyRepositoryTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var currencyRepository: CurrencyRepository

    @Mock
    private lateinit var currencyRatesService: CurrencyRatesService

    @Mock
    private lateinit var currencyDAO: CurrencyDAO

    @Before
    fun setUp() {
        currencyRepository = CurrencyRepository(currencyRatesService, currencyDAO)
    }

    @Test
    fun shouldReturnSameLiveDateAsDAOWhenRequestCurrencyRates() {
        val currencyRates = CurrencyRates(1, "base", "date", mapOf())
        val expectedLiveData = MutableLiveData<CurrencyRates>()
        expectedLiveData.value = currencyRates

        `when`(currencyDAO.getCurrencyRates()).thenReturn(expectedLiveData)
        val userLiveData = currencyRepository.getCurrencyRates()

        assertThat(userLiveData).isEqualTo(expectedLiveData)
    }

    @Test
    fun shouldReturnObservableWhenRequestedCurrencyRatesFromServer() {
        val expectedCurrencyRates = CurrencyRates(1, "base", "date", mapOf())
        val observable = Observable.just(expectedCurrencyRates)
        `when`(currencyRatesService.getRates("EUR")).thenReturn(observable)

        val currencyRatesObservable = currencyRepository.fetchCurrencyRates("EUR")
        val testObserver = TestObserver<CurrencyRates>()
        currencyRatesObservable.subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue(expectedCurrencyRates)
    }

    @Test
    fun shouldPassParameterToDAOWhenSaveIsRequested() {
        val expectedCurrencyRates = CurrencyRates(1, "base", "date", mapOf())

        currencyRepository.updatesDatabase(expectedCurrencyRates)

        verify(currencyDAO).save(expectedCurrencyRates)
    }
}