package com.kay.forecast.ui

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.kay.forecast.R
import com.kay.forecast.fakeForeCastWrapper
import org.junit.After
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@LargeTest
@Config(sdk = [28])
class MainActivityTest {

    private lateinit var scenario: ActivityScenario<MainActivity>


    @Before
    fun setUp() {
        scenario = launchActivity<MainActivity>()
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `when receive uiState Success , should display list data`() {
        scenario.onActivity { it.onUiStateChange(UiState.Success(fakeForeCastWrapper())) }

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.scrollToPosition<ForecastAdapter.ForecastViewHolder>(0)
        )

        val date = "Date: Thu, 18 Jun 2020"
        val avgTemp = "Average Temperature: 30℃"
        val pressure = "Pressure: 1005"
        val humidity = "Humidity: 80%"
        val desc = "Description: It's gonna rain"

        onView(withText(date)).check(matches(isDisplayed()))
        onView(withText(avgTemp)).check(matches(isDisplayed()))
        onView(withText(pressure)).check(matches(isDisplayed()))
        onView(withText(humidity)).check(matches(isDisplayed()))
        onView(withText(desc)).check(matches(isDisplayed()))

    }
}