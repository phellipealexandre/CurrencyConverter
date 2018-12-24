package com.phellipesilva.currencyconverter.view.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import com.phellipesilva.currencyconverter.system.ConnectionManager
import com.phellipesilva.currencyconverter.view.state.Event
import com.phellipesilva.currencyconverter.view.state.ViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CurrencyConverterViewModel(
    private val currencyRepository: CurrencyRepository,
    private val compositeDisposable: CompositeDisposable,
    private val connectionManager: ConnectionManager
) : ViewModel() {

    private var rateOrderMask: List<String>? = null
    private var currentBaseCurrency = Currency("EUR", 100.0)
    private val currencyRates by lazy { currencyRepository.getCurrencyRates() }
    private val viewState = MutableLiveData<Event<ViewState>>()

    init {
        currentBaseCurrency = currencyRepository.getBaseCurrencyFromPreferences()
    }

    override fun onCleared() {
        super.onCleared()
        currencyRepository.saveBaseCurrencyOnSharedPrefs(currentBaseCurrency)
        compositeDisposable.clear()
    }

    fun viewState(): LiveData<Event<ViewState>> = viewState

    fun startCurrencyRatesUpdate() {
        val disposable = Observable
            .interval(1, TimeUnit.SECONDS)
            .doOnSubscribe(::handleInternetConnectionBeforeSubscribe)
            .switchMap { currencyRepository.fetchCurrencyRates(currentBaseCurrency) }
            .doOnNext(::initMaskAndBaseRate)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(currencyRepository::updatesCurrencyRates, ::emitsErrorState)

        compositeDisposable.add(disposable)
    }

    fun getObservableListOfCurrencies(): LiveData<List<Currency>> {
        return Transformations.map(currencyRates) { currencyRates ->
            currencyRates?.let {
                initMaskAndBaseRate(it)
                transformCurrencyRatesToListApplyingMask(it)
            }
        }
    }

    fun updatesRateOrderMask(newMask: List<Currency>) {
        this.rateOrderMask = newMask.map { it.currencyName }
        this.currentBaseCurrency = newMask.first()

        recalculateRatesWhenChangesBaseCurrency(newMask)

        compositeDisposable.clear()
        startCurrencyRatesUpdate()
    }

    fun updateBaseCurrencyValue(currency: Currency) {
        currencyRepository.updatesBaseCurrencyValue(currency)
        this.currentBaseCurrency = currency

        if (currency.currencyValue == 0.0) {
            compositeDisposable.clear()
        } else if (compositeDisposable.isDisposed && currency.currencyValue > 0.0) {
            startCurrencyRatesUpdate()
        }
    }

    private fun recalculateRatesWhenChangesBaseCurrency(newMask: List<Currency>) {
        if (currentBaseCurrency.currencyValue > 0.0) {
            val currencyRatesMap = newMask.asSequence().filterIndexed { index, _ -> index != 0 }
                .map { it.currencyName to it.currencyValue.div(currentBaseCurrency.currencyValue) }
                .toMap()

            val newCurrencyRates = CurrencyRates(1, currentBaseCurrency, currencyRatesMap)
            currencyRepository.updatesCurrencyRates(newCurrencyRates)
        } else {
            viewState.value = Event(ViewState.RECALCULATING_RATES)
        }
    }

    private fun transformCurrencyRatesToListApplyingMask(currencyRates: CurrencyRates): List<Currency>? {
        return rateOrderMask?.map {

            if (currencyRates.rates.containsKey(it)) {
                Currency(it, currencyRates.rates.getValue(it).times(currentBaseCurrency.currencyValue))
            } else {
                currentBaseCurrency
            }
        }
    }

    private fun emitsErrorState(throwable: Throwable) {
        Timber.e(throwable)
        viewState.value = Event(ViewState.UNEXPECTED_ERROR)
    }

    private fun emitsNoInternetConnectionState() {
        Timber.e("No Internet Connection")
        viewState.value = Event(ViewState.NO_INTERNET_ERROR)
    }

    private fun handleInternetConnectionBeforeSubscribe(it: Disposable) {
        if (!connectionManager.isOnline()) {
            it.dispose()
            emitsNoInternetConnectionState()
        }
    }

    private fun initMaskAndBaseRate(currencyRatesFromDatabase: CurrencyRates?) {
        if (rateOrderMask == null && currencyRatesFromDatabase != null) {
            rateOrderMask = listOf(currencyRatesFromDatabase.base.currencyName) + currencyRatesFromDatabase.rates.map { it.key }
            currentBaseCurrency = currencyRatesFromDatabase.base
        }
    }
}