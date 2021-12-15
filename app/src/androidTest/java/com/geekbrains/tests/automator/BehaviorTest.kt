package com.geekbrains.tests.automator

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class BehaviorTest {
    private val uiDevice: UiDevice = UiDevice.getInstance(getInstrumentation())
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val packageName = context.packageName

    @Before
    fun setup() {
        uiDevice.pressHome()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        uiDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), TIMEOUT)
    }

    @Test
    fun test_MainActivityIsStarted() {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        Assert.assertNotNull(editText)
    }

    @Test
    fun test_SearchIsPositive() {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        editText.text = "UiAnimator"
        val searchButton = uiDevice.findObject(By.res(packageName, "searchButton"))
        searchButton.click()

        val changedText = uiDevice.wait(Until.findObject(By
            .res(packageName, "totalCountTextView")), TIMEOUT)
        Assert.assertEquals(changedText.text.toString(), "Number of results: 4")
        Assert.assertNotEquals(changedText.text.toString(), "Number of results: 0")
    }

    @Test
    fun test_OpenDetailsScreen() {
        val toDetails = uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))
        toDetails.click()
        val changedText = uiDevice.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")), TIMEOUT)
        Assert.assertEquals(changedText.text, "Number of results: 0")
        Assert.assertNotEquals(changedText.text, "Number of results: 4")
    }

    @Test
    fun test_SearchIsPositive_OpenDetailsScreen_ResultIsCorrect() {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        editText.text = "UiAnimator"
        val searchButton = uiDevice.findObject(By.res(packageName, "searchButton"))
        searchButton.click()

        val changedTextMain = uiDevice.wait(Until.findObject(By
            .res(packageName, "totalCountTextView")), TIMEOUT)
        Assert.assertEquals(changedTextMain.text.toString(), "Number of results: 4")

        val toDetails = uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))
        toDetails.click()
        val changedTextDetails = uiDevice.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")), TIMEOUT)
        Assert.assertEquals(changedTextDetails.text, "Number of results: 4")
        Assert.assertNotEquals(changedTextDetails.text, "4")
    }

    @Test
    fun test_DetailsIncrementButton() {
        val toDetails = uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))
        toDetails.click()
        val changedTextDetails = uiDevice.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")), TIMEOUT)
        val incrementButton = uiDevice.findObject(By.res(packageName, "incrementButton"))
        incrementButton.click()
        Assert.assertEquals(changedTextDetails.text, "Number of results: 1")
        Assert.assertNotEquals(changedTextDetails.text, "Number of results: 0")
    }

    @Test
    fun test_DetailsDecrementButton() {
        val toDetails = uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))
        toDetails.click()
        val changedTextDetails = uiDevice.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")), TIMEOUT)
        val decrementButton = uiDevice.findObject(By.res(packageName, "decrementButton"))
        decrementButton.click()
        Assert.assertEquals(changedTextDetails.text, "Number of results: -1")
        Assert.assertNotEquals(changedTextDetails.text, "Number of results: 0")
    }

    @Test
    fun test_MainRecyclerViewScrollable() {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        editText.text = "algol"
        val searchButton = uiDevice.findObject(By.res(packageName, "searchButton"))
        searchButton.click()
        val recyclerView = UiScrollable(UiSelector().className("androidx.recyclerview.widget.RecyclerView"))
        recyclerView.swipeUp(5)
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}