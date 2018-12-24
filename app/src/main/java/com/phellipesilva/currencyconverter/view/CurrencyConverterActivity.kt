package com.phellipesilva.currencyconverter.view

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
        adapter.setOnPositionChangedListener(viewModel::updatesRateOrderMask)
        adapter.setOnBaseValueChangedListener(viewModel::updateBaseCurrencyValue)
        recyclerView.adapter = adapter
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        viewModel.getObservableListOfCurrencies().observe(this, Observer {
            it?.let {
                progressBar.visibility = GONE
                recyclerView.visibility = VISIBLE
                adapter.updateData(it)
            }
        })
    }

    private fun initializeViewStateObserver() {
        viewModel.viewState().observe(this, Observer {
            it.getContentIfNotHandled()?.let { Snackbar.make(coordinatorLayout, getString(it.stringMessageId), Snackbar.LENGTH_LONG).show() }
        })
    }
}
