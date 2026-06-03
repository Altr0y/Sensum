package si.sensum.demo.components.datetime

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import si.sensum.demo.components.theme.SensumThemeColors

@Composable
fun TimeNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= 2 && newValue.all { character -> character.isDigit() }) {
                onValueChange(newValue)
            }
        },
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isFocused) {
                    SensumThemeColors.accent
                } else {
                    SensumThemeColors.border
                },
                shape = RoundedCornerShape(6.dp)
            )
            .padding(10.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center,
            color = SensumThemeColors.onSurface
        )
    )
}