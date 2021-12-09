package com.geekbrains.tests

import android.os.Build
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.view.search.MainActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class MainActivityTest {
    lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun close() { scenario.close() }

    @Test
    fun activitySearchEditText_HasText() {
        scenario.onActivity {
            val searchEditText = it.findViewById<EditText>(R.id.searchEditText)
            val testText = "test"
            searchEditText.setText(testText, TextView.BufferType.EDITABLE)
            assertNotNull(searchEditText.text)
            assertEquals(testText, searchEditText.text.toString())
        }
    }
}