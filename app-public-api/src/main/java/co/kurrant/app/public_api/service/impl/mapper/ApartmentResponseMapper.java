package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.ApartmentSpot;
import co.dalicious.domain.client.entity.Corporation;
import co.kurrant.app.public_api.dto.client.SpotListResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ApartmentResponseMapper extends GenericMapper<SpotListResponseDto, Apartment> {
    @Mapping(target = "clientType", constant = "0")
    @Mapping(source = "id", target = "clientId")
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "spots", target = "spots", qualifiedByName = "spotToDto")
    SpotListResponseDto toDto(Corporation corporation);

    @Named("spotToDto")
    default List<SpotListResponseDto.Spot> spotToDto(List<ApartmentSpot> spots) {
        List<SpotListResponseDto.Spot> spotDtoList = new ArrayList<>();
        for (ApartmentSpot spot : spots) {
            spotDtoList.add(SpotListResponseDto.Spot.builder()
                    .spotName(spot.getName())
                    .spotId(spot.getId())
                    .build());
        }
        return spotDtoList;
    }
}
