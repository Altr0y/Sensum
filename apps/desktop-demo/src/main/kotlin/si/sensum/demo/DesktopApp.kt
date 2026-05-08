package si.sensum.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.DataLoader
import si.sensum.demo.components.MeasurementChart
import si.sensum.demo.components.DatabasePanel
import androidx.compose.ui.graphics.Color

@Composable
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFe9ecef))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DataLoader()
            MeasurementChart()
            DatabasePanel()
        }
    }
}