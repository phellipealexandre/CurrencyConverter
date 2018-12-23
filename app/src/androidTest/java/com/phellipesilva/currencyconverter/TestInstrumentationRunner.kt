package com.phellipesilva.currencyconverter

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.phellipesilva.currencyconverter.application.TestApplication

class TestInstrumentationRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}