package com.phellipesilva.currencyconverter.view.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.requestFocusWithKeyboard() {
    requestFocus()
    val systemService = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    systemService.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)

    val textLength = text.length
    this.setSelection(textLength)
}

fun EditText.onContentChange(callback: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) { callback(s.toString()) }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}