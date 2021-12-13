package com.geekbrains.tests

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import view.search.MainActivity
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() { scenario = ActivityScenario.launch(MainActivity::class.java) }

    @After
    fun close() { scenario.close() }

    @Test
    fun activitySearchEditText_HintShowing() {
        onView(withId(R.id.searchEditText)).check(matches(withHint("Enter keyword e.g. android")))
    }

    @Test
    fun activitySearch_IsWorking() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"), closeSoftKeyboard())
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())

        onView(isRoot()).perform(delay())
        onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: 2711")))
    }

    private fun delay(): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $2 seconds"
            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(2000)
            }
        }
    }

    @Test
    fun activityProgressBar_ShowingOnSearch() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"), closeSoftKeyboard())
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())
        onView(withId(R.id.progressBar)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun activityToDetailsActivityButton_Visible() {
        onView(withId(R.id.toDetailsActivityButton)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun activityToDetailsActivityButton_TextCorrect() {
        onView(withId(R.id.toDetailsActivityButton)).check(matches(withText("to details")))
    }

    @Test
    fun activityTotalResults_NotShowing() {
        onView(withId(R.id.totalCountTextView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun activityTotalResults_DefaultText() {
        onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: %d")))
    }

    @Test
    fun activityResultsRecyclerView_Visible() {
        onView(withId(R.id.recyclerView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}