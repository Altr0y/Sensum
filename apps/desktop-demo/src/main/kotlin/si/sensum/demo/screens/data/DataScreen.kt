package si.sensum.demo.screens.data

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import si.sensum.demo.api.SensumApiClient
import si.sensum.demo.components.background.AppBackgroundStyle
import si.sensum.demo.components.background.AppVectorBackground
import si.sensum.demo.components.layout.ScreenContainer
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.components.ui.SensumButton
import si.sensum.demo.components.ui.SensumButtonVariant
import si.sensum.demo.components.ui.SensumCard
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.model.UiStatus
import si.sensum.demo.screens.data.components.DataActionButtons
import si.sensum.demo.screens.data.components.DataEntitySelector
import si.sensum.demo.screens.data.components.DataPreviewPanel
import si.sensum.demo.screens.data.components.DataResultDialog
import si.sensum.demo.screens.data.components.DataSourceTabs
import si.sensum.demo.screens.data.components.DateRangeSection
import si.sensum.demo.screens.data.forms.DslDataForm
import si.sensum.demo.screens.data.forms.ManualDataForm
import si.sensum.demo.screens.data.forms.SimDataForm
import si.sensum.demo.screens.data.forms.SwsDataForm
import si.sensum.demo.screens.data.model.DataSourceType
import java.time.LocalDateTime

@Composable
fun DataScreen(
    apiClient: SensumApiClient,
    isDark: Boolean,
    onStatusChange: (UiStatus) -> Unit = {},
    onOpenRecords: (List<MeasurementUi>) -> Unit
) {
    val scope = rememberCoroutineScope()

    val state = remember(apiClient) {
        DataScreenState(
            apiClient = apiClient,
            scope = scope,
            onOpenRecords = onOpenRecords,
            onStatusChange = onStatusChange
        )
    }

    AppVectorBackground(
        isDark = isDark,
        style = AppBackgroundStyle.Network
    ) {
        BoxWithConstraints {
            val compact = maxWidth < 1280.dp

            ScreenContainer(
                scrollable = true,
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)
            ) {
                DataTopToolbar(
                    state = state,
                    compact = compact
                )

                HorizontalDivider(color = SensumThemeColors.border)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SensumSpacing.lg)
                ) {
                    Column(
                        modifier = Modifier.weight(
                            if (state.previewVisible) {
                                0.52f
                            } else {
                                1f
                            }
                        ),
                        verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)
                    ) {
                        when (state.sourceType) {
                            DataSourceType.ALL -> {
                                SwsDataForm(state)
                                DslDataForm(state)
                                SimDataForm(state)
                                ManualDataForm(state)
                            }

                            DataSourceType.SWS -> SwsDataForm(state)
                            DataSourceType.DSL -> DslDataForm(state)
                            DataSourceType.SIM -> SimDataForm(state)
                            DataSourceType.MANUAL -> ManualDataForm(state)
                        }
                    }

                    if (state.previewVisible) {
                        DataPreviewPanel(
                            state = state,
                            modifier = Modifier.weight(0.48f)
                        )
                    }
                }
            }

            DataResultDialog(
                dialog = state.dialog,
                onDismiss = state::dismissDialog
            )
        }
    }
}

@Composable
private fun DataTopToolbar(
    state: DataScreenState,
    compact: Boolean
) {
    SensumCard(
        overlay = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)
        ) {
            if (compact) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)
                ) {
                    SelectionChips(state)
                    DateControls(state)
                    ActionControls(state)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SensumSpacing.lg)
                ) {
                    Column(
                        modifier = Modifier.weight(0.46f),
                        verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
                    ) {
                        SelectionChips(state)
                    }

                    Column(
                        modifier = Modifier.weight(0.36f),
                        verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
                    ) {
                        DateControls(state)
                    }

                    Column(
                        modifier = Modifier.weight(0.18f),
                        verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
                    ) {
                        ActionControls(state)
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionChips(
    state: DataScreenState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
        ) {
            DataSourceTabs(
                selected = state.sourceType,
                onSelected = {
                    state.sourceType = it
                    state.updateSqlPreview()
                }
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(SensumSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
        ) {
            DataEntitySelector(
                selected = state.entityType,
                onSelected = {
                    state.entityType = it
                    state.updateSqlPreview()
                }
            )
        }
    }
}

@Composable
private fun DateControls(
    state: DataScreenState
) {
    DateRangeSection(
        singleTimestamp = state.singleTimestamp,
        from = state.datetimeFrom,
        to = state.datetimeTo,
        onSingleTimestampChange = { enabled ->
            state.singleTimestamp = enabled

            if (enabled) {
                state.datetimeTo = state.datetimeFrom
            }

            state.updateSqlPreview()
        },
        onCurrentYearSelected = {
            val now = LocalDateTime.now()
            state.singleTimestamp = false
            state.datetimeFrom = LocalDateTime.of(now.year, 1, 1, 0, 0)
            state.datetimeTo = LocalDateTime.of(now.year, 12, 31, 23, 59)
            state.updateSqlPreview()
        },
        onCurrentMonthSelected = {
            val now = LocalDateTime.now()
            val start = LocalDateTime.of(now.year, now.month, 1, 0, 0)

            state.singleTimestamp = false
            state.datetimeFrom = start
            state.datetimeTo = start.plusMonths(1).minusMinutes(1)
            state.updateSqlPreview()
        },
        onFromChange = {
            state.datetimeFrom = it

            if (state.singleTimestamp) {
                state.datetimeTo = it
            }

            state.updateSqlPreview()
        },
        onToChange = {
            state.datetimeTo = it
            state.updateSqlPreview()
        }
    )
}

@Composable
private fun ActionControls(
    state: DataScreenState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SensumSpacing.sm)
    ) {
        DataActionButtons(
            sourceType = state.sourceType,
            status = state.status,
            previewVisible = state.previewVisible,
            onRun = state::runPrimaryAction,
            onOpenRecords = { state.openCurrentRecords() },
            onTogglePreview = {
                state.previewVisible = !state.previewVisible
            }
        )

        SensumButton(
            text = "Clear filters",
            onClick = state::clearFilters,
            variant = SensumButtonVariant.Outline,
            compact = true
        )
    }
}