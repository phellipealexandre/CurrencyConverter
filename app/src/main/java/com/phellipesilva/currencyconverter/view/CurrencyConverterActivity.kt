package com.phellipesilva.currencyconverter.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import com.phellipesilva.currencyconverter.R
import com.phellipesilva.currencyconverter.dependencyInjection.injector
import com.phellipesilva.currencyconverter.view.recyclerView.CurrencyRatesAdapter
import com.phellipesilva.currencyconverter.view.viewModel.CurrencyConverterViewModel
import kotlinx.android.synthetic.main.activity_main.*

class CurrencyConverterActivity : AppCompatActivity() {

    private lateinit var viewModel: CurrencyConverterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViewModel(savedInstanceState)
        initializeRecyclerView()
        initializeViewStateObserver()
    }

    private fun initializeViewModel(savedInstanceState: Bundle?) {
        val currencyConverterViewModelFactory = injector.getCurrencyConverterViewModelFactory()
        viewModel = ViewModelProviders.of(this, currencyConverterViewModelFactory).get(CurrencyConverterViewModel::class.java)

        if (savedInstanceState == null) viewModel.startCurrencyRatesUpdate()
    }

    private fun initializeRecyclerView() {
        val adapter = CurrencyRatesAdapter(this)
        adapter.setOnPositionChangedListener {
            viewModel.updatesRateOrderMask(it)
            recyclerView.scrollToPosition(0)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        viewModel.getObservableListOfRates().observe(this, Observer {
            it?.let { adapter.updateData(it) }
        })
    }

    private fun initializeViewStateObserver() {
        viewModel.viewState().observe(this, Observer {
            Snackbar.make(coordinatorLayout, getString(R.string.activity_error_msg), Snackbar.LENGTH_LONG).show()
        })
    }
}
