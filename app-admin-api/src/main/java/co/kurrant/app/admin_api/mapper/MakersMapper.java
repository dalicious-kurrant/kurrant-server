package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.food.entity.Makers;
import co.kurrant.app.admin_api.dto.MakersDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MakersMapper {
    @Mapping(source = "id", target = "makersId")
    @Mapping(source = "name", target = "makersName")
    MakersDto.Makers makersToDto(Makers makers);

    default List<MakersDto.Makers> makersToDtos(List<Makers> makers) {
        return makers.stream()
                .map(this::makersToDto)
                .collect(Collectors.toList());
    }
}
