package si.sensum.demo.components.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SensumUiScale(
    val value: Float
) {
    fun dp(size: Dp): Dp = size * value
    fun fraction(value: Float): Float = value
}

object SensumSizes {
    val breakpointCompact = 0.dp
    val breakpointSmall = 800.dp
    val breakpointMedium = 1100.dp
    val breakpointLarge = 1400.dp

    const val scaleLarge = 1.0f
    const val scaleMedium = 0.94f
    const val scaleSmall = 0.88f
    const val scaleCompact = 0.80f

    fun scaleFor(width: Dp): SensumUiScale {
        return SensumUiScale(
            value = when {
                width >= breakpointLarge -> scaleLarge
                width >= breakpointMedium -> scaleMedium
                width >= breakpointSmall -> scaleSmall
                else -> scaleCompact
            }
        )
    }

    val topBarHeight = 60.dp
    val topBarLogoHeight = 38.dp
    val topBarSideExpandedWidth = 216.dp
    val topBarSideCollapsedWidth = 56.dp
    val topBarStatusWidth = 260.dp
    val topBarIconSize = 22.dp

    val sideBarCollapsed = 68.dp
    val sideBarExpanded = 232.dp
    val sideBarItemHeight = 56.dp
    val sideBarActiveIndicatorWidth = 3.dp
    val sideBarIconSize = 24.dp

    val screenPadding = 24.dp
    val screenPaddingHorizontal = 24.dp
    val screenPaddingVertical = 16.dp

    val cardPadding = 16.dp
    val cardSpacing = 12.dp
    val sectionSpacing = 16.dp
    val fieldSpacing = 15.dp
    val headerSpacing = 14.dp

    val buttonHeight = 44.dp
    val buttonCompactHeight = 34.dp
    val buttonHorizontalPadding = 18.dp
    val buttonVerticalPadding = 10.dp
    val buttonCompactHorizontalPadding = 12.dp
    val buttonCompactVerticalPadding = 6.dp
    val buttonLoaderSize = 18.dp

    val iconButtonSize = 32.dp
    val iconButtonRadius = 8.dp
    val iconButtonIconSize = 16.dp

    val themeToggleIconSize = 20.dp
    val themeTogglePadding = 18.dp

    val fieldIconSize = 20.dp
    val statusIconSize = 18.dp
    val trayIconSize = 18.dp
    val settingsPlaceholderMaxWidth = 720.dp

    val fieldMinHeight = 56.dp
    val fieldBorderWidth = 1.dp
    val fieldHorizontalPadding = 12.dp
    val fieldVerticalPadding = 10.dp

    val dialogMinWidth = 380.dp
    val dialogMaxWidth = 640.dp
    val dialogPadding = 24.dp

    val emptyStateIconSize = 64.dp

    val popupDateTimeWidth = 420.dp
    val dateIconSize = 18.dp
    val dateNavIconSize = 28.dp
    val timeNumberWidth = 36.dp

    val authFormMaxWidth = 360.dp
    val authFormPadding = 24.dp
    val authSubtitleTopPadding = 2.dp

    const val authWidthFractionLarge = 0.24f
    const val authWidthFractionMedium = 0.30f
    const val authWidthFractionSmall = 0.42f
    const val authWidthFractionCompact = 0.86f

    val authFooterWidth = 220.dp
    val authFooterLogoWidth = 122.dp
    val authFooterLogoHeight = 76.dp
    val authFooterHorizontalPadding = 18.dp
    val authFooterVerticalPadding = 10.dp
    val authFooterExtraHeight = 42.dp

    val recordsFilterMenuWidth = 420.dp
    val recordsPageFieldWidth = 96.dp
    val recordsSortIconSize = 14.dp

    val tableCellPaddingHorizontal = 6.dp
    val tableRowPadding = 8.dp
    val tableRowSpacing = 8.dp

    val previewTableMaxHeight = 260.dp
    val sqlPreviewMinHeight = 170.dp
    val chartHeight = 360.dp
}