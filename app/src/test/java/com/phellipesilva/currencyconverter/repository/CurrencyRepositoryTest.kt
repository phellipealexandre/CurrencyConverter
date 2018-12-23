package com.phellipesilva.currencyconverter.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.phellipesilva.currencyconverter.database.CurrencyDAO
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates
import com.phellipesilva.currencyconverter.service.CurrencyRatesService
import com.phellipesilva.currencyconverter.service.RemoteCurrencyRates
import com.phellipesilva.currencyconverter.utils.RxUtils
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.After
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
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldReturnSameLiveDateAsDAOWhenRequestCurrencyRates() {
        val currencyRates = CurrencyRates(1, Currency("EUR", 100.0), mapOf())
        val expectedLiveData = MutableLiveData<CurrencyRates>()
        expectedLiveData.value = currencyRates

        `when`(currencyDAO.getCurrencyRates()).thenReturn(expectedLiveData)
        val userLiveData = currencyRepository.getCurrencyRates()

        assertThat(userLiveData).isEqualTo(expectedLiveData)
    }

    @Test
    fun shouldReturnObservableWhenRequestedCurrencyRatesFromServer() {
        val remoteCurrencyRates = RemoteCurrencyRates("base", "date", mapOf())
        val expectedCurrencyRates = CurrencyRates(1, Currency("EUR", 100.0), mapOf())
        val observable = Observable.just(remoteCurrencyRates)
        `when`(currencyRatesService.getRates("EUR")).thenReturn(observable)

        val currencyRatesObservable = currencyRepository.fetchCurrencyRates(Currency("EUR", 100.0))
        val testObserver = TestObserver<CurrencyRates>()
        currencyRatesObservable.subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue(expectedCurrencyRates)
    }

    @Test
    fun shouldPassParameterToDAOWhenSaveIsRequested() {
        val expectedCurrencyRates = CurrencyRates(1, Currency("EUR", 100.0), mapOf())

        currencyRepository.updatesDatabase(expectedCurrencyRates)

        verify(currencyDAO).saveCurrencyRates(expectedCurrencyRates)
    }

    @Test
    fun shouldPassCurrencyValueToDAOWhenUpdatingBaseCurrencyValueIsRequested() {
        val currency = Currency("EUR", 123.9)

        currencyRepository.updatesBaseCurrencyValue(currency)

        verify(currencyDAO).updateBaseCurrencyValue(123.9)
    }
}