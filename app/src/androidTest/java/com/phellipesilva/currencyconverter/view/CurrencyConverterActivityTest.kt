package com.phellipesilva.currencyconverter.view

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.phellipesilva.currencyconverter.R
import com.phellipesilva.currencyconverter.utils.RecyclerViewMatcher.nthChildOf
import com.phellipesilva.currencyconverter.utils.ViewVisibilityIdlingResource
import com.phellipesilva.currencyconverter.view.recyclerView.CurrencyRatesAdapter
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencyConverterActivityTest {

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<CurrencyConverterActivity> =
        ActivityTestRule(CurrencyConverterActivity::class.java, false, false)

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
    fun shouldStartFetchingWithProgressBar() {
        startServerWithJsonFileResponse("json-responses/simple_response.json")
        activityRule.launchActivity(Intent())

        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldLoadCurrencyRatesFromServer() {
        startServerWithJsonFileResponse("json-responses/simple_response.json")
        val launchActivity = activityRule.launchActivity(Intent())
        val viewVisibilityIdlingResource = ViewVisibilityIdlingResource(launchActivity.recyclerView)
        IdlingRegistry.getInstance().register(viewVisibilityIdlingResource)

        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("EUR"))))
        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("100.00"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("AUD"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("162.45"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("BGN"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("196.55"))))

        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))

        IdlingRegistry.getInstance().unregister(viewVisibilityIdlingResource)
    }

    @Test
    fun shouldPutClickedItemOnTopOfList() {
        startServerWithJsonFileResponse("json-responses/simple_response.json")
        val launchActivity = activityRule.launchActivity(Intent())
        val viewVisibilityIdlingResource = ViewVisibilityIdlingResource(launchActivity.recyclerView)
        IdlingRegistry.getInstance().register(viewVisibilityIdlingResource)

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<CurrencyRatesAdapter.CurrencyRatesViewHolder>(1, click()))

        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("AUD"))))
        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("162.45"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("EUR"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("100.00"))))

        IdlingRegistry.getInstance().unregister(viewVisibilityIdlingResource)
    }

    @Test
    fun shouldSlideItemsFromRecyclerViewWhenPuttingItemInTop() {
        startServerWithJsonFileResponse("json-responses/simple_response.json")
        val launchActivity = activityRule.launchActivity(Intent())
        val viewVisibilityIdlingResource = ViewVisibilityIdlingResource(launchActivity.recyclerView)
        IdlingRegistry.getInstance().register(viewVisibilityIdlingResource)

        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("EUR"))))
        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("100.00"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("AUD"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("162.45"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("BGN"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("196.55"))))
        onView(nthChildOf(withId(R.id.recyclerView), 3)).check(matches(hasDescendant(withText("BRL"))))
        onView(nthChildOf(withId(R.id.recyclerView), 3)).check(matches(hasDescendant(withText("481.57"))))

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<CurrencyRatesAdapter.CurrencyRatesViewHolder>(3, click()))

        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("BRL"))))
        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("481.57"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("EUR"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("100.00"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("AUD"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("162.45"))))
        onView(nthChildOf(withId(R.id.recyclerView), 3)).check(matches(hasDescendant(withText("BGN"))))
        onView(nthChildOf(withId(R.id.recyclerView), 3)).check(matches(hasDescendant(withText("196.55"))))

        IdlingRegistry.getInstance().unregister(viewVisibilityIdlingResource)
    }

    @Test
    fun shouldScrollToTopWhenClickedElementIsNotOnRange() {
        startServerWithJsonFileResponse("json-responses/simple_response.json")
        val launchActivity = activityRule.launchActivity(Intent())
        val viewVisibilityIdlingResource = ViewVisibilityIdlingResource(launchActivity.recyclerView)
        IdlingRegistry.getInstance().register(viewVisibilityIdlingResource)

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<CurrencyRatesAdapter.CurrencyRatesViewHolder>(32, click()))

        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("ZAR"))))
        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("1791.20"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("EUR"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("100.00"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("AUD"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("162.45"))))
        onView(nthChildOf(withId(R.id.recyclerView), 3)).check(matches(hasDescendant(withText("BGN"))))
        onView(nthChildOf(withId(R.id.recyclerView), 3)).check(matches(hasDescendant(withText("196.55"))))

        IdlingRegistry.getInstance().unregister(viewVisibilityIdlingResource)
    }

    @Test
    fun shouldChangeOtherCurrencyValuesWhenChangingBaseValue() {
        startServerWithJsonFileResponse("json-responses/simple_response.json")
        val launchActivity = activityRule.launchActivity(Intent())
        val viewVisibilityIdlingResource = ViewVisibilityIdlingResource(launchActivity.recyclerView)
        IdlingRegistry.getInstance().register(viewVisibilityIdlingResource)

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<CurrencyRatesAdapter.CurrencyRatesViewHolder>(0, click()))

        onView(allOf(withId(R.id.edtRateValue), withText("100.00"))).perform(replaceText("200.00"))
        Espresso.closeSoftKeyboard()

        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("EUR"))))
        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("200.00"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("AUD"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("324.90"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("BGN"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("393.10"))))

        IdlingRegistry.getInstance().unregister(viewVisibilityIdlingResource)
    }

    @Test
    fun shouldPutZeroPlaceHolderWhenBaseValueIsEmpty() {
        startServerWithJsonFileResponse("json-responses/simple_response.json")
        val launchActivity = activityRule.launchActivity(Intent())
        val viewVisibilityIdlingResource = ViewVisibilityIdlingResource(launchActivity.recyclerView)
        IdlingRegistry.getInstance().register(viewVisibilityIdlingResource)

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<CurrencyRatesAdapter.CurrencyRatesViewHolder>(0, click()))

        onView(allOf(withId(R.id.edtRateValue), withText("100.00"))).perform(clearText())
        Espresso.closeSoftKeyboard()

        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("EUR"))))
        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText(""))))
        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withHint("0"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("AUD"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("0.00"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("BGN"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("0.00"))))

        IdlingRegistry.getInstance().unregister(viewVisibilityIdlingResource)
    }

    @Test
    fun shouldMaintainCurrencyValuesWhenDeviceIsOnLandscape() {
        startServerWithJsonFileResponse("json-responses/simple_response.json")
        val launchActivity = activityRule.launchActivity(Intent())
        val viewVisibilityIdlingResource = ViewVisibilityIdlingResource(launchActivity.recyclerView)
        IdlingRegistry.getInstance().register(viewVisibilityIdlingResource)

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<CurrencyRatesAdapter.CurrencyRatesViewHolder>(0, click()))

        onView(allOf(withId(R.id.edtRateValue), withText("100.00"))).perform(replaceText("200.00"))
        Espresso.closeSoftKeyboard()
        launchActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("EUR"))))
        onView(nthChildOf(withId(R.id.recyclerView), 0)).check(matches(hasDescendant(withText("200.00"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("AUD"))))
        onView(nthChildOf(withId(R.id.recyclerView), 1)).check(matches(hasDescendant(withText("324.90"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("BGN"))))
        onView(nthChildOf(withId(R.id.recyclerView), 2)).check(matches(hasDescendant(withText("393.10"))))

        IdlingRegistry.getInstance().unregister(viewVisibilityIdlingResource)
    }

    @Test
    fun shouldMaintainScrollStateWhenDeviceIsOnLandscape() {
        startServerWithJsonFileResponse("json-responses/simple_response.json")
        val launchActivity = activityRule.launchActivity(Intent())
        val viewVisibilityIdlingResource = ViewVisibilityIdlingResource(launchActivity.recyclerView)
        IdlingRegistry.getInstance().register(viewVisibilityIdlingResource)

        onView(withId(R.id.recyclerView))
            .perform(actionOnItemAtPosition<CurrencyRatesAdapter.CurrencyRatesViewHolder>(15, scrollTo()))

        onView(withText("HUF")).check(matches(isDisplayed()))
        onView(withText("32812.00")).check(matches(isDisplayed()))

        launchActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onView(withText("HUF")).check(matches(isDisplayed()))
        onView(withText("32812.00")).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(viewVisibilityIdlingResource)
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