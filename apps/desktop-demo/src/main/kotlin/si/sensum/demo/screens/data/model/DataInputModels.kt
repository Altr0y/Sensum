package si.sensum.demo.screens.data.model

enum class DataSourceType(
    val label: String
) {
    SWS("SWS"),
    DSL("DSL"),
    SIM("SIM"),
    MANUAL("Manual")
}

enum class DataEntityType(
    val label: String
) {
    STATION("Station"),
    CHANNEL("Channel"),
    MEASUREMENT("Measurement")
}

data class DataActionDialog(
    val title: String,
    val message: String,
    val isError: Boolean = false
)
