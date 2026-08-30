package com.forgetmenot

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.forgetmenot.ui.App

fun main() = application {
    val state = rememberWindowState(width = 1120.dp, height = 820.dp)
    Window(onCloseRequest = ::exitApplication, state = state, title = "ForgetMeNot") {
        App()
    }
}
