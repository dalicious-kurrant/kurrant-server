package co.dalicious.domain.client.mapper;


import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupResponseMapper {
    @Mapping(source = "id", target = "clientId")
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "spots", target = "spots", qualifiedByName = "spotToDto")
    SpotListResponseDto toDto(Group corporation);

    @Named("spotToDto")
    default List<SpotListResponseDto.Spot> spotToDto(List<Spot> spots) {
        List<SpotListResponseDto.Spot> spotDtoList = new ArrayList<>();
        for (Spot spot : spots) {
            spotDtoList.add(SpotListResponseDto.Spot.builder()
                    .spotName(spot.getName())
                    .spotId(spot.getId())
                    .build());
        }
        return spotDtoList;
    }
}
