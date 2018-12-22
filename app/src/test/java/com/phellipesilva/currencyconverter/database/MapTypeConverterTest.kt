package com.phellipesilva.currencyconverter.database

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class MapTypeConverterTest {

    private lateinit var mapTypeConverter: MapTypeConverter

    @Before
    fun setUp() {
        mapTypeConverter = MapTypeConverter()
    }

    @Test
    fun shouldReturnEmptyJsonStringWhenConvertingEmptyMap() {
        val returnedString = mapTypeConverter.fromMapToString(mapOf())

        assertThat(returnedString).isEqualTo("{}")
    }

    @Test
    fun shouldPerformASimpleMapConversionWhenMapHasOneElement() {
        val returnedString = mapTypeConverter.fromMapToString(mapOf("Key" to 1.5))

        assertThat(returnedString).isEqualTo("""{"Key":1.5}""")
    }

    @Test
    fun shouldPerformAMapConversionWhenMapHasNElements() {
        val returnedString = mapTypeConverter.fromMapToString(mapOf("Key1" to 1.0, "Ke2" to 1.2, "Key3" to 1.5))

        assertThat(returnedString).isEqualTo("""{"Key1":1.0,"Ke2":1.2,"Key3":1.5}""")
    }

    @Test
    fun shouldReturnEmptyMapWhenConvertingEmptyJsonString() {
        val returnedMap = mapTypeConverter.fromStringToMap("{}")

        assertThat(returnedMap).isEqualTo(mapOf<String, Double>())
    }

    @Test
    fun shouldPerformASimpleStringConversionWhenStringHasOneElement() {
        val returnedMap = mapTypeConverter.fromStringToMap("""{"Key":1.5}""")

        assertThat(returnedMap).isEqualTo(mapOf("Key" to 1.5))
    }

    @Test
    fun shouldPerformAStringConversionWhenStringHasNElements() {
        val returnedMap = mapTypeConverter.fromStringToMap("""{"Key1":1.0,"Ke2":1.2,"Key3":1.5}""")

        assertThat(returnedMap).isEqualTo(mapOf("Key1" to 1.0, "Ke2" to 1.2, "Key3" to 1.5))
    }
}
