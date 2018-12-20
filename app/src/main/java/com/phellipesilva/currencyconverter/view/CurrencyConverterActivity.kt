package com.phellipesilva.currencyconverter.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.phellipesilva.currencyconverter.R
import com.phellipesilva.currencyconverter.application.CurrencyConverterApplication

class CurrencyConverterActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrencyConverterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViewModel()

        if (savedInstanceState == null)
            viewModel.loadCurrencyRates()

        viewModel.currencyRates().observe(this, Observer {
            Toast.makeText(this, "Success for rate ${it.base} and date ${it.date}", Toast.LENGTH_LONG).show()
        })
    }

    private fun initializeViewModel() {
        val currencyConverterViewModelFactory =
            (application as CurrencyConverterApplication).component.getCurrencyConverterViewModelFactory()

        viewModel = ViewModelProviders.of(this, currencyConverterViewModelFactory).get(CurrencyConverterViewModel::class.java)
    }
}
