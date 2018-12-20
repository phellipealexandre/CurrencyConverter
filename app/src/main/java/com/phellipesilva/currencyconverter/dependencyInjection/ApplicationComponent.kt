package com.phellipesilva.currencyconverter.dependencyInjection

import android.content.Context
import com.phellipesilva.currencyconverter.view.CurrencyConverterViewModelFactory
import dagger.BindsInstance
import dagger.Component

@Component(modules = [DatabaseModule::class, ServiceModule::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(applicationContext: Context): Builder
        fun build(): ApplicationComponent
    }

    fun getCurrencyConverterViewModelFactory(): CurrencyConverterViewModelFactory
}