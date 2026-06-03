package si.sensum.demo.screens.data.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.screens.data.DataScreenState
import si.sensum.demo.screens.data.model.DataEntityType

@Composable
fun ManualDataForm(
    state: DataScreenState
) {
    Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
        when (state.entityType) {
            DataEntityType.ALL -> {
                StationFields(state)
                ChannelFields(state)
                MeasurementFields(state)
            }
            DataEntityType.STATION -> StationFields(state)
            DataEntityType.CHANNEL -> {
                StationFields(state)
                ChannelFields(state)
            }
            DataEntityType.MEASUREMENT -> {
                StationFields(state)
                ChannelFields(state)
                MeasurementFields(state)
            }
        }
    }
}
