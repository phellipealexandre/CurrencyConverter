package com.phellipesilva.currencyconverter.view.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.phellipesilva.currencyconverter.R
import com.phellipesilva.currencyconverter.models.Rate
import kotlinx.android.synthetic.main.currency_rate_list_item.view.*
import java.util.*
import androidx.recyclerview.widget.DiffUtil
import com.phellipesilva.currencyconverter.view.extensions.requestFocusWithKeyboard
import java.text.DecimalFormat

class CurrencyRatesAdapter(
    private val context: Context
) : RecyclerView.Adapter<CurrencyRatesAdapter.CurrencyRatesViewHolder>() {

    private val doubleTwoDigitsFormat = "0.00"
    private var currencyRatesList = LinkedList<Rate>()
    private lateinit var onChangedListener: (List<Rate>) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRatesViewHolder {
        return CurrencyRatesViewHolder(
            LayoutInflater.from(context).inflate(R.layout.currency_rate_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return currencyRatesList.size
    }

    override fun onBindViewHolder(holder: CurrencyRatesViewHolder, position: Int) {
        val rate = currencyRatesList[position]
        holder.txtRateName.text = rate.rateName
        holder.edtRateValue.setText(DecimalFormat(doubleTwoDigitsFormat).format(rate.rateValue))

        holder.itemView.setOnClickListener { moveClickedRateToFirstPositionIndex(holder, position) }
    }

    fun updateData(newRates: List<Rate>) {
        val diffCallback = CurrencyRateDiffCallback(this.currencyRatesList, newRates)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        diffResult.dispatchUpdatesTo(this)

        this.currencyRatesList = LinkedList(newRates)
    }

    fun setOnPositionChangedListener(listener: (List<Rate>) -> Unit) {
        this.onChangedListener = listener
    }

    private fun moveClickedRateToFirstPositionIndex(holder: CurrencyRatesViewHolder, position: Int) {
        val newList = LinkedList(currencyRatesList)
        val rate = newList.removeAt(position)
        newList.addFirst(rate)

        this.updateData(newList)
        holder.edtRateValue.requestFocusWithKeyboard()

        if (::onChangedListener.isInitialized) {
            onChangedListener.invoke(currencyRatesList)
        }
    }

    class CurrencyRatesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRateName: TextView = view.txtRateName
        val edtRateValue: EditText = view.edtRateValue
    }
}

