package com.phellipesilva.currencyconverter.view.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.currencyconverter.repository.CurrencyRepository
import com.phellipesilva.currencyconverter.system.ConnectionManager
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class CurrencyConverterViewModelFactory @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val connectionManager: ConnectionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrencyConverterViewModel(currencyRepository, CompositeDisposable(), connectionManager) as T
    }
}