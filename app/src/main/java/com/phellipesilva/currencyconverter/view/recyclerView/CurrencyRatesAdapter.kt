package com.phellipesilva.currencyconverter.view.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.phellipesilva.currencyconverter.R
import com.phellipesilva.currencyconverter.database.entity.Currency
import com.phellipesilva.currencyconverter.view.extensions.onContentChange
import com.phellipesilva.currencyconverter.view.extensions.requestFocusWithKeyboard
import kotlinx.android.synthetic.main.currency_rate_list_item.view.*
import java.text.DecimalFormat
import java.util.*

class CurrencyRatesAdapter(
    private val context: Context
) : RecyclerView.Adapter<CurrencyRatesAdapter.CurrencyRatesViewHolder>() {

    private var currencyRatesList = LinkedList<Currency>()
    private lateinit var onPositionChangedListener: (List<Currency>) -> Unit
    private lateinit var onBaseValueChangedListener: (Currency) -> Unit

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

        holder.bind(
            rate,
            { holderPosition: Int -> moveClickedRateToFirstPositionIndex(holderPosition) },
            { holderPosition: Int, value: String -> emitContentChangedEventWhenItemIsInFirstPosition(holderPosition, value) }
        )

    }

    fun updateData(newCurrencies: List<Currency>) {
        val diffCallback = CurrencyRateDiffCallback(this.currencyRatesList, newCurrencies)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        diffResult.dispatchUpdatesTo(this)

        this.currencyRatesList = LinkedList(newCurrencies)
    }

    fun setOnPositionChangedListener(listener: (List<Currency>) -> Unit) {
        this.onPositionChangedListener = listener
    }

    fun setOnBaseValueChangedListener(listener: (Currency) -> Unit) {
        this.onBaseValueChangedListener = listener
    }

    private fun emitContentChangedEventWhenItemIsInFirstPosition(position: Int, newRateValue: String) {
        val rateValue = if (newRateValue.isBlank()) 0.0 else newRateValue.toDouble()
        if (::onBaseValueChangedListener.isInitialized && position == 0 && currencyRatesList.first.currencyValue != rateValue) {
            onBaseValueChangedListener.invoke(Currency(currencyRatesList.first.currencyName, rateValue))
        }
    }

    private fun moveClickedRateToFirstPositionIndex(position: Int) {
        val newList = LinkedList(currencyRatesList)
        val rate = newList.removeAt(position)
        newList.addFirst(rate)

        if (::onPositionChangedListener.isInitialized) {
            onPositionChangedListener.invoke(newList)
        }
    }

    class CurrencyRatesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val doubleTwoDigitsFormat = "0.00"

        val txtRateName: TextView = view.txtRateName
        val edtRateValue: EditText = view.edtRateValue

        fun bind(rate: Currency, onClickListener: (Int) -> Unit, onContentChangedListener: (Int, String) -> Unit) {
            txtRateName.text = rate.currencyName
            setFormattedCurrencyValueWhenFieldIsNotFocused(rate)
            setRowClickListener(onClickListener)
            setContentChangeListener(onContentChangedListener)
        }

        private fun setRowClickListener(onClickListener: (Int) -> Unit) {
            itemView.setOnClickListener {
                edtRateValue.requestFocusWithKeyboard()
                onClickListener.invoke(adapterPosition)
            }
        }

        private fun setContentChangeListener(onContentChangedListener: (Int, String) -> Unit) {
            edtRateValue.onContentChange {
                onContentChangedListener.invoke(adapterPosition, it)
                putCursorInTheEndForFocusedField()
            }
        }

        private fun putCursorInTheEndForFocusedField() {
            if (edtRateValue.isFocused) {
                edtRateValue.setSelection(edtRateValue.text.length)
            }
        }

        private fun setFormattedCurrencyValueWhenFieldIsNotFocused(rate: Currency) {
            if (!edtRateValue.isFocused) {
                edtRateValue.setText(DecimalFormat(doubleTwoDigitsFormat).format(rate.currencyValue))
            }
        }
    }
}

