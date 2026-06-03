package si.sensum.backend.mapper

import si.sensum.backend.domain.channel.ChannelEntity
import si.sensum.shared.models.channels.ChannelDto

fun ChannelEntity.toDto(): ChannelDto {
    return ChannelDto(
        stationId = stationId,
        channelId = id,
        name = name,
        description = description,
        unit = unit.name,
        source = source
    )
}