package com.phellipesilva.currencyconverter.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import dagger.Reusable
import javax.inject.Inject

@Reusable
class CurrencyConverterViewModelFactory @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrencyConverterViewModel(currencyRepository) as T
    }
}