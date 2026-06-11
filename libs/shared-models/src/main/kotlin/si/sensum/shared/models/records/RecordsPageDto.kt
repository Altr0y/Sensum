package si.sensum.shared.models.records

import kotlinx.serialization.Serializable

@Serializable
data class RecordsPageDto<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)