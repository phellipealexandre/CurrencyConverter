package com.phellipesilva.currencyconverter.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.currencyconverter.database.CurrencyDAO
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates
import com.phellipesilva.currencyconverter.service.CurrencyRatesService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val currencyRatesService: CurrencyRatesService,
    private val currencyDAO: CurrencyDAO
) {

    fun getCurrencyRates(): LiveData<CurrencyRates> {
        return currencyDAO.getCurrencyRates()
    }

    fun fetchCurrencyRates(currency: Currency): Observable<CurrencyRates> {
        return currencyRatesService
            .getRates(currency.currencyName)
            .map { CurrencyRates(1, currency, it.rates) }
    }

    fun updatesDatabase(currencyRates: CurrencyRates) {
        Completable.create { currencyDAO.saveCurrencyRates(currencyRates) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun updatesBaseCurrencyValue(newBaseRate: Currency) {
        Completable.create { currencyDAO.updateBaseCurrencyValue(newBaseRate.currencyValue) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}