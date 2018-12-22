package com.phellipesilva.currencyconverter.view.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.phellipesilva.currencyconverter.models.Rate

class CurrencyRateDiffCallback(
    private val oldRates: List<Rate>,
    private val newRates: List<Rate>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRates[oldItemPosition].rateName == newRates[newItemPosition].rateName
    }

    override fun getOldListSize(): Int {
        return oldRates.size
    }

    override fun getNewListSize(): Int {
        return newRates.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRates[oldItemPosition] == newRates[newItemPosition]
    }
}