package si.sensum.demo.screens.data.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumCard
import si.sensum.demo.components.ui.SensumTextField
import si.sensum.demo.screens.data.DataScreenState
import si.sensum.demo.screens.data.model.DataEntityType

@Composable
fun SwsDataForm(
    state: DataScreenState
) {
    Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
        when (state.entityType) {
            DataEntityType.STATION -> {
                SensumCard {
                    Text(
                        text = "SWS Station load does not need input fields. It calls GET /api/v1/gm/stations.",
                        color = SensumThemeColors.muted
                    )
                }
            }

            DataEntityType.CHANNEL -> {
                SwsStationIdField(state)
            }

            DataEntityType.MEASUREMENT -> {
                SwsStationIdField(state)

                ChannelSelector(
                    state = state,
                    optional = true
                )
            }
        }
    }
}

@Composable
private fun SwsStationIdField(
    state: DataScreenState
) {
    SensumCard {
        Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)) {
            SensumTextField(
                value = state.stationIdText,
                onValueChange = { value ->
                    state.stationIdText = value
                    state.updateSqlPreview()
                },
                label = "Station ID *"
            )

            Text(
                text = "Required for SWS channels and measurements. Maps to SWS field StationID.",
                color = SensumThemeColors.muted
            )
        }
    }
}