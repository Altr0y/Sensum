package si.sensum.demo.screens.data.components

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumCard
import si.sensum.demo.components.ui.SensumTextField
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.screens.data.DataScreenState

@Composable
fun DataPreviewPanel(
    state: DataScreenState,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)
    ) {
        SensumCard {
            Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Preview",
                        style = MaterialTheme.typography.titleSmall,
                        color = SensumThemeColors.onSurface
                    )

                    SensumButton(
                        text = "Open",
                        onClick = state::openCurrentRecords,
                        variant = SensumButtonVariant.Secondary
                    )
                }

                PreviewTable(
                    rows = state.generatedPreview
                )
            }
        }

        SensumCard {
            Column(verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
                Row(horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm)) {
                    Text(
                        text = "SQL",
                        style = MaterialTheme.typography.titleSmall,
                        color = SensumThemeColors.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    SensumButton(
                        text = "Copy",
                        onClick = {
                            clipboard.setText(
                                AnnotatedString(state.sqlText)
                            )
                        },
                        variant = SensumButtonVariant.Secondary
                    )

                    SensumButton(
                        text = "Restore",
                        onClick = state::restoreLastSql,
                        variant = SensumButtonVariant.Outline
                    )
                }

                SensumTextField(
                    value = state.sqlText,
                    onValueChange = {
                        state.sqlText = it
                    },
                    label = "Executed / prepared SQL",
                    singleLine = false,
                    modifier = Modifier.heightIn(
                        min = 170.dp,
                        max = 260.dp
                    )
                )
            }
        }
    }
}

@Composable
private fun PreviewTable(
    rows: List<MeasurementUi>
) {
    val vScroll = rememberScrollState()
    val hScroll = rememberScrollState()

    if (rows.isEmpty()) {
        Text(
            text = "No preview rows yet.",
            style = MaterialTheme.typography.bodySmall,
            color = SensumThemeColors.muted
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .horizontalScroll(hScroll)
                .verticalScroll(vScroll)
                .padding(
                    end = 14.dp,
                    bottom = 14.dp
                )
        ) {
            PreviewHeader()

            rows.take(100).forEach { row ->
                PreviewRow(row)

                HorizontalDivider(
                    color = SensumThemeColors.border.copy(alpha = 0.45f)
                )
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(vScroll),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
        )

        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(hScroll),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun PreviewHeader() {
    Row(
        modifier = Modifier
            .background(SensumThemeColors.surfaceVariant.copy(alpha = 0.55f))
            .padding(vertical = 6.dp)
    ) {
        Cell("Station", 90)
        Cell("Channel", 90)
        Cell("Time", 170)
        Cell("Value", 90)
        Cell("Status", 80)
        Cell("Source", 90)
    }
}

@Composable
private fun PreviewRow(
    row: MeasurementUi
) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Cell(row.stationId.toString(), 90)
        Cell(row.channelId.toString(), 90)
        Cell(row.dateTime.toString(), 170)
        Cell(row.value.toString(), 90)
        Cell(row.status.toString(), 80)
        Cell(row.source.name, 90)
    }
}

@Composable
private fun Cell(
    text: String,
    width: Int
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = SensumThemeColors.onSurface,
        maxLines = 1,
        modifier = Modifier
            .width(width.dp)
            .padding(horizontal = 6.dp)
    )
}