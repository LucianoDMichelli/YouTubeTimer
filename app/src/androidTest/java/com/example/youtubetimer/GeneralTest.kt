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
class GeneralTest {

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
    fun generalTest() {

        val switchCompat = onView(
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
        switchCompat.perform(scrollTo(), click())

        val textView = onView(
                allOf(
                        withId(R.id.tvDescription),
                        withParent(withParent(withId(R.id.svExtras))),
                        isDisplayed()
                )
        )
        textView.check(matches(isDisplayed()))

        val switchCompat2 = onView(
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
        switchCompat2.perform(scrollTo(), click())

        val switchCompat3 = onView(
                allOf(
                        withId(R.id.swComments), withText("Comments"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.svExtras),
                                        0
                                ),
                                2
                        )
                )
        )
        switchCompat3.perform(scrollTo(), click())

        val recyclerView2 = onView(
                allOf(
                        withId(R.id.rvComments),
                        withParent(withParent(withId(R.id.svExtras))),
                        isDisplayed()
                )
        )
        recyclerView2.check(matches(isDisplayed()))

        val switchCompat4 = onView(
                allOf(
                        withId(R.id.swComments), withText("Comments"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.svExtras),
                                        0
                                ),
                                2
                        )
                )
        )
        switchCompat4.perform(scrollTo(), click())

        val switchCompat5 = onView(
                allOf(
                        withId(R.id.swRelatedVideos), withText("Related Videos"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.svExtras),
                                        0
                                ),
                                4
                        )
                )
        )
        switchCompat5.perform(scrollTo(), click())

        val switchCompat6 = onView(
                allOf(
                        withId(R.id.swRelatedVideos), withText("Related Videos"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.svExtras),
                                        0
                                ),
                                4
                        )
                )
        )
        switchCompat6.perform(scrollTo(), click())

        val floatingActionButton = onView(
                allOf(
                        withId(R.id.btnBlackScreen),
                        withContentDescription("Turns screen black (less light for when you are trying to sleep and saves power on OLED screens)"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        floatingActionButton.perform(click())

        val floatingActionButton2 = onView(
                allOf(
                        withId(R.id.btnBlackScreen),
                        withContentDescription("Turns screen black (less light for when you are trying to sleep and saves power on OLED screens)"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        floatingActionButton2.perform(click())

        val floatingActionButton3 = onView(
                allOf(
                        withId(R.id.btnPreferences),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0
                                ),
                                7
                        ),
                        isDisplayed()
                )
        )
        floatingActionButton3.perform(click())

        val recyclerView = onView(
                allOf(
                        withId(R.id.recycler_view),
                        childAtPosition(
                                withId(android.R.id.list_container),
                                0
                        )
                )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        pressBack()

        val floatingActionButton4 = onView(
                allOf(
                        withId(R.id.btnTimer), withContentDescription("Set timer"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        floatingActionButton4.perform(click())

        val materialButton = onView(
                allOf(
                        withId(R.id.btn5), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.TableLayout")),
                                        1
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        materialButton.perform(click())

        val materialButton2 = onView(
                allOf(
                        withId(R.id.btn5), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.TableLayout")),
                                        1
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        materialButton2.perform(click())

        val appCompatImageButton = onView(
                allOf(
                        withId(R.id.btnBackspace), withContentDescription("Backspace"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.TableLayout")),
                                        3
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        appCompatImageButton.perform(click())

        val appCompatImageButton2 = onView(
                allOf(
                        withId(R.id.btnBackspace), withContentDescription("Backspace"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.TableLayout")),
                                        3
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        appCompatImageButton2.perform(click())

        val materialButton3 = onView(
                allOf(withId(R.id.btn1), withText("1"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.TableLayout")),
                                        0),
                                0),
                        isDisplayed()))
        materialButton3.perform(click())

        val materialButton4 = onView(
                allOf(withId(R.id.btn5), withText("5"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.TableLayout")),
                                        1),
                                1),
                        isDisplayed()))
        materialButton4.perform(click())

        val appCompatImageButton3 = onView(
                allOf(withId(R.id.btnStart), withContentDescription("Start Timer"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.TableLayout")),
                                        3),
                                2),
                        isDisplayed()))
        appCompatImageButton3.perform(click())

        val floatingActionButton5 = onView(
                allOf(withId(R.id.btnTimer), withContentDescription("Set timer"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()))
        floatingActionButton5.perform(click())

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
