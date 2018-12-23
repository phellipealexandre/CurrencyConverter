package com.phellipesilva.currencyconverter.view.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import com.phellipesilva.currencyconverter.view.state.ViewState
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Reusable
class CurrencyConverterViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private var rateOrderMask: List<String>? = null
    private var currentBaseCurrency = Currency("EUR", 100.0)
    private var disposable = Disposables.empty()

    private val currencyRates by lazy { currencyRepository.getCurrencyRates() }
    private val viewState = MutableLiveData<ViewState>()

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun viewState(): LiveData<ViewState> = viewState

    fun startCurrencyRatesUpdate() {
        disposable = Observable
            .interval(1, TimeUnit.SECONDS)
            .switchMap { currencyRepository.fetchCurrencyRates(currentBaseCurrency) }
            .doOnNext(::initMaskAndBaseRate)
            .subscribe(currencyRepository::updatesDatabase) {
                emitsErrorState()
            }
    }

    fun getObservableListOfRates(): LiveData<List<Currency>> {
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

        disposable.dispose()
        startCurrencyRatesUpdate()
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