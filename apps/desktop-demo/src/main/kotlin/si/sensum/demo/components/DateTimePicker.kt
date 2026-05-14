package si.sensum.demo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumColors
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.calendar
import si.sensum.demo.resources.chevron_left
import si.sensum.demo.resources.chevron_right
import si.sensum.demo.resources.clock
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
    var showPopup by remember { mutableStateOf(false) }

    // Interno stanje — neodvisno od value
    var displayMonth by remember { mutableStateOf(YearMonth.from(value)) }
    var selectedDate by remember { mutableStateOf(value.toLocalDate()) }
    var hourText by remember { mutableStateOf(value.hour.toString().padStart(2, '0')) }
    var minuteText by remember { mutableStateOf(value.minute.toString().padStart(2, '0')) }

    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = SensumThemeColors.muted)
        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, if (showPopup) SensumThemeColors.accent else SensumThemeColors.border, RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable {
                    // Inicializiraj interno stanje ob odprtju
                    displayMonth = YearMonth.from(value)
                    selectedDate = value.toLocalDate()
                    hourText = value.hour.toString().padStart(2, '0')
                    minuteText = value.minute.toString().padStart(2, '0')
                    showPopup = true
                }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.calendar),
                contentDescription = null,
                tint = if (showPopup) SensumThemeColors.accent else SensumThemeColors.muted,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = value.format(DISPLAY_FORMAT),
                style = MaterialTheme.typography.bodyMedium,
                color = SensumThemeColors.onSurface
            )
        }

        if (showPopup) {
            Dialog(onDismissRequest = { showPopup = false }) {
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, SensumThemeColors.border, RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        // Mesec navigacija
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { displayMonth = displayMonth.minusMonths(1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.chevron_left),
                                    null,
                                    tint = SensumThemeColors.muted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                "${displayMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${displayMonth.year}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            IconButton(
                                onClick = { displayMonth = displayMonth.plusMonths(1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.chevron_right),
                                    null,
                                    tint = SensumThemeColors.muted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Dnevi v tednu
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { day ->
                                Text(
                                    day,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SensumThemeColors.muted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Dnevi v mesecu
                        val firstDayOfWeek = displayMonth.atDay(1).dayOfWeek.value - 1
                        val daysInMonth = displayMonth.lengthOfMonth()
                        val totalCells = firstDayOfWeek + daysInMonth
                        val rows = (totalCells + 6) / 7

                        for (row in 0 until rows) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                for (col in 0..6) {
                                    val dayNum = row * 7 + col - firstDayOfWeek + 1
                                    if (dayNum < 1 || dayNum > daysInMonth) {
                                        Spacer(Modifier.weight(1f))
                                    } else {
                                        val date = displayMonth.atDay(dayNum)
                                        val isSelected = date == selectedDate
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (isSelected) SensumThemeColors.accent
                                                    else MaterialTheme.colorScheme.surface
                                                )
                                                .clickable { selectedDate = date },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "$dayNum",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isSelected) SensumThemeColors.onAccent else SensumThemeColors.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = SensumThemeColors.border)

                        // Ura in minute
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.clock),
                                null,
                                tint = SensumThemeColors.muted,
                                modifier = Modifier.size(16.dp)
                            )
                            BasicTextField(
                                value = hourText,
                                onValueChange = {
                                    if (it.length <= 2 && it.all { c -> c.isDigit() }) hourText = it
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, SensumThemeColors.accent, RoundedCornerShape(4.dp))
                                    .padding(8.dp),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    textAlign = TextAlign.Center,
                                    color = SensumThemeColors.onSurface
                                )
                            )
                            Text(":", style = MaterialTheme.typography.titleMedium, color = SensumThemeColors.muted)
                            BasicTextField(
                                value = minuteText,
                                onValueChange = {
                                    if (it.length <= 2 && it.all { c -> c.isDigit() }) minuteText = it
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, SensumThemeColors.border, RoundedCornerShape(4.dp))
                                    .padding(8.dp),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    textAlign = TextAlign.Center,
                                    color = SensumThemeColors.onSurface
                                )
                            )

                            // OK — šele tukaj pokličemo onValueChange
                            TextButton(onClick = {
                                val h = hourText.toIntOrNull()?.coerceIn(0, 23) ?: 0
                                val m = minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0
                                onValueChange(selectedDate.atTime(h, m))
                                showPopup = false
                            }) {
                                Text("OK", color = SensumThemeColors.accent)
                            }
                        }
                    }
                }
            }
        }
    }
}