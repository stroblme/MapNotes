package com.alex.mapnotes

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.alex.mapnotes.login.signin.SignInActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInValidationTest {
    private val incorrectEmail = "test"
    private val correctEmail = "test@test.com"

    @Rule @JvmField
    val activityRule = ActivityTestRule<SignInActivity>(SignInActivity::class.java)

    @Test
    fun shouldDisplayEmailErrorWhenEmailIsEmpty() {
        onView(withId(R.id.signIn))
                .perform(click())

        onView(withText(R.string.error_email_should_be_valid))
                .check(matches(isDisplayed()))
    }

    @Test
    fun shouldDisplayEmailErrorWhenEmailIsNotCorrect() {
        onView(withId(R.id.email))
                .perform(replaceText(incorrectEmail))

        onView(withId(R.id.signIn))
                .perform(click())

        onView(withText(R.string.error_email_should_be_valid))
                .check(matches(isDisplayed()))
    }

    @Test
    fun shouldDisplayPasswordErrorWhenPasswordIsEmpty() {
        onView(withId(R.id.email))
                .perform(replaceText(correctEmail))

        onView(withId(R.id.signIn))
                .perform(click())

        onView(withText(R.string.error_password_should_not_be_empty))
                .check(matches(isDisplayed()))
    }
}