package si.sensum.demo.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.background.AppBackgroundStyle
import si.sensum.demo.components.background.AppVectorBackground
import si.sensum.demo.components.layout.ScreenContainer
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumCard
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.layout.SideBarTab

@Composable
fun HomeScreen(
    isDark: Boolean,
    onNavigate: (SideBarTab) -> Unit
) {
    AppVectorBackground(
        isDark = isDark,
        style = AppBackgroundStyle.Network
    ) {
        ScreenContainer(
            scrollable = true,
            verticalArrangement = Arrangement.spacedBy(SensumSpacing.lg)
        ) {
            SensumCard(overlay = true) {
                Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
                    Text(
                        text = "SENSUM · EL3",
                        style = MaterialTheme.typography.headlineMedium,
                        color = SensumThemeColors.onSurface
                    )
                    Text(
                        text = "Digitalni dvojček za postaje, kanale in meritve.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SensumThemeColors.muted
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(SensumSpacing.lg)) {
                HomeTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Dataset,
                    title = "Data",
                    text = "Vnos, generiranje, SWS osveževanje in DSL priprava podatkov.",
                    action = "Open data",
                    onClick = { onNavigate(SideBarTab.DATA) }
                )

                HomeTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Storage,
                    title = "Records",
                    text = "Pregled tabel in filtrov za stations, channels in measurements.",
                    action = "Open records",
                    onClick = { onNavigate(SideBarTab.RECORDS) }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(SensumSpacing.lg)) {
                HomeTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.BarChart,
                    title = "Charts",
                    text = "Berljivi Lets-Plot prikazi po source, kanalih in skupno.",
                    action = "Open charts",
                    onClick = { onNavigate(SideBarTab.MEASUREMENT_CHART) }
                )

                HomeTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.DeviceHub,
                    title = "Digital twin",
                    text = "Vizualni pogled digitalnega dvojčka in meritev.",
                    action = "Open twin",
                    onClick = { onNavigate(SideBarTab.DIGITAL_TWIN) }
                )
            }
        }
    }
}

@Composable
private fun HomeTile(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    text: String,
    action: String,
    onClick: () -> Unit
) {
    SensumCard(modifier = modifier, overlay = true) {
        Column(
            verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(SensumSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SensumThemeColors.accent
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = SensumThemeColors.onSurface
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.muted
            )

            SensumButton(
                text = action,
                onClick = onClick,
                variant = SensumButtonVariant.Secondary
            )
        }
    }
}
