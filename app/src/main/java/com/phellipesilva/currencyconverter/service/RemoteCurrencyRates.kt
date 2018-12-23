package com.phellipesilva.currencyconverter.service

data class RemoteCurrencyRates(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)