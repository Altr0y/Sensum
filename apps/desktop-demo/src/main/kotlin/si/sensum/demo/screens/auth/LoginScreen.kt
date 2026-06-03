package si.sensum.demo.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.background.AppBackgroundStyle
import si.sensum.demo.components.background.AppVectorBackground
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.theme.SensumUiScale
import si.sensum.demo.components.ui.AppThemeToggle
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumPasswordField
import si.sensum.demo.components.ui.SensumTextField
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.id_card
import si.sensum.demo.resources.logo_transparent_vector

@Composable
fun LoginScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
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

    AppVectorBackground(
        isDark = isDark,
        style = AppBackgroundStyle.Network
    ) {
        AppThemeToggle(
            isDark = isDark,
            onToggleTheme = onToggleTheme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SensumSizes.themeTogglePadding)
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(SensumSizes.screenPadding),
            contentAlignment = Alignment.Center
        ) {
            val uiScale = SensumSizes.scaleFor(maxWidth)
            val contentWidthFraction = authWidthFraction(maxWidth)

            val formMaxWidth = uiScale.dp(SensumSizes.authFormMaxWidth)
            val formPadding = uiScale.dp(SensumSizes.authFormPadding)

            val footerWidth = uiScale.dp(SensumSizes.authFooterWidth)
            val footerLogoWidth = uiScale.dp(SensumSizes.authFooterLogoWidth)
            val footerLogoHeight = uiScale.dp(SensumSizes.authFooterLogoHeight)

            Column(
                modifier = Modifier
                    .fillMaxWidth(contentWidthFraction)
                    .widthIn(max = formMaxWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    uiScale.dp(SensumSizes.cardSpacing)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(formPadding),
                        verticalArrangement = Arrangement.spacedBy(
                            uiScale.dp(SensumSizes.fieldSpacing)
                        )
                    ) {
                        LoginHeader(uiScale = uiScale)

                        val submitOnEnter = Modifier.onPreviewKeyEvent { event ->
                            if (
                                (event.key == Key.Enter || event.key == Key.NumPadEnter) &&
                                event.type == KeyEventType.KeyDown
                            ) {
                                submitLogin()
                                true
                            } else {
                                false
                            }
                        }

                        SensumTextField(
                            value = username,
                            onValueChange = { newValue ->
                                username = newValue
                                errorMessage = ""
                            },
                            label = "Username",
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(submitOnEnter),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(Res.drawable.id_card),
                                    contentDescription = null,
                                    tint = SensumThemeColors.muted,
                                    modifier = Modifier.size(SensumSizes.fieldIconSize)
                                )
                            }
                        )

                        SensumPasswordField(
                            value = password,
                            onValueChange = { newValue ->
                                password = newValue
                                errorMessage = ""
                            },
                            label = "Password",
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(submitOnEnter)
                        )

                        SensumButton(
                            text = "Login",
                            onClick = { submitLogin() },
                            enabled = !isLoading,
                            isLoading = isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

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

                LoginFooter(
                    modifier = Modifier.width(footerWidth),
                    logoWidth = footerLogoWidth,
                    logoHeight = footerLogoHeight,
                    uiScale = uiScale
                )
            }
        }
    }
}

private fun authWidthFraction(width: Dp): Float {
    return when {
        width >= SensumSizes.breakpointLarge -> SensumSizes.authWidthFractionLarge
        width >= SensumSizes.breakpointMedium -> SensumSizes.authWidthFractionMedium
        width >= SensumSizes.breakpointSmall -> SensumSizes.authWidthFractionSmall
        else -> SensumSizes.authWidthFractionCompact
    }
}

@Composable
private fun LoginHeader(
    uiScale: SensumUiScale
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            uiScale.dp(SensumSizes.headerSpacing)
        )
    ) {
        StretchedLoginTitle(
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(
            color = SensumThemeColors.accent.copy(alpha = 0.55f)
        )

        Text(
            text = "Login into Desktop App",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = uiScale.dp(SensumSizes.authSubtitleTopPadding)),
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
private fun LoginFooter(
    modifier: Modifier = Modifier,
    logoWidth: Dp,
    logoHeight: Dp,
    uiScale: SensumUiScale
) {
    Surface(
        modifier = modifier.heightIn(
            min = logoHeight + uiScale.dp(SensumSizes.authFooterExtraHeight)
        ),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = uiScale.dp(SensumSizes.authFooterHorizontalPadding),
                    vertical = uiScale.dp(SensumSizes.authFooterVerticalPadding)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                uiScale.dp(SensumSpacing.xs)
            )
        ) {
            Text(
                text = "Powered by",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = SensumThemeColors.muted.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )

            Image(
                painter = painterResource(Res.drawable.logo_transparent_vector),
                contentDescription = "Sensum logo",
                modifier = Modifier
                    .width(logoWidth)
                    .height(logoHeight),
                contentScale = ContentScale.Fit
            )
        }
    }
}