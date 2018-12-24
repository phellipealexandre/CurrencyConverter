package com.phellipesilva.currencyconverter.view.state

import com.phellipesilva.currencyconverter.R

enum class ViewState(val stringMessageId: Int, val progressVisible: Boolean) {
    UNEXPECTED_ERROR(R.string.activity_error_msg, false),
    NO_INTERNET_ERROR(R.string.no_internet_error_msg, false),
    RECALCULATING_RATES(R.string.recalculating_rates_msg, true),
}