package co.kurrant.app.public_api.service.impl.mapper.client;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.CorporationSpot;
import co.kurrant.app.public_api.dto.client.SpotListResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CorporationResponseMapper extends GenericMapper<SpotListResponseDto, Corporation> {
    CorporationResponseMapper INSTANCE = Mappers.getMapper(CorporationResponseMapper.class);

    @Mapping(target = "clientType", constant = "1")
    @Mapping(source = "id", target = "clientId")
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "spots", target = "spots", qualifiedByName = "spotToDto")
    SpotListResponseDto toDto(Corporation corporation);

    @Named("spotToDto")
    default List<SpotListResponseDto.Spot> spotToDto(List<CorporationSpot> spots) {
        List<SpotListResponseDto.Spot> spotDtoList = new ArrayList<>();
        for (CorporationSpot spot : spots) {
            spotDtoList.add(SpotListResponseDto.Spot.builder()
                    .spotName(spot.getName())
                    .spotId(spot.getId())
                    .build());
        }
        return spotDtoList;
    }
}
