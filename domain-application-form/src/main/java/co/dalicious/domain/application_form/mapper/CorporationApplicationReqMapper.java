package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormRequestDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface CorporationApplicationReqMapper {
    @Mapping(target = "progressStatus", constant = "APPLY")
    @Mapping(source = "user.name", target = "applierName")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "corporationInfo.corporationName", target = "corporationName")
    @Mapping(source = "corporationInfo.employeeCount", target = "employeeCount")
    @Mapping(source = "corporationInfo.startDate", target = "serviceStartDate", qualifiedByName = "stringToLocalDate")
    @Mapping(source = "address", target = "address", qualifiedByName = "getAddress")
    @Mapping(source = "option.isGarbage", target = "isGarbage")
    @Mapping(source = "option.isHotStorage", target = "isHotStorage")
    @Mapping(source = "option.isSetting", target = "isSetting")
    @Mapping(source = "option.memo", target = "memo")
    CorporationApplicationForm toEntity(CorporationApplicationFormRequestDto corporationApplicationFormRequestDto);

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String serviceDate) {
        return LocalDate.of(Integer.parseInt(serviceDate.substring(0, 4)),
                Integer.parseInt(serviceDate.substring(4, 6)),
                Integer.parseInt(serviceDate.substring(6, 8)));
    }

    @Named("getAddress")
    default Address getAddress(CreateAddressRequestDto createAddressRequestDto) {
        // FIXME: 위도 경도 추가?
        try {
            return new Address(createAddressRequestDto.getZipCode(),
                    createAddressRequestDto.getAddress1(),
                    createAddressRequestDto.getAddress2(),
                    createAddressRequestDto.getAddress3(),
                    null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

}
}
