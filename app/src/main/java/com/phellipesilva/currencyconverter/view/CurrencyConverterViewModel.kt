package com.phellipesilva.currencyconverter.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.phellipesilva.currencyconverter.models.CurrencyRates
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Reusable
class CurrencyConverterViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private var disposable = Disposables.empty()
    private val currencyRates by lazy { currencyRepository.getCurrencyRates() }

    fun currencyRates(): LiveData<CurrencyRates> = currencyRates

    fun startCurrencyRatesUpdate() {
        disposable = Observable
            .interval(1, TimeUnit.SECONDS)
            .switchMap { currencyRepository.fetchCurrencyRates() }
            .subscribe(currencyRepository::updatesDatabase)
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}