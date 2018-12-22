package com.phellipesilva.currencyconverter.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.currencyconverter.database.CurrencyDAO
import com.phellipesilva.currencyconverter.models.CurrencyRates
import com.phellipesilva.currencyconverter.service.CurrencyRatesService
import io.reactivex.Observable
import javax.inject.Inject


class CurrencyRepository @Inject constructor(
    private val currencyRatesService: CurrencyRatesService,
    private val currencyDAO: CurrencyDAO
) {

    fun getCurrencyRates(): LiveData<CurrencyRates> {
        return currencyDAO.getCurrencyRates()
    }

    fun fetchCurrencyRates(baseRate: String): Observable<CurrencyRates> {
        return currencyRatesService.getRates(baseRate)
    }

    fun updatesDatabase(currencyRates: CurrencyRates) {
        currencyDAO.save(currencyRates)
    }
}