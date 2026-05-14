package si.sensum.demo.components.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumColors
import si.sensum.demo.components.theme.SensumThemeColors

@Composable
fun User() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Login Page", style = MaterialTheme.typography.titleLarge)

        Text(
            "Local demo login placeholder",
            style = MaterialTheme.typography.bodyMedium,
            color = SensumThemeColors.muted
        )

        HorizontalDivider(color = SensumThemeColors.border)

        Surface(
            modifier = Modifier.widthIn(max = 420.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        loginMessage = ""
                    },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        loginMessage = ""
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            loginMessage =
                                if (username.isBlank() || password.isBlank()) {
                                    "Username and password are required."
                                } else {
                                    "Logged in as $username."
                                }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SensumThemeColors.accent)
                    ) {
                        Text("Login", color = SensumThemeColors.onAccent)
                    }

                    OutlinedButton(
                        onClick = {
                            username = ""
                            password = ""
                            loginMessage = ""
                        }
                    ) {
                        Text("Clear", color = SensumThemeColors.muted)
                    }
                }

                if (loginMessage.isNotBlank()) {
                    Text(
                        text = loginMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (loginMessage.startsWith("Logged")) {
                            SensumThemeColors.success
                        } else {
                            SensumThemeColors.error
                        }
                    )
                }
            }
        }
    }
}