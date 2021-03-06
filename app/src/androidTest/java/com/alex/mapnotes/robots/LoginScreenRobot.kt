package com.alex.mapnotes.robots

import com.agoda.kakao.intent.KIntent
import com.alex.mapnotes.R
import com.alex.mapnotes.login.LoginActivity

fun loginScreen(func: LoginScreenRobot.() -> Unit) = LoginScreenRobot().apply { func() }

class LoginScreenRobot : BaseTestRobot() {

    fun openSignIn() {
        clickOnView(R.id.signIn)
    }

    fun openSignUp() {
        clickOnView(R.id.signUp)
    }

    fun isSuccessfullyLoaded() {
        KIntent {
            hasComponent(LoginActivity::class.java.name)
        }
    }
}