package si.sensum.backend.domain.records

data class RecordsQuery(
    val page: Int,
    val pageSize: Int,
    val sortBy: String,
    val sortDirection: SortDirection,
    val filters: List<RecordFilter>
)

data class RecordFilter(
    val field: String,
    val operator: FilterOperator,
    val value: String
)

enum class SortDirection {
    ASC,
    DESC
}

enum class FilterOperator {
    EQ,
    CONTAINS,
    GT,
    LT,
    GTE,
    LTE
}