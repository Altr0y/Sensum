package si.sensum.demo.screens.data.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import si.sensum.demo.screens.data.DataScreenState

@Composable
fun SwsDataForm(
    state: DataScreenState
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StationFields(state)
        ChannelSelector(state)
    }
}
