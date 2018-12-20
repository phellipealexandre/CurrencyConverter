package com.phellipesilva.currencyconverter.service

import com.phellipesilva.currencyconverter.models.CurrencyRates
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRatesService {

    @GET("latest")
    fun getRates(@Query("baseRate") baseRate: String): Observable<CurrencyRates>
}