package com.phellipesilva.currencyconverter.application

import android.app.Application
import com.phellipesilva.currencyconverter.BuildConfig
import com.phellipesilva.currencyconverter.dependencyInjection.ApplicationComponent
import com.phellipesilva.currencyconverter.dependencyInjection.DaggerApplicationComponent
import com.phellipesilva.currencyconverter.dependencyInjection.DaggerComponentProvider
import timber.log.Timber

class CurrencyConverterApplication : Application(), DaggerComponentProvider {

    override val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationContext(applicationContext)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        setupTimber()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}