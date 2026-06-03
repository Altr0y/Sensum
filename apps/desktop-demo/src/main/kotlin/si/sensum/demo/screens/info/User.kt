package si.sensum.demo.screens.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.layout.ScreenContainer
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumCard
import si.sensum.demo.components.ui.SensumPasswordField
import si.sensum.demo.components.ui.SensumTextField
// TODO: USER ADD SCREEN, CUSTOMER ADD
@Composable
fun User() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }

    ScreenContainer {
        Text(
            text = "User",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Local demo user placeholder",
            style = MaterialTheme.typography.bodyMedium,
            color = SensumThemeColors.muted
        )

        HorizontalDivider(color = SensumThemeColors.border)

        SensumCard(
            modifier = Modifier.widthIn(max = 420.dp),
            fillMaxWidth = false
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SensumTextField(
                    value = username,
                    onValueChange = { value ->
                        username = value
                        loginMessage = ""
                    },
                    label = "Username",
                    modifier = Modifier.fillMaxWidth()
                )

                SensumPasswordField(
                    value = password,
                    onValueChange = { value ->
                        password = value
                        loginMessage = ""
                    },
                    label = "Password",
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SensumButton(
                        text = "Login",
                        onClick = {
                            loginMessage =
                                if (username.isBlank() || password.isBlank()) {
                                    "Username and password are required."
                                } else {
                                    "Logged in as $username."
                                }
                        }
                    )

                    OutlinedButton(
                        onClick = {
                            username = ""
                            password = ""
                            loginMessage = ""
                        }
                    ) {
                        Text(
                            text = "Clear",
                            color = SensumThemeColors.muted
                        )
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