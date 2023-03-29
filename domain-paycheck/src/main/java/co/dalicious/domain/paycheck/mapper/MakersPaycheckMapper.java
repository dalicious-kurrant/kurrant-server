package co.dalicious.domain.paycheck.mapper;

import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.entity.MakersPaycheck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MakersPaycheckMapper {

    @Mapping(source = "makers", target = "makers")
    @Mapping(source = "", target = "")
    @Mapping(source = "", target = "")
    @Mapping(source = "", target = "")
    MakersPaycheck toEntity(PaycheckDto.MakersRequest paycheckDto, Makers makers);
}
