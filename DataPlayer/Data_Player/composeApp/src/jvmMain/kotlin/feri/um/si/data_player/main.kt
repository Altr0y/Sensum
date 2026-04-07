package feri.um.si.data_player

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "data_player",
    ) {
        AppTest()
    }
}