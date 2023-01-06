package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.CorporationSpot;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CorporationResponseMapper {
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
