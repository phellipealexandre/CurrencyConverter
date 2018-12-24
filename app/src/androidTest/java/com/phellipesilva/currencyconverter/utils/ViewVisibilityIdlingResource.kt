package com.phellipesilva.currencyconverter.utils

import android.view.View
import androidx.test.espresso.IdlingResource

class ViewVisibilityIdlingResource(private val view: View) : IdlingResource {

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return this::class.java.name
    }

    override fun isIdleNow(): Boolean {
        if (view.visibility == View.VISIBLE) {
            this.resourceCallback?.onTransitionToIdle()
            return true
        }

        return false
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback
    }
}