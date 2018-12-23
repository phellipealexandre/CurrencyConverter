package com.phellipesilva.currencyconverter.view.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import com.phellipesilva.currencyconverter.utils.RxUtils
import com.phellipesilva.currencyconverter.view.state.ViewState
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class CurrencyConverterViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: CurrencyRepository

    @Mock
    private lateinit var disposable: Disposable

    private lateinit var currencyConverterViewModel: CurrencyConverterViewModel
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        whenever(repository.getBaseCurrencyFromPreferences()).thenReturn(Currency("EUR", 100.0))

        currencyConverterViewModel = CurrencyConverterViewModel(repository, disposable)
        testScheduler = TestScheduler()
        RxUtils.overridesEnvironmentToCustomScheduler(testScheduler)
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldInitializeViewModelWithEURBaseRate() {
        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository).fetchCurrencyRates(Currency("EUR", 100.0))
    }

    @Test
    fun shouldInitializeViewModelWithRateStoredInPreferences() {
        whenever(repository.getBaseCurrencyFromPreferences()).thenReturn(Currency("BRL", 150.0))
        currencyConverterViewModel = CurrencyConverterViewModel(repository, disposable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository).fetchCurrencyRates(Currency("BRL", 150.0))
    }

    @Test
    fun shouldNotCallCurrencyRatesServiceBeforeTheFirstSecond() {
        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)

        verify(repository, never()).fetchCurrencyRates(any())
    }

    @Test
    fun shouldCallCurrencyRatesServiceAfterPassingOneSecond() {
        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository, times(1)).fetchCurrencyRates(Currency("EUR", 100.0))
    }

    @Test
    fun shouldEmitErrorStateWhenServiceCallFails() {
        val observable = Observable.error<CurrencyRates>(Exception())
        whenever(repository.fetchCurrencyRates(Currency("EUR", 100.0))).thenReturn(observable)

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        currencyConverterViewModel.viewState().observeForever {
            assertThat(it).isEqualTo(ViewState.ERROR)
        }
    }

    @Test
    fun shouldCallCurrencyRatesServiceTenTimesAfterPassingTenSeconds() {
        mockCurrencyRatesResponseFromServer()

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify(repository, times(10)).fetchCurrencyRates(Currency("EUR", 100.0))
    }

    @Test
    fun shouldSaveCurrencyRateInDatabaseWhenResponseFromServerIsSuccessful() {
        val expectedCurrencyRates = mockCurrencyRatesResponseFromServer()

        currencyConverterViewModel.startCurrencyRatesUpdate()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository).updatesCurrencyRates(expectedCurrencyRates)
    }

    @Test
    fun shouldReturnNullRateListWhenCurrencyRatesIsEmptyFromRepository() {
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        whenever(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)

        currencyConverterViewModel.getObservableListOfCurrencies().observeForever {
            assertThat(it).isNull()
        }
    }

    @Test
    fun shouldProcessedCurrencyListSizeBeTheSumOfBaseCurrencyWithListOfRates() {
        mockCurrencyRatesResponseFromDatabase(
            Currency("EUR", 120.0),
            mapOf("Key1" to 2.0, "Key2" to 1.5, "Key3" to 1.5)
        )

        currencyConverterViewModel.getObservableListOfCurrencies().observeForever {
            assertThat(it.size).isEqualTo(4)
        }
    }

    @Test
    fun shouldCurrencyValuesBeMultiplicationBetweenTheBaseCurrencyValueAndCurrencyRates() {
        mockCurrencyRatesResponseFromDatabase(
            Currency("EUR", 120.0),
            mapOf("Key1" to 2.0, "Key2" to 1.5)
        )

        currencyConverterViewModel.getObservableListOfCurrencies().observeForever {
            assertThat(it).containsExactly(
                Currency("EUR", 120.0),
                Currency("Key1", 240.0),
                Currency("Key2", 180.0)
            )
        }
    }

    @Test
    fun shouldReturnedCurrencyListBeInTheExactOrderFromDatabaseIfNoMaskIsApplied() {
        mockCurrencyRatesResponseFromDatabase(
            Currency("EUR", 120.0),
            mapOf("Key1" to 1.0, "Key2" to 1.0, "Key3" to 1.0, "Key4" to 1.0 ,"Key5" to 1.0)
        )

        currencyConverterViewModel.getObservableListOfCurrencies().observeForever {
            assertThat(it).hasSize(6)
            assertThat(it).containsExactly(
                Currency("EUR", 120.0),
                Currency("Key1", 120.0),
                Currency("Key2", 120.0),
                Currency("Key3", 120.0),
                Currency("Key4", 120.0),
                Currency("Key5", 120.0)
            )
        }
    }

    @Test
    fun shouldReturnedCurrencyListBeInTheExactOrderOfMaskWhenApplied() {
        mockCurrencyRatesResponseFromDatabase(
            Currency("EUR", 120.0),
            mapOf("Key1" to 1.0, "Key2" to 1.0, "Key3" to 1.0, "Key4" to 1.0 ,"Key5" to 1.0)
        )

        currencyConverterViewModel.updatesRateOrderMask(
            listOf(
                Currency("EUR", 100.0),
                Currency("Key5", 100.0),
                Currency("Key4", 100.0),
                Currency("Key3", 100.0),
                Currency("Key2", 100.0),
                Currency("Key1", 100.0)
            )
        )

        currencyConverterViewModel.getObservableListOfCurrencies().observeForever {
            assertThat(it).hasSize(6)
            assertThat(it).containsExactly(
                Currency("EUR", 100.0),
                Currency("Key5", 100.0),
                Currency("Key4", 100.0),
                Currency("Key3", 100.0),
                Currency("Key2", 100.0),
                Currency("Key1", 100.0)
            )
        }
    }

    @Test
    fun shouldRemoveRatesFromListWhenKeyDoesNotExistOnMask() {
        mockCurrencyRatesResponseFromDatabase(
            Currency("EUR", 120.0),
            mapOf("Key1" to 1.0, "Key2" to 1.0, "Key3" to 1.0, "Key4" to 1.0 ,"Key5" to 1.0)
        )

        currencyConverterViewModel.updatesRateOrderMask(
            listOf(
                Currency("EUR", 100.0),
                Currency("Key5", 100.0),
                Currency("Key4", 100.0),
                Currency("Key3", 100.0)
            )
        )

        currencyConverterViewModel.getObservableListOfCurrencies().observeForever {
            assertThat(it).hasSize(4)
            assertThat(it).containsExactly(
                Currency("EUR", 100.0),
                Currency("Key5", 100.0),
                Currency("Key4", 100.0),
                Currency("Key3", 100.0)
            )
        }
    }

    @Test
    fun shouldFetchNewCurrencyRatesWithFirstElementOfMask() {
        currencyConverterViewModel.updatesRateOrderMask(
            listOf(
                Currency("BASE", 101.0),
                Currency("Key3", 1.8)
            )
        )

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository, times(1)).fetchCurrencyRates(Currency("BASE", 101.0))
    }

    @Test
    fun shouldProcessAndUpdateDatabaseWithNewCurrencyRatesWhenUpdateMaskAndBaseValueIsGreaterThanZero() {
        currencyConverterViewModel.updatesRateOrderMask(
            listOf(
                Currency("BASE", 101.0),
                Currency("Key3", 101.0)
            )
        )

        val expectedCurrencyRates = CurrencyRates(
            1,
            Currency("BASE", 101.0),
            mapOf("Key3" to 1.0)
        )

        verify(repository).updatesCurrencyRates(expectedCurrencyRates)
    }

    @Test
    fun shouldNotUpdateDatabaseWithNewCurrencyRatesWhenBaseValueIsEqualsZero() {
        currencyConverterViewModel.updatesRateOrderMask(
            listOf(
                Currency("BASE", 0.0),
                Currency("Key3", 0.0)
            )
        )

        verify(repository, never()).updatesCurrencyRates(any())
    }

    @Test
    fun shouldProcessNewCurrencyRatesFromMaskValueToHaveTheCurrencyValueDividedByBaseValue() {
        currencyConverterViewModel.updatesRateOrderMask(
            listOf(
                Currency("BASE", 100.0),
                Currency("Key1", 180.0),
                Currency("Key2", 80.0),
                Currency("Key3", 55.0),
                Currency("Key4", 280.0),
                Currency("Key5", 100.0)
            )
        )

        val expectedCurrencyRates = CurrencyRates(
            1,
            Currency("BASE", 100.0),
            mapOf("Key1" to 1.8, "Key2" to 0.8, "Key3" to 0.55, "Key4" to 2.8, "Key5" to 1.0)
        )

        verify(repository).updatesCurrencyRates(expectedCurrencyRates)
    }

    @Test
    fun shouldCancelRatesUpdateAndStartAgainWhenSettingANewMask() {
        currencyConverterViewModel.updatesRateOrderMask(listOf(Currency("BASE", 100.0)))

        verify(disposable).dispose()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository).fetchCurrencyRates(Currency("BASE", 100.0))
    }

    @Test
    fun shouldCallRepositoryWithNewBaseValueWhenUpdatingFromViewModel() {
        val currency = Currency("BASE", 11.0)

        currencyConverterViewModel.updateBaseCurrencyValue(currency)

        verify(repository).updatesBaseCurrencyValue(currency)
    }

    @Test
    fun shouldCancelRatesUpdateFromServerWhenUpdatingBaseCurrencyValueWithZero() {
        val currency = Currency("BASE", 0.0)

        currencyConverterViewModel.updateBaseCurrencyValue(currency)

        verify(disposable).dispose()
    }

    @Test
    fun shouldStartRatesUpdateFromServerWhenUpdatingBaseCurrencyValueIsGreaterThanZeroAndDisposableWasPreviouslyDisposed() {
        val currency = Currency("BASE", 100.0)
        whenever(disposable.isDisposed).thenReturn(true)

        currencyConverterViewModel.updateBaseCurrencyValue(currency)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository).fetchCurrencyRates(Currency("BASE", 100.0))
    }

    @Test
    fun shouldNotStartRatesUpdateFromServerWhenObservableDisposableIsNotDisposed() {
        val currency = Currency("BASE", 100.0)
        whenever(disposable.isDisposed).thenReturn(false)

        currencyConverterViewModel.updateBaseCurrencyValue(currency)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        verify(repository, never()).fetchCurrencyRates(any())
    }

    private fun mockCurrencyRatesResponseFromServer(
        baseCurrency: Currency = Currency("EUR", 100.0),
        ratesMap: Map<String, Double> = mapOf()
    ): CurrencyRates {
        val currencyRates = CurrencyRates(1, baseCurrency, ratesMap)
        val observable = Observable.just(currencyRates)
        whenever(repository.fetchCurrencyRates(baseCurrency)).thenReturn(observable)
        return currencyRates
    }

    private fun mockCurrencyRatesResponseFromDatabase(
        baseCurrency: Currency = Currency("EUR", 100.0),
        ratesMap: Map<String, Double> = mapOf()
    ): CurrencyRates {
        val currencyRatesFromDatabase = CurrencyRates(1, baseCurrency, ratesMap)
        val currencyRateLiveData = MutableLiveData<CurrencyRates>()
        currencyRateLiveData.value = currencyRatesFromDatabase
        whenever(repository.getCurrencyRates()).thenReturn(currencyRateLiveData)
        return currencyRatesFromDatabase
    }
}
