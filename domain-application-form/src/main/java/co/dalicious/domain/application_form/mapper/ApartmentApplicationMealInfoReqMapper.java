package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.apartment.ApartmentMealInfoRequestDto;
import co.dalicious.domain.application_form.entity.ApartmentApplicationMealInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApartmentApplicationMealInfoReqMapper {
    ApartmentApplicationMealInfo toEntity(ApartmentMealInfoRequestDto apartmentMealInfoRequestDto);
}
