package com.phellipesilva.currencyconverter.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.phellipesilva.currencyconverter.R
import com.phellipesilva.currencyconverter.models.CurrencyRates
import kotlinx.android.synthetic.main.currency_rate_list_item.view.*

class CurrencyRatesAdapter(
    private val context: Context
) : RecyclerView.Adapter<CurrencyRatesAdapter.CurrencyRatesViewHolder>() {

    private var currencyRates: CurrencyRates = CurrencyRates(1, "EUR", "2018-09-06", mapOf())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRatesViewHolder {
        return CurrencyRatesViewHolder(LayoutInflater.from(context).inflate(R.layout.currency_rate_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return currencyRates.rates.size
    }

    override fun onBindViewHolder(holder: CurrencyRatesViewHolder, position: Int) {
        val currencyRatePair = currencyRates.rates.toList()[position]
        holder.txtRateName.text = currencyRatePair.first
        holder.txtRateValue.text = currencyRatePair.second.toString()
    }

    fun updateData(currencyRates: CurrencyRates) {
        this.currencyRates = currencyRates
        notifyDataSetChanged()
    }

    class CurrencyRatesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRateName: TextView = view.txtRateName
        val txtRateValue: TextView = view.txtRateValue
    }
}

