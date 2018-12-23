package com.phellipesilva.currencyconverter.view

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencyConverterActivityTest {

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<CurrencyConverterActivity> = ActivityTestRule(CurrencyConverterActivity::class.java)

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun shouldLoadCurrenciesFromServer() {
        startServerWithJsonFileResponse("json-responses/simple_response.json")
    }

    private fun startServerWithJsonFileResponse(jsonFilePath: String) {
        val json = readJsonFromResources(jsonFilePath)
        val mockResponse = MockResponse().setBody(json)
        server.enqueue(mockResponse)
        server.start(4040)
    }

    private fun readJsonFromResources(fileName: String): String {
        return this.javaClass.classLoader
            .getResourceAsStream(fileName)
            .bufferedReader().use { it.readText() }
    }

}