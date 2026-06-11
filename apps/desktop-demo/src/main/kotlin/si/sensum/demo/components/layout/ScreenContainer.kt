package si.sensum.demo.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import si.sensum.demo.components.theme.SensumSizes

@Composable
fun ScreenContainer(
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = SensumSizes.screenPaddingHorizontal,
        vertical = SensumSizes.screenPaddingVertical
    ),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(SensumSizes.cardSpacing),
    content: @Composable () -> Unit
) {
    val baseModifier = modifier
        .fillMaxSize()
        .padding(contentPadding)

    Column(
        modifier = if (scrollable) {
            baseModifier.verticalScroll(rememberScrollState())
        } else {
            baseModifier
        },
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}