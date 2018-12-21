package com.phellipesilva.currencyconverter.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.phellipesilva.currencyconverter.R
import com.phellipesilva.currencyconverter.dependencyInjection.injector
import kotlinx.android.synthetic.main.activity_main.*

class CurrencyConverterActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrencyConverterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViewModel(savedInstanceState)
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        val adapter = CurrencyRatesAdapter( this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.currencyRates().observe(this, Observer {
            it?.let { adapter.updateData(it) }
        })
    }

    private fun initializeViewModel(savedInstanceState: Bundle?) {
        val currencyConverterViewModelFactory = injector.getCurrencyConverterViewModelFactory()
        viewModel = ViewModelProviders.of(this, currencyConverterViewModelFactory).get(CurrencyConverterViewModel::class.java)

        if (savedInstanceState == null) viewModel.startCurrencyRatesUpdate()
    }
}
