package com.phellipesilva.currencyconverter.view.state

import com.phellipesilva.currencyconverter.R

enum class ViewState(val stringMessageId: Int) {
    UNEXPECTED_ERROR(R.string.activity_error_msg),
    NO_INTERNET_ERROR(R.string.no_internet_error_msg)
}