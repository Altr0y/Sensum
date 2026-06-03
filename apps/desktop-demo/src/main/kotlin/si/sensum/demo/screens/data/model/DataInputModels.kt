package si.sensum.demo.screens.data.model

enum class DataSourceType(
    val label: String
) {
    ALL("ALL"),
    SWS("SWS"),
    DSL("DSL"),
    SIM("SIM"),
    MANUAL("Manual")
}

enum class DataEntityType(
    val label: String
) {
    ALL("All"),
    STATION("Station"),
    CHANNEL("Channel"),
    MEASUREMENT("Measurement")
}

data class DataActionDialog(
    val title: String,
    val message: String,
    val isError: Boolean = false
)
