package com.phellipesilva.currencyconverter.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.database.entity.CurrencyRates
import com.phellipesilva.currencyconverter.database.room.CurrencyDAO
import com.phellipesilva.currencyconverter.database.sharedprefs.CurrencyPreferences
import com.phellipesilva.currencyconverter.service.CurrencyRatesService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val currencyRatesService: CurrencyRatesService,
    private val currencyDAO: CurrencyDAO,
    private val currencyPreferences: CurrencyPreferences
) {

    fun getCurrencyRates(): LiveData<CurrencyRates> {
        return currencyDAO.getCurrencyRates()
    }

    fun fetchCurrencyRates(baseCurrency: Currency): Observable<CurrencyRates> {
        return currencyRatesService
            .getRates(baseCurrency.currencyName)
            .map { CurrencyRates(1, baseCurrency, it.rates) }
    }

    fun updatesCurrencyRates(currencyRates: CurrencyRates) {
        Completable.fromAction { currencyDAO.saveCurrencyRates(currencyRates) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun updatesBaseCurrencyValue(newBaseRate: Currency) {
        Completable.fromAction { currencyDAO.updateBaseCurrencyValue(newBaseRate.currencyValue) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getBaseCurrencyFromPreferences(): Currency {
        return currencyPreferences.getBaseCurrencyFromPreferences()
    }

    fun saveBaseCurrencyOnSharedPrefs(currency: Currency) {
        currencyPreferences.saveBaseCurrencyOnSharedPrefs(currency)
    }
}