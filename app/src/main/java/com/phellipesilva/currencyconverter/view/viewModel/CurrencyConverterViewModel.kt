package com.phellipesilva.currencyconverter.view.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import com.phellipesilva.currencyconverter.view.state.ViewState
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class CurrencyConverterViewModel(
    private val currencyRepository: CurrencyRepository,
    private var disposable: Disposable
) : ViewModel() {

    private var rateOrderMask: List<String>? = null
    private var currentBaseCurrency = Currency("EUR", 100.0)

    private val currencyRates by lazy { currencyRepository.getCurrencyRates() }
    private val viewState = MutableLiveData<ViewState>()

    init {
        currentBaseCurrency = currencyRepository.getBaseCurrencyFromPreferences()
    }

    override fun onCleared() {
        super.onCleared()
        currencyRepository.saveBaseCurrencyOnSharedPrefs(currentBaseCurrency)
        disposable.dispose()
    }

    fun viewState(): LiveData<ViewState> = viewState

    fun startCurrencyRatesUpdate() {
        disposable = Observable
            .interval(1, TimeUnit.SECONDS)
            .switchMap { currencyRepository.fetchCurrencyRates(currentBaseCurrency) }
            .doOnNext(::initMaskAndBaseRate)
            .subscribe(
                { currencyRepository.updatesCurrencyRates(it) },
                { emitsErrorState() }
            )
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

        disposable.dispose()
        startCurrencyRatesUpdate()
    }

    fun updateBaseCurrencyValue(currency: Currency) {
        currencyRepository.updatesBaseCurrencyValue(currency)
        this.currentBaseCurrency = currency

        if (currency.currencyValue == 0.0) {
            disposable.dispose()
        } else if (disposable.isDisposed && currency.currencyValue > 0.0) {
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

    private fun emitsErrorState() {
        viewState.postValue(ViewState.ERROR)
    }

    private fun initMaskAndBaseRate(currencyRatesFromDatabase: CurrencyRates?) {
        if (rateOrderMask == null && currencyRatesFromDatabase != null) {
            rateOrderMask = listOf(currencyRatesFromDatabase.base.currencyName) + currencyRatesFromDatabase.rates.map { it.key }
            currentBaseCurrency = currencyRatesFromDatabase.base
        }
    }
}