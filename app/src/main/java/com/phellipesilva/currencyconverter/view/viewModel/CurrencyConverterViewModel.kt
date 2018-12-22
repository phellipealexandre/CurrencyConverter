package com.phellipesilva.currencyconverter.view.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.phellipesilva.currencyconverter.models.CurrencyRates
import com.phellipesilva.currencyconverter.models.Rate
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import com.phellipesilva.currencyconverter.view.state.ViewState
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Reusable
class CurrencyConverterViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private var disposable = Disposables.empty()
    private var rateOrderMask = listOf<String>()

    private val currencyRates by lazy { currencyRepository.getCurrencyRates() }
    private val viewState = MutableLiveData<ViewState>()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun viewState(): LiveData<ViewState> = viewState

    fun startCurrencyRatesUpdate() {
        disposable = Observable
            .interval(1, TimeUnit.SECONDS)
            .switchMap { currencyRepository.fetchCurrencyRates() }
            .subscribe({
                currencyRepository.updatesDatabase(it)
                setMaskInitialValue(it)
            }, {
                emitsErrorState()
            })
    }

    fun currencyRates(): LiveData<List<Rate>> {
        return Transformations.map(currencyRates) { currencyRates ->
            rateOrderMask.map { Rate(it, currencyRates.rates.getValue(it)) }
        }
    }

    fun updatesRateOrderMask(mask: List<String>) {
        this.rateOrderMask = mask
    }

    private fun emitsErrorState() {
        viewState.postValue(ViewState.ERROR)
    }

    private fun setMaskInitialValue(currencyRates: CurrencyRates?) {
        currencyRates?.let { _ ->
            if (rateOrderMask.isEmpty()) {
                rateOrderMask = currencyRates.rates.map { it.key }
            }
        }
    }
}