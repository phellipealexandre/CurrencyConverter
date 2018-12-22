package com.phellipesilva.currencyconverter.view.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.phellipesilva.currencyconverter.models.CurrencyRates
import com.phellipesilva.currencyconverter.models.Rate
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import com.phellipesilva.currencyconverter.utils.RxUtils
import com.phellipesilva.currencyconverter.view.state.ViewState
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class CurrencyConverterViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: CurrencyRepository

    private lateinit var currencyConverterViewModel: CurrencyConverterViewModel
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        currencyConverterViewModel = CurrencyConverterViewModel(repository)
        testScheduler = TestScheduler()
        RxUtils.overridesEnvironmentToCustomScheduler(testScheduler)
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldNotCallCurrencyRatesServiceBeforeTheFirstSecond() {
        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(0, TimeUnit.SECONDS)

        verify(repository, never()).fetchCurrencyRates()
    }

    @Test
    fun shouldCallCurrencyRatesServiceAfterTheFirstSecond() {
        val currencyRates = CurrencyRates(1, "base", "date", mapOf())
        val observable = Observable.just(currencyRates)
        `when`(repository.fetchCurrencyRates()).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository, atLeastOnce()).fetchCurrencyRates()
    }

    @Test
    fun shouldEmitErrorStateWhenServiceCallFails() {
        val observable = Observable.error<CurrencyRates>(Exception())
        `when`(repository.fetchCurrencyRates()).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        currencyConverterViewModel.viewState().observeForever {
            assertThat(it).isEqualTo(ViewState.ERROR)
        }
    }

    @Test
    fun shouldCallCurrencyRatesServiceInEachSecond() {
        val currencyRates = CurrencyRates(1, "base", "date", mapOf())
        val observable = Observable.just(currencyRates)
        `when`(repository.fetchCurrencyRates()).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify(repository, times(10)).fetchCurrencyRates()
    }

    @Test
    fun shouldSaveCurrencyRateInDatabaseWhenResponseFromServerIsSuccessful() {
        val currencyRates = CurrencyRates(1, "base", "date", mapOf())
        val observable = Observable.just(currencyRates)
        `when`(repository.fetchCurrencyRates()).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository).updatesDatabase(currencyRates)
    }

    @Test
    fun shouldReturnNullRateListWhenCurrencyRatesIsEmptyFromRepository() {
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        currencyConverterViewModel.currencyRates().observeForever {
            assertThat(it).isNull()
        }
    }

    @Test
    fun shouldMapCurrencyRatesObjectToAListOfRateWithOneElement() {
        val currencyRatesFromDatabase = CurrencyRates(1, "base", "date", mapOf("Key1" to 1.5))
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        currencyRateLiveData.value = currencyRatesFromDatabase
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        val currencyRatesFromServer = CurrencyRates(1, "base", "date", mapOf("Key1" to 1.5))
        val observable = Observable.just(currencyRatesFromServer)
        `when`(repository.fetchCurrencyRates()).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        currencyConverterViewModel.currencyRates().observeForever {
            assertThat(it).hasSize(1)
            assertThat(it).containsExactly(Rate("Key1", 1.5))
        }
    }

    @Test
    fun shouldMapCurrencyRatesObjectToAListOfRateWithNElements() {
        val currencyRatesFromDatabase = CurrencyRates(1, "base", "date", mapOf("Key1" to 1.5, "Key2" to 1.6, "Key3" to 1.8))
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        currencyRateLiveData.value = currencyRatesFromDatabase
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        val currencyRatesFromServer = CurrencyRates(1, "base", "date", mapOf("Key1" to 1.5, "Key2" to 1.6, "Key3" to 1.8))
        val observable = Observable.just(currencyRatesFromServer)
        `when`(repository.fetchCurrencyRates()).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        currencyConverterViewModel.currencyRates().observeForever {
            assertThat(it).hasSize(3)
            assertThat(it).containsExactly(
                Rate("Key1", 1.5),
                Rate("Key2", 1.6),
                Rate("Key3", 1.8)
            )
        }
    }

    @Test
    fun shouldSetRatesWithMaskOrder() {
        val currencyRatesFromDatabase = CurrencyRates(1, "base", "date", mapOf("Key1" to 1.5, "Key2" to 1.6, "Key3" to 1.8))
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        currencyRateLiveData.value = currencyRatesFromDatabase
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        currencyConverterViewModel.updatesRateOrderMask(listOf("Key3", "Key2", "Key1"))

        currencyConverterViewModel.currencyRates().observeForever {
            assertThat(it).hasSize(3)
            assertThat(it).containsExactly(
                Rate("Key3", 1.8),
                Rate("Key2", 1.6),
                Rate("Key1", 1.5)
            )
        }
    }

    @Test
    fun shouldRemoveRatesFromListWhenItDoesntExistOnMask() {
        val currencyRatesFromDatabase = CurrencyRates(1, "base", "date", mapOf("Key1" to 1.5, "Key2" to 1.6, "Key3" to 1.8))
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        currencyRateLiveData.value = currencyRatesFromDatabase
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        currencyConverterViewModel.updatesRateOrderMask(listOf("Key2"))

        currencyConverterViewModel.currencyRates().observeForever {
            assertThat(it).hasSize(1)
            assertThat(it).containsExactly(Rate("Key2", 1.6))
        }
    }
}
