package si.sensum.demo.components.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumThemeColors

@Composable
fun Info() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("About page", style = MaterialTheme.typography.titleLarge)

        Text(
            "Sensum Desktop Demo",
            style = MaterialTheme.typography.bodyMedium,
            color = SensumThemeColors.muted
        )

        HorizontalDivider(color = SensumThemeColors.border)

        AboutCard(
            title = "Project",
            text = "Sensum is a data visualization and digital twin system developed for working with station measurement data."
        )

        AboutCard(
            title = "Desktop application",
            text = "This desktop demo loads measurement data, saves it into PostgreSQL, and displays loaded measurements through database and chart views."
        )

        AboutCard(
            title = "Technology",
            text = "Built with Kotlin, Compose Desktop, PostgreSQL, Docker, and Lets-Plot."
        )
    }
}

@Composable
private fun AboutCard(
    title: String,
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = SensumThemeColors.onSurface
            )
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.onBackground
            )
        }
    }
}