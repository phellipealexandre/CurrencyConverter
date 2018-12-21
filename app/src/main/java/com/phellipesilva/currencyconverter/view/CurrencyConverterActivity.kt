package com.phellipesilva.currencyconverter.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.phellipesilva.currencyconverter.R
import com.phellipesilva.currencyconverter.dependencyInjection.injector
import kotlinx.android.synthetic.main.activity_main.*

class CurrencyConverterActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrencyConverterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViewModel()

        if (savedInstanceState == null)
            viewModel.startCurrencyRatesUpdate()

        initObservers()
    }

    private fun initObservers() {
        viewModel.currencyRates().observe(this, Observer {
            it?.let {
                txtHelloWorld.text = "Success for rate ${it.rates.keys.first()} with value ${it.rates.values.first()}"
            }
        })
    }

    private fun initializeViewModel() {
        val currencyConverterViewModelFactory = injector.getCurrencyConverterViewModelFactory()
        viewModel = ViewModelProviders.of(this, currencyConverterViewModelFactory).get(CurrencyConverterViewModel::class.java)
    }
}
