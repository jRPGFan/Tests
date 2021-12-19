package com.geekbrains.tests.automator

import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class OpenOtherAppsTest {
    private val uiDevice: UiDevice = UiDevice.getInstance(getInstrumentation())

    @Test
    fun test_OpenSettings() {
        uiDevice.pressHome()
        uiDevice.swipe(uiDevice.displayWidth / 2, uiDevice.displayHeight - 50,
            uiDevice.displayHeight / 2, 0, 10)
        val appViews = UiScrollable(UiSelector().scrollable(true))
        val settingsApp = appViews.getChildByText(UiSelector().className(TextView::class.java.name), "Settings")
        settingsApp.clickAndWaitForNewWindow()

        val settingsValidation = uiDevice.findObject(UiSelector().packageName("com.android.settings"))
        Assert.assertTrue(settingsValidation.exists())
    }
}