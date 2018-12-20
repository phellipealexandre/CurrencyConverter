package com.phellipesilva.currencyconverter.repository

import com.phellipesilva.currencyconverter.database.CurrencyDAO
import com.phellipesilva.currencyconverter.models.CurrencyRates
import com.phellipesilva.currencyconverter.service.CurrencyRatesService
import io.reactivex.Observable
import javax.inject.Inject


class CurrencyRepository @Inject constructor(
    private val currencyRatesService: CurrencyRatesService,
    private val currencyDAO: CurrencyDAO
) {

    fun getCurrencyRates(): Observable<CurrencyRates> {
        return currencyRatesService.getRates("EUR")
    }
}