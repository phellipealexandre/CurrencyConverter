package com.phellipesilva.currencyconverter.dependencyInjection

import com.phellipesilva.currencyconverter.service.CurrencyRatesService
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ServiceModule {

    @Provides
    @Reusable
    fun providesCurrencyRatesService(): CurrencyRatesService = Retrofit.Builder()
        .baseUrl("https://revolut.duckdns.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .build()
        .create(CurrencyRatesService::class.java)
}