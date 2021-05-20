package com.example.youtubetimer


import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
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
class RelatedVideoTest {

    private fun getVideoPageTestIntent(): Intent {
        val videoPageTestIntent = Intent(ApplicationProvider.getApplicationContext(), VideoPage::class.java)
        videoPageTestIntent.putExtra("videoTitle", "🌱 Chillhop Essentials · Spring 2021 [instrumental beats compilation]")
        videoPageTestIntent.putExtra("videoInfo",   "UI TESTING IN PROGRESS")
        videoPageTestIntent.putExtra("videoDescription", "UI TESTING IN PROGRESS")
        videoPageTestIntent.putExtra("videoCode", "lve6KTZTKDw")
        videoPageTestIntent.putExtra("isFullscreen", false)
        return videoPageTestIntent
    }

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule<VideoPage>(getVideoPageTestIntent())

    @Test
    fun relatedVideoTest() {

        val floatingActionButton6 = onView(
                allOf(withId(R.id.btnTimer), withContentDescription("Set timer"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()))
        floatingActionButton6.perform(click())

        val materialButton5 = onView(
                allOf(withId(R.id.btn1), withText("1"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.TableLayout")),
                                        0),
                                0),
                        isDisplayed()))
        materialButton5.perform(click())

        val materialButton6 = onView(
                allOf(withId(R.id.btn5), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.TableLayout")),
                                        1),
                                1),
                        isDisplayed()))
        materialButton6.perform(click())

        val appCompatImageButton4 = onView(
                allOf(withId(R.id.btnStart), withContentDescription("Start Timer"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.TableLayout")),
                                        3),
                                2),
                        isDisplayed()))
        appCompatImageButton4.perform(click())

        val switchCompat9 = onView(
                allOf(withId(R.id.swRelatedVideos), withText("Related Videos"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.svExtras),
                                        0),
                                4)))
        switchCompat9.perform(scrollTo(), click())

        val recyclerView4 = onView(
                allOf(withId(R.id.rvRelatedVideos),
                        childAtPosition(
                                withClassName(`is`("android.widget.LinearLayout")),
                                5)))
        recyclerView4.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val switchCompat7 = onView(
                allOf(
                        withId(R.id.swDescription), withText("Description"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.svExtras),
                                        0
                                ),
                                0
                        )
                )
        )
        switchCompat7.perform(scrollTo(), click())

        val textView2 = onView(
                allOf(
                        withId(R.id.tvDescription),
                        withParent(withParent(withId(R.id.svExtras))),
                        isDisplayed()
                )
        )
        textView2.check(matches(isDisplayed()))

        val switchCompat8 = onView(
                allOf(
                        withId(R.id.swDescription), withText("Description"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.svExtras),
                                        0
                                ),
                                0
                        )
                )
        )
        switchCompat8.perform(scrollTo(), click())

        Thread.sleep(15000)

    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

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
