package si.sensum.demo.components.datetime

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.calendar
import si.sensum.demo.resources.chevron_left
import si.sensum.demo.resources.chevron_right
import si.sensum.demo.resources.clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd. MM. yyyy  HH:mm")

@Composable
fun DateTimePicker(
    label: String,
    value: LocalDateTime,
    onValueChange: (LocalDateTime) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var displayMonth by remember(value) { mutableStateOf(YearMonth.from(value)) }
    var selectedDate by remember(value) { mutableStateOf(value.toLocalDate()) }
    var selectedHour by remember(value) { mutableStateOf(value.hour) }
    var selectedMinute by remember(value) { mutableStateOf(value.minute) }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = SensumThemeColors.muted
        )

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .border(
                    width = 1.dp,
                    color = if (expanded) SensumThemeColors.accent else SensumThemeColors.border,
                    shape = MaterialTheme.shapes.small
                )
                .background(MaterialTheme.colorScheme.surface)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.calendar),
                contentDescription = null,
                tint = if (expanded) SensumThemeColors.accent else SensumThemeColors.muted,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = value.format(DISPLAY_FORMAT),
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.onSurface
            )
        }

        if (expanded) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true)
            ) {
                Column(
                    modifier = Modifier
                        .width(420.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .border(1.dp, SensumThemeColors.border, MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(SensumSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(SensumSpacing.md)
                ) {
                    MonthHeader(
                        displayMonth = displayMonth,
                        onPrevious = { displayMonth = displayMonth.minusMonths(1) },
                        onNext = { displayMonth = displayMonth.plusMonths(1) }
                    )

                    CalendarGrid(
                        displayMonth = displayMonth,
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it }
                    )

                    HorizontalDivider(color = SensumThemeColors.border)

                    TimeSelector(
                        hour = selectedHour,
                        minute = selectedMinute,
                        onHourChange = { selectedHour = it },
                        onMinuteChange = { selectedMinute = it }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { expanded = false }) {
                            Text("Cancel", color = SensumThemeColors.muted)
                        }
                        TextButton(
                            onClick = {
                                onValueChange(
                                    selectedDate.atTime(selectedHour, selectedMinute)
                                )
                                expanded = false
                            }
                        ) {
                            Text("Apply", color = SensumThemeColors.accent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(
    displayMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(Res.drawable.chevron_left),
            contentDescription = "Previous month",
            tint = SensumThemeColors.muted,
            modifier = Modifier.size(28.dp).clickable { onPrevious() }.padding(4.dp)
        )

        Text(
            text = "${displayMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${displayMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            color = SensumThemeColors.onSurface
        )

        Icon(
            painter = painterResource(Res.drawable.chevron_right),
            contentDescription = "Next month",
            tint = SensumThemeColors.muted,
            modifier = Modifier.size(28.dp).clickable { onNext() }.padding(4.dp)
        )
    }
}

@Composable
private fun CalendarGrid(
    displayMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDay = displayMonth.atDay(1)
    val daysInMonth = displayMonth.lengthOfMonth()
    val offset = firstDay.dayOfWeek.value - 1
    val cells = (0 until 42).map { index ->
        val day = index - offset + 1
        if (day in 1..daysInMonth) displayMonth.atDay(day) else null
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = SensumThemeColors.muted
                )
            }
        }

        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                week.forEach { date ->
                    val selected = date == selectedDate
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                when {
                                    selected -> SensumThemeColors.accent
                                    date != null -> SensumThemeColors.surfaceVariant.copy(alpha = 0.35f)
                                    else -> androidx.compose.ui.graphics.Color.Transparent
                                }
                            )
                            .clickable(enabled = date != null) {
                                date?.let(onDateSelected)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date?.dayOfMonth?.toString() ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (selected) SensumThemeColors.onAccent else SensumThemeColors.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSelector(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.lg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.clock),
            contentDescription = null,
            tint = SensumThemeColors.accent
        )

        TimeStepper("Hour", hour, 0..23, onHourChange)
        TimeStepper("Minute", minute, 0..59, onMinuteChange)
    }
}

@Composable
private fun TimeStepper(
    label: String,
    value: Int,
    range: IntRange,
    onChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = SensumThemeColors.muted)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TextButton(onClick = { onChange(if (value == range.first) range.last else value - 1) }) {
                Text("-", color = SensumThemeColors.accent)
            }
            Text(
                text = value.toString().padStart(2, '0'),
                style = MaterialTheme.typography.titleMedium,
                color = SensumThemeColors.onSurface,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center
            )
            TextButton(onClick = { onChange(if (value == range.last) range.first else value + 1) }) {
                Text("+", color = SensumThemeColors.accent)
            }
        }
    }
}
