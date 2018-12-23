package com.phellipesilva.currencyconverter.service

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRatesService {

    @GET("latest")
    fun getRates(@Query("base") baseRate: String): Observable<RemoteCurrencyRates>
}