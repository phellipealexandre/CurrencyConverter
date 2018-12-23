package com.phellipesilva.currencyconverter.service

import com.google.common.truth.Truth.assertThat
import io.reactivex.observers.TestObserver
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyRatesServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var currencyRatesService: CurrencyRatesService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start(4040)
        server.url("/latest?base=EUR")

        currencyRatesService = Retrofit.Builder()
            .baseUrl("http://localhost:4040")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(CurrencyRatesService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun shouldReceiveAndParseResponseWhenServiceCallIsMade() {
        val testObserver = TestObserver<RemoteCurrencyRates>()
        val json = readJsonFromResources("json-responses/simple_response.json")
        val mockResponse = MockResponse().setBody(json)
        server.enqueue(mockResponse)

        val ratesObservable = currencyRatesService.getRates("EUR")
        ratesObservable.subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertValue { it.base == "EUR" }
        testObserver.assertValue { it.date == "2018-09-06" }
        testObserver.assertValue { it.rates["AUD"] == 1.6245 }
    }

    @Test
    fun shouldBuildCorrectQueryParameterWithBaseRateParameter() {
        val testObserver = TestObserver<RemoteCurrencyRates>()
        val json = readJsonFromResources("json-responses/simple_response.json")
        val mockResponse = MockResponse().setBody(json)
        server.enqueue(mockResponse)

        val ratesObservable = currencyRatesService.getRates("XXX")
        ratesObservable.subscribe(testObserver)

        val recordedRequest = server.takeRequest()
        assertThat(recordedRequest.path).isEqualTo("/latest?base=XXX")
    }

    private fun readJsonFromResources(fileName: String): String {
        return this.javaClass
            .classLoader
            ?.getResourceAsStream(fileName)
            ?.bufferedReader().use { it!!.readText() }
    }
}