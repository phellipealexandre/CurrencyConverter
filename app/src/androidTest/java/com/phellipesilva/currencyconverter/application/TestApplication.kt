package com.phellipesilva.currencyconverter.application

import android.app.Application
import com.phellipesilva.currencyconverter.dependencyInjection.ApplicationComponent
import com.phellipesilva.currencyconverter.dependencyInjection.DaggerComponentProvider
import com.phellipesilva.currencyconverter.dependencyInjection.DaggerTestComponent

class TestApplication : Application(), DaggerComponentProvider {
    override val component: ApplicationComponent = DaggerTestComponent
        .builder()
        .applicationContext(this)
        .build()
}