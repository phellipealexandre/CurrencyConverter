package com.phellipesilva.currencyconverter.view.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.requestFocusWithKeyboard() {
    requestFocus()
    val systemService = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    systemService.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)

    val textLength = text.length
    this.setSelection(textLength)
}