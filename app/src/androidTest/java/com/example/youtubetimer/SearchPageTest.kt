package com.example.youtubetimer


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class SearchPageTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(SearchPage::class.java)

    @Test
    fun searchPageTest() {
        val searchAutoComplete = onView(
                allOf(
                        withId(R.id.svSearch),
                        isDisplayed()
                )
        )
        searchAutoComplete.perform(replaceText("Chillhop Essentials Spring 2021 [instrumental beats compilation]"), closeSoftKeyboard())

        val searchAutoComplete2 = onView(
                allOf(
                        supportsInputMethods(),
                        isDescendantOfA(withId(R.id.svSearch)),
                        isDisplayed()
                )
        )
        searchAutoComplete2.perform(pressImeActionButton())

        val recyclerView = onView(
                allOf(withId(R.id.rvVideoList),
                        childAtPosition(
                                withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                                2)))
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val textView = onView(
                allOf(withId(R.id.tvTitle),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()))
        textView.check(matches(withText("\uD83C\uDF31 Chillhop Essentials · Spring 2021 [instrumental beats compilation]")))
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
