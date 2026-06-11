package si.sensum.demo

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.layout.SideBarTab
import si.sensum.demo.components.theme.SensumTheme
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumDialog
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.eltratec_logo
import java.awt.EventQueue

fun main() {
    Thread.setDefaultUncaughtExceptionHandler { thread, error ->
        printSensumError(
            title = "Uncaught application error",
            threadName = thread.name,
            error = error
        )
    }

    try {
        application {
            val appStateHolder = remember { SensumAppStateHolder() }
            val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
            val trayState = rememberTrayState()
            val icon = painterResource(Res.drawable.eltratec_logo)

            var isDark by remember { mutableStateOf(true) }
            var isWindowVisible by remember { mutableStateOf(true) }
            var errorDialogMessage by remember { mutableStateOf<String?>(null) }

            DisposableEffect(Unit) {
                val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

                Thread.setDefaultUncaughtExceptionHandler { thread, error ->
                    printSensumError(
                        title = "Uncaught application error",
                        threadName = thread.name,
                        error = error
                    )

                    EventQueue.invokeLater {
                        errorDialogMessage = error.toSensumDialogMessage()
                        isWindowVisible = true
                    }
                }

                onDispose {
                    Thread.setDefaultUncaughtExceptionHandler(previousHandler)
                }
            }

            Tray(
                icon = icon,
                state = trayState,
                tooltip = "Sensum",
                menu = {
                    Item(
                        text = if (isWindowVisible) "Hide" else "Show",
                        onClick = {
                            isWindowVisible = !isWindowVisible
                        }
                    )

                    if (appStateHolder.uiState.isLoggedIn) {
                        Separator()

                        SideBarTab.entries.forEach { tab ->
                            Item(
                                text = tab.title,
                                onClick = {
                                    runSafely(
                                        actionName = "Tray tab selection",
                                        onError = { error ->
                                            errorDialogMessage = error.toSensumDialogMessage()
                                            isWindowVisible = true
                                        }
                                    ) {
                                        appStateHolder.selectTab(tab)
                                        isWindowVisible = true
                                    }
                                }
                            )
                        }

                        Separator()

                        Item(
                            text = "Logout",
                            onClick = {
                                runSafely(
                                    actionName = "Tray logout dialog",
                                    onError = { error ->
                                        errorDialogMessage = error.toSensumDialogMessage()
                                        isWindowVisible = true
                                    }
                                ) {
                                    appStateHolder.showLogoutDialog()
                                    isWindowVisible = true
                                }
                            }
                        )
                    }

                    Separator()

                    Item(
                        text = "Exit",
                        onClick = {
                            exitApplication()
                        }
                    )
                }
            )

            if (isWindowVisible) {
                Window(
                    onCloseRequest = {
                        isWindowVisible = false
                    },
                    title = "Sensum",
                    icon = icon,
                    state = windowState
                ) {
                    SensumTheme(isDark = isDark) {
                        App(
                            stateHolder = appStateHolder,
                            isDark = isDark,
                            onToggleTheme = { isDark = !isDark }
                        )

                        errorDialogMessage?.let { message ->
                            SensumDialog(
                                title = "Application error",
                                message = message,
                                confirmText = "OK",
                                dismissText = "Close",
                                confirmVariant = SensumButtonVariant.Primary,
                                onConfirm = {
                                    errorDialogMessage = null
                                },
                                onDismiss = {
                                    errorDialogMessage = null
                                }
                            )
                        }
                    }
                }
            }
        }
    } catch (error: Throwable) {
        printSensumError(
            title = "Fatal startup error",
            threadName = Thread.currentThread().name,
            error = error
        )

        println()
        println("Program se ni zaprl brez izpisa. Pritisni ENTER za izhod.")
        readlnOrNull()
    }
}

private fun runSafely(
    actionName: String,
    onError: (Throwable) -> Unit,
    block: () -> Unit
) {
    runCatching {
        block()
    }.onFailure { error ->
        printSensumError(
            title = actionName,
            threadName = Thread.currentThread().name,
            error = error
        )

        onError(error)
    }
}

private fun printSensumError(
    title: String,
    threadName: String,
    error: Throwable
) {
    println()
    println("========================================")
    println("[SENSUM DESKTOP] $title")
    println("Thread: $threadName")
    println("Type: ${error::class.qualifiedName}")
    println("Message: ${error.message ?: "-"}")
    println("========================================")
    error.printStackTrace()
    println("========================================")
    println()
}

private fun Throwable.toSensumDialogMessage(): String {
    val type = this::class.simpleName ?: "Unknown error"
    val message = this.message ?: "No error message."

    return "$type\n\n$message".take(MAX_ERROR_DIALOG_LENGTH)
}

private const val MAX_ERROR_DIALOG_LENGTH = 1800