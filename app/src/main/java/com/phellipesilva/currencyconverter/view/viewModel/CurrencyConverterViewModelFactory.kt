package com.phellipesilva.currencyconverter.view.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import dagger.Reusable
import io.reactivex.disposables.Disposables
import javax.inject.Inject

@Reusable
class CurrencyConverterViewModelFactory @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrencyConverterViewModel(currencyRepository, Disposables.empty()) as T
    }
}