package com.phellipesilva.currencyconverter.dependencyInjection

import android.app.Activity
import com.phellipesilva.currencyconverter.application.CurrencyConverterApplication

val Activity.injector get() = (application as CurrencyConverterApplication).component