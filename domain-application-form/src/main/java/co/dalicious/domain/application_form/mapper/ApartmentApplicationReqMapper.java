package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormRequestDto;
import co.dalicious.domain.application_form.entity.ApartmentApplicationForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface ApartmentApplicationReqMapper {
    @Mapping(source = "user.name", target = "applierName")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "address", target = "address")
    @Mapping(target = "progressStatus", constant = "APPLY")
    @Mapping(source = "apartmentInfo.apartmentName", target = "apartmentName")
    @Mapping(source = "apartmentInfo.serviceStartDate", target = "serviceStartDate", qualifiedByName = "stringToLocalDate")
    @Mapping(source = "apartmentInfo.dongCount", target = "dongCount")
    @Mapping(source = "apartmentInfo.familyCount", target = "totalFamilyCount")
    ApartmentApplicationForm toEntity(ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto);

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String serviceDate) {
        return LocalDate.of(Integer.parseInt(serviceDate.substring(0, 4)),
                Integer.parseInt(serviceDate.substring(4, 6)),
                Integer.parseInt(serviceDate.substring(6, 8)));
    }
}
