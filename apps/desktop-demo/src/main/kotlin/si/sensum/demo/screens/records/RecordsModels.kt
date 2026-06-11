package si.sensum.demo.screens.records

enum class RecordsEntityType(
    val label: String
) {
    STATIONS("Stations"),
    CHANNELS("Channels"),
    MEASUREMENTS("Measurements")
}

enum class RecordsSortDirection(
    val apiValue: String,
    val symbol: String
) {
    ASC("asc", "↑"),
    DESC("desc", "↓");

    fun toggled(): RecordsSortDirection {
        return when (this) {
            ASC -> DESC
            DESC -> ASC
        }
    }
}

enum class RecordsFilterOperator(
    val label: String,
    val apiValue: String
) {
    EQ("Equals", "eq"),
    CONTAINS("Contains", "contains"),
    GT("Bigger than", "gt"),
    LT("Smaller than", "lt"),
    GTE("Bigger or equal", "gte"),
    LTE("Smaller or equal", "lte")
}

data class RecordsFilter(
    val field: String,
    val operator: RecordsFilterOperator,
    val value: String
)