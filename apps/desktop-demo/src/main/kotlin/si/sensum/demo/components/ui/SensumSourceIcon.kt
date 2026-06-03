package si.sensum.demo.components.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import si.sensum.demo.components.theme.SensumSizes
import si.sensum.demo.components.theme.SensumSpacing
import si.sensum.demo.components.theme.SensumThemeColors
import si.sensum.demo.resources.Res
import si.sensum.demo.resources.account_tree
import si.sensum.demo.resources.all_inclusive
import si.sensum.demo.resources.data_object
import si.sensum.demo.resources.digital_twin
import si.sensum.demo.resources.keyboard_external_input
import si.sensum.demo.screens.data.model.DataSourceType
import si.sensum.shared.models.common.DataSourceDto

@Composable
fun SensumSourceIcon(
    sourceName: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    Icon(
        painter = painterResource(sourceIconResource(sourceName)),
        contentDescription = sourceName,
        tint = if (selected) SensumThemeColors.accent else SensumThemeColors.muted,
        modifier = modifier.size(SensumSizes.fieldIconSize)
    )
}

@Composable
fun SensumSourceLabel(
    sourceName: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SensumSpacing.xs)
    ) {
        SensumSourceIcon(
            sourceName = sourceName,
            selected = selected
        )

        Text(
            text = sourceName,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) SensumThemeColors.accent else SensumThemeColors.onSurface
        )
    }
}

fun sourceIconResource(sourceName: String): DrawableResource {
    return when (sourceName.uppercase()) {
        "ALL" -> Res.drawable.all_inclusive
        "SIM", "SIMULATOR" -> Res.drawable.digital_twin
        "DSL" -> Res.drawable.data_object
        "MANUAL" -> Res.drawable.keyboard_external_input
        "SWS" -> Res.drawable.account_tree
        else -> Res.drawable.all_inclusive
    }
}

fun DataSourceType.iconResource(): DrawableResource {
    return sourceIconResource(name)
}

fun DataSourceDto.iconResource(): DrawableResource {
    return sourceIconResource(name)
}