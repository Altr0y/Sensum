package si.sensum.backend.mapper

import si.sensum.shared.models.common.DataSourceDto

fun String?.toDataSourceDto(): DataSourceDto {
    return runCatching {
        DataSourceDto.valueOf(this?.uppercase() ?: DataSourceDto.UNKNOWN.name)
    }.getOrDefault(DataSourceDto.UNKNOWN)
}

fun DataSourceDto.toDbValue(): String {
    return name
}