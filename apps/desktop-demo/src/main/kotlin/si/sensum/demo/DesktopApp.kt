package si.sensum.demo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

object ApiInfo {
    const val NAME = "Desktop Demo"
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sensum Desktop Demo"
    ) {
        App()
    }
}

@Composable
fun App() {
    MaterialTheme {
        Text("Sensum Desktop Demo started.")
    }
}