package si.sensum.demo.components.main.database

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.model.MeasurementUi
import si.sensum.demo.resources.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val TABLE_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

@Composable
fun EditableMeasurementRow(
    measurement: MeasurementUi,
    isEven: Boolean,
    onUpdate: (MeasurementUi) -> Unit,
    onDelete: (Int) -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var dateTimeValue by remember(measurement.id) {
        mutableStateOf(measurement.dateTime.format(TABLE_DATE_TIME_FORMAT))
    }
    var measurementValue by remember(measurement.id) {
        mutableStateOf(measurement.value.toString())
    }
    var statusValue by remember(measurement.id) {
        mutableStateOf(measurement.status.toString())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isEven) MaterialTheme.colorScheme.background
                else MaterialTheme.colorScheme.surface
            )
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (editing) {
            InlineField(dateTimeValue, Modifier.weight(3f)) { dateTimeValue = it }
            InlineField(measurementValue, Modifier.weight(2f)) { measurementValue = it }
            InlineField(statusValue, Modifier.weight(1f)) { statusValue = it }

            Row(
                modifier = Modifier.width(60.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = {
                        val parsedDateTime = parseDateTimeOrNull(dateTimeValue)
                        val parsedValue = measurementValue.toDoubleOrNull()
                        val parsedStatus = statusValue.toIntOrNull()

                        if (parsedDateTime != null && parsedValue != null && parsedStatus != null) {
                            onUpdate(
                                measurement.copy(
                                    dateTime = parsedDateTime,
                                    value = parsedValue,
                                    status = parsedStatus
                                )
                            )
                            editing = false
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.check),
                        contentDescription = null,
                        tint = SensumThemeColors.success,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { editing = false },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.x),
                        contentDescription = null,
                        tint = SensumThemeColors.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else {
            TableCell(measurement.dateTime.format(TABLE_DATE_TIME_FORMAT), 3f)
            TableCell(String.format("%.4f", measurement.value), 2f)
            TableCell(measurement.status.toString(), 1f)

            Row(
                modifier = Modifier.width(60.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { editing = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.edit),
                        contentDescription = null,
                        tint = SensumThemeColors.muted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { measurement.id?.let(onDelete) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.trash),
                        contentDescription = null,
                        tint = SensumThemeColors.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InlineField(
    value: String,
    modifier: Modifier,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.padding(end = 4.dp),
        textStyle = MaterialTheme.typography.bodySmall.copy(
            color = SensumThemeColors.onSurface
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = SensumThemeColors.accent,
            unfocusedIndicatorColor = SensumThemeColors.border,
            cursorColor = SensumThemeColors.accent
        )
    )
}

private fun parseDateTimeOrNull(value: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(value, TABLE_DATE_TIME_FORMAT)
    } catch (_: DateTimeParseException) {
        null
    }
}