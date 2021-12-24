package com.geekbrains.tests

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.view.search.SearchResultAdapter
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.geekbrains.tests.view.search.MainActivity

@RunWith(AndroidJUnit4::class)
class MainActivityRecyclerViewTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() { scenario = ActivityScenario.launch(MainActivity::class.java) }

    @After
    fun close() { scenario.close() }

    @Test
    fun activitySearch_ScrollTo() {
        loadList()
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.scrollTo<SearchResultAdapter.SearchResultViewHolder>(
                hasDescendant(withText("FullName: 55"))
            )
        )
    }

    @Test
    fun activitySearch_PerformClickAtPosition() {
        loadList()
        onView(withId(R.id.recyclerView)).perform(
            actionOnItemAtPosition<SearchResultAdapter.SearchResultViewHolder>(0, click())
        )
    }

    @Test
    fun activitySearch_PerformClickOnItem() {
        loadList()
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.scrollTo<SearchResultAdapter.SearchResultViewHolder>(
                hasDescendant(withText("FullName: 55")))
        )
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItem<SearchResultAdapter.SearchResultViewHolder>(
                hasDescendant(withText("FullName: 50")), click())
        )
    }

    private fun loadList() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"))
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())
    }

    private fun tapOnItemWithId(id: Int) = object : ViewAction {
        override fun getConstraints(): Matcher<View>? {
            return null
        }

        override fun getDescription(): String {
            return "Pressed view with the indicated id"
        }

        override fun perform(uiController: UiController?, view: View) {
            val v = view.findViewById(id) as View
            v.performClick()
        }
    }

    @Test
    fun activitySearch_PerformCustomClick() {
        loadList()
        onView(withId(R.id.recyclerView)).perform(
            actionOnItemAtPosition<SearchResultAdapter.SearchResultViewHolder>(
                0, tapOnItemWithId(R.id.checkbox))
        )
    }
}