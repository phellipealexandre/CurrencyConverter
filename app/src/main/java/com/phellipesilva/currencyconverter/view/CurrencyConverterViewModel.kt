package com.phellipesilva.currencyconverter.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phellipesilva.currencyconverter.models.CurrencyRates
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import dagger.Reusable
import io.reactivex.disposables.Disposables
import javax.inject.Inject

@Reusable
class CurrencyConverterViewModel @Inject constructor(private val currencyRepository: CurrencyRepository) : ViewModel() {

    private var disposable = Disposables.empty()
    private val currencyRates = MutableLiveData<CurrencyRates>()

    fun currencyRates(): LiveData<CurrencyRates> = currencyRates

    fun loadCurrencyRates() {
        disposable = currencyRepository.getCurrencyRates()
            .subscribe(currencyRates::postValue, this::showError)
    }

    private fun showError(throwable: Throwable) {
        // Show Error
        Log.e("Error", "Error with observable call", throwable)
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}