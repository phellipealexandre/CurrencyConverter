package com.phellipesilva.currencyconverter.view.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.phellipesilva.currencyconverter.database.entity.Currency

class CurrencyRateDiffCallback(
    private val oldCurrencies: List<Currency>,
    private val newCurrencies: List<Currency>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCurrencies[oldItemPosition].currencyName == newCurrencies[newItemPosition].currencyName
    }

    override fun getOldListSize(): Int {
        return oldCurrencies.size
    }

    override fun getNewListSize(): Int {
        return newCurrencies.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCurrencies[oldItemPosition] == newCurrencies[newItemPosition]
    }
}