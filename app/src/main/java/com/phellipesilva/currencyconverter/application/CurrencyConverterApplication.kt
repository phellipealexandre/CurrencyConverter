package com.phellipesilva.currencyconverter.application

import android.app.Application
import com.phellipesilva.currencyconverter.dependencyInjection.ApplicationComponent
import com.phellipesilva.currencyconverter.dependencyInjection.DaggerApplicationComponent
import com.phellipesilva.currencyconverter.dependencyInjection.DaggerComponentProvider

class CurrencyConverterApplication : Application(), DaggerComponentProvider {

    override val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationContext(applicationContext)
            .build()
    }
}