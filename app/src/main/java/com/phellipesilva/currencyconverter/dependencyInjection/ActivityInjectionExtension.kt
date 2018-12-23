package com.phellipesilva.currencyconverter.dependencyInjection

import android.app.Activity

val Activity.injector get() = (application as DaggerComponentProvider).component