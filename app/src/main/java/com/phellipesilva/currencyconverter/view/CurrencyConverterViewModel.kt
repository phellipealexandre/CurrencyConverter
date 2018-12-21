package com.phellipesilva.currencyconverter.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phellipesilva.currencyconverter.models.CurrencyRates
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
    private val currencyRates by lazy { currencyRepository.getCurrencyRates() }
    private val viewState = MutableLiveData<ViewState>()

    fun currencyRates(): LiveData<CurrencyRates> = currencyRates
    fun viewState(): LiveData<ViewState> = viewState

    fun startCurrencyRatesUpdate() {
        disposable = Observable
            .interval(1, TimeUnit.SECONDS)
            .switchMap { currencyRepository.fetchCurrencyRates() }
            .subscribe(currencyRepository::updatesDatabase, this::emitsErrorState)
    }

    private fun emitsErrorState(throwable: Throwable) {
        viewState.postValue(ViewState.ERROR)
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}