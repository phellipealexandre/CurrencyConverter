package com.phellipesilva.currencyconverter.dependencyInjection

import com.phellipesilva.currencyconverter.service.CurrencyRatesService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object ServiceTestModule {

    @Singleton
    @Provides
    @JvmStatic
    fun providesCurrencyRatesService(): CurrencyRatesService = Retrofit.Builder()
        .baseUrl("http://localhost:4040")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(CurrencyRatesService::class.java)
}