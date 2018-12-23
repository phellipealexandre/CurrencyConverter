package com.phellipesilva.currencyconverter.view.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates
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
    fun shouldInitializeVIewModelWithEURBaseRate() {
        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository).fetchCurrencyRates(Currency("EUR", 100.0))
    }

    @Test
    fun shouldNotCallCurrencyRatesServiceBeforeTheFirstSecond() {
        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(0, TimeUnit.SECONDS)

        verify(repository, never()).fetchCurrencyRates(Currency("EUR", 100.0))
    }

    @Test
    fun shouldCallCurrencyRatesServiceAfterTheFirstSecond() {
        val currencyRates = CurrencyRates(1, Currency("EUR", 100.0), mapOf())
        val observable = Observable.just(currencyRates)
        `when`(repository.fetchCurrencyRates(Currency("EUR", 100.0))).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository, atLeastOnce()).fetchCurrencyRates(Currency("EUR", 100.0))
    }

    @Test
    fun shouldEmitErrorStateWhenServiceCallFails() {
        val observable = Observable.error<CurrencyRates>(Exception())
        `when`(repository.fetchCurrencyRates(Currency("EUR", 100.0))).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        currencyConverterViewModel.viewState().observeForever {
            assertThat(it).isEqualTo(ViewState.ERROR)
        }
    }

    @Test
    fun shouldCallCurrencyRatesServiceInEachSecond() {
        val currencyRates = CurrencyRates(1, Currency("EUR", 100.0), mapOf())
        val observable = Observable.just(currencyRates)
        `when`(repository.fetchCurrencyRates(Currency("EUR", 100.0))).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify(repository, times(10)).fetchCurrencyRates(Currency("EUR", 100.0))
    }

    @Test
    fun shouldSaveCurrencyRateInDatabaseWhenResponseFromServerIsSuccessful() {
        val currencyRates = CurrencyRates(1, Currency("EUR", 100.0), mapOf())
        val observable = Observable.just(currencyRates)
        `when`(repository.fetchCurrencyRates(Currency("EUR", 100.0))).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository).updatesDatabase(currencyRates)
    }

    @Test
    fun shouldReturnNullRateListWhenCurrencyRatesIsEmptyFromRepository() {
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        currencyConverterViewModel.getObservableListOfRates().observeForever {
            assertThat(it).isNull()
        }
    }

    @Test
    fun shouldBaseCurrencyValueBe100AndAllOtherCurrenciesBeMultipliedByTheBaseRate() {
        val currencyRatesFromDatabase = CurrencyRates(1, Currency("EUR", 100.0), mapOf("Key1" to 1.555))
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        currencyRateLiveData.value = currencyRatesFromDatabase
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        currencyConverterViewModel.updatesRateOrderMask(
            listOf(
                Currency("base", 100.0),
                Currency("Key1", 1.555)
            ))


        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        currencyConverterViewModel.getObservableListOfRates().observeForever {
            assertThat(it).hasSize(2)
            assertThat(it).containsExactly(
                Currency("base", 100.0),
                Currency("Key1", 155.5)
            )
        }
    }

    @Test
    fun shouldMapCurrencyRatesObjectToAListOfRateWithOneElement() {
        val currencyRatesFromDatabase = CurrencyRates(1, Currency("EUR", 100.0), mapOf("Key1" to 1.5))
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        currencyRateLiveData.value = currencyRatesFromDatabase
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        val currencyRatesFromServer = CurrencyRates(1, Currency("EUR", 100.0), mapOf("Key1" to 1.5))
        val observable = Observable.just(currencyRatesFromServer)
        `when`(repository.fetchCurrencyRates(Currency("EUR", 100.0))).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        currencyConverterViewModel.getObservableListOfRates().observeForever {
            assertThat(it).hasSize(2)
            assertThat(it).containsExactly(
                Currency("EUR", 100.0),
                Currency("Key1", 150.0)
            )
        }
    }

    @Test
    fun shouldMapCurrencyRatesObjectToAListOfRateWithNElements() {
        val currencyRatesFromDatabase = CurrencyRates(1, Currency("EUR", 100.0), mapOf("Key1" to 1.5, "Key2" to 1.6, "Key3" to 1.8))
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        currencyRateLiveData.value = currencyRatesFromDatabase
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        val currencyRatesFromServer = CurrencyRates(1, Currency("EUR", 100.0), mapOf("Key1" to 1.5, "Key2" to 1.6, "Key3" to 1.8))
        val observable = Observable.just(currencyRatesFromServer)
        `when`(repository.fetchCurrencyRates(Currency("EUR", 100.0))).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        currencyConverterViewModel.getObservableListOfRates().observeForever {
            assertThat(it).hasSize(4)
            assertThat(it).containsExactly(
                Currency("EUR", 100.0),
                Currency("Key1", 150.0),
                Currency("Key2", 160.0),
                Currency("Key3", 180.0)
            )
        }
    }

    @Test
    fun shouldSetRatesWithMaskOrder() {
        val currencyRatesFromDatabase = CurrencyRates(1, Currency("EUR", 100.0), mapOf("Key1" to 1.5, "Key2" to 1.6, "Key3" to 1.8))
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        currencyRateLiveData.value = currencyRatesFromDatabase
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        currencyConverterViewModel.updatesRateOrderMask(
            listOf(
                Currency("EUR", 100.0),
                Currency("Key3", 1.8),
                Currency("Key2", 1.6),
                Currency("Key1", 1.5)
            )
        )

        currencyConverterViewModel.getObservableListOfRates().observeForever {
            assertThat(it).hasSize(4)
            assertThat(it).containsExactly(
                Currency("EUR", 100.0),
                Currency("Key3", 180.0),
                Currency("Key2", 160.0),
                Currency("Key1", 150.0)
            )
        }
    }

    @Test
    fun shouldRemoveRatesFromListWhenKeyDoesNotExistOnMask() {
        val currencyRatesFromDatabase = CurrencyRates(1, Currency("EUR", 100.0), mapOf("Key1" to 1.5, "Key2" to 1.6, "Key3" to 1.8))
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        currencyRateLiveData.value = currencyRatesFromDatabase
        `when`(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        currencyConverterViewModel.updatesRateOrderMask(
            listOf(
                Currency("EUR", 100.0),
                Currency("Key3", 1.8)
            )
        )

        currencyConverterViewModel.getObservableListOfRates().observeForever {
            assertThat(it).hasSize(2)
            assertThat(it).containsExactly(
                Currency("EUR", 100.0),
                Currency("Key3", 180.0)
            )
        }
    }

    @Test
    fun shouldFetchNewCurrencyRatesWithFirstElementOfMask() {
        currencyConverterViewModel.updatesRateOrderMask(
            listOf(
                Currency("EUR", 100.0),
                Currency("Key3", 1.8)
            )
        )

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository, atLeastOnce()).fetchCurrencyRates(Currency("EUR", 100.0))
    }
}
