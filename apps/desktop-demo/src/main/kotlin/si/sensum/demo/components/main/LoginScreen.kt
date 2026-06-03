package si.sensum.demo.components.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.eltratec_logo

@Composable
fun LoginScreen(
    onLogin: suspend (username: String, password: String) -> Unit,
    onLoginSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun submitLogin() {
        if (isLoading) return

        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Username and password are required."
            return
        }

        scope.launch {
            isLoading = true
            errorMessage = ""

            runCatching {
                onLogin(username.trim(), password)
            }.onSuccess {
                onLoginSuccess()
            }.onFailure {
                errorMessage = "Login failed. Check credentials or API status."
            }

            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 440.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    LoginHeader()

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = ""
                        },
                        label = { Text("Username") },
                        singleLine = true,
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 58.dp)
                            .onPreviewKeyEvent { event ->
                                if (
                                    (event.key == Key.Enter || event.key == Key.NumPadEnter) &&
                                    event.type == KeyEventType.KeyDown
                                ) {
                                    submitLogin()
                                    true
                                } else {
                                    false
                                }
                            },
                        colors = loginTextFieldColors()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        label = { Text("Password") },
                        singleLine = true,
                        enabled = !isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 58.dp)
                            .onPreviewKeyEvent { event ->
                                if (
                                    (event.key == Key.Enter || event.key == Key.NumPadEnter) &&
                                    event.type == KeyEventType.KeyDown
                                ) {
                                    submitLogin()
                                    true
                                } else {
                                    false
                                }
                            },
                        colors = loginTextFieldColors()
                    )

                    Button(
                        onClick = {
                            submitLogin()
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SensumThemeColors.accent,
                            disabledContainerColor = SensumThemeColors.accentMuted
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = SensumThemeColors.onAccent,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Login",
                                color = SensumThemeColors.onAccent,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    if (errorMessage.isNotBlank()) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = SensumThemeColors.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            LoginFooter()
        }
    }
}

@Composable
private fun LoginHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        StretchedLoginTitle(
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(color = SensumThemeColors.border)

        Text(
            text = "Login into Desktop App",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = SensumThemeColors.muted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StretchedLoginTitle(
    modifier: Modifier = Modifier
) {
    val text = "SENSUM"

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        text.forEach { character ->
            Text(
                text = character.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = SensumThemeColors.accent
            )
        }
    }
}

@Composable
private fun LoginFooter() {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "Powered by",
                style = MaterialTheme.typography.labelSmall,
                color = SensumThemeColors.muted.copy(alpha = 0.75f)
            )

            Image(
                painter = painterResource(Res.drawable.eltratec_logo),
                contentDescription = "Eltratec logo",
                modifier = Modifier
                    .width(75.dp)
                    .height(75.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun loginTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SensumThemeColors.accent,
    unfocusedBorderColor = SensumThemeColors.border,
    focusedLabelColor = SensumThemeColors.accent,
    unfocusedLabelColor = SensumThemeColors.muted,
    cursorColor = SensumThemeColors.accent,
    focusedTextColor = SensumThemeColors.onSurface,
    unfocusedTextColor = SensumThemeColors.onSurface
)